// Programa de prueba de la Fase 7: valida CalculadoraDuts, GestorTema y los gestores de persistencia.
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Estudiante;
import modelo.Movimiento;
import modelo.Novedad;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.CalculadoraDuts;
import util.GestorTema;

public class PruebaLogicaFase7 {

    private static int totalPruebas = 0;
    private static int pruebasExitosas = 0;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS DE LOGICA FASE 7 ===");
        System.out.println("");

        System.out.println("[MODULO 1] CalculadoraDuts (T42)");
        pruebaCalculadoraDuts();

        System.out.println("");
        System.out.println("[MODULO 2] GestorTema (T40)");
        pruebaGestorTema();

        System.out.println("");
        System.out.println("[MODULO 3] Gestores de persistencia");
        pruebaGestores();

        System.out.println("");
        System.out.println("=== RESUMEN ===");
        System.out.println("Pruebas ejecutadas: " + totalPruebas);
        System.out.println("Pruebas exitosas:  " + pruebasExitosas);
        System.out.println("Pruebas fallidas:  " + (totalPruebas - pruebasExitosas));
        if (pruebasExitosas == totalPruebas) {
            System.out.println("RESULTADO: TODAS LAS PRUEBAS PASARON");
        } else {
            System.out.println("RESULTADO: HAY PRUEBAS FALLIDAS");
        }
    }

    private static void pruebaCalculadoraDuts() {
        prueba("Faltantes con saldo 50000 = 20000", 20000, CalculadoraDuts.calcularDutsFaltantes(50000));
        prueba("Faltantes con saldo 70000 = 0", 0, CalculadoraDuts.calcularDutsFaltantes(70000));
        prueba("Faltantes con saldo 0 = 70000", 70000, CalculadoraDuts.calcularDutsFaltantes(0));
        prueba("Porcentaje con saldo 35000 = 50", 50, CalculadoraDuts.calcularPorcentajeMeta(35000));
        prueba("Porcentaje con saldo 70000 = 100", 100, CalculadoraDuts.calcularPorcentajeMeta(70000));
        prueba("Porcentaje con saldo 0 = 0", 0, CalculadoraDuts.calcularPorcentajeMeta(0));
        prueba("Saldo resultante 100-30 = 70", 70, CalculadoraDuts.calcularSaldoResultante(100, 30));
        prueba("Saldo suficiente 100>=50", true, CalculadoraDuts.tieneSaldoSuficiente(100, 50));
        prueba("Saldo insuficiente 30<50", false, CalculadoraDuts.tieneSaldoSuficiente(30, 50));
        prueba("Meses para meta 50000 con 10000/mes = 2", 2, CalculadoraDuts.calcularMesesParaMeta(50000, 10000));
        prueba("Meses para meta con 0 por mes = 0", 0, CalculadoraDuts.calcularMesesParaMeta(50000, 0));
        prueba("Eventos para meta 50000 con 5000 = 4", 4, CalculadoraDuts.calcularEventosNecesarios(50000, 5000));
        prueba("Eventos para meta con 0 por evento = 0", 0, CalculadoraDuts.calcularEventosNecesarios(50000, 0));
    }

    private static void pruebaGestorTema() {
        pruebaLogica("GestorTema inicia en modo claro", !GestorTema.esTemaOscuro());
        int r = GestorTema.PANTONE_5535_C.getRed();
        int g = GestorTema.PANTONE_5535_C.getGreen();
        int b = GestorTema.PANTONE_5535_C.getBlue();
        pruebaLogica("Pantone 5535 C = #132F20", r == 19 && g == 47 && b == 32);

        java.awt.Color claro = GestorTema.getColorTexto();
        GestorTema.alternarTema();
        boolean oscuroAhora = GestorTema.esTemaOscuro();
        java.awt.Color oscuro = GestorTema.getColorTexto();
        GestorTema.alternarTema();
        pruebaLogica("alternarTema cambia a oscuro y vuelve a claro", oscuroAhora && !GestorTema.esTemaOscuro());
        pruebaLogica("Color de texto cambia con el tema", !claro.equals(oscuro));
    }

    private static void pruebaGestores() {
        // Se usa un directorio de trabajo temporal: los gestores usan rutas relativas a "datos/"
        String dirTrabajo = System.getProperty("java.io.tmpdir") + "/duts_prueba_fase7";
        File carpeta = new File(dirTrabajo);
        carpeta.mkdirs();

        // Se limpian los datos de corridas anteriores para que la prueba sea re-ejecutable
        // (los gestores escriben en "datos/", "reportes/" y "comprobantes/" del directorio actual)
        limpiarCarpeta(new File("datos"));
        limpiarCarpeta(new File("reportes"));
        limpiarCarpeta(new File("comprobantes"));

        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorMovimientos gestorMovimientos = new GestorMovimientos(gestorUsuarios);
        GestorNovedades gestorNovedades = new GestorNovedades();
        GestorNotificaciones gestorNotificaciones = new GestorNotificaciones();
        GestorInscripciones gestorInscripciones = new GestorInscripciones(gestorMovimientos,
                gestorNotificaciones, gestorUsuarios, gestorNovedades);
        GestorAmigos gestorAmigos = new GestorAmigos(gestorUsuarios);

        // Admin por defecto
        try {
            modelo.Usuario admin = gestorUsuarios.iniciarSesion("admin", "admin123");
            pruebaLogica("Login admin/admin123 funciona y es ADMINISTRADOR", admin.getRol().equals("ADMINISTRADOR"));
        } catch (Exception e) {
            pruebaLogica("Login admin/admin123 funciona y es ADMINISTRADOR", false);
        }

        // Credenciales incorrectas
        try {
            gestorUsuarios.iniciarSesion("admin", "malaClave");
            pruebaLogica("Credenciales incorrectas muestran mensaje claro", false);
        } catch (Exception e) {
            pruebaLogica("Credenciales incorrectas muestran mensaje claro", e.getMessage().contains("Credenciales"));
        }

        // Registro de estudiante
        Estudiante estudiante = new Estudiante("1094938271", "Ana Maria Lopez", "analopez",
                "Clave123", "Color favorita", "Azul", "Ingenieria de Sistemas", 0);
        try {
            gestorUsuarios.registrar(estudiante);
            pruebaLogica("Registro de estudiante nuevo", true);
        } catch (Exception e) {
            pruebaLogica("Registro de estudiante nuevo", false);
        }

        // Documento duplicado
        Estudiante duplicado = new Estudiante("1094938271", "Otro", "otro", "Clave123", "P", "R", "Programa", 0);
        try {
            gestorUsuarios.registrar(duplicado);
            pruebaLogica("Documento duplicado es rechazado", false);
        } catch (Exception e) {
            pruebaLogica("Documento duplicado es rechazado", true);
        }

        // Nombre de usuario duplicado
        Estudiante duplicadoUsuario = new Estudiante("1094938272", "Otro", "analopez", "Clave123", "P", "R", "Programa", 0);
        try {
            gestorUsuarios.registrar(duplicadoUsuario);
            pruebaLogica("Nombre de usuario duplicado es rechazado", false);
        } catch (Exception e) {
            pruebaLogica("Nombre de usuario duplicado es rechazado", true);
        }

        // Login de estudiante por nombre de usuario
        try {
            modelo.Usuario u = gestorUsuarios.iniciarSesion("analopez", "Clave123");
            pruebaLogica("Login de estudiante por nombre de usuario", u.getRol().equals("ESTUDIANTE"));
        } catch (Exception e) {
            pruebaLogica("Login de estudiante por nombre de usuario", false);
        }

        // Login de estudiante por documento (T53)
        try {
            modelo.Usuario u = gestorUsuarios.iniciarSesion("1094938271", "Clave123");
            pruebaLogica("Login de estudiante por documento (T53)",
                    u.getRol().equals("ESTUDIANTE") && u.getDocumento().equals("1094938271"));
        } catch (Exception e) {
            pruebaLogica("Login de estudiante por documento (T53)", false);
        }

        // Login por documento con contrasena incorrecta (T53)
        try {
            gestorUsuarios.iniciarSesion("1094938271", "claveMala");
            pruebaLogica("Documento con contrasena incorrecta es rechazado (T53)", false);
        } catch (Exception e) {
            pruebaLogica("Documento con contrasena incorrecta es rechazado (T53)",
                    e.getMessage().contains("Credenciales"));
        }

        // Login por documento inexistente (T53)
        try {
            gestorUsuarios.iniciarSesion("9999999999", "Clave123");
            pruebaLogica("Documento inexistente es rechazado (T53)", false);
        } catch (Exception e) {
            pruebaLogica("Documento inexistente es rechazado (T53)", e.getMessage().contains("Credenciales"));
        }

        // El administrador es la excepcion: solo entra con su nombre de usuario (T53)
        try {
            gestorUsuarios.iniciarSesion("1000", "admin123");
            pruebaLogica("Administrador NO entra con su documento (T53)", false);
        } catch (Exception e) {
            pruebaLogica("Administrador NO entra con su documento (T53)", e.getMessage().contains("Credenciales"));
        }

        // El administrador si entra con su nombre de usuario (T53)
        try {
            modelo.Usuario u = gestorUsuarios.iniciarSesion("admin", "admin123");
            pruebaLogica("Administrador si entra con su nombre de usuario (T53)",
                    u.getRol().equals("ADMINISTRADOR"));
        } catch (Exception e) {
            pruebaLogica("Administrador si entra con su nombre de usuario (T53)", false);
        }

        // Asignacion de DUTS
        try {
            gestorMovimientos.asignarDuts(estudiante, 50000, "Beca inicial");
            pruebaLogica("Asignacion de 50000 DUTS deja saldo 50000", estudiante.getSaldoDuts() == 50000);
        } catch (Exception e) {
            pruebaLogica("Asignacion de 50000 DUTS deja saldo 50000", false);
        }

        // Transferencia
        Estudiante receptor = new Estudiante("1094938273", "Carlos Ruiz", "carlosruiz",
                "Clave123", "P", "R", "Contaduria", 0);
        try {
            gestorUsuarios.registrar(receptor);
            gestorMovimientos.transferir(estudiante, receptor, 10000, "Prueba");
            pruebaLogica("Transferencia de 10000 ajusta ambos saldos",
                    estudiante.getSaldoDuts() == 40000 && receptor.getSaldoDuts() == 10000);
        } catch (Exception e) {
            pruebaLogica("Transferencia de 10000 ajusta ambos saldos", false);
        }

        // Saldo insuficiente
        try {
            gestorMovimientos.transferir(estudiante, receptor, 999999, "Prueba negativa");
            pruebaLogica("Transferencia sin saldo es rechazada", false);
        } catch (Exception e) {
            pruebaLogica("Transferencia sin saldo es rechazada", e.getMessage().contains("Saldo insuficiente"));
        }

        // Novedades por defecto
        ArrayList<Novedad> novedades = gestorNovedades.listar();
        pruebaLogica("Se cargan 4 novedades por defecto", novedades.size() == 4);

        // Calculo de promedios con el nuevo DIAS_SEMESTRE = 90 (T51)
        double promedioSem = gestorMovimientos.calcularPromedio(estudiante.getDocumento(),
                GestorMovimientos.DIAS_SEMESTRE);
        pruebaLogica("DIAS_SEMESTRE vale 90 (T51)", GestorMovimientos.DIAS_SEMESTRE == 90);
        pruebaLogica("Promedio por cuatrimestre es mayor o igual a 0", promedioSem >= 0);

        // Movimientos del usuario
        ArrayList<Movimiento> movimientos = gestorMovimientos.listarPorUsuario(estudiante.getDocumento());
        pruebaLogica("El estudiante tiene al menos 2 movimientos", movimientos.size() >= 2);

        // Filtro por tipo
        ArrayList<Movimiento> asignaciones = gestorMovimientos.filtrar(estudiante.getDocumento(),
                Movimiento.TIPO_ASIGNACION, null, null);
        pruebaLogica("Filtro por tipo ASIGNACION devuelve 1 registro", asignaciones.size() == 1);

        // Exportacion CSV
        String rutaCsv = gestorMovimientos.exportarCsv(estudiante.getDocumento());
        pruebaLogica("Exportacion CSV genera archivo", new File(rutaCsv).exists());

        // Notificaciones
        gestorNotificaciones.agregar(estudiante.getDocumento(), "Mensaje de prueba");
        pruebaLogica("Notificacion registrada para el estudiante",
                gestorNotificaciones.listarPorUsuario(estudiante.getDocumento()).size() >= 1);

        // Amigos
        try {
            gestorAmigos.agregar(estudiante.getDocumento(), receptor.getDocumento());
            pruebaLogica("Amigo agregado", gestorAmigos.listarAmigos(estudiante.getDocumento()).size() == 1);
        } catch (Exception e) {
            pruebaLogica("Amigo agregado", false);
        }

        // Inscripcion en novedad
        if (novedades.size() > 0) {
            Novedad novedad = novedades.get(0);
            try {
                gestorInscripciones.inscribir(novedad.getCodigo(), estudiante.getDocumento());
                pruebaLogica("Inscripcion en novedad creada",
                        gestorInscripciones.listarPorEstudiante(estudiante.getDocumento()).size() >= 1);
            } catch (Exception e) {
                pruebaLogica("Inscripcion en novedad creada", false);
            }
        }

        System.out.println("");
        System.out.println("Archivos de prueba generados en: " + dirTrabajo);
    }

    // Borra el contenido de una carpeta para que la prueba arranque siempre desde cero
    private static void limpiarCarpeta(File carpeta) {
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            return;
        }
        File[] archivos = carpeta.listFiles();
        if (archivos == null) {
            return;
        }
        for (int i = 0; i < archivos.length; i++) {
            archivos[i].delete();
        }
    }

    private static void prueba(String nombre, Object esperado, Object obtenido) {
        totalPruebas++;
        boolean ok;
        if (esperado instanceof Integer) {
            ok = ((Integer) esperado).intValue() == ((Integer) obtenido).intValue();
        } else if (esperado instanceof Boolean) {
            ok = ((Boolean) esperado).booleanValue() == ((Boolean) obtenido).booleanValue();
        } else {
            ok = String.valueOf(esperado).equals(String.valueOf(obtenido));
        }
        if (ok) {
            pruebasExitosas++;
            System.out.println("  OK   " + nombre + " = " + obtenido);
        } else {
            System.out.println("  FALLA " + nombre + " -> esperado " + esperado + ", obtenido " + obtenido);
        }
    }

    private static void pruebaLogica(String nombre, boolean resultado) {
        totalPruebas++;
        if (resultado) {
            pruebasExitosas++;
            System.out.println("  OK   " + nombre);
        } else {
            System.out.println("  FALLA " + nombre);
        }
    }
}
