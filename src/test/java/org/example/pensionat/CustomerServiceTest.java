package org.example.pensionat;

import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    void getAllCustomers_returnsList() {
        List<CustomerDto> result = customerService.getAllCustomers();
        assertNotNull(result);
    }

    @Test
    void saveCustomer_works() {
        CustomerDto dto = new CustomerDto();
        dto.setFirstName("Test");
        dto.setLastName("Person");
        dto.setEmail("test@test.com");
        dto.setPhoneNumber("0701234567");

        customerService.saveCustomer(dto);

        List<CustomerDto> result = customerService.getAllCustomers();
        assertFalse(result.isEmpty());
    }
}