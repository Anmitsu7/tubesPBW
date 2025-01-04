-- Membuat sequences untuk auto-increment
CREATE SEQUENCE genre_id_seq START 1;
CREATE SEQUENCE aktor_id_seq START 1;
CREATE SEQUENCE film_id_seq START 1;
CREATE SEQUENCE pengguna_id_seq START 1;
CREATE SEQUENCE penyewaan_id_seq START 1;

-- Tabel Genre
CREATE TABLE genre (
   id INT PRIMARY KEY DEFAULT nextval('genre_id_seq'),
   nama VARCHAR(50) NOT NULL UNIQUE,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP
);

-- Tabel Aktor
CREATE TABLE aktor (
   id INT PRIMARY KEY DEFAULT nextval('aktor_id_seq'),
   nama VARCHAR(100) NOT NULL,
   negara_asal VARCHAR(50),
   foto_url VARCHAR(255),
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP
);

-- Tabel Film
CREATE TABLE film (
   id INT PRIMARY KEY DEFAULT nextval('film_id_seq'),
   judul VARCHAR(200) NOT NULL,
   deskripsi TEXT,
   tahun_rilis INT,
   genre_id INT,
   stok INT NOT NULL,
   cover_url VARCHAR(255),
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP,
   CONSTRAINT fk_film_genre 
       FOREIGN KEY (genre_id) 
       REFERENCES genre(id)
);

-- Tabel Film_Aktor (relasi many-to-many)
CREATE TABLE film_aktor (
   film_id INT,
   aktor_id INT,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (film_id, aktor_id),
   CONSTRAINT fk_film_aktor_film 
       FOREIGN KEY (film_id) 
       REFERENCES film(id),
   CONSTRAINT fk_film_aktor_aktor 
       FOREIGN KEY (aktor_id) 
       REFERENCES aktor(id)
);

-- Tabel Pengguna
CREATE TABLE pengguna (
   id INT PRIMARY KEY DEFAULT nextval('pengguna_id_seq'),
   username VARCHAR(50) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   email VARCHAR(100) UNIQUE,
   role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'PELANGGAN')),
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP,
   last_login_time TIMESTAMP
);

-- Tabel Penyewaan
CREATE TABLE penyewaan (
   id INT PRIMARY KEY DEFAULT nextval('penyewaan_id_seq'),
   pengguna_id INT,
   film_id INT,
   tanggal_sewa DATE NOT NULL,
   tanggal_kembali DATE,
   status VARCHAR(15) DEFAULT 'DISEWA' CHECK (status IN ('DISEWA', 'DIKEMBALIKAN')),
   rental_duration INT,
   late_fee DECIMAL(10,2),
   notes TEXT,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP,
   CONSTRAINT fk_penyewaan_pengguna 
       FOREIGN KEY (pengguna_id) 
       REFERENCES pengguna(id),
   CONSTRAINT fk_penyewaan_film 
       FOREIGN KEY (film_id) 
       REFERENCES film(id)
);

-- Trigger untuk mengupdate updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Buat trigger untuk setiap tabel
CREATE TRIGGER update_genre_updated_at
    BEFORE UPDATE ON genre
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_aktor_updated_at
    BEFORE UPDATE ON aktor
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_film_updated_at
    BEFORE UPDATE ON film
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_pengguna_updated_at
    BEFORE UPDATE ON pengguna
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_penyewaan_updated_at
    BEFORE UPDATE ON penyewaan
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Menambahkan data genre
INSERT INTO genre (nama) VALUES
    ('Action'),
    ('Adventure'),
    ('Animation'),
    ('Comedy'),
    ('Crime'),
    ('Documentary'),
    ('Drama'),
    ('Family'),
    ('Fantasy'),
    ('Horror'),
    ('Mystery'),
    ('Romance'),
    ('Science Fiction'),
    ('Thriller'),
    ('War'),
    ('Western'),
    ('Musical'),
    ('Biography'),
    ('Sport'),
    ('Historical'),
    ('Superhero'),
    ('Martial Arts'),
    ('Film Noir'),
    ('Psychological');

