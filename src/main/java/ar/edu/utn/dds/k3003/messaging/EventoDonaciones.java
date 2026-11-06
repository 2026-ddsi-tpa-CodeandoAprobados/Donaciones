package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventoDonaciones(
        String id,
        TipoEventoDonaciones tipo,
        Instant timestamp,
        String idReferencia,
        DonacionDTO donacion
) implements Serializable {
}