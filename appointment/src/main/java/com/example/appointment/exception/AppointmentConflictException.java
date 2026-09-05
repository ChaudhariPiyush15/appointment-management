package com.example.appointment.exception;

/**
 * Thrown when a doctor already has an active (non-cancelled) appointment
 * at the requested date and time.
 * Handled by GlobalExceptionHandler -> returns HTTP 409 CONFLICT.
 */
public class AppointmentConflictException extends RuntimeException {

    public AppointmentConflictException(String message) {
        super(message);
    }
}
