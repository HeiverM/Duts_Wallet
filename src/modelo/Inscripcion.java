// Clase que representa la inscripción de un estudiante a una novedad institucional.
package modelo;

import interfaces.Persistible;
import java.time.LocalDate;

// Aquí se aplica encapsulamiento: solo el estado se puede cambiar con un setter.
// Implementa Persistible para guardarse en inscripciones.txt
public class Inscripcion implements Persistible {

    // Estados posibles de una inscripción (constantes String en lugar de enum)
    public static final String ESTADO_INSCRITO = "INSCRITO";
    public static final String ESTADO_CANCELADA = "CANCELADA";
    public static final String ESTADO_ASISTIO = "ASISTIO";
    public static final String ESTADO_NO_ASISTIO = "NO_ASISTIO";

    private String codigoNovedad;
    private String documentoEstudiante;
    private String estado;
    private LocalDate fechaInscripcion;

    public Inscripcion(String codigoNovedad, String documentoEstudiante, String estado,
                       LocalDate fechaInscripcion) {
        this.codigoNovedad = codigoNovedad;
        this.documentoEstudiante = documentoEstudiante;
        this.estado = estado;
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getCodigoNovedad() {
        return codigoNovedad;
    }

    public String getDocumentoEstudiante() {
        return documentoEstudiante;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    // El estado cambia al cancelar, reactivar o marcar asistencia
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Formato: codigoNovedad;documentoEstudiante;estado;fechaInscripcion
    @Override
    public String aLineaTexto() {
        return codigoNovedad + ";" + documentoEstudiante + ";" + estado + ";" + fechaInscripcion;
    }
}
