package org.example.pensionat.service;


import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Customer;
import org.example.pensionat.repository.CustomerRepository;
import org.example.pensionat.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public CustomerService(CustomerRepository customerRepository,
                           BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }


    private CustomerDto toDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        return dto;
    }


    private Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        return customer;
    }


    public List<CustomerDto> getAllCustomers() {
        List<CustomerDto> list = new ArrayList<>();

        for (Customer c : customerRepository.findAll()) {
            list.add(toDto(c));
        }

        return list;
    }


    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return toDto(customer);
    }


    public void saveCustomer(CustomerDto dto) {
        customerRepository.save(toEntity(dto));
    }


    public void updateCustomer(Long id, CustomerDto dto) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());

        customerRepository.save(customer);
    }


    public boolean deleteCustomer(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Booking> bookings = bookingRepository.findAll();

        for (Booking b : bookings) {
            if (b.getCustomer().getId().equals(id)) {
                return false;
            }
        }

        customerRepository.delete(customer);
        return true;

    }
}