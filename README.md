# DUTS Wallet

**Plataforma institucional de las Unidades Tecnológicas de Santander (UTS) para el manejo de la moneda virtual DUTS**

---

## 📋 Descripción

DUTS Wallet es un prototipo funcional de escritorio desarrollado en **Java con interfaz gráfica Swing** que permite a los estudiantes de la UTS administrar su moneda virtual institucional llamada **DUTS** y hacer seguimiento a la **meta de 70.000 DUTS** exigida para graduarse.

**Para el estudiante:**
- 💰 Consultar saldo y avance hacia la meta de grado
- 💸 Transferir DUTS a otros estudiantes usando su número de documento
- 📊 Ver el historial de movimientos con filtros por tipo y fecha
- 📈 Consultar promedios de DUTS por semana, mes, año y cuatrimestre
- 📉 Visualizar un dashboard interactivo de los últimos 60 días
- 🧮 Usar la calculadora de DUTS (faltantes para la meta, simulador de transferencia y proyección)
- 🎓 Inscribirse en novedades institucionales (eventos que otorgan DUTS)
- 🔔 Recibir notificaciones automáticas (transferencias, inscripciones, proximidad de eventos)
- 👥 Agregar amigos, ver su avance y hacerles transferencias rápidas
- 📄 Exportar el historial a CSV y generar comprobantes de transferencia
- 🛡️ Recuperar la contraseña con una pregunta de seguridad
- 🌗 Cambiar entre modo claro y modo oscuro

**Para el administrador:**
- ➕ Asignar, actualizar y eliminar DUTS de los estudiantes
- 📝 Crear, consultar, actualizar y eliminar novedades institucionales
- ✅ Marcar asistencia o inasistencia a eventos (con penalización del 20 % por inasistencia)

---

## 🎯 Características técnicas

- **Lenguaje:** Java, con sintaxis compatible con Java 8
- **Interfaz:** Swing (`JFrame`, `JDialog`, `JPanel`, `JTable`, `JOptionPane`, etc.)
- **Persistencia:** archivos de texto plano (`.txt`) con separador `;`
- **Sin dependencias externas:** solo el SDK estándar de Java
- **Arquitectura:** orientada a objetos, con aplicación directa de los 5 pilares de POO
- **Validaciones centralizadas:** clase `Validador` con métodos reutilizables
- **Tema visual:** modo claro y oscuro global, con el color institucional Pantone 5535 C (`#132F20`)
- **Auditoría oculta:** registro de las operaciones importantes en `datos/auditoria.txt`

---

## 🧰 Requisitos de ejecución

- **Java:** JDK 8 o superior (el proyecto se desarrolló y probó con JDK 21, compilando con sintaxis de Java 8). Se necesita el JDK completo, no solo el JRE, para poder compilar el código fuente.
- **Sistema operativo:** Windows, Linux o macOS con entorno gráfico, porque la aplicación usa ventanas de escritorio y no funciona en terminales sin pantalla.
- **Dependencias:** ninguna. No requiere instalar librerías, bases de datos, servidores ni conexión a internet.
- **Permisos de escritura:** la carpeta del proyecto debe permitir crear archivos, ya que la aplicación genera `datos/`, `reportes/` y `comprobantes/` por su cuenta.
- **Imagen de fondo:** el archivo `recursos/Fondo_login.jpg` debe conservarse en esa ruta. Si falta, la aplicación avisa y muestra un fondo de color sólido.

---

## 📦 Estructura del proyecto

```
Duts_Wallet/
├── src/
│   ├── interfaces/         # Persistible, Exportable
│   ├── excepciones/        # ValidacionException
│   ├── util/               # Validador, GestorTema, CalculadoraDuts
│   ├── modelo/             # Usuario, Estudiante, Administrador, Movimiento,
│   │                       # Novedad, Inscripcion, Notificacion, Amistad
│   ├── persistencia/       # ArchivoTexto y los gestores de cada archivo (7)
│   └── vista/              # Interfaz gráfica (Main y 21 clases de Swing):
│                           #   · Acceso: inicio, registro, recuperación y soporte
│                           #   · Estudiante: menú, perfil, transferencias, historial,
│                           #     estadísticas, dashboard, novedades, notificaciones,
│                           #     amigos y calculadora
│                           #   · Administrador: menú, DUTS, novedades y asistencia
│                           #   · Componentes: botón de tema, fondo del login y gráfico
├── datos/                  # Archivos .txt (se crean al ejecutar)
├── reportes/               # CSV exportados
├── comprobantes/           # Comprobantes de transferencia
├── recursos/               # Imágenes de la aplicación (Fondo_login.jpg)
└── README.md               # Este archivo
```

---

## 🔐 Acceso

**Administrador:**
- Usuario: `admin`
- Contraseña: `admin123`

