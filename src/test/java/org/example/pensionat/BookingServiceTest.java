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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private KundtjanstServiceClient kundtjanstServiceClient;

    @Autowired
    private TestRestTemplate restTemplate;

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

        Long roomId = rooms.get(0).getId();

        BookingDto dto = new BookingDto();
        dto.setStartDate(LocalDate.of(2026, 10, 1));
        dto.setEndDate(LocalDate.of(2026, 10, 5));
        dto.setNumberOfGuests(1);
        dto.setCustomerId(customerId);
        dto.setRoomId(roomId);

        ResponseEntity<String> firstResponse = restTemplate.postForEntity("/api/bookings", dto, String.class);
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());
    }

    @Test
    void createDoublebooking_works() {
        Long customerId = 1L;

        boolean customerExists = kundtjanstServiceClient.customerExists(customerId);
        assertTrue(customerExists, "Kunden måste existera i Kundtjänst för att kunna boka");

        List<RoomDto> rooms = roomService.getAllRooms();
        assertFalse(rooms.isEmpty(), "Det måste finnas minst ett rum i databasen");

        Long roomId = rooms.get(0).getId();

        // Skapa den första boknnigen
        BookingDto dto = new BookingDto();
        dto.setStartDate(LocalDate.of(2026, 9, 10));
        dto.setEndDate(LocalDate.of(2026, 9, 15));
        dto.setNumberOfGuests(1);
        dto.setCustomerId(customerId);
        dto.setRoomId(roomId);

        ResponseEntity<String> firstResponse = restTemplate.postForEntity("/api/bookings", dto, String.class);
        assertEquals(HttpStatus.CREATED, firstResponse.getStatusCode());

        // Skapa en dubbelbokning
        BookingDto dto2 = new BookingDto();
        dto2.setStartDate(LocalDate.of(2026, 9, 10));
        dto2.setEndDate(LocalDate.of(2026, 9, 15));
        dto2.setNumberOfGuests(1);
        dto2.setCustomerId(customerId);
        dto2.setRoomId(roomId);

        ResponseEntity<String> secondResponse = restTemplate.postForEntity("/api/bookings", dto2, String.class);
        assertEquals(HttpStatus.CONFLICT, secondResponse.getStatusCode());
    }
}