# Diagramas UML — DUTS Wallet

Diagramas de clases UML que documentan la arquitectura completa del proyecto **DUTS Wallet**, elaborados a partir de `AGENTS.md` y `plan_implementacion.md`.

Los diagramas están disponibles en dos formatos:
- **PlantUML** (`.puml`) → mayor detalle, exportables a PNG/SVG con herramientas (ver carpeta `documentacion/diagramas/`)
- **Mermaid** (este archivo) → se renderizan directamente en GitHub, GitLab, VS Code, Obsidian

---

## Índice de diagramas (versión Mermaid)

| # | Sección | Contenido |
|---|---|---|
| 1 | Paquete `modelo` | `Usuario` (abstracta), `Estudiante`, `Administrador`, `Movimiento`, `Novedad`, `Inscripcion`, `Notificacion`, `Amistad` |
| 2 | Interfaces y utilidades | `Persistible`, `Exportable`, `ValidacionException`, `Validador`, `GestorTema`, `CalculadoraDuts` |
| 3 | Paquete `persistencia` | `ArchivoTexto`, los 7 gestores y sus dependencias |
| 4 | Vista: hubs y login | `Main`, `VentanaInicio`, menús, registro, recuperación, soporte |
| 5 | Vista: módulos estudiante | 9 ventanas del estudiante (`Perfil`, `Transferir`, `Historial`, etc.) |
| 6 | Vista: módulos admin | 3 ventanas del administrador (DUTS, novedades, asistencia) |
| 7 | Arquitectura de paquetes | Organización por capas y flujo de dependencias |
| 8 | Dependencias entre gestores | Cómo `Main` crea e inyecta los gestores |
| 9 | Navegación | Flujo entre ventanas (estados) |
| 10 | Resumen por capas | Vista general de la arquitectura |

> **Los diagramas en formato PNG** con mayor resolución están en `documentacion/diagramas/*.png` (generados desde los `.puml`).

---

## 1. Diagrama de clases — Paquete `modelo`

```mermaid
classDiagram
    class Usuario {
        <<abstract>>
        -String documento
        -String nombreCompleto
        -String nombreUsuario
        -String contrasena
        -String preguntaSeguridad
        -String respuestaSeguridad
        +getDocumento() String
        +getNombreCompleto() String
        +getNombreUsuario() String
        +getContrasena() String
        +getPreguntaSeguridad() String
        +getRespuestaSeguridad() String
        +setContrasena(String) void
        +getRol()* String
        +getResumenPerfil()* String
        +aLineaTexto()* String
    }

    class Estudiante {
        -String programaAcademico
        -int saldoDuts
        +int META_DUTS = 70000
        +getProgramaAcademico() String
        +getSaldoDuts() int
        +sumarDuts(int) void
        +restarDuts(int) void
        +getDutsFaltantes() int
        +getPorcentajeMeta() int
        +metaCumplida() boolean
        +getRol() String
        +getResumenPerfil() String
        +aLineaTexto() String
    }

    class Administrador {
        +getRol() String
        +getResumenPerfil() String
        +aLineaTexto() String
    }

    class Movimiento {
        -String codigo
        -LocalDate fecha
        -String tipo
        -String documentoEmisor
        -String documentoReceptor
        -int valor
        -String descripcion
        +String TIPO_TRANSFERENCIA
        +String TIPO_EVENTO
        +String TIPO_PENALIZACION
        +String TIPO_ASIGNACION
        +String SISTEMA
        +setValor(int) void
        +setDescripcion(String) void
        +aLineaTexto() String
        +aFilaCsv() String
    }

    class Novedad {
        -String codigo
        -String nombre
        -String descripcion
        -LocalDate fecha
        -String lugar
        -int valorDuts
        -String condiciones
        +aLineaTexto() String
    }

    class Inscripcion {
        -String codigoNovedad
        -String documentoEstudiante
        -String estado
        -LocalDate fechaInscripcion
        +String ESTADO_INSCRITO
        +String ESTADO_CANCELADA
        +String ESTADO_ASISTIO
        +String ESTADO_NO_ASISTIO
        +setEstado(String) void
        +aLineaTexto() String
    }

    class Notificacion {
        -String documentoDestino
        -LocalDate fecha
        -String mensaje
        +aLineaTexto() String
    }

    class Amistad {
        -String documentoUsuario
        -String documentoAmigo
        +aLineaTexto() String
    }

    class Persistible {
        <<interface>>
        +aLineaTexto() String
    }

    class Exportable {
        <<interface>>
        +aFilaCsv() String
    }

    Usuario <|-- Estudiante
    Usuario <|-- Administrador
    Usuario ..|> Persistible
    Movimiento ..|> Persistible
    Movimiento ..|> Exportable
    Novedad ..|> Persistible
    Inscripcion ..|> Persistible
    Notificacion ..|> Persistible
    Amistad ..|> Persistible

    Estudiante "1" --> "0..*" Movimiento : envía / recibe
    Estudiante "1" --> "0..*" Inscripcion : se inscribe
    Estudiante "1" --> "0..*" Notificacion : recibe
    Estudiante "1" --> "0..*" Amistad : tiene amigos
    Novedad "1" --> "0..*" Inscripcion : tiene inscritos
```

