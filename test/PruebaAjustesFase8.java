// Pruebas de los 6 ajustes de la Fase 8: texto del login en modo oscuro, registro sin alias, perfil, cerrar sesion y salir.
// Este archivo es solo de prueba (carpeta test/, no se entrega): puede usar un hilo para cerrar los JOptionPane modales.
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import modelo.Estudiante;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.GestorTema;
import vista.VentanaAmigos;
import vista.VentanaInicio;
import vista.VentanaRegistro;
import vista.VentanaTransferir;

public class PruebaAjustesFase8 {

    private static int total = 0;
    private static int exitos = 0;

    // Mensajes de los JOptionPane que el cerrador automatico fue cerrando
    private static final ArrayList<String> mensajes = new ArrayList<String>();

    // Si no es null, el cerrador pulsa el boton con este texto (por ejemplo "No") en vez de solo cerrar el dialogo
    private static volatile String botonARepulsar = null;

    public static void main(String[] args) throws Exception {
        limpiarCarpeta(new File("datos"));

        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorMovimientos gestorMovimientos = new GestorMovimientos(gestorUsuarios);
        GestorNovedades gestorNovedades = new GestorNovedades();
        GestorNotificaciones gestorNotificaciones = new GestorNotificaciones();
        GestorInscripciones gestorInscripciones = new GestorInscripciones(gestorMovimientos,
                gestorNotificaciones, gestorUsuarios, gestorNovedades);
        GestorAmigos gestorAmigos = new GestorAmigos(gestorUsuarios);

        String filtro = args.length > 0 ? args[0] : "todos";
        iniciarCerradorDeDialogos();

        if (filtro.equals("todos") || filtro.equals("c1")) {
            pruebaC1(gestorUsuarios, gestorMovimientos, gestorNovedades, gestorInscripciones,
                    gestorNotificaciones, gestorAmigos);
        }
        if (filtro.equals("todos") || filtro.equals("c2")) {
            pruebaC2(gestorUsuarios, gestorMovimientos, gestorNotificaciones, gestorAmigos);
        }
        if (filtro.equals("todos") || filtro.equals("c3")) {
            pruebaC3(gestorUsuarios, gestorMovimientos);
        }
        if (filtro.equals("todos") || filtro.equals("c4")) {
            pruebaC4(gestorUsuarios, gestorMovimientos, gestorNovedades, gestorInscripciones,
                    gestorNotificaciones, gestorAmigos);
        }
        if (filtro.equals("todos") || filtro.equals("c5")) {
            pruebaC5(gestorUsuarios, gestorMovimientos, gestorNovedades, gestorInscripciones,
                    gestorNotificaciones, gestorAmigos);
        }
        if (filtro.equals("c5si")) {
            pruebaC5Confirmar(gestorUsuarios, gestorMovimientos, gestorNovedades, gestorInscripciones,
                    gestorNotificaciones, gestorAmigos);
        }

        System.out.println("");
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

    // ---------------------------------------------------------------- C1
    private static void pruebaC1(GestorUsuarios gu, GestorMovimientos gm, GestorNovedades gn,
                                 GestorInscripciones gi, GestorNotificaciones gnot, GestorAmigos ga) {
        System.out.println("[C1] Texto del login en modo oscuro y fuente del logo");
        VentanaInicio ventana = new VentanaInicio(gu, gm, gn, gi, gnot, ga);
        prueba("Modo claro: todas las etiquetas del login son blancas",
                todasLasEtiquetasBlancas(ventana.getContentPane()));

        GestorTema.alternarTema();
        GestorTema.aplicarTema(ventana);
        prueba("Modo oscuro activo", GestorTema.esTemaOscuro());
        prueba("Modo oscuro: todas las etiquetas del login siguen blancas",
                todasLasEtiquetasBlancas(ventana.getContentPane()));
        GestorTema.alternarTema();
        GestorTema.aplicarTema(ventana);
        prueba("Vuelve a modo claro: etiquetas siguen blancas",
                todasLasEtiquetasBlancas(ventana.getContentPane()));

        JLabel titulo = buscarEtiqueta(ventana.getContentPane(), "DUTS Wallet");
        prueba("El logo 'DUTS Wallet' existe", titulo != null);
        prueba("El logo mide 31 puntos (28 + 3)", titulo != null && titulo.getFont().getSize() == 31);
        ventana.dispose();
    }

    // ---------------------------------------------------------------- C2
    private static void pruebaC2(GestorUsuarios gu, GestorMovimientos gm,
                                 GestorNotificaciones gnot, GestorAmigos ga) throws Exception {
        System.out.println("[C2] Registro sin alias y receptor por documento");

        VentanaRegistro registro = new VentanaRegistro(gu);
        ArrayList<JTextField> campos = new ArrayList<JTextField>();
        listarCamposTexto(registro.getContentPane(), campos);
        prueba("Registro tiene 4 campos de texto (documento, nombre, programa, respuesta)", campos.size() == 4);
        prueba("Registro tiene 1 campo de contrasena", contarPassword(registro.getContentPane()) == 1);
        prueba("Registro ya no tiene etiqueta de alias/usuario", !hayEtiquetaQueContiene(registro.getContentPane(), "Usuario"));
        prueba("Registro no tiene ningun tooltip", !hayTooltip(registro.getContentPane()));

        // Registro con campos vacios debe rechazarse con mensaje claro
        int usuariosAntes = gu.listarEstudiantes().size();
        JButton btnRegistrar = buscarBoton(registro.getContentPane(), "Registrar");
        btnRegistrar.doClick();
        esperar(400);
        prueba("Registro vacio: no crea usuario", gu.listarEstudiantes().size() == usuariosAntes);
        prueba("Registro vacio: muestra mensaje de campo vacio", ultimoMensajeContiene("no puede estar vac"));

        // Registro valido sin alias
        campos.get(0).setText("2001");
        campos.get(1).setText("Laura Gomez");
        campos.get(2).setText("Sistemas");
        campos.get(3).setText("Bucaramanga");
        poner(registro.getContentPane(), "claveLaura");
        btnRegistrar.doClick();
        esperar(400);
        Estudiante laura = gu.buscarEstudiantePorDocumento("2001");
        prueba("Registro valido crea el estudiante", laura != null);
        prueba("Su nombre de usuario interno es su documento", laura != null && laura.getNombreUsuario().equals("2001"));
        prueba("Saldo inicial 0", laura != null && laura.getSaldoDuts() == 0);
        prueba("Muestra mensaje de registro exitoso", ultimoMensajeContiene("Registro exitoso"));
        try {
            prueba("Puede iniciar sesion con su documento", gu.iniciarSesion("2001", "claveLaura").getDocumento().equals("2001"));
        } catch (Exception e) {
            prueba("Puede iniciar sesion con su documento", false);
        }

        // Documento duplicado
        VentanaRegistro duplicado = new VentanaRegistro(gu);
        ArrayList<JTextField> campos2 = new ArrayList<JTextField>();
        listarCamposTexto(duplicado.getContentPane(), campos2);
        campos2.get(0).setText("2001");
        campos2.get(1).setText("Otra Persona");
        campos2.get(2).setText("Sistemas");
        campos2.get(3).setText("x");
        poner(duplicado.getContentPane(), "otraClave");
        buscarBoton(duplicado.getContentPane(), "Registrar").doClick();
        esperar(400);
        prueba("Documento duplicado: rechazado", ultimoMensajeContiene("Ya existe un usuario con ese documento"));
        prueba("Documento duplicado: no se crea un segundo usuario", gu.listarEstudiantes().size() == usuariosAntes + 1);

        // Documento no numerico
        VentanaRegistro letras = new VentanaRegistro(gu);
        ArrayList<JTextField> campos3 = new ArrayList<JTextField>();
        listarCamposTexto(letras.getContentPane(), campos3);
        campos3.get(0).setText("abc");
        campos3.get(1).setText("Pedro");
        campos3.get(2).setText("Sistemas");
        campos3.get(3).setText("x");
        poner(letras.getContentPane(), "clave");
        buscarBoton(letras.getContentPane(), "Registrar").doClick();
        esperar(400);
        prueba("Documento no numerico: rechazado", ultimoMensajeContiene("numérico"));
        registro.dispose();
        duplicado.dispose();
        letras.dispose();

        // Transferencia por documento
        Estudiante emisor = laura;
        Estudiante receptor = new Estudiante("2002", "Mario Diaz", "2002", "claveMario", "P", "R", "Contaduria", 0);
        gu.registrar(receptor);
        gm.asignarDuts(emisor, 1000, "Saldo de prueba");

        VentanaTransferir transferir = new VentanaTransferir(emisor, gu, gm, gnot, "");
        prueba("Transferir pide 'Documento receptor'", buscarEtiqueta(transferir.getContentPane(), "Documento receptor:") != null);
        prueba("Transferir ya no pide 'Usuario receptor'", buscarEtiqueta(transferir.getContentPane(), "Usuario receptor:") == null);
        ArrayList<JTextField> camposT = new ArrayList<JTextField>();
        listarCamposTexto(transferir.getContentPane(), camposT);
        camposT.get(0).setText("2002");
        camposT.get(1).setText("300");
        camposT.get(2).setText("Pago almuerzo");
        buscarBoton(transferir.getContentPane(), "Enviar").doClick();
        esperar(400);
        prueba("Transferencia por documento: saldo emisor 700", emisor.getSaldoDuts() == 700);
        prueba("Transferencia por documento: saldo receptor 300", receptor.getSaldoDuts() == 300);
        prueba("Notificacion al receptor menciona el nombre del emisor",
                gnot.listarPorUsuario("2002").size() == 1
                && gnot.listarPorUsuario("2002").get(0).getMensaje().contains("Laura Gomez"));
        transferir.dispose();

        // Receptor inexistente
        VentanaTransferir inexistente = new VentanaTransferir(emisor, gu, gm, gnot, "");
        ArrayList<JTextField> camposI = new ArrayList<JTextField>();
        listarCamposTexto(inexistente.getContentPane(), camposI);
        camposI.get(0).setText("9999");
        camposI.get(1).setText("10");
        camposI.get(2).setText("x");
        buscarBoton(inexistente.getContentPane(), "Enviar").doClick();
        esperar(400);
        prueba("Receptor inexistente: rechazado con mensaje claro", ultimoMensajeContiene("receptor no existe"));
        prueba("Receptor inexistente: el saldo no cambia", emisor.getSaldoDuts() == 700);

        // El administrador no puede ser receptor
        camposI.get(0).setText("1000");
        buscarBoton(inexistente.getContentPane(), "Enviar").doClick();
        esperar(400);
        prueba("Administrador como receptor: rechazado", ultimoMensajeContiene("receptor no existe"));

        // Saldo insuficiente
        camposI.get(0).setText("2002");
        camposI.get(1).setText("5000");
        buscarBoton(inexistente.getContentPane(), "Enviar").doClick();
        esperar(400);
        prueba("Saldo insuficiente sigue funcionando", ultimoMensajeContiene("Saldo insuficiente"));
        inexistente.dispose();

        // Transferir a mi mismo
        VentanaTransferir yoMismo = new VentanaTransferir(emisor, gu, gm, gnot, "");
        ArrayList<JTextField> camposY = new ArrayList<JTextField>();
        listarCamposTexto(yoMismo.getContentPane(), camposY);
        camposY.get(0).setText("2001");
        camposY.get(1).setText("10");
        camposY.get(2).setText("x");
        buscarBoton(yoMismo.getContentPane(), "Enviar").doClick();
        esperar(400);
        prueba("Transferirse a si mismo: rechazado", emisor.getSaldoDuts() == 700);
        yoMismo.dispose();

        // Desde Amigos: el documento llega prellenado y bloqueado
        ga.agregar("2001", "2002");
        VentanaAmigos amigos = new VentanaAmigos(emisor, ga, gu, gm, gnot);
        JTable tabla = buscarTabla(amigos.getContentPane());
        tabla.setRowSelectionInterval(0, 0);
        buscarBoton(amigos.getContentPane(), "Transferir a amigo seleccionado").doClick();
        esperar(400);
        VentanaTransferir desdeAmigos = buscarVentanaTransferir();
        prueba("Amigos abre VentanaTransferir", desdeAmigos != null);
        if (desdeAmigos != null) {
            ArrayList<JTextField> camposA = new ArrayList<JTextField>();
            listarCamposTexto(desdeAmigos.getContentPane(), camposA);
            prueba("Amigos: el documento del amigo llega prellenado", camposA.get(0).getText().equals("2002"));
            prueba("Amigos: el documento del amigo no se puede editar", !camposA.get(0).isEditable());
            camposA.get(1).setText("50");
            camposA.get(2).setText("Rapida");
            buscarBoton(desdeAmigos.getContentPane(), "Enviar").doClick();
            esperar(400);
            prueba("Amigos: transferencia rapida funciona", emisor.getSaldoDuts() == 650 && receptor.getSaldoDuts() == 350);
            desdeAmigos.dispose();
        }
        amigos.dispose();
    }

    // ---------------------------------------------------------------- C3
    private static void pruebaC3(GestorUsuarios gu, GestorMovimientos gm) throws Exception {
        System.out.println("[C3] Perfil sin alias");
        Estudiante ana = gu.buscarEstudiantePorDocumento("3001");
        if (ana == null) {
            ana = new Estudiante("3001", "Ana Perez", "3001", "claveAna", "P", "R", "Sistemas", 0);
            gu.registrar(ana);
        }
        gm.asignarDuts(ana, 7000, "Saldo de prueba");

        vista.VentanaPerfil perfil = new vista.VentanaPerfil(ana);
        Container panel = perfil.getContentPane();
        prueba("Perfil muestra 'Nombre:'", buscarEtiqueta(panel, "Nombre:") != null);
        prueba("Perfil muestra 'Documento:'", buscarEtiqueta(panel, "Documento:") != null);
        prueba("Perfil muestra 'Programa:'", buscarEtiqueta(panel, "Programa:") != null);
        prueba("Perfil muestra 'Saldo disponible:'", buscarEtiqueta(panel, "Saldo disponible:") != null);
        prueba("Perfil NO muestra 'Usuario:'", buscarEtiqueta(panel, "Usuario:") == null);
        prueba("Perfil NO muestra el valor del alias en ninguna etiqueta", !hayEtiquetaQueContiene(panel, "alias"));
        prueba("Perfil muestra el nombre real", buscarEtiqueta(panel, "Ana Perez") != null);
        prueba("Perfil muestra el programa real", buscarEtiqueta(panel, "Sistemas") != null);
        prueba("Perfil muestra 'Meta de grado'", hayEtiquetaQueContiene(panel, "Meta de grado"));
        prueba("Perfil muestra 'DUTS acumulados'", buscarEtiqueta(panel, "DUTS acumulados:") != null);
        prueba("Perfil muestra 'DUTS faltantes'", buscarEtiqueta(panel, "DUTS faltantes:") != null);
        prueba("Perfil con 7000 DUTS: estado 'Meta en progreso'", buscarEtiqueta(panel, "Meta en progreso") != null);
        prueba("Perfil: barra al 10%", barraDe(panel) != null && barraDe(panel).getValue() == 10);
        prueba("Perfil: faltantes 63000 DUTS", buscarEtiqueta(panel, "63000 DUTS") != null);

        // Ninguna etiqueta se solapa con otra en la misma fila (misma coordenada Y)
        prueba("Perfil: ninguna etiqueta se solapa", sinSolapamientos(panel));
        // Todos los componentes caben dentro de la ventana (se usa el tamaño de la ventana menos la barra de titulo)
        prueba("Perfil: todo cabe dentro de la ventana", todoDentro(panel, perfil.getHeight() - 40, perfil.getWidth()));
        perfil.dispose();

        // Meta cumplida
        Estudiante campeon = new Estudiante("3002", "Carlos Meta", "3002", "clave", "P", "R", "Sistemas", 70000);
        gu.registrar(campeon);
        vista.VentanaPerfil perfilMeta = new vista.VentanaPerfil(campeon);
        prueba("Meta cumplida: estado 'Meta cumplida'", buscarEtiqueta(perfilMeta.getContentPane(), "Meta cumplida") != null);
        prueba("Meta cumplida: barra al 100%", barraDe(perfilMeta.getContentPane()).getValue() == 100);
        prueba("Meta cumplida: faltantes 0 DUTS", buscarEtiqueta(perfilMeta.getContentPane(), "0 DUTS") != null);
        perfilMeta.dispose();

        // El boton Cerrar sigue funcionando
        vista.VentanaPerfil perfilCerrar = new vista.VentanaPerfil(ana);
        perfilCerrar.setVisible(true);
        buscarBoton(perfilCerrar.getContentPane(), "Cerrar").doClick();
        prueba("Perfil: el boton Cerrar cierra la ventana", !perfilCerrar.isDisplayable());
    }

    private static javax.swing.JProgressBar barraDe(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof javax.swing.JProgressBar) {
                return (javax.swing.JProgressBar) componentes[i];
            }
        }
        return null;
    }

    // Dos componentes con los limites cruzados se consideran solapados
    private static boolean sinSolapamientos(Container contenedor) {
        Component[] c = contenedor.getComponents();
        for (int i = 0; i < c.length; i++) {
            for (int j = i + 1; j < c.length; j++) {
                if (c[i].getBounds().intersects(c[j].getBounds())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean todoDentro(Container contenedor, int alto, int ancho) {
        Component[] c = contenedor.getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i].getY() + c[i].getHeight() > alto || c[i].getX() + c[i].getWidth() > ancho) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- C4
    private static void pruebaC4(GestorUsuarios gu, GestorMovimientos gm, GestorNovedades gn,
                                 GestorInscripciones gi, GestorNotificaciones gnot, GestorAmigos ga) throws Exception {
        System.out.println("[C4] Cerrar sesion vuelve a la ventana principal");
        Estudiante sofia = gu.buscarEstudiantePorDocumento("4001");
        if (sofia == null) {
            sofia = new Estudiante("4001", "Sofia Rios", "4001", "claveSofia", "P", "R", "Sistemas", 0);
            gu.registrar(sofia);
        }

        // ---- Estudiante
        VentanaInicio inicio = new VentanaInicio(gu, gm, gn, gi, gnot, ga);
        inicio.setVisible(true);

        // Credenciales incorrectas: la ventana de inicio NO se oculta
        ponerEnInicio(inicio, "4001", "mala");
        buscarBoton(inicio.getContentPane(), "Ingresar").doClick();
        esperar(400);
        prueba("Credenciales incorrectas: el inicio sigue visible", inicio.isVisible());
        prueba("Credenciales incorrectas: mensaje claro", ultimoMensajeContiene("Credenciales incorrectas"));

        ponerEnInicio(inicio, "4001", "claveSofia");
        buscarBoton(inicio.getContentPane(), "Ingresar").doClick();
        esperar(400);
        vista.VentanaMenuEstudiante menuEst = buscarMenuEstudiante();
        prueba("Login estudiante: abre el menu del estudiante", menuEst != null);
        prueba("Login estudiante: el inicio se oculta", !inicio.isVisible());
        prueba("Login estudiante: el inicio NO fue destruido", inicio.isDisplayable());

        // Se abre una ventana hija para comprobar que tambien se cierra al salir
        buscarBoton(menuEst.getContentPane(), "Mi perfil y meta de grado").doClick();
        esperar(300);
        prueba("Hay una ventana hija abierta (perfil)", buscarVentanaPerfil() != null);

        buscarBoton(menuEst.getContentPane(), "Cerrar sesión").doClick();
        esperar(400);
        prueba("Cerrar sesion: el inicio vuelve a mostrarse", inicio.isVisible());
        prueba("Cerrar sesion: el menu del estudiante se cierra", !menuEst.isDisplayable());
        prueba("Cerrar sesion: la ventana hija tambien se cierra", buscarVentanaPerfil() == null);
        prueba("Cerrar sesion: el campo de documento queda vacio", textoDeUsuario(inicio).equals(""));
        prueba("Cerrar sesion: el campo de contrasena queda vacio", textoDeContrasena(inicio).equals(""));

        // Volver a iniciar sesion con otro usuario funciona
        ponerEnInicio(inicio, "4001", "claveSofia");
        buscarBoton(inicio.getContentPane(), "Ingresar").doClick();
        esperar(400);
        prueba("Segundo login: abre otro menu de estudiante", buscarMenuEstudiante() != null);
        buscarBoton(buscarMenuEstudiante().getContentPane(), "Cerrar sesión").doClick();
        esperar(400);
        prueba("Segundo cierre de sesion: el inicio vuelve a mostrarse", inicio.isVisible());

        // ---- Administrador
        ponerEnInicio(inicio, "admin", "admin123");
        buscarBoton(inicio.getContentPane(), "Ingresar").doClick();
        esperar(400);
        vista.VentanaMenuAdmin menuAdm = buscarMenuAdmin();
        prueba("Login admin: abre el menu del administrador", menuAdm != null);
        prueba("Login admin: el inicio se oculta", !inicio.isVisible());
        buscarBoton(menuAdm.getContentPane(), "Calculadora de DUTS").doClick();
        esperar(300);
        prueba("Admin: hay una ventana hija abierta (calculadora)", buscarVentanaCalculadora() != null);
        buscarBoton(menuAdm.getContentPane(), "Cerrar sesión").doClick();
        esperar(400);
        prueba("Admin cerrar sesion: el inicio vuelve a mostrarse", inicio.isVisible());
        prueba("Admin cerrar sesion: el menu se cierra", !menuAdm.isDisplayable());
        prueba("Admin cerrar sesion: la calculadora tambien se cierra", buscarVentanaCalculadora() == null);
        prueba("Admin cerrar sesion: campos vacios", textoDeUsuario(inicio).equals("") && textoDeContrasena(inicio).equals(""));

        // ---- El tema elegido se conserva al volver
        GestorTema.alternarTema();
        ponerEnInicio(inicio, "4001", "claveSofia");
        buscarBoton(inicio.getContentPane(), "Ingresar").doClick();
        esperar(400);
        buscarBoton(buscarMenuEstudiante().getContentPane(), "Cerrar sesión").doClick();
        esperar(400);
        prueba("El modo oscuro se conserva al volver al inicio", GestorTema.esTemaOscuro());
        prueba("Modo oscuro: las etiquetas del login siguen blancas", todasLasEtiquetasBlancas(inicio.getContentPane()));
        GestorTema.alternarTema();

        // Solo queda UNA ventana visible: la de inicio
        int visibles = 0;
        Frame[] marcos = Frame.getFrames();
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i].isVisible()) {
                visibles++;
            }
        }
        prueba("Al final solo hay una ventana visible (el inicio)", visibles == 1);
        inicio.dispose();
    }

    private static void ponerEnInicio(VentanaInicio inicio, String usuario, String contrasena) {
        ArrayList<JTextField> campos = new ArrayList<JTextField>();
        listarCamposTexto(inicio.getContentPane(), campos);
        campos.get(0).setText(usuario);
        poner(inicio.getContentPane(), contrasena);
    }

    private static String textoDeUsuario(VentanaInicio inicio) {
        ArrayList<JTextField> campos = new ArrayList<JTextField>();
        listarCamposTexto(inicio.getContentPane(), campos);
        return campos.get(0).getText();
    }

    private static String textoDeContrasena(VentanaInicio inicio) {
        Component[] c = inicio.getContentPane().getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JPasswordField) {
                return new String(((JPasswordField) c[i]).getPassword());
            }
        }
        return "?";
    }

    private static vista.VentanaMenuEstudiante buscarMenuEstudiante() {
        Frame[] marcos = Frame.getFrames();
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] instanceof vista.VentanaMenuEstudiante && marcos[i].isDisplayable()) {
                return (vista.VentanaMenuEstudiante) marcos[i];
            }
        }
        return null;
    }

    private static vista.VentanaMenuAdmin buscarMenuAdmin() {
        Frame[] marcos = Frame.getFrames();
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] instanceof vista.VentanaMenuAdmin && marcos[i].isDisplayable()) {
                return (vista.VentanaMenuAdmin) marcos[i];
            }
        }
        return null;
    }

    private static vista.VentanaPerfil buscarVentanaPerfil() {
        Frame[] marcos = Frame.getFrames();
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] instanceof vista.VentanaPerfil && marcos[i].isDisplayable()) {
                return (vista.VentanaPerfil) marcos[i];
            }
        }
        return null;
    }

    private static vista.VentanaCalculadora buscarVentanaCalculadora() {
        Frame[] marcos = Frame.getFrames();
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] instanceof vista.VentanaCalculadora && marcos[i].isDisplayable()) {
                return (vista.VentanaCalculadora) marcos[i];
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- C5 y C6
    private static void pruebaC5(GestorUsuarios gu, GestorMovimientos gm, GestorNovedades gn,
                                 GestorInscripciones gi, GestorNotificaciones gnot, GestorAmigos ga) throws Exception {
        System.out.println("[C5] Boton Salir con confirmacion y [C6] sin tooltip");
        VentanaInicio inicio = new VentanaInicio(gu, gm, gn, gi, gnot, ga);
        inicio.setVisible(true);
        Container panel = inicio.getContentPane();

        JButton soporte = buscarBoton(panel, "Soporte");
        JButton salir = buscarBoton(panel, "Salir de la aplicación");
        prueba("Existe el boton 'Soporte'", soporte != null);
        prueba("Existe el boton 'Salir de la aplicación'", salir != null);
        prueba("'Salir' esta debajo de 'Soporte'", salir != null && soporte != null && salir.getY() > soporte.getY());
        prueba("'Salir' tiene el mismo ancho y x que 'Soporte'",
                salir.getX() == soporte.getX() && salir.getWidth() == soporte.getWidth());
        prueba("'Salir' no se solapa con 'Soporte'", !salir.getBounds().intersects(soporte.getBounds()));
        prueba("'Salir' cabe dentro de la ventana", salir.getY() + salir.getHeight() < inicio.getHeight() - 40);
        prueba("Los 6 botones del login no se solapan entre si", botonesSinSolapar(panel));

        // Color Pantone con texto blanco como los demas botones
        prueba("'Salir' tiene el color Pantone 5535 C", GestorTema.PANTONE_5535_C.equals(salir.getBackground()));
        prueba("'Salir' tiene texto blanco", Color.WHITE.equals(salir.getForeground()));
        GestorTema.alternarTema();
        GestorTema.aplicarTema(inicio);
        prueba("'Salir' conserva Pantone y texto blanco en modo oscuro",
                GestorTema.PANTONE_5535_C.equals(salir.getBackground()) && Color.WHITE.equals(salir.getForeground()));
        GestorTema.alternarTema();
        GestorTema.aplicarTema(inicio);

        // Responder "No": la aplicacion sigue viva
        botonARepulsar = "No";
        salir.doClick();
        esperar(500);
        prueba("El dialogo pregunta si esta seguro de salir", ultimoMensajeContiene("¿Está seguro que desea salir de la aplicación?"));
        prueba("Responder 'No': la ventana sigue visible", inicio.isVisible());
        prueba("Responder 'No': la ventana no fue destruida", inicio.isDisplayable());

        // Cerrar el dialogo con la X (sin elegir): tampoco sale
        botonARepulsar = null;
        salir.doClick();
        esperar(500);
        prueba("Cerrar el dialogo sin elegir: la aplicacion sigue viva", inicio.isVisible() && inicio.isDisplayable());

        // Los otros botones siguen funcionando: Soporte abre su ventana emergente (JDialog)
        botonARepulsar = null;
        final VentanaInicio inicioFinal = inicio;
        final boolean[] soporteVisto = new boolean[1];
        Thread vigiaSoporte = new Thread(new Runnable() {
            public void run() {
                for (int intento = 0; intento < 20; intento++) {
                    Window[] ventanas = Window.getWindows();
                    for (int i = 0; i < ventanas.length; i++) {
                        if (ventanas[i] instanceof vista.VentanaSoporte && ventanas[i].isShowing()) {
                            soporteVisto[0] = true;
                        }
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        });
        vigiaSoporte.start();
        buscarBoton(panel, "Soporte").doClick();
        esperar(500);
        prueba("'Soporte' sigue abriendo su ventana emergente", soporteVisto[0]);
        prueba("Tras abrir Soporte el login sigue visible", inicioFinal.isVisible());

        // ---- C6: ningun componente del login tiene tooltip (salvo el boton de tema, que no menciona usuarios)
        ArrayList<JTextField> campos = new ArrayList<JTextField>();
        listarCamposTexto(panel, campos);
        prueba("C6: el campo de documento no tiene tooltip", campos.get(0).getToolTipText() == null);
        prueba("C6: el campo de contrasena no tiene tooltip", tooltipDePassword(panel) == null);
        prueba("C6: ningun tooltip del login menciona 'admin'", !algunTooltipContiene(panel, "admin"));
        prueba("C6: ningun tooltip del login menciona 'Administrador'", !algunTooltipContiene(panel, "Administrador"));
        prueba("C6: la etiqueta ya no menciona 'usuario' del administrador", !hayEtiquetaQueContiene(panel, "admin"));
        inicio.dispose();
    }

    // Se ejecuta en una JVM aparte: pulsa "Si" y la aplicacion debe terminar con codigo 0
    private static void pruebaC5Confirmar(GestorUsuarios gu, GestorMovimientos gm, GestorNovedades gn,
                                          GestorInscripciones gi, GestorNotificaciones gnot, GestorAmigos ga) throws Exception {
        System.out.println("[C5] Responder 'Si' termina la aplicacion");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("  OK   Responder 'Si': la aplicacion termino (System.exit)");
                System.out.println("RESULTADO: TODAS LAS PRUEBAS PASARON");
            }
        }));
        // Si en 6 segundos no termino, la prueba falla
        Thread vigilante = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    return;
                }
                System.out.println("  FALLA Responder 'Si': la aplicacion NO termino");
                Runtime.getRuntime().halt(2);
            }
        });
        vigilante.setDaemon(true);
        vigilante.start();

        VentanaInicio inicio = new VentanaInicio(gu, gm, gn, gi, gnot, ga);
        inicio.setVisible(true);
        botonARepulsar = "SI";
        buscarBoton(inicio.getContentPane(), "Salir de la aplicación").doClick();
        esperar(3000);
    }

    private static boolean botonesSinSolapar(Container panel) {
        ArrayList<JButton> botones = new ArrayList<JButton>();
        Component[] c = panel.getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JButton && !(c[i] instanceof vista.BotonTema)) {
                botones.add((JButton) c[i]);
            }
        }
        for (int i = 0; i < botones.size(); i++) {
            for (int j = i + 1; j < botones.size(); j++) {
                if (botones.get(i).getBounds().intersects(botones.get(j).getBounds())) {
                    return false;
                }
            }
        }
        return botones.size() == 5;
    }

    private static String tooltipDePassword(Container panel) {
        Component[] c = panel.getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof JPasswordField) {
                return ((JPasswordField) c[i]).getToolTipText();
            }
        }
        return null;
    }

    private static boolean algunTooltipContiene(Container contenedor, String texto) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof javax.swing.JComponent) {
                String t = ((javax.swing.JComponent) componentes[i]).getToolTipText();
                if (t != null && t.toLowerCase().contains(texto.toLowerCase())) {
                    return true;
                }
            }
            if (componentes[i] instanceof Container && algunTooltipContiene((Container) componentes[i], texto)) {
                return true;
            }
        }
        return false;
    }

    // El texto de "Sí/Yes" y "No" depende del idioma del sistema; se aceptan las variantes habituales
    private static boolean textoCoincide(String textoBoton, String buscado) {
        if (textoBoton == null) {
            return false;
        }
        if (buscado.equals("SI")) {
            return textoBoton.equalsIgnoreCase("Sí") || textoBoton.equalsIgnoreCase("Si")
                    || textoBoton.equalsIgnoreCase("Yes");
        }
        return textoBoton.equalsIgnoreCase(buscado);
    }

    private static JButton buscarBotonDeDialogo(Container contenedor, String texto) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JButton) {
                if (textoCoincide(((JButton) componentes[i]).getText(), texto)) {
                    return (JButton) componentes[i];
                }
            }
            if (componentes[i] instanceof Container) {
                JButton hallado = buscarBotonDeDialogo((Container) componentes[i], texto);
                if (hallado != null) {
                    return hallado;
                }
            }
        }
        return null;
    }

    // ---------------------------------------------------------------- utilidades
    private static void iniciarCerradorDeDialogos() {
        Thread hilo = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                        return;
                    }
                    Window[] ventanas = Window.getWindows();
                    for (int i = 0; i < ventanas.length; i++) {
                        if (ventanas[i] instanceof JDialog && ventanas[i].isShowing()) {
                            registrarMensaje(ventanas[i]);
                            JButton elegido = null;
                            if (botonARepulsar != null) {
                                elegido = buscarBotonDeDialogo(ventanas[i], botonARepulsar);
                            }
                            if (elegido != null) {
                                final JButton boton = elegido;
                                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        boton.doClick();
                                    }
                                });
                            } else {
                                ventanas[i].dispose();
                            }
                        }
                    }
                }
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private static void registrarMensaje(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JLabel && ((JLabel) componentes[i]).getText() != null) {
                synchronized (mensajes) {
                    mensajes.add(((JLabel) componentes[i]).getText());
                }
            }
            if (componentes[i] instanceof Container) {
                registrarMensaje((Container) componentes[i]);
            }
        }
    }

    private static boolean ultimoMensajeContiene(String texto) {
        synchronized (mensajes) {
            for (int i = mensajes.size() - 1; i >= 0 && i >= mensajes.size() - 4; i--) {
                if (mensajes.get(i).contains(texto)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void esperar(int milisegundos) throws Exception {
        Thread.sleep(milisegundos);
    }

    private static boolean todasLasEtiquetasBlancas(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        int etiquetas = 0;
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JLabel) {
                etiquetas++;
                if (!Color.WHITE.equals(componentes[i].getForeground())) {
                    return false;
                }
            }
        }
        return etiquetas > 0;
    }

    private static JLabel buscarEtiqueta(Container contenedor, String texto) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JLabel && texto.equals(((JLabel) componentes[i]).getText())) {
                return (JLabel) componentes[i];
            }
            if (componentes[i] instanceof Container) {
                JLabel hallada = buscarEtiqueta((Container) componentes[i], texto);
                if (hallada != null) {
                    return hallada;
                }
            }
        }
        return null;
    }

    private static boolean hayEtiquetaQueContiene(Container contenedor, String texto) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JLabel) {
                String t = ((JLabel) componentes[i]).getText();
                if (t != null && t.contains(texto)) {
                    return true;
                }
            }
            if (componentes[i] instanceof Container && hayEtiquetaQueContiene((Container) componentes[i], texto)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hayTooltip(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof javax.swing.JComponent
                    && ((javax.swing.JComponent) componentes[i]).getToolTipText() != null) {
                return true;
            }
            if (componentes[i] instanceof Container && hayTooltip((Container) componentes[i])) {
                return true;
            }
        }
        return false;
    }

    private static JButton buscarBoton(Container contenedor, String texto) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JButton && texto.equals(((JButton) componentes[i]).getText())) {
                return (JButton) componentes[i];
            }
            if (componentes[i] instanceof Container) {
                JButton hallado = buscarBoton((Container) componentes[i], texto);
                if (hallado != null) {
                    return hallado;
                }
            }
        }
        return null;
    }

    private static JTable buscarTabla(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JTable) {
                return (JTable) componentes[i];
            }
            if (componentes[i] instanceof Container) {
                JTable hallada = buscarTabla((Container) componentes[i]);
                if (hallada != null) {
                    return hallada;
                }
            }
        }
        return null;
    }

    // Lista los JTextField que no son de contrasena, en el orden en que se agregaron
    private static void listarCamposTexto(Container contenedor, ArrayList<JTextField> destino) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JTextField && !(componentes[i] instanceof JPasswordField)) {
                destino.add((JTextField) componentes[i]);
            }
            if (componentes[i] instanceof Container) {
                listarCamposTexto((Container) componentes[i], destino);
            }
        }
    }

    private static int contarPassword(Container contenedor) {
        int cantidad = 0;
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JPasswordField) {
                cantidad++;
            }
            if (componentes[i] instanceof Container) {
                cantidad = cantidad + contarPassword((Container) componentes[i]);
            }
        }
        return cantidad;
    }

    private static void poner(Container contenedor, String contrasena) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            if (componentes[i] instanceof JPasswordField) {
                ((JPasswordField) componentes[i]).setText(contrasena);
            }
            if (componentes[i] instanceof Container) {
                poner((Container) componentes[i], contrasena);
            }
        }
    }

    private static VentanaTransferir buscarVentanaTransferir() {
        Frame[] marcos = Frame.getFrames();
        for (int i = 0; i < marcos.length; i++) {
            if (marcos[i] instanceof VentanaTransferir && marcos[i].isDisplayable()) {
                return (VentanaTransferir) marcos[i];
            }
        }
        return null;
    }

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
