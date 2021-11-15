package academy.devdojo.springboot2.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.util.AnimeUtil;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
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
        PageImpl<Anime> anime = new PageImpl<>(List.of(AnimeCreator.createAnimeToBeSaved()));
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
                .thenReturn(AnimeCreator.createValidAnime());
        /**
         * Podemose determinar o tipo de objeto que é válido. Nesse caso só será válido
         * se o parâmetro for um AnimePostRequestBody
         */
        BDDMockito.when(this.service.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        /**
         * Usamos o doNothing para indicar que nada será feito ao chamar o método. Isso
         * serve em casos de métodos com retorno void
         */
        BDDMockito.doNothing().when(this.service).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
        BDDMockito.doNothing().when(this.service).delete(ArgumentMatchers.anyLong());
    }

    @Test
    @DisplayName("List returns list of animes inside page object when successful")
    public void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        String expectedName = AnimeCreator.createAnimeToBeSaved().getName();
        Page<Anime> list = animeController.list(null).getBody();
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("List returns list of animes when successful")
    public void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        String expectedName = AnimeCreator.createAnimeToBeSaved().getName();
        Page<Anime> list = animeController.findByName(expectedName, null).getBody();
        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("FindByName returns empty list of animes when anime name is not found")
    public void findByName_ReturnsEmptyListOfAnimes_WhenAnimeNameIsNotFound() {
        BDDMockito.when(this.service.findByName(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        Page<Anime> list = animeController.findByName("", null).getBody();

        Assertions.assertThat(list).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("List returns anime when successful")
    public void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = AnimeCreator.createValidAnime().getId();
        Anime anime = animeController.findById(expectedId).getBody();
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Save returns new anime persisted when successful")
    public void save_ReturnsNewAnimePersisted_WhenSuccessful() {
        Anime anime = animeController.save(AnimeUtil.postBodyfromValidAnime()).getBody();
        Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace returns no content when successful")
    public void replace_ReturnsNoContent_WhenSuccessful() {
        /**
         * No caso em que o método não retorna nada podemos validar o http response
         * status se irá estoruar alguma exceção
         */
        Assertions.assertThatCode(() -> animeController.replace(AnimeUtil.putBodyfromValidAnime()))
                .doesNotThrowAnyException();
        ResponseEntity<Void> entity = animeController.replace(AnimeUtil.putBodyfromValidAnime());
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("replace returns BadRequestException when anime not found")
    public void replace_ReturnsBadRequestException_WhenAnimeNotFound() {
        BadRequestException expected = new BadRequestException("Anime not found");
        BDDMockito.doThrow(expected).when(this.service).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
        Exception thrown = assertThrows(Exception.class, () -> animeController.replace(AnimeUtil.putBodyfromValidAnime()));
        Assertions.assertThat(thrown.getMessage()).isEqualTo("Anime not found");
        Assertions.assertThat(thrown.getClass()).isEqualTo(BadRequestException.class);
    }

    @Test
    @DisplayName("delete returns no content when successful")
    public void delete_ReturnsNoContent_WhenSuccessful() {
        Assertions.assertThatCode(() -> animeController.delete(1l)).doesNotThrowAnyException();
        ResponseEntity<Void> entity = animeController.delete(1l);
        Assertions.assertThat(entity).isNotNull();
        Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
