package ar.edu.utn.dds.k3003.controllers.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import lombok.Getter;
import lombok.Setter;

public record EstadoDonacionRequest (String estado){}
