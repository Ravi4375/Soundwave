package com.soundwave.repository;

import com.soundwave.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByOwnerId(Long ownerId);
    List<Playlist> findByIsPublicTrue();

    @Query("SELECT p FROM Playlist p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%')) AND p.isPublic = true")
    List<Playlist> searchPublicByName(@Param("q") String query);
}
