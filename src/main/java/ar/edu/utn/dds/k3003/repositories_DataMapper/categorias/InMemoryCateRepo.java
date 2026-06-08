package ar.edu.utn.dds.k3003.repositories_DataMapper.categorias;

import ar.edu.utn.dds.k3003.model.categorias.Categoria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryCateRepo implements CategoriasRepository{

    @Setter @Getter private AtomicLong idSecuencial =  new AtomicLong(1);
    @Setter @Getter private List<Categoria> categorias = new ArrayList<>();

    @Override
    public Categoria save(Categoria categoria) {

        if(categoria.getId() == null){
            categoria.setId(String.valueOf(this.getIdSecuencial().getAndIncrement()));
        }

        this.categorias.add(categoria);

        return this.findById(categoria.getId()).get();

    }

    @Override
    public Optional<Categoria> findById(String categoriaID) {
        return categorias.stream().filter(cat -> cat.getId().equals(categoriaID)).findFirst();
    }

    @Override
    public void deleteById(String categoriaID) {
        categorias.removeIf(cat -> cat.getId().equals(categoriaID));
    }

    @Override
    public List<Categoria> findAll() {
        return this.getCategorias();
    }
}
