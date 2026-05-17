package org.example.pensionat.controller;

import jakarta.validation.Valid;
import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // عرض كل الغرف
    @GetMapping
    public String listRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("roomDto", new RoomDto());
        model.addAttribute("roomTypes", RoomType.values());
        return "rooms/list";
    }

    // حفظ غرفة جديدة
    @PostMapping("/save")
    public String saveRoom(@Valid @ModelAttribute RoomDto roomDto,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("roomTypes", RoomType.values());
            return "rooms/list";
        }
        roomService.saveRoom(roomDto);
        redirectAttributes.addFlashAttribute("success",
                "Rummet sparades! / تم حفظ الغرفة!");
        return "redirect:/rooms";
    }
}
