# FarmaCode

## Descripción

FarmaCode es una aplicación móvil Android que permite identificar medicamentos a partir de su nombre comercial (mediante OCR o ingreso manual) y obtener su principio activo, junto con una lista de alternativas bioequivalentes almacenadas en base de datos.

---

## Arquitectura

Arquitectura cliente-servidor en tres capas:

```id="arch001"
Android App (Kotlin)
        ↓
REST API (Spring Boot)
        ↓
MySQL Database
```

---

## Stack Tecnológico

### Frontend

* Kotlin
* CameraX
* ML Kit (OCR)
* Retrofit
* OkHttp

### Backend

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* Spring Validation

### Base de Datos

* MySQL

### Integraciones

* Claude API (procesamiento de lenguaje)

### Testing

* JUnit
* Postman

---

## Estructura del Proyecto

```id="struct001"
FarmaCode/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── config/
│
├── android-app/
│   ├── ui/
│   ├── network/
│   ├── scanner/
│   └── model/
│
├── database/
│   └── schema.sql
│
└── docs/
```

---

## Modelo de Datos

### Entidad: Medicamento

```json id="json001"
{
  "id": 1,
  "nombreComercial": "Tapsin",
  "principioActivo": "Paracetamol",
  "laboratorio": "Laboratorio X"
}
```

### Entidad: Bioequivalente

```json id="json002"
{
  "id": 10,
  "nombre": "Paracetamol Genérico",
  "principioActivo": "Paracetamol",
  "precio": 1200
}
```

---

## API REST

### Base URL

```id="url001"
http://localhost:8080/api
```

---

### Obtener principio activo desde nombre comercial

**POST /analizar**

```json id="req001"
{
  "nombre": "Tapsin"
}
```

**Respuesta:**

```json id="res001"
{
  "principioActivo": "Paracetamol"
}
```

---

### Obtener bioequivalentes

**GET /bioequivalentes/{principioActivo}**

Ejemplo:

```id="url002"
/api/bioequivalentes/Paracetamol
```

**Respuesta:**

```json id="res002"
[
  {
    "nombre": "Paracetamol Genérico",
    "precio": 1200
  },
  {
    "nombre": "Paracetamol 500mg",
    "precio": 1500
  }
]
```

---

### CRUD Medicamentos

* GET /medicamentos
* GET /medicamentos/{id}
* POST /medicamentos
* PUT /medicamentos/{id}
* DELETE /medicamentos/{id}

---

## Flujo de Funcionamiento

1. El usuario escanea un medicamento o ingresa el nombre
2. ML Kit extrae el texto (si aplica)
3. La app envía el nombre al backend
4. El backend utiliza IA para identificar el principio activo
5. Se consulta la base de datos
6. Se retornan los bioequivalentes ordenados por precio
7. La app muestra los resultados

---

## Configuración

### Backend

Archivo:

```id="cfg001"
application.properties
```

Ejemplo:

```properties id="cfg002"
spring.datasource.url=jdbc:mysql://localhost:3306/farmacode
spring.datasource.username=root
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

### Variables externas

* API Key de Claude
* Configuración de base de datos

---

## Ejecución

### Backend

```bash id="run001"
cd backend
./mvnw spring-boot:run
```

---

### Android

* Abrir en Android Studio
* Configurar endpoint base
* Ejecutar en dispositivo

---

## Testing

* Pruebas unitarias en servicios (JUnit)
* Pruebas de endpoints con Postman
* Validación de integración completa

---

## Consideraciones Técnicas

* Separación por capas (Controller, Service, Repository)
* Uso de DTOs para transferencia de datos
* Manejo de errores centralizado
* Preparado para autenticación futura

---

## Estado del Proyecto

MVP en desarrollo con funcionalidades base implementadas:

* API REST operativa
* Integración inicial con OCR
* Modelo de datos definido

---
