package ar.edu.utn.dds.k3003.model.productos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
public class Producto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private String id;

    @Column(name = "prod_nombre" , nullable = false)
    @Getter @Setter private String nombre;

    @Column(name = "prod_descripcion" , nullable = false)
    @Getter @Setter private String descripcion;

    @Column(name = "prod_categoria" , nullable = false)
    @Getter @Setter private String categoriaID;

    @Getter @Setter private String identificadorID;

    public Producto(
        String nombre,
        String descripcion,
        String categoriaID,
        String identificadorID
    )
        {
            this.setNombre(nombre);
            this.setDescripcion(descripcion);
            this.setCategoriaID(categoriaID);
            this.setIdentificadorID(identificadorID);
        }


}
