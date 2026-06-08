package ar.edu.utn.dds.k3003.model.registroEstado;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class RegistroEstado {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Getter @Setter private String donacionID;

    @Column (nullable = false , length = 10)
    @Getter @Setter private LocalDate fechaEstado;

    @Enumerated (EnumType.STRING)
    @Column(nullable = false , length = 30)
    @Getter @Setter private EstadoDonacionEnum estado;

    public RegistroEstado(){}

    public RegistroEstado (String donacionID, EstadoDonacionEnum estado) {
        setDonacionID(donacionID);
        setFechaEstado(LocalDate.now());
        setEstado(estado);
    }
}
