# 💊 FarmaCode

## 📱 Descripción

FarmaCode es una aplicación móvil Android que permite identificar medicamentos a partir de su nombre comercial (mediante OCR o ingreso manual) y obtener su **principio activo**, junto con una lista de **alternativas bioequivalentes**.

---

## 🧠 Arquitectura

El sistema sigue una arquitectura **cliente-servidor en tres capas**:

```
Android App (Cliente)
        ↓
API REST (Backend - Spring Boot)
        ↓
Base de Datos (MySQL)
```

---

## ⚙️ Stack Tecnológico

### 📱 Frontend (Android)

* Kotlin
* CameraX
* Retrofit
* OkHttp
* Material Components
* Coil / Glide

### 🔎 Reconocimiento de texto

* ML Kit (OCR)

### ⚙️ Backend

* Java
* Spring Boot
* Spring Data JPA
* Spring Validation
* Lombok (opcional)
* Spring Security (opcional)

### 🤖 Integración IA

* Claude API (Anthropic)

### 🗄️ Base de Datos

* MySQL

### 🧪 Testing

* JUnit
* Postman / Insomnia

### 🔧 Herramientas

* GitHub
* Swagger / OpenAPI
* Docker (opcional)

---

## 🔌 Funcionalidades Principales

* 📷 Escaneo de medicamentos mediante OCR
* ⌨️ Búsqueda manual por nombre comercial
* 🔄 Mapeo a principio activo mediante IA
* 📊 Listado de bioequivalentes
* 💲 Comparación de precios
* 🔗 Consumo de API REST

---

## 📂 Estructura del Proyecto

```
FarmaCode/
│
├── backend/          # API REST (Spring Boot)
├── android-app/      # Aplicación Android
├── database/         # Scripts y modelo de datos
└── docs/             # Documentación técnica
```

---

## 🧩 Backend

### Funcionalidades

* API REST para gestión de medicamentos
* CRUD de entidades principales
* Integración con IA para procesamiento de texto
* Documentación automática con Swagger

### Entidades principales

* Medicamento
* PrincipioActivo
* Bioequivalente
* Precio

---

## 📱 Aplicación Android

### Funcionalidades

* Captura de imágenes con CameraX
* Reconocimiento de texto con ML Kit
* Consumo de API mediante Retrofit
* Navegación entre pantallas
* Manejo de errores

---

## 🔄 Flujo de funcionamiento

1. Usuario escanea o ingresa un medicamento
2. OCR extrae el texto (si aplica)
3. Se envía el nombre al backend
4. Backend consulta IA → obtiene principio activo
5. Se consultan bioequivalentes en la base de datos
6. Se retorna listado ordenado por precio
7. App muestra resultados

---

## 🚀 Instalación

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

Configurar conexión a MySQL en:

```
src/main/resources/application.properties
```

---

### Android

1. Abrir proyecto en Android Studio
2. Configurar URL del backend
3. Ejecutar en emulador o dispositivo

---

## 🔎 Documentación API

Disponible en:

```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testing

* Pruebas unitarias con JUnit
* Pruebas de endpoints con Postman
* Validación de integración frontend-backend

---

## 🔐 Consideraciones

* API preparada para integración con seguridad (Spring Security)
* Manejo de errores en frontend y backend
* Arquitectura escalable y modular

---

## 📌 Estado del Proyecto

🚧 En desarrollo (MVP funcional en construcción)

---
