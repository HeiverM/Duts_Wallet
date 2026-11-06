// Excepción propia del proyecto para avisar errores de validación de datos.
package excepciones;

// Aquí se aplica herencia: ValidacionException reutiliza todo el comportamiento
// de la clase Exception de Java (por ejemplo el método getMessage()).
// Se usa para campos vacíos, credenciales incorrectas, registros duplicados,
// saldo insuficiente y fechas no válidas.
public class ValidacionException extends Exception {

    // Constructor que recibe el mensaje que se le mostrará al usuario
    public ValidacionException(String mensaje) {
        // Se envía el mensaje al constructor de la clase padre (Exception)
        super(mensaje);
    }
}
