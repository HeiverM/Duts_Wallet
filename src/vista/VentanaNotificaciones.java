// Ventana que muestra la bandeja de notificaciones del usuario.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Estudiante;
import modelo.Notificacion;
import persistencia.GestorNotificaciones;

// Aquí se aplica herencia: la ventana es un JFrame y reutiliza todo su comportamiento
public class VentanaNotificaciones extends JFrame implements ActionListener {

    private static final String[] COLUMNAS = {"Fecha", "Mensaje"};

    private Estudiante estudiante;
    private GestorNotificaciones gestorNotificaciones;

    private DefaultTableModel modeloTabla;
    private JTable tablaNotificaciones;
    private JButton btnCerrar;

    public VentanaNotificaciones(Estudiante estudiante, GestorNotificaciones gestorNotificaciones) {
        super("DUTS Wallet - Bandeja de notificaciones");
        this.estudiante = estudiante;
        this.gestorNotificaciones = gestorNotificaciones;
        setSize(720, 420);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearTabla();
        crearBotones();
        cargarTabla();
    }

    // Tabla central con todas las notificaciones del usuario
    private void crearTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0);
        tablaNotificaciones = new JTable(modeloTabla);
        // Se quita el editor para que las celdas no se puedan modificar
        tablaNotificaciones.setDefaultEditor(Object.class, null);
        add(new JScrollPane(tablaNotificaciones), BorderLayout.CENTER);
    }

    // Panel inferior con el botón de cerrar
    private void crearBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBotones.add(btnCerrar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    // Llena la tabla recorriendo la lista al revés para mostrar primero las más recientes
    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        ArrayList<Notificacion> notificaciones = gestorNotificaciones.listarPorUsuario(estudiante.getDocumento());
        for (int i = notificaciones.size() - 1; i >= 0; i--) {
            Notificacion notificacion = notificaciones.get(i);
            Object[] fila = {notificacion.getFecha().toString(), notificacion.getMensaje()};
            modeloTabla.addRow(fila);
        }
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCerrar) {
            dispose();
        }
    }
}
