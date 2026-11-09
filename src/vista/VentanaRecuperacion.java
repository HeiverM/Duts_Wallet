// Ventana para recuperar la contraseña respondiendo la pregunta de seguridad.
package vista;

import excepciones.ValidacionException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.Usuario;
import persistencia.GestorAuditoria;
import persistencia.GestorUsuarios;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaRecuperacion extends JFrame implements ActionListener {

    private GestorUsuarios gestorUsuarios;

    // Nombre del usuario encontrado en el paso 1 (vacío mientras no se haya buscado)
    private String usuarioBuscado;

    private JTextField txtUsuario;
    private JButton btnBuscar;
    private JLabel lblPregunta;
    private JTextField txtRespuesta;
    private JPasswordField txtNuevaContrasena;
    private JButton btnCambiar;
    private JButton btnCancelar;

    public VentanaRecuperacion(GestorUsuarios gestorUsuarios) {
        super("DUTS Wallet - Recuperar contraseña");
        this.gestorUsuarios = gestorUsuarios;
        this.usuarioBuscado = "";
        setSize(470, 320);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearPasoBuscar();
        crearPasoCambiar();
    }

    // Paso 1: escribir el documento o el usuario y buscar su pregunta de seguridad
    private void crearPasoBuscar() {
        agregarEtiqueta("Documento o usuario:", 30);
        txtUsuario = new JTextField();
        txtUsuario.setBounds(170, 30, 150, 25);
        add(txtUsuario);
        btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(330, 30, 100, 25);
        btnBuscar.addActionListener(this);
        add(btnBuscar);
        agregarEtiqueta("Pregunta:", 75);
        lblPregunta = new JLabel("(primero busque su documento o usuario)");
        lblPregunta.setBounds(170, 75, 260, 25);
        add(lblPregunta);
    }

    // Paso 2: responder la pregunta y escribir la contraseña nueva (bloqueado hasta buscar)
    private void crearPasoCambiar() {
        agregarEtiqueta("Respuesta:", 115);
        txtRespuesta = new JTextField();
        txtRespuesta.setBounds(170, 115, 260, 25);
        txtRespuesta.setEnabled(false);
        add(txtRespuesta);
        agregarEtiqueta("Nueva contraseña:", 155);
        txtNuevaContrasena = new JPasswordField();
        txtNuevaContrasena.setBounds(170, 155, 260, 25);
        txtNuevaContrasena.setEnabled(false);
        add(txtNuevaContrasena);
        btnCambiar = new JButton("Cambiar contraseña");
        btnCambiar.setBounds(70, 215, 170, 30);
        btnCambiar.setEnabled(false);
        btnCambiar.addActionListener(this);
        add(btnCambiar);
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(260, 215, 130, 30);
        btnCancelar.addActionListener(this);
        add(btnCancelar);
    }

    private void agregarEtiqueta(String texto, int y) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setBounds(30, y, 140, 25);
        add(etiqueta);
    }

    // Busca el usuario por documento o por nombre de usuario, muestra su pregunta y habilita el paso 2
    private void buscarUsuario() throws ValidacionException {
        Validador.validarNoVacio(txtUsuario.getText(), "Documento o usuario");
        String dato = txtUsuario.getText().trim();
        // El estudiante puede buscar con su documento; el administrador con su nombre de usuario
        Usuario usuario = gestorUsuarios.buscarPorDocumento(dato);
        if (usuario == null) {
            usuario = gestorUsuarios.buscarPorNombreUsuario(dato);
        }
        if (usuario == null) {
            throw new ValidacionException("El usuario no existe");
        }
        usuarioBuscado = usuario.getNombreUsuario();
        lblPregunta.setText(usuario.getPreguntaSeguridad());
        txtUsuario.setEditable(false);
        btnBuscar.setEnabled(false);
        txtRespuesta.setEnabled(true);
        txtNuevaContrasena.setEnabled(true);
        btnCambiar.setEnabled(true);
    }

    // Verifica la respuesta y guarda la contraseña nueva
    private void cambiarContrasena() throws ValidacionException {
        Validador.validarNoVacio(txtRespuesta.getText(), "Respuesta");
        String nuevaContrasena = new String(txtNuevaContrasena.getPassword());
        gestorUsuarios.restablecerContrasena(usuarioBuscado, txtRespuesta.getText(), nuevaContrasena);
        GestorAuditoria.registrar(usuarioBuscado, "RECUPERAR_CONTRASENA", "Contraseña restablecida");
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnBuscar) {
                buscarUsuario();
            } else if (e.getSource() == btnCambiar) {
                cambiarContrasena();
                JOptionPane.showMessageDialog(this, "Contraseña actualizada. Ya puede iniciar sesión");
                dispose();
            } else if (e.getSource() == btnCancelar) {
                dispose();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(txtUsuario.getText().trim(), "ERROR", "Recuperación: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