**Pilares de POO aplicados:**
- **Abstracción:** `Usuario` es abstracta y declara `getRol()` y `getResumenPerfil()`.
- **Herencia:** `Estudiante` y `Administrador` extienden `Usuario`.
- **Polimorfismo:** ambos sobrescriben `getRol()`, `getResumenPerfil()` y `aLineaTexto()`.
- **Encapsulamiento:** atributos `private`, setters selectivos.
- **Interfaces:** `Persistible` y `Exportable`.

---

## 2. Diagrama de clases — Interfaces, excepciones y utilidades

```mermaid
classDiagram
    class Persistible {
        <<interface>>
        +aLineaTexto() String
    }

    class Exportable {
        <<interface>>
        +aFilaCsv() String
    }

    class ValidacionException {
        <<exception>>
        +ValidacionException(String mensaje)
    }

    class Exception {
        <<Java API>>
    }

    class Validador {
        <<utility>>
        +validarNoVacio(String, String)$ void
        +validarSinPuntoYComa(String, String)$ void
        +validarEnteroPositivo(String, String)$ int
        +validarFecha(String, String)$ LocalDate
        +validarRangoFechas(LocalDate, LocalDate)$ void
    }

    class GestorTema {
        <<utility>>
        +Color PANTONE_5535_C
        -int temaActual$
        +alternarTema()$ void
        +aplicarTema(Window)$ void
        +esTemaOscuro()$ boolean
        +getColorFondoVentana()$ Color
        +getColorFondoPanel()$ Color
        +getColorTexto()$ Color
        +getColorTextoBoton()$ Color
        +getColorCampoFondo()$ Color
        +getColorBarraCanal()$ Color
        +getColorTextoGrafico()$ Color
        +getColorEjeGrafico()$ Color
    }

    class CalculadoraDuts {
        <<utility>>
        +int META_DUTS = 70000
        +calcularDutsFaltantes(int)$ int
        +calcularPorcentajeMeta(int)$ int
        +calcularSaldoResultante(int, int)$ int
        +tieneSaldoSuficiente(int, int)$ boolean
        +calcularMesesParaMeta(int, int)$ int
        +calcularEventosNecesarios(int, int)$ int
    }

    Exception <|-- ValidacionException
    Validador ..> ValidacionException : lanza
```

**Pilar aplicado:** **Encapsulamiento** — toda la validación vive en `Validador`; toda la lógica de cálculo en `CalculadoraDuts` (la ventana solo la presenta).

---

## 3. Diagrama de clases — Paquete `persistencia`

