// Clase que administra las notificaciones de los usuarios y las guarda en datos/notificaciones.txt.
package persistencia;

import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Inscripcion;
import modelo.Notificacion;
import modelo.Novedad;

public class GestorNotificaciones {

    private static final String RUTA = "datos/notificaciones.txt";

    // Días de anticipación para avisar que un evento está próximo
    private static final int DIAS_PROXIMIDAD = 3;

    private ArrayList<Notificacion> notificaciones;

    public GestorNotificaciones() {
        notificaciones = new ArrayList<Notificacion>();
        cargar();
    }

    // Lee el archivo y crea un objeto Notificacion por cada línea
    private void cargar() {
        ArrayList<String> lineas = ArchivoTexto.leerLineas(RUTA);
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes.length == 3) {
                notificaciones.add(new Notificacion(partes[0], LocalDate.parse(partes[1]), partes[2]));
            }
        }
    }

    // Reescribe el archivo completo con la lista actual
    private void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        for (Notificacion notificacion : notificaciones) {
            lineas.add(notificacion.aLineaTexto());
        }
        ArchivoTexto.escribirLineas(RUTA, lineas);
    }

    // Crea una notificación con la fecha de hoy para el usuario indicado
    public void agregar(String documento, String mensaje) {
        notificaciones.add(new Notificacion(documento, LocalDate.now(), mensaje));
        guardar();
    }

    // Devuelve las notificaciones de un usuario en orden cronológico
    public ArrayList<Notificacion> listarPorUsuario(String documento) {
        ArrayList<Notificacion> resultado = new ArrayList<Notificacion>();
        for (Notificacion notificacion : notificaciones) {
            if (notificacion.getDocumentoDestino().equals(documento)) {
                resultado.add(notificacion);
            }
        }
        return resultado;
    }

    // Avisa al estudiante de los eventos en los que está INSCRITO y que son de hoy a 3 días.
    // Recibe la lista de inscripciones del estudiante y el gestor para buscar cada novedad.
    public void generarProximidad(String documento, ArrayList<Inscripcion> inscripciones,
                                  GestorNovedades gestorNovedades) {
        long hoy = LocalDate.now().toEpochDay();
        for (Inscripcion inscripcion : inscripciones) {
            Novedad novedad = gestorNovedades.buscarPorCodigo(inscripcion.getCodigoNovedad());
            if (novedad != null && inscripcion.getEstado().equals(Inscripcion.ESTADO_INSCRITO)) {
                long diasFaltantes = novedad.getFecha().toEpochDay() - hoy;
                String mensaje = "Recordatorio: el evento " + novedad.getNombre() + " es el " + novedad.getFecha();
                // Solo se crea si el evento es de hoy a 3 días y no se había avisado antes
                if (diasFaltantes >= 0 && diasFaltantes <= DIAS_PROXIMIDAD && !existe(documento, mensaje)) {
                    agregar(documento, mensaje);
                }
            }
        }
    }

    // Revisa si el usuario ya tiene una notificación con ese mismo mensaje
    private boolean existe(String documento, String mensaje) {
        for (Notificacion notificacion : notificaciones) {
            if (notificacion.getDocumentoDestino().equals(documento) && notificacion.getMensaje().equals(mensaje)) {
                return true;
            }
        }
        return false;
    }
}
