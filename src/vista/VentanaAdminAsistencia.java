// Ventana de administración para marcar asistencia o inasistencia de estudiantes inscritos en novedades.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import excepciones.ValidacionException;
import modelo.Administrador;
import modelo.Estudiante;
import modelo.Inscripcion;
import modelo.Novedad;
import persistencia.GestorAuditoria;
import persistencia.GestorInscripciones;
import persistencia.GestorNovedades;
import persistencia.GestorUsuarios;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaAdminAsistencia extends JFrame implements ActionListener {

    private static final String[] COLUMNAS = {"Nombre estudiante", "Documento", "Estado"};

    private Administrador administrador;
    private GestorNovedades gestorNovedades;
    private GestorInscripciones gestorInscripciones;
    private GestorUsuarios gestorUsuarios;

    private JComboBox<String> comboNovedades;
    private JButton btnCargar;
    private DefaultTableModel modeloTabla;
    private JTable tablaInscritos;
    private JButton btnAsistio;
    private JButton btnNoAsistio;
    private JButton btnCerrar;

    public VentanaAdminAsistencia(Administrador administrador, GestorNovedades gestorNovedades,
                                  GestorInscripciones gestorInscripciones, GestorUsuarios gestorUsuarios) {
        super("DUTS Wallet - Marcar asistencia");
        this.administrador = administrador;
        this.gestorNovedades = gestorNovedades;
        this.gestorInscripciones = gestorInscripciones;
        this.gestorUsuarios = gestorUsuarios;
        setSize(750, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearPanelSuperior();
        crearTabla();
        crearBotones();
    }

    // Panel superior con el combo de novedades y el botón de cargar
    private void crearPanelSuperior() {
        JPanel panelSuperior = new JPanel(new FlowLayout());
        panelSuperior.add(new JLabel("Novedad:"));
        comboNovedades = new JComboBox<String>();
        cargarComboNovedades();
        panelSuperior.add(comboNovedades);
        btnCargar = new JButton("Cargar inscritos");
        btnCargar.addActionListener(this);
        panelSuperior.add(btnCargar);
        add(panelSuperior, BorderLayout.NORTH);
    }

    // Llena el combo con todas las novedades
    private void cargarComboNovedades() {
        comboNovedades.removeAllItems();
        ArrayList<Novedad> novedades = gestorNovedades.listar();
        for (int i = 0; i < novedades.size(); i++) {
            Novedad novedad = novedades.get(i);
            comboNovedades.addItem(novedad.getCodigo() + " - " + novedad.getNombre());
        }
    }

    // Tabla central con los inscritos
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaInscritos = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaInscritos.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaInscritos), BorderLayout.CENTER);
    }

    // Panel inferior con los botones de asistencia
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnAsistio = new JButton("Marcar asistió");
        btnAsistio.addActionListener(this);
        panelBotones.add(btnAsistio);
        btnNoAsistio = new JButton("Marcar no asistió");
        btnNoAsistio.addActionListener(this);
        panelBotones.add(btnNoAsistio);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Devuelve el código de la novedad seleccionada en el combo
    private String obtenerCodigoNovedadSeleccionada() {
        String seleccion = (String) comboNovedades.getSelectedItem();
        if (seleccion == null || seleccion.isEmpty()) {
            return null;
        }
        // El combo contiene "codigo - nombre", se extrae el código
        return seleccion.split(" - ")[0];
    }

    // Carga los inscritos de la novedad seleccionada
    private void cargarInscritos() throws ValidacionException {
        String codigoNovedad = obtenerCodigoNovedadSeleccionada();
        if (codigoNovedad == null) {
            throw new ValidacionException("Seleccione una novedad");
        }
        modeloTabla.setRowCount(0);
        ArrayList<Inscripcion> inscritos = gestorInscripciones.listarInscritos(codigoNovedad);
        for (int i = 0; i < inscritos.size(); i++) {
            Inscripcion inscripcion = inscritos.get(i);
            Estudiante estudiante = gestorUsuarios.buscarEstudiantePorDocumento(inscripcion.getDocumentoEstudiante());
            String nombre = "No encontrado";
            if (estudiante != null) {
                nombre = estudiante.getNombreCompleto();
            }
            Object[] fila = {nombre, inscripcion.getDocumentoEstudiante(), inscripcion.getEstado()};
            modeloTabla.addRow(fila);
        }
    }

    // Devuelve el documento del estudiante seleccionado en la tabla
    private String obtenerDocumentoSeleccionado() throws ValidacionException {
        int fila = tablaInscritos.getSelectedRow();
        if (fila == -1) {
            throw new ValidacionException("Seleccione un estudiante de la tabla");
        }
        return (String) modeloTabla.getValueAt(fila, 1);
    }

    // Marca que el estudiante asistió: acredita DUTS y cambia el estado
    private void marcarAsistencia() throws ValidacionException {
        String codigoNovedad = obtenerCodigoNovedadSeleccionada();
        if (codigoNovedad == null) {
            throw new ValidacionException("Seleccione una novedad");
        }
        String documento = obtenerDocumentoSeleccionado();
        gestorInscripciones.marcarAsistencia(codigoNovedad, documento);
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "MARCAR_ASISTENCIA",
                codigoNovedad + " - " + documento);
        cargarInscritos();
    }

    // Marca que el estudiante no asistió: penalización del 20% y cambia el estado
    private void marcarInasistencia() throws ValidacionException {
        String codigoNovedad = obtenerCodigoNovedadSeleccionada();
        if (codigoNovedad == null) {
            throw new ValidacionException("Seleccione una novedad");
        }
        String documento = obtenerDocumentoSeleccionado();
        gestorInscripciones.marcarInasistencia(codigoNovedad, documento);
        GestorAuditoria.registrar(administrador.getNombreUsuario(), "MARCAR_INASISTENCIA",
                codigoNovedad + " - " + documento);
        cargarInscritos();
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnCargar) {
                cargarInscritos();
            } else if (e.getSource() == btnAsistio) {
                marcarAsistencia();
                JOptionPane.showMessageDialog(this, "Asistencia registrada");
            } else if (e.getSource() == btnNoAsistio) {
                marcarInasistencia();
                JOptionPane.showMessageDialog(this, "Inasistencia registrada y penalización aplicada");
            } else if (e.getSource() == btnCerrar) {
                dispose();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(administrador.getNombreUsuario(), "ERROR", "Admin Asistencia: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
