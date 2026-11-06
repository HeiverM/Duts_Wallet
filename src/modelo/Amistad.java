// Clase que representa la relación de amistad entre un usuario y otro estudiante.
package modelo;

import interfaces.Persistible;

// Aquí se aplica encapsulamiento: los atributos son privados y no tienen setters,
// porque una amistad no se modifica después de creada.
// Implementa Persistible para guardarse en amigos.txt
public class Amistad implements Persistible {

    private String documentoUsuario;
    private String documentoAmigo;

    public Amistad(String documentoUsuario, String documentoAmigo) {
        this.documentoUsuario = documentoUsuario;
        this.documentoAmigo = documentoAmigo;
    }

    public String getDocumentoUsuario() {
        return documentoUsuario;
    }

    public String getDocumentoAmigo() {
        return documentoAmigo;
    }

    // Formato: documentoUsuario;documentoAmigo
    @Override
    public String aLineaTexto() {
        return documentoUsuario + ";" + documentoAmigo;
    }
}
