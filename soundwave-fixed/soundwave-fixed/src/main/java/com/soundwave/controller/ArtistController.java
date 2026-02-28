package com.soundwave.controller;

import com.soundwave.model.Album;
import com.soundwave.model.Artist;
import com.soundwave.model.Song;
import com.soundwave.repository.AlbumRepository;
import com.soundwave.repository.ArtistRepository;
import com.soundwave.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    @GetMapping
    public ResponseEntity<List<Artist>> getAll() {
        return ResponseEntity.ok(artistRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtist(@PathVariable Long id) {
        return artistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<List<Album>> getAlbums(@PathVariable Long id) {
        return ResponseEntity.ok(albumRepository.findByArtistId(id));
    }

    @GetMapping("/{id}/top-songs")
    public ResponseEntity<List<Song>> getTopSongs(@PathVariable Long id) {
        return ResponseEntity.ok(songRepository.findByArtist(id));
    }
}
