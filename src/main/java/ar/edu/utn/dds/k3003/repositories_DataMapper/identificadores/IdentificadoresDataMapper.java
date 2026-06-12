package ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.model.identificadores.Identificador;
import org.springframework.stereotype.Component;

@Component
public class IdentificadoresDataMapper {

    public Identificador toIdentificador(IdentificadorDTO identificadorDTO) {

        return new Identificador(
            identificadorDTO.tipo(),
            identificadorDTO.descripcion()
        );
    }

    public IdentificadorDTO toIdentificadorDTO(Identificador identificador) {

        return new IdentificadorDTO(
            identificador.getId().toString(),
            identificador.getTipo(),
            identificador.getDescripcion()
        );
    }

}
