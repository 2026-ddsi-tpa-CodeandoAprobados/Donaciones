package ar.edu.utn.dds.k3003.repositories_dataMapper.productos.DetallesProductos;

import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryDetalleProdRepository implements DetallesProductosRepository {

    @Setter
    @Getter
    private List<DetalleProducto> detallesProductos;

    @Setter @Getter private AtomicLong idSecuencial = new AtomicLong(1);


    @Override
    public Optional<DetalleProducto> findById(String id) {
        return this.getDetallesProductos().stream().filter(d -> d.getDetalleProductoID().equals(id)).findFirst();
    }

    @Override
    public DetalleProducto saveOne(DetalleProducto detalleProductoAguardar){

        if(detalleProductoAguardar.getDetalleProductoID()== null){
            detalleProductoAguardar.setDetalleProductoID(String.valueOf(this.getIdSecuencial().getAndIncrement()));
        }

        this.detallesProductos.add(detalleProductoAguardar);

        val detalleProductoConID = detalleProductoAguardar;

        return detalleProductoConID;
    }

    public List<DetalleProducto> saveAll(List<DetalleProducto> detalleProductos){
       return detalleProductos.stream().map(detalleProducto -> this.saveOne(detalleProducto)).toList();
    }


}