```mermaid
classDiagram
    class ArchivoTexto {
        <<utility>>
        +leerLineas(String)$ ArrayList~String~
        +escribirLineas(String, ArrayList~String~)$ void
        +agregarLinea(String, String)$ void
    }

    class GestorUsuarios {
        -ArrayList~Usuario~ usuarios
        +registrar(Estudiante) void
        +iniciarSesion(String, String) Usuario
        +buscarPorDocumento(String) Usuario
        +buscarPorNombreUsuario(String) Usuario
        +buscarEstudiantePorDocumento(String) Estudiante
        +listarEstudiantes() ArrayList~Estudiante~
        +restablecerContrasena(String, String, String) void
        +guardar() void
    }

    class GestorMovimientos {
        -ArrayList~Movimiento~ movimientos
        -GestorUsuarios gestorUsuarios
        +int DIAS_SEMANA = 7
        +int DIAS_MES = 30
        +int DIAS_ANIO = 365
        +int DIAS_SEMESTRE = 90
        +transferir(Estudiante, Estudiante, int, String) void
        +asignarDuts(Estudiante, int, String) void
        +registrarMovimiento(String, String, String, int, String) void
        +listarPorUsuario(String) ArrayList~Movimiento~
        +listarAsignaciones() ArrayList~Movimiento~
        +actualizarAsignacion(String, int, String) void
        +eliminarAsignacion(String) void
        +filtrar(String, String, LocalDate, LocalDate) ArrayList~Movimiento~
        +calcularPromedio(String, int) double
        +exportarCsv(String) String
        +exportarComprobante(Movimiento) String
    }

    class GestorNovedades {
        -ArrayList~Novedad~ novedades
        +crear(Novedad) void
        +listar() ArrayList~Novedad~
        +buscarPorCodigo(String) Novedad
        +actualizar(Novedad) void
        +eliminar(String) void
    }

    class GestorInscripciones {
        -ArrayList~Inscripcion~ inscripciones
        -GestorMovimientos gestorMovimientos
        -GestorNotificaciones gestorNotificaciones
        -GestorUsuarios gestorUsuarios
        -GestorNovedades gestorNovedades
        +int PORCENTAJE_PENALIZACION = 20
        +inscribir(String, String) void
        +cancelar(String, String) void
        +marcarAsistencia(String, String) void
        +marcarInasistencia(String, String) void
        +listarPorEstudiante(String) ArrayList~Inscripcion~
        +listarInscritos(String) ArrayList~Inscripcion~
    }

    class GestorNotificaciones {
        -ArrayList~Notificacion~ notificaciones
        +agregar(String, String) void
        +listarPorUsuario(String) ArrayList~Notificacion~
        +generarProximidad(String, ArrayList~Inscripcion~, GestorNovedades) void
    }

    class GestorAmigos {
        -ArrayList~Amistad~ amistades
        -GestorUsuarios gestorUsuarios
        +agregar(String, String) void
        +listarAmigos(String) ArrayList~Amistad~
    }

    class GestorAuditoria {
        <<utility>>
        +registrar(String, String, String)$ void
    }

    ArchivoTexto <.. GestorUsuarios
    ArchivoTexto <.. GestorMovimientos
    ArchivoTexto <.. GestorNovedades
    ArchivoTexto <.. GestorInscripciones
    ArchivoTexto <.. GestorNotificaciones
    ArchivoTexto <.. GestorAmigos
    ArchivoTexto <.. GestorAuditoria

    GestorMovimientos --> GestorUsuarios : recibe
    GestorInscripciones --> GestorMovimientos : recibe
    GestorInscripciones --> GestorNotificaciones : recibe
    GestorInscripciones --> GestorUsuarios : recibe
    GestorInscripciones --> GestorNovedades : recibe
    GestorAmigos --> GestorUsuarios : recibe
```

**Archivos de persistencia:**

| Gestor | Archivo `.txt` |
|---|---|
| `GestorUsuarios` | `datos/usuarios.txt` |
| `GestorMovimientos` | `datos/movimientos.txt` |
| `GestorNovedades` | `datos/novedades.txt` |
| `GestorInscripciones` | `datos/inscripciones.txt` |
| `GestorNotificaciones` | `datos/notificaciones.txt` |
| `GestorAmigos` | `datos/amigos.txt` |
| `GestorAuditoria` | `datos/auditoria.txt` |

---

## 4. Diagrama de clases — Vista: hubs y login

