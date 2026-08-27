package org.example.pensionat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.pensionat.dto.CustomerDto;
import org.example.pensionat.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "API för kundhantering")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @Operation(summary = "Hämta alla kunder")
    @ApiResponse(responseCode = "200", description = "Lista med alla kunder")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Hämta kund via ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kund hittad"),
            @ApiResponse(responseCode = "404", description = "Kund hittades inte")
    })
    public ResponseEntity<CustomerDto> getCustomerById(
            @Parameter(description = "Kund-ID") @PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PostMapping
    @Operation(summary = "Skapa ny kund")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Kund skapad"),
            @ApiResponse(responseCode = "400", description = "Valideringsfel")
    })
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerDto customerDto,
                                            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        customerService.saveCustomer(customerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Kunden sparades!");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Uppdatera kund")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kund uppdaterad"),
            @ApiResponse(responseCode = "400", description = "Valideringsfel"),
            @ApiResponse(responseCode = "404", description = "Kund hittades inte")
    })
    public ResponseEntity<?> updateCustomer(
            @Parameter(description = "Kund-ID") @PathVariable Long id,
            @Valid @RequestBody CustomerDto customerDto,
            BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        customerService.updateCustomer(id, customerDto);
        return ResponseEntity.ok("Kunden uppdaterades!");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Ta bort kund")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kund borttagen"),
            @ApiResponse(responseCode = "409", description = "Kan inte ta bort kund med bokningar")
    })
    public ResponseEntity<String> deleteCustomer(
            @Parameter(description = "Kund-ID") @PathVariable Long id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseEntity.ok("Kunden togs bort!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Kan inte ta bort kund med bokningar!");
        }
    }
}