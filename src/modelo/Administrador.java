// Clase que representa al administrador que gestiona los DUTS, las novedades y la asistencia.
package modelo;

// Aquí se aplica herencia: Administrador reutiliza todos los atributos de Usuario
// y no necesita atributos propios.
public class Administrador extends Usuario {

    // El constructor envía todos los datos a la clase padre con super(...)
    public Administrador(String documento, String nombreCompleto, String nombreUsuario,
                         String contrasena, String preguntaSeguridad, String respuestaSeguridad) {
        super(documento, nombreCompleto, nombreUsuario, contrasena, preguntaSeguridad, respuestaSeguridad);
    }

    // Aquí se aplica polimorfismo sobrescribiendo el método de la clase padre
    @Override
    public String getRol() {
        return "ADMINISTRADOR";
    }

    // Aquí se aplica polimorfismo: el administrador describe su perfil a su manera
    @Override
    public String getResumenPerfil() {
        return "Administrador: " + getNombreCompleto() + " (" + getNombreUsuario() + ")";
    }

    // Formato: ADMINISTRADOR;documento;nombreCompleto;nombreUsuario;contrasena;pregunta;respuesta
    @Override
    public String aLineaTexto() {
        return getRol() + ";" + getDocumento() + ";" + getNombreCompleto() + ";" + getNombreUsuario()
                + ";" + getContrasena() + ";" + getPreguntaSeguridad() + ";" + getRespuestaSeguridad();
    }
}