```mermaid
classDiagram
    class JFrame {
        <<Swing>>
    }
    class JDialog {
        <<Swing>>
    }
    class JPanel {
        <<Swing>>
    }
    class JButton {
        <<Swing>>
    }
    class ActionListener {
        <<interface>>
        +actionPerformed(ActionEvent) void
    }

    class Main {
        <<entry point>>
        +main(String[])$ void
    }

    class VentanaInicio {
        -JTextField txtIdentificacion
        -JPasswordField txtContrasena
        -JButton btnIngresar
        -JButton btnRegistrarse
        -JButton btnRecuperar
        -JButton btnSoporte
        -JButton btnSalir
        -BotonTema btnTema
        -PanelFondoLogin panelFondo
        +volverAlInicio() void
        +actionPerformed(ActionEvent) void
    }

    class PanelFondoLogin {
        -Image imagenFondo
        +paintComponent(Graphics) void
    }

    class BotonTema {
        +actionPerformed(ActionEvent) void
        -actualizarEtiqueta() void
    }

    class VentanaRegistro {
        -JTextField txtDocumento
        -JTextField txtNombre
        -JPasswordField txtContrasena
        -JTextField txtPrograma
        -JComboBox~String~ cmbPregunta
        -JTextField txtRespuesta
        +actionPerformed(ActionEvent) void
    }

    class VentanaRecuperacion {
        -JTextField txtUsuario
        -JComboBox~String~ cmbPregunta
        -JTextField txtRespuesta
        -JPasswordField txtNuevaContrasena
        +actionPerformed(ActionEvent) void
    }

    class VentanaSoporte {
        <<JDialog>>
        -JTextArea txtAyuda
        +actionPerformed(ActionEvent) void
    }

    class VentanaMenuEstudiante {
        -Estudiante estudiante
        -VentanaInicio ventanaInicio
        -BotonTema btnTema
        +actionPerformed(ActionEvent) void
    }

    class VentanaMenuAdmin {
        -Administrador administrador
        -VentanaInicio ventanaInicio
        -BotonTema btnTema
        +actionPerformed(ActionEvent) void
    }

    JFrame <|-- VentanaInicio
    JFrame <|-- VentanaRegistro
    JFrame <|-- VentanaRecuperacion
    JFrame <|-- VentanaMenuEstudiante
    JFrame <|-- VentanaMenuAdmin
    JDialog <|-- VentanaSoporte
    JPanel <|-- PanelFondoLogin
    JButton <|-- BotonTema

    ActionListener <|.. VentanaInicio
    ActionListener <|.. BotonTema
    ActionListener <|.. VentanaRegistro
    ActionListener <|.. VentanaRecuperacion
    ActionListener <|.. VentanaSoporte
    ActionListener <|.. VentanaMenuEstudiante
    ActionListener <|.. VentanaMenuAdmin

    Main ..> VentanaInicio : crea
    VentanaInicio *-- PanelFondoLogin
    VentanaInicio *-- BotonTema
    VentanaInicio ..> VentanaRegistro : abre
    VentanaInicio ..> VentanaRecuperacion : abre
    VentanaInicio ..> VentanaSoporte : abre
    VentanaInicio ..> VentanaMenuEstudiante : abre (rol ESTUDIANTE)
    VentanaInicio ..> VentanaMenuAdmin : abre (rol ADMINISTRADOR)
    VentanaMenuEstudiante ..> VentanaInicio : volverAlInicio()
    VentanaMenuAdmin ..> VentanaInicio : volverAlInicio()
```

---

## 5. Diagrama de clases — Vista: módulos del estudiante

