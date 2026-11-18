-- ==============================================================================
-- DonorLog - DDL-скрипт инициализации схемы PostgreSQL для демонстрации в pgAdmin
-- Разработано для курсового проекта: «Дневник донора крови» (Московский Политех)
-- ==============================================================================

-- 1. Создание базы данных (если не создана через интерфейс pgAdmin):
-- CREATE DATABASE donorlogdb WITH OWNER = postgres ENCODING = 'UTF8';

-- Переключение на базу данных donorlogdb:
-- \c donorlogdb;

-- 2. Таблица учетных записей пользователей (users)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    blood_group VARCHAR(32),
    rh_factor VARCHAR(16),
    region VARCHAR(128) DEFAULT 'г. Москва',
    role VARCHAR(32) DEFAULT 'ROLE_DONOR',
    created_at BIGINT NOT NULL
);

-- 2.1 Таблица медицинских профилей доноров (donor_profiles)
CREATE TABLE IF NOT EXISTS donor_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    blood_group VARCHAR(32) NOT NULL,
    rh_factor VARCHAR(16) NOT NULL,
    kell_factor VARCHAR(16) DEFAULT 'Kell-',
    gender VARCHAR(16) DEFAULT 'MALE',
    birth_date DATE,
    updated_at BIGINT NOT NULL
);

-- 3. Таблица записей о донациях (donations)
CREATE TABLE IF NOT EXISTS donations (
    id VARCHAR(64) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    donation_date DATE NOT NULL,
    component_type VARCHAR(64) NOT NULL,
    volume_ml INTEGER NOT NULL,
    station_name VARCHAR(255) NOT NULL,
    hemoglobin_grams_per_liter INTEGER,
    systolic_bp INTEGER,
    diastolic_bp INTEGER,
    pulse_bpm INTEGER,
    wellbeing VARCHAR(64) DEFAULT 'GOOD',
    notes VARCHAR(1000),
    updated_at BIGINT NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE
);

-- 4. Таблица станций переливания крови (blood_stations)
CREATE TABLE IF NOT EXISTS blood_stations (
    id BIGSERIAL PRIMARY KEY,
    station_name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(64),
    schedule VARCHAR(255),
    need_status VARCHAR(32) DEFAULT 'GREEN',
    o_pos_status VARCHAR(32) DEFAULT 'GREEN',
    o_neg_status VARCHAR(32) DEFAULT 'YELLOW',
    a_pos_status VARCHAR(32) DEFAULT 'GREEN',
    a_neg_status VARCHAR(32) DEFAULT 'RED',
    b_pos_status VARCHAR(32) DEFAULT 'YELLOW',
    b_neg_status VARCHAR(32) DEFAULT 'GREEN',
    ab_pos_status VARCHAR(32) DEFAULT 'GREEN',
    ab_neg_status VARCHAR(32) DEFAULT 'YELLOW'
);

-- 5. Начальные демонстрационные данные (соответствуют данным в мобильном клиенте)
INSERT INTO donor_profiles (user_id, full_name, blood_group, rh_factor, kell_factor, gender, birth_date, updated_at)
VALUES 
    ('default_user', 'Ряховский Алексей Александрович', 'A(II)', 'Rh+', 'Kell-', 'MALE', '2000-05-14', EXTRACT(EPOCH FROM NOW()) * 1000)
ON CONFLICT (user_id) DO UPDATE SET 
    full_name = EXCLUDED.full_name,
    blood_group = EXCLUDED.blood_group,
    rh_factor = EXCLUDED.rh_factor,
    updated_at = EXCLUDED.updated_at;

INSERT INTO donations (id, user_id, donation_date, component_type, volume_ml, station_name, hemoglobin_grams_per_liter, systolic_bp, diastolic_bp, pulse_bpm, wellbeing, notes, updated_at, is_deleted)
VALUES
    ('don-1', 'default_user', '2026-01-15', 'WHOLE_BLOOD', 450, 'Центр крови им. О.К. Гаврилова (Бакинская ул.)', 148, 120, 80, 72, 'GOOD', 'Первая кроводача в новом году. Самочувствие отличное.', 1768464000000, FALSE),
    ('don-2', 'default_user', '2026-03-20', 'WHOLE_BLOOD', 450, 'Центр крови им. О.К. Гаврилова (Поликарпова ул.)', 145, 118, 78, 70, 'GOOD', 'Плановая донация цельной крови.', 1774003200000, FALSE),
    ('don-3', 'default_user', '2026-05-25', 'WHOLE_BLOOD', 450, 'НИИ скорой помощи им. Н.В. Склифосовского', 142, 122, 80, 74, 'GOOD', 'Донация в отделении трансфузиологии Склифа.', 1779696000000, FALSE)
ON CONFLICT (id) DO NOTHING;

INSERT INTO blood_stations (station_name, city, address, phone, schedule, need_status, o_pos_status, o_neg_status, a_pos_status, a_neg_status, b_pos_status, b_neg_status, ab_pos_status, ab_neg_status)
VALUES
    ('Центр крови им. О.К. Гаврилова', 'Москва', 'ул. Бакинская, д. 31', '+7 (495) 326-98-36', 'Пн-Вс: 08:00 - 13:30', 'RED', 'GREEN', 'YELLOW', 'GREEN', 'RED', 'YELLOW', 'GREEN', 'GREEN', 'YELLOW'),
    ('НИИ СП им. Н.В. Склифосовского', 'Москва', 'Б. Сухаревская пл., д. 3, корп. 1', '+7 (495) 680-89-76', 'Пн-Пт: 08:30 - 12:30', 'YELLOW', 'YELLOW', 'RED', 'GREEN', 'YELLOW', 'GREEN', 'YELLOW', 'GREEN', 'GREEN'),
    ('ГКБ №52 (Центр переливания)', 'Москва', 'ул. Пехотная, д. 3', '+7 (499) 196-35-33', 'Пн-Пт: 08:30 - 13:00', 'GREEN', 'GREEN', 'YELLOW', 'GREEN', 'GREEN', 'GREEN', 'YELLOW', 'GREEN', 'YELLOW')
ON CONFLICT DO NOTHING;

-- 6. Контрольные проверочные запросы для демонстрации в pgAdmin:
-- SELECT * FROM donor_profiles;
-- SELECT * FROM donations ORDER BY donation_date DESC;
-- SELECT * FROM blood_stations;
