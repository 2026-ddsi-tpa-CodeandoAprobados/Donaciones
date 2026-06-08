package ar.edu.utn.dds.k3003.model.donaciones;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Donaciones")
public class Donacion{

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter @Getter private String id;

    @Column(name = "donadorID" ,  nullable = false)
    @Setter @Getter private String donadorID;

    @Column(name = "depositoID" , nullable = false)
    @Setter @Getter private String depositoID;

    @Column(name = "descripcionDonacion" ,  nullable = false)
    @Setter @Getter private String descripcionDonacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado" , nullable = false)
    @Setter @Getter private EstadoDonacionEnum estado;

    @OneToMany
    @JoinColumn(name = "detalleProductoID")
    @Setter @Getter private List<DetalleProducto> detallesProductos;

    @Column(nullable = false , length = 10)
    @Setter @Getter private LocalDate fechaRegistro;

    public Donacion() {}

    public Donacion
        (String donadorID,
         String depositoID,
         String descripcionDonacion,
         EstadoDonacionEnum estado
        ){
        this.setDonadorID(donadorID);
        this.setDepositoID(depositoID);
        this.setDescripcionDonacion(descripcionDonacion);
        this.setDetallesProductos(detallesProductos);
        this.setEstado(estado);
        this.fechaRegistro = LocalDate.now();
    }

    public Donacion modificarEstado(EstadoDonacionEnum nuevoEstado){
        setEstado(nuevoEstado);
        return this;
    }

    public Boolean cumpleConFecha(LocalDate fecha)
    {return !this.getFechaRegistro().isBefore(fecha);}

    public Boolean mismoDonador(String donadorID)
    {return this.getDonadorID().equals(donadorID);}

}
