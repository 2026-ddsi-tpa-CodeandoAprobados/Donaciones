package ar.edu.utn.dds.k3003.controllers.productos;

import lombok.Getter;
import lombok.Setter;

public record ProductoRequest (
    String nombre, String descripcion, String subcategoriaID, String identificadorID
){}
