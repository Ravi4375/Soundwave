package com.soundwave.config;

import com.soundwave.model.*;
import com.soundwave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedData();
    }

    private void seedData() {
        // Demo user
        User demo = User.builder()
                .username("demo")
                .email("demo@soundwave.com")
                .password(passwordEncoder.encode("demo123"))
                .displayName("Demo User")
                .role(User.Role.USER)
                .build();
        userRepository.save(demo);

        // ---- Artists ----
        Artist artist1 = artistRepository.save(Artist.builder()
                .name("The Midnight")
                .genre("Synthwave")
                .bio("Synthwave duo Tim McEwan and Tyler Lyle from LA.")
                .monthlyListeners(2500000L)
                .imageUrl("https://picsum.photos/seed/artist1/400/400")
                .build());

        Artist artist2 = artistRepository.save(Artist.builder()
                .name("Billie Eilish")
                .genre("Pop")
                .bio("Grammy-winning pop sensation.")
                .monthlyListeners(45000000L)
                .imageUrl("https://picsum.photos/seed/artist2/400/400")
                .build());

        Artist artist3 = artistRepository.save(Artist.builder()
                .name("Daft Punk")
                .genre("Electronic")
                .bio("Legendary French electronic duo.")
                .monthlyListeners(20000000L)
                .imageUrl("https://picsum.photos/seed/artist3/400/400")
                .build());

        Artist artist4 = artistRepository.save(Artist.builder()
                .name("Tame Impala")
                .genre("Psychedelic Rock")
                .bio("Kevin Parker's psychedelic rock project.")
                .monthlyListeners(18000000L)
                .imageUrl("https://picsum.photos/seed/artist4/400/400")
                .build());

        // ---- Albums ----
        Album album1 = albumRepository.save(Album.builder()
                .title("Monsters")
                .artist(artist1).type(Album.AlbumType.ALBUM)
                .releaseDate(LocalDate.of(2022, 7, 15))
                .genre("Synthwave")
                .coverUrl("https://picsum.photos/seed/album1/300/300")
                .build());

        Album album2 = albumRepository.save(Album.builder()
                .title("Happier Than Ever")
                .artist(artist2).type(Album.AlbumType.ALBUM)
                .releaseDate(LocalDate.of(2021, 7, 30))
                .genre("Pop")
                .coverUrl("https://picsum.photos/seed/album2/300/300")
                .build());

        Album album3 = albumRepository.save(Album.builder()
                .title("Random Access Memories")
                .artist(artist3).type(Album.AlbumType.ALBUM)
                .releaseDate(LocalDate.of(2013, 5, 17))
                .genre("Electronic")
                .coverUrl("https://picsum.photos/seed/album3/300/300")
                .build());

        Album album4 = albumRepository.save(Album.builder()
                .title("Currents")
                .artist(artist4).type(Album.AlbumType.ALBUM)
                .releaseDate(LocalDate.of(2015, 7, 17))
                .genre("Psychedelic Rock")
                .coverUrl("https://picsum.photos/seed/album4/300/300")
                .build());

        // ---- Songs ----
        List<Song> songs = List.of(
            songRepository.save(Song.builder().title("Monsters").artist(artist1).album(album1).trackNumber(1)
                .durationSeconds(254).genre("Synthwave").playCount(5200000L).popularity(87.0)
                .energy(0.75).danceability(0.65).valence(0.72).tempo(120.0).acousticness(0.1)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("Los Angeles").artist(artist1).album(album1).trackNumber(2)
                .durationSeconds(233).genre("Synthwave").playCount(3100000L).popularity(78.0)
                .energy(0.70).danceability(0.68).valence(0.65).tempo(115.0).acousticness(0.12)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("Happier Than Ever").artist(artist2).album(album2).trackNumber(1)
                .durationSeconds(298).genre("Pop").playCount(90000000L).popularity(96.0)
                .energy(0.38).danceability(0.52).valence(0.24).tempo(95.0).acousticness(0.6)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("NDA").artist(artist2).album(album2).trackNumber(2)
                .durationSeconds(192).genre("Pop").playCount(55000000L).popularity(89.0)
                .energy(0.42).danceability(0.60).valence(0.30).tempo(100.0).acousticness(0.5)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("Get Lucky").artist(artist3).album(album3).trackNumber(1)
                .durationSeconds(369).genre("Electronic").playCount(300000000L).popularity(98.0)
                .energy(0.85).danceability(0.90).valence(0.95).tempo(116.0).acousticness(0.05)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("Instant Crush").artist(artist3).album(album3).trackNumber(2)
                .durationSeconds(337).genre("Electronic").playCount(150000000L).popularity(92.0)
                .energy(0.68).danceability(0.78).valence(0.55).tempo(112.0).acousticness(0.15)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("The Less I Know The Better").artist(artist4).album(album4).trackNumber(1)
                .durationSeconds(216).genre("Psychedelic Rock").playCount(700000000L).popularity(99.0)
                .energy(0.80).danceability(0.82).valence(0.88).tempo(116.0).acousticness(0.08)
                .audioUrl("").build()),
            songRepository.save(Song.builder().title("Let It Happen").artist(artist4).album(album4).trackNumber(2)
                .durationSeconds(467).genre("Psychedelic Rock").playCount(400000000L).popularity(97.0)
                .energy(0.76).danceability(0.76).valence(0.72).tempo(118.0).acousticness(0.06)
                .audioUrl("").build())
        );

        // ---- Featured Playlist ----
        Playlist featured = Playlist.builder()
                .name("SoundWave Essentials")
                .description("The best tracks curated for you")
                .isPublic(true)
                .owner(demo)
                .songs(songs)
                .build();
        playlistRepository.save(featured);

        Playlist chillVibes = Playlist.builder()
                .name("Chill Vibes")
                .description("Perfect for relaxing and unwinding")
                .isPublic(true)
                .owner(demo)
                .songs(songs.subList(2, 5))
                .build();
        playlistRepository.save(chillVibes);

        System.out.println("✅ SoundWave: Sample data seeded successfully!");
        System.out.println("   Login: demo / demo123");
    }
}
