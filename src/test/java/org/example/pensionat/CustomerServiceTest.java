package org.example.pensionat;

import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.model.Customer;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.CustomerRepository;
import org.example.pensionat.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Ahmed");
        customer.setLastName("Ali");
        customer.setEmail("ahmed@gmail.com");
        customer.setPhoneNumber("0701234567");
        customer.setBookings(new ArrayList<>());

        customerDto = new CustomerDto();
        customerDto.setFirstName("Ahmed");
        customerDto.setLastName("Ali");
        customerDto.setEmail("ahmed@gmail.com");
        customerDto.setPhoneNumber("0701234567");
    }

    // ✅ Test 1: جلب كل العملاء
    @Test
    void getAllCustomers_ShouldReturnList() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDto> result = customerService.getAllCustomers();

        assertEquals(1, result.size());
        assertEquals("Ahmed", result.get(0).getFirstName());
    }

    // ✅ Test 2: جلب عميل بالـ ID
    @Test
    void getCustomerById_ShouldReturnCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerDto result = customerService.getCustomerById(1L);

        assertEquals("Ahmed", result.getFirstName());
        assertEquals("Ali", result.getLastName());
    }

    // ✅ Test 3: حفظ عميل جديد
    @Test
    void saveCustomer_ShouldSaveSuccessfully() {
        customerService.saveCustomer(customerDto);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    // ✅ Test 4: تعديل عميل
    @Test
    void updateCustomer_ShouldUpdateSuccessfully() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerDto.setFirstName("Mohammed");
        customerService.updateCustomer(1L, customerDto);

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    // ✅ Test 5: حذف عميل بدون حجوزات
    @Test
    void deleteCustomer_WithNoBookings_ShouldReturnTrue() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.deleteCustomer(1L);

        assertTrue(result);
        verify(customerRepository, times(1)).delete(customer);
    }

    // ✅ Test 6: حذف عميل مع حجوزات - يجب أن يفشل
    @Test
    void deleteCustomer_WithBookings_ShouldReturnFalse() {
        customer.setBookings(List.of(new org.example.pensionat.model.Booking()));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        boolean result = customerService.deleteCustomer(1L);

        assertFalse(result);
        verify(customerRepository, never()).delete(customer);
    }
}