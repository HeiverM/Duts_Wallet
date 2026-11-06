// Clase que administra la lista de amigos de cada usuario y la guarda en datos/amigos.txt.
package persistencia;

import excepciones.ValidacionException;
import java.util.ArrayList;
import modelo.Amistad;
import modelo.Estudiante;

public class GestorAmigos {

    private static final String RUTA = "datos/amigos.txt";

    private ArrayList<Amistad> amistades;

    // Se necesita el gestor de usuarios para verificar que el amigo exista
    private GestorUsuarios gestorUsuarios;

    public GestorAmigos(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
        amistades = new ArrayList<Amistad>();
        cargar();
    }

    // Lee el archivo y crea un objeto Amistad por cada línea
    private void cargar() {
        ArrayList<String> lineas = ArchivoTexto.leerLineas(RUTA);
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes.length == 2) {
                amistades.add(new Amistad(partes[0], partes[1]));
            }
        }
    }

    // Reescribe el archivo completo con la lista actual
    private void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        for (Amistad amistad : amistades) {
            lineas.add(amistad.aLineaTexto());
        }
        ArchivoTexto.escribirLineas(RUTA, lineas);
    }

    // Agrega un amigo directamente (sin solicitud de aceptación)
    public void agregar(String documentoUsuario, String documentoAmigo) throws ValidacionException {
        if (gestorUsuarios.buscarEstudiantePorDocumento(documentoAmigo) == null) {
            throw new ValidacionException("No existe un estudiante con ese documento");
        }
        if (documentoUsuario.equals(documentoAmigo.trim())) {
            throw new ValidacionException("No puede agregarse a sí mismo como amigo");
        }
        // Aquí se valida que no haya amistades duplicadas
        for (Amistad amistad : amistades) {
            if (amistad.getDocumentoUsuario().equals(documentoUsuario)
                    && amistad.getDocumentoAmigo().equals(documentoAmigo.trim())) {
                throw new ValidacionException("Ese estudiante ya está en su lista de amigos");
            }
        }
        amistades.add(new Amistad(documentoUsuario, documentoAmigo.trim()));
        guardar();
    }

    // Devuelve los estudiantes que el usuario tiene como amigos
    public ArrayList<Estudiante> listarAmigos(String documentoUsuario) {
        ArrayList<Estudiante> amigos = new ArrayList<Estudiante>();
        for (Amistad amistad : amistades) {
            if (amistad.getDocumentoUsuario().equals(documentoUsuario)) {
                Estudiante amigo = gestorUsuarios.buscarEstudiantePorDocumento(amistad.getDocumentoAmigo());
                if (amigo != null) {
                    amigos.add(amigo);
                }
            }
        }
        return amigos;
    }
}
