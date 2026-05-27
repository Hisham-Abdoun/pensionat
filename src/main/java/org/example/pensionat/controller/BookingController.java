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
import java.util.stream.Collectors;

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

    // عرض كل الحجوزات
    @GetMapping
    public String listBookings(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("bookingDto", new BookingDto());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "bookings/list";
    }

    // إنشاء حجز جديد
    @PostMapping("/save")
    public String saveBooking(@Valid @ModelAttribute BookingDto bookingDto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("bookings", bookingService.getAllBookings());
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("rooms", roomService.getAllRooms());
            String errorMessage = result.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .filter(m -> m != null && !m.isBlank())
                    .distinct()
                    .collect(Collectors.joining("\n"));
            model.addAttribute("error", errorMessage);
            return "bookings/list";
        }

        boolean created = bookingService.createBooking(bookingDto);
        if (created) {
            redirectAttributes.addFlashAttribute("success",
                    "Bokning skapad! / تم إنشاء الحجز!");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Rummet är redan bokat! / الغرفة محجوزة في هذا التاريخ!");
        }
        return "redirect:/bookings";
    }

    // عرض فورم التعديل
    @GetMapping("/edit/{id}")
    public String editBooking(@PathVariable Long id, Model model) {
        model.addAttribute("bookingDto", bookingService.getBookingById(id));
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "bookings/form";
    }

    // تعديل حجز
    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable Long id,
                                @Valid @ModelAttribute BookingDto bookingDto,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            model.addAttribute("rooms", roomService.getAllRooms());
            String errorMessage = result.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .filter(m -> m != null && !m.isBlank())
                    .distinct()
                    .collect(Collectors.joining("\n"));
            model.addAttribute("error", errorMessage);
            return "bookings/form";
        }
        boolean updated = bookingService.updateBooking(id, bookingDto);
        if (updated) {
            redirectAttributes.addFlashAttribute("success",
                    "Bokning uppdaterad! / تم تعديل الحجز!");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Rummet är redan bokat! / الغرفة محجوزة في هذا التاريخ!");
        }
        return "redirect:/bookings";
    }

    // إلغاء حجز
    @GetMapping("/delete/{id}")
    public String deleteBooking(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        bookingService.deleteBooking(id);
        redirectAttributes.addFlashAttribute("success",
                "Bokning avbokad! / تم إلغاء الحجز!");
        return "redirect:/bookings";
    }

    // بحث عن غرف متاحة ⭐ VG
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
