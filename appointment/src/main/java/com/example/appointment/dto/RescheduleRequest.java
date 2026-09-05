package com.example.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO used when a client reschedules an existing appointment
 * (PUT /api/appointments/{id}/reschedule).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleRequest {

    @NotNull(message = "New appointment date is required")
    private LocalDate appointmentDate;

    @NotNull(message = "New appointment time is required")
    private LocalTime appointmentTime;
}
