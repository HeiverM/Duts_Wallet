// Ventana de solo lectura con los promedios de DUTS por semana, mes, año y semestre.
package vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modelo.Estudiante;
import persistencia.GestorMovimientos;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaEstadisticas extends JFrame implements ActionListener {

    private Estudiante estudiante;
    private GestorMovimientos gestorMovimientos;
    private JButton btnCerrar;

    public VentanaEstadisticas(Estudiante estudiante, GestorMovimientos gestorMovimientos) {
        super("DUTS Wallet - Estadísticas de DUTS");
        this.estudiante = estudiante;
        this.gestorMovimientos = gestorMovimientos;
        setSize(450, 330);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearEstadisticas();
    }

    // Calcula y muestra los 4 promedios usando las constantes del gestor
    private void crearEstadisticas() {
        JLabel lblTitulo = new JLabel("Promedio de DUTS recibidos:");
        lblTitulo.setBounds(30, 25, 380, 25);
        add(lblTitulo);

        String documento = estudiante.getDocumento();
        double promedioSemana = gestorMovimientos.calcularPromedio(documento, GestorMovimientos.DIAS_SEMANA);
        double promedioMes = gestorMovimientos.calcularPromedio(documento, GestorMovimientos.DIAS_MES);
        double promedioAnio = gestorMovimientos.calcularPromedio(documento, GestorMovimientos.DIAS_ANIO);
        double promedioSemestre = gestorMovimientos.calcularPromedio(documento, GestorMovimientos.DIAS_SEMESTRE);

        // Muestra cada promedio en una fila con su etiqueta y valor
        agregarPromedio("Por semana:", promedioSemana, 70);
        agregarPromedio("Por mes:", promedioMes, 110);
        agregarPromedio("Por año:", promedioAnio, 150);
        agregarPromedio("Por semestre:", promedioSemestre, 190);

        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(165, 240, 110, 28);
        btnCerrar.addActionListener(this);
        add(btnCerrar);
    }

    // Agrega una fila con el título del período y su promedio calculado
    private void agregarPromedio(String titulo, double valor, int y) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setBounds(50, y, 140, 25);
        add(lblTitulo);
        JLabel lblValor = new JLabel(valor + " DUTS");
        lblValor.setBounds(200, y, 200, 25);
        add(lblValor);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCerrar) {
            dispose();
        }
    }
}
