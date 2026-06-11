# Prompt — Casos de Prueba FarmaCode (Resumen + Detalle)

Usar este prompt con Claude pasando las tres fuentes indicadas.
Reemplazar `[MODULO]` y `[FECHA]` antes de ejecutar.
Ejecutar una vez por módulo para evitar límites de output del modelo.
Importante: las funcionalidades con Estado "No Operativa" en la pestaña Pantallas del Excel
deben ignorarse — no generar casos para ellas.

---

```
Eres un analista QA experto en aplicaciones móviles Android (Jetpack Compose, MVVM,
Room SQLite) y APIs REST Spring Boot con integración a servicios externos (Gemini AI,
ML Kit, CameraX, MySQL en Railway).

Tienes tres fuentes de información sobre la aplicación FarmaCode:
1. LEVANTAMIENTO AS-IS: análisis funcional, arquitectura, integraciones y base de datos.
2. CÓDIGO FUENTE: ViewModels, Screens, Repositorios, DAOs, Controllers, Services y
   configuración del backend. Incluir los archivos relevantes al módulo [MODULO].
3. INFORMACIÓN FUNCIONAL (Documentacion/Funcional/): Analisis_Funcional_FarmaCode.md,
   Levantamiento_Funcional_FarmaCode.xlsx y CSVs individuales. Resumen del análisis
   funcional: módulos, funcionalidades, roles, reglas de negocio, integraciones y
   hallazgos legacy.

Tu tarea es generar los casos de prueba críticos para el módulo [MODULO], cruzando las
tres fuentes para identificar los puntos de mayor riesgo. Por cada caso debes producir
simultáneamente el resumen y el detalle ejecutable, en un único JSON con dos secciones.

## Formato de salida
Devuelve ÚNICAMENTE un JSON con esta estructura exacta, sin texto adicional ni markdown:

{
  "app": "FarmaCode",
  "modulo": "[MODULO]",
  "generado": "FECHA yy-mm-dd",
  "id_inicio": <número entero — primer ID de este módulo, para mantener correlatividad entre módulos>,
  "resumen": [
    {
      "id": "TC-001",
      "area": "Autenticación",
      "nombre": "Login con credenciales válidas registradas en Room",
      "objetivo": "Verificar que LoginViewModel.onLoginClick() valida email y contraseña contra Room SQLite, navega a la ruta 'home' y establece UserSession.userEmail correctamente",
      "prioridad": "Alta",
      "tipo": "Funcional",
      "riesgo": "Alto",
      "origen": "código+funcional"
    }
  ],
  "detalle": [
    {
      "id": "TC-001",
      "area": "Autenticación",
      "nombre": "Login con credenciales válidas registradas en Room",
      "objetivo": "Verificar que LoginViewModel.onLoginClick() valida email y contraseña contra Room SQLite, navega a la ruta 'home' y establece UserSession.userEmail correctamente",
      "precondiciones": [
        "Usuario registrado en Room SQLite (tabla user) con email 'test@farmacode.cl' y contraseña 'Test123' en texto plano",
        "App instalada y ejecutándose en emulador Android API 26+ o dispositivo físico"
      ],
      "pasos": [
        "1. Abrir la app — se muestra Pantalla de Inicio de Sesión (ruta 'login')",
        "2. Ingresar 'test@farmacode.cl' en el campo Email",
        "3. Ingresar 'Test123' en el campo Contraseña",
        "4. Pulsar el botón 'Iniciar Sesión'",
        "5. Verificar que la pantalla navega a la ruta 'home' (barra de navegación inferior visible)",
        "6. Verificar que UserSession.userEmail == 'test@farmacode.cl'"
      ],
      "datos_prueba": "Email: test@farmacode.cl / Contraseña: Test123 (texto plano) / Rol: USUARIO_REGISTRADO / BD: Room SQLite tabla user",
      "resultado_esperado": "Pantalla 'home' visible con barra de navegación inferior. UserSession.userEmail establecido. Sin mensaje de error.",
      "criterio_aprobacion": "PASA si la ruta activa es 'home' y UserSession.userEmail tiene el valor ingresado. FALLA si aparece mensaje de error o la navegación no ocurre.",
      "objetos_bd_involucrados": ["Room SQLite: tabla user"],
      "endpoints_involucrados": [],
      "notas_tecnicas": "Contraseñas almacenadas y comparadas en texto plano (user.password == state.password) — ver FACO-LEG-005. En dispositivos rooteados la BD Room es accesible sin restricciones.",
      "prioridad": "Alta",
      "tipo": "Funcional",
      "riesgo": "Alto",
      "origen": "código+funcional"
    }
  ]
}

Regla crítica de IDs: el array "resumen" y el array "detalle" deben tener exactamente
los mismos IDs en el mismo orden. Cada caso aparece una vez en cada sección con el mismo
"id", "nombre" y "objetivo". Esto garantiza trazabilidad completa entre ambas salidas.

Valores válidos:
- prioridad: "Alta" | "Media" | "Baja"
- tipo: "Funcional" | "Integración" | "Regresión" | "Rendimiento" | "Seguridad"
- riesgo: "Alto" | "Medio" | "Bajo"
- origen: "as-is" | "código" | "funcional" | "as-is+código" | "as-is+funcional" | "código+funcional" | "todos"

## Criterios de generación del resumen

### Cobertura mínima obligatoria por módulo
Genera al menos un caso por cada uno de los siguientes criterios aplicables al módulo:

1. **Pantallas de escritura (L/E):** un caso funcional por cada pantalla con Acción
   "Lectura y Escritura" en la pestaña Pantallas del Excel. Usar el campo COMENTARIO
   de esa fila para redactar el objetivo con la lógica de negocio real.

2. **Endpoints REST y métodos críticos:** un caso por cada endpoint activo del módulo
   identificado en el campo "Integra con" o "Invoca" de la pestaña Pantallas, y en los
   Controllers/Services del backend. Si un endpoint existe en el código pero el frontend
   no lo invoca actualmente (ej. /api/busqueda/foto), incluirlo con riesgo Alto y nota
   indicando que no está integrado en el cliente Android.

3. **Integraciones externas:** un caso de tipo Integración por cada sistema externo
   identificado en el módulo (Gemini API, ML Kit TextRecognition, CameraX, Room SQLite,
   MySQL Railway, ApiKeyFilter). Usar la pestaña "Reglas e Integraciones" del Excel para
   obtener detalles técnicos del endpoint, timeouts y condiciones de error.

4. **Regresión por módulo:** un caso de tipo Regresión enfocado en el flujo principal
   completo del módulo (happy path de extremo a extremo).

5. **Cobertura por perfil de usuario:** al menos un caso que valide que el perfil
   USUARIO_REGISTRADO accede únicamente a las pantallas autenticadas del módulo. Si el
   módulo tiene pantallas accesibles sin sesión (login, register), incluir un caso que
   verifique que las pantallas protegidas redirigen a 'login' cuando no hay sesión.

6. **Seguridad transversal** (solo en módulo Autenticación): mínimo 4 casos cubriendo:
   credenciales inválidas (email incorrecto, contraseña incorrecta, cuenta inexistente),
   acceso a rutas protegidas sin sesión activa, cierre de sesión con limpieza completa
   del backstack, y exposición de contraseñas en texto plano (FACO-LEG-005).

7. **Hallazgos Legacy:** un caso de tipo Seguridad o Funcional por cada hallazgo
   FACO-LEG-XXX con impacto "Requiere validación" que pertenezca al módulo. Indicar el
   ID del hallazgo en el objetivo y en notas_tecnicas. Los hallazgos con impacto
   "Sin impacto (código muerto)" no requieren caso de prueba propio pero deben
   mencionarse en notas_tecnicas de los casos relacionados.

### Reglas de priorización y riesgo
- Cruza las tres fuentes: si el As-Is describe la integración, el código confirma el
  endpoint o método, y el funcional documenta la regla de negocio en COMENTARIO,
  márcalo como origen "todos" y prioridad Alta.
- Marca riesgo Alto si: el caso involucra una llamada al backend Railway (conectividad
  externa), usa la Gemini API (servicio externo de pago), tiene un hallazgo Legacy
  asociado con impacto "Requiere validación", maneja datos sensibles (contraseñas,
  email, API key), o el endpoint existe en el backend pero el frontend no lo llama aún.
- Para reglas con umbrales numéricos (ej. OCR insuficiente: <15 letras, ratio <40%,
  <2 palabras ≥4 chars): aplicar técnica de valor límite — caso dentro del umbral,
  caso exactamente en el límite, caso fuera del umbral.
- Si una funcionalidad aparece como Operativa en el Excel pero su implementación es
  parcial o tiene un TODO documentado en el código, incluir un caso marcando riesgo
  Alto con nota descriptiva en notas_tecnicas.

## Criterios de generación del detalle

### Precondiciones
- Especificar la BD (Room SQLite del dispositivo o MySQL Railway), la tabla y el estado
  requerido con nombres reales de tablas (user, medication, scan_history, principio_activo,
  laboratorio, medicamento, precio, usuario, historial_busqueda).
- Indicar si se requiere conectividad a internet (para casos que llaman al backend Railway
  o a la Gemini API).
- Incluir el permiso Android requerido si aplica (CAMERA para módulo Escáner OCR).
- Si el caso depende de un caso anterior (ej. debe estar logueado), indicarlo explícitamente.

### Pasos
- Acciones numeradas, concretas y en orden. Cada paso es una sola acción.
- Para pasos en la UI Android: indicar pantalla activa, elemento de UI y acción
  (ej. "Pulsar botón 'Escanear' en Pantalla de Fotografiar Envase").
- Para verificaciones en BD Room: indicar tabla y campo a inspeccionar.
- Para llamadas al backend: indicar el endpoint HTTP (método + ruta), el cuerpo esperado
  y el código de respuesta esperado.
- Para llamadas a Gemini API: indicar cuándo se activa el fallback (OCR insuficiente)
  y qué respuesta especial se prueba (NO_ES_MEDICAMENTO, IMAGEN_ILEGIBLE).
- Para casos de valor límite: incluir pasos explícitos para cada punto del límite
  (dentro, en el límite exacto, fuera del límite).

### Datos de prueba
- Valores concretos: email, contraseña, nombre de medicamento, texto OCR de prueba,
  imagen de envase (si aplica), idioma seleccionado.
- Usar nombres de rol reales: USUARIO_REGISTRADO (activo) / USUARIO_ANONIMO (no implementado).
- Para casos de Escáner OCR: indicar si el texto OCR de prueba es suficiente o insuficiente
  según los umbrales de BusquedaService.esOcrInsuficiente().

### Resultado esperado
- Describir qué debe ocurrir en la UI Android Y qué debe quedar en BD (Room o MySQL).
- Para integraciones: describir la respuesta HTTP esperada del backend y cómo se refleja
  en la UI (mensaje, ModalBottomSheet, SnackBar, etc.).
- Para la Gemini API: describir el campo de respuesta clave (principioActivo, nombreComercial)
  y cómo se muestra al usuario.

### Criterio de aprobación
- Condición binaria: "PASA si... FALLA si..."
- Para casos de seguridad: FALLA si se expone cualquier dato sensible no autorizado o si
  una pantalla protegida es accesible sin sesión activa.

### Objetos de BD y endpoints
- objetos_bd_involucrados: solo las tablas que el caso efectivamente lee o escribe.
  Formato: "Room SQLite: tabla X" o "MySQL Railway: tabla Y".
- endpoints_involucrados: solo los endpoints HTTP que el caso invoca.
  Formato: "POST /api/busqueda/ocr" o "DELETE /api/historial/{id}".
- Si un endpoint aparece en el As-Is o el funcional pero no fue encontrado activo en
  el código del cliente Android, incluirlo y agregar en notas_tecnicas:
  "Endpoint existe en backend pero no está siendo invocado por el cliente Android actual".

### Notas técnicas
- Qué verificar en el ambiente de prueba: conectividad Railway, variables de entorno
  del backend (GEMINI_API_KEY, APP_API_KEY, DB_URL), versión Android del dispositivo.
- Para integraciones con Gemini: URL del endpoint, modelo usado (gemini-2.5-flash),
  condición de activación (OCR insuficiente vs. suficiente vs. solo imagen).
- Para hallazgos Legacy: referenciar el ID FACO-LEG-XXX y describir el riesgo técnico
  que el caso está validando.
- Para el módulo Autenticación: recordar que no hay JWT ni Spring Security activos —
  el HARDCODED_USER_ID = 1L en HistorialController afecta los datos de historial backend.

## FUENTE 1 — Levantamiento As-Is
[PEGAR CONTENIDO DE Documentacion/resumen_as-is_FarmaCode.md]

## FUENTE 2 — Código Fuente
[PEGAR CONTENIDO DE LOS ARCHIVOS RELEVANTES AL MÓDULO [MODULO]]
Frontend Android (Producto/FarmaCode-Frontend/):
  Incluir: Screen correspondiente, ViewModel, Repository, DAO, entidad Room, RetrofitClient.
  Excluir: archivos de tema/estilos, assets estáticos, res/drawable.
Backend Spring Boot (Producto/Backend_FarmaCode/):
  Incluir: Controller, Service, entidad JPA, repository JPA del módulo.
  Excluir: configuración de Swagger, archivos de migración, logs.

## FUENTE 3 — Información Funcional (Documentacion/Funcional/)
[PEGAR CONTENIDO EXPORTADO DE Levantamiento_Funcional_FarmaCode.xlsx O CSVs RELEVANTES]
Incluir las siguientes pestañas en formato tabular (CSV o tabla markdown):
- Pantallas (modulos_FarmaCode.csv): filas del módulo [MODULO] con columnas ID, MODULO,
  FUNCIONALIDAD, OPCION/PANTALLA, COMENTARIO, Accion, Estado, Dependencias.
- Roles (roles_FarmaCode.csv): todas las filas con columnas Nombre, Descripcion,
  Permisos, Estado.
- Reglas e Integraciones (reglas_integraciones_FarmaCode.csv): filas del módulo [MODULO]
  con todas las columnas (ID, Categoria, Tipo, Descripcion, Detalle Tecnico, Estado).
- Hallazgos Legacy (legacy_FarmaCode.csv): filas relacionadas al módulo [MODULO]
  con columnas ID, Tipo, Ubicacion, Descripcion, Impacto Funcional, Recomendacion.
```

