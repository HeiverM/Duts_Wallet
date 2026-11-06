// Clase que representa a un estudiante de las UTS con su saldo de DUTS y su meta de grado.
package modelo;

import excepciones.ValidacionException;

// Aquí se aplica herencia: Estudiante reutiliza los atributos de Usuario
// (documento, nombre, usuario, contraseña, pregunta y respuesta) y agrega los suyos.
public class Estudiante extends Usuario {

    // Cantidad de DUTS que exige la universidad para graduarse
    public static final int META_DUTS = 70000;

    private String programaAcademico;
    private int saldoDuts;

    // El constructor envía los datos comunes a la clase padre con super(...)
    public Estudiante(String documento, String nombreCompleto, String nombreUsuario,
                      String contrasena, String preguntaSeguridad, String respuestaSeguridad,
                      String programaAcademico, int saldoDuts) {
        super(documento, nombreCompleto, nombreUsuario, contrasena, preguntaSeguridad, respuestaSeguridad);
        this.programaAcademico = programaAcademico;
        this.saldoDuts = saldoDuts;
    }

    public String getProgramaAcademico() {
        return programaAcademico;
    }

    public int getSaldoDuts() {
        return saldoDuts;
    }

    // Suma DUTS al saldo (asignaciones, eventos y transferencias recibidas)
    public void sumarDuts(int valor) {
        saldoDuts = saldoDuts + valor;
    }

    // Aquí se valida que el saldo alcance antes de descontar
    public void restarDuts(int valor) throws ValidacionException {
        if (valor > saldoDuts) {
            throw new ValidacionException("Saldo insuficiente");
        }
        saldoDuts = saldoDuts - valor;
    }

    // DUTS que le faltan para llegar a la meta (nunca es negativo)
    public int getDutsFaltantes() {
        int faltantes = META_DUTS - saldoDuts;
        if (faltantes < 0) {
            return 0;
        }
        return faltantes;
    }

    // Porcentaje de avance hacia la meta, siempre entre 0 y 100
    public int getPorcentajeMeta() {
        if (saldoDuts >= META_DUTS) {
            return 100;
        }
        if (saldoDuts <= 0) {
            return 0;
        }
        return saldoDuts * 100 / META_DUTS;
    }

    // Indica si el estudiante ya reunió los 70.000 DUTS
    public boolean metaCumplida() {
        return saldoDuts >= META_DUTS;
    }

    // Aquí se aplica polimorfismo sobrescribiendo el método de la clase padre
    @Override
    public String getRol() {
        return "ESTUDIANTE";
    }

    // Aquí se aplica polimorfismo: el estudiante describe su perfil a su manera
    @Override
    public String getResumenPerfil() {
        return "Estudiante: " + getNombreCompleto() + " - " + programaAcademico
                + " - Saldo: " + saldoDuts + " DUTS";
    }

    // Formato: ESTUDIANTE;documento;nombreCompleto;nombreUsuario;contrasena;pregunta;respuesta;programa;saldo
    @Override
    public String aLineaTexto() {
        return getRol() + ";" + getDocumento() + ";" + getNombreCompleto() + ";" + getNombreUsuario()
                + ";" + getContrasena() + ";" + getPreguntaSeguridad() + ";" + getRespuestaSeguridad()
                + ";" + programaAcademico + ";" + saldoDuts;
    }
}