-- Membuat admin default dan pengguna
INSERT INTO pengguna (username, password, email, role) VALUES 
('admin', '$2a$10$qFh7g/wxI/eaPgiIZDHyyuJFjw7q2wPBTB0Ufi6sg8h30tC9PhQ9S', 'admin@gmail.com', 'ADMIN'),
('john_doe', '$2a$10$qFh7g/wxI/eaPgiIZDHyyuJFjw7q2wPBTB0Ufi6sg8h30tC9PhQ9S', 'john@example.com', 'PELANGGAN'),
('jane_smith', '$2a$10$qFh7g/wxI/eaPgiIZDHyyuJFjw7q2wPBTB0Ufi6sg8h30tC9PhQ9S', 'jane@example.com', 'PELANGGAN'),
('robert_wilson', '$2a$10$qFh7g/wxI/eaPgiIZDHyyuJFjw7q2wPBTB0Ufi6sg8h30tC9PhQ9S', 'robert@example.com', 'PELANGGAN'),
('sarah_brown', '$2a$10$qFh7g/wxI/eaPgiIZDHyyuJFjw7q2wPBTB0Ufi6sg8h30tC9PhQ9S', 'sarah@example.com', 'PELANGGAN');

-- Insert Aktor
INSERT INTO aktor (nama, negara_asal, foto_url) VALUES
('Tom Cruise', 'United States', 'https://example.com/photos/tom_cruise.jpg'),
('Leonardo DiCaprio', 'United States', 'https://example.com/photos/leonardo_dicaprio.jpg'),
('Emma Watson', 'United Kingdom', 'https://example.com/photos/emma_watson.jpg'),
('Jackie Chan', 'Hong Kong', 'https://example.com/photos/jackie_chan.jpg'),
('Scarlett Johansson', 'United States', 'https://example.com/photos/scarlett_johansson.jpg'),
('Robert Downey Jr.', 'United States', 'https://example.com/photos/robert_downey_jr.jpg'),
('Gal Gadot', 'Israel', 'https://example.com/photos/gal_gadot.jpg'),
('Chris Evans', 'United States', 'https://example.com/photos/chris_evans.jpg'),
('Jennifer Lawrence', 'United States', 'https://example.com/photos/jennifer_lawrence.jpg'),
('Brad Pitt', 'United States', 'https://example.com/photos/brad_pitt.jpg');

-- Insert Film
INSERT INTO film (judul, deskripsi, tahun_rilis, genre_id, stok, cover_url) VALUES
('Top Gun: Maverick', 'After thirty years, Maverick is still pushing the envelope as a top naval aviator', 2022, 1, 5, 'top_gun_maverick.jpg'),
('Inception', 'A thief who enters the dreams of others to steal secrets from their subconscious', 2010, 13, 3, 'inception.jpg'),
('Harry Potter and the Deathly Hallows', 'The final chapter of Harry Potter saga', 2011, 9, 4, 'deathly_hallows.jpg'),
('Rush Hour', 'A Hong Kong detective teams up with a Los Angeles cop for an investigation', 1998, 1, 2, 'rush_hour.jpg'),
('Black Widow', 'Natasha Romanoff confronts the darker parts of her ledger', 2021, 21, 6, 'black_widow.jpg'),
('Iron Man', 'Genius billionaire invents high-tech armor and becomes a superhero', 2008, 21, 3, 'iron_man.jpg'),
('Wonder Woman', 'Amazon princess leaves her island home to fight in a war to end all wars', 2017, 21, 4, 'wonder_woman.jpg'),
('Avengers: Endgame', 'The Avengers take one final stand against Thanos', 2019, 21, 5, 'avengers_endgame.jpg'),
('Fight Club', 'An insomniac office worker and a mysterious soap maker form an underground fight club', 1999, 7, 3, 'fight_club.jpg'),
('Mission: Impossible - Dead Reckoning', 'Ethan Hunt and his IMF team embark on their most dangerous mission yet', 2023, 1, 4, 'mi_dead_reckoning.jpg');

