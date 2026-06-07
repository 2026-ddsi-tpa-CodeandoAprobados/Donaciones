package ar.edu.utn.dds.k3003.model.categorias;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorias")

public class Categoria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter private String id;;

    @Column(name = "cate_nombre" , nullable = false)
    @Setter @Getter private String nombre;

    @Column(name = "cate_descripcion" , nullable = false)
    @Setter @Getter private String descripcion;

    @Column(name = "cate_subcategoria" ,  nullable = false)
    @Setter @Getter private String subcategoriaID;

    public Categoria(String nombre, String descripcion, String subcategoriaID) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
        this.setSubcategoriaID(subcategoriaID);
    }

}
