package ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;

public class SubcategoriasDataMapper {

    public SubcategoriaDTO toSubcategoriaDTO(Subcategoria subcategoria){
        return new SubcategoriaDTO(
                subcategoria.getId().toString(),
                subcategoria.getDescripcion(),
                subcategoria.getNombre(),
                subcategoria.getCategoriaID()
        );
    }

    public Subcategoria toSubcategoria(SubcategoriaDTO subcategoriaDTO){
        return new Subcategoria(
                subcategoriaDTO.nombre(),
                subcategoriaDTO.descripcion(),
                subcategoriaDTO.categoriaID()
        );
    }

}
