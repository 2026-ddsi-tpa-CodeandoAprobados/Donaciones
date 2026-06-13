package ar.edu.utn.dds.k3003.controllers.identificadores;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import ar.edu.utn.dds.k3003.exceptions.identificadores.IdentificadorInvalido;
import ar.edu.utn.dds.k3003.exceptions.identificadores.IdentificadorNoEncontrado;
import ar.edu.utn.dds.k3003.exceptions.identificadores.SinIdentificadores;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/identificadores")
public class IdentificadorController {

    @Autowired
    private Fachada fachada;

    @PostMapping
    public ResponseEntity<IdentificadorDTO> createIdentificador(@RequestBody IdentificadorRequest identificadorRequest) {
        try{

            TipoIdentificadorEnum tipoEnumRequest = TipoIdentificadorEnum.valueOf(identificadorRequest.getTipo().toUpperCase().trim());

            val identificadorDTOSinID = new IdentificadorDTO(null, tipoEnumRequest, identificadorRequest.getDescripcion());

            val identificadorDTOConID= this.fachada.agregarIdentificador(identificadorDTOSinID);

            return ResponseEntity.status(HttpStatus.valueOf(201)).body(identificadorDTOConID);

        }catch (IdentificadorInvalido e){
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<IdentificadorDTO>> findAllIdentificadores() {
        try{

            val identificadoresDTO = fachada.findAllIdentificadores();
            return ResponseEntity.status(HttpStatus.valueOf(200)).body(identificadoresDTO);

        }catch (SinIdentificadores e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIdentificador(@PathVariable("id") String identificadorID) {

        try{
            fachada.eliminarIdentificador(identificadorID);

            return ResponseEntity.status(HttpStatus.valueOf(204)).build();

        }catch(IdentificadorNoEncontrado e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }
    }


}
