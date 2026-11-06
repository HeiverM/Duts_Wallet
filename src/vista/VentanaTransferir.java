// Ventana con el formulario para enviar DUTS a otro estudiante usando su nombre de usuario.
package vista;

import excepciones.ValidacionException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import modelo.Estudiante;
import modelo.Usuario;
import persistencia.GestorAuditoria;
import persistencia.GestorMovimientos;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaTransferir extends JFrame implements ActionListener {

    private Estudiante emisor;
    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;
    private GestorNotificaciones gestorNotificaciones;

    private JLabel lblSaldo;
    private JTextField txtReceptor;
    private JTextField txtValor;
    private JTextField txtDescripcion;
    private JButton btnEnviar;
    private JButton btnCancelar;

    // usuarioReceptor llega vacío desde el menú, o con el usuario del amigo desde VentanaAmigos
    public VentanaTransferir(Estudiante emisor, GestorUsuarios gestorUsuarios, GestorMovimientos gestorMovimientos,
                             GestorNotificaciones gestorNotificaciones, String usuarioReceptor) {
        super("DUTS Wallet - Transferir DUTS");
        this.emisor = emisor;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNotificaciones = gestorNotificaciones;
        setSize(450, 300);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearComponentes(usuarioReceptor);
    }

    private void crearComponentes(String usuarioReceptor) {
        lblSaldo = new JLabel("Saldo disponible: " + emisor.getSaldoDuts() + " DUTS");
        lblSaldo.setBounds(30, 20, 380, 25);
        add(lblSaldo);
        agregarEtiqueta("Usuario receptor:", 60);
        txtReceptor = agregarCampoTexto(60);
        // Si viene desde Amigos, el receptor ya está elegido y no se puede cambiar
        if (!usuarioReceptor.trim().isEmpty()) {
            txtReceptor.setText(usuarioReceptor);
            txtReceptor.setEditable(false);
        }
        agregarEtiqueta("Valor (DUTS):", 100);
        txtValor = agregarCampoTexto(100);
        agregarEtiqueta("Descripción:", 140);
        txtDescripcion = agregarCampoTexto(140);
        btnEnviar = new JButton("Enviar");
        btnEnviar.setBounds(90, 200, 120, 30);
        btnEnviar.addActionListener(this);
        add(btnEnviar);
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(230, 200, 120, 30);
        btnCancelar.addActionListener(this);
        add(btnCancelar);
    }

    private void agregarEtiqueta(String texto, int y) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setBounds(30, y, 140, 25);
        add(etiqueta);
    }

    private JTextField agregarCampoTexto(int y) {
        JTextField campo = new JTextField();
        campo.setBounds(170, y, 240, 25);
        add(campo);
        return campo;
    }

    // Valida el formulario, hace la transferencia y avisa al receptor
    private void enviar() throws ValidacionException {
        String nombreReceptor = txtReceptor.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        Validador.validarNoVacio(nombreReceptor, "Usuario receptor");
        int valor = Validador.validarEnteroPositivo(txtValor.getText(), "Valor");
        Validador.validarNoVacio(descripcion, "Descripción");
        Validador.validarSinPuntoYComa(descripcion, "Descripción");

        // El receptor debe existir y ser un estudiante (no se puede enviar al administrador)
        Usuario usuario = gestorUsuarios.buscarPorNombreUsuario(nombreReceptor);
        if (!(usuario instanceof Estudiante)) {
            throw new ValidacionException("El usuario receptor no existe");
        }
        Estudiante receptor = (Estudiante) usuario;
        // El gestor valida el saldo suficiente y que no sea el mismo estudiante
        gestorMovimientos.transferir(emisor, receptor, valor, descripcion);
        gestorNotificaciones.agregar(receptor.getDocumento(),
                "Recibiste " + valor + " DUTS de " + emisor.getNombreUsuario());
        GestorAuditoria.registrar(emisor.getNombreUsuario(), "TRANSFERENCIA",
                valor + " DUTS a " + receptor.getNombreUsuario());
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEnviar) {
            try {
                enviar();
                JOptionPane.showMessageDialog(this, "Transferencia realizada. Nuevo saldo: "
                        + emisor.getSaldoDuts() + " DUTS");
                dispose();
            } catch (ValidacionException error) {
                GestorAuditoria.registrar(emisor.getNombreUsuario(), "ERROR", "Transferencia: " + error.getMessage());
                JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == btnCancelar) {
            dispose();
        }
    }
}
