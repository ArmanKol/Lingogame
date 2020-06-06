package hu.bep.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreboardRepository extends JpaRepository<Player, Long> {

    public Player save(Player player);
}
