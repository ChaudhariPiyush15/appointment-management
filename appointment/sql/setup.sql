-- =========================================================
-- Appointment Management System - Database Setup
-- =========================================================
-- Run this once in MySQL (Workbench, CLI, or DBeaver) before
-- starting the Spring Boot application.
-- The "appointments" table itself does NOT need to be created
-- manually - Hibernate will create/update it automatically
-- because spring.jpa.hibernate.ddl-auto=update.
-- =========================================================

CREATE DATABASE IF NOT EXISTS appointment_db;

USE appointment_db;

-- (Optional) You can inspect the auto-generated table after running
-- the application at least once, with:
-- DESCRIBE appointments;
-- SELECT * FROM appointments;
