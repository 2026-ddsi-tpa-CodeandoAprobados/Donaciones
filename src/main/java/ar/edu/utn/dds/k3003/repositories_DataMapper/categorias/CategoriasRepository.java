package ar.edu.utn.dds.k3003.repositories_DataMapper.categorias;

import ar.edu.utn.dds.k3003.model.categorias.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriasRepository {

    public Categoria save(Categoria categoria);

    public Optional<Categoria> findById(String categoriaID);

    public void deleteById(String categoriaID);

    public List<Categoria> findAll();



}