---

## Cómo usar este prompt (flujo por módulo)

1. Reemplazar `[MODULO]` con el nombre exacto del módulo y `[FECHA]` con la fecha actual.
2. Definir `id_inicio` según el último ID generado en el módulo anterior
   (ej: si el módulo anterior terminó en TC-013, este empieza en 14).
3. Pegar las tres fuentes filtrando el código fuente y las filas de la pestaña Pantallas
   al módulo que se está generando.
4. Ejecutar con Claude y guardar la salida en:
   `Documentacion/Casos_Prueba/FarmaCode_[MODULO].json`
5. Para consolidar el resumen global, extraer solo el array `"resumen"` de cada
   archivo y unirlos en `Documentacion/Casos_Prueba/casos_prueba_resumen_FarmaCode.json`.


## Módulos de FarmaCode (orden sugerido de ejecución)

| Orden | Módulo                  | ID funcionalidades         | Hallazgos Legacy asociados        |
|-------|-------------------------|----------------------------|-----------------------------------|
| 1     | Autenticación           | FACO-AUTH-001, FACO-AUTH-002 | FACO-LEG-005                    |
| 2     | Catálogo de Medicamentos| FACO-CAT-001               | —                                 |
| 3     | Escáner OCR             | FACO-SCAN-001              | FACO-LEG-003, LEG-006, LEG-007, LEG-008 |
| 4     | Asistente IA            | FACO-CHAT-001              | —                                 |
| 5     | Ayuda                   | FACO-HLP-001               | FACO-LEG-007                      |
| 6     | Perfil y Configuración  | FACO-PERF-001, FACO-PERF-002 | FACO-LEG-004                    |


## Archivos de salida

- Por módulo:  `Documentacion/Casos_Prueba/FarmaCode_[MODULO].json`  (resumen + detalle)
- Resumen consolidado: `Documentacion/Casos_Prueba/casos_prueba_resumen_FarmaCode.json`
