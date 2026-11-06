// Interfaz que deben implementar todas las clases que se guardan en archivos de texto.
package interfaces;

// Aquí se aplica el concepto de interfaz: define QUÉ debe hacer una clase,
// pero no CÓMO lo hace. Cada clase que la implemente decide su propio formato.
public interface Persistible {

    // Convierte el objeto en una línea de texto con los campos separados por ";"
    // para poder guardarlo en su archivo .txt
    String aLineaTexto();
}

