package ar.edu.utn.dds.k3003.controllers.productos;

import lombok.Getter;
import lombok.Setter;

public class ProductoRequest {
    @Getter @Setter private String nombre;
    @Getter @Setter private String descripcion;
    @Getter @Setter private String categoriaID;
    @Getter @Setter private String identificadorID;
}
