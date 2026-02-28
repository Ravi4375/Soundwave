package com.soundwave.controller;

import com.soundwave.model.Playlist;
import com.soundwave.model.Song;
import com.soundwave.model.User;
import com.soundwave.repository.PlaylistRepository;
import com.soundwave.repository.SongRepository;
import com.soundwave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.server.ResponseStatusException;
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

    private User requireUser(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Playlist getOwnedPlaylist(Long id, User user) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist not found"));

        if (playlist.getOwner() == null || !playlist.getOwner().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only modify your own playlists");
        }
        return playlist;
    }

    @GetMapping("/my")
    public ResponseEntity<List<Playlist>> getMyPlaylists(@AuthenticationPrincipal UserDetails userDetails) {
        User user = requireUser(userDetails);
        return ResponseEntity.ok(playlistRepository.findByOwnerId(user.getId()));
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Playlist>> getFeatured() {
        return ResponseEntity.ok(playlistRepository.findByIsPublicTrue());
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody Map<String, Object> body,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        User user = requireUser(userDetails);
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Playlist name is required");
        }

        Playlist playlist = Playlist.builder()
                .name(name)
                .description((String) body.get("description"))
                .isPublic(Boolean.TRUE.equals(body.get("isPublic")))
                .owner(user)
                .build();
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @PostMapping("/{id}/songs/{songId}")
    public ResponseEntity<Playlist> addSong(@PathVariable Long id,
                                            @PathVariable Long songId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = requireUser(userDetails);
        Playlist playlist = getOwnedPlaylist(id, user);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found"));

        boolean alreadyAdded = playlist.getSongs().stream().anyMatch(s -> s.getId().equals(songId));
        if (!alreadyAdded) {
            playlist.getSongs().add(song);
        }

        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @DeleteMapping("/{id}/songs/{songId}")
    public ResponseEntity<Playlist> removeSong(@PathVariable Long id,
                                               @PathVariable Long songId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = requireUser(userDetails);
        Playlist playlist = getOwnedPlaylist(id, user);
        playlist.getSongs().removeIf(s -> s.getId().equals(songId));
        return ResponseEntity.ok(playlistRepository.save(playlist));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = requireUser(userDetails);
        Playlist playlist = getOwnedPlaylist(id, user);
        playlistRepository.delete(playlist);
        return ResponseEntity.noContent().build();
    }
}
