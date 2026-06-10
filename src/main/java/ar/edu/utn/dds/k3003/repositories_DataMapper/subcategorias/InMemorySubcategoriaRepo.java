package ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias;

import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemorySubcategoriaRepo implements SubcategoriasRepository{

    @Setter @Getter private AtomicLong idSecuencial = new AtomicLong(1);
    @Setter @Getter private List<Subcategoria> subcategorias = new ArrayList<>();

    @Override
    public Subcategoria save(Subcategoria subcategoria) {

        if(subcategoria.getId() == null){
            subcategoria.setId(this.getIdSecuencial().getAndIncrement());
        }

        this.subcategorias.add(subcategoria);

        return this.findById(subcategoria.getId()).get();
    }

    @Override
    public Optional<Subcategoria> findById(Long subcategoriaID) {
        return this.subcategorias.stream().filter(s -> s.getId().equals(subcategoriaID)).findFirst();
    }

    @Override
    public void deleteById(String subcategoriaID) {
        this.subcategorias.removeIf(s -> s.getId().toString().equals(subcategoriaID));
    }

    @Override
    public List<Subcategoria> findAll() {
        return this.getSubcategorias();
    }

}
