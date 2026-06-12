package ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosDataMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DonacionesDataMapper {

    private final DetallesProductosDataMapper detallesProductosDataMapper;

    public DonacionesDataMapper(DetallesProductosDataMapper detallesProductosDataMapper) {
        this.detallesProductosDataMapper = detallesProductosDataMapper;
    }

    public Donacion toDonacion(DonacionDTO donacionDTO){

        return new Donacion(
                donacionDTO.donadorID(),
                donacionDTO.depositoID(),
                donacionDTO.descripcion(),
                donacionDTO.estado()
        );
    }

    public DonacionDTO toDonacionDTO(Donacion donacion){
        return new DonacionDTO(
                donacion.getId().toString(),
                donacion.getDonadorID(),
                donacion.getDepositoID(),
                donacion.getDescripcionDonacion(),
                this.detallesProductosDataMapper.toDetallesProductoDTOs(donacion.getDetallesProductos()),
                donacion.getEstado()
        );
    }

    public List<DonacionDTO> donacionesToDonacionesDTO(List<Donacion> donaciones){
        return donaciones.stream().map(this::toDonacionDTO).toList();
    }

}
