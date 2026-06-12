package ar.edu.utn.dds.k3003.controllers.admin;

import ar.edu.utn.dds.k3003.Fachada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")

public class AdminController {

    @Autowired
    private Fachada fachada;

    @DeleteMapping("/db")
    public ResponseEntity<Void> vaciarBaseDeDatos(){
        fachada.vaciarBaseDeDatos();
        return ResponseEntity.status(HttpStatusCode.valueOf(204)).build();
    }



}
