package com.example.demo.admin.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Data
@Entity
@Table(name = "film")
public class Film {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String judul;

   @Column(columnDefinition = "TEXT")
   private String deskripsi;

   private Integer tahunRilis;

   @Column(nullable = false)
   private Integer stok;

   private String coverUrl;

   @ManyToOne
   @JoinColumn(name = "genre_id")
   private Genre genre;

   @ManyToMany
   @JoinTable(
       name = "film_aktor",
       joinColumns = @JoinColumn(name = "film_id"),
       inverseJoinColumns = @JoinColumn(name = "aktor_id")
   )
   private Set<Aktor> actors;
}