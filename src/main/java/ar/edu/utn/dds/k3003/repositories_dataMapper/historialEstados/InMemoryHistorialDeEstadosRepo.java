package ar.edu.utn.dds.k3003.repositories_dataMapper.historialEstados;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.model.registroEstado.RegistroEstado;
import lombok.val;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistorialDeEstadosRepo implements HistorialEstadosDonacionRepository  {

    private List<RegistroEstado> registroEstados;

    @Override
    public void save(String donacionID, EstadoDonacionEnum estado) {
        val nuevoRegistro = new RegistroEstado(donacionID, estado);
        this.registroEstados.add(nuevoRegistro);
    }

    public InMemoryHistorialDeEstadosRepo(){
        this.registroEstados = new ArrayList<>();
    }

    @Override
    public List<RegistroEstado> filterEstadosById(String id) {
        return this.registroEstados.stream().filter((registroEstado-> registroEstado.getDonacionID().equals(id))).toList();
    }
}
