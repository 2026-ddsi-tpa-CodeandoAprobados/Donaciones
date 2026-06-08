package ar.edu.utn.dds.k3003.repositories_DataMapper.productos;

import ar.edu.utn.dds.k3003.model.productos.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductosRepository {

    Optional<Producto> findById(String id);

    Producto save(Producto producto);

    List<Producto> findAll();

    void deleteById(String productoID);

}
