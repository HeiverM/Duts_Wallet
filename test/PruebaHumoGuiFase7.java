// Prueba de humo de la GUI: construye las ventanas nuevas, aplica el tema y verifica el fondo del login.
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import vista.PanelFondoLogin;
import vista.VentanaCalculadora;
import vista.VentanaInicio;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.GestorTema;

public class PruebaHumoGuiFase7 {

    private static int total = 0;
    private static int exitosas = 0;

    public static void main(String[] args) {
        System.out.println("=== PRUEBA DE HUMO GUI (FASE 7) ===");

        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorMovimientos gestorMovimientos = new GestorMovimientos(gestorUsuarios);
        GestorNovedades gestorNovedades = new GestorNovedades();
        GestorNotificaciones gestorNotificaciones = new GestorNotificaciones();
        GestorInscripciones gestorInscripciones = new GestorInscripciones(gestorMovimientos,
                gestorNotificaciones, gestorUsuarios, gestorNovedades);
        GestorAmigos gestorAmigos = new GestorAmigos(gestorUsuarios);

        // 1. VentanaInicio (T46)
        VentanaInicio ventanaInicio = new VentanaInicio(gestorUsuarios, gestorMovimientos,
                gestorNovedades, gestorInscripciones, gestorNotificaciones, gestorAmigos);
        prueba("VentanaInicio se construye sin excepciones", ventanaInicio != null);
        prueba("VentanaInicio mide 1280x720 (T46)", ventanaInicio.getWidth() == 1280 && ventanaInicio.getHeight() == 720);
        prueba("VentanaInicio usa PanelFondoLogin como content pane",
                ventanaInicio.getContentPane() instanceof PanelFondoLogin);
        ventanaInicio.dispose();

        // 2. PanelFondoLogin (T44) carga la imagen
        PanelFondoLogin panelFondo = new PanelFondoLogin();
        prueba("PanelFondoLogin se construye", panelFondo != null);
        BufferedImage imagen = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics g = imagen.getGraphics();
        panelFondo.setSize(1280, 720);
        panelFondo.paint(g);
        prueba("PanelFondoLogin dibuja sin excepciones", imagen.getWidth() == 1280);
        prueba("El recurso Fondo_login.jpg existe en recursos/",
                new File("recursos/Fondo_login.jpg").exists());
        g.dispose();

        // 3. GestorTema aplica tema a una ventana (T40/T47/T48)
        VentanaCalculadora calculadora = new VentanaCalculadora(null, gestorMovimientos,
                gestorNovedades, gestorUsuarios);
        prueba("VentanaCalculadora se construye en modo admin (T43)", calculadora != null);
        GestorTema.aplicarTema(calculadora);
        prueba("GestorTema.aplicarTema no lanza excepciones", true);
        calculadora.dispose();

        // 4. Alternancia de tema
        GestorTema.alternarTema();
        prueba("Tema alterna a oscuro", GestorTema.esTemaOscuro());
        GestorTema.aplicarTema(calculadora);
        GestorTema.alternarTema();
        prueba("Tema vuelve a claro", !GestorTema.esTemaOscuro());
        calculadora.dispose();

        System.out.println("");
        System.out.println("Pruebas ejecutadas: " + total);
        System.out.println("Pruebas exitosas:  " + exitosas);
        System.out.println("Pruebas fallidas:  " + (total - exitosas));
        if (exitosas == total) {
            System.out.println("RESULTADO: HUMO GUI SUPERADO");
        } else {
            System.out.println("RESULTADO: HAY FALLAS EN HUMO GUI");
        }
        System.exit(0);
    }

    private static void prueba(String nombre, boolean resultado) {
        total++;
        if (resultado) {
            exitosas++;
            System.out.println("  OK   " + nombre);
        } else {
            System.out.println("  FALLA " + nombre);
        }
    }
}
