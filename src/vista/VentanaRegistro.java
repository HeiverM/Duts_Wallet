// Ventana con el formulario para que un estudiante nuevo se registre en DUTS Wallet.
package vista;

import excepciones.ValidacionException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import modelo.Estudiante;
import persistencia.GestorAuditoria;
import persistencia.GestorUsuarios;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaRegistro extends JFrame implements ActionListener {

    // Lista fija de preguntas de seguridad que se muestran en el JComboBox
    private static final String[] PREGUNTAS = {"¿Nombre de su primera mascota?", "¿Ciudad donde nació?",
        "¿Nombre de su colegio?", "¿Comida favorita?"};

    private GestorUsuarios gestorUsuarios;

    private JTextField txtDocumento;
    private JTextField txtNombre;
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JTextField txtPrograma;
    private JComboBox<String> cmbPregunta;
    private JTextField txtRespuesta;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public VentanaRegistro(GestorUsuarios gestorUsuarios) {
        super("DUTS Wallet - Registro de estudiante");
        this.gestorUsuarios = gestorUsuarios;
        setSize(450, 430);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearCampos();
        crearBotones();
    }

    // Crea las etiquetas y los campos del formulario (uno por fila)
    private void crearCampos() {
        agregarEtiqueta("Documento:", 30);
        txtDocumento = agregarCampoTexto(30);
        agregarEtiqueta("Nombre completo:", 70);
        txtNombre = agregarCampoTexto(70);
        agregarEtiqueta("Usuario (alias):", 110);
        txtUsuario = agregarCampoTexto(110);
        txtUsuario.setToolTipText("Alias de la cuenta. Para iniciar sesión se usa el número de documento");
        agregarEtiqueta("Contraseña:", 150);
        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(190, 150, 210, 25);
        add(txtContrasena);
        agregarEtiqueta("Programa académico:", 190);
        txtPrograma = agregarCampoTexto(190);
        agregarEtiqueta("Pregunta de seguridad:", 230);
        cmbPregunta = new JComboBox<String>(PREGUNTAS);
        cmbPregunta.setBounds(190, 230, 210, 25);
        add(cmbPregunta);
        agregarEtiqueta("Respuesta:", 270);
        txtRespuesta = agregarCampoTexto(270);

        // Recordatorio de acceso: el estudiante inicia sesión con su número de documento
        JLabel lblAviso = new JLabel("Para iniciar sesión usará su número de documento y su contraseña.");
        lblAviso.setBounds(30, 295, 400, 20);
        add(lblAviso);
    }

    private void crearBotones() {
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(90, 330, 120, 30);
        btnRegistrar.addActionListener(this);
        add(btnRegistrar);
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(230, 330, 120, 30);
        btnCancelar.addActionListener(this);
        add(btnCancelar);
    }

    private void agregarEtiqueta(String texto, int y) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setBounds(30, y, 160, 25);
        add(etiqueta);
    }

    private JTextField agregarCampoTexto(int y) {
        JTextField campo = new JTextField();
        campo.setBounds(190, y, 210, 25);
        add(campo);
        return campo;
    }

    // Valida los datos, crea el estudiante y lo registra
    private void registrar() throws ValidacionException {
        String documento = txtDocumento.getText().trim();
        String nombre = txtNombre.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());
        String programa = txtPrograma.getText().trim();
        String respuesta = txtRespuesta.getText().trim();
        String pregunta = (String) cmbPregunta.getSelectedItem();

        // Aquí se valida que ningún campo esté vacío ni tenga ";"
        String[] valores = {documento, nombre, usuario, contrasena, programa, respuesta};
        String[] nombres = {"Documento", "Nombre completo", "Usuario", "Contraseña", "Programa académico", "Respuesta"};
        for (int i = 0; i < valores.length; i++) {
            Validador.validarNoVacio(valores[i], nombres[i]);
            Validador.validarSinPuntoYComa(valores[i], nombres[i]);
        }
        validarDocumentoNumerico(documento);

        // Aquí se crea un objeto Estudiante con new; los usuarios nuevos empiezan con saldo 0
        Estudiante estudiante = new Estudiante(documento, nombre, usuario, contrasena,
                pregunta, respuesta, programa, 0);
        gestorUsuarios.registrar(estudiante);
        GestorAuditoria.registrar(usuario, "REGISTRO", "Nuevo estudiante con documento " + documento);
    }

    // Revisa carácter por carácter que el documento solo tenga dígitos
    private void validarDocumentoNumerico(String documento) throws ValidacionException {
        for (int i = 0; i < documento.length(); i++) {
            if (!Character.isDigit(documento.charAt(i))) {
                throw new ValidacionException("El documento debe ser numérico");
            }
        }
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegistrar) {
            try {
                registrar();
                JOptionPane.showMessageDialog(this,
                        "Registro exitoso. Ya puede iniciar sesión con su número de documento");
                dispose();
            } catch (ValidacionException error) {
                GestorAuditoria.registrar(txtUsuario.getText().trim(), "ERROR", "Registro: " + error.getMessage());
                JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancelar) {
            dispose();
        }
    }
}
