# Arquitectura Backend — FarmaCode

## Descripción General

El backend de FarmaCode es una API REST desarrollada con **Spring Boot (Java)** que expone endpoints consumidos por la app Android. Su responsabilidad principal es recibir un nombre comercial de medicamento (vía texto manual u OCR), consultar a la **Claude API (Anthropic)** para identificar el principio activo, y retornar un listado comparativo de bioequivalentes con precios.

---

## Capas de la Arquitectura

| Capa | Componente | Responsabilidad | Tecnología | Notas |
|------|------------|-----------------|------------|-------|
| **Controller** | `MedicamentoController` | Expone endpoints para buscar y listar medicamentos | Spring MVC (`@RestController`) | Maneja parámetros de búsqueda por nombre comercial y principio activo |
| **Controller** | `BusquedaController` | Recibe nombre comercial o texto OCR y orquesta la búsqueda con IA | Spring MVC (`@RestController`) | Endpoint principal del flujo de escaneo |
| **Controller** | `UsuarioController` | Registro e inicio de sesión de usuarios | Spring MVC + Spring Security | Retorna JWT al autenticarse |
| **Controller** | `HistorialController` | Consulta historial de búsquedas del usuario autenticado | Spring MVC (`@RestController`) | Requiere token JWT |
| **Service** | `MedicamentoService` | Lógica de negocio: filtrado, ordenamiento por precio, agrupación por principio activo | Spring (`@Service`) | Consulta repositorios y ensambla el DTO de respuesta |
| **Service** | `ClaudeApiService` | Envía el nombre comercial a Claude API y extrae el principio activo de la respuesta | Spring (`@Service`) + HTTP client | Usa prompt estructurado; maneja errores de la API externa |
| **Service** | `BusquedaService` | Orquesta el flujo: recibe texto → llama ClaudeApiService → consulta MedicamentoService → retorna bioequivalentes | Spring (`@Service`) | Punto central del caso de uso principal |
| **Service** | `UsuarioService` | Creación de usuarios, validación de credenciales, generación de JWT | Spring (`@Service`) + Spring Security | Usa BCrypt para contraseñas |
| **Service** | `HistorialService` | Persiste y consulta búsquedas realizadas por el usuario | Spring (`@Service`) | Guarda término buscado, tipo (MANUAL/OCR) y resultado |
| **Repository** | `MedicamentoRepository` | Acceso a datos de medicamentos, búsqueda por nombre comercial y principio activo | Spring Data JPA (`@Repository`) | Métodos: `findByNombreComercialContaining`, `findByPrincipioActivo_Id` |
| **Repository** | `PrincipioActivoRepository` | Búsqueda de principio activo por nombre exacto o parcial | Spring Data JPA | Método: `findByNombreIgnoreCase` |
| **Repository** | `LaboratorioRepository` | Acceso a datos de laboratorios | Spring Data JPA | Usado al persistir nuevos medicamentos |
| **Repository** | `PrecioRepository` | Consulta de precios vigentes por medicamento | Spring Data JPA | Método: `findLatestByMedicamento_Id` |
| **Repository** | `UsuarioRepository` | Búsqueda de usuario por email | Spring Data JPA | Usado en autenticación |
| **Repository** | `HistorialBusquedaRepository` | Acceso al historial por usuario | Spring Data JPA | Ordenado por fecha descendente |
| **Model / Entity** | `Medicamento` | Entidad JPA que representa un medicamento en la BD | JPA (`@Entity`) | Relacionado con PrincipioActivo, Laboratorio y Precio |
| **Model / Entity** | `PrincipioActivo` | Entidad que representa un ingrediente activo farmacológico | JPA (`@Entity`) | Un principio activo agrupa N medicamentos bioequivalentes |
| **Model / Entity** | `Laboratorio` | Entidad que representa el laboratorio fabricante | JPA (`@Entity`) | Incluye país de origen |
| **Model / Entity** | `Precio` | Registro de precio de un medicamento con fecha | JPA (`@Entity`) | Permite histórico de precios |
| **Model / Entity** | `Usuario` | Entidad de usuario de la aplicación | JPA (`@Entity`) | Almacena hash de contraseña (BCrypt) |
| **Model / Entity** | `HistorialBusqueda` | Registro de cada búsqueda realizada | JPA (`@Entity`) | Asociada opcionalmente a un usuario |
| **DTO** | `MedicamentoResponseDTO` | Objeto de respuesta con datos del medicamento formateados para el frontend | Java POJO | Incluye nombre, principio activo, laboratorio, precio, tipo, certISP |
| **DTO** | `BioequivalentesResponseDTO` | Agrupa el principio activo identificado + lista de medicamentos bioequivalentes | Java POJO | Respuesta principal del flujo de búsqueda |
| **DTO** | `BusquedaRequestDTO` | Objeto de entrada con el nombre comercial o texto OCR | Java POJO | Campo: `nombreComercial` (String) |
| **DTO** | `UsuarioRegistroDTO` | Datos para registro de nuevo usuario | Java POJO | Campos: nombre, email, password |
| **DTO** | `LoginRequestDTO` | Credenciales de autenticación | Java POJO | Campos: email, password |
| **DTO** | `LoginResponseDTO` | Respuesta al autenticarse exitosamente | Java POJO | Incluye token JWT y datos básicos del usuario |
| **DTO** | `HistorialBusquedaDTO` | Ítem del historial de búsquedas | Java POJO | Campos: término, tipo, fecha, resultado |
| **Config** | `ClaudeApiConfig` | Configuración del cliente HTTP para Claude API (URL, API Key, timeout) | Spring (`@Configuration`) | Lee `claude.api.key` desde `application.properties` |
| **Config** | `SecurityConfig` | Configura Spring Security: rutas públicas, filtro JWT, CORS | Spring Security (`@Configuration`) | Rutas públicas: `/api/busqueda/**`, `/api/auth/**` |
| **Config** | `CorsConfig` | Permite peticiones desde el cliente Android (y localhost en desarrollo) | Spring (`@Configuration`) | Configurar orígenes permitidos |
| **Integración IA** | `ClaudeApiService` | Construye el prompt, llama a la Claude API REST y parsea la respuesta para extraer el principio activo | RestTemplate / WebClient | Prompt ejemplo: *"Dado el medicamento comercial '{nombre}', ¿cuál es su principio activo?"* |
| **Seguridad** | `JwtFilter` | Intercepta requests, valida el token JWT e inyecta el usuario en el contexto de seguridad | Spring Security (`OncePerRequestFilter`) | Token enviado en header `Authorization: Bearer {token}` |
| **Seguridad** | `JwtUtil` | Genera y valida tokens JWT | `io.jsonwebtoken` (JJWT) | Expiración configurable vía `application.properties` |

