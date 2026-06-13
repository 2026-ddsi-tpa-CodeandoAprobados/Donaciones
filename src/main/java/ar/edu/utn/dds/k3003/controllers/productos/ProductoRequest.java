package ar.edu.utn.dds.k3003.controllers.productos;

public record ProductoRequest (
    String nombre, String descripcion, String subcategoriaID, String identificadorId
){}
