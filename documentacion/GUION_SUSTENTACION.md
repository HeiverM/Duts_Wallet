# Guion de Sustentación — DUTS Wallet

> Guía práctica para exponer y defender el proyecto **DUTS Wallet** en la sustentación de Programación Orientada a Objetos (3er semestre, UTS).  
> Apóyate en los diagramas ubicados en `documentacion/diagramas/*.png` (o `documentacion/DIAGRAMAS_UML.md`).

---

## 1. Apertura: ¿Qué es DUTS Wallet? (1 minuto)

**Qué decir:**
> "DUTS Wallet es una aplicación de escritorio desarrollada en Java con interfaz gráfica Swing (`JFrame`) para las Unidades Tecnológicas de Santander. Administra la moneda virtual institucional **DUTS**: permite al estudiante consultar su saldo, hacer seguimiento a la meta de **70.000 DUTS** para graduarse, transferir DUTS a otros estudiantes, consultar estadísticas y un dashboard gráfico, inscribirse en novedades institucionales y recibir notificaciones. Todo funciona con persistencia local en archivos `.txt` y sin librerías externas."

**Puntos clave si preguntan por el entorno técnico:**
- Solo SDK estándar de Java (sintaxis compatible con Java 8).
- Sin frameworks, sin bases de datos, sin lambdas/streams ni patrones de diseño complejos, para mantener el código claro y fácil de sustentar.
- Persistencia en archivos `.txt` usando `;` como separador y fechas en formato `yyyy-MM-dd`.

---

## 2. Arquitectura general del proyecto (2 minutos)

*Apóyate en `documentacion/diagramas/7_diagrama_paquetes.png` y `9_diagrama_arquitectura_completa.png`.*

El proyecto tiene **44 clases en `src/`**, organizadas en **6 paquetes** con flujo de dependencias unidireccional (de arriba hacia abajo):

| Paquete | Clases | Responsabilidad |
|---|---|---|
| `interfaces` | `Persistible`, `Exportable` | Contratos que obligan a saber convertirse a línea `.txt` o fila `.csv`. |
| `excepciones` | `ValidacionException` | Única excepción propia para errores de negocio (saldo insuficiente, duplicados, credenciales). |
| `util` | `Validador`, `GestorTema`, `CalculadoraDuts` | Validaciones reutilizables, tema claro/oscuro global y cálculos de la calculadora. |
| `modelo` | `Usuario`, `Estudiante`, `Administrador`, `Movimiento`, `Novedad`, `Inscripcion`, `Notificacion`, `Amistad` | Entidades del dominio con atributos privados, getters y setters selectivos. |
| `persistencia` | `ArchivoTexto`, `GestorUsuarios`, `GestorMovimientos`, `GestorNovedades`, `GestorInscripciones`, `GestorNotificaciones`, `GestorAmigos`, `GestorAuditoria` | Mantienen listas `ArrayList` en memoria y sincronizan con `datos/*.txt`. |
| `vista` | `Main` + 21 clases de interfaz gráfica | Ventanas Swing (`JFrame`/`JDialog`) y componentes (`PanelFondoLogin`, `PanelGrafico`, `BotonTema`). |

**Regla de oro de la arquitectura:**
- La **vista** no contiene lógica de negocio ni escribe archivos: delega en los **gestores**, en `Validador` y en `CalculadoraDuts`.
- Los eventos de todas las ventanas se manejan con `implements ActionListener` y un único `actionPerformed(ActionEvent e)`.

---

## 3. Cómo explicar los pilares de POO con el código real (3 minutos)

*Esta es la pregunta más importante de la sustentación. Apóyate en `1_diagrama_modelo.png` y `2_diagrama_interfaces_util.png`.*

### 3.1 Abstracción
- **Dónde está:** `src/modelo/Usuario.java` (`public abstract class Usuario`).
- **Cómo explicarlo:**
  > "`Usuario` es una clase abstracta que representa lo común entre cualquier usuario del sistema (`documento`, `nombreCompleto`, `nombreUsuario`, `contrasena`, `preguntaSeguridad`, `respuestaSeguridad`). No se puede instanciar directamente con `new Usuario(...)`, y declara dos métodos abstractos: `getRol()` y `getResumenPerfil()`, obligando a las clases hijas a implementarlos."

