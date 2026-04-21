# Entidades de Base de Datos — FarmaCode

Base de datos: **MySQL**
Charset: `utf8mb4` / Collation: `utf8mb4_unicode_ci`

---

## Entidad: `principio_activo`

Representa el ingrediente farmacológico activo. Es la entidad central que agrupa a todos los medicamentos bioequivalentes.

| Entidad | Atributo | Tipo SQL | Restricciones | Descripción |
|---------|----------|----------|---------------|-------------|
| `principio_activo` | `id` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único |
| `principio_activo` | `nombre` | `VARCHAR(255)` | NOT NULL, UNIQUE | Nombre del principio activo (ej: "atorvastatina") |
| `principio_activo` | `descripcion` | `TEXT` | NULLABLE | Descripción farmacológica general |
| `principio_activo` | `categoria` | `VARCHAR(100)` | NOT NULL | Categoría terapéutica (ej: "Hipolipemiante", "Antidepresivo") |
| `principio_activo` | `created_at` | `DATETIME` | NOT NULL, DEFAULT NOW() | Fecha de creación del registro |

---

## Entidad: `laboratorio`

Representa el laboratorio farmacéutico fabricante del medicamento.

| Entidad | Atributo | Tipo SQL | Restricciones | Descripción |
|---------|----------|----------|---------------|-------------|
| `laboratorio` | `id` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único |
| `laboratorio` | `nombre` | `VARCHAR(255)` | NOT NULL, UNIQUE | Nombre del laboratorio (ej: "Pfizer", "Recalcine") |
| `laboratorio` | `pais` | `VARCHAR(100)` | NOT NULL | País de origen (ej: "Chile", "Estados Unidos") |
| `laboratorio` | `created_at` | `DATETIME` | NOT NULL, DEFAULT NOW() | Fecha de creación del registro |

---

## Entidad: `medicamento`

Representa un medicamento específico (producto comercial). Cada medicamento pertenece a un principio activo y a un laboratorio.

| Entidad | Atributo | Tipo SQL | Restricciones | Descripción |
|---------|----------|----------|---------------|-------------|
| `medicamento` | `id` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único |
| `medicamento` | `nombre_comercial` | `VARCHAR(255)` | NOT NULL | Nombre de marca del medicamento (ej: "Lipitor") |
| `medicamento` | `dosis` | `VARCHAR(100)` | NOT NULL | Dosis del medicamento (ej: "20mg", "500mg") |
| `medicamento` | `presentacion` | `VARCHAR(255)` | NOT NULL | Forma de presentación (ej: "Comprimidos recubiertos x30") |
| `medicamento` | `administracion` | `VARCHAR(100)` | NOT NULL | Vía de administración (ej: "Oral", "Tópica") |
| `medicamento` | `tipo` | `ENUM('Genérico','Referencia','Bioequivalente')` | NOT NULL | Clasificación del medicamento según ISP |
| `medicamento` | `cert_isp` | `BOOLEAN` | NOT NULL, DEFAULT FALSE | Indica si cuenta con certificación ISP Chile |
| `medicamento` | `descripcion` | `TEXT` | NULLABLE | Descripción adicional del producto |
| `medicamento` | `principio_activo_id` | `BIGINT` | FK → `principio_activo(id)`, NOT NULL | Principio activo al que pertenece |
| `medicamento` | `laboratorio_id` | `BIGINT` | FK → `laboratorio(id)`, NOT NULL | Laboratorio que lo fabrica |
| `medicamento` | `created_at` | `DATETIME` | NOT NULL, DEFAULT NOW() | Fecha de creación del registro |
| `medicamento` | `updated_at` | `DATETIME` | NOT NULL, DEFAULT NOW() ON UPDATE NOW() | Última actualización |

---

## Entidad: `precio`

Registra el precio de un medicamento. Se mantiene historial para permitir comparativas en el tiempo.

| Entidad | Atributo | Tipo SQL | Restricciones | Descripción |
|---------|----------|----------|---------------|-------------|
| `precio` | `id` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único |
| `precio` | `medicamento_id` | `BIGINT` | FK → `medicamento(id)`, NOT NULL | Medicamento al que corresponde el precio |
| `precio` | `valor` | `DECIMAL(10,2)` | NOT NULL | Precio en pesos chilenos (CLP) |
| `precio` | `fuente` | `VARCHAR(100)` | NULLABLE | Fuente del precio (ej: "Farmacias Ahumada", "Cruz Verde") |
| `precio` | `fecha_registro` | `DATETIME` | NOT NULL, DEFAULT NOW() | Fecha en que se registró el precio |
| `precio` | `vigente` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Indica si es el precio actualmente válido |

