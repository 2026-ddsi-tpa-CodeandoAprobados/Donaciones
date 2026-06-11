package ar.edu.utn.dds.k3003.repositories_DataMapper.categorias;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.model.categorias.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriasRepository extends JpaRepository<Categoria , Long> {
}
