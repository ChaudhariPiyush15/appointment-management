package com.example.appointment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO used when a client books a new appointment (POST /api/appointments).
 * Bean Validation annotations here are checked automatically when the
 * controller method parameter is annotated with @Valid.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequest {

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotBlank(message = "Doctor name is required")
    private String doctorName;

    @NotNull(message = "Appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time is required")
    private LocalTime appointmentTime;

    private String reason;
}
