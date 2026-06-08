package ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;

import ar.edu.utn.dds.k3003.model.donaciones.Donacion;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class InMemoryDonacionesRepository implements DonacionesRepository {

    @Setter @Getter private List<Donacion> donaciones;

    @Setter @Getter private AtomicLong idSecuencial = new AtomicLong(1);

    public InMemoryDonacionesRepository(){
        this.donaciones = new ArrayList<>();
    }

    public List<Donacion> filterByDateAndDonadorID(String donadorID, LocalDate fecha) {
        return this.getDonaciones().stream().filter(d -> d.mismoDonador(donadorID) && d.cumpleConFecha(fecha)).toList();
    }

    public List<Donacion> findAll(){
        return this.getDonaciones();
    }

    @Override
    public Optional<Donacion> findById(String id) {
        return this.getDonaciones().stream().filter(d -> d.getId().equals(id)).findFirst();
    }

    @Override
    public Donacion save(Donacion donacion) {

        val donacionConID = donacion;

        if(donacionConID.getId() == null) {
            donacionConID.setId(String.valueOf(this.getIdSecuencial().getAndIncrement()));
        }

        this.getDonaciones().add(donacionConID);

        return this.findById(donacionConID.getId()).get();
    }


    @Override
    public void deleteById(String id) {
        this.getDonaciones().removeIf(d -> d.getId().equals(id));
    }
}
