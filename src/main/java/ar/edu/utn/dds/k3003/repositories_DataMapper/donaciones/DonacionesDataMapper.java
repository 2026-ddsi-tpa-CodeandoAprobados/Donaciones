package ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosDataMapper;
import lombok.Getter;

import java.util.List;

public class DonacionesDataMapper {

    @Getter DetallesProductosDataMapper detallesProductosDataMapper = new DetallesProductosDataMapper();

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
                donacion.getId(),
                donacion.getDonadorID(),
                donacion.getDepositoID(),
                donacion.getDescripcionDonacion(),
                this.getDetallesProductosDataMapper().toDetallesProductoDTOs(donacion.getDetallesProductos()),
                donacion.getEstado()
        );
    }

    public List<DonacionDTO> donacionesToDonacionesDTO(List<Donacion> donaciones){
        return donaciones.stream().map(this::toDonacionDTO).toList();
    }

}
