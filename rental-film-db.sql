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
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

-- Tabel Film_Aktor (relasi many-to-many)
CREATE TABLE film_aktor (
    film_id INT,
    aktor_id INT,
    PRIMARY KEY (film_id, aktor_id),
    FOREIGN KEY (film_id) REFERENCES film(id),
    FOREIGN KEY (aktor_id) REFERENCES aktor(id)
);

-- Tabel Pengguna
CREATE TABLE pengguna (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(10) CHECK (role IN ('ADMIN', 'PELANGGAN')) NOT NULL
);

-- Tabel Penyewaan
CREATE TABLE penyewaan (
    id SERIAL PRIMARY KEY,
    pengguna_id INT,
    film_id INT,
    tanggal_sewa DATE,
    tanggal_kembali DATE,
    status VARCHAR(20) CHECK (status IN ('DISEWA', 'DIKEMBALIKAN')) DEFAULT 'DISEWA',
    FOREIGN KEY (pengguna_id) REFERENCES pengguna(id),
    FOREIGN KEY (film_id) REFERENCES film(id)
);