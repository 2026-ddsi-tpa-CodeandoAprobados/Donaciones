package ar.edu.utn.dds.k3003;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.ProductoDTO;
import ar.edu.utn.dds.k3003.clients.DonadoresYEntidadesClient;
import ar.edu.utn.dds.k3003.clients.LogisticaClient;
import ar.edu.utn.dds.k3003.messaging.DonacionesEventPublisher;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.historialEstados.HistorialEstadosDonacionRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores.IdentificadoresRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

class FachadaAdminTest {

    private Fachada fachada;
    private DonacionesEventPublisher publicador;
    private IdentificadoresRepository identificadores;
    private ProductosRepository productos;
    private DonacionesRepository donaciones;
    private HistorialEstadosDonacionRepository historial;
    private DetallesProductosRepository detalles;
    private CategoriasRepository categorias;
    private SubcategoriasRepository subcategorias;

    @BeforeEach
    void preparar() {
        fachada = new Fachada(mock(DonadoresYEntidadesClient.class), mock(LogisticaClient.class));
        publicador = mock(DonacionesEventPublisher.class);
        identificadores = mock(IdentificadoresRepository.class);
        productos = mock(ProductosRepository.class);
        donaciones = mock(DonacionesRepository.class);
        historial = mock(HistorialEstadosDonacionRepository.class);
        detalles = mock(DetallesProductosRepository.class);
        categorias = mock(CategoriasRepository.class);
        subcategorias = mock(SubcategoriasRepository.class);

        fachada.setIdentificadoresRepository(identificadores);
        fachada.setProductosRepository(productos);
        fachada.setDonacionesRepository(donaciones);
        fachada.setHistorialEstadosRepository(historial);
        fachada.setDetallesProductosRepository(detalles);
        fachada.setCategoriasRepository(categorias);
        fachada.setSubcategoriasRepository(subcategorias);
        fachada.setProductosDataMapper(new ProductosDataMapper());
        ReflectionTestUtils.setField(fachada, "donacionesEventPublisher", publicador);
    }

    @Test
    void vaciarLaBasePublicaBaseLimpiadaDespuesDeBorrarTodo() {
        fachada.vaciarBaseDeDatos();

        InOrder orden = inOrder(identificadores, productos, donaciones, historial, detalles, categorias,
                subcategorias, publicador);
        orden.verify(identificadores).deleteAll();
        orden.verify(productos).deleteAll();
        orden.verify(donaciones).deleteAll();
        orden.verify(historial).deleteAll();
        orden.verify(detalles).deleteAll();
        orden.verify(categorias).deleteAll();
        orden.verify(subcategorias).deleteAll();
        orden.verify(publicador).publicarBaseLimpiada();
    }

    @Test
    void siFallaElBorradoNoSePublicaBaseLimpiada() {
        doThrow(new RuntimeException("falla la base")).when(donaciones).deleteAll();

        assertThrows(RuntimeException.class, () -> fachada.vaciarBaseDeDatos());

        verify(publicador, never()).publicarBaseLimpiada();
    }

    @Test
    void republicarProductosPublicaTodosLosProductosYDevuelveRepublicadosYTotal() {
        when(productos.findAll()).thenReturn(List.of(
                producto(7L, "Fideos", "2"), producto(8L, "Arroz", "3"), producto(9L, "Leche", "4")));
        when(publicador.republicarProductos(anyList())).thenReturn(2);

        Map<String, Integer> resultado = fachada.republicarProductos();

        assertEquals(Map.of("republicados", 2, "total", 3), resultado);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ProductoDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(publicador).republicarProductos(captor.capture());
        assertEquals(List.of("7", "8", "9"), captor.getValue().stream().map(ProductoDTO::id).toList());
        assertEquals("Fideos", captor.getValue().get(0).nombre());
        assertEquals("2", captor.getValue().get(0).subcategoriaID());
    }

    @Test
    void republicarSinProductosDevuelveCero() {
        when(productos.findAll()).thenReturn(List.of());
        when(publicador.republicarProductos(anyList())).thenReturn(0);

        assertEquals(Map.of("republicados", 0, "total", 0), fachada.republicarProductos());
    }

    private static Producto producto(Long id, String nombre, String subcategoriaID) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setSubcategoriaID(subcategoriaID);
        return p;
    }
}
