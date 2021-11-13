package academy.devdojo.springboot2.repository;

import java.util.List;
import java.util.Optional;

import javax.validation.ConstraintViolationException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import academy.devdojo.springboot2.domain.Anime;

@DataJpaTest
public class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository repository;

    @Test
    @DisplayName("Save persist anime when successful")
    public void save_PersistAnime_WhenSuccessful() {
        Anime animeToBeSaved = this.createAnime();
        Anime animeSaved = this.repository.save(animeToBeSaved);
        Assertions.assertThat(animeSaved).isNotNull();
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Save update anime when successful")
    public void save_UpdateAnime_WhenSuccessful() {
        Anime animeToBeSaved = this.createAnime();
        Anime animeSaved = this.repository.save(animeToBeSaved);

        String name = "Novo nome de teste";
        animeSaved.setName(name);

        Anime animeUpdated = this.repository.save(animeSaved);

        Assertions.assertThat(animeUpdated).isNotNull();
        Assertions.assertThat(animeUpdated.getId()).isNotNull().isEqualTo(animeSaved.getId());
        Assertions.assertThat(animeUpdated.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Delete anime when successful")
    public void delete_RemoveAnime_WhenSuccessful() {
        Anime animeToBeSaved = this.createAnime();

        Anime animeSaved = this.repository.save(animeToBeSaved);

        this.repository.delete(animeSaved);

        Optional<Anime> deletedAnimeOptional = this.repository.findById(animeSaved.getId());

        Assertions.assertThat(deletedAnimeOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by name return list of anime when successful")
    public void findByName_ReturnListOfAnime_WhenSuccessful() {
        Anime animeToBeSaved = this.createAnime();

        Anime animeSaved = this.repository.save(animeToBeSaved);

        String name = animeSaved.getName();

        List<Anime> animes = this.repository.findByName(name);

        Assertions.assertThat(animes).isNotEmpty();
        Assertions.assertThat(animes).contains(animeSaved);

    }

    @Test
    @DisplayName("Find by name return empty list of anime when no anime is found")
    public void findByName_ReturnEmptyListOfAnime_WhenAnimeIsNotFound() {
        List<Anime> animes = this.repository.findByName("");

        Assertions.assertThat(animes).isEmpty();

    }

    @Test
    @DisplayName("Throws ConstraintViolationException when name is empty or null")
    public void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {
        Anime anime = new Anime();
        Assertions.assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> this.repository.save(anime))
                .withMessageContaining("The anime name cannot be empty");
    }

    private Anime createAnime() {
        return Anime.builder().name("Anime para teste").build();
    }

}
