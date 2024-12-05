PGDMP  	                    |            sewafilm    17.1    17.1 1    *           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                           false            +           0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                           false            ,           0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                           false            -           1262    16435    sewafilm    DATABASE     �   CREATE DATABASE sewafilm WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'English_United States.1252';
    DROP DATABASE sewafilm;
                     postgres    false            �            1259    16446    aktor    TABLE     �   CREATE TABLE public.aktor (
    id integer NOT NULL,
    nama character varying(100) NOT NULL,
    negara_asal character varying(50)
);
    DROP TABLE public.aktor;
       public         heap r       postgres    false            �            1259    16445    aktor_id_seq    SEQUENCE     �   CREATE SEQUENCE public.aktor_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.aktor_id_seq;
       public               postgres    false    220            .           0    0    aktor_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.aktor_id_seq OWNED BY public.aktor.id;
          public               postgres    false    219            �            1259    16453    film    TABLE     �   CREATE TABLE public.film (
    id integer NOT NULL,
    judul character varying(200) NOT NULL,
    deskripsi text,
    tahun_rilis integer,
    genre_id integer,
    stok integer NOT NULL,
    cover_url character varying(255)
);
    DROP TABLE public.film;
       public         heap r       postgres    false            �            1259    16466 
   film_aktor    TABLE     `   CREATE TABLE public.film_aktor (
    film_id integer NOT NULL,
    aktor_id integer NOT NULL
);
    DROP TABLE public.film_aktor;
       public         heap r       postgres    false            �            1259    16452    film_id_seq    SEQUENCE     �   CREATE SEQUENCE public.film_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 "   DROP SEQUENCE public.film_id_seq;
       public               postgres    false    222            /           0    0    film_id_seq    SEQUENCE OWNED BY     ;   ALTER SEQUENCE public.film_id_seq OWNED BY public.film.id;
          public               postgres    false    221            �            1259    16437    genre    TABLE     `   CREATE TABLE public.genre (
    id integer NOT NULL,
    nama character varying(50) NOT NULL
);
    DROP TABLE public.genre;
       public         heap r       postgres    false            �            1259    16436    genre_id_seq    SEQUENCE     �   CREATE SEQUENCE public.genre_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 #   DROP SEQUENCE public.genre_id_seq;
       public               postgres    false    218            0           0    0    genre_id_seq    SEQUENCE OWNED BY     =   ALTER SEQUENCE public.genre_id_seq OWNED BY public.genre.id;
          public               postgres    false    217            �            1259    16482    pengguna    TABLE     o  CREATE TABLE public.pengguna (
    id integer NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    email character varying(100),
    role character varying(10) NOT NULL,
    CONSTRAINT pengguna_role_check CHECK (((role)::text = ANY ((ARRAY['ADMIN'::character varying, 'PELANGGAN'::character varying])::text[])))
);
    DROP TABLE public.pengguna;
       public         heap r       postgres    false            �            1259    16481    pengguna_id_seq    SEQUENCE     �   CREATE SEQUENCE public.pengguna_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 &   DROP SEQUENCE public.pengguna_id_seq;
       public               postgres    false    225            1           0    0    pengguna_id_seq    SEQUENCE OWNED BY     C   ALTER SEQUENCE public.pengguna_id_seq OWNED BY public.pengguna.id;
          public               postgres    false    224            �            1259    16492 	   penyewaan    TABLE     x  CREATE TABLE public.penyewaan (
    id integer NOT NULL,
    pengguna_id integer,
    film_id integer,
    tanggal_sewa date,
    tanggal_kembali date,
    status character varying(15) DEFAULT 'DISEWA'::character varying,
    CONSTRAINT penyewaan_status_check CHECK (((status)::text = ANY ((ARRAY['DISEWA'::character varying, 'DIKEMBALIKAN'::character varying])::text[])))
);
    DROP TABLE public.penyewaan;
       public         heap r       postgres    false            �            1259    16491    penyewaan_id_seq    SEQUENCE     �   CREATE SEQUENCE public.penyewaan_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
 '   DROP SEQUENCE public.penyewaan_id_seq;
       public               postgres    false    227            2           0    0    penyewaan_id_seq    SEQUENCE OWNED BY     E   ALTER SEQUENCE public.penyewaan_id_seq OWNED BY public.penyewaan.id;
          public               postgres    false    226            p           2604    16449    aktor id    DEFAULT     d   ALTER TABLE ONLY public.aktor ALTER COLUMN id SET DEFAULT nextval('public.aktor_id_seq'::regclass);
 7   ALTER TABLE public.aktor ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    219    220    220            q           2604    16456    film id    DEFAULT     b   ALTER TABLE ONLY public.film ALTER COLUMN id SET DEFAULT nextval('public.film_id_seq'::regclass);
 6   ALTER TABLE public.film ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    221    222    222            o           2604    16440    genre id    DEFAULT     d   ALTER TABLE ONLY public.genre ALTER COLUMN id SET DEFAULT nextval('public.genre_id_seq'::regclass);
 7   ALTER TABLE public.genre ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    218    217    218            r           2604    16485    pengguna id    DEFAULT     j   ALTER TABLE ONLY public.pengguna ALTER COLUMN id SET DEFAULT nextval('public.pengguna_id_seq'::regclass);
 :   ALTER TABLE public.pengguna ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    224    225    225            s           2604    16495    penyewaan id    DEFAULT     l   ALTER TABLE ONLY public.penyewaan ALTER COLUMN id SET DEFAULT nextval('public.penyewaan_id_seq'::regclass);
 ;   ALTER TABLE public.penyewaan ALTER COLUMN id DROP DEFAULT;
       public               postgres    false    227    226    227                       0    16446    aktor 
   TABLE DATA           6   COPY public.aktor (id, nama, negara_asal) FROM stdin;
    public               postgres    false    220   -7       "          0    16453    film 
   TABLE DATA           \   COPY public.film (id, judul, deskripsi, tahun_rilis, genre_id, stok, cover_url) FROM stdin;
    public               postgres    false    222   J7       #          0    16466 
   film_aktor 
   TABLE DATA           7   COPY public.film_aktor (film_id, aktor_id) FROM stdin;
    public               postgres    false    223   g7                 0    16437    genre 
   TABLE DATA           )   COPY public.genre (id, nama) FROM stdin;
    public               postgres    false    218   �7       %          0    16482    pengguna 
   TABLE DATA           G   COPY public.pengguna (id, username, password, email, role) FROM stdin;
    public               postgres    false    225   �7       '          0    16492 	   penyewaan 
   TABLE DATA           d   COPY public.penyewaan (id, pengguna_id, film_id, tanggal_sewa, tanggal_kembali, status) FROM stdin;
    public               postgres    false    227   �7       3           0    0    aktor_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.aktor_id_seq', 1, false);
          public               postgres    false    219            4           0    0    film_id_seq    SEQUENCE SET     :   SELECT pg_catalog.setval('public.film_id_seq', 1, false);
          public               postgres    false    221            5           0    0    genre_id_seq    SEQUENCE SET     ;   SELECT pg_catalog.setval('public.genre_id_seq', 1, false);
          public               postgres    false    217            6           0    0    pengguna_id_seq    SEQUENCE SET     >   SELECT pg_catalog.setval('public.pengguna_id_seq', 1, false);
          public               postgres    false    224            7           0    0    penyewaan_id_seq    SEQUENCE SET     ?   SELECT pg_catalog.setval('public.penyewaan_id_seq', 1, false);
          public               postgres    false    226            |           2606    16451    aktor aktor_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.aktor
    ADD CONSTRAINT aktor_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.aktor DROP CONSTRAINT aktor_pkey;
       public                 postgres    false    220            �           2606    16470    film_aktor film_aktor_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY public.film_aktor
    ADD CONSTRAINT film_aktor_pkey PRIMARY KEY (film_id, aktor_id);
 D   ALTER TABLE ONLY public.film_aktor DROP CONSTRAINT film_aktor_pkey;
       public                 postgres    false    223    223            ~           2606    16460    film film_pkey 
   CONSTRAINT     L   ALTER TABLE ONLY public.film
    ADD CONSTRAINT film_pkey PRIMARY KEY (id);
 8   ALTER TABLE ONLY public.film DROP CONSTRAINT film_pkey;
       public                 postgres    false    222            x           2606    16444    genre genre_nama_key 
   CONSTRAINT     O   ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_nama_key UNIQUE (nama);
 >   ALTER TABLE ONLY public.genre DROP CONSTRAINT genre_nama_key;
       public                 postgres    false    218            z           2606    16442    genre genre_pkey 
   CONSTRAINT     N   ALTER TABLE ONLY public.genre
    ADD CONSTRAINT genre_pkey PRIMARY KEY (id);
 :   ALTER TABLE ONLY public.genre DROP CONSTRAINT genre_pkey;
       public                 postgres    false    218            �           2606    16488    pengguna pengguna_pkey 
   CONSTRAINT     T   ALTER TABLE ONLY public.pengguna
    ADD CONSTRAINT pengguna_pkey PRIMARY KEY (id);
 @   ALTER TABLE ONLY public.pengguna DROP CONSTRAINT pengguna_pkey;
       public                 postgres    false    225            �           2606    16490    pengguna pengguna_username_key 
   CONSTRAINT     ]   ALTER TABLE ONLY public.pengguna
    ADD CONSTRAINT pengguna_username_key UNIQUE (username);
 H   ALTER TABLE ONLY public.pengguna DROP CONSTRAINT pengguna_username_key;
       public                 postgres    false    225            �           2606    16499    penyewaan penyewaan_pkey 
   CONSTRAINT     V   ALTER TABLE ONLY public.penyewaan
    ADD CONSTRAINT penyewaan_pkey PRIMARY KEY (id);
 B   ALTER TABLE ONLY public.penyewaan DROP CONSTRAINT penyewaan_pkey;
       public                 postgres    false    227            �           2606    16476    film_aktor fk_film_aktor_aktor    FK CONSTRAINT     ~   ALTER TABLE ONLY public.film_aktor
    ADD CONSTRAINT fk_film_aktor_aktor FOREIGN KEY (aktor_id) REFERENCES public.aktor(id);
 H   ALTER TABLE ONLY public.film_aktor DROP CONSTRAINT fk_film_aktor_aktor;
       public               postgres    false    4732    223    220            �           2606    16471    film_aktor fk_film_aktor_film    FK CONSTRAINT     {   ALTER TABLE ONLY public.film_aktor
    ADD CONSTRAINT fk_film_aktor_film FOREIGN KEY (film_id) REFERENCES public.film(id);
 G   ALTER TABLE ONLY public.film_aktor DROP CONSTRAINT fk_film_aktor_film;
       public               postgres    false    223    222    4734            �           2606    16461    film fk_film_genre    FK CONSTRAINT     r   ALTER TABLE ONLY public.film
    ADD CONSTRAINT fk_film_genre FOREIGN KEY (genre_id) REFERENCES public.genre(id);
 <   ALTER TABLE ONLY public.film DROP CONSTRAINT fk_film_genre;
       public               postgres    false    218    4730    222            �           2606    16505    penyewaan fk_penyewaan_film    FK CONSTRAINT     y   ALTER TABLE ONLY public.penyewaan
    ADD CONSTRAINT fk_penyewaan_film FOREIGN KEY (film_id) REFERENCES public.film(id);
 E   ALTER TABLE ONLY public.penyewaan DROP CONSTRAINT fk_penyewaan_film;
       public               postgres    false    222    4734    227            �           2606    16500    penyewaan fk_penyewaan_pengguna    FK CONSTRAINT     �   ALTER TABLE ONLY public.penyewaan
    ADD CONSTRAINT fk_penyewaan_pengguna FOREIGN KEY (pengguna_id) REFERENCES public.pengguna(id);
 I   ALTER TABLE ONLY public.penyewaan DROP CONSTRAINT fk_penyewaan_pengguna;
       public               postgres    false    227    225    4738                   x������ � �      "      x������ � �      #      x������ � �            x������ � �      %      x������ � �      '      x������ � �     