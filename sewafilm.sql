-- Tabel Genre
CREATE TABLE genre (
   id SERIAL PRIMARY KEY,
   nama VARCHAR(50) NOT NULL UNIQUE,
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP
);

-- Tabel Aktor
CREATE TABLE aktor (
   id SERIAL PRIMARY KEY,
   nama VARCHAR(100) NOT NULL,
   negara_asal VARCHAR(50),
   foto_url VARCHAR(255),
   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at TIMESTAMP
);

-- Tabel Film
CREATE TABLE film (
   id SERIAL PRIMARY KEY,
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
   id SERIAL PRIMARY KEY,
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
   id SERIAL PRIMARY KEY,
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

-- Membuat admin default
INSERT INTO pengguna (username, password, email, role) 
VALUES ('admin', '$2a$10$qFh7g/wxI/eaPgiIZDHyyuJFjw7q2wPBTB0Ufi6sg8h30tC9PhQ9S', 'admin@gmail.com', 'ADMIN');

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
('The Adventure Begins', 'An epic journey through unknown lands', 2023, 2, 5, 'https://example.com/covers/adventure_begins.jpg'),
('City Lights', 'A romantic comedy set in New York', 2022, 4, 3, 'https://example.com/covers/city_lights.jpg'),
('The Last Stand', 'Action-packed thriller about a final mission', 2023, 1, 4, 'https://example.com/covers/last_stand.jpg'),
('Mystery Manor', 'A detective story full of twists', 2021, 11, 2, 'https://example.com/covers/mystery_manor.jpg'),
('Space Warriors', 'Science fiction epic about interstellar war', 2022, 13, 6, 'https://example.com/covers/space_warriors.jpg'),
('Love in Paris', 'A romantic tale in the city of love', 2023, 12, 3, 'https://example.com/covers/love_paris.jpg'),
('The Dragon Master', 'Martial arts adventure in ancient China', 2021, 22, 4, 'https://example.com/covers/dragon_master.jpg'),
('Heroes Unite', 'Superhero team saves the world', 2022, 21, 5, 'https://example.com/covers/heroes_unite.jpg'),
('Historical Queens', 'Drama about rival monarchs', 2023, 20, 3, 'https://example.com/covers/historical_queens.jpg'),
('Mind Games', 'Psychological thriller about memory loss', 2022, 24, 4, 'https://example.com/covers/mind_games.jpg');

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