```mermaid
classDiagram
    class JFrame {
        <<Swing>>
    }
    class JPanel {
        <<Swing>>
    }

    class VentanaPerfil {
        -Estudiante estudiante
        -JProgressBar barraProgreso
        +VentanaPerfil(Estudiante)
        +actionPerformed(ActionEvent) void
    }

    class VentanaTransferir {
        -Estudiante emisor
        -JTextField txtDocumentoReceptor
        -JTextField txtValor
        -JTextField txtDescripcion
        -GestorMovimientos gestorMovimientos
        -GestorUsuarios gestorUsuarios
        -GestorNotificaciones gestorNotificaciones
        +actionPerformed(ActionEvent) void
    }

    class VentanaHistorial {
        -Estudiante estudiante
        -JTable tabla
        -DefaultTableModel modelo
        -JComboBox~String~ cmbTipo
        -JTextField txtDesde
        -JTextField txtHasta
        -JButton btnExportarCsv
        -JButton btnComprobante
        -GestorMovimientos gestorMovimientos
        +cargarTabla() void
        +actionPerformed(ActionEvent) void
    }

    class VentanaEstadisticas {
        -Estudiante estudiante
        -GestorMovimientos gestorMovimientos
        +cargarPromedios() void
        +actionPerformed(ActionEvent) void
    }

    class VentanaCalculadora {
        -Estudiante estudianteActual
        -boolean esAdmin
        -JComboBox~String~ cmbEstudiantes
        -JTextField txtSaldoA
        -JTextField txtSaldoB
        -JTextField txtTransferenciaB
        -JTextField txtSaldoC
        -JTextField txtDutsMesC
        -JTextField txtDutsEventoC
        -GestorMovimientos gestorMovimientos
        -GestorNovedades gestorNovedades
        -GestorUsuarios gestorUsuarios
        +precargarDatosEstudiante() void
        +actionPerformed(ActionEvent) void
    }

    class VentanaDashboard {
        -Estudiante estudiante
        -PanelGrafico panelGrafico
        -JComboBox~String~ cmbTipo
        -JTextField txtDesde
        -JTextField txtHasta
        -GestorMovimientos gestorMovimientos
        +cargarDatos() void
        +actionPerformed(ActionEvent) void
    }

    class PanelGrafico {
        -int[] ingresos
        -int[] egresos
        +setDatos(int[], int[]) void
        +paintComponent(Graphics) void
    }

    class VentanaNovedades {
        -Estudiante estudiante
        -JTable tablaNovedades
        -JTable tablaInscripciones
        -GestorNovedades gestorNovedades
        -GestorInscripciones gestorInscripciones
        +cargarTablas() void
        +actionPerformed(ActionEvent) void
    }

    class VentanaNotificaciones {
        -Estudiante estudiante
        -JTable tabla
        -GestorNotificaciones gestorNotificaciones
        +cargarTabla() void
        +actionPerformed(ActionEvent) void
    }

    class VentanaAmigos {
        -Estudiante estudiante
        -JTextField txtDocumentoAmigo
        -JTable tabla
        -GestorAmigos gestorAmigos
        -GestorUsuarios gestorUsuarios
        -GestorMovimientos gestorMovimientos
        +cargarTabla() void
        +actionPerformed(ActionEvent) void
    }

    JFrame <|-- VentanaPerfil
    JFrame <|-- VentanaTransferir
    JFrame <|-- VentanaHistorial
    JFrame <|-- VentanaEstadisticas
    JFrame <|-- VentanaCalculadora
    JFrame <|-- VentanaDashboard
    JFrame <|-- VentanaNovedades
    JFrame <|-- VentanaNotificaciones
    JFrame <|-- VentanaAmigos
    JPanel <|-- PanelGrafico

    VentanaDashboard *-- PanelGrafico
    VentanaDashboard --> GestorMovimientos
    VentanaHistorial --> GestorMovimientos
    VentanaTransferir --> GestorMovimientos
    VentanaTransferir --> GestorNotificaciones
    VentanaEstadisticas --> GestorMovimientos
    VentanaNovedades --> GestorNovedades
    VentanaNovedades --> GestorInscripciones
    VentanaNotificaciones --> GestorNotificaciones
    VentanaAmigos --> GestorAmigos
    VentanaAmigos ..> VentanaTransferir : abre prellenada
    VentanaCalculadora --> CalculadoraDuts
    VentanaCalculadora --> GestorUsuarios
```

---

## 6. Diagrama de clases — Vista: módulos del administrador

