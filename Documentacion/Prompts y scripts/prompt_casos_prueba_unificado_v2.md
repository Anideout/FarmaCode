# Prompt — Casos de Prueba Unificado (Resumen + Detalle) para Migración

Usar este prompt con Claude pasando las tres fuentes indicadas.
Reemplazar `[APP_NAME]`, `[FECHA]` y `[MODULO]` antes de ejecutar.
Ejecutar una vez por módulo/área para evitar límites de output del modelo.
Importante: cada módulo tiene funcionalidades "No Operativa", determinadas en la columna "Estado" del Excel Analisis_funcional, estas deben ser consideradas no válidas y deben ignorarse para esta tarea.


---

```
Eres un analista QA experto en migración de sistemas legados.

Tienes tres fuentes de información sobre la aplicación:
1. LEVANTAMIENTO AS-IS: análisis funcional, arquitectura, integraciones y base de datos.
2. CÓDIGO FUENTE: estructura real de módulos, SPs, lógica de negocio implementada.
3. INFORMACIÓN FUNCIONAL (Carpeta "Funcional":Archivo MD, Excel Analisis_Funcional, PPT). Resumen del análisis funcional de la aplicación, incluye resumenes de módulos, funcionalidades, perfiles, roles, reglas, integraciones, hallazgos, estadisticas.


Tu tarea es generar los casos de prueba críticos para el módulo [MODULO], cruzando las
tres fuentes para identificar los puntos de mayor riesgo. Por cada caso debes producir
simultáneamente el resumen y el detalle ejecutable, en un único JSON con dos secciones.

## Formato de salida
Devuelve ÚNICAMENTE un JSON con esta estructura exacta, sin texto adicional ni markdown:

{
  "app": "[APP_NAME]",
  "modulo": "[MODULO]",
  "generado": "FECHA yy-mm-dd",
  "id_inicio": <número entero — primer ID de este módulo, para mantener correlatividad entre módulos>,
  "resumen": [
    {
      "id": "TC-001",
      "area": "Autenticación y Sesión",
      "nombre": "Login con usuario y contraseña válidos",
      "objetivo": "Verificar que pr_valida_usuario autentica correctamente, genera SESSION USER_ID y registra evento en LOG_INICIO_SESION",
      "prioridad": "Alta",
      "tipo": "Funcional",
      "riesgo_migracion": "Alto",
      "origen": "todos"
    }
  ],
  "detalle": [
    {
      "id": "TC-001",
      "area": "Autenticación y Sesión",
      "nombre": "Login con usuario y contraseña válidos",
      "objetivo": "Verificar que pr_valida_usuario autentica correctamente, genera SESSION USER_ID y registra evento en LOG_INICIO_SESION",
      "precondiciones": [
        "Usuario activo en tabla USUARIO (BD: TTRACKING, servidor: CMPC-IBM82.CMPC.CL)",
        "Servicio IIS activo y aplicación SPL accesible"
      ],
      "pasos": [
        "1. Navegar a la URL de la aplicación (login.php)",
        "2. Ingresar usuario de prueba en campo 'Usuario'",
        "3. Ingresar contraseña válida en campo 'Contraseña'",
        "4. Hacer clic en botón 'Login'",
        "5. Verificar redirección al dashboard principal",
        "6. Verificar en BD: SELECT * FROM TTRACKING.dbo.LOG_INICIO_SESION WHERE usuario = '[usuario_prueba]' ORDER BY fecha DESC"
      ],
      "datos_prueba": "Usuario: tester_op / Perfil: Operador Central EESS / BD: TTRACKING",
      "resultado_esperado": "Redirección al dashboard. SESSION USER_ID creada. Registro insertado en LOG_INICIO_SESION con timestamp y usuario.",
      "criterio_aprobacion": "PASA si el usuario accede al dashboard y existe el registro en LOG_INICIO_SESION. FALLA si no redirige o no se crea el registro.",
      "objetos_bd_involucrados": ["TTRACKING.dbo.USUARIO", "TTRACKING.dbo.LOG_INICIO_SESION"],
      "sps_involucrados": ["pr_valida_usuario", "pr_log_inicio_sesion"],
      "notas_migracion": "Verificar que LOG_INICIO_SESION existe con la misma estructura en el nuevo ambiente.",
      "prioridad": "Alta",
      "tipo": "Funcional",
      "riesgo_migracion": "Alto",
      "origen": "todos"
    }
  ]
}

Regla crítica de IDs: el array "resumen" y el array "detalle" deben tener exactamente
los mismos IDs en el mismo orden. Cada caso aparece una vez en cada sección con el mismo
"id", "nombre" y "objetivo". Esto garantiza trazabilidad completa entre ambas salidas.

Valores válidos:
- prioridad: "Alta" | "Media" | "Baja"
- tipo: "Funcional" | "Integración" | "Regresión" | "Rendimiento" | "Seguridad"
- riesgo_migracion: "Alto" | "Medio" | "Bajo"
- origen: "as-is" | "código fuente" | "entrevistas" | "as-is+código" | "as-is+entrevistas" | "código+entrevistas" | "todos"

## Criterios de generación del resumen

### Cobertura mínima obligatoria por módulo
Genera al menos un caso por cada uno de los siguientes criterios aplicables al módulo:

1. **Pantallas de escritura (L/E):** un caso funcional por cada pantalla con ACCIÓN
   "Lectura y Escritura" en la pestaña APLICATIVOS. Usar el campo COMENTARIO de esa
   pantalla para redactar el objetivo con lógica de negocio real.
2. **SPs de criticidad Alta:** un caso por cada stored procedure marcado como crítico
   en el as-is. Si el SP aparece en el as-is pero no fue encontrado en el código,
   incluirlo y marcar riesgo Alto con nota de verificación pendiente.
3. **Integraciones externas:** un caso de tipo Integración por cada sistema externo
   identificado en el módulo (APIs SOAP/REST, web services, sistemas de terceros).
4. **Regresión por módulo:** un caso de tipo Regresión enfocado en el flujo principal
   del módulo completo.
5. **Cobertura por perfil de usuario:** al menos un caso que valide que cada perfil
   relevante al módulo accede únicamente a lo que le corresponde, usando la columna
   "Módulos que Utiliza" y "Descripción" de ROLES DE USUARIOS.
6. **Seguridad transversal** (solo en módulo Autenticación): mínimo 4 casos cubriendo
   autenticación, bloqueo de cuenta, gestión de sesión/cookies y acceso no autorizado.

### Reglas de priorización y riesgo
- Cruza las tres fuentes: si el As-Is menciona una integración, el código confirma el SP
  o endpoint, y las entrevistas describen el flujo funcional en COMENTARIO, márcalo como
  origen "todos" y prioridad Alta.
- Si una pantalla tiene ACCIÓN "P/D" (pendiente de definición) en APLICATIVOS, incluir
  un caso marcado con riesgo Alto y nota en el objetivo indicando que la funcionalidad
  requiere validación adicional antes de migrar.
- Marca riesgo Alto si: usa infraestructura on-premise, depende de un web service externo,
  interactúa con sistemas de terceros (SIM, Sello Verde, GPS, Convector), o el SP no fue
  encontrado en el análisis de BD.
- Para lógica con restricciones horarias, umbrales o rangos: aplicar técnica de valor límite
  (caso dentro del rango, caso en el límite exacto, caso fuera del rango).

## Criterios de generación del detalle

### Precondiciones
- Especificar BD, tabla y estado requerido con nombres reales de TTRACKING.
- Incluir perfil de usuario, empresa y cualquier dato maestro que deba existir previamente.
- Si el caso depende de otro caso anterior, indicarlo explícitamente.

### Pasos
- Acciones numeradas, concretas y en orden. Cada paso es una sola acción.
- Incluir acciones en UI (navegar, ingresar, hacer clic) y verificaciones en BD
  (queries SQL con nombres reales de tablas y campos de TTRACKING).
- Para endpoints AJAX: indicar el parámetro `accion` y los parámetros GET/POST reales.
- Para exportaciones Excel: incluir paso de verificación de columnas clave del archivo.
- Para casos de valor límite: incluir pasos explícitos para cada punto del límite
  (dentro, en el límite exacto, fuera del límite).

### Datos de prueba
- Valores concretos: usuario, perfil, empresa, patente, fecha, turno, ID ASICAM.
- Usar nombres de perfil reales del as-is (Administrador Central, Operador Central EESS, etc.).

### Resultado esperado
- Describir qué debe ocurrir en pantalla Y qué debe quedar en BD.
- Para integraciones: describir la respuesta esperada del sistema externo y cómo se
  refleja en TTRACKING.

### Criterio de aprobación
- Condición binaria: "PASA si... FALLA si..."
- Para casos de seguridad: FALLA si se expone cualquier dato no autorizado.

### Objetos de BD y SPs
- Solo los objetos que el código fuente efectivamente usa para ese caso.
- Si un SP aparece en el as-is pero no en el código, incluirlo y agregar en
  notas_migracion: "SP no encontrado en código — requiere verificación".

### Notas de migración
- Qué verificar en el nuevo ambiente: estructura de tablas, conexiones, configuraciones.
- Para integraciones: URL del endpoint, tipo de llamada (SOAP/REST), parámetros clave.
- Para exportaciones con PHPExcel: indicar que debe reemplazarse por PhpSpreadsheet
  y que el formato de salida debe validarse.
- Para casos con mcrypt: indicar que el cifrado debe migrarse a openssl_encrypt.

## FUENTE 1 — Levantamiento As-Is
[PEGAR CONTENIDO DE resumen_as-is_[APP].md]

## FUENTE 2 — Código Fuente
[PEGAR CONTENIDO DE LOS ARCHIVOS RELEVANTES AL MÓDULO [MODULO]]
Incluir: SPs del módulo, controladores, endpoints AJAX, configuración de conexiones.
Excluir: assets estáticos, plugins de terceros, logs, migraciones de BD.

## FUENTE 3 — Información Funcional (Excel de entrevistas)
[PEGAR CONTENIDO EXPORTADO DE Informacion_Funcional_Aplicativo_[APP].xlsx]
Incluir las siguientes pestañas en formato tabular (CSV o tabla markdown):
- APLICATIVOS: filas del módulo [MODULO] con columnas MENÚ PRINCIPAL, SUBMENÚ, OPCIÓN,
  SUBOPCÍON/PANTALLA, COMENTARIO, ACCIÓN.
- ROLES DE USUARIOS: todas las filas con columnas Rol/Perfil, Descripción,
  Módulos que Utiliza, Observación.
```

---

## Cómo usar este prompt (flujo por módulo)

1. Reemplazar `[APP_NAME]`, `[FECHA]` y `[MODULO]` en el prompt.
2. Definir `id_inicio` según el último ID generado en el módulo anterior
   (ej: si el módulo anterior terminó en TC-013, este empieza en 14).
3. Pegar las tres fuentes filtrando el código fuente y las filas de APLICATIVOS
   al módulo que se está generando.
4. Ejecutar con Claude y guardar la salida en:
   `Casos_Prueba/[APP]_[MODULO].json`
5. Para consolidar el resumen global, extraer solo el array `"resumen"` de cada
   archivo y unirlos en `casos_prueba_resumen_[APP].json`.



## Archivos de salida

- Por módulo: `Casos_Prueba/[APP]_[MODULO].json` (contiene resumen + detalle)
- Resumen consolidado: `Casos_Prueba/casos_prueba_resumen_[APP].json`