---

## Entidad: `usuario`

Representa al usuario registrado de la app FarmaCode.

| Entidad | Atributo | Tipo SQL | Restricciones | Descripción |
|---------|----------|----------|---------------|-------------|
| `usuario` | `id` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único |
| `usuario` | `nombre` | `VARCHAR(255)` | NOT NULL | Nombre del usuario |
| `usuario` | `email` | `VARCHAR(255)` | NOT NULL, UNIQUE | Correo electrónico (usado para login) |
| `usuario` | `password_hash` | `VARCHAR(255)` | NOT NULL | Hash BCrypt de la contraseña |
| `usuario` | `activo` | `BOOLEAN` | NOT NULL, DEFAULT TRUE | Indica si la cuenta está activa |
| `usuario` | `created_at` | `DATETIME` | NOT NULL, DEFAULT NOW() | Fecha de registro |
| `usuario` | `ultimo_acceso` | `DATETIME` | NULLABLE | Última vez que inició sesión |

---

## Entidad: `historial_busqueda`

Registra cada búsqueda realizada en la app, opcionalmente asociada a un usuario autenticado.

| Entidad | Atributo | Tipo SQL | Restricciones | Descripción |
|---------|----------|----------|---------------|-------------|
| `historial_busqueda` | `id` | `BIGINT` | PK, AUTO_INCREMENT, NOT NULL | Identificador único |
| `historial_busqueda` | `usuario_id` | `BIGINT` | FK → `usuario(id)`, NULLABLE | Usuario que realizó la búsqueda (null si no autenticado) |
| `historial_busqueda` | `termino_busqueda` | `VARCHAR(255)` | NOT NULL | Texto ingresado o extraído por OCR |
| `historial_busqueda` | `tipo_busqueda` | `ENUM('MANUAL','OCR')` | NOT NULL | Origen de la búsqueda |
| `historial_busqueda` | `resultado_principio_activo` | `VARCHAR(255)` | NULLABLE | Principio activo identificado por Claude API |
| `historial_busqueda` | `resultados_encontrados` | `INT` | NOT NULL, DEFAULT 0 | Cantidad de bioequivalentes retornados |
| `historial_busqueda` | `fecha` | `DATETIME` | NOT NULL, DEFAULT NOW() | Fecha y hora de la búsqueda |

---

## Relaciones

### `principio_activo` → `medicamento`
- **Tipo:** Uno a Muchos (1:N)
- Un principio activo puede tener muchos medicamentos asociados (de marca, genéricos y bioequivalentes).
- Un medicamento pertenece a exactamente un principio activo.
- Clave foránea: `medicamento.principio_activo_id` → `principio_activo.id`

### `laboratorio` → `medicamento`
- **Tipo:** Uno a Muchos (1:N)
- Un laboratorio puede fabricar muchos medicamentos.
- Un medicamento es fabricado por exactamente un laboratorio.
- Clave foránea: `medicamento.laboratorio_id` → `laboratorio.id`

### `medicamento` → `precio`
- **Tipo:** Uno a Muchos (1:N)
- Un medicamento puede tener múltiples registros de precio (historial).
- Cada precio pertenece a exactamente un medicamento.
- El precio vigente se identifica con el campo `precio.vigente = TRUE`.
- Clave foránea: `precio.medicamento_id` → `medicamento.id`

### `usuario` → `historial_busqueda`
- **Tipo:** Uno a Muchos (1:N), opcional
- Un usuario puede tener muchas búsquedas en su historial.
- Una búsqueda puede existir sin usuario (búsqueda anónima), por eso `usuario_id` es NULLABLE.
- Clave foránea: `historial_busqueda.usuario_id` → `usuario.id`

---

## Diagrama de Relaciones (texto)

```
principio_activo (1) ────────────── (N) medicamento (N) ────────────── (1) laboratorio
                                            │
                                            │ (1)
                                            │
                                           (N)
                                          precio

usuario (1) ──────────────────────────── (N) historial_busqueda
```
