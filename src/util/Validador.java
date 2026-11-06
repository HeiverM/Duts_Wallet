// Clase con métodos de apoyo para validar los datos que el usuario escribe en los formularios.
package util;

import excepciones.ValidacionException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Aquí se aplica encapsulamiento: todas las reglas de validación quedan en un solo lugar
// y las ventanas solo llaman a estos métodos. Son "static" porque no necesitan
// crear un objeto Validador para usarse: se llaman como Validador.validarNoVacio(...)
public class Validador {

    // Verifica que el texto no sea nulo ni esté formado solo por espacios
    public static void validarNoVacio(String valor, String nombreCampo) throws ValidacionException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo " + nombreCampo + " no puede estar vacío");
        }
    }

    // Verifica que el texto no tenga ";" porque ese carácter separa los campos en los archivos .txt
    public static void validarSinPuntoYComa(String valor, String nombreCampo) throws ValidacionException {
        if (valor != null && valor.contains(";")) {
            throw new ValidacionException("El campo " + nombreCampo + " no puede contener el carácter ;");
        }
    }

    // Convierte el texto en un número entero y verifica que sea mayor que 0
    public static int validarEnteroPositivo(String texto, String nombreCampo) throws ValidacionException {
        validarNoVacio(texto, nombreCampo);
        int numero;
        // Aquí se controla el error que lanza Java cuando el texto no es un número
        try {
            numero = Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo " + nombreCampo + " debe ser un número entero");
        }
        if (numero <= 0) {
            throw new ValidacionException("El campo " + nombreCampo + " debe ser mayor que 0");
        }
        return numero;
    }

    // Convierte el texto en una fecha con formato yyyy-MM-dd (por ejemplo 2026-11-05)
    public static LocalDate validarFecha(String texto, String nombreCampo) throws ValidacionException {
        validarNoVacio(texto, nombreCampo);
        // Aquí se controla el error que lanza Java cuando la fecha no existe o tiene otro formato
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new ValidacionException("El campo " + nombreCampo + " debe ser una fecha válida con formato yyyy-MM-dd");
        }
    }

    // Verifica que la fecha inicial no sea posterior a la fecha final
    public static void validarRangoFechas(LocalDate desde, LocalDate hasta) throws ValidacionException {
        // Solo se compara si las dos fechas existen (una fecha vacía significa "sin filtro")
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ValidacionException("La fecha Desde no puede ser posterior a la fecha Hasta");
        }
    }
}
