// Calculadora de DUTS con tres módulos: faltantes (A), simulador de transferencia (B) y proyección (C).
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import modelo.Estudiante;
import modelo.Novedad;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorUsuarios;
import util.CalculadoraDuts;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaCalculadora extends JFrame implements ActionListener {

    private GestorMovimientos gestorMovimientos;
    private GestorNovedades gestorNovedades;
    private GestorUsuarios gestorUsuarios;
    private Estudiante estudianteActual; // null si no hay un estudiante seleccionado aún

    // Módulo A
    private JTextField txtSaldoA;
    private JButton btnCalcularA;

    // Módulo B
    private JTextField txtSaldoB;
    private JTextField txtTransferenciaB;
    private JButton btnCalcularB;

    // Módulo C
    private JTextField txtSaldoC;
    private JTextField txtDutsMesC;
    private JTextField txtDutsEventoC;
    private JButton btnCalcularC;

    // Solo para el administrador: combo de estudiantes o manual
    private JComboBox<String> cmbEstudiantes;
    private boolean esAdmin;

    private JButton btnCargar;
    private JButton btnCerrar;

    public VentanaCalculadora(Estudiante estudiante, GestorMovimientos gestorMovimientos,
                              GestorNovedades gestorNovedades, GestorUsuarios gestorUsuarios) {
        super("DUTS Wallet - Calculadora de DUTS");
        this.estudianteActual = estudiante;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNovedades = gestorNovedades;
        this.gestorUsuarios = gestorUsuarios;
        // El admin pasa estudiante=null y gestorUsuarios!=null
        this.esAdmin = (estudiante == null && gestorUsuarios != null);
        setSize(500, 580);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearComponentes();
        if (!esAdmin) {
            precargarDatosEstudiante();
        }
    }

    private void crearComponentes() {
        // Panel de selección de estudiante (solo para admin)
        if (esAdmin) {
            JPanel panelSelector = new JPanel(new FlowLayout());
            panelSelector.add(new JLabel("Estudiante:"));
            cmbEstudiantes = new JComboBox<String>();
            cmbEstudiantes.addItem("Ingreso manual");
            ArrayList<Estudiante> estudiantes = gestorUsuarios.listarEstudiantes();
            for (int i = 0; i < estudiantes.size(); i++) {
                Estudiante est = estudiantes.get(i);
                cmbEstudiantes.addItem(est.getNombreCompleto() + " \u2014 " + est.getDocumento());
            }
            btnCargar = new JButton("Cargar");
            btnCargar.addActionListener(this);
            panelSelector.add(cmbEstudiantes);
            panelSelector.add(btnCargar);
            add(panelSelector, BorderLayout.NORTH);
        }

        // Tres paneles apilados en el centro
        JPanel panelCentro = new JPanel(new GridLayout(3, 1, 6, 6));
        panelCentro.add(crearPanelA());
        panelCentro.add(crearPanelB());
        panelCentro.add(crearPanelC());
        add(panelCentro, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Módulo A: DUTS faltantes para la meta
    private JPanel crearPanelA() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("A) Meta de grado"));
        panel.add(new JLabel("Saldo actual:"));
        txtSaldoA = new JTextField(8);
        panel.add(txtSaldoA);
        btnCalcularA = new JButton("Calcular");
        btnCalcularA.addActionListener(this);
        panel.add(btnCalcularA);
        return panel;
    }

    // Módulo B: simulador de transferencia
    private JPanel crearPanelB() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("B) Simulador de transferencia"));
        panel.add(new JLabel("Saldo:"));
        txtSaldoB = new JTextField(7);
        panel.add(txtSaldoB);
        panel.add(new JLabel("Valor:"));
        txtTransferenciaB = new JTextField(7);
        panel.add(txtTransferenciaB);
        btnCalcularB = new JButton("Calcular");
        btnCalcularB.addActionListener(this);
        panel.add(btnCalcularB);
        return panel;
    }

    // Módulo C: proyección a la meta
    private JPanel crearPanelC() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel("C) Proyección a la meta"));
        panel.add(new JLabel("Saldo:"));
        txtSaldoC = new JTextField(7);
        panel.add(txtSaldoC);
        panel.add(new JLabel("DUTS/mes:"));
        txtDutsMesC = new JTextField(6);
        panel.add(txtDutsMesC);
        panel.add(new JLabel("DUTS/evento:"));
        txtDutsEventoC = new JTextField(6);
        panel.add(txtDutsEventoC);
        btnCalcularC = new JButton("Calcular");
        btnCalcularC.addActionListener(this);
        panel.add(btnCalcularC);
        return panel;
    }

    // Precarga los datos reales del estudiante en los tres módulos
    private void precargarDatosEstudiante() {
        if (estudianteActual == null) {
            return;
        }
        String saldo = String.valueOf(estudianteActual.getSaldoDuts());
        txtSaldoA.setText(saldo);
        txtSaldoB.setText(saldo);
        txtSaldoC.setText(saldo);
        // Precarga el promedio mensual real del estudiante
        double promedioMes = gestorMovimientos.calcularPromedio(
                estudianteActual.getDocumento(), GestorMovimientos.DIAS_MES);
        txtDutsMesC.setText(String.valueOf((int) promedioMes));
        // Precarga el promedio de DUTS de los eventos con valorDuts > 0
        int promedioEvento = calcularPromedioEventos();
        if (promedioEvento > 0) {
            txtDutsEventoC.setText(String.valueOf(promedioEvento));
        }
    }

    // Calcula el promedio de valorDuts de las novedades que otorgan DUTS
    private int calcularPromedioEventos() {
        ArrayList<Novedad> novedades = gestorNovedades.listar();
        int suma = 0;
        int cantidad = 0;
        for (int i = 0; i < novedades.size(); i++) {
            Novedad novedad = novedades.get(i);
            if (novedad.getValorDuts() > 0) {
                suma = suma + novedad.getValorDuts();
                cantidad++;
            }
        }
        if (cantidad == 0) {
            return 0;
        }
        return suma / cantidad;
    }

    // Lee el saldo de un campo y lo convierte a entero. Devuelve -1 si falla
    private int leerSaldo(JTextField campo, String nombre) {
        try {
            int valor = Integer.parseInt(campo.getText().trim());
            if (valor < 0) {
                JOptionPane.showMessageDialog(this, "El " + nombre + " no puede ser negativo", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return -1;
            }
            return valor;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El " + nombre + " debe ser un número entero", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    // Ejecuta el módulo A y muestra resultados
    private void calcularModuloA() {
        int saldo = leerSaldo(txtSaldoA, "saldo");
        if (saldo < 0) {
            return;
        }
        int faltantes = CalculadoraDuts.calcularDutsFaltantes(saldo);
        int porcentaje = CalculadoraDuts.calcularPorcentajeMeta(saldo);
        String estado = faltantes == 0 ? "¡Meta cumplida!" : "Meta en progreso";
        JOptionPane.showMessageDialog(this,
                "Módulo A — Meta de grado\n" +
                "DUTS acumulados: " + saldo + "\n" +
                "DUTS faltantes:  " + faltantes + "\n" +
                "Avance:          " + porcentaje + "%\n" +
                "Estado:          " + estado,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    // Ejecuta el módulo B y muestra resultado
    private void calcularModuloB() {
        int saldo = leerSaldo(txtSaldoB, "saldo");
        if (saldo < 0) {
            return;
        }
        int transferencia = leerSaldo(txtTransferenciaB, "valor a transferir");
        if (transferencia < 0) {
            return;
        }
        int resultado = CalculadoraDuts.calcularSaldoResultante(saldo, transferencia);
        String mensaje = CalculadoraDuts.tieneSaldoSuficiente(saldo, transferencia)
                ? "Saldo resultante: " + resultado + " DUTS"
                : "Saldo insuficiente. Faltan " + (transferencia - saldo) + " DUTS para realizar esta transferencia.";
        JOptionPane.showMessageDialog(this,
                "Módulo B — Simulador de transferencia\n" + mensaje,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    // Ejecuta el módulo C y muestra proyección
    private void calcularModuloC() {
        int saldo = leerSaldo(txtSaldoC, "saldo");
        if (saldo < 0) {
            return;
        }
        int dutsMes = leerSaldo(txtDutsMesC, "DUTS por mes");
        if (dutsMes < 0) {
            return;
        }
        int dutsEvento = leerSaldo(txtDutsEventoC, "DUTS por evento");
        if (dutsEvento < 0) {
            return;
        }
        int meses = CalculadoraDuts.calcularMesesParaMeta(saldo, dutsMes);
        int eventos = CalculadoraDuts.calcularEventosNecesarios(saldo, dutsEvento);
        String linMeses = dutsMes > 0 ? ("Si ganás " + dutsMes + " DUTS/mes: " + meses + " meses") : "DUTS/mes no definido";
        String linEventos = dutsEvento > 0 ? ("Si asistís a eventos de " + dutsEvento + " DUTS: " + eventos + " eventos") : "DUTS/evento no definido";
        JOptionPane.showMessageDialog(this,
                "Módulo C — Proyección a la meta\n" +
                "DUTS faltantes: " + CalculadoraDuts.calcularDutsFaltantes(saldo) + "\n" +
                linMeses + "\n" +
                linEventos,
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }

    // Carga los datos del estudiante elegido en el combo (solo admin)
    private void cargarEstudianteAdmin() {
        int indice = cmbEstudiantes.getSelectedIndex();
        if (indice == 0) {
            // Ingreso manual: limpiar todos los campos
            estudianteActual = null;
            txtSaldoA.setText("");
            txtSaldoB.setText("");
            txtSaldoC.setText("");
            txtDutsMesC.setText("");
            txtDutsEventoC.setText("");
        } else {
            ArrayList<Estudiante> estudiantes = gestorUsuarios.listarEstudiantes();
            estudianteActual = estudiantes.get(indice - 1);
            precargarDatosEstudiante();
        }
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCalcularA) {
            calcularModuloA();
        } else if (e.getSource() == btnCalcularB) {
            calcularModuloB();
        } else if (e.getSource() == btnCalcularC) {
            calcularModuloC();
        } else if (e.getSource() == btnCargar) {
            cargarEstudianteAdmin();
        } else if (e.getSource() == btnCerrar) {
            dispose();
        }
    }
}
