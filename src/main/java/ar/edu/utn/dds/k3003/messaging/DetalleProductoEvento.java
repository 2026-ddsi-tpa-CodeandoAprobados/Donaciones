package ar.edu.utn.dds.k3003.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/** Un producto de la donación, con su categoría y subcategoría (id y nombre). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DetalleProductoEvento(
        String id,
        String productoID,
        Integer cantidadProducto,
        String categoriaID,
        String categoria,
        String subcategoriaID,
        String subcategoria
) implements Serializable {
}
