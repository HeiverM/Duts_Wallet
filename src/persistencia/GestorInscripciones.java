// Clase que administra las inscripciones a novedades y las guarda en datos/inscripciones.txt.
package persistencia;

import excepciones.ValidacionException;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Estudiante;
import modelo.Inscripcion;
import modelo.Movimiento;
import modelo.Novedad;

public class GestorInscripciones {

    private static final String RUTA = "datos/inscripciones.txt";

    // Porcentaje del valor de la novedad que se descuenta si el estudiante no asiste
    public static final int PORCENTAJE_PENALIZACION = 20;

    private ArrayList<Inscripcion> inscripciones;

    // Gestores que se necesitan para mover DUTS, avisar al estudiante y buscar datos
    private GestorMovimientos gestorMovimientos;
    private GestorNotificaciones gestorNotificaciones;
    private GestorUsuarios gestorUsuarios;
    private GestorNovedades gestorNovedades;

    public GestorInscripciones(GestorMovimientos gestorMovimientos, GestorNotificaciones gestorNotificaciones,
                               GestorUsuarios gestorUsuarios, GestorNovedades gestorNovedades) {
        this.gestorMovimientos = gestorMovimientos;
        this.gestorNotificaciones = gestorNotificaciones;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorNovedades = gestorNovedades;
        inscripciones = new ArrayList<Inscripcion>();
        cargar();
    }

