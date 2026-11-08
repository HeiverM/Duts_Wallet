// Clase principal que inicia la aplicación: crea los gestores y abre la ventana de inicio.
package vista;

import persistencia.GestorAmigos;
import persistencia.GestorInscripciones;
import persistencia.GestorMovimientos;
import persistencia.GestorNovedades;
import persistencia.GestorNotificaciones;
import persistencia.GestorUsuarios;

// El punto de entrada del programa. Aquí se instancian los objetos de los gestores
// y se muestra la ventana inicial. Cada gestor carga sus datos desde el archivo
// correspondiente al crearse, y los mantiene en memoria durante toda la ejecución.
public class Main {

    public static void main(String[] args) {
        // Se crean los gestores una sola vez. Cada uno carga sus datos al crearse
        GestorUsuarios gestorUsuarios = new GestorUsuarios();
        GestorMovimientos gestorMovimientos = new GestorMovimientos(gestorUsuarios);
        GestorNovedades gestorNovedades = new GestorNovedades();
        GestorNotificaciones gestorNotificaciones = new GestorNotificaciones();
        GestorInscripciones gestorInscripciones = new GestorInscripciones(gestorMovimientos, gestorNotificaciones,
                gestorUsuarios, gestorNovedades);
        GestorAmigos gestorAmigos = new GestorAmigos(gestorUsuarios);

        // Se abre la ventana inicial
        VentanaInicio ventanaInicio = new VentanaInicio(gestorUsuarios, gestorMovimientos, gestorNovedades,
                gestorInscripciones, gestorNotificaciones, gestorAmigos);
        ventanaInicio.setVisible(true);
    }
}
