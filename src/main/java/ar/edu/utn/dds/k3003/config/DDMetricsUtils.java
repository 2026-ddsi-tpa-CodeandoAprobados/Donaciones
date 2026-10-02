package ar.edu.utn.dds.k3003.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.datadog.DatadogConfig;
import io.micrometer.datadog.DatadogMeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class DDMetricsUtils {

  @Getter
  private final MeterRegistry meterRegistry;

  public DDMetricsUtils(@Value("${datadog.api.key:}") String apiKey) {
    // Si hay una API key configurada, usar Datadog; si no, usar un registro vacío
    if (apiKey != null && !apiKey.isEmpty()) {
      log.info("Initializing Datadog metrics registry");
      var config = new DatadogConfig() {
        @Override
        public Duration step() {
          return Duration.ofSeconds(10);
        }

        @Override
        public String apiKey() {
          return apiKey;
        }

        @Override
        public String uri() {
          return "https://api.us5.datadoghq.com";
        }

        @Override
        public String get(String k) {
          return null; // accept the rest of the defaults
        }
      };
      this.meterRegistry = new DatadogMeterRegistry(config, io.micrometer.core.instrument.Clock.SYSTEM);
    } else {
      log.warn("Datadog API key not configured. Metrics will not be sent to Datadog.");
      this.meterRegistry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    }

    this.meterRegistry.config().commonTags("app", "donaciones");
  }
}
