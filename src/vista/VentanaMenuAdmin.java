// Menú principal del administrador con acceso a todas las funciones administrativas.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import modelo.Administrador;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorUsuarios;
import util.GestorTema;

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
    private JButton btnCalculadora;
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
        setSize(500, 320);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        crearComponentes();
    }

    // Crea la cabecera con bienvenida y botón de tema, y la cuadrícula de botones
    private void crearComponentes() {
        // Panel superior: bienvenida + BotonTema (esquina superior derecha)
        JPanel panelCabecera = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel("Panel de administración - " + administrador.getNombreCompleto(), JLabel.LEFT);
        JPanel panelBienvenida = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBienvenida.add(lblBienvenida);
        panelCabecera.add(panelBienvenida, BorderLayout.CENTER);
        JPanel panelBotonTema = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonTema.add(new BotonTema());
        panelCabecera.add(panelBotonTema, BorderLayout.EAST);
        add(panelCabecera, BorderLayout.NORTH);

        // Panel central: cuadrícula de botones
        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 8, 8));

        btnAdminDuts = new JButton("Administrar asignaciones de DUTS");
        btnAdminDuts.addActionListener(this);
        panelBotones.add(btnAdminDuts);

        btnAdminNovedades = new JButton("Administrar novedades institucionales");
        btnAdminNovedades.addActionListener(this);
        panelBotones.add(btnAdminNovedades);

        btnAdminAsistencia = new JButton("Marcar asistencia a eventos");
        btnAdminAsistencia.addActionListener(this);
        panelBotones.add(btnAdminAsistencia);

        btnCalculadora = new JButton("Calculadora de DUTS");
        btnCalculadora.addActionListener(this);
        panelBotones.add(btnCalculadora);

        btnSalir = new JButton("Cerrar sesión");
        btnSalir.addActionListener(this);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.CENTER);
    }

    // Abre una ventana hija y aplica el tema activo antes de mostrarla
    private void abrirConTema(JFrame ventana) {
        GestorTema.aplicarTema(ventana);
        ventana.setVisible(true);
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdminDuts) {
            abrirConTema(new VentanaAdminDuts(administrador, gestorUsuarios, gestorMovimientos));
        } else if (e.getSource() == btnAdminNovedades) {
            abrirConTema(new VentanaAdminNovedades(administrador, gestorNovedades));
        } else if (e.getSource() == btnAdminAsistencia) {
            abrirConTema(new VentanaAdminAsistencia(administrador, gestorNovedades,
                    gestorInscripciones, gestorUsuarios));
        } else if (e.getSource() == btnCalculadora) {
            // Admin accede con gestor de usuarios para elegir estudiante
            abrirConTema(new VentanaCalculadora(null, gestorMovimientos, gestorNovedades, gestorUsuarios));
        } else if (e.getSource() == btnSalir) {
            dispose();
        }
    }
}
