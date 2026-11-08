# DUTS Wallet

**Plataforma institucional de las Unidades Tecnológicas de Santander (UTS) para el manejo de la moneda virtual DUTS**

---

## 📋 Descripción

DUTS Wallet es un prototipo funcional de escritorio desarrollado en **Java con interfaz gráfica Swing (`JFrame`)** que permite a los estudiantes de la UTS administrar su moneda virtual institucional llamada **DUTS**. El sistema permite:

- 💰 Consultar saldo y hacer seguimiento a la **meta de 70.000 DUTS** para graduarse
- 💸 Transferir DUTS entre estudiantes
- 📊 Ver historial de movimientos con filtros por tipo y fecha
- 📈 Consultar estadísticas y promedios (por semana, mes, año y semestre)
- 📉 Visualizar un dashboard interactivo de los últimos 60 días
- 🎓 Inscribirse en novedades institucionales (eventos que otorgan DUTS)
- 🔔 Recibir notificaciones automáticas (transferencias, inscripciones, proximidad de eventos)
- 👥 Administrar amigos y hacer transferencias rápidas
- 📄 Exportar historial a CSV y generar comprobantes de transferencia
- 🛡️ Recuperar contraseña con pregunta de seguridad

**Panel de administración:**
- ➕ Asignar, actualizar y eliminar DUTS de estudiantes
- 📝 CRUD completo de novedades institucionales
- ✅ Marcar asistencia o inasistencia de estudiantes (con penalización del 20% por inasistencia)

---

## 🎯 Características técnicas

- **Lenguaje:** Java (sintaxis compatible con Java 8, compilado con JDK 17+)
- **Interfaz:** Swing (`JFrame`, `JDialog`, `JPanel`, `JTable`, `JOptionPane`, etc.)
- **Persistencia:** Archivos de texto plano (`.txt`) con separador `;`
- **Sin dependencias externas:** solo el SDK estándar de Java
- **Arquitectura:** orientada a objetos con aplicación directa de los 5 pilares de POO
- **Validaciones centralizadas:** clase `Validador` con métodos reutilizables
- **Auditoría oculta:** registro de todas las operaciones importantes en `datos/auditoria.txt`

---

## 📦 Estructura del proyecto

```
Duts_Wallet/
├── src/
│   ├── interfaces/         # Persistible, Exportable
│   ├── excepciones/        # ValidacionException
│   ├── util/               # Validador
│   ├── modelo/             # Usuario, Estudiante, Administrador, Movimiento, 
│   │                       # Novedad, Inscripcion, Notificacion, Amistad
│   ├── persistencia/       # ArchivoTexto, Gestor... (8 clases)
│   └── vista/              # 19 ventanas + Main
├── datos/                  # Archivos .txt (se crean al ejecutar)
├── reportes/               # CSV exportados
├── comprobantes/           # Comprobantes de transferencia
├── AGENTS.md               # Especificación completa para agentes de IA
├── plan_implementacion.md  # Plan de desarrollo iterativo (39 tareas)
└── README.md               # Este archivo
```

---

## 🚀 Cómo ejecutar

### Requisitos previos
- **JDK 8 o superior** instalado (probado con JDK 17 y JDK 21)
- Terminal (PowerShell en Windows o Bash en Linux/Mac)

### Compilación y ejecución

**Desde la raíz del proyecto:**

```powershell
# Compilar todas las clases
javac --release 8 -encoding UTF-8 -d out (Get-ChildItem -Recurse src -Filter *.java).FullName

# Ejecutar la aplicación
java -cp out vista.Main
```

**En Linux/Mac:**

```bash
# Compilar todas las clases
javac --release 8 -encoding UTF-8 -d out $(find src -name "*.java")

# Ejecutar la aplicación
java -cp out vista.Main
```

---

