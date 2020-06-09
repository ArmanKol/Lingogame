package hu.bep.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScoreboardRepository extends JpaRepository<Player, Long> {

    public Player save(Player player);

    @Query(value = "select id, playername, totalscore from player order by totalscore desc;", nativeQuery = true)
    public List<Player> findAll();

    public int deleteByPlayerName(String playerName);
}
