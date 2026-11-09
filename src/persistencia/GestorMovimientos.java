// Clase que administra los movimientos de DUTS y los guarda en datos/movimientos.txt.
package persistencia;

import excepciones.ValidacionException;
import java.time.LocalDate;
import java.util.ArrayList;
import modelo.Estudiante;
import modelo.Movimiento;
import modelo.Usuario;

public class GestorMovimientos {

    private static final String RUTA = "datos/movimientos.txt";

    // Días aproximados de cada período para calcular los promedios
    public static final int DIAS_SEMANA = 7;
    public static final int DIAS_MES = 30;
    public static final int DIAS_ANIO = 365;
    public static final int DIAS_SEMESTRE = 90; // 4 cuatrimestres por año (360/4)

    private ArrayList<Movimiento> movimientos;

    // Se necesita el gestor de usuarios para buscar estudiantes y guardar sus saldos
    private GestorUsuarios gestorUsuarios;

    public GestorMovimientos(GestorUsuarios gestorUsuarios) {
        this.gestorUsuarios = gestorUsuarios;
        movimientos = new ArrayList<Movimiento>();
        cargar();
    }

    // Lee el archivo y crea un objeto Movimiento por cada línea
    private void cargar() {
        ArrayList<String> lineas = ArchivoTexto.leerLineas(RUTA);
        for (String linea : lineas) {
            String[] partes = linea.split(";", -1);
            if (partes.length == 7) {
                movimientos.add(new Movimiento(partes[0], LocalDate.parse(partes[1]), partes[2],
                        partes[3], partes[4], Integer.parseInt(partes[5]), partes[6]));
            }
        }
    }

    // Reescribe el archivo completo con la lista actual
    private void guardar() {
        ArrayList<String> lineas = new ArrayList<String>();
        for (Movimiento movimiento : movimientos) {
            lineas.add(movimiento.aLineaTexto());
        }
        ArchivoTexto.escribirLineas(RUTA, lineas);
    }

    // Código único "MOV-" + milisegundos. Si ya existe (mismo milisegundo) se aumenta en 1
    private String generarCodigo() {
        long numero = System.currentTimeMillis();
        while (buscarPorCodigo("MOV-" + numero) != null) {
            numero++;
        }
        return "MOV-" + numero;
    }

    // Crea un movimiento con la fecha de hoy y un código nuevo, y lo guarda.
    // Lo usan transferir, asignarDuts y GestorInscripciones (eventos y penalizaciones)
    public void registrarMovimiento(String tipo, String documentoEmisor, String documentoReceptor,
                                    int valor, String descripcion) {
        movimientos.add(new Movimiento(generarCodigo(), LocalDate.now(), tipo,
                documentoEmisor, documentoReceptor, valor, descripcion));
        guardar();
    }

    // Envía DUTS de un estudiante a otro
    public void transferir(Estudiante emisor, Estudiante receptor, int valor, String descripcion)
            throws ValidacionException {
        if (valor <= 0) {
            throw new ValidacionException("El valor a transferir debe ser mayor que 0");
        }
        if (emisor.getDocumento().equals(receptor.getDocumento())) {
            throw new ValidacionException("No puede transferirse DUTS a sí mismo");
        }
        // Aquí se valida que el saldo alcance antes de transferir (restarDuts lanza "Saldo insuficiente")
        emisor.restarDuts(valor);
        receptor.sumarDuts(valor);
        registrarMovimiento(Movimiento.TIPO_TRANSFERENCIA, emisor.getDocumento(),
                receptor.getDocumento(), valor, descripcion);
        gestorUsuarios.guardar();
    }

    // CREAR (CRUD de DUTS): el administrador asigna DUTS a un estudiante
    public void asignarDuts(Estudiante estudiante, int valor, String descripcion) throws ValidacionException {
        if (valor <= 0) {
            throw new ValidacionException("El valor a asignar debe ser mayor que 0");
        }
        estudiante.sumarDuts(valor);
        registrarMovimiento(Movimiento.TIPO_ASIGNACION, Movimiento.SISTEMA,
                estudiante.getDocumento(), valor, descripcion);
        gestorUsuarios.guardar();
    }

