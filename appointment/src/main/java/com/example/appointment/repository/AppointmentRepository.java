package com.example.appointment.repository;

import com.example.appointment.entity.Appointment;
import com.example.appointment.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository layer - talks directly to the database via Spring Data JPA.
 * Spring automatically generates the implementation of these methods
 * based on their names (derived queries) or the supplied @Query.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Used to detect double-booking: finds an appointment for the same
     * doctor at the same date & time, excluding a given status (CANCELLED),
     * so cancelled slots don't block new bookings.
     */
    Optional<Appointment> findByDoctorNameAndAppointmentDateAndAppointmentTimeAndStatusNot(
            String doctorName,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus statusNot
    );

    /**
     * Flexible search/filter query. Any of patientName, doctorName, or status
     * can be null - in which case that filter is simply skipped
     * (handled via the COALESCE / IS NULL OR pattern below).
     */
    @Query("SELECT a FROM Appointment a WHERE " +
            "(:patientName IS NULL OR LOWER(a.patientName) LIKE LOWER(CONCAT('%', :patientName, '%'))) AND " +
            "(:doctorName IS NULL OR LOWER(a.doctorName) LIKE LOWER(CONCAT('%', :doctorName, '%'))) AND " +
            "(:status IS NULL OR a.status = :status)")
    List<Appointment> search(
            @Param("patientName") String patientName,
            @Param("doctorName") String doctorName,
            @Param("status") AppointmentStatus status
    );
}
