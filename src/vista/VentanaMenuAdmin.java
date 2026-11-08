// Menú principal del administrador con acceso a todas las funciones administrativas.
package vista;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import modelo.Administrador;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorUsuarios;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaMenuAdmin extends JFrame implements ActionListener {

    private Administrador administrador;
    private GestorUsuarios gestorUsuarios;
    private GestorMovimientos gestorMovimientos;
    private GestorNovedades gestorNovedades;
    private GestorInscripciones gestorInscripciones;

    private JButton btnAdminDuts;
    private JButton btnAdminNovedades;
    private JButton btnAdminAsistencia;
    private JButton btnSalir;

    public VentanaMenuAdmin(Administrador administrador, GestorUsuarios gestorUsuarios,
                            GestorMovimientos gestorMovimientos, GestorNovedades gestorNovedades,
                            GestorInscripciones gestorInscripciones) {
        super("DUTS Wallet - Menú administrador");
        this.administrador = administrador;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNovedades = gestorNovedades;
        this.gestorInscripciones = gestorInscripciones;
        setSize(450, 220);
        setLayout(new GridLayout(5, 1, 10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearComponentes();
    }

    // Crea el título de bienvenida y todos los botones del menú
    private void crearComponentes() {
        JLabel lblBienvenida = new JLabel("Panel de administración - " + administrador.getNombreCompleto(), JLabel.CENTER);
        add(lblBienvenida);

        btnAdminDuts = new JButton("Administrar asignaciones de DUTS");
        btnAdminDuts.addActionListener(this);
        add(btnAdminDuts);

        btnAdminNovedades = new JButton("Administrar novedades institucionales");
        btnAdminNovedades.addActionListener(this);
        add(btnAdminNovedades);

        btnAdminAsistencia = new JButton("Marcar asistencia a eventos");
        btnAdminAsistencia.addActionListener(this);
        add(btnAdminAsistencia);

        btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(this);
        add(btnSalir);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdminDuts) {
            VentanaAdminDuts ventana = new VentanaAdminDuts(administrador, gestorUsuarios, gestorMovimientos);
            ventana.setVisible(true);
        } else if (e.getSource() == btnAdminNovedades) {
            VentanaAdminNovedades ventana = new VentanaAdminNovedades(administrador, gestorNovedades);
            ventana.setVisible(true);
        } else if (e.getSource() == btnAdminAsistencia) {
            VentanaAdminAsistencia ventana = new VentanaAdminAsistencia(administrador, gestorNovedades,
                    gestorInscripciones, gestorUsuarios);
            ventana.setVisible(true);
        } else if (e.getSource() == btnSalir) {
            dispose();
        }
    }
}
