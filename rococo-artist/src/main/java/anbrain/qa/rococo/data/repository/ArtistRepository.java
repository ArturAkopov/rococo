package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.ArtistEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtistRepository extends JpaRepository<ArtistEntity, UUID> {

    Page<ArtistEntity> findByName(String name, PageRequest pageable);

    Page<ArtistEntity> findAll(Pageable pageable);
}