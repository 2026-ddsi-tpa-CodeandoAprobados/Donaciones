package ar.edu.utn.dds.k3003.controllers.donaciones;

import java.util.List;

public record DonacionRequest (
    String donadorID, String depositoID, List<DetalleProductoRequest> detallesProductosRequest,
    String descripcion
){}
