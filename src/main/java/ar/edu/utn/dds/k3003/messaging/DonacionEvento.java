package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * La donación tal como viaja en los eventos: los mismos campos que {@code DonacionDTO}, pero con la categoría y la
 * subcategoría de cada producto.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DonacionEvento(
        String id,
        String donadorID,
        String depositoID,
        String descripcion,
        List<DetalleProductoEvento> detallesProductosDTO,
        EstadoDonacionEnum estado,
        LocalDate fechaRegistro
) implements Serializable {
}
