package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DetalleProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.categorias.Categoria;
import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Publica los cambios de estado de Donaciones para el modelo de lectura.
 *
 * <ul>
 *   <li><b>Después del commit.</b> Si la operación está dentro de una transacción, el envío se hace recién cuando
 *       se confirma; si hay rollback no sale ningún evento.
 *   <li><b>Sin romper la operación.</b> Si el broker no está o falla el armado del evento, se registra un aviso y la
 *       operación sigue: la fuente de verdad es la base de Donaciones.
 *   <li><b>Con la clasificación del producto.</b> Cada detalle de una donación lleva la categoría y la subcategoría
 *       de su producto (id y nombre), y los productos se publican al crearse, modificarse y eliminarse, para que
 *       el modelo de lectura pueda clasificar también las necesidades.
 * </ul>
 */
@Component
public class DonacionesEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DonacionesEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final ProductosRepository productosRepository;
    private final SubcategoriasRepository subcategoriasRepository;
    private final CategoriasRepository categoriasRepository;

    @Value("${app.rabbitmq.exchange:donatrack.exchange}")
    private String exchangeName;

    public DonacionesEventPublisher(RabbitTemplate rabbitTemplate,
                                    ProductosRepository productosRepository,
                                    SubcategoriasRepository subcategoriasRepository,
                                    CategoriasRepository categoriasRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.productosRepository = productosRepository;
        this.subcategoriasRepository = subcategoriasRepository;
        this.categoriasRepository = categoriasRepository;
    }

    // ---------------- Donaciones ----------------

    public void publicarDonacionCreada(DonacionDTO donacion) {
        publicarDonacion("donaciones.donacion.creada", TipoEventoDonaciones.DONACION_CREADA, donacion);
    }

    public void publicarEstadoModificado(DonacionDTO donacion) {
        publicarDonacion("donaciones.donacion.estado_modificado", TipoEventoDonaciones.DONACION_ESTADO_MODIFICADO, donacion);
    }

    public void publicarDonacionEliminada(DonacionDTO donacion) {
        publicarDonacion("donaciones.donacion.eliminada", TipoEventoDonaciones.DONACION_ELIMINADA, donacion);
    }

    // ---------------- Productos ----------------

    public void publicarProductoCreado(ProductoDTO producto) {
        publicarProducto("donaciones.producto.creado", TipoEventoDonaciones.PRODUCTO_CREADO, producto);
    }

    public void publicarProductoModificado(ProductoDTO producto) {
        publicarProducto("donaciones.producto.modificado", TipoEventoDonaciones.PRODUCTO_MODIFICADO, producto);
    }

    public void publicarProductoEliminado(ProductoDTO producto) {
        publicarProducto("donaciones.producto.eliminado", TipoEventoDonaciones.PRODUCTO_ELIMINADO, producto);
    }

    /**
     * Vuelve a publicar PRODUCTO_CREADO por cada producto, con el mismo payload que al crearlo. Sirve para que el
     * modelo de lectura conozca los productos que existían antes de que se publicaran estos eventos. Un producto que
     * falla se registra y se sigue con el próximo.
     *
     * @return cuántos eventos se enviaron al broker
     */
    public int republicarProductos(List<ProductoDTO> productos) {
        int enviados = 0;
        for (ProductoDTO producto : productos) {
            try {
                EventoDonaciones evento = new EventoDonaciones(UUID.randomUUID().toString(),
                        TipoEventoDonaciones.PRODUCTO_CREADO, Instant.now(), producto.id(), null,
                        productoEvento(producto));
                if (enviar("donaciones.producto.creado", evento)) {
                    enviados++;
                }
            } catch (Exception e) {
                log.warn("No se pudo armar el evento PRODUCTO_CREADO del producto {}: {}", producto.id(),
                        e.getMessage());
            }
        }
        return enviados;
    }

    // ---------------- Base ----------------

    /** Se vació la base de Donaciones: el modelo de lectura debe borrar lo suyo. */
    public void publicarBaseLimpiada() {
        try {
            EventoDonaciones evento = new EventoDonaciones(UUID.randomUUID().toString(),
                    TipoEventoDonaciones.BASE_LIMPIADA, Instant.now(), "donaciones", null, null);
            enviarDespuesDelCommit("donaciones.base.limpiada", evento);
        } catch (Exception e) {
            log.warn("No se pudo armar el evento {}: {}", TipoEventoDonaciones.BASE_LIMPIADA, e.getMessage());
        }
    }

    // ---------------- Armado y envío ----------------

    private void publicarDonacion(String routingKey, TipoEventoDonaciones tipo, DonacionDTO donacion) {
        try {
            EventoDonaciones evento = new EventoDonaciones(UUID.randomUUID().toString(), tipo, Instant.now(),
                    donacion.id(), donacionEvento(donacion), null);
            enviarDespuesDelCommit(routingKey, evento);
        } catch (Exception e) {
            log.warn("No se pudo armar el evento {} de la donación {}: {}", tipo, donacion.id(), e.getMessage());
        }
    }

    private void publicarProducto(String routingKey, TipoEventoDonaciones tipo, ProductoDTO producto) {
        try {
            EventoDonaciones evento = new EventoDonaciones(UUID.randomUUID().toString(), tipo, Instant.now(),
                    producto.id(), null, productoEvento(producto));
            enviarDespuesDelCommit(routingKey, evento);
        } catch (Exception e) {
            log.warn("No se pudo armar el evento {} del producto {}: {}", tipo, producto.id(), e.getMessage());
        }
    }

    private DonacionEvento donacionEvento(DonacionDTO donacion) {
        List<DetalleProductoEvento> detalles = donacion.detallesProductosDTO() == null ? List.of()
                : donacion.detallesProductosDTO().stream().map(this::detalleEvento).toList();
        return new DonacionEvento(donacion.id(), donacion.donadorID(), donacion.depositoID(), donacion.descripcion(),
                detalles, donacion.estado(), donacion.fechaRegistro());
    }

    private DetalleProductoEvento detalleEvento(DetalleProductoDTO detalle) {
        Clasificacion c = clasificarProducto(detalle.productoID());
        return new DetalleProductoEvento(detalle.id(), detalle.productoID(), detalle.cantidadProducto(),
                c.categoriaID(), c.categoria(), c.subcategoriaID(), c.subcategoria());
    }

    private ProductoEvento productoEvento(ProductoDTO producto) {
        Clasificacion c = clasificarSubcategoria(producto.subcategoriaID());
        return new ProductoEvento(producto.id(), producto.nombre(), producto.descripcion(),
                c.subcategoriaID(), c.subcategoria(), c.categoriaID(), c.categoria());
    }

    /** Producto, subcategoría y categoría. Si algo no se encuentra, esos datos quedan vacíos; nunca falla. */
    private Clasificacion clasificarProducto(String productoID) {
        try {
            return productosRepository.findById(Long.valueOf(productoID))
                    .map(p -> clasificarSubcategoria(p.getSubcategoriaID()))
                    .orElse(Clasificacion.VACIA);
        } catch (Exception e) {
            return Clasificacion.VACIA;
        }
    }

    private Clasificacion clasificarSubcategoria(String subcategoriaID) {
        try {
            Subcategoria sub = subcategoriasRepository.findById(Long.valueOf(subcategoriaID)).orElse(null);
            if (sub == null) {
                return new Clasificacion(null, null, subcategoriaID, null);
            }
            Categoria cat = categoriasRepository.findById(Long.valueOf(sub.getCategoriaID())).orElse(null);
            return new Clasificacion(sub.getCategoriaID(), cat == null ? null : cat.getNombre(),
                    String.valueOf(sub.getId()), sub.getNombre());
        } catch (Exception e) {
            return Clasificacion.VACIA;
        }
    }

    private record Clasificacion(String categoriaID, String categoria, String subcategoriaID, String subcategoria) {
        static final Clasificacion VACIA = new Clasificacion(null, null, null, null);
    }

    private void enviarDespuesDelCommit(String routingKey, EventoDonaciones evento) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    enviar(routingKey, evento);
                }
            });
        } else {
            enviar(routingKey, evento);
        }
    }

    /** @return true si el broker aceptó el envío */
    private boolean enviar(String routingKey, EventoDonaciones evento) {
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey, evento);
            log.info("Evento {} publicado en {} ({}), idReferencia {}", evento.tipo(), exchangeName, routingKey,
                    evento.idReferencia());
            return true;
        } catch (Exception e) {
            log.warn("No se pudo publicar el evento {} (idReferencia {}): {}", evento.tipo(), evento.idReferencia(),
                    e.getMessage());
            return false;
        }
    }
}
