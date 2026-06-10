package ar.edu.utn.dds.k3003.model.productos;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_productos")

public class DetalleProducto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter  private Long detalleProductoID;

    @Column(nullable = false)
    @Getter @Setter private String productoID;

    @Column(nullable = false)
    @Getter @Setter private Integer cantidadProducto;

    public DetalleProducto() {}

    public DetalleProducto(String productoID, Integer cantidadProducto) {
        this.setProductoID(productoID);
        this.setCantidadProducto(cantidadProducto);
    }
}
