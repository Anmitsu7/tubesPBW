-- Tabel Genre
CREATE TABLE genre (
   id SERIAL PRIMARY KEY,
   nama VARCHAR(50) NOT NULL UNIQUE
);

-- Tabel Aktor
CREATE TABLE aktor (
   id SERIAL PRIMARY KEY,
   nama VARCHAR(100) NOT NULL,
   negara_asal VARCHAR(50),
   foto_url VARCHAR(255)
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

-- Tabel Pengguna dengan kolom tambahan
CREATE TABLE pengguna (
   id SERIAL PRIMARY KEY,
   username VARCHAR(50) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   email VARCHAR(100) UNIQUE,
   role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'PELANGGAN')),
   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
   last_login_time TIMESTAMP
);

-- Tabel Penyewaan
CREATE TABLE penyewaan (
   id SERIAL PRIMARY KEY,
   pengguna_id INT,
   film_id INT,
   tanggal_sewa DATE,
   tanggal_kembali DATE,
   status VARCHAR(15) DEFAULT 'DISEWA' CHECK (status IN ('DISEWA', 'DIKEMBALIKAN')),
   CONSTRAINT fk_penyewaan_pengguna 
       FOREIGN KEY (pengguna_id) 
       REFERENCES pengguna(id),
   CONSTRAINT fk_penyewaan_film 
       FOREIGN KEY (film_id) 
       REFERENCES film(id)
);

-- Insert data untuk tabel genre
INSERT INTO genre (nama) VALUES
   ('Action'),     -- 1
   ('Comedy'),     -- 2
   ('Drama'),      -- 3
   ('Horror'),     -- 4
   ('Sci-Fi'),     -- 5
   ('Romance'),    -- 6
   ('Thriller'),   -- 7
   ('Animation');  -- 8

-- Insert data untuk tabel aktor
INSERT INTO aktor (nama, negara_asal) VALUES
   ('Leonardo DiCaprio', 'United States'),
   ('Robert Downey Jr.', 'United States'),
   ('Chris Evans', 'United States'),
   ('Tom Holland', 'United Kingdom'),
   ('Chris Hemsworth', 'Australia'),
   ('Scarlett Johansson', 'United States'),
   ('Mark Ruffalo', 'United States'),
   ('Jeremy Renner', 'United States');

-- Insert data untuk tabel film dengan film-film yang nyata
INSERT INTO film (judul, deskripsi, tahun_rilis, genre_id, stok, cover_url) VALUES
   ('Inception', 'Seorang pencuri ulung yang mampu menyusup ke alam bawah sadar orang lain mencoba melakukan misi terakhir yang hampir mustahil: menanamkan ide ke pikiran seseorang.', 2010, 5, 5, 'inception.jpg'),
   
   ('Avengers: Infinity War', 'The Avengers dan sekutu mereka harus bersedia mengorbankan segalanya dalam upaya mengalahkan Thanos yang kuat sebelum dia menghancurkan alam semesta.', 2018, 1, 6, 'infinity_war.jpg'),
   
   ('Spider-Man: No Way Home', 'Identitas Spider-Man sekarang terungkap, Peter meminta bantuan Doctor Strange. Namun ketika mantra salah, musuh berbahaya dari dunia lain mulai muncul.', 2021, 1, 4, 'spiderman.jpg'),
   
   ('Thor: Love and Thunder', 'Thor meminta bantuan Valkyrie, Korg dan mantan pacar Jane Foster untuk melawan Gorr the God Butcher, yang berniat membuat para dewa punah.', 2022, 1, 5, 'thor.jpg'),
   
   ('Black Widow', 'Natasha Romanoff menghadapi sisi gelap dari buku besar ketika konspirasi berbahaya yang terkait dengan masa lalunya muncul.', 2021, 1, 4, 'black_widow.jpg'),
   
   ('Titanic', 'Kisah cinta tragis antara Rose DeWitt Bukater yang kaya dan Jack Dawson yang miskin di kapal RMS Titanic yang legendaris.', 1997, 6, 3, 'titanic.jpg'),
   
   ('Interstellar', 'Sekelompok astronot melakukan perjalanan melewati lubang cacing untuk mencari planet yang layak huni bagi umat manusia yang terancam kepunahan di Bumi.', 2014, 5, 4, 'interstellar.jpg'),
   
   ('Iron Man', 'Tony Stark membangun setelan baju besi canggih dan menjadi Iron Man setelah terperangkap di gua Afghanistan.', 2008, 1, 5, 'ironman.jpg');

-- Insert data untuk tabel film_aktor yang sesuai dengan filmnya
INSERT INTO film_aktor (film_id, aktor_id) VALUES
   (1, 1),  -- Inception - Leonardo DiCaprio
   (2, 2),  -- Infinity War - Robert Downey Jr.  
   (2, 3),  -- Infinity War - Chris Evans
   (2, 5),  -- Infinity War - Chris Hemsworth
   (2, 6),  -- Infinity War - Scarlett Johansson
   (2, 7),  -- Infinity War - Mark Ruffalo
   (2, 8),  -- Infinity War - Jeremy Renner
   (3, 4),  -- Spider-Man - Tom Holland
   (4, 5),  -- Thor - Chris Hemsworth
   (5, 6),  -- Black Widow - Scarlett Johansson
   (6, 1),  -- Titanic - Leonardo DiCaprio
   (8, 2);  -- Iron Man - Robert Downey Jr.

-- Insert admin and user accounts (password: password123)
INSERT INTO pengguna (username, password, email, role) VALUES
   ('admin', '$2a$10$vn7JL6KQuXRUKygN0hWx1.UmCvJUWcteSv0WlgRAJsAwPo7vV1Pj2', 'admin@reelrental.com', 'ADMIN'),
   ('user1', '$2a$10$vn7JL6KQuXRUKygN0hWx1.UmCvJUWcteSv0WlgRAJsAwPo7vV1Pj2', 'user1@example.com', 'PELANGGAN'),
   ('user2', '$2a$10$vn7JL6KQuXRUKygN0hWx1.UmCvJUWcteSv0WlgRAJsAwPo7vV1Pj2', 'user2@example.com', 'PELANGGAN');

-- Insert more rental data (10 entries)
INSERT INTO penyewaan (pengguna_id, film_id, tanggal_sewa, tanggal_kembali, status) VALUES
    -- User1 rental history
    (2, 1, '2023-12-01', '2023-12-03', 'DIKEMBALIKAN'),  -- Inception
    (2, 2, '2023-12-03', '2023-12-05', 'DIKEMBALIKAN'),  -- Infinity War
    (2, 3, '2023-12-05', '2023-12-07', 'DIKEMBALIKAN'),  -- Spider-Man
    (2, 4, '2023-12-07', '2023-12-09', 'DIKEMBALIKAN'),  -- Thor
    (2, 8, '2023-12-10', NULL, 'DISEWA'),                -- Iron Man

    -- User2 rental history
    (3, 5, '2023-12-02', '2023-12-04', 'DIKEMBALIKAN'),  -- Black Widow
    (3, 6, '2023-12-04', '2023-12-06', 'DIKEMBALIKAN'),  -- Titanic
    (3, 7, '2023-12-06', '2023-12-08', 'DIKEMBALIKAN'),  -- Interstellar
    (3, 1, '2023-12-08', '2023-12-10', 'DIKEMBALIKAN'),  -- Inception
    (3, 2, '2023-12-10', NULL, 'DISEWA');                -- Infinity War



INSERT INTO pengguna (username, password, email, role) VALUES 
('admin1234', '12345678', 'admin1234@reelrental.com', 'ADMIN');