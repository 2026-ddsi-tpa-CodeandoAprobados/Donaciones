package ar.edu.utn.dds.k3003.model.identificadores;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.TipoIdentificadorEnum;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "identificadores")
public class Identificador {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Getter @Setter private TipoIdentificadorEnum tipo;

    @Column(nullable = false)
    @Getter @Setter private String descripcion;

    public Identificador(TipoIdentificadorEnum tipo, String descripcion) {
        this.setTipo(tipo);
        this.setDescripcion(descripcion);
    }

    public Identificador() {}
}
