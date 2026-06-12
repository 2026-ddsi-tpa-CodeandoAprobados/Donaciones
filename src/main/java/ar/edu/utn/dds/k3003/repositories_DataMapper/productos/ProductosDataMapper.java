package ar.edu.utn.dds.k3003.repositories_DataMapper.productos;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import org.springframework.stereotype.Component;

@Component
public class ProductosDataMapper{

    public ProductoDTO toProductoDTO(Producto producto){

        return new ProductoDTO(
            producto.getId().toString(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getSubcategoriaID(),
            producto.getIdentificadorID()
        );
    }

    public Producto toProducto(ProductoDTO productoDTO){

        return new Producto(
                productoDTO.nombre(),
                productoDTO.descripcion(),
                productoDTO.subcategoriaID(),
                productoDTO.identificadorID()
        );
    }

}
