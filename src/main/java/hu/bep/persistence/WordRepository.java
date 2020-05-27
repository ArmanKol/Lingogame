package hu.bep.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    public List<Word> findAll();

    @Query(value = "select MIN(id) from word;", nativeQuery = true)
    public int getMinID();

    @Query(value = "select MAX(id) from word;", nativeQuery = true)
    public int getMaxID();

    @Query(value = "select wd.id, wd.word from word wd where wd.id = :id", nativeQuery = true)
    public Word findByID(@Param("id") int id);
}
