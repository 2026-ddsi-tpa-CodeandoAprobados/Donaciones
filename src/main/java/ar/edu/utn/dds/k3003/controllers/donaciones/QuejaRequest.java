package ar.edu.utn.dds.k3003.controllers.donaciones;

import lombok.Getter;
import lombok.Setter;

public class QuejaRequest {

    @Getter @Setter
    private String descripcion;

    public QuejaRequest() {
    }

    public QuejaRequest(String descripcion) {
        this.descripcion = descripcion;
    }

}
