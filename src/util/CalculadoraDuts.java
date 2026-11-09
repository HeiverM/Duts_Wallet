// Calculadora de DUTS: lógica de cálculo pura para los módulos A, B y C (sin Swing).
package util;

// Aquí se aplica encapsulamiento: todos los métodos son static y la lógica está centralizada
public class CalculadoraDuts {

    public static final int META_DUTS = 70000;

    // Módulo A: Faltantes para la meta
    public static int calcularDutsFaltantes(int saldoActual) {
        int faltantes = META_DUTS - saldoActual;
        // Nunca devuelve negativo: si ya cumplió la meta, faltantes = 0
        return faltantes > 0 ? faltantes : 0;
    }

    public static int calcularPorcentajeMeta(int saldoActual) {
        if (saldoActual >= META_DUTS) {
            return 100;
        }
        // Porcentaje redondeado sin decimales
        return (int) ((saldoActual * 100.0) / META_DUTS);
    }

    // Módulo B: Simulador de transferencia
    public static int calcularSaldoResultante(int saldoActual, int valorTransferencia) {
        return saldoActual - valorTransferencia;
    }

    public static boolean tieneSaldoSuficiente(int saldoActual, int valorTransferencia) {
        return saldoActual >= valorTransferencia;
    }

    // Módulo C: Proyección a la meta
    public static int calcularMesesParaMeta(int saldoActual, int dutsPorMes) {
        int faltantes = calcularDutsFaltantes(saldoActual);
        if (faltantes == 0 || dutsPorMes <= 0) {
            return 0;
        }
        // Meses necesarios redondeando hacia arriba
        return (int) Math.ceil((double) faltantes / dutsPorMes);
    }

    public static int calcularEventosNecesarios(int saldoActual, int dutsPorEvento) {
        int faltantes = calcularDutsFaltantes(saldoActual);
        if (faltantes == 0 || dutsPorEvento <= 0) {
            return 0;
        }
        // Eventos necesarios redondeando hacia arriba
        return (int) Math.ceil((double) faltantes / dutsPorEvento);
    }
}
