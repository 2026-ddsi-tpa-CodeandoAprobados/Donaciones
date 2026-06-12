package ar.edu.utn.dds.k3003.model.productos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "productos")
public class Producto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @Column(nullable = false)
    @Getter @Setter private String nombre;

    @Column(nullable = false)
    @Getter @Setter private String descripcion;

    @Column(nullable = false)
    @Getter @Setter private String subcategoriaID;

    @Column(nullable = false)
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
            this.setSubcategoriaID(categoriaID);
            this.setIdentificadorID(identificadorID);
        }


    public Producto() {}
}
