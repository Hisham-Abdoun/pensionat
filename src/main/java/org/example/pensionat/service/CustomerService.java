package org.example.pensionat.service;

import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.model.Customer;
import org.example.pensionat.repository.CustomerRepository;
import org.example.pensionat.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;

    public CustomerService(CustomerRepository customerRepository,
                           BookingRepository bookingRepository) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
    }

    // تحويل Entity → DTO
    private CustomerDto toDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhoneNumber(customer.getPhoneNumber());
        return dto;
    }

    // تحويل DTO → Entity
    private Customer toEntity(CustomerDto dto) {
        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        return customer;
    }

    // جلب كل العملاء
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // جلب عميل بالـ ID
    public CustomerDto getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kund hittades inte"));
        return toDto(customer);
    }

    // حفظ عميل جديد
    public void saveCustomer(CustomerDto dto) {
        customerRepository.save(toEntity(dto));
    }

    // تعديل عميل
    public void updateCustomer(Long id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kund hittades inte"));
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhoneNumber(dto.getPhoneNumber());
        customerRepository.save(customer);
    }

    // حذف عميل (فقط إذا لا يوجد حجوزات)
    public boolean deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kund hittades inte"));
        if (!customer.getBookings().isEmpty()) {
            return false; // لا يمكن الحذف
        }
        customerRepository.delete(customer);
        return true; // تم الحذف
    }
}
