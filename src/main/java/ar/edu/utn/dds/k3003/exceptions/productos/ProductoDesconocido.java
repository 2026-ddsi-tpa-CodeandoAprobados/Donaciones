package ar.edu.utn.dds.k3003.exceptions.productos;

public class ProductoDesconocido extends RuntimeException {
    public ProductoDesconocido(String message) {
        super(message);
    }
}
