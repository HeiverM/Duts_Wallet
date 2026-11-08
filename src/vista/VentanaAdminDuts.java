// Ventana de administración para crear, consultar, actualizar y eliminar asignaciones de DUTS.
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
import modelo.Administrador;
import modelo.Estudiante;
import modelo.Movimiento;
import persistencia.GestorAuditoria;
import persistencia.GestorMovimientos;
import persistencia.GestorUsuarios;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaAdminDuts extends JFrame implements ActionListener {

    private static final String[] COLUMNAS = {"Código", "Estudiante", "Documento", "Fecha", "Valor (DUTS)", "Descripción"};

    private Administrador administrador;
    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;

    private JTextField txtDocumento;
    private JTextField txtValor;
    private JTextField txtDescripcion;
    private JButton btnCrear;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCerrar;
    private DefaultTableModel modeloTabla;
    private JTable tablaAsignaciones;

    public VentanaAdminDuts(Administrador administrador, GestorUsuarios gestorUsuarios,
                            GestorMovimientos gestorMovimientos) {
        super("DUTS Wallet - Administración de DUTS");
        this.administrador = administrador;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        setSize(800, 500);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearPanelFormulario();
        crearTabla();
        crearBotones();
        cargarTabla();
    }

    // Panel superior con los campos del formulario de asignación
    private void crearPanelFormulario() {
        JPanel panelFormulario = new JPanel(new FlowLayout());
        panelFormulario.add(new JLabel("Documento estudiante:"));
        txtDocumento = new JTextField(10);
        panelFormulario.add(txtDocumento);
        panelFormulario.add(new JLabel("Valor DUTS:"));
        txtValor = new JTextField(8);
        panelFormulario.add(txtValor);
        panelFormulario.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField(22);
        panelFormulario.add(txtDescripcion);
        add(panelFormulario, BorderLayout.NORTH);
    }

    // Tabla central con todas las asignaciones
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaAsignaciones = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaAsignaciones.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaAsignaciones), BorderLayout.CENTER);
    }

    // Panel inferior con los botones del CRUD
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCrear = new JButton("Crear asignación");
        btnCrear.addActionListener(this);
        panelBotones.add(btnCrear);
        btnActualizar = new JButton("Actualizar asignación");
        btnActualizar.addActionListener(this);
        panelBotones.add(btnActualizar);
        btnEliminar = new JButton("Eliminar asignación");
        btnEliminar.addActionListener(this);
        panelBotones.add(btnEliminar);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Llena la tabla con las asignaciones existentes
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Movimiento> asignaciones = gestorMovimientos.listarAsignaciones();
        for (int i = 0; i < asignaciones.size(); i++) {
            Movimiento asignacion = asignaciones.get(i);
            Estudiante estudiante = gestorUsuarios.buscarEstudiantePorDocumento(asignacion.getDocumentoReceptor());
            String nombre = "No encontrado";
            if (estudiante != null) {
                nombre = estudiante.getNombreCompleto();
            }
            Object[] fila = {asignacion.getCodigo(), nombre, asignacion.getDocumentoReceptor(),
                asignacion.getFecha().toString(), asignacion.getValor(), asignacion.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }

    // Devuelve el movimiento de asignación seleccionado, o null si no hay ninguno
    private Movimiento obtenerAsignacionSeleccionada() {
        int fila = tablaAsignaciones.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        return gestorMovimientos.buscarPorCodigo(codigo);
    }

    // Crea una asignación: suma DUTS al estudiante y genera un movimiento ASIGNACION
    private void crearAsignacion() throws ValidacionException {
        String documento = txtDocumento.getText().trim();
        Validador.validarNoVacio(documento, "Documento");
        int valor = Validador.validarEnteroPositivo(txtValor.getText(), "Valor");
        String descripcion = txtDescripcion.getText().trim();
        Validador.validarNoVacio(descripcion, "Descripción");
        Validador.validarSinPuntoYComa(descripcion, "Descripción");

        Estudiante estudiante = gestorUsuarios.buscarEstudiantePorDocumento(documento);
        if (estudiante == null) {
            throw new ValidacionException("No existe un estudiante con ese documento");
        }
        gestorMovimientos.asignarDuts(estudiante, valor, descripcion);
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "CREAR_ASIGNACION", documento + " - " + valor);
        limpiarCampos();
        cargarTabla();
    }

    // Actualiza el valor y la descripción de la asignación seleccionada
    private void actualizarAsignacion() throws ValidacionException {
        Movimiento asignacion = obtenerAsignacionSeleccionada();
        if (asignacion == null) {
            throw new ValidacionException("Seleccione una asignación de la tabla");
        }
        int valor = Validador.validarEnteroPositivo(txtValor.getText(), "Valor");
        String descripcion = txtDescripcion.getText().trim();
        Validador.validarNoVacio(descripcion, "Descripción");
        Validador.validarSinPuntoYComa(descripcion, "Descripción");

        // Se completan los datos con los de la fila seleccionada si el campo quedó vacío
        String documento = txtDocumento.getText().trim();
        if (documento.isEmpty()) {
            documento = asignacion.getDocumentoReceptor();
        }
        Estudiante estudiante = gestorUsuarios.buscarEstudiantePorDocumento(documento);
        if (estudiante == null) {
            throw new ValidacionException("No existe un estudiante con ese documento");
        }
        gestorMovimientos.actualizarAsignacion(asignacion.getCodigo(), valor, descripcion);
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "ACTUALIZAR_ASIGNACION",
                asignacion.getCodigo() + " - " + valor);
        limpiarCampos();
        cargarTabla();
    }

    // Elimina la asignación seleccionada: el gestor descuenta el valor del saldo
    private void eliminarAsignacion() throws ValidacionException {
        Movimiento asignacion = obtenerAsignacionSeleccionada();
        if (asignacion == null) {
            throw new ValidacionException("Seleccione una asignación de la tabla");
        }
        gestorMovimientos.eliminarAsignacion(asignacion.getCodigo());
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "ELIMINAR_ASIGNACION", asignacion.getCodigo());
        limpiarCampos();
        cargarTabla();
    }

    // Limpia los campos del formulario
    private void limpiarCampos() {
        txtDocumento.setText("");
        txtValor.setText("");
        txtDescripcion.setText("");
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnCrear) {
                crearAsignacion();
                JOptionPane.showMessageDialog(this, "Asignación creada");
            } else if (e.getSource() == btnActualizar) {
                actualizarAsignacion();
                JOptionPane.showMessageDialog(this, "Asignación actualizada");
            } else if (e.getSource() == btnEliminar) {
                eliminarAsignacion();
                JOptionPane.showMessageDialog(this, "Asignación eliminada");
            } else if (e.getSource() == btnCerrar) {
                dispose();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(administrador.getNombreUsuario(), "ERROR", "Admin DUTS: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