---

## Endpoints REST

| Método | Endpoint | Descripción | Request Body | Response Body |
|--------|----------|-------------|--------------|---------------|
| `POST` | `/api/busqueda/nombre-comercial` | Recibe nombre comercial, consulta Claude API y retorna bioequivalentes con precios | `{ "nombreComercial": "Aspirina" }` | `BioequivalentesResponseDTO` |
| `POST` | `/api/busqueda/ocr` | Recibe texto crudo de OCR, extrae nombre comercial y aplica mismo flujo | `{ "textoOcr": "Aspirina 100mg comprimidos..." }` | `BioequivalentesResponseDTO` |
| `GET` | `/api/medicamentos` | Lista todos los medicamentos (con paginación) | — | `Page<MedicamentoResponseDTO>` |
| `GET` | `/api/medicamentos/{id}` | Retorna detalle de un medicamento por ID | — | `MedicamentoResponseDTO` |
| `GET` | `/api/medicamentos/buscar?nombre={nombre}` | Busca medicamentos por nombre comercial parcial | — | `List<MedicamentoResponseDTO>` |
| `GET` | `/api/medicamentos/principio-activo/{nombre}` | Lista todos los medicamentos con ese principio activo, ordenados por precio | — | `List<MedicamentoResponseDTO>` |
| `POST` | `/api/auth/registro` | Registra un nuevo usuario | `UsuarioRegistroDTO` | `LoginResponseDTO` |
| `POST` | `/api/auth/login` | Autentica al usuario y retorna JWT | `LoginRequestDTO` | `LoginResponseDTO` |
| `GET` | `/api/historial` | Retorna historial de búsquedas del usuario autenticado | — (JWT requerido) | `List<HistorialBusquedaDTO>` |
| `DELETE` | `/api/historial/{id}` | Elimina un ítem del historial | — (JWT requerido) | `204 No Content` |

---

## Flujo Principal (Escaneo / Búsqueda)

```
Android App
    │
    ├─ [OCR] POST /api/busqueda/ocr  { textoOcr }
    └─ [Manual] POST /api/busqueda/nombre-comercial  { nombreComercial }
              │
              ▼
       BusquedaController
              │
              ▼
       BusquedaService
              ├──► ClaudeApiService ──► Claude API (Anthropic)
              │         └─ retorna: principioActivo (String)
              │
              └──► MedicamentoService
                        └──► MedicamentoRepository (MySQL)
                                  └─ findByPrincipioActivo + ordenar por Precio
                                  └─ retorna: List<Medicamento>
              │
              ▼
    BioequivalentesResponseDTO
    {
      principioActivo: "ácido acetilsalicílico",
      medicamentos: [
        { nombre, laboratorio, precio, tipo, certISP, ... },
        ...
      ]
    }
              │
              ▼
         Android App
```

---

## Variables de Configuración (`application.properties`)

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/farmacode_db
spring.datasource.username=farmacode_user
spring.datasource.password=tu_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Claude API
claude.api.key=sk-ant-...
claude.api.url=https://api.anthropic.com/v1/messages
claude.api.model=claude-opus-4-6

# JWT
jwt.secret=clave_secreta_larga
jwt.expiration.ms=86400000
```
