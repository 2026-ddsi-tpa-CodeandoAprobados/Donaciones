package ar.edu.utn.dds.k3003.repositories_DataMapper.historialEstados;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.model.registroEstado.RegistroEstado;

import java.util.List;

public interface HistorialEstadosDonacionRepository {

    void save(String donacionID , EstadoDonacionEnum estado);

    List<RegistroEstado> filterEstadosById(String id);

}
