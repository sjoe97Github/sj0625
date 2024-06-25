package com.example.demo.store.rentals.exceptions;

public class RentalRequestException extends  Exception {
    public RentalRequestException(String message) {
        super(message);
    }

    public RentalRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public RentalRequestException(Throwable cause) {
        super(cause);
    }
}
