package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.MuseumEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MuseumRepository extends JpaRepository<MuseumEntity, UUID> {

    Page<MuseumEntity> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM museum WHERE LOWER(title) LIKE LOWER(CONCAT('%', :title, '%'))", nativeQuery = true)
    List<MuseumEntity> findByTitleContainsIgnoreCase(@Param("title") String title);

    Optional<MuseumEntity> findByTitle(String title);
}
