package com.example.appointment.controller;

import com.example.appointment.dto.AppointmentRequest;
import com.example.appointment.dto.AppointmentResponse;
import com.example.appointment.dto.RescheduleRequest;
import com.example.appointment.entity.AppointmentStatus;
import com.example.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for all appointment-related endpoints.
 * This layer is intentionally "thin" - it only handles HTTP concerns
 * (request/response mapping, status codes) and delegates all business
 * logic to the AppointmentService.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * 1. Book a new appointment.
     * POST /api/appointments
     */
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. Get all appointments.
     * GET /api/appointments
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    /**
     * 3. Get a single appointment by id.
     * GET /api/appointments/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    /**
     * 4. Reschedule an existing appointment.
     * PUT /api/appointments/{id}/reschedule
     */
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest request) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, request));
    }

    /**
     * 5. Cancel an appointment (soft delete - status set to CANCELLED).
     * DELETE /api/appointments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    /**
     * 6. Search / filter appointments by patientName, doctorName and/or status.
     * GET /api/appointments/search?patientName=Piyush&doctorName=Sharma&status=BOOKED
     * All parameters are optional and can be combined.
     */
    @GetMapping("/search")
    public ResponseEntity<List<AppointmentResponse>> searchAppointments(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.searchAppointments(patientName, doctorName, status));
    }
}
