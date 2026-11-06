// Clase que administra las novedades institucionales y las guarda en datos/novedades.txt.
package persistencia;

import excepciones.ValidacionException;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Novedad;

public class GestorNovedades {

    private static final String RUTA = "datos/novedades.txt";

    private ArrayList<Novedad> novedades;

    public GestorNovedades() {
        novedades = new ArrayList<Novedad>();
        cargar();
    }

    // Lee el archivo y crea un objeto Novedad por cada línea
    private void cargar() {
        ArrayList<String> lineas = ArchivoTexto.leerLineas(RUTA);
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes.length == 7) {
                novedades.add(new Novedad(partes[0], partes[1], partes[2], LocalDate.parse(partes[3]),
                        partes[4], Integer.parseInt(partes[5]), partes[6]));
            }
        }
        // Si el archivo no existe (o está vacío) se crean las 4 novedades de ejemplo
        if (novedades.isEmpty()) {
            crearNovedadesEjemplo();
        }
    }

    // Crea las novedades del documento del curso con fechas cercanas a hoy
    private void crearNovedadesEjemplo() {
        LocalDate hoy = LocalDate.now();
        novedades.add(new Novedad(generarCodigo(), "Semana de la Ciencia",
                "Ponencias y muestras de proyectos de investigación", hoy.plusDays(2),
                "Auditorio Mayor UTS", 5000, "Asistir y registrar asistencia en al menos 3 ponencias"));
        novedades.add(new Novedad(generarCodigo(), "Feria Empresarial",
                "Encuentro con empresas de la región y ofertas de práctica", hoy.plusDays(10),
                "Plazoleta central", 3000, "Visitar los stands y entregar la hoja de vida"));
        novedades.add(new Novedad(generarCodigo(), "Emprendimiento Uteísta",
                "Concurso de ideas de negocio de los estudiantes", hoy.plusDays(20),
                "Bloque B", 8000, "Presentar una idea de negocio ante el jurado"));
        novedades.add(new Novedad(generarCodigo(), "Jornada de Ingeniería UTS",
                "Charlas sobre tendencias en ingeniería", hoy.plusDays(30),
                "Edificio A", 0, "Evento informativo, no otorga DUTS"));
        guardar();
    }

    // Reescribe el archivo completo con la lista actual
    private void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        for (Novedad novedad : novedades) {
            lineas.add(novedad.aLineaTexto());
        }
        ArchivoTexto.escribirLineas(RUTA, lineas);
    }

    // Código único "NOV-" + milisegundos. Si ya existe (mismo milisegundo) se aumenta en 1
    private String generarCodigo() {
        long numero = System.currentTimeMillis();
        while (buscarPorCodigo("NOV-" + numero) != null) {
            numero++;
        }
        return "NOV-" + numero;
    }

    // CREAR: agrega una novedad nueva. La fecha no puede ser anterior a hoy
    public void crear(String nombre, String descripcion, LocalDate fecha, String lugar,
                      int valorDuts, String condiciones) throws ValidacionException {
        if (fecha.isBefore(LocalDate.now())) {
            throw new ValidacionException("La fecha de la novedad no puede ser anterior a hoy");
        }
        validarValorDuts(valorDuts);
        novedades.add(new Novedad(generarCodigo(), nombre, descripcion, fecha, lugar, valorDuts, condiciones));
        guardar();
    }

    // CONSULTAR: devuelve todas las novedades
    public ArrayList<Novedad> listar() {
        return novedades;
    }

    // Busca una novedad por su código. Devuelve null si no existe
    public Novedad buscarPorCodigo(String codigo) {
        for (Novedad novedad : novedades) {
            if (novedad.getCodigo().equals(codigo)) {
                return novedad;
            }
        }
        return null;
    }

    // ACTUALIZAR: cambia todos los datos de la novedad menos el código
    public void actualizar(String codigo, String nombre, String descripcion, LocalDate fecha, String lugar,
                           int valorDuts, String condiciones) throws ValidacionException {
        Novedad novedad = buscarExistente(codigo);
        validarValorDuts(valorDuts);
        novedad.setNombre(nombre);
        novedad.setDescripcion(descripcion);
        novedad.setFecha(fecha);
        novedad.setLugar(lugar);
        novedad.setValorDuts(valorDuts);
        novedad.setCondiciones(condiciones);
        guardar();
    }

    // ELIMINAR: borra la novedad de la lista y del archivo
    public void eliminar(String codigo) throws ValidacionException {
        Novedad novedad = buscarExistente(codigo);
        novedades.remove(novedad);
        guardar();
    }

    // Busca la novedad y lanza un error si no existe
    private Novedad buscarExistente(String codigo) throws ValidacionException {
        Novedad novedad = buscarPorCodigo(codigo);
        if (novedad == null) {
            throw new ValidacionException("La novedad no existe");
        }
        return novedad;
    }

    // El valor en DUTS puede ser 0 (no otorga DUTS) pero nunca negativo
    private void validarValorDuts(int valorDuts) throws ValidacionException {
        if (valorDuts < 0) {
            throw new ValidacionException("El valor en DUTS no puede ser negativo");
        }
    }
}
