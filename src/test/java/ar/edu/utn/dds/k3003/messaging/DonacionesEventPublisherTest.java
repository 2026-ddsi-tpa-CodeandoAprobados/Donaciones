package ar.edu.utn.dds.k3003.messaging;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DetalleProductoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.model.categorias.Categoria;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class DonacionesEventPublisherTest {

    private RabbitTemplate rabbit;
    private ProductosRepository productos;
    private SubcategoriasRepository subcategorias;
    private CategoriasRepository categorias;
    private DonacionesEventPublisher publicador;

    @BeforeEach
    void preparar() {
        rabbit = mock(RabbitTemplate.class);
        productos = mock(ProductosRepository.class);
        subcategorias = mock(SubcategoriasRepository.class);
        categorias = mock(CategoriasRepository.class);
        publicador = new DonacionesEventPublisher(rabbit, productos, subcategorias, categorias);
        ReflectionTestUtils.setField(publicador, "exchangeName", "donatrack.exchange");

        Producto fideos = new Producto();
        fideos.setId(7L);
        fideos.setNombre("Fideos Matarazzo");
        fideos.setSubcategoriaID("2");
        Subcategoria subFideos = new Subcategoria();
        subFideos.setId(2L);
        subFideos.setNombre("Fideos");
        subFideos.setCategoriaID("1");
        Categoria alimentos = new Categoria();
        alimentos.setId(1L);
        alimentos.setNombre("Alimentos");
        when(productos.findById(7L)).thenReturn(Optional.of(fideos));
        when(subcategorias.findById(2L)).thenReturn(Optional.of(subFideos));
        when(categorias.findById(1L)).thenReturn(Optional.of(alimentos));
    }

    @AfterEach
    void limpiar() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    private DonacionDTO donacion(String productoID) {
        return new DonacionDTO("1052", "12", "3", "Fideos", List.of(new DetalleProductoDTO("x", productoID, 20)),
                EstadoDonacionEnum.INGRESADA, LocalDate.of(2026, 11, 6));
    }

    private EventoDonaciones eventoEnviado(String routingKey) {
        ArgumentCaptor<EventoDonaciones> captor = ArgumentCaptor.forClass(EventoDonaciones.class);
        verify(rabbit).convertAndSend(eq("donatrack.exchange"), eq(routingKey), captor.capture());
        return captor.getValue();
    }

    @Test
    void laDonacionCreadaLlevaLaCategoriaYLaSubcategoriaDeCadaProducto() {
        publicador.publicarDonacionCreada(donacion("7"));

        EventoDonaciones evento = eventoEnviado("donaciones.donacion.creada");
        assertEquals(TipoEventoDonaciones.DONACION_CREADA, evento.tipo());
        assertEquals("1052", evento.idReferencia());
        DetalleProductoEvento detalle = evento.donacion().detallesProductosDTO().get(0);
        assertEquals("7", detalle.productoID());
        assertEquals(20, detalle.cantidadProducto());
        assertEquals("1", detalle.categoriaID());
        assertEquals("Alimentos", detalle.categoria());
        assertEquals("2", detalle.subcategoriaID());
        assertEquals("Fideos", detalle.subcategoria());
        assertEquals("12", evento.donacion().donadorID());
        assertEquals("3", evento.donacion().depositoID());
    }

    @Test
    void unProductoSinClasificacionNoImpideQueSalgaElEvento() {
        publicador.publicarEstadoModificado(donacion("99"));

        EventoDonaciones evento = eventoEnviado("donaciones.donacion.estado_modificado");
        DetalleProductoEvento detalle = evento.donacion().detallesProductosDTO().get(0);
        assertEquals("99", detalle.productoID());
        assertNull(detalle.categoria());
        assertNull(detalle.subcategoria());
    }

    @Test
    void siElBrokerNoEstaLaOperacionNoFalla() {
        doThrow(new AmqpConnectException(new java.net.ConnectException("Connection refused")))
                .when(rabbit).convertAndSend(any(String.class), any(String.class), any(Object.class));

        assertDoesNotThrow(() -> publicador.publicarDonacionCreada(donacion("7")));
        assertDoesNotThrow(() -> publicador.publicarDonacionEliminada(donacion("7")));
    }

    @Test
    void dentroDeUnaTransaccionElEventoSaleRecienDespuesDelCommit() {
        TransactionSynchronizationManager.initSynchronization();

        publicador.publicarDonacionCreada(donacion("7"));

        verify(rabbit, never()).convertAndSend(any(String.class), any(String.class), any(Object.class));
        TransactionSynchronizationManager.getSynchronizations().forEach(TransactionSynchronization::afterCommit);
        verify(rabbit, times(1)).convertAndSend(eq("donatrack.exchange"), eq("donaciones.donacion.creada"), any(Object.class));
    }

    @Test
    void siLaTransaccionSeDeshaceNoSaleNingunEvento() {
        TransactionSynchronizationManager.initSynchronization();

        publicador.publicarDonacionCreada(donacion("7"));
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));

        verify(rabbit, never()).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }

    @Test
    void elProductoSePublicaConSuSubcategoriaYSuCategoria() {
        publicador.publicarProductoCreado(new ProductoDTO("7", "Fideos Matarazzo", "Paquete de 500g", "2", "5"));

        EventoDonaciones evento = eventoEnviado("donaciones.producto.creado");
        assertEquals(TipoEventoDonaciones.PRODUCTO_CREADO, evento.tipo());
        assertEquals("7", evento.idReferencia());
        assertNull(evento.donacion());
        ProductoEvento p = evento.producto();
        assertEquals("Fideos Matarazzo", p.nombre());
        assertEquals("2", p.subcategoriaID());
        assertEquals("Fideos", p.subcategoria());
        assertEquals("1", p.categoriaID());
        assertEquals("Alimentos", p.categoria());
    }

    @Test
    void elJsonConservaLosNombresDeCampoQueLeeElServicioDeConsultas() throws Exception {
        publicador.publicarDonacionCreada(donacion("7"));
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String json = mapper.writeValueAsString(eventoEnviado("donaciones.donacion.creada"));

        for (String campo : List.of("\"donacion\"", "\"donadorID\":\"12\"", "\"depositoID\":\"3\"",
                "\"detallesProductosDTO\"", "\"productoID\":\"7\"", "\"cantidadProducto\":20",
                "\"categoria\":\"Alimentos\"", "\"subcategoria\":\"Fideos\"", "\"categoriaID\":\"1\"",
                "\"estado\":\"INGRESADA\"", "\"fechaRegistro\":\"2026-11-06\"", "\"tipo\":\"DONACION_CREADA\"")) {
            assertTrue(json.contains(campo), campo + " no está en " + json);
        }
    }
}