-- Insert Film_Aktor
INSERT INTO film_aktor (film_id, aktor_id) VALUES
(1, 1), (1, 3),  -- The Adventure Begins stars Tom Cruise and Emma Watson
(2, 5), (2, 9),  -- City Lights stars Scarlett Johansson and Jennifer Lawrence
(3, 4), (3, 7),  -- The Last Stand stars Jackie Chan and Gal Gadot
(4, 2), (4, 10), -- Mystery Manor stars Leonardo DiCaprio and Brad Pitt
(5, 6), (5, 8),  -- Space Warriors stars Robert Downey Jr. and Chris Evans
(6, 3), (6, 9),  -- Love in Paris stars Emma Watson and Jennifer Lawrence
(7, 4), (7, 7),  -- The Dragon Master stars Jackie Chan and Gal Gadot
(8, 6), (8, 8),  -- Heroes Unite stars Robert Downey Jr. and Chris Evans
(9, 5), (9, 10), -- Historical Queens stars Scarlett Johansson and Brad Pitt
(10, 1), (10, 2); -- Mind Games stars Tom Cruise and Leonardo DiCaprio

-- Insert Penyewaan 
INSERT INTO penyewaan (pengguna_id, film_id, tanggal_sewa, tanggal_kembali, status, rental_duration, late_fee, notes) VALUES
(2, 1, '2023-12-25', '2024-01-01', 'DIKEMBALIKAN', 7, 0.00, 'Returned on time'),
(3, 2, '2023-12-28', NULL, 'DISEWA', 7, NULL, 'Currently rented'),
(4, 3, '2023-12-20', '2023-12-28', 'DIKEMBALIKAN', 7, 5.00, 'Returned 1 day late'),
(2, 4, '2023-12-30', NULL, 'DISEWA', 7, NULL, 'Currently rented'),
(5, 5, '2023-12-15', '2023-12-22', 'DIKEMBALIKAN', 7, 0.00, 'Returned on time'),
(3, 6, '2023-12-27', NULL, 'DISEWA', 7, NULL, 'Currently rented'),
(4, 7, '2023-12-18', '2023-12-25', 'DIKEMBALIKAN', 7, 0.00, 'Returned on time'),
(5, 8, '2023-12-29', NULL, 'DISEWA', 7, NULL, 'Currently rented'),
(2, 9, '2023-12-22', '2023-12-29', 'DIKEMBALIKAN', 7, 0.00, 'Returned on time'),
(3, 10, '2023-12-26', NULL, 'DISEWA', 7, NULL, 'Currently rented');

-- Update cover_url untuk setiap film
UPDATE film 
SET cover_url = 'top_gun_maverick.jpg',
    deskripsi = 'After thirty years, Maverick is still pushing the envelope as a top naval aviator'
WHERE id = 1;

UPDATE film 
SET cover_url = 'inception.jpg',
    deskripsi = 'A thief who enters the dreams of others to steal secrets from their subconscious'
WHERE id = 2;

UPDATE film 
SET cover_url = 'deathly_hallows.jpg',
    deskripsi = 'The final chapter of Harry Potter saga'
WHERE id = 3;

UPDATE film 
SET cover_url = 'rush_hour.jpg',
    deskripsi = 'A Hong Kong detective teams up with a Los Angeles cop for an investigation'
WHERE id = 4;

UPDATE film 
SET cover_url = 'black_widow.jpg',
    deskripsi = 'Natasha Romanoff confronts the darker parts of her ledger'
WHERE id = 5;

UPDATE film 
SET cover_url = 'iron_man.jpg',
    deskripsi = 'Genius billionaire invents high-tech armor and becomes a superhero'
WHERE id = 6;

UPDATE film 
SET cover_url = 'wonder_woman.jpg',
    deskripsi = 'Amazon princess leaves her island home to fight in a war to end all wars'
WHERE id = 7;

UPDATE film 
SET cover_url = 'avengers_endgame.jpg',
    deskripsi = 'The Avengers take one final stand against Thanos'
WHERE id = 8;

UPDATE film 
SET cover_url = 'fight_club.jpg',
    deskripsi = 'An insomniac office worker and a mysterious soap maker form an underground fight club'
WHERE id = 9;

UPDATE film 
SET cover_url = 'mi_dead_reckoning.jpg',
    deskripsi = 'Ethan Hunt and his IMF team embark on their most dangerous mission yet'
WHERE id = 10;