    // Lee el archivo y crea un objeto Inscripcion por cada línea
    private void cargar() {
        ArrayList<String> lineas = ArchivoTexto.leerLineas(RUTA);
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes.length == 4) {
                inscripciones.add(new Inscripcion(partes[0], partes[1], partes[2], LocalDate.parse(partes[3])));
            }
        }
    }

    // Reescribe el archivo completo con la lista actual
    private void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        for (Inscripcion inscripcion : inscripciones) {
            lineas.add(inscripcion.aLineaTexto());
        }
        ArchivoTexto.escribirLineas(RUTA, lineas);
    }

    // Inscribe al estudiante. Si tenía una inscripción cancelada, la reactiva
    public void inscribir(String codigoNovedad, String documentoEstudiante) throws ValidacionException {
        Novedad novedad = buscarNovedad(codigoNovedad);
        if (novedad.getFecha().isBefore(LocalDate.now())) {
            throw new ValidacionException("Solo puede inscribirse en novedades de hoy en adelante");
        }
        Inscripcion existente = buscar(codigoNovedad, documentoEstudiante);
        if (existente == null) {
            inscripciones.add(new Inscripcion(codigoNovedad, documentoEstudiante,
                    Inscripcion.ESTADO_INSCRITO, LocalDate.now()));
        } else if (existente.getEstado().equals(Inscripcion.ESTADO_CANCELADA)) {
            existente.setEstado(Inscripcion.ESTADO_INSCRITO);
        } else {
            // Aquí se valida que no haya inscripciones duplicadas
            throw new ValidacionException("Ya tiene una inscripción registrada en esta novedad");
        }
        guardar();
        gestorNotificaciones.agregar(documentoEstudiante, "Tu inscripción a " + novedad.getNombre() + " fue aceptada");
    }

    // Cancela la inscripción (solo si está en estado INSCRITO)
    public void cancelar(String codigoNovedad, String documentoEstudiante) throws ValidacionException {
        Inscripcion inscripcion = buscarActiva(codigoNovedad, documentoEstudiante);
        inscripcion.setEstado(Inscripcion.ESTADO_CANCELADA);
        guardar();
        Novedad novedad = gestorNovedades.buscarPorCodigo(codigoNovedad);
        String nombre = codigoNovedad;
        if (novedad != null) {
            nombre = novedad.getNombre();
        }
        gestorNotificaciones.agregar(documentoEstudiante, "Cancelaste tu inscripción a " + nombre);
    }

    // El estudiante asistió: se le acreditan los DUTS de la novedad con un movimiento EVENTO
    public void marcarAsistencia(String codigoNovedad, String documentoEstudiante) throws ValidacionException {
        Inscripcion inscripcion = buscarActiva(codigoNovedad, documentoEstudiante);
        Novedad novedad = buscarNovedad(codigoNovedad);
        Estudiante estudiante = buscarEstudiante(documentoEstudiante);
        if (novedad.getValorDuts() > 0) {
            estudiante.sumarDuts(novedad.getValorDuts());
            gestorMovimientos.registrarMovimiento(Movimiento.TIPO_EVENTO, Movimiento.SISTEMA,
                    documentoEstudiante, novedad.getValorDuts(), "Asistencia a " + novedad.getNombre());
            gestorUsuarios.guardar();
        }
        inscripcion.setEstado(Inscripcion.ESTADO_ASISTIO);
        guardar();
    }

    // El estudiante no asistió: se le descuenta el 20 % del valor de la novedad (o lo que tenga)
    public void marcarInasistencia(String codigoNovedad, String documentoEstudiante) throws ValidacionException {
        Inscripcion inscripcion = buscarActiva(codigoNovedad, documentoEstudiante);
        Novedad novedad = buscarNovedad(codigoNovedad);
        Estudiante estudiante = buscarEstudiante(documentoEstudiante);
        int penalizacion = novedad.getValorDuts() * PORCENTAJE_PENALIZACION / 100;
        // Si el saldo no alcanza, se descuenta solo lo que haya
        if (penalizacion > estudiante.getSaldoDuts()) {
            penalizacion = estudiante.getSaldoDuts();
        }
        if (penalizacion > 0) {
            estudiante.restarDuts(penalizacion);
            gestorMovimientos.registrarMovimiento(Movimiento.TIPO_PENALIZACION, documentoEstudiante,
                    Movimiento.SISTEMA, penalizacion, "Inasistencia a " + novedad.getNombre());
            gestorUsuarios.guardar();
        }
        inscripcion.setEstado(Inscripcion.ESTADO_NO_ASISTIO);
        guardar();
    }

    // Devuelve todas las inscripciones de un estudiante (con cualquier estado)
    public ArrayList<Inscripcion> listarPorEstudiante(String documentoEstudiante) {
        ArrayList<Inscripcion> resultado = new ArrayList<Inscripcion>();
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getDocumentoEstudiante().equals(documentoEstudiante)) {
                resultado.add(inscripcion);
            }
        }
        return resultado;
    }

    // Devuelve todas las inscripciones de una novedad (con cualquier estado)
    public ArrayList<Inscripcion> listarInscritos(String codigoNovedad) {
        ArrayList<Inscripcion> resultado = new ArrayList<Inscripcion>();
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getCodigoNovedad().equals(codigoNovedad)) {
                resultado.add(inscripcion);
            }
        }
        return resultado;
    }

    // Busca la inscripción de un estudiante en una novedad. Devuelve null si no existe
    private Inscripcion buscar(String codigoNovedad, String documentoEstudiante) {
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getCodigoNovedad().equals(codigoNovedad)
                    && inscripcion.getDocumentoEstudiante().equals(documentoEstudiante)) {
                return inscripcion;
            }
        }
        return null;
    }

    // Busca la inscripción y verifica que esté en estado INSCRITO
    private Inscripcion buscarActiva(String codigoNovedad, String documentoEstudiante) throws ValidacionException {
        Inscripcion inscripcion = buscar(codigoNovedad, documentoEstudiante);
        if (inscripcion == null || !inscripcion.getEstado().equals(Inscripcion.ESTADO_INSCRITO)) {
            throw new ValidacionException("La inscripción no existe o no está en estado INSCRITO");
        }
        return inscripcion;
    }

    // Busca la novedad y lanza un error si no existe
    private Novedad buscarNovedad(String codigoNovedad) throws ValidacionException {
        Novedad novedad = gestorNovedades.buscarPorCodigo(codigoNovedad);
        if (novedad == null) {
            throw new ValidacionException("La novedad no existe");
        }
        return novedad;
    }

    // Busca el estudiante y lanza un error si no existe
    private Estudiante buscarEstudiante(String documento) throws ValidacionException {
        Estudiante estudiante = gestorUsuarios.buscarEstudiantePorDocumento(documento);
        if (estudiante == null) {
            throw new ValidacionException("El estudiante no existe");
        }
        return estudiante;
    }
}
