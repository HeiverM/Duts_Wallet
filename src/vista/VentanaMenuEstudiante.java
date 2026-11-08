// Menú principal del estudiante con acceso a todos los módulos disponibles.
package vista;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modelo.Estudiante;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaMenuEstudiante extends JFrame implements ActionListener {

    private Estudiante estudiante;
    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;
    private GestorNovedades gestorNovedades;
    private GestorInscripciones gestorInscripciones;
    private GestorNotificaciones gestorNotificaciones;
    private GestorAmigos gestorAmigos;

    private JButton btnPerfil;
    private JButton btnTransferir;
    private JButton btnHistorial;
    private JButton btnEstadisticas;
    private JButton btnDashboard;
    private JButton btnNovedades;
    private JButton btnNotificaciones;
    private JButton btnAmigos;
    private JButton btnSalir;

    public VentanaMenuEstudiante(Estudiante estudiante, GestorUsuarios gestorUsuarios,
                                 GestorMovimientos gestorMovimientos, GestorNovedades gestorNovedades,
                                 GestorInscripciones gestorInscripciones, GestorNotificaciones gestorNotificaciones,
                                 GestorAmigos gestorAmigos) {
        super("DUTS Wallet - Menú estudiante");
        this.estudiante = estudiante;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNovedades = gestorNovedades;
        this.gestorInscripciones = gestorInscripciones;
        this.gestorNotificaciones = gestorNotificaciones;
        this.gestorAmigos = gestorAmigos;
        setSize(500, 450);
        setLayout(new GridLayout(10, 1, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearComponentes();
        generarNotificacionesProximidad();
    }

    // Crea el título de bienvenida y todos los botones del menú
    private void crearComponentes() {
        JLabel lblBienvenida = new JLabel("Bienvenido/a " + estudiante.getNombreCompleto(), JLabel.CENTER);
        add(lblBienvenida);

        btnPerfil = new JButton("Mi perfil y meta de grado");
        btnPerfil.addActionListener(this);
        add(btnPerfil);

        btnTransferir = new JButton("Transferir DUTS");
        btnTransferir.addActionListener(this);
        add(btnTransferir);

        btnHistorial = new JButton("Historial de movimientos");
        btnHistorial.addActionListener(this);
        add(btnHistorial);

        btnEstadisticas = new JButton("Estadísticas y promedios");
        btnEstadisticas.addActionListener(this);
        add(btnEstadisticas);

        btnDashboard = new JButton("Dashboard interactivo");
        btnDashboard.addActionListener(this);
        add(btnDashboard);

        btnNovedades = new JButton("Novedades institucionales");
        btnNovedades.addActionListener(this);
        add(btnNovedades);

        btnNotificaciones = new JButton("Bandeja de notificaciones");
        btnNotificaciones.addActionListener(this);
        add(btnNotificaciones);

        btnAmigos = new JButton("Mis amigos");
        btnAmigos.addActionListener(this);
        add(btnAmigos);

        btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(this);
        add(btnSalir);
    }

    // Genera notificaciones de proximidad de eventos al abrir el menú
    private void generarNotificacionesProximidad() {
        gestorNotificaciones.generarProximidad(estudiante.getDocumento(),
                gestorInscripciones.listarPorEstudiante(estudiante.getDocumento()), gestorNovedades);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPerfil) {
            VentanaPerfil ventana = new VentanaPerfil(estudiante);
            ventana.setVisible(true);
        } else if (e.getSource() == btnTransferir) {
            VentanaTransferir ventana = new VentanaTransferir(estudiante, gestorUsuarios, gestorMovimientos,
                    gestorNotificaciones, "");
            ventana.setVisible(true);
        } else if (e.getSource() == btnHistorial) {
            VentanaHistorial ventana = new VentanaHistorial(estudiante, gestorMovimientos);
            ventana.setVisible(true);
        } else if (e.getSource() == btnEstadisticas) {
            VentanaEstadisticas ventana = new VentanaEstadisticas(estudiante, gestorMovimientos);
            ventana.setVisible(true);
        } else if (e.getSource() == btnDashboard) {
            VentanaDashboard ventana = new VentanaDashboard(estudiante, gestorMovimientos);
            ventana.setVisible(true);
        } else if (e.getSource() == btnNovedades) {
            VentanaNovedades ventana = new VentanaNovedades(estudiante, gestorNovedades, gestorInscripciones);
            ventana.setVisible(true);
        } else if (e.getSource() == btnNotificaciones) {
            VentanaNotificaciones ventana = new VentanaNotificaciones(estudiante, gestorNotificaciones);
            ventana.setVisible(true);
        } else if (e.getSource() == btnAmigos) {
            VentanaAmigos ventana = new VentanaAmigos(estudiante, gestorAmigos, gestorUsuarios,
                    gestorMovimientos, gestorNotificaciones);
            ventana.setVisible(true);
        } else if (e.getSource() == btnSalir) {
            dispose();
        }
    }
}
