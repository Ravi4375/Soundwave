package com.soundwave.repository;

import com.soundwave.model.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Song> searchByTitle(@Param("q") String query);

    @Query("SELECT s FROM Song s WHERE LOWER(s.genre) = LOWER(:genre) ORDER BY s.popularity DESC")
    List<Song> findByGenre(@Param("genre") String genre);

    @Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
    List<Song> findTopCharts(Pageable pageable);

    @Query("SELECT s FROM Song s WHERE s.artist.id = :artistId ORDER BY s.playCount DESC")
    List<Song> findByArtist(@Param("artistId") Long artistId);

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
            Pageable pageable
    );
}
