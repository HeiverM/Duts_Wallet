// Botón reutilizable que alterna el tema y actualiza su propia etiqueta según el estado actual.
package vista;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import util.GestorTema;

// Aquí se aplica herencia: BotonTema extiende JButton y reutiliza todo su comportamiento
public class BotonTema extends JButton implements ActionListener {

    public BotonTema() {
        actualizarEtiqueta();
        addActionListener(this);
        setToolTipText("Cambiar entre modo claro y modo oscuro");
    }

    // Actualiza el texto del botón según el tema actual
    public void actualizarEtiqueta() {
        if (GestorTema.esTemaOscuro()) {
            setText("Modo Oscuro");
        } else {
            setText("Modo Claro");
        }
    }

    // Alterna el tema cuando se hace clic
    public void actionPerformed(ActionEvent e) {
        GestorTema.alternarTema();
    }
}
