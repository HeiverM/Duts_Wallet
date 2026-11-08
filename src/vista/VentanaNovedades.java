// Ventana para consultar las novedades institucionales y gestionar la inscripción.
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
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import excepciones.ValidacionException;
import modelo.Estudiante;
import modelo.Inscripcion;
import modelo.Novedad;
import persistencia.GestorAuditoria;
import persistencia.GestorInscripciones;
import persistencia.GestorNovedades;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaNovedades extends JFrame implements ActionListener {

    private static final String[] COLUMNAS = {"Código", "Nombre", "Fecha", "Lugar", "DUTS", "Otorga DUTS"};

    private Estudiante estudiante;
    private GestorNovedades gestorNovedades;
    private GestorInscripciones gestorInscripciones;

    private DefaultTableModel modeloTabla;
    private JTable tablaNovedades;
    private JTextArea txtCondiciones;
    private JButton btnInscribirse;
    private JButton btnCancelar;
    private JButton btnCerrar;

    public VentanaNovedades(Estudiante estudiante, GestorNovedades gestorNovedades,
                            GestorInscripciones gestorInscripciones) {
        super("DUTS Wallet - Novedades institucionales");
        this.estudiante = estudiante;
        this.gestorNovedades = gestorNovedades;
        this.gestorInscripciones = gestorInscripciones;
        setSize(760, 520);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearTabla();
        crearPanelCondiciones();
        crearBotones();
        cargarTabla();
    }

    // Tabla central con todas las novedades disponibles
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaNovedades = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaNovedades.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaNovedades), BorderLayout.CENTER);
    }

    // Panel inferior que muestra las condiciones de la novedad seleccionada
    private void crearPanelCondiciones() {
        JPanel panelInferior = new JPanel(new BorderLayout());
        JLabel lblTitulo = new JLabel("Condiciones de la novedad seleccionada:");
        panelInferior.add(lblTitulo, BorderLayout.NORTH);
        txtCondiciones = new JTextArea(5, 40);
        txtCondiciones.setEditable(false);
        txtCondiciones.setLineWrap(true);
        txtCondiciones.setWrapStyleWord(true);
        panelInferior.add(new JScrollPane(txtCondiciones), BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    // Panel de botones: inscripcion, cancelacion y cierre
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnInscribirse = new JButton("Inscribirse");
        btnInscribirse.addActionListener(this);
        panelBotones.add(btnInscribirse);
        btnCancelar = new JButton("Cancelar inscripción");
        btnCancelar.addActionListener(this);
        panelBotones.add(btnCancelar);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.NORTH);
    }

    // Llena la tabla con las novedades; el valor 0 se distingue como "No"
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Novedad> novedades = gestorNovedades.listar();
        for (int i = 0; i < novedades.size(); i++) {
            Novedad novedad = novedades.get(i);
            String otorga = "No";
            if (novedad.getValorDuts() > 0) {
                otorga = "Sí (" + novedad.getValorDuts() + " DUTS)";
            }
            Object[] fila = {novedad.getCodigo(), novedad.getNombre(),
                novedad.getFecha().toString(), novedad.getLugar(),
                novedad.getValorDuts(), otorga};
            modeloTabla.addRow(fila);
        }
    }

    // Muestra las condiciones de la novedad seleccionada en la tabla
    private void mostrarCondiciones() {
        Novedad novedad = obtenerNovedadSeleccionada();
        if (novedad == null) {
            txtCondiciones.setText("");
            return;
        }
        StringBuilder texto = new StringBuilder();
        texto.append(novedad.getNombre()).append("\n");
        texto.append("Fecha: ").append(novedad.getFecha()).append("\n");
        texto.append("Lugar: ").append(novedad.getLugar()).append("\n");
        texto.append("Descripción: ").append(novedad.getDescripcion()).append("\n");
        texto.append("Condiciones: ").append(novedad.getCondiciones()).append("\n");
        texto.append("Estado de su inscripción: ").append(obtenerEstadoInscripcion(novedad.getCodigo()));
        txtCondiciones.setText(texto.toString());
    }

    // Devuelve la novedad de la fila seleccionada, o null si no hay ninguna
    private Novedad obtenerNovedadSeleccionada() {
        int fila = tablaNovedades.getSelectedRow();
        if (fila == -1) {
            return null;
        }
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        return gestorNovedades.buscarPorCodigo(codigo);
    }

    // Busca el estado de la inscripción del estudiante en una novedad
    private String obtenerEstadoInscripcion(String codigoNovedad) {
        ArrayList<Inscripcion> inscripciones = gestorInscripciones.listarPorEstudiante(estudiante.getDocumento());
        for (int i = 0; i < inscripciones.size(); i++) {
            Inscripcion inscripcion = inscripciones.get(i);
            if (inscripcion.getCodigoNovedad().equals(codigoNovedad)) {
                return inscripcion.getEstado();
            }
        }
        return "No inscrito";
    }

    // Inscribe al estudiante en la novedad seleccionada
    private void inscribir() throws ValidacionException {
        Novedad novedad = obtenerNovedadSeleccionada();
        if (novedad == null) {
            throw new ValidacionException("Seleccione una novedad de la tabla");
        }
        gestorInscripciones.inscribir(novedad.getCodigo(), estudiante.getDocumento());
        GestorAuditoria.registrar(estudiante.getNombreUsuario(), "INSCRIPCION", novedad.getNombre());
        cargarTabla();
        mostrarCondiciones();
    }

    // Cancela la inscripción del estudiante en la novedad seleccionada
    private void cancelarInscripcion() throws ValidacionException {
        Novedad novedad = obtenerNovedadSeleccionada();
        if (novedad == null) {
            throw new ValidacionException("Seleccione una novedad de la tabla");
        }
        gestorInscripciones.cancelar(novedad.getCodigo(), estudiante.getDocumento());
        GestorAuditoria.registrar(estudiante.getNombreUsuario(), "CANCELAR_INSCRIPCION", novedad.getNombre());
        cargarTabla();
        mostrarCondiciones();
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnInscribirse) {
                inscribir();
                JOptionPane.showMessageDialog(this, "Inscripción registrada");
            } else if (e.getSource() == btnCancelar) {
                cancelarInscripcion();
                JOptionPane.showMessageDialog(this, "Inscripción cancelada");
            } else if (e.getSource() == btnCerrar) {
                dispose();
            } else {
                // Cualquier otro clic es una selección de fila: se muestran las condiciones
                mostrarCondiciones();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(estudiante.getNombreUsuario(), "ERROR", "Novedades: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
