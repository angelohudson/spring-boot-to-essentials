package academy.devdojo.springboot2.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimeUtil;

@ExtendWith(SpringExtension.class)
public class AnimeServiceTest {

    @InjectMocks
    AnimeService animeService;

    @Mock
    AnimeRepository repository;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> anime = new PageImpl<>(List.of(AnimeCreator.createAnimeToBeSaved()));
        BDDMockito.when(this.repository.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(anime);
        BDDMockito.when(this.repository.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(anime);
        BDDMockito.when(this.repository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(this.repository.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());
        BDDMockito.doNothing().when(this.repository).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("listAll returns list of animes inside page object when successful")
    public void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createAnimeToBeSaved().getName();
        Page<Anime> list = animeService.listAll(PageRequest.of(1, 1));
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns list of animes when successful")
    public void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createAnimeToBeSaved().getName();
        Page<Anime> list = animeService.findByName(expectedName, null);
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("findByName returns empty list of animes when anime name is not found")
    public void findByName_ReturnsEmptyListOfAnimes_WhenAnimeNameIsNotFound() {
        BDDMockito.when(this.repository.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Anime> list = animeService.findByName("", null);

        Assertions.assertThat(list).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns anime when successful")
    public void findByIdOrThrowBadRequestException_ReturnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeService.findByIdOrThrowBadRequestException(expectedId);
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("findByIdOrThrowBadRequestException returns BadRequestException when anime not found")
    public void findByIdOrThrowBadRequestException_ReturnsBadRequestException_WhenAnimeNotFound() {
        BDDMockito.when(this.repository.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.<Anime>empty());

        Exception thrown = assertThrows(Exception.class, () -> animeService.findByIdOrThrowBadRequestException(1l));

        Assertions.assertThat(thrown.getMessage()).isEqualTo("Anime not found");
        Assertions.assertThat(thrown.getClass()).isEqualTo(BadRequestException.class);
    }

    @Test
    @DisplayName("save returns new anime persisted when successful")
    public void save_ReturnsNewAnimePersisted_WhenSuccessful() {
        Anime anime = animeService.save(AnimeUtil.postBodyfromValidAnime());
        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace returns no content when successful")
    public void replace_ReturnsNoContent_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeService.replace(AnimeUtil.putBodyfromValidAnime()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("delete returns no content when successful")
    public void delete_ReturnsNoContent_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeService.delete(1l)).doesNotThrowAnyException();
    }

}
