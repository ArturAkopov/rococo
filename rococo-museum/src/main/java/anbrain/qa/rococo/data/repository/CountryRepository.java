package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
    Optional<CountryEntity> findByName(String name);
}