```mermaid
classDiagram
    class JFrame {
        <<Swing>>
    }

    class VentanaAdminDuts {
        -Administrador administrador
        -JTable tabla
        -DefaultTableModel modelo
        -JTextField txtDocumento
        -JTextField txtValor
        -JTextField txtDescripcion
        -GestorMovimientos gestorMovimientos
        -GestorUsuarios gestorUsuarios
        +cargarTabla() void
        +actionPerformed(ActionEvent) void
    }

    class VentanaAdminNovedades {
        -Administrador administrador
        -JTable tabla
        -JTextField txtNombre
        -JTextField txtFecha
        -JTextField txtLugar
        -JTextField txtValorDuts
        -JTextArea txtDescripcion
        -JTextArea txtCondiciones
        -GestorNovedades gestorNovedades
        +cargarTabla() void
        +leerValorDuts() int
        +actionPerformed(ActionEvent) void
    }

    class VentanaAdminAsistencia {
        -Administrador administrador
        -JComboBox~String~ cmbNovedades
        -JTable tablaInscritos
        -JButton btnAsistio
        -JButton btnNoAsistio
        -GestorNovedades gestorNovedades
        -GestorInscripciones gestorInscripciones
        +cargarInscritos() void
        +actionPerformed(ActionEvent) void
    }

    JFrame <|-- VentanaAdminDuts
    JFrame <|-- VentanaAdminNovedades
    JFrame <|-- VentanaAdminAsistencia

    VentanaAdminDuts --> GestorMovimientos
    VentanaAdminDuts --> GestorUsuarios
    VentanaAdminNovedades --> GestorNovedades
    VentanaAdminAsistencia --> GestorInscripciones
    VentanaAdminAsistencia --> GestorNovedades
```

---

## 7. Diagrama de paquetes

```mermaid
flowchart TD
    subgraph vista["vista (Swing)"]
        V1[VentanaInicio / Menús]
        V2[Módulos Estudiante]
        V3[Módulos Admin]
        V4[PanelGrafico / PanelFondoLogin / BotonTema]
    end

    subgraph persistencia["persistencia"]
        P1[ArchivoTexto]
        P2[GestorUsuarios]
        P3[GestorMovimientos]
        P4[GestorNovedades]
        P5[GestorInscripciones]
        P6[GestorNotificaciones]
        P7[GestorAmigos]
        P8[GestorAuditoria]
    end

    subgraph modelo["modelo"]
        M1[Usuario]
        M2[Estudiante]
        M3[Administrador]
        M4[Movimiento]
        M5[Novedad]
        M6[Inscripcion]
        M7[Notificacion]
        M8[Amistad]
    end

    subgraph util["util"]
        U1[Validador]
        U2[GestorTema]
        U3[CalculadoraDuts]
    end

    subgraph interfaces["interfaces"]
        I1[Persistible]
        I2[Exportable]
    end

    subgraph excepciones["excepciones"]
        E1[ValidacionException]
    end

    vista --> persistencia
    vista --> modelo
    vista --> util
    vista --> excepciones
    persistencia --> modelo
    persistencia --> interfaces
    persistencia --> util
    persistencia --> excepciones
    modelo --> interfaces
    modelo --> excepciones
    util --> excepciones
```

**Regla de dependencia:** las flechas siempre van "hacia abajo" (vista → persistencia → modelo → interfaces). Nunca al revés.

---

## 8. Diagrama de dependencias entre gestores (creación en `Main`)

```mermaid
flowchart LR
    MAIN([Main.main]):::entry

    GU[GestorUsuarios]:::gestor
    GM[GestorMovimientos]:::gestor
    GNV[GestorNovedades]:::gestor
    GN[GestorNotificaciones]:::gestor
    GI[GestorInscripciones]:::gestor
    GA[GestorAmigos]:::gestor
    AUD[GestorAuditoria<br/>static]:::util
    AT[ArchivoTexto<br/>static]:::util

    MAIN --> GU
    MAIN --> GM
    MAIN --> GNV
    MAIN --> GN
    MAIN --> GI
    MAIN --> GA

    GM -.->|"inyecta"| GU
    GI -.->|"inyecta"| GM
    GI -.->|"inyecta"| GN
    GI -.->|"inyecta"| GU
    GI -.->|"inyecta"| GNV
    GA -.->|"inyecta"| GU

    GU -.-> AT
    GM -.-> AT
    GNV -.-> AT
    GN -.-> AT
    GI -.-> AT
    GA -.-> AT
    AUD -.-> AT

    classDef entry fill:#132F20,color:#fff,stroke:#132F20
    classDef gestor fill:#E3F2FD,stroke:#132F20
    classDef util fill:#FFF9C4,stroke:#132F20
```