### 3.2 Herencia
- **Dónde está:** `Estudiante extends Usuario`, `Administrador extends Usuario`, `ValidacionException extends Exception`, y cada ventana `extends JFrame` (o `JDialog`).
- **Cómo explicarlo:**
  > "`Estudiante` y `Administrador` heredan los 6 atributos y getters de `Usuario` llamando a `super(...)` en su constructor. `Estudiante` además agrega `programaAcademico`, `saldoDuts` y la constante `META_DUTS = 70000` con métodos propios como `sumarDuts`, `restarDuts` y `getPorcentajeMeta`."

### 3.3 Polimorfismo
- **Dónde está:** `Usuario` / `Estudiante` / `Administrador`, `GestorUsuarios` y `VentanaInicio`.
- **Cómo explicarlo con dos ejemplos concretos:**
  1. **En persistencia (`GestorUsuarios.guardar()`):** la lista es `ArrayList<Usuario>`, pero guarda objetos `Estudiante` y `Administrador`. Al recorrerla con un `for`, llama a `usuario.aLineaTexto()` y Java ejecuta la versión correspondiente según el objeto real.
  2. **En el login (`VentanaInicio.iniciarSesion()`):** `gestorUsuarios.iniciarSesion(...)` devuelve un `Usuario`. Luego se llama `usuario.getRol()` (que responde `"ESTUDIANTE"` o `"ADMINISTRADOR"` según la instancia real) para abrir `VentanaMenuEstudiante` o `VentanaMenuAdmin`.

### 3.4 Encapsulamiento
- **Dónde está:** todas las clases de `modelo`, `Estudiante.restarDuts()` y `Validador`.
- **Cómo explicarlo:**
  > "Todos los atributos del modelo son `private` y solo tienen setters para lo estrictamente modificable (por ejemplo, `Usuario` solo expone `setContrasena`, y `Notificacion` y `Amistad` no tienen setters). Además, el saldo del estudiante no se modifica libremente: `restarDuts(int valor)` valida internamente si alcanza y lanza `ValidacionException(\"Saldo insuficiente\")` si falta saldo. Y `Validador` encapsula las validaciones de campos vacíos, `;`, enteros positivos y fechas."

### 3.5 Interfaces
- **Dónde está:** `src/interfaces/Persistible.java` y `src/interfaces/Exportable.java`.
- **Cómo explicarlo:**
  > "`Persistible` define `String aLineaTexto()` y la implementan todas las clases que se guardan en `.txt` (`Usuario`, `Movimiento`, `Novedad`, `Inscripcion`, `Notificacion`, `Amistad`). `Exportable` define `String aFilaCsv()` y la implementa `Movimiento` para generar filas de CSV limpiando comas de la descripción."

### 3.6 Objetos e instanciación
- **Dónde está:** `src/vista/Main.java` y métodos `cargar()` de los gestores.
- **Cómo explicarlo:**
  > "Al arrancar en `Main.main()`, se instancian con `new` los 6 gestores una sola vez, cada uno lee su archivo `.txt` e instancia sus objetos en memoria, y se pasan por constructor a `VentanaInicio`."

---

## 4. Arranque y dependencias entre gestores (1.5 minutos)

*Apóyate en `8_diagrama_dependencias_gestores.png` y `3_diagrama_persistencia.png`.*

En `Main.main()` los gestores se crean **una única vez** en orden de dependencias:

1. `GestorUsuarios` — lee `datos/usuarios.txt` (si no existe, crea `admin` / `admin123`).
2. `GestorMovimientos(gestorUsuarios)` — lee `datos/movimientos.txt`; necesita `GestorUsuarios` para ajustar y guardar saldos.
3. `GestorNovedades` — lee `datos/novedades.txt` (si no existe, crea 4 novedades institucionales por defecto).
4. `GestorNotificaciones` — lee `datos/notificaciones.txt`.
5. `GestorInscripciones(gestorMovimientos, gestorNotificaciones, gestorUsuarios, gestorNovedades)` — lee `datos/inscripciones.txt`; es el más conectado porque al marcar asistencia acredita DUTS, al inscribir/cancelar notifica, y consulta estudiantes y novedades.
6. `GestorAmigos(gestorUsuarios)` — lee `datos/amigos.txt`; valida que el amigo exista.
7. `GestorAuditoria` — es puramente `static`, no se instancia; escribe en `datos/auditoria.txt`.

