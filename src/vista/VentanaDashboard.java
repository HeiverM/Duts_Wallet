// Ventana con el gráfico de ingresos y egresos de los últimos 60 días y sus filtros.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.Estudiante;
import modelo.Movimiento;
import persistencia.GestorMovimientos;
import util.Validador;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaDashboard extends JFrame implements ActionListener {

    // El gráfico cubre 60 días agrupados en 6 bloques de 10 días
    private static final int DIAS_GRAFICO = 60;
    private static final int DIAS_POR_BLOQUE = 10;
    private static final int CANTIDAD_BLOQUES = 6;

    private static final String[] TIPOS = {"Todos", Movimiento.TIPO_TRANSFERENCIA, Movimiento.TIPO_EVENTO,
        Movimiento.TIPO_PENALIZACION, Movimiento.TIPO_ASIGNACION};

    private Estudiante estudiante;
    private GestorMovimientos gestorMovimientos;

    private JComboBox<String> cmbTipo;
    private JTextField txtDesde;
    private JTextField txtHasta;
    private JButton btnAplicar;
    private JButton btnCerrar;
    private PanelGrafico panelGrafico;

    public VentanaDashboard(Estudiante estudiante, GestorMovimientos gestorMovimientos) {
        super("DUTS Wallet - Dashboard de movimientos");
        this.estudiante = estudiante;
        this.gestorMovimientos = gestorMovimientos;
        setSize(820, 520);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearFiltros();
        panelGrafico = new PanelGrafico();
        add(panelGrafico, BorderLayout.CENTER);
        crearBotones();
        aplicarFiltro();
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
        btnAplicar = new JButton("Aplicar filtro");
        btnAplicar.addActionListener(this);
        panelFiltros.add(btnAplicar);
        add(panelFiltros, BorderLayout.NORTH);
    }

    // Panel inferior con el botón de cerrar
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Filtra los movimientos y vuelve a dibujar la gráfica
    private void aplicarFiltro() {
        String tipo = (String) cmbTipo.getSelectedItem();
        LocalDate desde = leerFecha(txtDesde, "Desde");
        LocalDate hasta = leerFecha(txtHasta, "Hasta");

        // El rango del gráfico siempre son los últimos 60 días
        LocalDate hoy = LocalDate.now();
        LocalDate limiteInferior = hoy.minusDays(DIAS_GRAFICO - 1);
        LocalDate limiteSuperior = hoy;

        // Si el usuario escribe un rango, se cruza con el de los 60 días
        if (desde != null && desde.isAfter(limiteInferior)) {
            limiteInferior = desde;
        }
        if (hasta != null && hasta.isBefore(limiteSuperior)) {
            limiteSuperior = hasta;
        }

        // Se usa el método filtrar del gestor para no repetir la lógica de filtros
        ArrayList<Movimiento> movimientos = gestorMovimientos.filtrar(
                estudiante.getDocumento(), tipo, limiteInferior, limiteSuperior);

        int[] ingresos = new int[CANTIDAD_BLOQUES];
        int[] egresos = new int[CANTIDAD_BLOQUES];
        for (int i = 0; i < movimientos.size(); i++) {
            Movimiento movimiento = movimientos.get(i);
            long diasTranscurridos = hoy.toEpochDay() - movimiento.getFecha().toEpochDay();
            if (diasTranscurridos < 0 || diasTranscurridos >= DIAS_GRAFICO) {
                continue;
            }
            // El bloque 0 es el más antiguo y el 5 el más reciente
            int bloque = CANTIDAD_BLOQUES - 1 - (int) (diasTranscurridos / DIAS_POR_BLOQUE);
            // Un ingreso es cuando el usuario es el receptor; un egreso cuando es el emisor
            if (movimiento.getDocumentoReceptor().equals(estudiante.getDocumento())) {
                ingresos[bloque] = ingresos[bloque] + movimiento.getValor();
            }
            if (movimiento.getDocumentoEmisor().equals(estudiante.getDocumento())) {
                egresos[bloque] = egresos[bloque] + movimiento.getValor();
            }
        }
        panelGrafico.setDatos(ingresos, egresos);
        panelGrafico.repaint();
    }

    // Si el campo está vacío devuelve null (sin filtro); si no, valida la fecha
    private LocalDate leerFecha(JTextField campo, String nombreCampo) {
        try {
            if (campo.getText().trim().isEmpty()) {
                return null;
            }
            return Validador.validarFecha(campo.getText(), nombreCampo);
        } catch (excepciones.ValidacionException error) {
            JOptionPane.showMessageDialog(this, error.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAplicar) {
            aplicarFiltro();
        } else if (e.getSource() == btnCerrar) {
            dispose();
        }
    }
}
