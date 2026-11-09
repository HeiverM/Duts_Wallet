// Ventana para agregar amigos, ver su avance hacia la meta y enviarles DUTS rápidamente.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import excepciones.ValidacionException;
import modelo.Estudiante;
import persistencia.GestorAmigos;
import persistencia.GestorAuditoria;
import persistencia.GestorMovimientos;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaAmigos extends JFrame implements ActionListener {

    private static final String[] COLUMNAS = {"Nombre", "Documento", "Saldo (DUTS)", "Avance meta (%)"};

    private Estudiante estudiante;
    private GestorAmigos gestorAmigos;
    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;
    private GestorNotificaciones gestorNotificaciones;

    private JTextField txtDocumento;
    private JButton btnAgregar;
    private DefaultTableModel modeloTabla;
    private JTable tablaAmigos;
    private JButton btnTransferir;
    private JButton btnCerrar;

    public VentanaAmigos(Estudiante estudiante, GestorAmigos gestorAmigos, GestorUsuarios gestorUsuarios,
                         GestorMovimientos gestorMovimientos, GestorNotificaciones gestorNotificaciones) {
        super("DUTS Wallet - Mis amigos");
        this.estudiante = estudiante;
        this.gestorAmigos = gestorAmigos;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNotificaciones = gestorNotificaciones;
        setSize(760, 480);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearPanelAgregar();
        crearTabla();
        crearBotones();
        cargarTabla();
    }

    // Panel superior para agregar un amigo por su documento
    private void crearPanelAgregar() {
        JPanel panelAgregar = new JPanel(new FlowLayout());
        panelAgregar.add(new JLabel("Documento del amigo:"));
        txtDocumento = new JTextField(12);
        panelAgregar.add(txtDocumento);
        btnAgregar = new JButton("Agregar amigo");
        btnAgregar.addActionListener(this);
        panelAgregar.add(btnAgregar);
        add(panelAgregar, BorderLayout.NORTH);
    }

    // Tabla central con los amigos y su avance hacia la meta
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaAmigos = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaAmigos.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaAmigos), BorderLayout.CENTER);
    }

    // Panel inferior con los botones de transferencia y cierre
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnTransferir = new JButton("Transferir a amigo seleccionado");
        btnTransferir.addActionListener(this);
        panelBotones.add(btnTransferir);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Llena la tabla con los amigos y su avance hacia la meta de grado
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Estudiante> amigos = gestorAmigos.listarAmigos(estudiante.getDocumento());
        for (int i = 0; i < amigos.size(); i++) {
            Estudiante amigo = amigos.get(i);
            Object[] fila = {amigo.getNombreCompleto(), amigo.getDocumento(),
                amigo.getSaldoDuts(), amigo.getPorcentajeMeta() + "%"};
            modeloTabla.addRow(fila);
        }
    }

    // Valida el documento y agrega al amigo
    private void agregarAmigo() throws ValidacionException {
        String documento = txtDocumento.getText().trim();
        Validador.validarNoVacio(documento, "Documento del amigo");
        gestorAmigos.agregar(estudiante.getDocumento(), documento);
        GestorAuditoria.registrar(estudiante.getNombreUsuario(), "AGREGAR_AMIGO", documento);
        txtDocumento.setText("");
        cargarTabla();
    }

    // Abre la ventana de transferencia con el amigo seleccionado ya elegido
    private void transferirAmigo() throws ValidacionException {
        int fila = tablaAmigos.getSelectedRow();
        if (fila == -1) {
            throw new ValidacionException("Seleccione un amigo de la tabla");
        }
        String documentoAmigo = (String) modeloTabla.getValueAt(fila, 1);
        Estudiante amigo = gestorUsuarios.buscarEstudiantePorDocumento(documentoAmigo);
        if (amigo == null) {
            throw new ValidacionException("El amigo ya no existe en el sistema");
        }
        // Se abre la ventana de transferir con el documento del amigo prellenado
        VentanaTransferir ventana = new VentanaTransferir(estudiante, gestorUsuarios, gestorMovimientos,
                gestorNotificaciones, amigo.getDocumento());
        util.GestorTema.aplicarTema(ventana);
        ventana.setVisible(true);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnAgregar) {
                agregarAmigo();
                JOptionPane.showMessageDialog(this, "Amigo agregado");
            } else if (e.getSource() == btnTransferir) {
                transferirAmigo();
            } else if (e.getSource() == btnCerrar) {
                dispose();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(estudiante.getNombreUsuario(), "ERROR", "Amigos: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
