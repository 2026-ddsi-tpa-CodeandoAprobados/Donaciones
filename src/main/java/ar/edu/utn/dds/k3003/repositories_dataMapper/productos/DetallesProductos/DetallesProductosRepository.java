package ar.edu.utn.dds.k3003.repositories_dataMapper.productos.DetallesProductos;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DetalleProductoDTO;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;

import java.util.List;
import java.util.Optional;

public interface DetallesProductosRepository   {

    Optional<DetalleProducto> findById(String id);

    DetalleProducto saveOne(DetalleProducto detalleProducto);

    List<DetalleProducto> saveAll(List<DetalleProducto> detalleProductos);

}
