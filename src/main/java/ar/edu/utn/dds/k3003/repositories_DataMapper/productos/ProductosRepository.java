package ar.edu.utn.dds.k3003.repositories_DataMapper.productos;

import ar.edu.utn.dds.k3003.model.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductosRepository extends JpaRepository<Producto, Long> {

    boolean existsByIdentificadorID(String identificadorID);
}
