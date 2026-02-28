package com.soundwave.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "artists")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String bio;
    private String imageUrl;
    private String genre;
    private Long monthlyListeners;

    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Album> albums = new ArrayList<>();

    @OneToMany(mappedBy = "artist")
    @Builder.Default
    private List<Song> songs = new ArrayList<>();
}
