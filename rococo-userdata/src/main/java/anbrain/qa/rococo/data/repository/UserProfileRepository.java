package anbrain.qa.rococo.data.repository;

import anbrain.qa.rococo.data.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, UUID> {

    Optional<UserProfileEntity> findByUsername(String username);

}
