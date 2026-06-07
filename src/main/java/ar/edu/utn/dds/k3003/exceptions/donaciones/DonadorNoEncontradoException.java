package ar.edu.utn.dds.k3003.exceptions.donaciones;

public class DonadorNoEncontradoException extends RuntimeException {
  public DonadorNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}
