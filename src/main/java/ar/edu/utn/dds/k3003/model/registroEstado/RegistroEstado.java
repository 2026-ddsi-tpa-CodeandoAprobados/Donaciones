package ar.edu.utn.dds.k3003.model.registroEstado;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


public class RegistroEstado {

    @Getter
    @Setter
    private String donacionID;

    @Getter
    @Setter
    private LocalDate fechaEstado;

    @Getter
    @Setter
    private EstadoDonacionEnum estado;

    public RegistroEstado (String donacionID, EstadoDonacionEnum estado) {
        setDonacionID(donacionID);
        setFechaEstado(LocalDate.now());
        setEstado(estado);
    }
}
