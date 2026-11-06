// Clase que registra en un archivo oculto las operaciones importantes del sistema (auditoría).
package persistencia;

import java.time.LocalDateTime;

// Ninguna pantalla muestra este archivo: sirve para revisar qué pasó si aparece un error.
public class GestorAuditoria {

    private static final String RUTA = "datos/auditoria.txt";

    // Agrega una línea con el formato fechaHora;usuario;accion;detalle
    // Es "static" para poder llamarlo desde cualquier ventana sin crear un objeto
    public static void registrar(String usuario, String accion, String detalle) {
        // withNano(0) quita las fracciones de segundo para que la fecha sea más fácil de leer
        String fechaHora = LocalDateTime.now().withNano(0).toString();
        // Si el usuario o el detalle traen ";" se cambia por "," para no dañar el formato de la línea
        String usuarioLimpio = usuario.replace(";", ",");
        String detalleLimpio = detalle.replace(";", ",");
        ArchivoTexto.agregarLinea(RUTA, fechaHora + ";" + usuarioLimpio + ";" + accion + ";" + detalleLimpio);
    }
}
