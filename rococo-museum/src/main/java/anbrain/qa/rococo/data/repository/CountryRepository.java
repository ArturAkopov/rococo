package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.CountryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
    Page<CountryEntity> findByNameIgnoreCase(String name, PageRequest pageable);
}
