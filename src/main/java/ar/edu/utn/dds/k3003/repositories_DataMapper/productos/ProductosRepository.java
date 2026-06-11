package ar.edu.utn.dds.k3003.repositories_DataMapper.productos;

import ar.edu.utn.dds.k3003.model.productos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductosRepository extends JpaRepository<Producto, Long> {
}
