package com.soundwave.repository;

import com.soundwave.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;

// ===== UserRepository =====
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

// ===== SongRepository =====
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Song> searchByTitle(@Param("q") String query);

    @Query("SELECT s FROM Song s WHERE LOWER(s.genre) = LOWER(:genre) ORDER BY s.popularity DESC")
    List<Song> findByGenre(@Param("genre") String genre);

    @Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
    List<Song> findTopCharts(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.artist.id = :artistId ORDER BY s.playCount DESC")
    List<Song> findByArtist(@Param("artistId") Long artistId);

    // Recommendation: find similar songs by audio features
    @Query("""
        SELECT s FROM Song s
        WHERE s.id != :songId
        AND s.genre = :genre
        AND ABS(s.energy - :energy) < 0.2
        AND ABS(s.danceability - :danceability) < 0.2
        ORDER BY s.popularity DESC
        """)
    List<Song> findSimilarSongs(
        @Param("songId") Long songId,
        @Param("genre") String genre,
        @Param("energy") Double energy,
        @Param("danceability") Double danceability,
        org.springframework.data.domain.Pageable pageable
    );
}

// ===== ArtistRepository =====
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    @Query("SELECT a FROM Artist a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Artist> searchByName(@Param("q") String query);

    List<Artist> findByGenre(String genre);
}

// ===== AlbumRepository =====
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    @Query("SELECT a FROM Album a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Album> searchByTitle(@Param("q") String query);

    List<Album> findByArtistId(Long artistId);
}

// ===== PlaylistRepository =====
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwnerId(Long ownerId);
    List<Playlist> findByIsPublicTrue();

    @Query("SELECT p FROM Playlist p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) AND p.isPublic = true")
    List<Playlist> searchPublicByName(@Param("q") String query);
}
