package ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DonacionesRepository {

    Optional<Donacion> findById(Long id);

    List<Donacion> findAll();

    Donacion save(Donacion donacion);

    void deleteById(String id);

    List<Donacion> filterByDateAndDonadorID(String donadorID, LocalDate fecha);

}
