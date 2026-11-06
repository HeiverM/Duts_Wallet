// Ventana con el historial de movimientos filtrable, la exportación a CSV y el generador de comprobantes.
package vista;

import excepciones.ValidacionException;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import modelo.Estudiante;
import modelo.Movimiento;
import persistencia.GestorAuditoria;
import persistencia.GestorMovimientos;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaHistorial extends JFrame implements ActionListener {

    // Opciones fijas del filtro por tipo y columnas de la tabla
    private static final String[] TIPOS = {"Todos", Movimiento.TIPO_TRANSFERENCIA, Movimiento.TIPO_EVENTO,
        Movimiento.TIPO_PENALIZACION, Movimiento.TIPO_ASIGNACION};
    private static final String[] COLUMNAS = {"Código", "Fecha", "Tipo", "Emisor", "Receptor", "Valor", "Descripción"};

    private Estudiante estudiante;
    private GestorMovimientos gestorMovimientos;

    private JComboBox<String> cmbTipo;
    private JTextField txtDesde;
    private JTextField txtHasta;
    private JButton btnFiltrar;
    private DefaultTableModel modeloTabla;
    private JTable tablaMovimientos;
    private JButton btnExportar;
    private JButton btnComprobante;
    private JButton btnCerrar;

    public VentanaHistorial(Estudiante estudiante, GestorMovimientos gestorMovimientos) {
        super("DUTS Wallet - Historial de movimientos");
        this.estudiante = estudiante;
        this.gestorMovimientos = gestorMovimientos;
        setSize(880, 460);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearFiltros();
        crearTabla();
        crearBotones();
        cargarTabla(gestorMovimientos.listarPorUsuario(estudiante.getDocumento()));
    }

    // Panel superior con el filtro por tipo y por rango de fechas
    private void crearFiltros() {
        JPanel panelFiltros = new JPanel(new FlowLayout());
        panelFiltros.add(new JLabel("Tipo:"));
        cmbTipo = new JComboBox<String>(TIPOS);
        panelFiltros.add(cmbTipo);
        panelFiltros.add(new JLabel("Desde (yyyy-MM-dd):"));
        txtDesde = new JTextField(9);
        panelFiltros.add(txtDesde);
        panelFiltros.add(new JLabel("Hasta (yyyy-MM-dd):"));
        txtHasta = new JTextField(9);
        panelFiltros.add(txtHasta);
        btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(this);
        panelFiltros.add(btnFiltrar);
        add(panelFiltros, BorderLayout.NORTH);
    }

    // Tabla central con los movimientos
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaMovimientos = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaMovimientos.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaMovimientos), BorderLayout.CENTER);
    }

    // Panel inferior con los botones de exportación
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnExportar = new JButton("Exportar CSV");
        btnExportar.addActionListener(this);
        panelBotones.add(btnExportar);
        btnComprobante = new JButton("Generar comprobante");
        btnComprobante.addActionListener(this);
        panelBotones.add(btnComprobante);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Llena la tabla recorriendo la lista al revés para mostrar primero los más recientes
    private void cargarTabla(ArrayList<Movimiento> movimientos) {
        modeloTabla.setRowCount(0);
        for (int i = movimientos.size() - 1; i >= 0; i--) {
            Movimiento movimiento = movimientos.get(i);
            Object[] fila = {movimiento.getCodigo(), movimiento.getFecha().toString(), movimiento.getTipo(),
                movimiento.getDocumentoEmisor(), movimiento.getDocumentoReceptor(),
                movimiento.getValor(), movimiento.getDescripcion()};
            modeloTabla.addRow(fila);
        }
    }

    // Lee los filtros y vuelve a cargar la tabla usando el método filtrar del gestor
    private void filtrar() throws ValidacionException {
        LocalDate desde = leerFecha(txtDesde, "Desde");
        LocalDate hasta = leerFecha(txtHasta, "Hasta");
        Validador.validarRangoFechas(desde, hasta);
        String tipo = (String) cmbTipo.getSelectedItem();
        cargarTabla(gestorMovimientos.filtrar(estudiante.getDocumento(), tipo, desde, hasta));
    }

    // Si el campo está vacío devuelve null (sin filtro); si no, valida la fecha
    private LocalDate leerFecha(JTextField campo, String nombreCampo) throws ValidacionException {
        if (campo.getText().trim().isEmpty()) {
            return null;
        }
        return Validador.validarFecha(campo.getText(), nombreCampo);
    }

    // Crea el CSV del historial completo con los promedios al final
    private void exportarCsv() {
        String ruta = gestorMovimientos.exportarCsv(estudiante.getDocumento());
        GestorAuditoria.registrar(estudiante.getNombreUsuario(), "EXPORTAR_CSV", ruta);
        JOptionPane.showMessageDialog(this, "Reporte exportado en:\n" + new File(ruta).getAbsolutePath());
    }

    // Crea el comprobante de la transferencia seleccionada (solo si el usuario la envió)
    private void generarComprobante() throws ValidacionException {
        int fila = tablaMovimientos.getSelectedRow();
        if (fila == -1) {
            throw new ValidacionException("Seleccione una transferencia de la tabla");
        }
        String codigo = (String) modeloTabla.getValueAt(fila, 0);
        Movimiento movimiento = gestorMovimientos.buscarPorCodigo(codigo);
        if (movimiento == null || !movimiento.getTipo().equals(Movimiento.TIPO_TRANSFERENCIA)
                || !movimiento.getDocumentoEmisor().equals(estudiante.getDocumento())) {
            throw new ValidacionException("Seleccione una transferencia que usted haya enviado");
        }
        String ruta = gestorMovimientos.exportarComprobante(movimiento);
        GestorAuditoria.registrar(estudiante.getNombreUsuario(), "COMPROBANTE", ruta);
        JOptionPane.showMessageDialog(this, "Comprobante generado en:\n" + new File(ruta).getAbsolutePath());
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == btnFiltrar) {
                filtrar();
            } else if (e.getSource() == btnExportar) {
                exportarCsv();
            } else if (e.getSource() == btnComprobante) {
                generarComprobante();
            } else if (e.getSource() == btnCerrar) {
                dispose();
            }
        } catch (ValidacionException error) {
            GestorAuditoria.registrar(estudiante.getNombreUsuario(), "ERROR", "Historial: " + error.getMessage());
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
