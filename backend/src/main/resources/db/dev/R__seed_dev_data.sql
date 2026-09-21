-- All dev users share the following password: Password123!
-- Hash: $2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq

-- 1. USERS
INSERT INTO users (id, name, email, password_hash, phone, role, created_at)
VALUES
    -- Admins
    (1, 'John Admin', 'admin1@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000001', 'ADMIN', CURRENT_TIMESTAMP),
    (2, 'Jane Admin', 'admin2@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000002', 'ADMIN', CURRENT_TIMESTAMP),
    -- Venue Admins
    (3, 'Alpha Venue', 'venueadmin1@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000003', 'VENUE_ADMIN', CURRENT_TIMESTAMP),
    (4, 'Beta Venue', 'venueadmin2@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000004', 'VENUE_ADMIN', CURRENT_TIMESTAMP),
    -- Participants
    (5, 'Primero Participant', 'participant1@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000005', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (6, 'Secundo Participant', 'participant2@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000006', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (7, 'Tercero Participant', 'participant3@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000007', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (8, 'Cuatro Participant', 'participant4@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000008', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (9, 'Cinco Participant', 'participant5@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000009', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (10, 'Seis Participant', 'participant6@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000010', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (11, 'Siete Participant', 'participant7@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000011', 'PARTICIPANT', CURRENT_TIMESTAMP),
    (12, 'Ocho Participant', 'participant8@example.com', '$2a$10$yYE9/.07leixgyy/GkbL5uRrDMqIj8fkc5RdnYYJCGCYmPDUVDyLq', '+10000000012', 'PARTICIPANT', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. VENUES
INSERT INTO venue (id, managed_by, name, description, address, created_at)
VALUES
    (1, 3, 'Cluj Arena', 'Premier multi-sport complex and training center', 'Aleea Stadionului 2, 400375 Cluj-Napoca', CURRENT_TIMESTAMP),
    (2, 4, 'Lakeside Community Center', 'Modern hall and outdoor facilities for social events', 'Aleea Targului 5, 500220 Piatra-Neamt', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 3. RESOURCES
INSERT INTO resources (id, venue_id, name, activity_type, activity_description, capacity, type, hourly_rate)
VALUES
    -- Cluj Arena (Venue 1)
    (1, 1, 'Main Basketball Court', 'Basketball', 'Hardwood indoor full-sized court', 30, 'INDOOR_SPORT', 55.00),
    (2, 1, 'Outdoor Soccer Pitch', 'Soccer', 'Regulation turf soccer pitch with lighting', 50, 'OUTDOOR_SPORT', 80.00),
    (3, 1, 'Squash Court A', 'Squash', 'Glass-back regulation squash court', 4, 'INDOOR_SPORT', 40.00),
    (4, 1, 'VIP Lounge & Games', 'Social Gathering', 'Equipped with board games and lounge seating', 25, 'BOARDGAME_SOCIAL', 45.00),
    -- Lakeside Community Center (Venue 2)
    (5, 2, 'Oak Conference Room', 'Workshops', 'Equipped with presentation monitors and seating', 20, 'BOARDGAME_SOCIAL', 35.00),
    (6, 2, 'Lakeside Volleyball Court', 'Beach Volleyball', 'Sand court near the lakefront', 16, 'OUTDOOR_SPORT', 30.00),
    (7, 2, 'Table Tennis & Billiards Room', 'Table Games', 'Recreational parlor with ping pong and pool tables', 15, 'INDOOR_SPORT', 25.00)
ON CONFLICT (id) DO NOTHING;

-- 4. INVENTORY
INSERT INTO inventory (id, venue_id, name, total_quantity, available_quantity)
VALUES
    -- Venue 1
    (1, 1, 'Basketballs', 25, 18),
    (2, 1, 'Training Cones Set', 40, 40),
    (3, 1, 'Soccer Balls', 30, 22),
    (4, 1, 'Squash Rackets', 12, 8),
    (5, 1, 'Catan & Carcassonne Sets', 6, 6),
    -- Venue 2
    (6, 2, 'Projector & Screen Kit', 5, 3),
    (7, 2, 'Folding Chairs', 120, 95),
    (8, 2, 'Volleyballs', 15, 12),
    (9, 2, 'Table Tennis Paddles & Balls Set', 10, 7),
    (10, 2, 'Extension Cords & Power Strips', 20, 16)
ON CONFLICT (id) DO NOTHING;

-- 5. RESET SERIAL SEQUENCES
-- Ensures subsequent auto-increment values do not clash with hardcoded IDs
SELECT setval(pg_get_serial_sequence('users', 'id'), COALESCE(MAX(id), 1)) FROM users;
SELECT setval(pg_get_serial_sequence('venue', 'id'), COALESCE(MAX(id), 1)) FROM venue;
SELECT setval(pg_get_serial_sequence('resources', 'id'), COALESCE(MAX(id), 1)) FROM resources;
SELECT setval(pg_get_serial_sequence('inventory', 'id'), COALESCE(MAX(id), 1)) FROM inventory;