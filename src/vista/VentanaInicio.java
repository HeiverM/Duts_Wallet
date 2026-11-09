// Ventana inicial de ingreso al sistema con fondo de imagen, tema y opciones de login, registro, recuperación y soporte.
package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import util.GestorTema;

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
    private BotonTema btnTema;

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
        
        setSize(1280, 720);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        
        crearComponentes();
        GestorTema.aplicarTema(this);
    }

    // Crea el fondo con imagen, el formulario a la izquierda y el botón de tema
    private void crearComponentes() {
        // Panel de fondo con la imagen
        PanelFondoLogin panelFondo = new PanelFondoLogin();
        setContentPane(panelFondo);
        panelFondo.setLayout(null);

        // Panel del formulario (izquierda, sobre el overlay del fondo)
        int anchoFormulario = 420;
        int altoFormulario = 520;
        int xFormulario = 40;
        int yFormulario = (720 - altoFormulario) / 2;

        // Título
        JLabel lblTitulo = new JLabel("DUTS Wallet", JLabel.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(28f));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(xFormulario, yFormulario, anchoFormulario, 40);
        panelFondo.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Sistema de gestión de DUTS", JLabel.CENTER);
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setBounds(xFormulario, yFormulario + 45, anchoFormulario, 25);
        panelFondo.add(lblSubtitulo);

        // Campo de acceso: los estudiantes usan su documento y el administrador su nombre de usuario
        JLabel lblUsuario = new JLabel("Documento o usuario:");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setBounds(xFormulario + 30, yFormulario + 100, 360, 25);
        panelFondo.add(lblUsuario);

        txtUsuario = new JTextField(18);
        txtUsuario.setBounds(xFormulario + 30, yFormulario + 130, 360, 30);
        txtUsuario.setToolTipText("Estudiantes: su número de documento. Administrador: admin");
        panelFondo.add(txtUsuario);

        // Campo Contraseña
        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setForeground(Color.WHITE);
        lblContrasena.setBounds(xFormulario + 30, yFormulario + 180, 360, 25);
        panelFondo.add(lblContrasena);

        txtContrasena = new JPasswordField(18);
        txtContrasena.setBounds(xFormulario + 30, yFormulario + 210, 360, 30);
        panelFondo.add(txtContrasena);

        // Botones principales
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(xFormulario + 30, yFormulario + 270, 170, 35);
        btnIngresar.addActionListener(this);
        panelFondo.add(btnIngresar);

        btnRegistrarse = new JButton("Registrarse");
        btnRegistrarse.setBounds(xFormulario + 220, yFormulario + 270, 170, 35);
        btnRegistrarse.addActionListener(this);
        panelFondo.add(btnRegistrarse);

        // Botones secundarios
        btnRecuperar = new JButton("Recuperar contraseña");
        btnRecuperar.setBounds(xFormulario + 30, yFormulario + 330, 360, 32);
        btnRecuperar.addActionListener(this);
        panelFondo.add(btnRecuperar);

        btnSoporte = new JButton("Soporte");
        btnSoporte.setBounds(xFormulario + 30, yFormulario + 380, 360, 32);
        btnSoporte.addActionListener(this);
        panelFondo.add(btnSoporte);

        // Botón de tema (esquina superior derecha)
        btnTema = new BotonTema();
        btnTema.setBounds(1130, 20, 130, 32);
        panelFondo.add(btnTema);
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
            GestorTema.aplicarTema(ventana);
            ventana.setVisible(true);
            dispose();
        } else if (usuario.getRol().equals("ADMINISTRADOR")) {
            VentanaMenuAdmin ventana = new VentanaMenuAdmin((Administrador) usuario, gestorUsuarios,
                    gestorMovimientos, gestorNovedades, gestorInscripciones);
            GestorTema.aplicarTema(ventana);
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
                GestorTema.aplicarTema(ventana);
                ventana.setVisible(true);
            } else if (e.getSource() == btnRecuperar) {
                VentanaRecuperacion ventana = new VentanaRecuperacion(gestorUsuarios);
                GestorTema.aplicarTema(ventana);
                ventana.setVisible(true);
            } else if (e.getSource() == btnSoporte) {
                VentanaSoporte ventana = new VentanaSoporte(this);
                GestorTema.aplicarTema(ventana);
                ventana.setVisible(true);
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(txtUsuario.getText().trim(), "ERROR", "Login: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
