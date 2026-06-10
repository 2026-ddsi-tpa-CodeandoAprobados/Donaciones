package ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias;

import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;

import java.util.List;
import java.util.Optional;

public interface SubcategoriasRepository {

    public Subcategoria save(Subcategoria subcategoria);

    public Optional<Subcategoria> findById(Long subcategoriaID);

    public void deleteById(String subcategoriaID);

    public List<Subcategoria> findAll();

}
