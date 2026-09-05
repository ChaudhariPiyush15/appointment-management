package com.example.appointment.dto;

import com.example.appointment.entity.Appointment;
import com.example.appointment.entity.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO returned to clients for all "read" style responses.
 * Keeping a separate response DTO (instead of returning the Entity directly)
 * means we control exactly what JSON shape is exposed to the outside world.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;
    private String patientName;
    private String doctorName;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convenience mapper: converts an Appointment entity into an
     * AppointmentResponse DTO. Keeping this here avoids duplicating
     * mapping logic across the service layer.
     */
    public static AppointmentResponse fromEntity(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientName(appointment.getPatientName())
                .doctorName(appointment.getDoctorName())
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
