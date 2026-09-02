package org.example.pensionat;

import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.service.BookingService;
import org.example.pensionat.service.CustomerService;
import org.example.pensionat.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoomService roomService;

    @Test
    void getAllBookings_returnsList() {
        List<BookingDto> result = bookingService.getAllBookings();
        assertNotNull(result);
    }

    @Test
    void createBooking_works() {
        CustomerDto customer = new CustomerDto();
        customer.setFirstName("Test");
        customer.setLastName("Person");
        customer.setEmail("booking@test.com");
        customer.setPhoneNumber("0701234567");
        customerService.saveCustomer(customer);

        RoomDto room = new RoomDto();
        room.setRoomNumber(200);
        room.setRoomType(RoomType.ENKEL);
        room.setExtraBeds(0);
        room.setPricePerNight(500.0);
        roomService.saveRoom(room);

        List<CustomerDto> customers = customerService.getAllCustomers();
        List<RoomDto> rooms = roomService.getAllRooms();

        BookingDto dto = new BookingDto();
        dto.setStartDate(LocalDate.of(2026, 7, 1));
        dto.setEndDate(LocalDate.of(2026, 7, 5));
        dto.setNumberOfGuests(1);
        dto.setCustomerId(customers.get(0).getId());
        dto.setRoomId(rooms.get(0).getId());

        boolean result = bookingService.createBooking(dto);
        assertTrue(result);
    }
}