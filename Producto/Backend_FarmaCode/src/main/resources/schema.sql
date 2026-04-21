-- =============================================================
-- FarmaCode - DDL de base de datos
-- Charset: utf8mb4 / Collation: utf8mb4_unicode_ci
-- Todas las tablas usan CREATE TABLE IF NOT EXISTS para ser
-- idempotentes en reinicios del servidor.
-- =============================================================

-- -------------------------------------------------------------
-- Tabla: principio_activo
-- Entidad central que agrupa medicamentos bioequivalentes
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS principio_activo (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria  VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_principio_activo_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tabla: laboratorio
-- Laboratorio farmacéutico fabricante del medicamento
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS laboratorio (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nombre     VARCHAR(255) NOT NULL,
    pais       VARCHAR(100) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_laboratorio_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tabla: medicamento
-- Producto comercial asociado a un principio activo y laboratorio
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS medicamento (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    nombre_comercial    VARCHAR(255) NOT NULL,
    dosis               VARCHAR(100) NOT NULL,
    presentacion        VARCHAR(255) NOT NULL,
    administracion      VARCHAR(100) NOT NULL,
    tipo                ENUM('GENERICO','REFERENCIA','BIOEQUIVALENTE') NOT NULL,
    cert_isp            BOOLEAN      NOT NULL DEFAULT FALSE,
    descripcion         TEXT,
    principio_activo_id BIGINT       NOT NULL,
    laboratorio_id      BIGINT       NOT NULL,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_medicamento_principio_activo
        FOREIGN KEY (principio_activo_id) REFERENCES principio_activo (id),
    CONSTRAINT fk_medicamento_laboratorio
        FOREIGN KEY (laboratorio_id) REFERENCES laboratorio (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tabla: precio
-- Historial de precios de un medicamento (vigente = TRUE = precio actual)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS precio (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    medicamento_id BIGINT         NOT NULL,
    valor          DECIMAL(10, 2) NOT NULL,
    fuente         VARCHAR(100),
    fecha_registro DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vigente        BOOLEAN        NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
    CONSTRAINT fk_precio_medicamento
        FOREIGN KEY (medicamento_id) REFERENCES medicamento (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tabla: usuario
-- Usuarios registrados en la app FarmaCode
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    nombre        VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso DATETIME,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -------------------------------------------------------------
-- Tabla: historial_busqueda
-- Registro de cada búsqueda realizada (anónima o por usuario)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historial_busqueda (
    id                        BIGINT       NOT NULL AUTO_INCREMENT,
    usuario_id                BIGINT,
    termino_busqueda          VARCHAR(255) NOT NULL,
    tipo_busqueda             ENUM('MANUAL','OCR') NOT NULL,
    resultado_principio_activo VARCHAR(255),
    resultados_encontrados    INT          NOT NULL DEFAULT 0,
    fecha                     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_historial_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
