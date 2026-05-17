package org.example.pensionat.controller;

import jakarta.validation.Valid;
import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // عرض كل العملاء
    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customerDto", new CustomerDto());
        return "customers/list";
    }

    // حفظ عميل جديد
    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute CustomerDto customerDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("customers", customerService.getAllCustomers());
            return "customers/list";
        }
        customerService.saveCustomer(customerDto);
        redirectAttributes.addFlashAttribute("success",
                "Kunden sparades! / تم حفظ العميل!");
        return "redirect:/customers";
    }

    // عرض فورم التعديل
    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("customerDto", customerService.getCustomerById(id));
        return "customers/form";
    }

    // تعديل عميل
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable Long id,
                                 @Valid @ModelAttribute CustomerDto customerDto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "customers/form";
        }
        customerService.updateCustomer(id, customerDto);
        redirectAttributes.addFlashAttribute("success",
                "Kunden uppdaterades! / تم تعديل العميل!");
        return "redirect:/customers";
    }

    // حذف عميل
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success",
                    "Kunden togs bort! / تم حذف العميل!");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "Kan inte ta bort kund med bokningar! / لا يمكن حذف عميل لديه حجوزات!");
        }
        return "redirect:/customers";
    }
}
