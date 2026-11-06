// Clase que representa una novedad institucional de las UTS (evento al que se pueden inscribir).
package modelo;

import interfaces.Persistible;
import java.time.LocalDate;

// Aquí se aplica encapsulamiento: los datos son privados y se modifican con setters.
// Implementa Persistible para guardarse en novedades.txt
public class Novedad implements Persistible {

    private String codigo;
    private String nombre;
    private String descripcion;
    private LocalDate fecha;
    private String lugar;
    private int valorDuts;
    private String condiciones;

    public Novedad(String codigo, String nombre, String descripcion, LocalDate fecha,
                   String lugar, int valorDuts, String condiciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.valorDuts = valorDuts;
        this.condiciones = condiciones;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getLugar() {
        return lugar;
    }

    // Si vale 0, la novedad no otorga DUTS
    public int getValorDuts() {
        return valorDuts;
    }

    public String getCondiciones() {
        return condiciones;
    }

    // Todos los datos se pueden modificar menos el código
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public void setValorDuts(int valorDuts) {
        this.valorDuts = valorDuts;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    // Formato: codigo;nombre;descripcion;fecha;lugar;valorDuts;condiciones
    @Override
    public String aLineaTexto() {
        return codigo + ";" + nombre + ";" + descripcion + ";" + fecha + ";"
                + lugar + ";" + valorDuts + ";" + condiciones;
    }
}
