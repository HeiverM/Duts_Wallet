// Clase que administra los usuarios en memoria y los guarda en datos/usuarios.txt.
package persistencia;

import excepciones.ValidacionException;
import java.util.ArrayList;
import modelo.Administrador;
import modelo.Estudiante;
import modelo.Usuario;
import util.Validador;

public class GestorUsuarios {

    private static final String RUTA = "datos/usuarios.txt";

    // Aquí se aplica polimorfismo: la lista es de tipo Usuario,
    // pero guarda objetos Estudiante y Administrador
    private ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        usuarios = new ArrayList<Usuario>();
        cargar();
    }

    // Lee el archivo y crea un objeto por cada línea según el rol (primer campo)
    private void cargar() {
        ArrayList<String> lineas = ArchivoTexto.leerLineas(RUTA);
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes[0].equals("ESTUDIANTE") && partes.length == 9) {
                usuarios.add(new Estudiante(partes[1], partes[2], partes[3], partes[4],
                        partes[5], partes[6], partes[7], Integer.parseInt(partes[8])));
            } else if (partes[0].equals("ADMINISTRADOR") && partes.length == 7) {
                usuarios.add(new Administrador(partes[1], partes[2], partes[3],
                        partes[4], partes[5], partes[6]));
            }
        }
        // Si el archivo no existe (o está vacío) se crea el administrador por defecto
        if (usuarios.isEmpty()) {
            usuarios.add(new Administrador("1000", "Administrador DUTS", "admin", "admin123",
                    "¿Ciudad donde nació?", "Bucaramanga"));
            guardar();
        }
    }

    // Reescribe el archivo completo. Es público porque GestorMovimientos y
    // GestorInscripciones cambian saldos y deben guardar los usuarios.
    // Aquí se aplica polimorfismo: se llama aLineaTexto() sin saber si es Estudiante o Administrador
    public void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        for (Usuario usuario : usuarios) {
            lineas.add(usuario.aLineaTexto());
        }
        ArchivoTexto.escribirLineas(RUTA, lineas);
    }

    // Registra un estudiante nuevo validando que no esté duplicado
    public void registrar(Estudiante estudiante) throws ValidacionException {
        if (buscarPorDocumento(estudiante.getDocumento()) != null) {
            throw new ValidacionException("Ya existe un usuario con ese documento");
        }
        if (buscarPorNombreUsuario(estudiante.getNombreUsuario()) != null) {
            throw new ValidacionException("Ese nombre de usuario ya está en uso");
        }
        usuarios.add(estudiante);
        guardar();
    }

    // Devuelve el usuario si el identificador y la contraseña coinciden.
    // Los estudiantes pueden iniciar sesión con su documento o con su nombre de usuario.
    // El administrador es la excepción: solo entra con su nombre de usuario.
    public Usuario iniciarSesion(String identificador, String contrasena) throws ValidacionException {
        if (identificador == null || identificador.trim().isEmpty()) {
            throw new ValidacionException("Credenciales incorrectas");
        }
        String dato = identificador.trim();
        Usuario usuario = null;

        // 1) Se busca por documento: es la forma principal de acceso del estudiante
        Usuario porDocumento = buscarPorDocumento(dato);
        if (porDocumento != null && porDocumento instanceof Estudiante) {
            usuario = porDocumento;
        }

        // 2) Si no se encontró un estudiante por documento, se busca por nombre de usuario
        // (así entra el administrador, que es la excepción, y también el alias del estudiante)
        if (usuario == null) {
            usuario = buscarPorNombreUsuario(dato);
        }

        if (usuario == null || !usuario.getContrasena().equals(contrasena)) {
            throw new ValidacionException("Credenciales incorrectas");
        }
        return usuario;
    }

    // Busca un usuario por su documento. Devuelve null si no existe
    public Usuario buscarPorDocumento(String documento) {
        for (Usuario usuario : usuarios) {
            if (usuario.getDocumento().equals(documento.trim())) {
                return usuario;
            }
        }
        return null;
    }

    // Busca un usuario por su nombre de usuario (sin importar mayúsculas). Devuelve null si no existe
    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario.trim())) {
                return usuario;
            }
        }
        return null;
    }

    // Busca un estudiante por su documento. Devuelve null si no existe o si es un administrador
    public Estudiante buscarEstudiantePorDocumento(String documento) {
        Usuario usuario = buscarPorDocumento(documento);
        // instanceof pregunta si el objeto realmente es un Estudiante antes de convertirlo
        if (usuario instanceof Estudiante) {
            return (Estudiante) usuario;
        }
        return null;
    }

    // Devuelve solo los estudiantes (sin los administradores)
    public ArrayList<Estudiante> listarEstudiantes() {
        ArrayList<Estudiante> estudiantes = new ArrayList<Estudiante>();
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Estudiante) {
                estudiantes.add((Estudiante) usuario);
            }
        }
        return estudiantes;
    }

    // Cambia la contraseña si la respuesta de seguridad es correcta
    public void restablecerContrasena(String nombreUsuario, String respuesta, String nuevaContrasena)
            throws ValidacionException {
        Usuario usuario = buscarPorNombreUsuario(nombreUsuario);
        if (usuario == null) {
            throw new ValidacionException("El usuario no existe");
        }
        // Se compara sin importar mayúsculas ni espacios al inicio o al final
        if (!usuario.getRespuestaSeguridad().trim().equalsIgnoreCase(respuesta.trim())) {
            throw new ValidacionException("La respuesta de seguridad es incorrecta");
        }
        Validador.validarNoVacio(nuevaContrasena, "Nueva contraseña");
        Validador.validarSinPuntoYComa(nuevaContrasena, "Nueva contraseña");
        usuario.setContrasena(nuevaContrasena);
        guardar();
    }
}
