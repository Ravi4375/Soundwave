package com.soundwave.controller;

import com.soundwave.model.Song;
import com.soundwave.repository.SongRepository;
import com.soundwave.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongRepository songRepository;
    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSong(@PathVariable Long id) {
        return songRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top-charts")
    public ResponseEntity<List<Song>> topCharts(@RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(recommendationService.getTopCharts(limit));
    }

    @GetMapping("/{id}/radio")
    public ResponseEntity<List<Song>> songRadio(@PathVariable Long id,
                                                  @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(recommendationService.getSongRadio(id, limit));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<Song>> byGenre(@PathVariable String genre) {
        return ResponseEntity.ok(songRepository.findByGenre(genre));
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<Void> recordPlay(@PathVariable Long id) {
        songRepository.findById(id).ifPresent(song -> {
            song.setPlayCount((song.getPlayCount() == null ? 0L : song.getPlayCount()) + 1);
            songRepository.save(song);
        });
        return ResponseEntity.ok().build();
    }
}
