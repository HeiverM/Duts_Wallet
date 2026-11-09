// Panel que pinta la imagen de fondo escalada dinámicamente y un overlay translúcido a la izquierda.
package vista;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import util.GestorTema;

// Aquí se aplica herencia: PanelFondoLogin extiende JPanel y reutiliza su comportamiento
// También se aplica polimorfismo al sobrescribir paintComponent para dibujar la imagen
public class PanelFondoLogin extends JPanel {

    private Image imagenFondo;
    private boolean imagenCargada;

    public PanelFondoLogin() {
        setLayout(null); // Layout manual con setBounds para los campos del formulario
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            File archivo = new File("recursos/Fondo_login.jpg");
            if (archivo.exists()) {
                imagenFondo = ImageIO.read(archivo);
                imagenCargada = true;
            } else {
                imagenCargada = false;
                // Fallback: si no existe, se pinta fondo sólido
                JOptionPane.showMessageDialog(null,
                        "No se encontró la imagen de fondo en recursos/Fondo_login.jpg\n" +
                        "Se usará un fondo de color sólido.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            imagenCargada = false;
            JOptionPane.showMessageDialog(null,
                    "Error al cargar la imagen de fondo: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Sobrescribe paintComponent para dibujar la imagen escalada y el overlay
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int ancho = getWidth();
        int alto = getHeight();

        if (imagenCargada && imagenFondo != null) {
            // Escala la imagen al tamaño actual del panel (dinámico al redimensionar)
            Image escalada = imagenFondo.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            g.drawImage(escalada, 0, 0, this);
        } else {
            // Fallback: fondo sólido oscuro
            g.setColor(new Color(15, 32, 24));
            g.fillRect(0, 0, ancho, alto);
        }

        // Overlay translúcido Pantone en el lado izquierdo (sombra para legibilidad del formulario)
        Color overlayColor = new Color(GestorTema.PANTONE_5535_C.getRed(),
                GestorTema.PANTONE_5535_C.getGreen(),
                GestorTema.PANTONE_5535_C.getBlue(),
                180); // Alpha 180 = semi-transparente
        g.setColor(overlayColor);
        int anchoOverlay = (int) (ancho * 0.35); // 35% del ancho total a la izquierda
        g.fillRect(0, 0, anchoOverlay, alto);

        // Borde sutil del overlay
        g.setColor(new Color(46, 91, 63, 100));
        g.drawRect(0, 0, anchoOverlay - 1, alto - 1);
    }
}
