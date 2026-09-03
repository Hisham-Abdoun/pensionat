package org.example.pensionat.controller;

import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.service.BookingService;
import org.example.pensionat.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller för webbhantering.
 * Här finns API-endpoint:er för att hantera bokningar, kunder och rum.
 */
@Controller
public class WebController {

    private final BookingService bookingService;
    private final RoomService roomService;

    public WebController(BookingService bookingService,
                         RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping("/bookings")
    public String bookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("bookingDto", new BookingDto());
        return "bookings/list";
    }

    @GetMapping("/bookings/edit/{id}")
    public String editBooking(@PathVariable Long id, Model model) {
        model.addAttribute("booking", bookingService.getBookingById(id));
        model.addAttribute("rooms", roomService.getAllRooms());
        return "bookings/form";
    }

    @PostMapping("/bookings/save")
    public String saveBooking(@ModelAttribute BookingDto bookingDto,
                              RedirectAttributes redirectAttributes) {
        boolean created = bookingService.createBooking(bookingDto);
        if (created) {
            redirectAttributes.addFlashAttribute("success", "Bokning skapad!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Rummet är redan bokat!");
        }
        return "redirect:/bookings";
    }

    @PostMapping("/bookings/delete/{id}")
    public String deleteBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.deleteBooking(id);
        redirectAttributes.addFlashAttribute("success", "Bokning avbokad!");
        return "redirect:/bookings";
    }

    @GetMapping("/bookings/search")
    public String searchBookings() {
        return "bookings/search";
    }

    /*
    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customerDto", new CustomerDto());
        return "customers/list";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        return "customers/form";
    }

    @PostMapping("/customers/save")
    public String saveCustomer(@ModelAttribute CustomerDto customerDto,
                               RedirectAttributes redirectAttributes) {
        customerService.saveCustomer(customerDto);
        redirectAttributes.addFlashAttribute("success", "Kunden sparades!");
        return "redirect:/customers";
    }
    */

    /*
    @PostMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Kunden togs bort!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Kan inte ta bort kund med bokningar!");
        }
        return "redirect:/customers";
    }
    */

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("roomDto", new RoomDto());
        model.addAttribute("roomTypes", List.of("SINGLE", "DOUBLE", "SUITE"));
        return "rooms/list";
    }

    @PostMapping("/rooms/save")
    public String saveRoom(@ModelAttribute RoomDto roomDto,
                           RedirectAttributes redirectAttributes) {
        roomService.saveRoom(roomDto);
        redirectAttributes.addFlashAttribute("success", "Rummet sparades!");
        return "redirect:/rooms";
    }
}
