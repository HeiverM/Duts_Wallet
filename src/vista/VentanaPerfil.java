// Ventana de solo lectura con los datos de la cuenta, el saldo y el avance hacia la meta de 70.000 DUTS.
package vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import modelo.Estudiante;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaPerfil extends JFrame implements ActionListener {

    private Estudiante estudiante;
    private JButton btnCerrar;

    public VentanaPerfil(Estudiante estudiante) {
        super("DUTS Wallet - Mi perfil");
        this.estudiante = estudiante;
        setSize(450, 430);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearDatosCuenta();
        crearAvanceMeta();
    }

    // Muestra los datos básicos de la cuenta usando los getters del estudiante
    private void crearDatosCuenta() {
        agregarDato("Nombre:", estudiante.getNombreCompleto(), 25);
        agregarDato("Documento:", estudiante.getDocumento(), 55);
        agregarDato("Usuario:", estudiante.getNombreUsuario(), 85);
        agregarDato("Programa:", estudiante.getProgramaAcademico(), 115);
        agregarDato("Saldo disponible:", estudiante.getSaldoDuts() + " DUTS", 145);
    }

    // Barra de progreso con el porcentaje hacia la meta de grado
    private void crearAvanceMeta() {
        JLabel lblMeta = new JLabel("Meta de grado: " + Estudiante.META_DUTS + " DUTS");
        lblMeta.setBounds(30, 190, 380, 25);
        add(lblMeta);
        JProgressBar barraMeta = new JProgressBar(0, 100);
        barraMeta.setValue(estudiante.getPorcentajeMeta());
        // Muestra el porcentaje escrito dentro de la barra
        barraMeta.setStringPainted(true);
        barraMeta.setBounds(30, 220, 380, 28);
        add(barraMeta);
        agregarDato("DUTS acumulados:", estudiante.getSaldoDuts() + " DUTS", 260);
        agregarDato("DUTS faltantes:", estudiante.getDutsFaltantes() + " DUTS", 290);
        String estado = "Meta en progreso";
        if (estudiante.metaCumplida()) {
            estado = "Meta cumplida";
        }
        agregarDato("Estado:", estado, 320);
        btnCerrar = new JButton("Cerrar");
        btnCerrar.setBounds(165, 355, 110, 28);
        btnCerrar.addActionListener(this);
        add(btnCerrar);
    }

    // Agrega una fila con el título a la izquierda y el valor a la derecha
    private void agregarDato(String titulo, String valor, int y) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setBounds(30, y, 140, 25);
        add(lblTitulo);
        JLabel lblValor = new JLabel(valor);
        lblValor.setBounds(170, y, 250, 25);
        add(lblValor);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCerrar) {
            dispose();
        }
    }
}
