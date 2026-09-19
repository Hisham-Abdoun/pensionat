package org.example.pensionat;

import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoomServiceTest {

    @Autowired
    private RoomService roomService;


    @Test
    void getAllRooms_returnsList() {
        List<RoomDto> result = roomService.getAllRooms();
        assertNotNull(result);
    }



    @Test
    void saveRoom_works() {
        RoomDto dto = new RoomDto();
        dto.setRoomNumber(301);
        dto.setRoomType(RoomType.ENKEL);
        dto.setExtraBeds(1);
        dto.setPricePerNight(900.0);

        roomService.saveRoom(dto);

        List<RoomDto> result = roomService.getAllRooms();
        assertFalse(result.isEmpty());
    }
}