Todos delegan la lectura/escritura en `ArchivoTexto` (`BufferedReader` / `PrintWriter` con `try-catch (IOException)`), que crea las carpetas automáticamente al escribir.

---

## 5. Flujo funcional completo (para la demo en vivo) (4 minutos)

*Apóyate en `10_diagrama_navegacion.png`, `4_diagrama_vista_hubs.png`, `5_diagrama_vista_modulos_estudiante.png` y `6_diagrama_vista_modulos_admin.png`.*

### Paso 1: Pantalla inicial (`VentanaInicio`)
- Ventana de 1280×720 con `PanelFondoLogin` (imagen `recursos/Fondo_login.jpg` + overlay institucional Pantone 5535 C `#132F20` en el lado izquierdo) y `BotonTema` (alterna modo claro/oscuro en toda la aplicación con `GestorTema`).
- Los **estudiantes** ingresan con su **documento** (en el registro no se pide alias; internamente `nombreUsuario = documento`). El **administrador** es la excepción y entra con `admin` / `admin123`.
- Desde aquí se accede a: **Registrarse** (`VentanaRegistro`), **Recuperar contraseña** (`VentanaRecuperacion` con pregunta de seguridad), **Soporte** (`VentanaSoporte` `JDialog`) y **Salir de la aplicación** (único lugar con `System.exit(0)` tras confirmación).
- Al iniciar sesión, `VentanaInicio` **se oculta** (`setVisible(false)`) y se pasa a sí misma a los menús; al pulsar **Cerrar sesión**, el menú llama a `ventanaInicio.volverAlInicio()`, que cierra las ventanas hijas abiertas, limpia los campos y conserva el tema seleccionado.

### Paso 2: Flujo del Administrador (`VentanaMenuAdmin`)
1. **CRUD de DUTS (`VentanaAdminDuts`):**
   - **Asignar:** suma al saldo del estudiante y crea un movimiento `ASIGNACION` (`emisor = "SISTEMA"`).
   - **Actualizar:** modifica valor/descripción y ajusta el saldo según la diferencia (`nuevoValor - valorAnterior`).
   - **Eliminar:** descuenta el valor asignado del saldo del estudiante (si ya lo gastó, lanza `"Saldo insuficiente"`).
2. **CRUD de Novedades (`VentanaAdminNovedades`):** crea/edita/elimina eventos institucionales validando fecha (`yyyy-MM-dd`, no anterior a hoy al crear) y `valorDuts >= 0` (permite eventos informativos con 0 DUTS).
3. **Asistencia (`VentanaAdminAsistencia`):**
   - **Asistió:** acredita `valorDuts` (movimiento `EVENTO`), cambia estado a `ASISTIO`.
   - **No asistió:** descuenta `PORCENTAJE_PENALIZACION` (20 % de `valorDuts`, movimiento `PENALIZACION`; si el saldo no alcanza, descuenta solo el saldo disponible) y pasa a `NO_ASISTIO`.
4. **Calculadora (`VentanaCalculadora`):** en modo administrador permite elegir un estudiante en un `JComboBox` o ingresar valores manualmente.

### Paso 3: Flujo del Estudiante (`VentanaMenuEstudiante`)
Al abrirse el menú, ejecuta automáticamente `gestorNotificaciones.generarProximidad(...)`, que crea alertas sin duplicar para eventos inscritos cuya fecha esté entre hoy y 3 días.

1. **Perfil y meta (`VentanaPerfil`):** muestra nombre, documento, programa, saldo y un `JProgressBar` (0–100) hacia los 70.000 DUTS (sin mostrar alias).
2. **Transferir (`VentanaTransferir`):** recibe el **documento** del receptor, valida valor `> 0`, receptor distinto del emisor y saldo suficiente; actualiza ambos saldos, guarda movimiento `TRANSFERENCIA` y crea notificación automática al receptor.
3. **Historial (`VentanaHistorial`):** filtra por tipo (`Todos`, `TRANSFERENCIA`, `EVENTO`, `PENALIZACION`, `ASIGNACION`) y rango de fechas; recorre al revés para mostrar lo más reciente primero. Permite:
   - **Exportar CSV** → `reportes/historial_<documento>.csv` (incluye promedios al final).
   - **Generar comprobante** (para transferencias enviadas) → `comprobantes/comprobante_<codigo>.txt`.
