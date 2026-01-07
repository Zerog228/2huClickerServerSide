package me.zink.clicker.repo;

import me.zink.clicker.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface PlayerRepo extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);

    Boolean existsByUsername(String username);
}
