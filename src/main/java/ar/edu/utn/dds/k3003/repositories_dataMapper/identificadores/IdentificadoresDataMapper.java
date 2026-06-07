package ar.edu.utn.dds.k3003.repositories_dataMapper.identificadores;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.model.identificadores.Identificador;

public class IdentificadoresDataMapper {

    public Identificador toIdentificador(IdentificadorDTO identificadorDTO) {

        return new Identificador(
            identificadorDTO.tipo(),
            identificadorDTO.descripcion()
        );
    }

    public IdentificadorDTO toIdentificadorDTO(Identificador identificador) {

        return new IdentificadorDTO(
            identificador.getId(),
            identificador.getTipo(),
            identificador.getDescripcion()
        );
    }

}
