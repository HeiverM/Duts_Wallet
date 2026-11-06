// Clase que representa una notificación dirigida a un usuario (aparece en su bandeja).
package modelo;

import interfaces.Persistible;
import java.time.LocalDate;

// Aquí se aplica encapsulamiento: los atributos son privados y no tienen setters,
// porque una notificación no se modifica después de creada.
// Implementa Persistible para guardarse en notificaciones.txt
public class Notificacion implements Persistible {

    private String documentoDestino;
    private LocalDate fecha;
    private String mensaje;

    public Notificacion(String documentoDestino, LocalDate fecha, String mensaje) {
        this.documentoDestino = documentoDestino;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    public String getDocumentoDestino() {
        return documentoDestino;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    // Formato: documentoDestino;fecha;mensaje
    @Override
    public String aLineaTexto() {
        return documentoDestino + ";" + fecha + ";" + mensaje;
    }
}
