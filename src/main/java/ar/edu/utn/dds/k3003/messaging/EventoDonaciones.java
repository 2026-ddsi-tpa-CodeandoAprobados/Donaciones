package ar.edu.utn.dds.k3003.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;

/** Sobre común de los eventos de Donaciones. Lleva la donación o el producto, según el tipo. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventoDonaciones(
        String id,
        TipoEventoDonaciones tipo,
        Instant timestamp,
        String idReferencia,
        DonacionEvento donacion,
        ProductoEvento producto
) implements Serializable {
}
