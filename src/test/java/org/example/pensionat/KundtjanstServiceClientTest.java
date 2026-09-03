package org.example.pensionat;

import org.example.pensionat.client.KundtjanstServiceClient;
import org.example.pensionat.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class KundtjanstServiceClientTest {

    @Autowired
    private KundtjanstServiceClient kundtjanstServiceClient;

    @Test
    void customerExists_returnsTrueWhenCustomerExists() {
        // Förutsätter att kundtjänsten körs på localhost:8081 och har en kund med id 1
        boolean result = kundtjanstServiceClient.customerExists(1L);
        assertTrue(result);
    }

    @Test
    void customerExists_returnsFalseWhenCustomerNotFound() {
        // Förutsätter att kundtjänsten körs på localhost:8081 och inte har en kund med detta id
        boolean result = kundtjanstServiceClient.customerExists(99999L);
        assertFalse(result);
    }

    @Test
    void getCustomerById_returnsCustomerDto() {
        // Förutsätter att kundtjänsten körs på localhost:8081 och har en kund med id 1
        CustomerDto customer = kundtjanstServiceClient.getCustomerById(1L);
        assertNotNull(customer);
        assertNotNull(customer.getFirstName());
        assertNotNull(customer.getLastName());
    }
}
