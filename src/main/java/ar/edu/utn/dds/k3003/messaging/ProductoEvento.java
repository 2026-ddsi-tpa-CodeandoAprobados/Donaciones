package ar.edu.utn.dds.k3003.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/** Un producto del catálogo con su subcategoría y su categoría (id y nombre). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductoEvento(
        String id,
        String nombre,
        String descripcion,
        String subcategoriaID,
        String subcategoria,
        String categoriaID,
        String categoria
) implements Serializable {
}
