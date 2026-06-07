package ar.edu.utn.dds.k3003.model;
import ar.edu.utn.dds.k3003.Fachada;
import ar.edu.utn.dds.k3003.catedra.ClassFinder;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.EstadoDonadorEnum;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.DepositoDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.logistica.TipoAlgoritmoEnum;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.donaciones.DonacionNoSePuedeRegistrar;
import ar.edu.utn.dds.k3003.repositories_dataMapper.historialEstados.HistorialEstadosDonacionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static ar.edu.utn.dds.k3003.catedra.dtos.donaciones.EstadoDonacionEnum.ACEPTADA;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@EnabledIf("ar.edu.utn.dds.k3003.catedra.donaciones.DonacionesTest#condicion")
public class DonacionesPropioTest {

    FachadaDonaciones instancia;

    @Mock
    FachadaDonadoresYEntidades fachadaDonadoresYEntidades;
    @Mock
    FachadaLogistica fachadaLogistica;

    @Mock
    HistorialEstadosDonacionRepository historialEstadosDonacionRepository;


    DonadorDTO donadorEjemplo;
    DonacionDTO donacionEjemplo;
    DonacionDTO donacionAceptadaEjemplo;
    QuejaDTO quejaAceptadaEjemplo;
    QuejaDTO quejaEjemplo;

    @SneakyThrows
    @BeforeEach
    public void setUp() {

        var clazz = ClassFinder.findClass();
        instancia = (FachadaDonaciones) clazz.getDeclaredConstructor().newInstance();

        instancia.setFachadaDonadoresYEntidades(fachadaDonadoresYEntidades);
        instancia.setFachadaLogistica(fachadaLogistica);

        ((Fachada) instancia).setHistorialEstadosRepository(historialEstadosDonacionRepository);

        donacionEjemplo =
         new DonacionDTO(
        null,
        "donador1",
        "deposito1",
        "descripcion1",
        "producto1",
        5,
         EstadoDonacionEnum.INGRESADA);

        donacionAceptadaEjemplo =
            new DonacionDTO(
                null,
                "donador1",
                "deposito1",
                "descripcion1",
                "producto1",
                5,
                ACEPTADA);

        donadorEjemplo =
                new DonadorDTO(
                    "donador1",
                    "donador1",
                    "donador1",
                    5,
                    "donador1",
                    "donador1",
                    "donador1",
                    EstadoDonadorEnum.VERIFICADO,
                    "donador1");

        quejaAceptadaEjemplo = new QuejaDTO(null, "donacion1", "donador1", null, "descripcion1");

        quejaEjemplo = new QuejaDTO(null, "donacion2", "donador2", null, "descripcion2");

    }

    static boolean condicion() {

        return FachadaDonaciones.class.isAssignableFrom(Fachada.class);
    }

    @Test
    public void testRegistrarDonacionFallidoDonadorInhabilitado()
    {
        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorEjemplo.id())).
                thenReturn(donadorEjemplo);
        when(fachadaDonadoresYEntidades.puedeDonar(donadorEjemplo.id())).
                thenReturn(Boolean.FALSE);

        Assertions.assertThrows(DonacionNoSePuedeRegistrar.class, () -> {
            instancia.registrarDonacion(donacionEjemplo);
        });

        verify(fachadaDonadoresYEntidades, times(1)).puedeDonar(donadorEjemplo.id());
        verify(fachadaDonadoresYEntidades, times(1)).buscarDonadorPorID(donadorEjemplo.id());
        verify(fachadaLogistica, never()).gestionarDonacion(any(), any(), any(), anyInt());
    }

    @Test
    public void testEstablecerQuejaComoEstado(){

        when(fachadaDonadoresYEntidades.buscarDonadorPorID(donadorEjemplo.id())).
                thenReturn(donadorEjemplo);
        when(fachadaDonadoresYEntidades.puedeDonar(donadorEjemplo.id())).
                thenReturn(Boolean.TRUE);
        when(fachadaDonadoresYEntidades.agregarQueja(any(QuejaDTO.class))).
                thenReturn(quejaAceptadaEjemplo);

        DonacionDTO donacionInicial = instancia.registrarDonacion(donacionEjemplo);
        String donacionID = donacionInicial.id();
        String descripcionQueja = "La comida llegó fría";


        instancia.registrarQuejaEnDonacion(donacionID, descripcionQueja);

        DonacionDTO donacionFinal = instancia.buscarDonacionPorID(donacionID);

        Assertions.assertEquals(EstadoDonacionEnum.CONQUEJA, donacionFinal.estado());

        verify(fachadaDonadoresYEntidades, times(1)).puedeDonar(donadorEjemplo.id());
        verify(fachadaDonadoresYEntidades, times(1)).buscarDonadorPorID(donadorEjemplo.id());
        verify(fachadaDonadoresYEntidades, times(1)).agregarQueja(any(QuejaDTO.class));
    }

    @Test
    public void testBuscarPorDonadorYFechaInicioFiltraCorrectamente() {
        DonacionDTO donacionA = new DonacionDTO(null, "donador1", "dep1", "desc1", "prod1", 5, EstadoDonacionEnum.INGRESADA);

        when(fachadaDonadoresYEntidades.buscarDonadorPorID(anyString())).thenReturn(donadorEjemplo);
        when(fachadaDonadoresYEntidades.puedeDonar(anyString())).thenReturn(Boolean.TRUE);

        instancia.registrarDonacion(donacionA);

        LocalDate fechaBusqueda = LocalDate.now();

        List<DonacionDTO> resultados = instancia.buscarPorDonadorYFechaInicio("donador1", fechaBusqueda);

        Assertions.assertFalse(resultados.isEmpty(), "La lista no debería estar vacía.");
        Assertions.assertEquals(1, resultados.size(), "Debería haber encontrado exactamente 1 donación.");
        Assertions.assertEquals("donador1", resultados.get(0).donadorID(), "El ID del donador debería coincidir.");
    }

    @Test
    public void testHistorialDeEstadosRegistraCambioCorrectamente() {
        // Arrange
        when(fachadaDonadoresYEntidades.buscarDonadorPorID("donador1")).thenReturn(donadorEjemplo);
        when(fachadaDonadoresYEntidades.puedeDonar("donador1")).thenReturn(Boolean.TRUE);
        when(fachadaLogistica.gestionarDonacion(any(), any(), any(), anyInt()))
                .thenReturn(new DepositoDTO("dep1", TipoAlgoritmoEnum.SUB_ATENDIDOS, "dep1", "dir1", 100, null));

        DonacionDTO donacion = instancia.registrarDonacion(donacionEjemplo);

        instancia.cambiarEstadoDeDonacion(donacion.id(), EstadoDonacionEnum.ACEPTADA);

        verify(historialEstadosDonacionRepository, atLeastOnce()).save(eq(donacion.id()), eq(EstadoDonacionEnum.ACEPTADA));

    }
}


