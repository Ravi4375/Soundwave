package com.soundwave.controller;

import com.soundwave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        Map<String, Object> results = new HashMap<>();
        results.put("songs", songRepository.searchByTitle(q));
        results.put("artists", artistRepository.searchByName(q));
        results.put("albums", albumRepository.searchByTitle(q));
        results.put("playlists", playlistRepository.searchPublicByName(q));
        return ResponseEntity.ok(results);
    }
}