**Estudiantes:** se registran desde la ventana de inicio con el botón "Registrarse" e ingresan con su **número de documento** y su contraseña.

---

## 📚 Pilares de POO aplicados

El proyecto fue diseñado con fines académicos para demostrar los 5 pilares de la Programación Orientada a Objetos:

| Pilar | Aplicación en el proyecto |
|---|---|
| **Encapsulamiento** | Todos los atributos son `private` con acceso por getters/setters. Las validaciones están centralizadas en la clase `Validador`. |
| **Abstracción** | `Usuario` es una clase abstracta con métodos abstractos `getRol()` y `getResumenPerfil()` implementados por `Estudiante` y `Administrador`. |
| **Herencia** | `Estudiante` y `Administrador` heredan de `Usuario`. Todas las ventanas heredan de `JFrame` o `JDialog`. |
| **Polimorfismo** | En `VentanaInicio`, al iniciar sesión, `usuario.getRol()` se ejecuta según el tipo real (`Estudiante` o `Administrador`) y se abre el menú correspondiente. |
| **Interfaces** | `Persistible` define `aLineaTexto()` para guardar en archivos. `Exportable` define `aFilaCsv()` para exportar. |

---

## ✅ Funcionalidades

### Requisitos base
1. Aplicación gráfica con Swing/JFrame
2. Menú inicial con ingreso, registro, recuperación de contraseña y soporte
3. Registro de estudiantes con validaciones
4. Inicio de sesión con validación de credenciales (estudiantes por **documento**; administrador por usuario)
5. Consulta de perfil (solo lectura)
6. CRUD de asignaciones de DUTS (administrador)
7. Consulta del saldo actual de DUTS
8. Promedios de DUTS por semana, mes, año y cuatrimestre (90 días)
9. Transferencia de DUTS entre estudiantes
10. Validación de saldo suficiente
11. Consulta de novedades institucionales
12. Inscripción en novedades (fecha igual o posterior a hoy)
13. Cancelación de inscripciones
14. CRUD de novedades (administrador)
15. Validaciones y mensajes claros
16. Historial filtrable por tipo y fechas

### Funcionalidades adicionales
17. Auditoría local oculta de todas las operaciones
18. Exportación a CSV con promedios
19. Barra de progreso hacia la meta de 70.000 DUTS
20. Dashboard con gráfico de barras (últimos 60 días en 6 bloques de 10)
21. Penalización del 20 % por inasistencia
22. Bandeja de notificaciones (transferencias, inscripciones, proximidad de eventos)
23. Generador de comprobantes de transferencia
24. Sistema de amigos con transferencia rápida
25. Recuperación de contraseña con pregunta de seguridad
26. Módulo de soporte con instrucciones y preguntas frecuentes

### Mejoras de interfaz y sesión
27. Tema claro/oscuro global (botón en los menús principales; las ventanas hijas heredan el tema)
28. Calculadora de DUTS con módulos A (meta), B (simulador de transferencia) y C (proyección)
29. Color institucional Pantone 5535 C en botones, títulos y barras de progreso
30. Pantalla de inicio con imagen de fondo (1280×720) y formulario a la izquierda
31. Texto del inicio siempre visible en modo oscuro
32. Registro sin alias: el estudiante se identifica solo por su documento
33. "Cerrar sesión" devuelve a la ventana principal con los campos limpios
34. Botón "Salir de la aplicación" con confirmación
35. Sin pistas de acceso visibles en la interfaz

---

## 📁 Archivos generados

### `datos/` (persistencia)
- `usuarios.txt` — Estudiantes y administradores
- `movimientos.txt` — Transferencias, asignaciones, eventos y penalizaciones
- `novedades.txt` — Eventos institucionales
- `inscripciones.txt` — Inscripciones de estudiantes en novedades
- `notificaciones.txt` — Notificaciones de cada usuario
- `amigos.txt` — Relaciones de amistad
- `auditoria.txt` — Registro oculto de operaciones (fecha y hora, usuario, acción y detalle)

### `reportes/` (exportaciones)
- `historial_<documento>.csv` — Historial de movimientos de un estudiante con sus promedios

### `comprobantes/`
- `comprobante_<codigo>.txt` — Comprobante de una transferencia enviada

---

## 👨‍🎓 Contexto académico

- **Asignatura:** Programación Orientada a Objetos (POO)
- **Institución:** Unidades Tecnológicas de Santander (UTS)
- **Nivel:** 3er semestre
- **Objetivo:** Demostrar dominio de los pilares de POO en un proyecto funcional con persistencia local

---

## 📝 Licencia

Este proyecto es un prototipo académico desarrollado para fines educativos.

---

**Desarrollado con ❤️ para las Unidades Tecnológicas de Santander (UTS)**
