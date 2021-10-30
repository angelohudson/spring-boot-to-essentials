package academy.devdojo.springboot2.repository;

import java.util.List;

import academy.devdojo.springboot2.domain.Anime;

/**
 * Local onde haverá as transações com o banco
 */
public interface AnimeRepository {
    public List<Anime> listAll();
}
