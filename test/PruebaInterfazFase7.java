// Prueba de humo de la interfaz: construye las ventanas de la Fase 7 y aplica el tema.
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import modelo.Estudiante;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.GestorTema;
import vista.VentanaCalculadora;
import vista.VentanaInicio;
import vista.VentanaMenuAdmin;
import vista.VentanaMenuEstudiante;

public class PruebaInterfazFase7 {

    private static int total = 0;
    private static int exitos = 0;

    public static void main(String[] args) throws Exception {
        String dirTrabajo = System.getProperty("java.io.tmpdir") + "/duts_prueba_interfaz";
        java.io.File carpeta = new java.io.File(dirTrabajo);
        carpeta.mkdirs();

        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorMovimientos gestorMovimientos = new GestorMovimientos(gestorUsuarios);
        GestorNovedades gestorNovedades = new GestorNovedades();
        GestorNotificaciones gestorNotificaciones = new GestorNotificaciones();
        GestorInscripciones gestorInscripciones = new GestorInscripciones(gestorMovimientos,
                gestorNotificaciones, gestorUsuarios, gestorNovedades);
        GestorAmigos gestorAmigos = new GestorAmigos(gestorUsuarios);

        Estudiante estudiante = new Estudiante("1094938271", "Ana Maria Lopez", "analopez",
                "Clave123", "Color favorita", "Azul", "Ingenieria de Sistemas", 0);
        try {
            gestorUsuarios.registrar(estudiante);
        } catch (Exception e) {
            // ya existia de una corrida anterior
        }

        // Todas las ventanas se construyen dentro del hilo de Swing
        final Object[] errores = new Object[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                try {
                    // Ventana de inicio con fondo de imagen
                    VentanaInicio ventanaInicio = new VentanaInicio(gestorUsuarios, gestorMovimientos,
                            gestorNovedades, gestorInscripciones, gestorNotificaciones, gestorAmigos);
                    prueba("VentanaInicio se construye con fondo", ventanaInicio != null);
                    prueba("Fondo de login usa PanelFondoLogin",
                            ventanaInicio.getContentPane().getClass().getSimpleName().equals("PanelFondoLogin"));

                    // Ventana de inicio en modo oscuro
                    GestorTema.alternarTema();
                    prueba("Tema alterna a oscuro", GestorTema.esTemaOscuro());
                    VentanaInicio oscuro = new VentanaInicio(gestorUsuarios, gestorMovimientos,
                            gestorNovedades, gestorInscripciones, gestorNotificaciones, gestorAmigos);
                    prueba("VentanaInicio se construye en modo oscuro", oscuro != null);
                    oscuro.dispose();
                    GestorTema.alternarTema();
                    prueba("Tema vuelve a claro", !GestorTema.esTemaOscuro());

                    // Menu del estudiante (contiene BotonTema y Calculadora)
                    VentanaMenuEstudiante menu = new VentanaMenuEstudiante(estudiante, gestorUsuarios,
                            gestorMovimientos, gestorNovedades, gestorInscripciones, gestorNotificaciones,
                            gestorAmigos, ventanaInicio);
                    prueba("VentanaMenuEstudiante se construye", menu != null);
                    menu.dispose();

                    // Menu del administrador
                    modelo.Administrador admin = (modelo.Administrador) gestorUsuarios.buscarPorNombreUsuario("admin");
                    VentanaMenuAdmin menuAdmin = new VentanaMenuAdmin(admin, gestorUsuarios,
                            gestorMovimientos, gestorNovedades, gestorInscripciones, ventanaInicio);
                    prueba("VentanaMenuAdmin se construye", menuAdmin != null);
                    menuAdmin.dispose();

                    // Calculadora en modo estudiante
                    VentanaCalculadora calcEstudiante = new VentanaCalculadora(estudiante,
                            gestorMovimientos, gestorNovedades, null);
                    prueba("Calculadora modo estudiante se construye", calcEstudiante != null);
                    calcEstudiante.dispose();

                    // Calculadora en modo administrador
                    VentanaCalculadora calcAdmin = new VentanaCalculadora(null,
                            gestorMovimientos, gestorNovedades, gestorUsuarios);
                    prueba("Calculadora modo administrador se construye", calcAdmin != null);
                    calcAdmin.dispose();
                } catch (Throwable t) {
                    errores[0] = t;
                }
            }
        });

        if (errores[0] != null) {
            Throwable t = (Throwable) errores[0];
            System.out.println("  FALLA Excepcion al construir ventanas: " + t);
            t.printStackTrace();
            total++;
        }

        System.out.println("");
        System.out.println("=== RESUMEN INTERFAZ ===");
        System.out.println("Pruebas ejecutadas: " + total);
        System.out.println("Pruebas exitosas:  " + exitos);
        System.out.println("Pruebas fallidas:  " + (total - exitos));
        if (exitos == total) {
            System.out.println("RESULTADO: TODAS LAS PRUEBAS PASARON");
        } else {
            System.out.println("RESULTADO: HAY PRUEBAS FALLIDAS");
        }
        System.exit(0);
    }

    private static void prueba(String nombre, boolean resultado) {
        total++;
        if (resultado) {
            exitos++;
            System.out.println("  OK   " + nombre);
        } else {
            System.out.println("  FALLA " + nombre);
        }
    }
}
