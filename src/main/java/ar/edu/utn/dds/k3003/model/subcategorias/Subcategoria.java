package ar.edu.utn.dds.k3003.model.subcategorias;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subcategorias")
public class Subcategoria {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Getter @Setter private String id;

	@Column(nullable = false)
	@Getter @Setter private String descripcion;

	@Column(nullable = false)
	@Getter @Setter private String nombre;

	@Column(nullable = false)
	@Getter @Setter private String categoriaID;

	public Subcategoria() {}

	public Subcategoria(String nombre, String descripcion, String categoriaID) {
		this.setNombre(nombre);
		this.setDescripcion(descripcion);
		this.setCategoriaID(categoriaID);
	}

}
