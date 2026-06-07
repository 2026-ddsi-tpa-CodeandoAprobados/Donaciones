package ar.edu.utn.dds.k3003.model.registroEstado;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class RegistroEstadoDTO {
    @Getter
    @Setter
    String donacionID;

    @Getter
    @Setter
    EstadoDonacionEnum estado;

    @Getter
    @Setter
    LocalDate fecha;

    public RegistroEstadoDTO(
        String donacionID,
        EstadoDonacionEnum estado,
        LocalDate fecha
    ){
        setDonacionID(donacionID);
        setEstado(estado);
        setFecha(fecha);
    }


}
