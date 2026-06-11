package ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias;

import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubcategoriasRepository extends JpaRepository<Subcategoria , Long> {
}
