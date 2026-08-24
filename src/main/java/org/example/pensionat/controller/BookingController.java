package org.example.pensionat.controller;

import jakarta.validation.Valid;
import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.service.BookingService;
import org.example.pensionat.service.CustomerService;
import org.example.pensionat.service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final CustomerService customerService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService,
                             CustomerService customerService,
                             RoomService roomService) {
        this.bookingService = bookingService;
        this.customerService = customerService;
        this.roomService = roomService;
    }

    // Visa alla bokningar
    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("bookingDto", new BookingDto());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "bookings/list";
    }

    // Skapa ny bokning
    @PostMapping("/save")
    public String saveBooking(@Valid @ModelAttribute BookingDto bookingDto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("bookings", bookingService.getAllBookings());
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("rooms", roomService.getAllRooms());
            return "bookings/list";
        }
        boolean created = bookingService.createBooking(bookingDto);
        if (created) {
            redirectAttributes.addFlashAttribute("success",
                    "Bokning skapad!");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Rummet är redan bokat!");
        }
        return "redirect:/bookings";
    }

    // Visa redigeringsformulär
    @GetMapping("/edit/{id}")
    public String editBooking(@PathVariable Long id, Model model) {
        model.addAttribute("bookingDto", bookingService.getBookingById(id));
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "bookings/form";
    }

    // Redigera bokning
    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable Long id,
                                @Valid @ModelAttribute BookingDto bookingDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("rooms", roomService.getAllRooms());
            return "bookings/form";
        }
        boolean updated = bookingService.updateBooking(id, bookingDto);
        if (updated) {
            redirectAttributes.addFlashAttribute("success",
                    "Bokning uppdaterad!");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Rummet är redan bokat!");
        }
        return "redirect:/bookings";
    }

    // Avboka bokning
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        bookingService.deleteBooking(id);
        redirectAttributes.addFlashAttribute("success",
                "Bokning avbokad!");
        return "redirect:/bookings";
    }

    // Sök tillgängliga rum ⭐ VG
    @GetMapping("/search")
    public String searchRooms(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer numberOfGuests,
            Model model) {
        if (startDate != null && endDate != null && numberOfGuests != null) {
            model.addAttribute("availableRooms",
                    roomService.getAvailableRooms(startDate, endDate, numberOfGuests));
        }
        return "bookings/search";
    }
}