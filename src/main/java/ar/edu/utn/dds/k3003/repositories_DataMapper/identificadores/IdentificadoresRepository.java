package ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores;

import ar.edu.utn.dds.k3003.model.identificadores.Identificador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdentificadoresRepository extends JpaRepository<Identificador , Long> {

}


