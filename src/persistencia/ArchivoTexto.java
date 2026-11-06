// Clase con métodos de apoyo para leer y escribir los archivos de texto (.txt y .csv) del proyecto.
package persistencia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

// Aquí se aplica encapsulamiento: todo el manejo de archivos queda en esta clase
// y los gestores solo trabajan con listas de líneas (ArrayList<String>).
// Los métodos son "static" porque no se necesita crear un objeto ArchivoTexto para usarlos.
public class ArchivoTexto {

    // Lee todas las líneas de un archivo. Si el archivo no existe devuelve una lista vacía
    public static ArrayList<String> leerLineas(String ruta) {
        ArrayList<String> lineas = new ArrayList<String>();
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return lineas;
        }
        try {
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea = lector.readLine();
            while (linea != null) {
                // Las líneas vacías se ignoran para no crear registros sin datos
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
                linea = lector.readLine();
            }
            lector.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo leer el archivo " + ruta);
        }
        return lineas;
    }

    // Escribe todas las líneas en el archivo, reemplazando lo que tenía antes
    public static void escribirLineas(String ruta, ArrayList<String> lineas) {
        crearCarpeta(ruta);
        try {
            PrintWriter escritor = new PrintWriter(new FileWriter(ruta));
            for (String linea : lineas) {
                escritor.println(linea);
            }
            escritor.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo escribir el archivo " + ruta);
        }
    }

    // Agrega una línea al final del archivo sin borrar lo que ya tenía
    public static void agregarLinea(String ruta, String linea) {
        crearCarpeta(ruta);
        try {
            // El "true" indica que se escribe al final del archivo (modo agregar)
            PrintWriter escritor = new PrintWriter(new FileWriter(ruta, true));
            escritor.println(linea);
            escritor.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No se pudo escribir el archivo " + ruta);
        }
    }

    // Crea la carpeta del archivo (datos, reportes o comprobantes) si todavía no existe
    private static void crearCarpeta(String ruta) {
        File carpeta = new File(ruta).getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }
    }
}
