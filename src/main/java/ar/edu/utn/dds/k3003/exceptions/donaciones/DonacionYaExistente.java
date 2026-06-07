package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class DonacionYaExistente extends RuntimeException {
    public DonacionYaExistente(String message) {
        super(message);
    }
}
