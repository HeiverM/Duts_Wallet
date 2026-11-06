// Interfaz que deben implementar las clases que se pueden exportar a un archivo CSV.
package interfaces;

// Aquí se aplica el concepto de interfaz: cualquier clase que la implemente
// se compromete a saber convertirse en una fila de CSV.
public interface Exportable {

    // Convierte el objeto en una fila de texto con los campos separados por ","
    // para escribirla en un reporte CSV
    String aFilaCsv();
}
