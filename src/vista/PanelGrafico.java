// Panel que dibuja un gráfico de barras comparando ingresos y egresos de DUTS por período.
package vista;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

// Aquí se aplica herencia: PanelGrafico es un JPanel (un contenedor Swing) y reutiliza
// todo su comportamiento de dibujo. Sobrescribe paintComponent para dibujar encima del panel.
public class PanelGrafico extends JPanel {

    // Datos que se dibujan: un valor por cada bloque de días (6 bloques de 10 días)
    private int[] ingresos;
    private int[] egresos;

    // Colores de las dos series de barras (fijos, no dependen del tema)
    private static final Color COLOR_INGRESOS = new Color(46, 125, 50);
    private static final Color COLOR_EGRESOS = new Color(198, 40, 40);

    public PanelGrafico() {
        // Se empiezan con 6 valores en cero para que el panel nunca sea nulo
        ingresos = new int[6];
        egresos = new int[6];
        // Fondo dinámico según el tema (se actualiza al repintar)
    }

    // Recibe los 6 valores de cada serie y prepara el repintado de la gráfica
    public void setDatos(int[] ingresos, int[] egresos) {
        this.ingresos = copiarArreglo(ingresos);
        this.egresos = copiarArreglo(egresos);
    }

    // Copia el arreglo recibido para que el panel no cambie si el llamador lo modifica
    private int[] copiarArreglo(int[] origen) {
        int[] copia = new int[origen.length];
        for (int i = 0; i < origen.length; i++) {
            copia[i] = origen[i];
        }
        return copia;
    }

    // Encuentra el valor más alto de las dos series para escalar las barras
    private int valorMaximo() {
        int maximo = 0;
        for (int i = 0; i < ingresos.length; i++) {
            if (ingresos[i] > maximo) {
                maximo = ingresos[i];
            }
            if (egresos[i] > maximo) {
                maximo = egresos[i];
            }
        }
        return maximo;
    }

    // Dibuja la gráfica: eje, barras de ingresos y de egresos, y la leyenda
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Aplica el fondo según el tema actual
        setBackground(util.GestorTema.getColorFondoPanel());

        int ancho = getWidth();
        int alto = getHeight();

        // Título de la gráfica (texto dinámico según tema)
        g.setColor(util.GestorTema.getColorTextoGrafico());
        g.drawString("Ingresos y egresos de DUTS por bloques de 10 días", 15, 20);

        // Marco del área donde se dibujan las barras
        int margenIzquierdo = 55;
        int margenSuperior = 35;
        int margenInferior = 45;
        int areaAncho = ancho - margenIzquierdo - 20;
        int areaAlto = alto - margenSuperior - margenInferior;

        // Ejes del gráfico (color dinámico según tema)
        g.setColor(util.GestorTema.getColorEjeGrafico());
        g.drawLine(margenIzquierdo, margenSuperior, margenIzquierdo, margenSuperior + areaAlto);
        g.drawLine(margenIzquierdo, margenSuperior + areaAlto, margenIzquierdo + areaAncho, margenSuperior + areaAlto);

        int maximo = valorMaximo();
        if (maximo <= 0) {
            maximo = 1;
        }

        // Se dibuja una barra de ingresos y una de egresos por cada bloque
        int cantidadBloques = ingresos.length;
        int anchoBloque = areaAncho / cantidadBloques;
        int anchoBarra = anchoBloque / 3;
        for (int i = 0; i < cantidadBloques; i++) {
            int xBloque = margenIzquierdo + (i * anchoBloque);
            int altoIngreso = areaAlto * ingresos[i] / maximo;
            int altoEgreso = areaAlto * egresos[i] / maximo;

            // Barra de ingresos (verde)
            g.setColor(COLOR_INGRESOS);
            g.fillRect(xBloque + 5, margenSuperior + areaAlto - altoIngreso, anchoBarra, altoIngreso);
            g.drawRect(xBloque + 5, margenSuperior + areaAlto - altoIngreso, anchoBarra, altoIngreso);

            // Barra de egresos (roja)
            g.setColor(COLOR_EGRESOS);
            g.fillRect(xBloque + 10 + anchoBarra, margenSuperior + areaAlto - altoEgreso, anchoBarra, altoEgreso);
            g.drawRect(xBloque + 10 + anchoBarra, margenSuperior + areaAlto - altoEgreso, anchoBarra, altoEgreso);

            // Etiqueta del bloque: número de días que representa
            g.setColor(util.GestorTema.getColorTextoGrafico());
            g.drawString("D-" + ((cantidadBloques - i) * 10), xBloque + 8, margenSuperior + areaAlto + 18);
        }

        // Escala vertical: valor máximo en la parte de arriba (colores dinámicos)
        g.setColor(util.GestorTema.getColorEjeGrafico());
        g.drawString(String.valueOf(maximo), 10, margenSuperior + 10);
        g.drawString("0", 30, margenSuperior + areaAlto);

        // Leyenda de colores
        g.setColor(COLOR_INGRESOS);
        g.fillRect(margenIzquierdo + 10, alto - 22, 12, 12);
        g.setColor(util.GestorTema.getColorTextoGrafico());
        g.drawString("Ingresos", margenIzquierdo + 27, alto - 12);
        g.setColor(COLOR_EGRESOS);
        g.fillRect(margenIzquierdo + 90, alto - 22, 12, 12);
        g.setColor(util.GestorTema.getColorTextoGrafico());
        g.drawString("Egresos", margenIzquierdo + 107, alto - 12);
    }
}
