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
VALUES ('admin', '$2a$10$DsgooQihdSDpjgwpwW7REOqAt75itONqXYNEeRcj3RpAhRuIeIU3a', 'admin@gmail.com', 'ADMIN');