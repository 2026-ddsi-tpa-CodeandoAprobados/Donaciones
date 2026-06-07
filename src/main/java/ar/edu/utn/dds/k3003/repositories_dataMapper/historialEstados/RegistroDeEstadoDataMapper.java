package ar.edu.utn.dds.k3003.repositories_dataMapper.historialEstados;

import ar.edu.utn.dds.k3003.model.registroEstado.RegistroEstado;
import ar.edu.utn.dds.k3003.model.registroEstado.RegistroEstadoDTO;

public class RegistroDeEstadoDataMapper {

    public RegistroEstadoDTO toRegistroDTO(RegistroEstado registro) {
        return new RegistroEstadoDTO (
                registro.getDonacionID(),
                registro.getEstado(),
                registro.getFechaEstado()
        );
    }

    public RegistroEstado toRegistro(RegistroEstadoDTO RegistroDTO) {

        RegistroEstado registro = new RegistroEstado (
                RegistroDTO.getDonacionID(),
                RegistroDTO.getEstado()
        );

        if (RegistroDTO.getFecha() != null) {
            registro.setFechaEstado(RegistroDTO.getFecha());
        }

        return registro;
    }
}

