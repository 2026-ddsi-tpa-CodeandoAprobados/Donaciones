package ar.edu.utn.dds.k3003.repositories_dataMapper.categorias;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.model.categorias.Categoria;

public class CategoriasDataMapper {

    public CategoriaDTO toCategoriaDTO(Categoria categoria){
        return new CategoriaDTO(
                categoria.getNombre(),
                categoria.getId(),
                categoria.getDescripcion(),
                categoria.getSubcategoriaID()
        );
    }

    public Categoria toCategoria(CategoriaDTO categoriaDTO){
        return new Categoria(
                categoriaDTO.nombre(),
                categoriaDTO.descripcion(),
                categoriaDTO.subcategoriaID()
        );
    }

}
