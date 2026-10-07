package org.example.pensionat.client;

import org.example.pensionat.dto.CustomerDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class KundtjanstServiceClient {

    private static final Logger log = LoggerFactory.getLogger(KundtjanstServiceClient.class);

    private final RestClient restClient;
    private final String customerServiceUrl;

    public KundtjanstServiceClient(
            RestClient restClient,
            @Value("${customer.service.url}") String customerServiceUrl
    ) {
        this.restClient = restClient;
        this.customerServiceUrl = customerServiceUrl;
    }

    /**
     * Kontrollera om kund existerar i Kundservice
     */
    public boolean customerExists(Long customerId) {
        try {
            restClient.get()
                    .uri(customerServiceUrl + "/api/customers/" + customerId)
                    .retrieve()
                    .toEntity(CustomerDto.class);

            log.info("Kund {} finns i Kundtjänst", customerId);
            return true; // 200 OK → kunden finns

        }
        catch (HttpClientErrorException.NotFound e)
        {
            log.warn("Kund {} hittades inte i Kundtjänst (404)", customerId);
            return false; // 404 → kunden finns inte

        }
        catch (Exception e)
        {
            log.error("Kundtjänsten är inte tillgänglig vid kontroll av kund {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Kundtjänsten är inte tillgänglig");
        }
    }

    /**
     * Hämta kundens detaljer (valfritt, används om du vill visa kundnamn i bokningslistan)
     */
    public CustomerDto getCustomerById(Long id) {
        try {
            CustomerDto customer = restClient.get()
                    .uri(customerServiceUrl + "/api/customers/" + id)
                    .retrieve()
                    .body(CustomerDto.class);
            log.info("Hämtade kunddata för kund {} från Kundtjänst", id);
            return customer;

        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Kunden {} hittades inte i Kundtjänst (404)", id);
            throw new RuntimeException("Kunden finns inte");

        } catch (Exception e) {
            log.error("Kundtjänsten är inte tillgänglig vid hämtning av kund {}: {}", id, e.getMessage());
            throw new RuntimeException("Kundtjänsten är inte tillgänglig");
        }
    }

    /**
     * Hämta alla kunder från Kundtjänst
     */
    public CustomerDto[] getAllCustomers() {
        try {
            CustomerDto[] customers = restClient.get()
                    .uri(customerServiceUrl + "/api/customers")
                    .retrieve()
                    .body(CustomerDto[].class);
            log.info("Hämtade {} kunder från Kundtjänst", customers.length);
            return customers;

        } catch (Exception e) {
            log.error("Kundtjänsten är inte tillgänglig vid hämtning av alla kunder: {}", e.getMessage());
            throw new RuntimeException("Kundtjänsten är inte tillgänglig");
        }
    }
}