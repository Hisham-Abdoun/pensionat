package org.example.pensionat;

import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.Room;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.RoomRepository;
import org.example.pensionat.service.RoomService;
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
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private RoomDto roomDto;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(1L);
        room.setRoomNumber(100);
        room.setRoomType(RoomType.SINGLE);
        room.setExtraBeds(0);
        room.setPricePerNight(500.0);
        room.setBookings(new ArrayList<>());

        roomDto = new RoomDto();
        roomDto.setRoomNumber(100);
        roomDto.setRoomType(RoomType.SINGLE);
        roomDto.setExtraBeds(0);
        roomDto.setPricePerNight(500.0);
    }

    // ✅ Test 1: جلب كل الغرف
    @Test
    void getAllRooms_ShouldReturnList() {
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomDto> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getRoomNumber());
    }

    // ✅ Test 2: جلب غرفة بالـ ID
    @Test
    void getRoomById_ShouldReturnRoom() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomDto result = roomService.getRoomById(1L);

        assertEquals(100, result.getRoomNumber());
        assertEquals(RoomType.SINGLE, result.getRoomType());
    }

    // ✅ Test 3: حفظ غرفة جديدة
    @Test
    void saveRoom_ShouldSaveSuccessfully() {
        roomService.saveRoom(roomDto);

        verify(roomRepository, times(1)).save(any(Room.class));
    }

    // ✅ Test 4: بحث عن غرف متاحة
    @Test
    void getAvailableRooms_ShouldReturnAvailableRooms() {
        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(bookingRepository.findConflictingBookings(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        List<RoomDto> result = roomService.getAvailableRooms(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                1
        );

        assertEquals(1, result.size());
    }

    // ✅ Test 5: غرفة DOUBLE مع أسرّة إضافية
    @Test
    void getAvailableRooms_DoubleWithExtraBeds_ShouldAcceptMoreGuests() {
        room.setRoomType(RoomType.DOUBLE);
        room.setExtraBeds(2);

        when(roomRepository.findAll()).thenReturn(List.of(room));
        when(bookingRepository.findConflictingBookings(any(), any(), any()))
                .thenReturn(new ArrayList<>());

        List<RoomDto> result = roomService.getAvailableRooms(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 5),
                4
        );

        assertEquals(1, result.size());
    }
}
