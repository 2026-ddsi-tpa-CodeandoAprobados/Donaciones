package ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos;

import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetallesProductosRepository extends JpaRepository<DetalleProducto , Long> {
}
