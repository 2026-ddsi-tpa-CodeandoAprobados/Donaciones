package ar.edu.utn.dds.k3003.clients;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "logistica-service" , url = "${LOGISTICA_URL}")
public interface LogisticaClient {

    @PostMapping("/donaciones")
    DepositoDTO gestionarDonacion(@RequestBody DonacionDTO donacionDTO);
}
