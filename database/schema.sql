-- ====================================================
-- Plateforme de Gestion des Missions Humanitaires
-- Database Schema - MySQL 8
-- ====================================================

CREATE DATABASE IF NOT EXISTS humanitaire_maroc
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE humanitaire_maroc;

-- ====================================================
-- Table: roles
-- ====================================================
CREATE TABLE IF NOT EXISTS roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ====================================================
-- Table: users
-- ====================================================
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  phone VARCHAR(20),
  city VARCHAR(100),
  region VARCHAR(100),
  avatar_url VARCHAR(500),
  enabled BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ====================================================
-- Table: user_roles (junction)
-- ====================================================
CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ====================================================
-- Table: missions
-- ====================================================
CREATE TABLE IF NOT EXISTS missions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  city VARCHAR(100),
  region VARCHAR(100),
  latitude DOUBLE,
  longitude DOUBLE,
  status VARCHAR(50) DEFAULT 'PLANNED',
  priority VARCHAR(50) DEFAULT 'MEDIUM',
  budget DECIMAL(12,2),
  start_date DATE,
  end_date DATE,
  created_by BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB;

-- ====================================================
-- Table: volunteers
-- ====================================================
CREATE TABLE IF NOT EXISTS volunteers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  email VARCHAR(255) UNIQUE,
  phone VARCHAR(20),
  address VARCHAR(255),
  city VARCHAR(100),
  region VARCHAR(100),
  skills VARCHAR(500),
  available BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ====================================================
-- Table: mission_volunteers (junction)
-- ====================================================
CREATE TABLE IF NOT EXISTS mission_volunteers (
  mission_id BIGINT NOT NULL,
  volunteer_id BIGINT NOT NULL,
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (mission_id, volunteer_id),
  FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
  FOREIGN KEY (volunteer_id) REFERENCES volunteers(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ====================================================
-- Table: beneficiaries
-- ====================================================
CREATE TABLE IF NOT EXISTS beneficiaries (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  full_name VARCHAR(200) NOT NULL,
  family_size INT DEFAULT 1,
  emergency_level VARCHAR(50) DEFAULT 'MEDIUM',
  address VARCHAR(255),
  city VARCHAR(100),
  region VARCHAR(100),
  phone VARCHAR(20),
  needs VARCHAR(500),
  mission_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ====================================================
-- Table: donations
-- ====================================================
CREATE TABLE IF NOT EXISTS donations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  donor_name VARCHAR(200) NOT NULL,
  donor_email VARCHAR(255),
  amount DECIMAL(12,2) NOT NULL,
  currency VARCHAR(10) DEFAULT 'MAD',
  payment_method VARCHAR(50) DEFAULT 'CASH',
  description TEXT,
  status VARCHAR(50) DEFAULT 'COMPLETED',
  transaction_id VARCHAR(100),
  mission_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ====================================================
-- Table: convoys
-- ====================================================
CREATE TABLE IF NOT EXISTS convoys (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  departure_city VARCHAR(100),
  destination_city VARCHAR(100),
  departure_latitude DOUBLE,
  departure_longitude DOUBLE,
  destination_latitude DOUBLE,
  destination_longitude DOUBLE,
  current_latitude DOUBLE,
  current_longitude DOUBLE,
  cargo VARCHAR(500),
  status VARCHAR(50) DEFAULT 'PENDING',
  departure_date TIMESTAMP,
  estimated_arrival TIMESTAMP,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ====================================================
-- Table: events
-- ====================================================
CREATE TABLE IF NOT EXISTS events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  event_date DATE,
  location VARCHAR(255),
  city VARCHAR(100),
  region VARCHAR(100),
  organizer VARCHAR(200),
  max_participants INT,
  current_participants INT DEFAULT 0,
  status VARCHAR(50) DEFAULT 'UPCOMING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ====================================================
-- Table: reports
-- ====================================================
CREATE TABLE IF NOT EXISTS reports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  type VARCHAR(50),
  content TEXT,
  generated_by BIGINT,
  file_path VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (generated_by) REFERENCES users(id)
) ENGINE=InnoDB;

-- ====================================================
-- Table: notifications
-- ====================================================
CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  message TEXT,
  type VARCHAR(50) DEFAULT 'SYSTEM',
  `read` BOOLEAN DEFAULT FALSE,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ====================================================
-- Indexes for performance
-- ====================================================
CREATE INDEX idx_missions_status ON missions(status);
CREATE INDEX idx_missions_region ON missions(region);
CREATE INDEX idx_missions_priority ON missions(priority);
CREATE INDEX idx_volunteers_region ON volunteers(region);
CREATE INDEX idx_volunteers_available ON volunteers(available);
CREATE INDEX idx_beneficiaries_emergency ON beneficiaries(emergency_level);
CREATE INDEX idx_donations_method ON donations(payment_method);
CREATE INDEX idx_donations_date ON donations(created_at);
CREATE INDEX idx_convoys_status ON convoys(status);
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(`read`);

-- ====================================================
-- Insert default roles
-- ====================================================
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_MANAGER'), ('ROLE_VOLUNTEER'), ('ROLE_DONOR')
ON DUPLICATE KEY UPDATE name = VALUES(name);
