package com.soundwave.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "songs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Integer durationSeconds;
    private Integer trackNumber;
    private String audioUrl;
    private String genre;
    private Long playCount;
    private Double popularity; // 0-100 score for recommendations

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @ElementCollection
    @CollectionTable(name = "song_tags", joinColumns = @JoinColumn(name = "song_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    // Audio features for recommendation engine
    private Double energy;       // 0.0 - 1.0
    private Double danceability; // 0.0 - 1.0
    private Double valence;      // 0.0 - 1.0 (mood/positivity)
    private Double tempo;        // BPM
    private Double acousticness; // 0.0 - 1.0
}
