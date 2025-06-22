package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

    Page<ArtistEntity> findByName(String name, PageRequest pageable);

    Page<ArtistEntity> findAll(Pageable pageable);

    @Query("SELECT a FROM ArtistEntity a WHERE LOWER(a.name) LIKE LOWER(concat('%', :name,'%'))")
    Page<ArtistEntity> findByNameContainsIgnoreCase(@Param("name") String name, Pageable pageable);
}