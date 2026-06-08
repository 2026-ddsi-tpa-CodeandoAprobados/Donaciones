package ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores;

import ar.edu.utn.dds.k3003.model.identificadores.Identificador;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryIdentificadorRepo implements IdentificadoresRepository{

    @Getter private AtomicLong idSecuencial = new AtomicLong(1);
    @Getter private List<Identificador> identificadores = new ArrayList<>();

    @Override
    public Identificador save(Identificador identificador) {

        if(identificador.getId() == null) {
            identificador.setId(String.valueOf(this.getIdSecuencial().getAndIncrement()));
        }

        this.identificadores.add(identificador);

        return this.findByID(identificador.getId()).get();

    }

    @Override
    public Optional<Identificador> findByID(String id) {
        return identificadores.stream().filter(identificador -> identificador.getId().equals(id)).findFirst();
    }

    @Override
    public void deleteById(String identificadorID) {
        this.getIdentificadores().removeIf(id -> id.getId().equals(identificadorID));
    }

    @Override
    public List<Identificador> findAllIdentificadores() {
        return this.getIdentificadores();
    }

}
