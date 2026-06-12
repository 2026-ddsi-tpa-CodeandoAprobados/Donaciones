package ar.edu.utn.dds.k3003.controllers.donaciones;


import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.exceptions.donaciones.CambioEstadoInvalido;
import ar.edu.utn.dds.k3003.exceptions.donaciones.DonacionNoEncontrada;
import ar.edu.utn.dds.k3003.exceptions.donaciones.DonacionNoSePuedeRegistrar;
import ar.edu.utn.dds.k3003.exceptions.donaciones.SinDonaciones;
import ar.edu.utn.dds.k3003.exceptions.donadores.DonadorNoEncontrado;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/donaciones")
public class DonacionController {

    @Autowired private Fachada fachada;

    @PostMapping
    public ResponseEntity<DonacionDTO> createDonacion(@RequestBody DonacionRequest donacionRequest) {

        try {
            val detallesProductosDTOs = fachada.detallesFromRequestToDTOs(donacionRequest.detallesProductosRequest());

            val donacionDTO =
                    new DonacionDTO(null, donacionRequest.donadorID(), donacionRequest.depositoID(), donacionRequest.descripcion(), detallesProductosDTOs, null);

            DonacionDTO donacionResponse = fachada.registrarDonacion(donacionDTO);
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(202))
                    .body(donacionResponse);
        } catch (DonacionNoSePuedeRegistrar e) {
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(401))
                    .body(null);
        } catch (DonadorNoEncontrado e) {
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(null);
        }

    }

    @GetMapping
    public ResponseEntity<List<DonacionDTO>> getAllDonaciones() {

        try {
            List<DonacionDTO> donacionesResponse = fachada.findAllDonaciones();
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(donacionesResponse);
        } catch (SinDonaciones e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonacionDTO> getDonacion(@PathVariable("id") String donacionID) {

        try{
            DonacionDTO donacionResponse = fachada.buscarDonacionPorID(donacionID);
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(donacionResponse);

        }catch (DonacionNoEncontrada e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonacion(@PathVariable("id") String donacionID) {

        try {
            fachada.deleteDonacion(donacionID);
            return ResponseEntity
                    .noContent()
                    .build();

        } catch (DonacionNoEncontrada e) {
            return ResponseEntity
                    .notFound()
                    .build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<DonacionDTO>> buscarDonacionesDonadorYfechaInicio (
            @RequestParam String donadorID,
            @RequestParam LocalDate fechaInicio
    )
        {
            try{
                List<DonacionDTO> donacionesDTO = fachada.findByDonadorYFechaInicio(donadorID, fechaInicio);
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(200))
                        .body(donacionesDTO);
            }catch(DonacionNoEncontrada e){
                return ResponseEntity
                        .status(HttpStatusCode.valueOf(404))
                        .body(null);
            }
        }

    @GetMapping("/search/{donadorID}")
    public ResponseEntity<List<DonacionDTO>> findDonacionesByDonadorID (@PathVariable("donadorID") String donadorID) {
        try{
            List<DonacionDTO> donacionesDTOs = fachada.findDonacionesByDonadorID(donadorID);
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(donacionesDTOs);
        }catch(DonacionNoEncontrada e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(null);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<DonacionDTO> actualizarEstadoDonacion(
            @PathVariable("id") String donacionID,
            @RequestBody EstadoDonacionRequest request
    )
    {
        try{
            DonacionDTO donacionModificadaDTO = fachada.cambiarEstadoDeDonacion(donacionID, request.getEstado());
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(200))
                    .body(donacionModificadaDTO);
        }
        catch (CambioEstadoInvalido e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(400))
                    .body(null);
        }
        catch(DonacionNoEncontrada e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(null);
        }
    }

    @PostMapping("/{id}/queja")
    public ResponseEntity<DonacionDTO> registrarQueja(
            @PathVariable("id")  String donacionID,
            @RequestBody QuejaRequest quejaRequest
    )
    {
        try{
            DonacionDTO donacionModificada =  fachada.registrarQuejaEnDonacion(donacionID,quejaRequest.descripcion());
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(201))
                    .body(donacionModificada);
        }
        catch(DonacionNoEncontrada e){
            return ResponseEntity
                    .status(HttpStatusCode.valueOf(404))
                    .body(null);
        }
    }


}



