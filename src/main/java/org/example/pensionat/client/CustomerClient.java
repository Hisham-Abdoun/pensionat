package org.example.pensionat.client;

import org.example.pensionat.exception.CustomerNotFoundException;
import org.example.pensionat.exception.KundtjanstUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

// Ansvarar för all kommunikation med kundtjänsten via REST.
@Component
public class CustomerClient {

    private final RestClient restClient;

    public CustomerClient(@Value("${kundtjanst.api.url}") String kundtjanstUrl) {
        this.restClient = RestClient.create(kundtjanstUrl);
    }

    // Kontrollerar att en kund finns i kundtjänsten.
    // Kastar CustomerNotFoundException om kunden inte finns (404).
    // Kastar KundtjanstUnavailableException om kundtjänsten inte svarar.
    public void verifyCustomerExists(Long customerId) {
        try {
            restClient.get()
                    .uri("/{id}", customerId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new CustomerNotFoundException(customerId);
        } catch (ResourceAccessException ex) {
            throw new KundtjanstUnavailableException(
                    "Kunde inte nå kundtjänsten just nu. Försök igen senare.");
        }
    }
}