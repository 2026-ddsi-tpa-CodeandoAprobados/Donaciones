package ar.edu.utn.dds.k3003.model.productos;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.IdentificadorDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "detalle_productos")

public class DetalleProducto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter  private String detalleProductoID;

    @Column(name = "deta_prod_producto" , nullable = false)
    @Getter @Setter private String productoID;

    @Column(name = "deta_prod_cantidad" , nullable = false)
    @Getter @Setter private Integer cantidadProducto;

    public DetalleProducto(String productoID, Integer cantidadProducto) {
        this.setProductoID(productoID);
        this.setCantidadProducto(cantidadProducto);
    }
}
