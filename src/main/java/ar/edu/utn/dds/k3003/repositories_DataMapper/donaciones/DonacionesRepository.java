package ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DonacionesRepository extends JpaRepository<Donacion , Long> {
}
