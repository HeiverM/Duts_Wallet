// Gestor centralizado de temas (claro/oscuro) con paleta Pantone 5535 C y recoloreado recursivo.
package util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import vista.BotonTema;

// Aquí se aplica encapsulamiento: la paleta y el estado del tema están centralizados
// en métodos static con atributos privados
public class GestorTema {

    private static final int TEMA_CLARO = 0;
    private static final int TEMA_OSCURO = 1;
    private static int temaActual = TEMA_CLARO; // Siempre inicia en claro, sin persistencia

    // Pantone 5535 C - intocable según requerimiento
    public static final Color PANTONE_5535_C = new Color(19, 47, 32); // #132F20

    // Paleta modo claro
    private static final Color CLARO_FONDO_VENTANA = new Color(244, 247, 244);
    private static final Color CLARO_FONDO_PANEL = Color.WHITE;
    private static final Color CLARO_TEXTO = new Color(27, 42, 32);
    private static final Color CLARO_TEXTO_BOTON = Color.WHITE;
    private static final Color CLARO_CAMPO_FONDO = Color.WHITE;
    private static final Color CLARO_BARRA_CANAL = new Color(227, 234, 229);

    // Paleta modo oscuro (tonos armonizados con Pantone)
    private static final Color OSCURO_FONDO_VENTANA = new Color(11, 26, 18);
    private static final Color OSCURO_FONDO_PANEL = new Color(15, 32, 24);
    private static final Color OSCURO_TEXTO = new Color(230, 239, 233);
    private static final Color OSCURO_TEXTO_BOTON = Color.WHITE;
    private static final Color OSCURO_CAMPO_FONDO = new Color(22, 40, 30);
    private static final Color OSCURO_BARRA_CANAL = new Color(217, 226, 220);
    private static final Color OSCURO_BORDE_BOTON = new Color(46, 91, 63);

    // Alterna el tema y recolorea todas las ventanas abiertas
    public static void alternarTema() {
        if (temaActual == TEMA_CLARO) {
            temaActual = TEMA_OSCURO;
        } else {
            temaActual = TEMA_CLARO;
        }
        configurarUIManager();
        Frame[] ventanas = Frame.getFrames();
        for (int i = 0; i < ventanas.length; i++) {
            if (ventanas[i].isDisplayable()) {
                aplicarTema(ventanas[i]);
                actualizarBotonesTema(ventanas[i]);
            }
        }
    }

    // Configura UIManager para que JOptionPane y componentes nuevos hereden el tema
    private static void configurarUIManager() {
        UIManager.put("OptionPane.background", getColorFondoPanel());
        UIManager.put("OptionPane.messageForeground", getColorTexto());
        UIManager.put("Panel.background", getColorFondoPanel());
        UIManager.put("Label.foreground", getColorTexto());
        UIManager.put("TextField.background", getColorCampoFondo());
        UIManager.put("TextField.foreground", getColorTexto());
        UIManager.put("TextArea.background", getColorCampoFondo());
        UIManager.put("TextArea.foreground", getColorTexto());
        UIManager.put("Table.background", getColorFondoPanel());
        UIManager.put("Table.foreground", getColorTexto());
    }

    // Aplica el tema actual a una ventana de forma recursiva
    public static void aplicarTema(Window ventana) {
        if (ventana instanceof Frame) {
            ((Frame) ventana).setBackground(getColorFondoVentana());
        }
        if (ventana instanceof Container) {
            recorrerYRecolorear((Container) ventana);
        }
        ventana.repaint();
    }

