// Menú principal del estudiante con acceso a todos los módulos disponibles.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modelo.Estudiante;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.GestorTema;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaMenuEstudiante extends JFrame implements ActionListener {

    private Estudiante estudiante;
    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;
    private GestorNovedades gestorNovedades;
    private GestorInscripciones gestorInscripciones;
    private GestorNotificaciones gestorNotificaciones;
    private GestorAmigos gestorAmigos;
    private VentanaInicio ventanaInicio; // Ventana a la que se vuelve al cerrar sesión

    private JButton btnPerfil;
    private JButton btnTransferir;
    private JButton btnHistorial;
    private JButton btnEstadisticas;
    private JButton btnDashboard;
    private JButton btnNovedades;
    private JButton btnNotificaciones;
    private JButton btnAmigos;
    private JButton btnCalculadora;
    private JButton btnSalir;

    public VentanaMenuEstudiante(Estudiante estudiante, GestorUsuarios gestorUsuarios,
                                 GestorMovimientos gestorMovimientos, GestorNovedades gestorNovedades,
                                 GestorInscripciones gestorInscripciones, GestorNotificaciones gestorNotificaciones,
                                 GestorAmigos gestorAmigos, VentanaInicio ventanaInicio) {
        super("DUTS Wallet - Menú estudiante");
        this.estudiante = estudiante;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNovedades = gestorNovedades;
        this.gestorInscripciones = gestorInscripciones;
        this.gestorNotificaciones = gestorNotificaciones;
        this.gestorAmigos = gestorAmigos;
        this.ventanaInicio = ventanaInicio;
        setSize(500, 580);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearComponentes();
        generarNotificacionesProximidad();
    }

    // Crea la cabecera con bienvenida y botón de tema, y la cuadrícula de botones de módulos
    private void crearComponentes() {
        // Panel superior: bienvenida + BotonTema (esquina superior derecha)
        JPanel panelCabecera = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Bienvenido/a " + estudiante.getNombreCompleto(), JLabel.LEFT);
        JPanel panelBienvenida = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBienvenida.add(lblBienvenida);
        panelCabecera.add(panelBienvenida, BorderLayout.CENTER);
        JPanel panelBotonTema = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonTema.add(new BotonTema());
        panelCabecera.add(panelBotonTema, BorderLayout.EAST);
        add(panelCabecera, BorderLayout.NORTH);

        // Panel central: cuadrícula de botones (11 filas = 10 módulos + calculadora)
        JPanel panelBotones = new JPanel(new GridLayout(11, 1, 8, 8));

        btnPerfil = new JButton("Mi perfil y meta de grado");
        btnPerfil.addActionListener(this);
        panelBotones.add(btnPerfil);

        btnTransferir = new JButton("Transferir DUTS");
        btnTransferir.addActionListener(this);
        panelBotones.add(btnTransferir);

        btnHistorial = new JButton("Historial de movimientos");
        btnHistorial.addActionListener(this);
        panelBotones.add(btnHistorial);

        btnEstadisticas = new JButton("Estadísticas y promedios");
        btnEstadisticas.addActionListener(this);
        panelBotones.add(btnEstadisticas);

        btnDashboard = new JButton("Dashboard interactivo");
        btnDashboard.addActionListener(this);
        panelBotones.add(btnDashboard);

        btnNovedades = new JButton("Novedades institucionales");
        btnNovedades.addActionListener(this);
        panelBotones.add(btnNovedades);

        btnNotificaciones = new JButton("Bandeja de notificaciones");
        btnNotificaciones.addActionListener(this);
        panelBotones.add(btnNotificaciones);

        btnAmigos = new JButton("Mis amigos");
        btnAmigos.addActionListener(this);
        panelBotones.add(btnAmigos);

        btnCalculadora = new JButton("Calculadora de DUTS");
        btnCalculadora.addActionListener(this);
        panelBotones.add(btnCalculadora);

        btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(this);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.CENTER);
    }

    // Genera notificaciones de proximidad de eventos al abrir el menú
    private void generarNotificacionesProximidad() {
        gestorNotificaciones.generarProximidad(estudiante.getDocumento(),
                gestorInscripciones.listarPorEstudiante(estudiante.getDocumento()), gestorNovedades);
    }

    // Abre una ventana hija y aplica el tema activo antes de mostrarla
    private void abrirConTema(JFrame ventana) {
        GestorTema.aplicarTema(ventana);
        ventana.setVisible(true);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPerfil) {
            abrirConTema(new VentanaPerfil(estudiante));
        } else if (e.getSource() == btnTransferir) {
            abrirConTema(new VentanaTransferir(estudiante, gestorUsuarios, gestorMovimientos,
                    gestorNotificaciones, ""));
        } else if (e.getSource() == btnHistorial) {
            abrirConTema(new VentanaHistorial(estudiante, gestorMovimientos));
        } else if (e.getSource() == btnEstadisticas) {
            abrirConTema(new VentanaEstadisticas(estudiante, gestorMovimientos));
        } else if (e.getSource() == btnDashboard) {
            abrirConTema(new VentanaDashboard(estudiante, gestorMovimientos));
        } else if (e.getSource() == btnNovedades) {
            abrirConTema(new VentanaNovedades(estudiante, gestorNovedades, gestorInscripciones));
        } else if (e.getSource() == btnNotificaciones) {
            abrirConTema(new VentanaNotificaciones(estudiante, gestorNotificaciones));
        } else if (e.getSource() == btnAmigos) {
            abrirConTema(new VentanaAmigos(estudiante, gestorAmigos, gestorUsuarios,
                    gestorMovimientos, gestorNotificaciones));
        } else if (e.getSource() == btnCalculadora) {
            abrirConTema(new VentanaCalculadora(estudiante, gestorMovimientos, gestorNovedades, null));
        } else if (e.getSource() == btnSalir) {
            // Cerrar sesión: la ventana de inicio cierra las demás ventanas y vuelve a mostrarse
            ventanaInicio.volverAlInicio();
        }
    }
}
