package com.example.appointment.exception;

/**
 * Thrown when trying to cancel or reschedule an appointment that
 * has already been cancelled.
 * Handled by GlobalExceptionHandler -> returns HTTP 400 BAD REQUEST.
 */
public class AppointmentAlreadyCancelledException extends RuntimeException {

    public AppointmentAlreadyCancelledException(Long id) {
        super("Appointment with id " + id + " is already cancelled");
    }

    public AppointmentAlreadyCancelledException(String message) {
        super(message);
    }
}
