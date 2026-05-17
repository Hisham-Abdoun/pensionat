package org.example.pensionat;

import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Customer;
import org.example.pensionat.model.Room;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.CustomerRepository;
import org.example.pensionat.repository.RoomRepository;
import org.example.pensionat.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking booking;
    private BookingDto bookingDto;
    private Customer customer;
    private Room room;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setFirstName("Ahmed");
        customer.setLastName("Ali");
        customer.setEmail("ahmed@gmail.com");
        customer.setPhoneNumber("0701234567");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber(100);
        room.setRoomType(RoomType.SINGLE);
        room.setExtraBeds(0);
        room.setPricePerNight(500.0);

        booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDate.of(2026, 6, 1));
        booking.setEndDate(LocalDate.of(2026, 6, 5));
        booking.setNumberOfGuests(1);
        booking.setCustomer(customer);
        booking.setRoom(room);

        bookingDto = new BookingDto();
        bookingDto.setStartDate(LocalDate.of(2026, 6, 1));
        bookingDto.setEndDate(LocalDate.of(2026, 6, 5));
        bookingDto.setNumberOfGuests(1);
        bookingDto.setCustomerId(1L);
        bookingDto.setRoomId(1L);
    }

    // ✅ Test 1: جلب كل الحجوزات
    @Test
    void getAllBookings_ShouldReturnList() {
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.getAllBookings();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getNumberOfGuests());
    }

    // ✅ Test 2: جلب حجز بالـ ID
    @Test
    void getBookingById_ShouldReturnBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(1L);

        assertEquals(1, result.getNumberOfGuests());
        assertEquals(1L, result.getCustomerId());
    }

    // ✅ Test 3: إنشاء حجز جديد بنجاح
    @Test
    void createBooking_ShouldReturnTrue_WhenNoConflict() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bookingRepository.findConflictingBookings(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        boolean result = bookingService.createBooking(bookingDto);

        assertTrue(result);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    // ✅ Test 4: إنشاء حجز فاشل - الغرفة محجوزة
    @Test
    void createBooking_ShouldReturnFalse_WhenConflict() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findConflictingBookings(any(), any(), any()))
                .thenReturn(List.of(booking));

        boolean result = bookingService.createBooking(bookingDto);

        assertFalse(result);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ✅ Test 5: إلغاء حجز
    @Test
    void deleteBooking_ShouldDeleteSuccessfully() {
        bookingService.deleteBooking(1L);

        verify(bookingRepository, times(1)).deleteById(1L);
    }
}
