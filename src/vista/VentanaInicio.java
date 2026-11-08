// Ventana inicial de ingreso al sistema con opciones de login, registro, recuperación y soporte.
package vista;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import excepciones.ValidacionException;
import modelo.Administrador;
import modelo.Estudiante;
import modelo.Usuario;
import persistencia.GestorAuditoria;
import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaInicio extends JFrame implements ActionListener {

    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;
    private GestorNovedades gestorNovedades;
    private GestorInscripciones gestorInscripciones;
    private GestorNotificaciones gestorNotificaciones;
    private GestorAmigos gestorAmigos;

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnRegistrarse;
    private JButton btnRecuperar;
    private JButton btnSoporte;

    public VentanaInicio(GestorUsuarios gestorUsuarios, GestorMovimientos gestorMovimientos,
                         GestorNovedades gestorNovedades, GestorInscripciones gestorInscripciones,
                         GestorNotificaciones gestorNotificaciones, GestorAmigos gestorAmigos) {
        super("DUTS Wallet - Bienvenido/a");
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNovedades = gestorNovedades;
        this.gestorInscripciones = gestorInscripciones;
        this.gestorNotificaciones = gestorNotificaciones;
        this.gestorAmigos = gestorAmigos;
        setSize(420, 240);
        setLayout(new GridLayout(5, 1, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearComponentes();
    }

    // Crea el título de bienvenida, los campos de texto y los botones
    private void crearComponentes() {
        JLabel lblTitulo = new JLabel("DUTS Wallet - Sistema de gestión de DUTS", JLabel.CENTER);
        add(lblTitulo);

        JPanel panelUsuario = new JPanel(new FlowLayout());
        panelUsuario.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField(18);
        panelUsuario.add(txtUsuario);
        add(panelUsuario);

        JPanel panelContrasena = new JPanel(new FlowLayout());
        panelContrasena.add(new JLabel("Contraseña:"));
        txtContrasena = new JPasswordField(18);
        panelContrasena.add(txtContrasena);
        add(panelContrasena);

        JPanel panelBotones1 = new JPanel(new FlowLayout());
        btnIngresar = new JButton("Ingresar");
        btnIngresar.addActionListener(this);
        panelBotones1.add(btnIngresar);
        btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.addActionListener(this);
        panelBotones1.add(btnRegistrarse);
        add(panelBotones1);

        JPanel panelBotones2 = new JPanel(new FlowLayout());
        btnRecuperar = new JButton("Recuperar contraseña");
        btnRecuperar.addActionListener(this);
        panelBotones2.add(btnRecuperar);
        btnSoporte = new JButton("Soporte");
        btnSoporte.addActionListener(this);
        panelBotones2.add(btnSoporte);
        add(panelBotones2);
    }

    // Inicia sesión y abre el menú correspondiente según el rol del usuario
    private void iniciarSesion() throws ValidacionException {
        String nombreUsuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        Usuario usuario = gestorUsuarios.iniciarSesion(nombreUsuario, contrasena);
        GestorAuditoria.registrar(usuario.getNombreUsuario(), "LOGIN", usuario.getRol());

        // Aquí se aplica polimorfismo: usuario puede ser Estudiante o Administrador
        // y cada uno implementa getRol() de forma diferente
        if (usuario.getRol().equals("ESTUDIANTE")) {
            VentanaMenuEstudiante ventana = new VentanaMenuEstudiante((Estudiante) usuario, gestorUsuarios,
                    gestorMovimientos, gestorNovedades, gestorInscripciones, gestorNotificaciones, gestorAmigos);
            ventana.setVisible(true);
            dispose();
        } else if (usuario.getRol().equals("ADMINISTRADOR")) {
            VentanaMenuAdmin ventana = new VentanaMenuAdmin((Administrador) usuario, gestorUsuarios,
                    gestorMovimientos, gestorNovedades, gestorInscripciones);
            ventana.setVisible(true);
            dispose();
        }
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnIngresar) {
                iniciarSesion();
            } else if (e.getSource() == btnRegistrarse) {
                VentanaRegistro ventana = new VentanaRegistro(gestorUsuarios);
                ventana.setVisible(true);
            } else if (e.getSource() == btnRecuperar) {
                VentanaRecuperacion ventana = new VentanaRecuperacion(gestorUsuarios);
                ventana.setVisible(true);
            } else if (e.getSource() == btnSoporte) {
                VentanaSoporte ventana = new VentanaSoporte(this);
                ventana.setVisible(true);
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(txtUsuario.getText().trim(), "ERROR", "Login: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
