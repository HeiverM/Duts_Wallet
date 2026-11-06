// Clase abstracta que representa a cualquier usuario del sistema (estudiante o administrador).
package modelo;

import interfaces.Persistible;

// Aquí se aplica abstracción: Usuario es "abstract", por eso no se pueden crear objetos
// con new Usuario(...). Solo sirve como molde común para Estudiante y Administrador.
// Implementa Persistible, así que todo usuario debe saber convertirse en una línea de texto;
// el método aLineaTexto() lo escribe cada clase hija a su manera.
public abstract class Usuario implements Persistible {

    // Aquí se aplica encapsulamiento: los atributos son privados
    // y solo se accede a ellos con getters (y setContrasena)
    private String documento;
    private String nombreCompleto;
    private String nombreUsuario;
    private String contrasena;
    private String preguntaSeguridad;
    private String respuestaSeguridad;

    // Constructor con todos los atributos comunes a cualquier usuario
    public Usuario(String documento, String nombreCompleto, String nombreUsuario,
                   String contrasena, String preguntaSeguridad, String respuestaSeguridad) {
        this.documento = documento;
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.preguntaSeguridad = preguntaSeguridad;
        this.respuestaSeguridad = respuestaSeguridad;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public String getPreguntaSeguridad() {
        return preguntaSeguridad;
    }

    public String getRespuestaSeguridad() {
        return respuestaSeguridad;
    }

    // Único dato modificable: se usa al recuperar la contraseña
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    // Métodos abstractos: se declaran aquí pero NO se implementan.
    // Cada clase hija está obligada a escribir su propia versión.
    public abstract String getRol();

    public abstract String getResumenPerfil();
}
