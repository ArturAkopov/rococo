package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.PaintingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface PaintingRepository extends JpaRepository<PaintingEntity, UUID> {

    Page<PaintingEntity> findAll(Pageable pageable);

    @Query("SELECT p FROM PaintingEntity p WHERE p.artistId = :artistId")
    Page<PaintingEntity> findByArtistId(@Param("artistId") UUID artistId, Pageable pageable);

    @Query("SELECT p FROM PaintingEntity p WHERE p.museumId = :museumId")
    Page<PaintingEntity> findByMuseumId(@Param("museumId") UUID museumId, Pageable pageable);

    @Query("SELECT p FROM PaintingEntity p WHERE LOWER(p.title) LIKE LOWER(concat('%', :title, '%'))")
    Page<PaintingEntity> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
}