    // CONSULTAR: movimientos en los que el usuario es emisor o receptor (orden cronológico)
    public ArrayList<Movimiento> listarPorUsuario(String documento) {
        ArrayList<Movimiento> resultado = new ArrayList<Movimiento>();
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getDocumentoEmisor().equals(documento)
                    || movimiento.getDocumentoReceptor().equals(documento)) {
                resultado.add(movimiento);
            }
        }
        return resultado;
    }

    // CONSULTAR: todas las asignaciones hechas por el administrador
    public ArrayList<Movimiento> listarAsignaciones() {
        ArrayList<Movimiento> resultado = new ArrayList<Movimiento>();
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getTipo().equals(Movimiento.TIPO_ASIGNACION)) {
                resultado.add(movimiento);
            }
        }
        return resultado;
    }

    // Busca un movimiento por su código. Devuelve null si no existe
    public Movimiento buscarPorCodigo(String codigo) {
        for (Movimiento movimiento : movimientos) {
            if (movimiento.getCodigo().equals(codigo)) {
                return movimiento;
            }
        }
        return null;
    }

    // ACTUALIZAR: cambia valor y descripción de una asignación y ajusta el saldo con la diferencia
    public void actualizarAsignacion(String codigo, int nuevoValor, String nuevaDescripcion)
            throws ValidacionException {
        Movimiento asignacion = buscarAsignacion(codigo);
        if (nuevoValor <= 0) {
            throw new ValidacionException("El valor de la asignación debe ser mayor que 0");
        }
        Estudiante estudiante = buscarEstudiante(asignacion.getDocumentoReceptor());
        int diferencia = nuevoValor - asignacion.getValor();
        if (diferencia > 0) {
            estudiante.sumarDuts(diferencia);
        } else if (diferencia < 0) {
            // Si el nuevo valor es menor, se le quita la diferencia (puede faltar saldo)
            estudiante.restarDuts(-diferencia);
        }
        asignacion.setValor(nuevoValor);
        asignacion.setDescripcion(nuevaDescripcion);
        guardar();
        gestorUsuarios.guardar();
    }

    // ELIMINAR: borra una asignación y le resta su valor al estudiante
    public void eliminarAsignacion(String codigo) throws ValidacionException {
        Movimiento asignacion = buscarAsignacion(codigo);
        Estudiante estudiante = buscarEstudiante(asignacion.getDocumentoReceptor());
        // Si el estudiante ya gastó esos DUTS, restarDuts lanza "Saldo insuficiente"
        estudiante.restarDuts(asignacion.getValor());
        movimientos.remove(asignacion);
        guardar();
        gestorUsuarios.guardar();
    }

    // Busca el movimiento y verifica que sea de tipo ASIGNACION
    private Movimiento buscarAsignacion(String codigo) throws ValidacionException {
        Movimiento movimiento = buscarPorCodigo(codigo);
        if (movimiento == null || !movimiento.getTipo().equals(Movimiento.TIPO_ASIGNACION)) {
            throw new ValidacionException("La asignación no existe");
        }
        return movimiento;
    }

    // Busca el estudiante y lanza un error si no existe
    private Estudiante buscarEstudiante(String documento) throws ValidacionException {
        Estudiante estudiante = gestorUsuarios.buscarEstudiantePorDocumento(documento);
        if (estudiante == null) {
            throw new ValidacionException("El estudiante no existe");
        }
        return estudiante;
    }

    // Filtra los movimientos del usuario. tipo "Todos" no filtra por tipo y las fechas null no filtran
    public ArrayList<Movimiento> filtrar(String documento, String tipo, LocalDate desde, LocalDate hasta) {
        ArrayList<Movimiento> resultado = new ArrayList<Movimiento>();
        for (Movimiento movimiento : listarPorUsuario(documento)) {
            boolean cumpleTipo = tipo.equals("Todos") || movimiento.getTipo().equals(tipo);
            boolean cumpleDesde = desde == null || !movimiento.getFecha().isBefore(desde);
            boolean cumpleHasta = hasta == null || !movimiento.getFecha().isAfter(hasta);
            if (cumpleTipo && cumpleDesde && cumpleHasta) {
                resultado.add(movimiento);
            }
        }
        return resultado;
    }

    // Promedio = DUTS recibidos / períodos desde el primer movimiento hasta hoy (mínimo 1 período)
    public double calcularPromedio(String documento, int diasPeriodo) {
        ArrayList<Movimiento> lista = listarPorUsuario(documento);
        if (lista.isEmpty()) {
            return 0;
        }
        int totalIngresos = 0;
        LocalDate primeraFecha = lista.get(0).getFecha();
        for (Movimiento movimiento : lista) {
            // Solo se suman los ingresos: movimientos donde el usuario es el receptor
            if (movimiento.getDocumentoReceptor().equals(documento)) {
                totalIngresos = totalIngresos + movimiento.getValor();
            }
            if (movimiento.getFecha().isBefore(primeraFecha)) {
                primeraFecha = movimiento.getFecha();
            }
        }
        long dias = LocalDate.now().toEpochDay() - primeraFecha.toEpochDay();
        long periodos = dias / diasPeriodo;
        if (periodos < 1) {
            periodos = 1;
        }
        // Se redondea a 2 decimales
        return Math.round(totalIngresos * 100.0 / periodos) / 100.0;
    }

    // Crea reportes/historial_<documento>.csv con los movimientos y los promedios. Devuelve la ruta
    public String exportarCsv(String documento) {
        String ruta = "reportes/historial_" + documento + ".csv";
        ArrayList<String> lineas = new ArrayList<String>();
        lineas.add("codigo,fecha,tipo,emisor,receptor,valor,descripcion");
        // Aquí se usa la interfaz Exportable: cada movimiento sabe convertirse en fila CSV
        for (Movimiento movimiento : listarPorUsuario(documento)) {
            lineas.add(movimiento.aFilaCsv());
        }
        lineas.add("");
        lineas.add("Promedio por semana," + calcularPromedio(documento, DIAS_SEMANA));
        lineas.add("Promedio por mes," + calcularPromedio(documento, DIAS_MES));
        lineas.add("Promedio por año," + calcularPromedio(documento, DIAS_ANIO));
        lineas.add("Promedio por cuatrimestre," + calcularPromedio(documento, DIAS_SEMESTRE));
        ArchivoTexto.escribirLineas(ruta, lineas);
        return ruta;
    }

    // Crea comprobantes/comprobante_<codigo>.txt de una transferencia. Devuelve la ruta
    public String exportarComprobante(Movimiento movimiento) throws ValidacionException {
        if (!movimiento.getTipo().equals(Movimiento.TIPO_TRANSFERENCIA)) {
            throw new ValidacionException("Solo se pueden generar comprobantes de transferencias");
        }
        String ruta = "comprobantes/comprobante_" + movimiento.getCodigo() + ".txt";
        ArrayList<String> lineas = new ArrayList<String>();
        lineas.add("===== COMPROBANTE DE TRANSFERENCIA - DUTS WALLET =====");
        lineas.add("Código: " + movimiento.getCodigo());
        lineas.add("Fecha: " + movimiento.getFecha());
        lineas.add("Emisor: " + describirUsuario(movimiento.getDocumentoEmisor()));
        lineas.add("Receptor: " + describirUsuario(movimiento.getDocumentoReceptor()));
        lineas.add("Valor: " + movimiento.getValor() + " DUTS");
        lineas.add("Descripción: " + movimiento.getDescripcion());
        ArchivoTexto.escribirLineas(ruta, lineas);
        return ruta;
    }

    // Devuelve "Nombre (documento X)" o solo el documento si el usuario no existe
    private String describirUsuario(String documento) {
        Usuario usuario = gestorUsuarios.buscarPorDocumento(documento);
        if (usuario == null) {
            return documento;
        }
        return usuario.getNombreCompleto() + " (documento " + documento + ")";
    }
}
