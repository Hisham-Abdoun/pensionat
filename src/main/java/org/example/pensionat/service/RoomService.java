package org.example.pensionat.service;

import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.Room;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository,
                       BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    // تحويل Entity → DTO
    private RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType());
        dto.setExtraBeds(room.getExtraBeds());
        dto.setPricePerNight(room.getPricePerNight());
        return dto;
    }

    // تحويل DTO → Entity
    private Room toEntity(RoomDto dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setExtraBeds(dto.getExtraBeds());
        room.setPricePerNight(dto.getPricePerNight());
        return room;
    }

    // جلب كل الغرف
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // جلب غرفة بالـ ID
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rum hittades inte"));
        return toDto(room);
    }

    // حفظ غرفة جديدة
    public void saveRoom(RoomDto dto) {
        roomRepository.save(toEntity(dto));
    }

    // البحث عن غرف متاحة ⭐ VG
    public List<RoomDto> getAvailableRooms(
            LocalDate startDate,
            LocalDate endDate,
            int numberOfGuests) {

        return roomRepository.findAll()
                .stream()
                .filter(room -> {
                    // تحقق عدد الضيوف
                    int maxGuests = getMaxGuests(room);
                    if (maxGuests < numberOfGuests) return false;

                    // تحقق لا يوجد حجوزات متعارضة
                    List conflicts = bookingRepository
                            .findConflictingBookings(room, startDate, endDate);
                    return conflicts.isEmpty();
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // حساب أقصى عدد ضيوف للغرفة
    private int getMaxGuests(Room room) {
        if (room.getRoomType() == RoomType.SINGLE) {
            return 1;
        }
        // DOUBLE: 2 + عدد الأسرّة الإضافية
        return 2 + room.getExtraBeds();
    }
}
