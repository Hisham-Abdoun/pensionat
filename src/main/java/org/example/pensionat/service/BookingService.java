package org.example.pensionat.service;

import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Customer;
import org.example.pensionat.model.Room;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.CustomerRepository;
import org.example.pensionat.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final RoomRepository roomRepository;

    public BookingService(BookingRepository bookingRepository,
                          CustomerRepository customerRepository,
                          RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.customerRepository = customerRepository;
        this.roomRepository = roomRepository;
    }

    // تحويل Entity → DTO
    private BookingDto toDto(Booking booking) {
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setCustomerId(booking.getCustomer().getId());
        dto.setCustomerName(booking.getCustomer().getFirstName()
                + " " + booking.getCustomer().getLastName());
        dto.setRoomId(booking.getRoom().getId());
        dto.setRoomNumber(booking.getRoom().getRoomNumber());
        return dto;
    }

    // جلب كل الحجوزات
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // جلب حجز بالـ ID
    public BookingDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));
        return toDto(booking);
    }

    // إنشاء حجز جديد
    public boolean createBooking(BookingDto dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Rum hittades inte"));

        // تحقق لا يوجد حجز متعارض
        List<Booking> conflicts = bookingRepository
                .findConflictingBookings(room, dto.getStartDate(), dto.getEndDate());
        if (!conflicts.isEmpty()) {
            return false; // الغرفة محجوزة
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Kund hittades inte"));

        Booking booking = new Booking();
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());
        booking.setCustomer(customer);
        booking.setRoom(room);

        bookingRepository.save(booking);
        return true; // تم الحجز
    }

    // تعديل حجز
    public boolean updateBooking(Long id, BookingDto dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bokning hittades inte"));

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Kund hittades inte"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Rum hittades inte"));

        // Krockkontroll: exkludera denna bokning (samma id), inte samma Java-instans
        List<Booking> conflicts = bookingRepository.findConflictingBookingsExcluding(
                room, dto.getStartDate(), dto.getEndDate(), booking.getId());
        if (!conflicts.isEmpty()) {
            return false;
        }

        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setNumberOfGuests(dto.getNumberOfGuests());
        booking.setCustomer(customer);
        booking.setRoom(room);

        bookingRepository.save(booking);
        return true;
    }

    // إلغاء حجز
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
