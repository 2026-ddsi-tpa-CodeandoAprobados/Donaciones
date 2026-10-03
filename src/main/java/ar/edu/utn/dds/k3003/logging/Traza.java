package ar.edu.utn.dds.k3003.logging;

import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.MDC;

public final class Traza {

    public static final String ENCABEZADO = "X-Trace-Id";

    public static final String MDC_TRAZA = "traceId";

    public static final String MDC_INSTANCIA = "instanceId";

    public static final String MDC_PEDIDO = "requestId";

    private Traza() {}

    public static String nuevoId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String actual() {
        return MDC.get(MDC_TRAZA);
    }

    public static String recibirOGenerar(String recibido) {
        return (recibido != null && VALIDO.matcher(recibido.trim()).matches())
                ? recibido.trim()
                : nuevoId();
    }

    private static final Pattern VALIDO = Pattern.compile("[A-Za-z0-9._-]{1,64}");
}
