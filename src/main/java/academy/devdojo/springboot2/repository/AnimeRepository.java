package academy.devdojo.springboot2.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import academy.devdojo.springboot2.domain.Anime;

/**
 * Local onde haverá as transações com o banco
 */
public interface AnimeRepository extends JpaRepository<Anime, Long> {

    List<Anime> findByName(String name);

    Page<Anime> findByName(String name, Pageable pageable);

}
