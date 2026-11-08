// Ventana de administración para crear, consultar, actualizar y eliminar novedades institucionales.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import excepciones.ValidacionException;
import modelo.Administrador;
import modelo.Novedad;
import persistencia.GestorAuditoria;
import persistencia.GestorNovedades;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaAdminNovedades extends JFrame implements ActionListener {

    private static final String[] COLUMNAS = {"Código", "Nombre", "Fecha", "Lugar", "DUTS", "Descripción"};

    private Administrador administrador;
    private GestorNovedades gestorNovedades;

    private JTextField txtNombre;
    private JTextField txtFecha;
    private JTextField txtLugar;
    private JTextField txtValorDuts;
    private JTextArea txtDescripcion;
    private JTextArea txtCondiciones;
    private JButton btnCrear;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnCerrar;
    private DefaultTableModel modeloTabla;
    private JTable tablaNovedades;

    public VentanaAdminNovedades(Administrador administrador, GestorNovedades gestorNovedades) {
        super("DUTS Wallet - Administración de novedades");
        this.administrador = administrador;
        this.gestorNovedades = gestorNovedades;
        setSize(900, 580);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearPanelFormulario();
        crearTabla();
        crearBotones();
        cargarTabla();
    }

    // Panel superior con los campos del formulario
    private void crearPanelFormulario() {
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(null);
        panelFormulario.setBounds(0, 0, 900, 220);

        agregarCampoTexto(panelFormulario, "Nombre:", 10, txtNombre = new JTextField(25));
        agregarCampoTexto(panelFormulario, "Fecha (yyyy-MM-dd):", 40, txtFecha = new JTextField(15));
        agregarCampoTexto(panelFormulario, "Lugar:", 70, txtLugar = new JTextField(25));
        agregarCampoTexto(panelFormulario, "Valor DUTS:", 100, txtValorDuts = new JTextField(8));

        JLabel lblDescripcion = new JLabel("Descripción:");
        lblDescripcion.setBounds(10, 130, 90, 20);
        panelFormulario.add(lblDescripcion);
        txtDescripcion = new JTextArea(3, 50);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setBounds(110, 130, 530, 60);
        panelFormulario.add(txtDescripcion);

        JLabel lblCondiciones = new JLabel("Condiciones:");
        lblCondiciones.setBounds(660, 130, 90, 20);
        panelFormulario.add(lblCondiciones);
        txtCondiciones = new JTextArea(3, 20);
        txtCondiciones.setLineWrap(true);
        txtCondiciones.setBounds(760, 130, 110, 60);
        panelFormulario.add(txtCondiciones);

        add(panelFormulario, BorderLayout.NORTH);
    }

    // Auxiliar para agregar campos de texto al formulario
    private void agregarCampoTexto(JPanel panel, String etiqueta, int y, JTextField campo) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setBounds(10, y, 100, 20);
        panel.add(lbl);
        campo.setBounds(110, y, 300, 20);
        panel.add(campo);
    }

    // Tabla central con todas las novedades
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaNovedades = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaNovedades.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaNovedades), BorderLayout.CENTER);
    }

    // Panel inferior con los botones del CRUD
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCrear = new JButton("Crear novedad");
        btnCrear.addActionListener(this);
        panelBotones.add(btnCrear);
        btnActualizar = new JButton("Actualizar novedad");
        btnActualizar.addActionListener(this);
        panelBotones.add(btnActualizar);
        btnEliminar = new JButton("Eliminar novedad");
        btnEliminar.addActionListener(this);
        panelBotones.add(btnEliminar);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Llena la tabla con todas las novedades
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Novedad> novedades = gestorNovedades.listar();
        for (int i = 0; i < novedades.size(); i++) {
            Novedad novedad = novedades.get(i);
            Object[] fila = {novedad.getCodigo(), novedad.getNombre(), novedad.getFecha().toString(),
                novedad.getLugar(), novedad.getValorDuts(), novedad.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }

    // Devuelve la novedad seleccionada, o null si no hay ninguna
    private Novedad obtenerNovedadSeleccionada() {
        int fila = tablaNovedades.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        return gestorNovedades.buscarPorCodigo(codigo);
    }

    // Valida y lee valorDuts: puede ser 0 o mayor (no negativo)
    private int leerValorDuts() throws ValidacionException {
        Validador.validarNoVacio(txtValorDuts.getText(), "Valor DUTS");
        int valor;
        try {
            valor = Integer.parseInt(txtValorDuts.getText().trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo Valor DUTS debe ser un número entero");
        }
        if (valor < 0) {
            throw new ValidacionException("El valor en DUTS no puede ser negativo");
        }
        return valor;
    }

    // Crea una novedad con validaciones
    private void crearNovedad() throws ValidacionException {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        LocalDate fecha = Validador.validarFecha(txtFecha.getText(), "Fecha");
        String lugar = txtLugar.getText().trim();
        int valorDuts = leerValorDuts();
        String condiciones = txtCondiciones.getText().trim();

        Validador.validarNoVacio(nombre, "Nombre");
        Validador.validarSinPuntoYComa(nombre, "Nombre");
        Validador.validarNoVacio(descripcion, "Descripción");
        Validador.validarSinPuntoYComa(descripcion, "Descripción");
        Validador.validarNoVacio(lugar, "Lugar");
        Validador.validarSinPuntoYComa(lugar, "Lugar");
        Validador.validarNoVacio(condiciones, "Condiciones");
        Validador.validarSinPuntoYComa(condiciones, "Condiciones");

        gestorNovedades.crear(nombre, descripcion, fecha, lugar, valorDuts, condiciones);
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "CREAR_NOVEDAD", nombre);
        limpiarCampos();
        cargarTabla();
    }

    // Actualiza la novedad seleccionada
    private void actualizarNovedad() throws ValidacionException {
        Novedad novedad = obtenerNovedadSeleccionada();
        if (novedad == null) {
            throw new ValidacionException("Seleccione una novedad de la tabla");
        }
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        LocalDate fecha = Validador.validarFecha(txtFecha.getText(), "Fecha");
        String lugar = txtLugar.getText().trim();
        int valorDuts = leerValorDuts();
        String condiciones = txtCondiciones.getText().trim();

        Validador.validarNoVacio(nombre, "Nombre");
        Validador.validarSinPuntoYComa(nombre, "Nombre");
        Validador.validarNoVacio(descripcion, "Descripción");
        Validador.validarSinPuntoYComa(descripcion, "Descripción");
        Validador.validarNoVacio(lugar, "Lugar");
        Validador.validarSinPuntoYComa(lugar, "Lugar");
        Validador.validarNoVacio(condiciones, "Condiciones");
        Validador.validarSinPuntoYComa(condiciones, "Condiciones");

        gestorNovedades.actualizar(novedad.getCodigo(), nombre, descripcion, fecha, lugar, valorDuts, condiciones);
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "ACTUALIZAR_NOVEDAD", novedad.getCodigo());
        limpiarCampos();
        cargarTabla();
    }

    // Elimina la novedad seleccionada
    private void eliminarNovedad() throws ValidacionException {
        Novedad novedad = obtenerNovedadSeleccionada();
        if (novedad == null) {
            throw new ValidacionException("Seleccione una novedad de la tabla");
        }
        gestorNovedades.eliminar(novedad.getCodigo());
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "ELIMINAR_NOVEDAD", novedad.getCodigo());
        limpiarCampos();
        cargarTabla();
    }

    // Limpia los campos del formulario
    private void limpiarCampos() {
        txtNombre.setText("");
        txtFecha.setText("");
        txtLugar.setText("");
        txtValorDuts.setText("");
        txtDescripcion.setText("");
        txtCondiciones.setText("");
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnCrear) {
                crearNovedad();
                JOptionPane.showMessageDialog(this, "Novedad creada");
            } else if (e.getSource() == btnActualizar) {
                actualizarNovedad();
                JOptionPane.showMessageDialog(this, "Novedad actualizada");
            } else if (e.getSource() == btnEliminar) {
                eliminarNovedad();
                JOptionPane.showMessageDialog(this, "Novedad eliminada");
            } else if (e.getSource() == btnCerrar) {
                dispose();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(administrador.getNombreUsuario(), "ERROR", "Admin Novedades: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
