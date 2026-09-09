package org.example.pensionat.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(Long customerId) {
        super("Kunden med id " + customerId + " hittades inte i kundtjänsten");
    }
}