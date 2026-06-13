package ar.edu.utn.dds.k3003.controllers.productos;

import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.exceptions.categorias.CategoriaNoEncontrada;
import ar.edu.utn.dds.k3003.exceptions.categorias.SubcategoriaNoEncontrada;
import ar.edu.utn.dds.k3003.exceptions.identificadores.IdentificadorNoEncontrado;
import ar.edu.utn.dds.k3003.exceptions.productos.ProductoInexistente;
import ar.edu.utn.dds.k3003.exceptions.productos.ProductoInvalido;
import ar.edu.utn.dds.k3003.exceptions.productos.SinProductos;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/productos")
public class ProductoController {

    @Autowired
    private Fachada fachada;

    @PostMapping
    public ResponseEntity<ProductoDTO> agregarProducto(@RequestBody ProductoRequest productoRequest) {

        try{
            val productoDTO = new ProductoDTO(
                 null, productoRequest.nombre(),
                    productoRequest.descripcion(), productoRequest.subcategoriaID(),
                    productoRequest.identificadorId());

            val productoAgregadoDTO = fachada.agregarProducto(productoDTO);
            return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(productoAgregadoDTO);
        }
        catch(SubcategoriaNoEncontrada e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
        catch(IdentificadorNoEncontrado e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
        catch(ProductoInvalido e){
            return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);
        }

    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> findAllProductos() {
        try{
            val productosDTO = fachada.findAllProductos();
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productosDTO);
        }
        catch(SinProductos e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> findProductoById(@PathVariable("id") String productoID) {

        try{
            val productoDTO = fachada.buscarProductoPorID(productoID);
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(productoDTO);

        } catch(ProductoInexistente e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(@PathVariable("id") String productoID, @RequestBody ProductoRequest productoRequest) {

        try {
            val productoDTO = new ProductoDTO(productoID, productoRequest.nombre(),
                    productoRequest.descripcion(), productoRequest.subcategoriaID(), productoRequest.identificadorId());

            val productoRegistradoDTO = fachada.modificarProducto(productoID, productoDTO);

            return ResponseEntity.ok(productoRegistradoDTO);

        } catch (ProductoInexistente e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } catch (ProductoInvalido e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("id") String productoID) {
        try{
            fachada.eliminarProducto(productoID);
            return ResponseEntity.status(HttpStatusCode.valueOf(204)).build();
        }catch(ProductoInexistente e){
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).build();
        }
    }
}
