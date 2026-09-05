package com.example.appointment.service;

import com.example.appointment.dto.AppointmentRequest;
import com.example.appointment.dto.AppointmentResponse;
import com.example.appointment.dto.RescheduleRequest;
import com.example.appointment.entity.AppointmentStatus;

import java.util.List;

/**
 * Service layer contract - defines all business operations available
 * for appointments. The Controller only talks to this interface,
 * never directly to the Repository.
 */
public interface AppointmentService {

    AppointmentResponse bookAppointment(AppointmentRequest request);

    List<AppointmentResponse> getAllAppointments();

    AppointmentResponse getAppointmentById(Long id);

    AppointmentResponse rescheduleAppointment(Long id, RescheduleRequest request);

    AppointmentResponse cancelAppointment(Long id);

    List<AppointmentResponse> searchAppointments(String patientName, String doctorName, AppointmentStatus status);
}
