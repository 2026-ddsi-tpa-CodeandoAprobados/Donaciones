package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "donadoresYentidades-service" , url = "${DONADORES_Y_ENTIDADES_URL}")
public interface DonadoresYEntidadesClient {

    @GetMapping("/donadores/{id}/puede-donar")
    Map<String, Boolean> verificarSiPuedeDonar(@PathVariable("id") String donadorID);

    @GetMapping("/donadores/{id}")
    DonadorDTO buscarDonadorPorID(@PathVariable("id") String donadorID);

    @PostMapping("/quejas")
    QuejaDTO agregarQueja(@RequestBody QuejaDTO quejaDTO);

}
