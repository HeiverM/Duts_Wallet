// Clase que representa un movimiento de DUTS (transferencia, evento, penalización o asignación).
package modelo;

import interfaces.Exportable;
import interfaces.Persistible;
import java.time.LocalDate;

// Aquí se aplican interfaces: Movimiento implementa dos a la vez.
// Persistible para guardarse en movimientos.txt y Exportable para el reporte CSV.
public class Movimiento implements Persistible, Exportable {

    // Tipos de movimiento (se usan constantes String en lugar de enum)
    public static final String TIPO_TRANSFERENCIA = "TRANSFERENCIA";
    public static final String TIPO_EVENTO = "EVENTO";
    public static final String TIPO_PENALIZACION = "PENALIZACION";
    public static final String TIPO_ASIGNACION = "ASIGNACION";

    // Se usa como emisor o receptor cuando los DUTS los da o los quita el sistema
    public static final String SISTEMA = "SISTEMA";

    private String codigo;
    private LocalDate fecha;
    private String tipo;
    private String documentoEmisor;
    private String documentoReceptor;
    private int valor;
    private String descripcion;

    public Movimiento(String codigo, LocalDate fecha, String tipo, String documentoEmisor,
                      String documentoReceptor, int valor, String descripcion) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.tipo = tipo;
        this.documentoEmisor = documentoEmisor;
        this.documentoReceptor = documentoReceptor;
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDocumentoEmisor() {
        return documentoEmisor;
    }

    public String getDocumentoReceptor() {
        return documentoReceptor;
    }

    public int getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Solo el valor y la descripción se pueden modificar (al actualizar una asignación)
    public void setValor(int valor) {
        this.valor = valor;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Formato: codigo;fecha;tipo;emisor;receptor;valor;descripcion
    @Override
    public String aLineaTexto() {
        return codigo + ";" + fecha + ";" + tipo + ";" + documentoEmisor + ";"
                + documentoReceptor + ";" + valor + ";" + descripcion;
    }

    // Fila CSV separada por comas; en la descripción se cambia "," por espacio
    // para que no se mezcle con el separador de columnas
    @Override
    public String aFilaCsv() {
        return codigo + "," + fecha + "," + tipo + "," + documentoEmisor + ","
                + documentoReceptor + "," + valor + "," + descripcion.replace(",", " ");
    }
}
