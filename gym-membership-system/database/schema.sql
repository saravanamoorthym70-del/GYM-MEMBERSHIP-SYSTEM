-- Gym Membership System - Database Schema
-- This file is for reference only. Spring Boot JPA auto-creates tables.

CREATE DATABASE IF NOT EXISTS gym_membership_db;
USE gym_membership_db;

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS resources (
    id VARCHAR(10) PRIMARY KEY,
    reference VARCHAR(50),
    service_name VARCHAR(100) NOT NULL,
    capacity INT NOT NULL DEFAULT 0,
    available_capacity INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
);

CREATE TABLE IF NOT EXISTS service_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_id VARCHAR(20) UNIQUE NOT NULL,
    user_id VARCHAR(10) NOT NULL,
    resource_id VARCHAR(10) NOT NULL,
    name_snapshot VARCHAR(100),
    phone_snapshot VARCHAR(15),
    description_snapshot VARCHAR(1000),
    service_id_snapshot VARCHAR(10),
    service_name_snapshot VARCHAR(100),
    capacity_snapshot INT,
    service_status_snapshot VARCHAR(20),
    charge DECIMAL(10,2),
    status VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME,
    cancelled_at DATETIME,
    resource_released BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS request_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_id VARCHAR(20),
    actor_id VARCHAR(10),
    old_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    event_at DATETIME
);

-- Seed Data
INSERT INTO users (id, name, role, status) VALUES
('U001', 'Arun Kumar', 'USER', 'ACTIVE'),
('U002', 'Bala Kumar', 'USER', 'ACTIVE'),
('A001', 'Admin', 'ADMINISTRATOR', 'ACTIVE');

INSERT INTO resources (id, reference, service_name, capacity, available_capacity, status, created_at, updated_at) VALUES
('S101', 'S101', 'Membership A', 10, 10, 'ACTIVE', NOW(), NOW()),
('S102', 'S102', 'Membership B', 5, 5, 'ACTIVE', NOW(), NOW()),
('S103', 'S103', 'Membership C', 0, 0, 'INACTIVE', NOW(), NOW());
