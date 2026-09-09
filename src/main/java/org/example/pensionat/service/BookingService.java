package org.example.pensionat.service;

import org.example.pensionat.client.CustomerClient;
import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Room;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final CustomerClient customerClient;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          CustomerClient customerClient) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.customerClient = customerClient;
    }

    // Konvertera Entity → DTO
    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setCustomerId(booking.getCustomerId());
        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomNumber(booking.getRoom().getRoomNumber());
        return dto;
    }

    // Hämta alla bokningar
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Hämta bokning via ID
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));
        return toDto(booking);
    }

    // Skapa ny bokning
    public boolean createBooking(BookingDto dto) {
        // Fråga kundtjänsten om kunden verkligen finns innan bokningen skapas.
        // Kastar CustomerNotFoundException eller KundtjanstUnavailableException vid problem.
        customerClient.verifyCustomerExists(dto.getCustomerId());

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Rum hittades inte"));

        // Kontrollera att det inte finns en motstridig bokning
        List<Booking> conflicts = bookingRepository
                .findConflictingBookings(room, dto.getStartDate(), dto.getEndDate());
        if (!conflicts.isEmpty()) {
            return false; // Rummet är bokat
        }

        Booking booking = new Booking();
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());
        booking.setCustomerId(dto.getCustomerId());
        booking.setRoom(room);

        bookingRepository.save(booking);
        return true; // Bokningen genomförd
    }

    // Redigera bokning
    public boolean updateBooking(Long id, BookingDto dto) {
        // Fråga kundtjänsten om kunden verkligen finns innan bokningen uppdateras.
        customerClient.verifyCustomerExists(dto.getCustomerId());

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Rum hittades inte"));

        // Kontrollera att det inte finns en konflikt (förutom den aktuella bokningen)
        List<Booking> conflicts = bookingRepository
                .findConflictingBookings(room, dto.getStartDate(), dto.getEndDate());
        conflicts.remove(booking);
        if (!conflicts.isEmpty()) {
            return false;
        }

        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());
        booking.setCustomerId(dto.getCustomerId());
        booking.setRoom(room);

        bookingRepository.save(booking);
        return true;
    }

    // Avboka bokning
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}