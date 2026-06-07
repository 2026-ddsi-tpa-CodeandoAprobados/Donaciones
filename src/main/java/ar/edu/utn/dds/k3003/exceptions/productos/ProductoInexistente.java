package ar.edu.utn.dds.k3003.exceptions.productos;

public class ProductoInexistente extends RuntimeException {
    public ProductoInexistente(String message) {
        super(message);
    }
}
