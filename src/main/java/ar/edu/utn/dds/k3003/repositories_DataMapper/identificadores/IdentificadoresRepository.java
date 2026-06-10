package ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores;

import ar.edu.utn.dds.k3003.model.identificadores.Identificador;

import java.util.List;
import java.util.Optional;

public interface IdentificadoresRepository {

    Identificador save(Identificador identificador);

    Optional<Identificador> findByID(Long id);

    List<Identificador> findAllIdentificadores();

    void deleteById(String identificadorID);

}