**Orden de creación en `Main` (por dependencias):**
1. `GestorUsuarios`
2. `GestorMovimientos(GestorUsuarios)`
3. `GestorNovedades`
4. `GestorNotificaciones`
5. `GestorInscripciones(GestorMovimientos, GestorNotificaciones, GestorUsuarios, GestorNovedades)`
6. `GestorAmigos(GestorUsuarios)`

Cada gestor carga su archivo `.txt` en el constructor.

---

## 9. Diagrama de navegación (flujo entre ventanas)

```mermaid
flowchart TD
    INI([VentanaInicio<br/>Login / Registro / Recuperación / Soporte]):::hub

    REG[VentanaRegistro]:::win
    REC[VentanaRecuperacion]:::win
    SOP[VentanaSoporte<br/>JDialog]:::win

    ME[VentanaMenuEstudiante]:::hub
    MA[VentanaMenuAdmin]:::hub

    subgraph est["Módulos del estudiante"]
        PER[VentanaPerfil]:::win
        TRA[VentanaTransferir]:::win
        HIS[VentanaHistorial]:::win
        EST[VentanaEstadisticas]:::win
        CAL[VentanaCalculadora]:::win
        DAS[VentanaDashboard]:::win
        NOV[VentanaNovedades]:::win
        NOT[VentanaNotificaciones]:::win
        AMI[VentanaAmigos]:::win
    end

    subgraph adm["Módulos del administrador"]
        ADU[VentanaAdminDuts]:::win
        ANO[VentanaAdminNovedades]:::win
        AAS[VentanaAdminAsistencia]:::win
    end

    INI -->|Registrarse| REG
    INI -->|Recuperar| REC
    INI -->|Soporte| SOP
    REG -->|Cancelar / Registrar| INI
    REC -->|Cambiar / Cancelar| INI
    SOP -->|Cerrar| INI

    INI -->|Login rol ESTUDIANTE| ME
    INI -->|Login rol ADMINISTRADOR| MA

    ME --> PER
    ME --> TRA
    ME --> HIS
    ME --> EST
    ME --> CAL
    ME --> DAS
    ME --> NOV
    ME --> NOT
    ME --> AMI

    MA --> ADU
    MA --> ANO
    MA --> AAS

    AMI -->|abre prellenada| TRA

    ME -->|Cerrar sesión| INI
    MA -->|Cerrar sesión| INI

    INI -->|Salir confirmado| FIN([System.exit]):::exit

    classDef hub fill:#132F20,color:#fff,stroke:#132F20
    classDef win fill:#F3E5F5,stroke:#132F20
    classDef exit fill:#FFEBEE,stroke:#B71C1C
```

---

## 10. Resumen de la arquitectura por capas

```mermaid
flowchart TB
    subgraph presentacion["Capa de presentación — vista/"]
        P[Ventanas Swing<br/>JFrame / JDialog<br/>ActionListener]
    end
    subgraph datos["Capa de datos — persistencia/"]
        D[Gestores<br/>Listas en memoria + .txt]
    end
    subgraph dominio["Capa de dominio — modelo/"]
        M[Clases modelo<br/>Usuario, Estudiante, Movimiento, ...]
    end
    subgraph soporte["Soporte"]
        U[util/<br/>Validador, GestorTema, CalculadoraDuts]
        I[interfaces/<br/>Persistible, Exportable]
        E[excepciones/<br/>ValidacionException]
    end

    P --> D
    P --> M
    P --> U
    P --> E
    D --> M
    D --> I
    D --> U
    D --> E
    M --> I
    M --> E
    U --> E
```

---

## Ver también

- Versión **PlantUML** con más detalle: `documentacion/diagramas/*.puml`
- Índice y guía de renderizado: `documentacion/diagramas/README.md`
- Reglas del proyecto: `AGENTS.md`
- Plan de implementación: `plan_implementacion.md`