    // Recorre recursivamente el árbol de componentes y aplica colores
    private static void recorrerYRecolorear(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            Component c = componentes[i];

            if (c instanceof JButton) {
                JButton boton = (JButton) c;
                // Los botones usan Pantone con texto blanco (intocable)
                boton.setBackground(PANTONE_5535_C);
                boton.setForeground(getColorTextoBoton());
                if (temaActual == TEMA_OSCURO) {
                    boton.setBorder(javax.swing.BorderFactory.createLineBorder(OSCURO_BORDE_BOTON, 1));
                } else {
                    boton.setBorder(javax.swing.BorderFactory.createLineBorder(PANTONE_5535_C, 1));
                }
            } else if (c instanceof JLabel) {
                JLabel etiqueta = (JLabel) c;
                etiqueta.setForeground(getColorTexto());
            } else if (c instanceof JTextField || c instanceof JPasswordField) {
                JTextField campo = (JTextField) c;
                campo.setBackground(getColorCampoFondo());
                campo.setForeground(getColorTexto());
                campo.setCaretColor(getColorTexto());
            } else if (c instanceof JTextArea) {
                JTextArea area = (JTextArea) c;
                area.setBackground(getColorCampoFondo());
                area.setForeground(getColorTexto());
                area.setCaretColor(getColorTexto());
            } else if (c instanceof JComboBox) {
                JComboBox combo = (JComboBox) c;
                combo.setBackground(getColorCampoFondo());
                combo.setForeground(getColorTexto());
            } else if (c instanceof JTable) {
                JTable tabla = (JTable) c;
                tabla.setBackground(getColorFondoPanel());
                tabla.setForeground(getColorTexto());
                tabla.setSelectionBackground(PANTONE_5535_C);
                tabla.setSelectionForeground(Color.WHITE);
                JTableHeader encabezado = tabla.getTableHeader();
                if (encabezado != null) {
                    encabezado.setBackground(PANTONE_5535_C);
                    encabezado.setForeground(Color.WHITE);
                }
            } else if (c instanceof JProgressBar) {
                JProgressBar barra = (JProgressBar) c;
                // Relleno Pantone intacto, canal claro para contraste
                barra.setForeground(PANTONE_5535_C);
                barra.setBackground(getColorBarraCanal());
            } else if (c instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) c;
                scroll.getViewport().setBackground(getColorFondoPanel());
            } else if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                // Solo si el panel es opaco
                if (panel.isOpaque()) {
                    panel.setBackground(getColorFondoPanel());
                }
            }

            // Recursión para contenedores
            if (c instanceof Container) {
                recorrerYRecolorear((Container) c);
            }
        }
    }

    // Actualiza la etiqueta de todos los BotonTema encontrados
    private static void actualizarBotonesTema(Container contenedor) {
        Component[] componentes = contenedor.getComponents();
        for (int i = 0; i < componentes.length; i++) {
            Component c = componentes[i];
            if (c instanceof BotonTema) {
                ((BotonTema) c).actualizarEtiqueta();
            }
            if (c instanceof Container) {
                actualizarBotonesTema((Container) c);
            }
        }
    }

    // Getters del tema actual
    public static boolean esTemaOscuro() {
        return temaActual == TEMA_OSCURO;
    }

    public static Color getColorFondoVentana() {
        return temaActual == TEMA_CLARO ? CLARO_FONDO_VENTANA : OSCURO_FONDO_VENTANA;
    }

    public static Color getColorFondoPanel() {
        return temaActual == TEMA_CLARO ? CLARO_FONDO_PANEL : OSCURO_FONDO_PANEL;
    }

    public static Color getColorTexto() {
        return temaActual == TEMA_CLARO ? CLARO_TEXTO : OSCURO_TEXTO;
    }

    public static Color getColorTextoBoton() {
        return temaActual == TEMA_CLARO ? CLARO_TEXTO_BOTON : OSCURO_TEXTO_BOTON;
    }

    public static Color getColorCampoFondo() {
        return temaActual == TEMA_CLARO ? CLARO_CAMPO_FONDO : OSCURO_CAMPO_FONDO;
    }

    public static Color getColorBarraCanal() {
        return temaActual == TEMA_CLARO ? CLARO_BARRA_CANAL : OSCURO_BARRA_CANAL;
    }

    // Para PanelGrafico (colores de ejes y texto)
    public static Color getColorTextoGrafico() {
        return getColorTexto();
    }

    public static Color getColorEjeGrafico() {
        return temaActual == TEMA_CLARO ? new Color(120, 120, 120) : new Color(160, 160, 160);
    }
}
