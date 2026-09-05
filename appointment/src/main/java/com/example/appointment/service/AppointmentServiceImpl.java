package com.example.appointment.service;

import com.example.appointment.dto.AppointmentRequest;
import com.example.appointment.dto.AppointmentResponse;
import com.example.appointment.dto.RescheduleRequest;
import com.example.appointment.entity.Appointment;
import com.example.appointment.entity.AppointmentStatus;
import com.example.appointment.exception.AppointmentAlreadyCancelledException;
import com.example.appointment.exception.AppointmentConflictException;
import com.example.appointment.exception.AppointmentNotFoundException;
import com.example.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer implementation - contains all the business logic:
 * conflict checking, status transitions, and validation rules that
 * go beyond simple Bean Validation (e.g. "doctor already booked").
 */
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        // Business rule: doctor cannot have two active appointments at the same date/time
        checkForConflict(request.getDoctorName(), request.getAppointmentDate(), request.getAppointmentTime());

        Appointment appointment = Appointment.builder()
                .patientName(request.getPatientName())
                .doctorName(request.getDoctorName())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(saved);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    @Override
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);
        return AppointmentResponse.fromEntity(appointment);
    }

    @Override
    public AppointmentResponse rescheduleAppointment(Long id, RescheduleRequest request) {
        Appointment appointment = findAppointmentOrThrow(id);

        // Rule: cannot reschedule a cancelled appointment
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppointmentAlreadyCancelledException(
                    "Cannot reschedule appointment with id " + id + " because it is already cancelled");
        }

        // Rule: new date/time must not conflict with another active appointment
        // for the same doctor (excluding this appointment itself)
        appointmentRepository.findByDoctorNameAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        appointment.getDoctorName(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime(),
                        AppointmentStatus.CANCELLED)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new AppointmentConflictException(
                            "Doctor is already booked at this date and time");
                });

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);

        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(updated);
    }

    @Override
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = findAppointmentOrThrow(id);

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppointmentAlreadyCancelledException(id);
        }

        // Soft cancellation - update the status, do NOT delete the row
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment updated = appointmentRepository.save(appointment);
        return AppointmentResponse.fromEntity(updated);
    }

    @Override
    public List<AppointmentResponse> searchAppointments(String patientName, String doctorName, AppointmentStatus status) {
        return appointmentRepository.search(patientName, doctorName, status)
                .stream()
                .map(AppointmentResponse::fromEntity)
                .toList();
    }

    /**
     * Shared helper: fetches an appointment by id or throws 404.
     */
    private Appointment findAppointmentOrThrow(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    /**
     * Shared helper: throws AppointmentConflictException if the doctor
     * already has a non-cancelled appointment at the given date/time.
     */
    private void checkForConflict(String doctorName, java.time.LocalDate date, java.time.LocalTime time) {
        appointmentRepository.findByDoctorNameAndAppointmentDateAndAppointmentTimeAndStatusNot(
                        doctorName, date, time, AppointmentStatus.CANCELLED)
                .ifPresent(existing -> {
                    throw new AppointmentConflictException("Doctor is already booked at this date and time");
                });
    }
}
