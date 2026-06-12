package ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DetalleProductoDTO;
import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetallesProductosDataMapper {

    public List<DetalleProducto> toDetallesProductos(List<DetalleProductoDTO> detallesProductoDTOs) {
        return detallesProductoDTOs.stream().map(detalleProductoDTO -> this.toDetalleProducto(detalleProductoDTO)).toList();
    }

    private DetalleProducto toDetalleProducto(DetalleProductoDTO detalleProductoDTO){
        return new DetalleProducto(
                detalleProductoDTO.productoID(),
                detalleProductoDTO.cantidadProducto()
        );
    }

    public List<DetalleProductoDTO> toDetallesProductoDTOs(List<DetalleProducto> detallesProductos){
        return detallesProductos.stream().map(detalleProducto -> this.toDetalleProductoDTO(detalleProducto)).toList();
    }

    private DetalleProductoDTO toDetalleProductoDTO(DetalleProducto detalleProducto){
        return new DetalleProductoDTO(
                detalleProducto.getDetalleProductoID().toString(),
                detalleProducto.getProductoID(),
                detalleProducto.getCantidadProducto()
        );
    }

}
