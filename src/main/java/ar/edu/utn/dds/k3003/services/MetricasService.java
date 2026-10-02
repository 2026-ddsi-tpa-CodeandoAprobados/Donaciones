package ar.edu.utn.dds.k3003.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class MetricasService {

  private final MeterRegistry meterRegistry;

  // Contadores de donaciones
  private final Counter donacionesRegistradas;
  private final Counter donacionesExitosas;
  private final Counter donacionesRechazadas;

  // Gauges para estados
  private final AtomicInteger donacionesIngresadas = new AtomicInteger(0);
  private final AtomicInteger donacionesAceptadas = new AtomicInteger(0);
  private final AtomicInteger donacionesConQueja = new AtomicInteger(0);

  // Timer para procesamiento
  private final Timer timerRegistroDonacion;

  public MetricasService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;

    // Inicializar contadores
    this.donacionesRegistradas = Counter.builder("dds.donaciones.registradas")
        .description("Cantidad total de donaciones registradas en el sistema")
        .register(meterRegistry);

    this.donacionesExitosas = Counter.builder("dds.donaciones.exitosas")
        .description("Cantidad de donaciones procesadas exitosamente")
        .register(meterRegistry);

    this.donacionesRechazadas = Counter.builder("dds.donaciones.rechazadas")
        .description("Cantidad de donaciones rechazadas")
        .register(meterRegistry);

    // Inicializar gauges para estados
    meterRegistry.gauge("dds.donaciones.estado.ingresada", donacionesIngresadas);
    meterRegistry.gauge("dds.donaciones.estado.aceptada", donacionesAceptadas);
    meterRegistry.gauge("dds.donaciones.estado.conqueja", donacionesConQueja);

    // Inicializar timer
    this.timerRegistroDonacion = Timer.builder("dds.donaciones.tiempo_procesamiento")
        .description("Tiempo que tarda registrar una donación en el sistema")
        .publishPercentiles(0.5, 0.95, 0.99)
        .register(meterRegistry);
  }

  /**
   * Registra una nueva donación exitosa
   */
  public void registrarDonacionExitosa() {
    donacionesRegistradas.increment();
    donacionesExitosas.increment();
    donacionesIngresadas.incrementAndGet();
    log.info("Donación registrada exitosamente. Total: {}", 
        donacionesRegistradas.count());
  }

  /**
   * Registra una donación rechazada
   */
  public void registrarDonacionRechazada(String razon) {
    donacionesRegistradas.increment();
    donacionesRechazadas.increment();
    
    Counter errorCounter = Counter.builder("dds.donaciones.errores")
        .tag("tipo", razon)
        .description("Errores en el procesamiento de donaciones")
        .register(meterRegistry);
    errorCounter.increment();
    
    log.warn("Donación rechazada. Razón: {}. Total rechazadas: {}", 
        razon, donacionesRechazadas.count());
  }

  /**
   * Registra el cambio de estado de una donación
   */
  public void registrarCambioEstado(String estadoAnterior, String estadoNuevo) {
    // Decrementar el estado anterior
    switch (estadoAnterior) {
      case "INGRESADA" -> donacionesIngresadas.decrementAndGet();
      case "ACEPTADA" -> donacionesAceptadas.decrementAndGet();
      case "CONQUEJA" -> donacionesConQueja.decrementAndGet();
    }

    // Incrementar el nuevo estado
    switch (estadoNuevo) {
      case "ACEPTADA" -> {
        donacionesAceptadas.incrementAndGet();
        Counter counter = Counter.builder("dds.donaciones.por_estado")
            .tag("estado", "aceptada")
            .register(meterRegistry);
        counter.increment();
      }
      case "CONQUEJA" -> {
        donacionesConQueja.incrementAndGet();
        Counter counter = Counter.builder("dds.donaciones.por_estado")
            .tag("estado", "conqueja")
            .register(meterRegistry);
        counter.increment();
      }
      case "INGRESADA" -> {
        donacionesIngresadas.incrementAndGet();
        Counter counter = Counter.builder("dds.donaciones.por_estado")
            .tag("estado", "ingresada")
            .register(meterRegistry);
        counter.increment();
      }
    }

    log.info("Cambio de estado registrado: {} -> {}", estadoAnterior, estadoNuevo);
  }

  /**
   * Registra una métrica de producto en una donación
   */
  public void registrarProductoEnDonacion(String categoriaProducto, int cantidad) {
    Counter productCounter = Counter.builder("dds.donaciones.productos_cantidad")
        .tag("categoria", categoriaProducto)
        .description("Cantidad de productos por categoría en donaciones")
        .register(meterRegistry);
    
    productCounter.increment(cantidad);
    log.debug("Producto registrado. Categoría: {}, Cantidad: {}", categoriaProducto, cantidad);
  }

  /**
   * Registra el tiempo de procesamiento de una donación
   */
  public Timer.Sample iniciarTimerDonacion() {
    return Timer.start(meterRegistry);
  }

  /**
   * Finaliza el timer de procesamiento
   */
  public void finalizarTimerDonacion(Timer.Sample sample) {
    sample.stop(timerRegistroDonacion);
    log.debug("Tiempo de procesamiento de donación registrado");
  }

  /**
   * Obtener métrica actual de donaciones registradas
   */
  public double obtenerTotalDonacionesRegistradas() {
    return donacionesRegistradas.count();
  }

  /**
   * Obtener métrica actual de donaciones exitosas
   */
  public double obtenerTotalDonacionesExitosas() {
    return donacionesExitosas.count();
  }

  /**
   * Obtener métrica actual de donaciones rechazadas
   */
  public double obtenerTotalDonacionesRechazadas() {
    return donacionesRechazadas.count();
  }

  /**
   * Obtener cantidad de donaciones por estado
   */
  public int obtenerDonacionesPorEstado(String estado) {
    return switch (estado) {
      case "INGRESADA" -> donacionesIngresadas.get();
      case "ACEPTADA" -> donacionesAceptadas.get();
      case "CONQUEJA" -> donacionesConQueja.get();
      default -> 0;
    };
  }
}
