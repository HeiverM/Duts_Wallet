// Ventana emergente con las instrucciones de uso y las preguntas frecuentes de DUTS Wallet.
package vista;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import modelo.Estudiante;
import persistencia.GestorInscripciones;

// Aquí se aplica herencia: VentanaSoporte es un JDialog (ventana emergente) y reutiliza todo su comportamiento.
// Implementa ActionListener para responder al clic del botón.
public class VentanaSoporte extends JDialog implements ActionListener {

    private JButton btnCerrar;

    public VentanaSoporte(JFrame ventanaPadre) {
        // "true" hace que la ventana sea modal: bloquea la ventana de atrás hasta que se cierre
        super(ventanaPadre, "DUTS Wallet - Soporte", true);
        setSize(540, 460);
        setLocationRelativeTo(ventanaPadre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        crearComponentes();
    }

    // Crea el área de texto con la ayuda y el botón Cerrar
    private void crearComponentes() {
        JTextArea txtAyuda = new JTextArea(obtenerTextoAyuda());
        txtAyuda.setEditable(false);
        txtAyuda.setLineWrap(true);
        txtAyuda.setWrapStyleWord(true);
        txtAyuda.setMargin(new java.awt.Insets(10, 10, 10, 10));
        add(new JScrollPane(txtAyuda), BorderLayout.CENTER);

        JPanel panelBoton = new JPanel(new FlowLayout());
        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(this);
        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }

    // Texto fijo con las instrucciones y preguntas frecuentes
    private String obtenerTextoAyuda() {
        return "INSTRUCCIONES DE USO\n\n"
                + "1. Si es nuevo, presione \"Registrarse\" y llene todos los campos.\n"
                + "2. Ingrese con su usuario y contraseña en la ventana inicial.\n"
                + "3. En el menú del estudiante encontrará: perfil, transferir DUTS, historial, "
                + "estadísticas, dashboard, novedades, notificaciones y amigos.\n"
                + "4. Para graduarse debe reunir " + Estudiante.META_DUTS + " DUTS. "
                + "Su avance aparece en la ventana de perfil.\n\n"
                + "PREGUNTAS FRECUENTES\n\n"
                + "¿Olvidé mi contraseña, qué hago?\n"
                + "Use \"Recuperar contraseña\" y responda su pregunta de seguridad.\n\n"
                + "¿Cómo obtengo DUTS?\n"
                + "Por asignaciones del administrador, por asistir a novedades que otorgan DUTS "
                + "y por transferencias de otros estudiantes.\n\n"
                + "¿Qué pasa si me inscribo en una novedad y no asisto?\n"
                + "Se le descuenta el " + GestorInscripciones.PORCENTAJE_PENALIZACION
                + " % del valor en DUTS de esa novedad.\n\n"
                + "¿Puedo cancelar una inscripción?\n"
                + "Sí, mientras esté en estado INSCRITO.\n\n"
                + "¿Por qué no puedo escribir el carácter ; en los campos?\n"
                + "Porque el sistema lo usa para separar los datos en sus archivos.\n\n"
                + "¿Dónde quedan mis reportes y comprobantes?\n"
                + "En las carpetas \"reportes\" y \"comprobantes\" junto al programa.\n\n"
                + "Si el problema continúa, comuníquese con el administrador de la plataforma.";
    }

    // Único método que atiende los eventos de la ventana
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCerrar) {
            dispose();
        }
    }
}
