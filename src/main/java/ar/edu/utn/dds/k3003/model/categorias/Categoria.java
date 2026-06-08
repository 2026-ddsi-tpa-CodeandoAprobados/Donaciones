package ar.edu.utn.dds.k3003.model.categorias;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorias")

public class Categoria {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter @Getter private String id;;

    @Column(nullable = false)
    @Setter @Getter private String nombre;

    @Column(nullable = false)
    @Setter @Getter private String descripcion;

    public Categoria() {}

    public Categoria (String nombre, String descripcion) {
        this.setNombre(nombre);
        this.setDescripcion(descripcion);
    }
}
