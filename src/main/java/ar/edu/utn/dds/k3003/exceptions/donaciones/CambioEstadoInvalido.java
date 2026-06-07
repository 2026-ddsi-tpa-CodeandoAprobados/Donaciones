package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class CambioEstadoInvalido extends RuntimeException {
    public CambioEstadoInvalido (String message) {
        super(message);
    }
}
