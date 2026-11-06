package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.DonacionDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DonacionesEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:donatrack.exchange}")
    private String exchangeName;

    public DonacionesEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarDonacionCreada(DonacionDTO donacion) {
        EventoDonaciones evento = crearEvento(
                TipoEventoDonaciones.DONACION_CREADA,
                donacion
        );

        enviarEvento("donaciones.donacion.creada", evento);
    }

    public void publicarEstadoModificado(DonacionDTO donacion) {
        EventoDonaciones evento = crearEvento(
                TipoEventoDonaciones.DONACION_ESTADO_MODIFICADO,
                donacion
        );

        enviarEvento("donaciones.donacion.estado_modificado", evento);
    }

    public void publicarDonacionEliminada(DonacionDTO donacion) {
        EventoDonaciones evento = crearEvento(
                TipoEventoDonaciones.DONACION_ELIMINADA,
                donacion
        );

        enviarEvento("donaciones.donacion.eliminada", evento);
    }

    private EventoDonaciones crearEvento(
            TipoEventoDonaciones tipo,
            DonacionDTO donacion) {

        return new EventoDonaciones(
                UUID.randomUUID().toString(),
                tipo,
                Instant.now(),
                donacion.id(),
                donacion
        );
    }

    private void enviarEvento(String routingKey, EventoDonaciones evento) {
        rabbitTemplate.convertAndSend(
                exchangeName,
                routingKey,
                evento
        );
    }
}