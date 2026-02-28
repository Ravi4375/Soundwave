package com.soundwave.controller;

import com.soundwave.model.Playlist;
import com.soundwave.model.Song;
import com.soundwave.model.User;
import com.soundwave.repository.PlaylistRepository;
import com.soundwave.repository.SongRepository;
import com.soundwave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    @GetMapping("/my")
    public ResponseEntity<List<Playlist>> getMyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(playlistRepository.findByOwnerId(user.getId()));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Playlist>> getFeatured() {
        return ResponseEntity.ok(playlistRepository.findByIsPublicTrue());
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Map<String, Object> body,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Playlist playlist = Playlist.builder()
                .name((String) body.get("name"))
                .description((String) body.get("description"))
                .isPublic(Boolean.TRUE.equals(body.get("isPublic")))
                .owner(user)
                .build();
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Playlist> addSong(@PathVariable Long id, @PathVariable Long songId) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow();
        Song song = songRepository.findById(songId).orElseThrow();
        playlist.getSongs().add(song);
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Playlist> removeSong(@PathVariable Long id, @PathVariable Long songId) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow();
        playlist.getSongs().removeIf(s -> s.getId().equals(songId));
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
