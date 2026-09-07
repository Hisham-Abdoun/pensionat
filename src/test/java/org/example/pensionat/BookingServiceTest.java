package org.example.pensionat;

import org.example.pensionat.client.KundtjanstServiceClient;
import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.service.BookingService;
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
    private RoomService roomService;

    @Autowired
    private KundtjanstServiceClient kundtjanstServiceClient;

    @Test
    void getAllBookings_returnsList() {
        List<BookingDto> result = bookingService.getAllBookings();
        assertNotNull(result);
    }

    @Test
    void createBooking_works() {
        Long customerId = 1L;

        boolean customerExists = kundtjanstServiceClient.customerExists(customerId);
        assertTrue(customerExists, "Kunden måste existera i Kundtjänst för att kunna boka");

        List<RoomDto> rooms = roomService.getAllRooms();
        assertFalse(rooms.isEmpty(), "Det måste finnas minst ett rum i databasen");

        BookingDto dto = new BookingDto();
        dto.setStartDate(LocalDate.of(2026, 9, 10));
        dto.setEndDate(LocalDate.of(2026, 9, 15));
        dto.setNumberOfGuests(1);
        dto.setCustomerId(customerId);
        dto.setRoomId(rooms.get(0).getId());

        boolean result = bookingService.createBooking(dto);
        assertTrue(result);
    }
}