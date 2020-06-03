package hu.bep.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreboardRepository extends JpaRepository<Word, Long> {
}
