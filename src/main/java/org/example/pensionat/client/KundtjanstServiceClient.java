package org.example.pensionat.client;

import org.example.pensionat.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class KundtjanstServiceClient {

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

            return true; // 200 OK → kunden finns

        } catch (HttpClientErrorException.NotFound e) {
            return false; // 404 → kunden finns inte

        } catch (Exception e) {
            throw new RuntimeException("Kundtjänsten är inte tillgänglig");
        }
    }

    /**
     * Hämta kundens detaljer (valfritt, används om du vill visa kundnamn i bokningslistan)
     */
    public CustomerDto getCustomerById(Long id) {
        try {
            return restClient.get()
                    .uri(customerServiceUrl + "/api/customers/" + id)
                    .retrieve()
                    .body(CustomerDto.class);

        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Kunden finns inte");

        } catch (Exception e) {
            throw new RuntimeException("Kundtjänsten är inte tillgänglig");
        }
    }
}