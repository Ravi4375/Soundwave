package com.soundwave.controller;

import com.soundwave.model.Artist;
import com.soundwave.model.Song;
import com.soundwave.model.User;
import com.soundwave.repository.ArtistRepository;
import com.soundwave.repository.SongRepository;
import com.soundwave.repository.UserRepository;
import com.soundwave.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final RecommendationService recommendationService;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("displayName", user.getDisplayName());
        profile.put("email", user.getEmail());
        profile.put("avatarUrl", user.getAvatarUrl());
        profile.put("likedSongsCount", user.getLikedSongs().size());
        profile.put("playlistsCount", user.getPlaylists().size());
        profile.put("followedArtistsCount", user.getFollowedArtists().size());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<Song>> getRecommendations(@AuthenticationPrincipal UserDetails userDetails,
                                                           @RequestParam(defaultValue = "20") int limit) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(recommendationService.getPersonalizedRecommendations(user.getId(), limit));
    }

    @GetMapping("/liked-songs")
    public ResponseEntity<Set<Song>> getLikedSongs(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        return ResponseEntity.ok(user.getLikedSongs());
    }

    @PostMapping("/liked-songs/{songId}")
    public ResponseEntity<Void> likeSong(@PathVariable Long songId,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Song song = songRepository.findById(songId).orElseThrow();
        user.getLikedSongs().add(song);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/liked-songs/{songId}")
    public ResponseEntity<Void> unlikeSong(@PathVariable Long songId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        user.getLikedSongs().removeIf(s -> s.getId().equals(songId));
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/follow/{artistId}")
    public ResponseEntity<Void> followArtist(@PathVariable Long artistId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        Artist artist = artistRepository.findById(artistId).orElseThrow();
        user.getFollowedArtists().add(artist);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
