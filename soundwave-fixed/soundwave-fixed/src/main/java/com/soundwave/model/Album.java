package com.soundwave.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "albums")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String coverUrl;
    private LocalDate releaseDate;
    private String genre;

    @Enumerated(EnumType.STRING)
    private AlbumType type;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    @OrderBy("trackNumber ASC")
    @Builder.Default
    private List<Song> songs = new ArrayList<>();

    public enum AlbumType { ALBUM, SINGLE, EP }
}
