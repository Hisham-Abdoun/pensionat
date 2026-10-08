package org.example.pensionat.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Egen health indicator som kontrollerar att Kundtjänsten (som pensionat är beroende av) svarar.
 * Visas under "kundtjanst" i /actuator/health.
 *
 * Om Kundtjänsten är nere rapporteras DEGRADED i stället för DOWN. Pensionat fungerar
 * fortfarande (rum och bokningar), och vi vill inte att Render startar om vår tjänst
 * bara för att ett beroende är nere. Se management.endpoint.health.status.order.
 */
@Component("kundtjanst")
public class KundtjanstHealthIndicator implements HealthIndicator {

    public static final Status DEGRADED = new Status("DEGRADED", "Kundtjänsten svarar inte");

    private static final Logger log = LoggerFactory.getLogger(KundtjanstHealthIndicator.class);

    private final RestClient restClient;
    private final String customerServiceUrl;

    @Autowired
    public KundtjanstHealthIndicator(@Value("${customer.service.url}") String customerServiceUrl) {
        this(RestClient.builder().requestFactory(timeouts()).build(), customerServiceUrl);
    }

    // Används i tester så att vi kan mocka HTTP-anropen
    public KundtjanstHealthIndicator(RestClient restClient, String customerServiceUrl) {
        this.restClient = restClient;
        this.customerServiceUrl = customerServiceUrl;
    }

    @Override
    public Health health() {
        long start = System.currentTimeMillis();
        try {
            restClient.get()
                    .uri(customerServiceUrl + "/api/customers")
                    .retrieve()
                    .toBodilessEntity();
            long ms = System.currentTimeMillis() - start;
            return Health.up()
                    .withDetail("url", customerServiceUrl)
                    .withDetail("responseTimeMs", ms)
                    .build();
        } catch (Exception e) {
            log.warn("Health check: Kundtjänsten svarar inte ({})", e.getMessage());
            return Health.status(DEGRADED)
                    .withDetail("url", customerServiceUrl)
                    .withDetail("error", e.getClass().getSimpleName())
                    .build();
        }
    }

    // Korta timeouts så att health-anropet aldrig hänger (Renders health check har egen timeout)
    private static SimpleClientHttpRequestFactory timeouts() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(3000);
        return factory;
    }
}
