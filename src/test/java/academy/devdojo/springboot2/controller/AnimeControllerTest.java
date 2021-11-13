package academy.devdojo.springboot2.controller;

import java.util.List;

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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.DateUtil;

@ExtendWith(SpringExtension.class)
public class AnimeControllerTest {

    /**
     * Indica a classe que será testada, ou seja os mocks descritos serão aplicados
     * na instanciação da seguinte classe
     */
    @InjectMocks
    private AnimeController animeController;

    /** Anota o mock de cada dependência necessária da animeController */
    @Mock
    private AnimeService service;

    @Mock
    private DateUtil dateUtil;

    /** Definie o setup das classes que serão mockadas */
    @BeforeEach
    private void setup() {
        PageImpl<Anime> anime = new PageImpl<>(List.of(AnimeCreator.createToSaved()));
        /**
         * O ArgumentMatchers.any() serve para informar que qualquer valor que for
         * passado ao método listAll será aceito. O BDDMockito.when dirá que o retorno
         * será sempre o mesmo. Esse setup configura o a chmada do método listAll antes
         * da classe AnimeService ser instanciada
         */
        BDDMockito.when(this.service.listAll(ArgumentMatchers.any())).thenReturn(anime);
        BDDMockito.when(this.service.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(anime);
        BDDMockito.when(this.service.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
                .thenReturn(AnimeCreator.createToSaved());
    }

    @Test
    @DisplayName("List returns list of animes inside page object when successful")
    public void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createToSaved().getName();
        Page<Anime> list = animeController.list(null).getBody();
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("List returns list of animes when successful")
    public void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createToSaved().getName();
        Page<Anime> list = animeController.findByName(expectedName, null).getBody();
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("List returns anime when successful")
    public void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createToSaved().getId();
        Anime anime = animeController.findById(expectedId).getBody();
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

}
