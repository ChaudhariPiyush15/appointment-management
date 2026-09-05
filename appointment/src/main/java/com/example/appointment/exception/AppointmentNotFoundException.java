package com.example.appointment.exception;

/**
 * Thrown when an appointment with the given ID does not exist.
 * Handled by GlobalExceptionHandler -> returns HTTP 404 NOT FOUND.
 */
public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(Long id) {
        super("Appointment not found with id: " + id);
    }

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
