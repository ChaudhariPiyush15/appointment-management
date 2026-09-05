package com.example.appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Appointment Management System Spring Boot application.
 * Running this class starts an embedded Tomcat server on the port
 * configured in application.properties (default 8080).
 */
@SpringBootApplication
public class AppointmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentApplication.class, args);
    }

}
