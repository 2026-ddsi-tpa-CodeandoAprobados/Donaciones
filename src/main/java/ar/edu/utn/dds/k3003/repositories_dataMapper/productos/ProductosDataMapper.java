package ar.edu.utn.dds.k3003.repositories_dataMapper.productos;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.productos.Producto;

public class ProductosDataMapper{

    public ProductoDTO toProductoDTO(Producto producto){

        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getCategoriaID(),
            producto.getIdentificadorID()
        );
    }

    public Producto toProducto(ProductoDTO productoDTO){

        return new Producto(
                productoDTO.nombre(),
                productoDTO.descripcion(),
                productoDTO.identificadorID(),
                productoDTO.categoriaID()
        );
    }

}