4. **Estadísticas (`VentanaEstadisticas`):** muestra promedios de ingresos por **semana (7 días)**, **mes (30 días)**, **cuatrimestre (90 días)** y **año (365 días)**: `totalIngresos / max(1, diasDesdePrimerMovimiento / diasPeriodo)`.
5. **Dashboard (`VentanaDashboard` + `PanelGrafico`):** dibuja con `Graphics` (`fillRect`, `drawString`) barras de ingresos y egresos de los últimos 60 días agrupados en 6 bloques de 10 días, con filtros que repintan usando `repaint()`.
6. **Calculadora (`VentanaCalculadora` + `CalculadoraDuts`):** tres módulos — **A** (faltantes y % meta), **B** (simulador de transferencia) y **C** (proyección en meses y eventos).
7. **Novedades (`VentanaNovedades`):** consulta novedades y condiciones, inscribe al estudiante (o reactiva si estaba `CANCELADA`) y cancela inscripciones activas (`INSCRITO`), generando notificación en cada caso.
8. **Notificaciones (`VentanaNotificaciones`):** bandeja de mensajes de transferencias recibidas, inscripciones, cancelaciones y proximidad.
9. **Amigos (`VentanaAmigos`):** agrega amigos por documento (sin duplicados ni autoagregarse), muestra su avance hacia la meta y abre `VentanaTransferir` con el documento del amigo prellenado.

---

## 6. Posibles preguntas difíciles del docente (y respuestas cortas)

| Pregunta probable | Cómo responder |
|---|---|
| **¿Por qué no usaron base de datos o JSON?** | Por requisito académico: el proyecto exige persistencia local en archivos `.txt` separados por `;` usando únicamente el SDK estándar de Java. |
| **¿Por qué la calculadora tiene lógica en `CalculadoraDuts` y no en la ventana?** | Para separar la lógica de cálculo de la interfaz gráfica: `VentanaCalculadora` solo lee entradas y muestra resultados; `CalculadoraDuts` hace los cálculos sin depender de Swing. |
| **¿Por qué `VentanaInicio` se oculta en lugar de `dispose()` al iniciar sesión?** | Para conservar la instancia y el estado del tema al cerrar sesión: los menús llaman `ventanaInicio.volverAlInicio()`, que cierra las demás ventanas, limpia los campos y la vuelve a mostrar. |
| **¿Dónde se aplica polimorfismo exactamente?** | En `Usuario.getRol()`, `getResumenPerfil()` y `aLineaTexto()`, sobrescritos por `Estudiante` y `Administrador`. `GestorUsuarios` guarda `ArrayList<Usuario>` y llama `aLineaTexto()` sin preguntar el tipo; `VentanaInicio` usa `usuario.getRol()` para decidir qué menú abrir. |
| **¿Qué pasa si dos movimientos se crean en el mismo milisegundo?** | `generarCodigo()` en `GestorMovimientos` y `GestorNovedades` verifica con un `while` si `"MOV-" + numero` ya existe y le suma `1` hasta garantizar unicidad. |
| **¿Cómo manejan los errores sin que la aplicación se cierre?** | Todas las validaciones lanzan `ValidacionException` (o en `ArchivoTexto` se captura `IOException`). Las ventanas la capturan en `actionPerformed`, la registran en `GestorAuditoria` y la muestran en un `JOptionPane`. |
| **¿Dónde está la auditoría?** | En `datos/auditoria.txt`, escrita por `GestorAuditoria.registrar(...)` en cada acción importante y error. Es oculta: ninguna pantalla la muestra. |

---

## 7. Checklist rápido antes de sustentar

- [ ] Compilar desde la raíz:
  ```powershell
  javac --release 8 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
  ```
- [ ] Ejecutar:
  ```powershell
  java -cp out vista.Main
  ```
- [ ] Verificar que `recursos/Fondo_login.jpg` está presente.
- [ ] Tener a mano los diagramas clave para proyectar:
  1. `9_diagrama_arquitectura_completa.png` (visión por capas)
  2. `1_diagrama_modelo.png` (pilares POO)
  3. `8_diagrama_dependencias_gestores.png` (creación e inyección en `Main`)
  4. `10_diagrama_navegacion.png` (flujo entre pantallas)
