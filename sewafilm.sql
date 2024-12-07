-- Tabel Genre

CREATE TABLE genre (

    id SERIAL PRIMARY KEY,

    nama VARCHAR(50) NOT NULL UNIQUE

);

-- Tabel Aktor

CREATE TABLE aktor (

    id SERIAL PRIMARY KEY,

    nama VARCHAR(100) NOT NULL,

    negara_asal VARCHAR(50)

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

    CONSTRAINT fk_film_genre 

        FOREIGN KEY (genre_id) 

        REFERENCES genre(id)

);

-- Tabel Film_Aktor (relasi many-to-many)

CREATE TABLE film_aktor (

    film_id INT,

    aktor_id INT,

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

    email VARCHAR(100),

    role VARCHAR(10) NOT NULL 

        CHECK (role IN ('ADMIN', 'PELANGGAN'))

);

-- Tabel Penyewaan

CREATE TABLE penyewaan (

    id SERIAL PRIMARY KEY,

    pengguna_id INT,

    film_id INT,

    tanggal_sewa DATE,

    tanggal_kembali DATE,

    status VARCHAR(15) DEFAULT 'DISEWA' 

        CHECK (status IN ('DISEWA', 'DIKEMBALIKAN')),

    CONSTRAINT fk_penyewaan_pengguna 

        FOREIGN KEY (pengguna_id) 

        REFERENCES pengguna(id),

    CONSTRAINT fk_penyewaan_film 

        FOREIGN KEY (film_id) 

        REFERENCES film(id)

);



-- Insert data untuk tabel genre
INSERT INTO genre (nama) VALUES
    ('Action'),
    ('Comedy'),
    ('Drama'),
    ('Horror'),
    ('Sci-Fi'),
    ('Romance'),
    ('Thriller'),
    ('Animation');

-- Insert data untuk tabel aktor
INSERT INTO aktor (nama, negara_asal) VALUES
    ('Tom Cruise', 'United States'),
    ('Leonardo DiCaprio', 'United States'),
    ('Angelina Jolie', 'United States'),
    ('Joe Taslim', 'Indonesia'),
    ('Iko Uwais', 'Indonesia'),
    ('Emma Watson', 'United Kingdom'),
    ('Jackie Chan', 'Hong Kong'),
    ('Scarlett Johansson', 'United States'),
    ('Chelsea Islan', 'Indonesia'),
    ('Reza Rahadian', 'Indonesia');

-- Insert data untuk tabel film
INSERT INTO film (judul, deskripsi, tahun_rilis, genre_id, stok, cover_url) VALUES
    ('The Last Mission', 'Film action terbaru yang menegangkan', 2023, 1, 5, 'last_mission.jpg'),
    ('Love in Jakarta', 'Kisah cinta di ibukota', 2023, 6, 3, 'love_jakarta.jpg'),
    ('Ghost Hospital', 'Kisah seram di rumah sakit tua', 2022, 4, 4, 'ghost_hospital.jpg'),
    ('Time Machine', 'Petualangan melintasi waktu', 2023, 5, 6, 'time_machine.jpg'),
    ('Comedy Night', 'Film komedi yang menghibur', 2022, 2, 5, 'comedy_night.jpg'),
    ('The CEO', 'Drama kehidupan eksekutif muda', 2023, 3, 4, 'the_ceo.jpg'),
    ('Kung Fu Master', 'Aksi pertarungan kungfu', 2022, 1, 3, 'kungfu_master.jpg'),
    ('Space Adventure', 'Petualangan di luar angkasa', 2023, 5, 5, 'space_adventure.jpg');

-- Insert data untuk tabel film_aktor
INSERT INTO film_aktor (film_id, aktor_id) VALUES
    (1, 1), -- The Last Mission - Tom Cruise
    (1, 4), -- The Last Mission - Joe Taslim
    (2, 9), -- Love in Jakarta - Chelsea Islan
    (2, 10), -- Love in Jakarta - Reza Rahadian
    (3, 3), -- Ghost Hospital - Angelina Jolie
    (4, 2), -- Time Machine - Leonardo DiCaprio
    (5, 7), -- Comedy Night - Jackie Chan
    (6, 10), -- The CEO - Reza Rahadian
    (7, 5), -- Kung Fu Master - Iko Uwais
    (7, 7), -- Kung Fu Master - Jackie Chan
    (8, 8); -- Space Adventure - Scarlett Johansson

-- Insert data untuk tabel pengguna
INSERT INTO pengguna (username, password, email, role) VALUES
    ('admin1', 'hashed_password123', 'admin1@sewafilm.com', 'ADMIN'),
    ('admin2', 'hashed_password456', 'admin2@sewafilm.com', 'ADMIN'),
    ('john_doe', 'hashed_password789', 'john@email.com', 'PELANGGAN'),
    ('jane_smith', 'hashed_passwordabc', 'jane@email.com', 'PELANGGAN'),
    ('budi123', 'hashed_passworddef', 'budi@email.com', 'PELANGGAN'),
    ('maria456', 'hashed_passwordghi', 'maria@email.com', 'PELANGGAN');

-- Insert data untuk tabel penyewaan
INSERT INTO penyewaan (pengguna_id, film_id, tanggal_sewa, tanggal_kembali, status) VALUES
    (3, 1, '2023-12-01', '2023-12-03', 'DIKEMBALIKAN'),
    (4, 2, '2023-12-02', '2023-12-04', 'DIKEMBALIKAN'),
    (5, 3, '2023-12-03', NULL, 'DISEWA'),
    (6, 4, '2023-12-03', NULL, 'DISEWA'),
    (3, 5, '2023-12-04', NULL, 'DISEWA'),
    (4, 6, '2023-12-01', '2023-12-03', 'DIKEMBALIKAN'),
    (5, 7, '2023-12-02', '2023-12-04', 'DIKEMBALIKAN'),
    (6, 8, '2023-12-04', NULL, 'DISEWA');