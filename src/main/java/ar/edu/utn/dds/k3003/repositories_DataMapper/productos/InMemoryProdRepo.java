package ar.edu.utn.dds.k3003.repositories_DataMapper.productos;

import ar.edu.utn.dds.k3003.model.productos.Producto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


public class InMemoryProdRepo implements ProductosRepository {

    @Setter @Getter List<Producto> productos = new ArrayList<>();
    @Setter @Getter private AtomicLong idSecuencial = new AtomicLong(1);

    @Override
    public Optional<Producto> findById(Long id) {
        return this.getProductos().stream().filter(producto -> producto.getId().equals(id)).findFirst();
    }

    @Override
    public Producto save(Producto producto) {

        if(producto.getId() == null){
            producto.setId(getIdSecuencial().getAndIncrement());
        }

        this.productos.add(producto);

        return producto;

    }

    @Override
    public List<Producto> findAll() {
        return this.getProductos();
    }

    @Override
    public void deleteById(String productoID) {
        this.getProductos().removeIf(producto -> producto.getId().toString().equals(productoID));
    }


}
