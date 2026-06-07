package ar.edu.utn.dds.k3003.controllers.categorias;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.CategoriaDTO;
import ar.edu.utn.dds.k3003.exceptions.categorias.CategoriaDesconocida;
import ar.edu.utn.dds.k3003.exceptions.categorias.CategoriaNoEncontrada;
import ar.edu.utn.dds.k3003.exceptions.categorias.SinCategorias;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private Fachada fachada;

    @PostMapping
    public ResponseEntity<CategoriaDTO> createCategoria(@RequestBody CategoriaRequest categoriaRequest){

        try{
            val categoriaDTO = new CategoriaDTO(
                    null, categoriaRequest.getSubcategoriaID(), categoriaRequest.getNombre(), categoriaRequest.getDescripcion()
            );

            val categoriaAgregadaDTO = fachada.agregarCategoria(categoriaDTO);

            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(categoriaAgregadaDTO);

        }catch(CategoriaDesconocida e){
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> findAllCategorias(){
        try{
            val categoriasDTO = fachada.findAllCategorias();

            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(categoriasDTO);

        } catch(SinCategorias e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable("id") String categoriaID){

        try{
            fachada.eliminarCategoria(categoriaID);
            return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(null);
        }catch(CategoriaNoEncontrada e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }

}
