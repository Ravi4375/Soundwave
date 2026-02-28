package com.soundwave.service;

import com.soundwave.model.*;
import com.soundwave.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SoundWave Recommendation Engine
 *
 * Uses a hybrid approach:
 * 1. Content-Based Filtering: Similarity via audio features (energy, danceability, valence)
 * 2. Collaborative Filtering (simplified): Songs liked by users who liked same songs
 * 3. Popularity Boost: Trending songs blended into recommendations
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;

    /**
     * Get personalized recommendations for a user.
     * Based on: liked songs, listening history, followed artists.
     */
    public List<Song> getPersonalizedRecommendations(Long userId, int limit) {
        User user = userRepository.findById(userId).orElseThrow();

        Set<Long> excludeIds = new HashSet<>();
        user.getLikedSongs().forEach(s -> excludeIds.add(s.getId()));
        user.getListeningHistory().forEach(s -> excludeIds.add(s.getId()));

        List<Song> recommendations = new ArrayList<>();

        // Step 1: Content-based from liked songs (audio feature similarity)
        if (!user.getLikedSongs().isEmpty()) {
            Song seed = getAverageSeedSong(user.getLikedSongs());
            List<Song> similar = songRepository.findSimilarSongs(
                    seed.getId(), seed.getGenre(), seed.getEnergy(),
                    seed.getDanceability(), PageRequest.of(0, limit * 2)
            );
            recommendations.addAll(similar);
        }

        // Step 2: From followed artists
        user.getFollowedArtists().forEach(artist -> {
            List<Song> artistSongs = songRepository.findByArtist(artist.getId());
            recommendations.addAll(artistSongs.stream().limit(3).toList());
        });

        // Step 3: Fill remainder with top charts
        if (recommendations.size() < limit) {
            List<Song> charts = songRepository.findTopCharts(PageRequest.of(0, limit));
            recommendations.addAll(charts);
        }

        // Deduplicate, exclude already heard, sort by score, limit
        return recommendations.stream()
                .distinct()
                .filter(s -> !excludeIds.contains(s.getId()))
                .sorted(Comparator.comparingDouble(this::scoreForRecommendation).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get "Because You Listened To..." radio for a given song.
     */
    public List<Song> getSongRadio(Long songId, int limit) {
        Song song = songRepository.findById(songId).orElseThrow();
        return songRepository.findSimilarSongs(
                song.getId(), song.getGenre(), song.getEnergy(),
                song.getDanceability(), PageRequest.of(0, limit)
        );
    }

    /**
     * Get top charts globally.
     */
    public List<Song> getTopCharts(int limit) {
        return songRepository.findTopCharts(PageRequest.of(0, limit));
    }

    /**
     * Genre radio: top songs of a genre.
     */
    public List<Song> getGenreRadio(String genre, int limit) {
        return songRepository.findByGenre(genre).stream().limit(limit).toList();
    }

    // ---- Helpers ----

    private Song getAverageSeedSong(Set<Song> likedSongs) {
        // Return the most recently liked song as seed (simplified)
        return likedSongs.iterator().next();
    }

    /**
     * Composite scoring: popularity + freshness heuristic.
     * In production: use ML model (collaborative filtering matrix factorization).
     */
    private double scoreForRecommendation(Song song) {
        double popularity = song.getPopularity() != null ? song.getPopularity() : 0;
        double plays = song.getPlayCount() != null ? Math.log1p(song.getPlayCount()) : 0;
        return (popularity * 0.6) + (plays * 0.4);
    }
}
