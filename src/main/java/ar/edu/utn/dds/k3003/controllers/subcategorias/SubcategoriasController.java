package ar.edu.utn.dds.k3003.controllers.subcategorias;


import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.SubcategoriaDTO;
import ar.edu.utn.dds.k3003.exceptions.categorias.CategoriaNoEncontrada;
import ar.edu.utn.dds.k3003.exceptions.categorias.Sinsubcategorias;
import ar.edu.utn.dds.k3003.exceptions.categorias.SubcategoriaNoEncontrada;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/subcategorias")
public class SubcategoriasController {

    @Autowired private Fachada fachada;

    @PostMapping
    public ResponseEntity<SubcategoriaDTO> createSubcategoria(@RequestBody SubcategoriaRequest subcategoriaRequest){

        try{
            val subcategoriaDTO = new SubcategoriaDTO(
                    null, subcategoriaRequest.descripcion(), subcategoriaRequest.nombre(), subcategoriaRequest.categoriaID()
            );

            val subcategoriaAgregadaDTO = fachada.agregarSubcategoria(subcategoriaDTO);

            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(subcategoriaAgregadaDTO);

        } catch(CategoriaNoEncontrada e){
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<SubcategoriaDTO>> findAllSubcategorias(){

        try{
            val subcategoriasDTOs = fachada.findAllSubcategorias();

            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(subcategoriasDTOs);
        } catch(Sinsubcategorias e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubcategoriaDTO> findSubcategoriaById(@PathVariable ("id") String subcategoriaID){
        try{
            val subcategoriaDTO = fachada.findSubcategoriaById(subcategoriaID);

            return  ResponseEntity.status(HttpStatusCode.valueOf(200)).body(subcategoriaDTO);

        } catch(SubcategoriaNoEncontrada e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubcategoriaById(@PathVariable("id") String subcategoriaID){
        try{
            this.fachada.deleteSubcategoriaById(subcategoriaID);

            return  ResponseEntity.status(HttpStatusCode.valueOf(204)).body(null);

        } catch(SubcategoriaNoEncontrada e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }

}
