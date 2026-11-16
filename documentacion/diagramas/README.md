# Diagramas UML — DUTS Wallet

Diagramas de clases UML que documentan la arquitectura completa del proyecto **DUTS Wallet**, elaborados a partir de `AGENTS.md` y `plan_implementacion.md`.

Los diagramas están en formato **PlantUML** (`.puml`). Son texto plano que se puede versionar y renderizar con cualquier herramienta compatible.

---

## Índice de diagramas

| # | Archivo fuente | Imagen generada | Resolución | Contenido | Cuándo usarlo |
|---|---|---|---|---|---|
| 1 | `1_diagrama_modelo.puml` | `1_diagrama_modelo.png` | 2786×1484 | Paquete `modelo`: `Usuario`, `Estudiante`, `Administrador`, `Movimiento`, `Novedad`, `Inscripcion`, `Notificacion`, `Amistad` con todos sus atributos, métodos y relaciones. | Explicar **herencia**, **abstracción**, **polimorfismo** y **encapsulamiento**. |
| 2 | `2_diagrama_interfaces_util.puml` | `2_diagrama_interfaces_util.png` | 2836×589 | Interfaces `Persistible`/`Exportable`, `ValidacionException` y utilidades `Validador`, `GestorTema`, `CalculadoraDuts`. | Explicar **interfaces** y la capa de utilidades. |
| 3 | `3_diagrama_persistencia.puml` | `3_diagrama_persistencia.png` | 2092×1880 | `ArchivoTexto`, todos los `Gestor...` y sus dependencias por constructor. | Explicar la **capa de datos** y la persistencia en `.txt`. |
| 4 | `4_diagrama_vista_hubs.puml` | `4_diagrama_vista_hubs.png` | 2234×908 | `Main`, `VentanaInicio`, `PanelFondoLogin`, `BotonTema`, `VentanaRegistro`, `VentanaRecuperacion`, `VentanaSoporte`, `VentanaMenuEstudiante`, `VentanaMenuAdmin`. | Explicar el **arranque**, el **login** y la **navegación principal**. |
| 5 | `5_diagrama_vista_modulos_estudiante.puml` | `5_diagrama_vista_modulos_estudiante.png` | 1230×1790 | Perfil, Transferir, Historial, Estadísticas, Calculadora, Dashboard, Novedades, Notificaciones, Amigos, `PanelGrafico`. | Explicar los **módulos del estudiante**. |
| 6 | `6_diagrama_vista_modulos_admin.puml` | `6_diagrama_vista_modulos_admin.png` | 1487×508 | `VentanaAdminDuts`, `VentanaAdminNovedades`, `VentanaAdminAsistencia`. | Explicar los **módulos del administrador**. |
| 7 | `7_diagrama_paquetes.puml` | `7_diagrama_paquetes.png` | 2942×1042 | Organización en paquetes y dependencias entre capas. | Explicar la **arquitectura general** y el flujo de dependencias. |
| 8 | `8_diagrama_dependencias_gestores.puml` | `8_diagrama_dependencias_gestores.png` | 1915×1082 | Cómo `Main` crea los gestores y cómo se inyectan entre sí. | Explicar la **inyección por constructor** y el orden de creación. |
| 9 | `9_diagrama_arquitectura_completa.puml` | `9_diagrama_arquitectura_completa.png` | 1598×1122 | Visión integral: todas las capas y relaciones en un solo diagrama (versión simplificada por capas). | Vista **de conjunto** para la sustentación. |
| 10 | `10_diagrama_navegacion.puml` | `10_diagrama_navegacion.png` | 2561×717 | Flujo de navegación entre ventanas (estados). | Explicar cómo se **navega** entre pantallas. |

---

## Cómo renderizar los diagramas

### Opción A — VS Code (recomendada)
1. Instalar la extensión **PlantUML** (`jebbs.plantuml`).
2. Abrir cualquier `.puml` y presionar `Alt + D` para la vista previa.
3. `Ctrl + Shift + P` → *PlantUML: Export Current Diagram* para exportar a PNG/SVG.

### Opción B — Línea de comandos (requiere Java y `plantuml.jar`)
```powershell
java -jar plantuml.jar -tpng documentacion/diagramas/*.puml
```
Genera un `.png` por cada `.puml` en la misma carpeta.

### Opción C — En línea
Copiar el contenido del archivo en <https://www.plantuml.com/plantuml/uml/>.

### Opción D — IntelliJ IDEA
Instalar el plugin **PlantUML Integration** y abrir el archivo.

---

## Convenciones usadas en los diagramas

| Símbolo | Significado |
|---|---|
| `- atributo: Tipo` | Atributo privado (encapsulamiento) |
| `+ metodo(): Tipo` | Método público |
| `{static}` | Método o constante estática |
| `{abstract}` | Método abstracto (sin implementación) |
| `<<abstract>>` | Clase abstracta |
| `<<interface>>` | Interfaz |
| `<<static>>` | Clase de utilidades (solo `static`) |
| `<|--` | Herencia (extends) |
| `..|>` | Implementación de interfaz (implements) |
| `.->` | Dependencia (uso) |
| `*--` | Composición (parte de) |
| `o--` | Agregación (tiene) |

---

## Pilares de POO y dónde verlos

| Pilar | Clases / Relaciones | Diagrama |
|---|---|---|
| **Abstracción** | `Usuario` (abstracta) declara `getRol()` y `getResumenPerfil()` | 1, 9 |
| **Herencia** | `Estudiante` y `Administrador` extienden `Usuario` | 1, 9 |
| **Polimorfismo** | `getRol()`, `getResumenPerfil()`, `aLineaTexto()` sobrescritos | 1, 9 |
| **Encapsulamiento** | Todos los atributos `private`, setters selectivos | 1, 9 |
| **Interfaces** | `Persistible` (todas las clases persistibles) y `Exportable` (`Movimiento`) | 1, 2, 9 |
| **Objetos / instanciación** | `Main` crea los gestores con `new`; los gestores crean modelos al leer archivos | 3, 8 |

---

## Resumen de la arquitectura por capas

```
vista/         → Presentación (Swing: JFrame/JDialog, ActionListener)
persistencia/  → Datos (Gestores + ArchivoTexto, listas en memoria + .txt)
modelo/        → Dominio (Usuario, Estudiante, Movimiento, ...)
util/          → Utilidades (Validador, GestorTema, CalculadoraDuts)
interfaces/    → Contratos (Persistible, Exportable)
excepciones/   → ValidacionException
```

**Regla de dependencia:** la `vista` conoce `persistencia`, `modelo`, `util` y `excepciones`; la `persistencia` conoce `modelo`, `interfaces`, `util` y `excepciones`; el `modelo` conoce `interfaces` y `excepciones`. Nunca al revés (no hay MVC formal ni patrones de diseño, tal como exige `AGENTS.md` §2.2.3).