## 🔐 Credenciales por defecto

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin123`

**Estudiantes:** se registran desde la ventana de inicio con el botón "Registrarse".

---

## 📚 Pilares de POO aplicados

El proyecto fue diseñado con fines académicos para demostrar los 5 pilares de la Programación Orientada a Objetos:

| Pilar | Aplicación en el proyecto |
|---|---|
| **Encapsulamiento** | Todos los atributos son `private` con acceso por getters/setters. Las validaciones están centralizadas en la clase `Validador`. |
| **Abstracción** | `Usuario` es una clase abstracta con métodos abstractos `getRol()` y `getResumenPerfil()` implementados por `Estudiante` y `Administrador`. |
| **Herencia** | `Estudiante` y `Administrador` heredan de `Usuario`. Todas las ventanas heredan de `JFrame` o `JDialog`. |
| **Polimorfismo** | En `VentanaInicio`, al hacer login, `usuario.getRol()` se ejecuta según el tipo real (`Estudiante` o `Administrador`) y se abre el menú correspondiente. |
| **Interfaces** | `Persistible` define `aLineaTexto()` para guardar en archivos. `Exportable` define `aFilaCsv()` para exportar. |

---

## 📋 Funcionalidades completas (26 requisitos)

### Base (16 requisitos)
1. ✅ Aplicación gráfica con Swing/JFrame
2. ✅ Menú inicial con login, registro, recuperación y soporte
3. ✅ Registro de estudiantes con validaciones
4. ✅ Inicio de sesión con validación de credenciales
5. ✅ Consulta de perfil (solo lectura)
6. ✅ CRUD de asignaciones de DUTS (administrador)
7. ✅ Consultar saldo actual de DUTS
8. ✅ Promedios de DUTS (semana, mes, año, semestre)
9. ✅ Transferir DUTS entre estudiantes
10. ✅ Validación de saldo suficiente
11. ✅ Consultar novedades institucionales
12. ✅ Inscribirse en novedades (fecha >= hoy)
13. ✅ Cancelar inscripción
14. ✅ CRUD de novedades (administrador)
15. ✅ Validaciones y mensajes claros
16. ✅ Historial filtrable por tipo y fechas

### Adicionales (10 requisitos)
17. ✅ Auditoría local oculta de todas las operaciones
18. ✅ Exportación a CSV con promedios
19. ✅ Barra de progreso hacia la meta de 70.000 DUTS
20. ✅ Dashboard con gráfico de barras (últimos 60 días en 6 bloques de 10)
21. ✅ Penalización del 20% por inasistencia
22. ✅ Bandeja de notificaciones (transferencias, inscripciones, proximidad de eventos)
23. ✅ Generador de comprobantes de transferencia
24. ✅ Sistema de amigos con transferencia rápida
25. ✅ Recuperación de contraseña con pregunta de seguridad
26. ✅ Módulo de soporte con instrucciones y FAQ

---

## 📁 Archivos generados

### `datos/` (persistencia)
- `usuarios.txt` — Estudiantes y administradores
- `movimientos.txt` — Transferencias, asignaciones, eventos, penalizaciones
- `novedades.txt` — Eventos institucionales
- `inscripciones.txt` — Inscripciones de estudiantes en novedades
- `notificaciones.txt` — Notificaciones de cada usuario
- `amigos.txt` — Relaciones de amistad
- `auditoria.txt` — Log oculto de operaciones (con fecha, hora, usuario, acción, detalle)

### `reportes/` (exportaciones)
- `historial_<documento>.csv` — Historial de movimientos de un estudiante con promedios

### `comprobantes/` (comprobantes)
- `comprobante_<codigo>.txt` — Comprobante de una transferencia enviada

---

## 🛠️ Restricciones técnicas

El proyecto fue desarrollado siguiendo estrictas restricciones académicas:

**Prohibido:**
- ❌ Streams, lambdas, referencias a métodos
- ❌ Maven, Gradle, librerías externas
- ❌ Patrones de diseño (Singleton, Factory, MVC formal, etc.)
- ❌ Reflexión, hilos, concurrencia
- ❌ `enum`, `record`, `var`, `Optional`, `interface` con métodos `default`
- ❌ Bases de datos, JDBC, HTTP, JSON, XML
- ❌ JavaFX, diseñadores visuales que generen `GroupLayout`
- ❌ Cifrado o hash de contraseñas

**Permitido:**
- ✅ Solo SDK estándar: `java.io`, `java.util` (ArrayList, List), `java.time` (LocalDate, LocalDateTime), `javax.swing`, `java.awt`
- ✅ Ciclos `for`, `for-each`, `while`, condicionales `if-else`, `switch` clásico
- ✅ Eventos con `implements ActionListener` y método `actionPerformed` único por ventana
- ✅ Layouts: `BorderLayout`, `FlowLayout`, `GridLayout`, `setLayout(null)` con `setBounds`

---

## 📖 Documentación completa

- **`AGENTS.md`**: especificación completa del proyecto (arquitectura, restricciones, reglas de estilo, flujo de trabajo para agentes de IA)
- **`plan_implementacion.md`**: plan de desarrollo iterativo con las 39 tareas (T1–T39) organizadas en 6 fases

---

## 👨‍🎓 Contexto académico

**Proyecto desarrollado para:**
- **Asignatura:** Programación Orientada a Objetos (POO)
- **Institución:** Unidades Tecnológicas de Santander (UTS)
- **Nivel:** 3er semestre
- **Objetivo:** Demostrar dominio de los pilares de POO en un proyecto funcional con persistencia local

---

## ✅ Estado del proyecto

**🎉 Proyecto completo y listo para probar (100% funcional)**

- ✅ 39 clases implementadas
- ✅ Compilación exitosa con Java 8
- ✅ Todas las restricciones técnicas respetadas
- ✅ 26 requisitos funcionales cubiertos
- ✅ Pilares de POO aplicados y documentados
- ✅ Código comentado para sustentación

---

## 📝 Licencia

Este proyecto es un prototipo académico desarrollado para fines educativos.

---

**Desarrollado con ❤️ para las Unidades Tecnológicas de Santander (UTS)**
