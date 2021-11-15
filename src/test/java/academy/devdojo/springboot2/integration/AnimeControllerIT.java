package academy.devdojo.springboot2.integration;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimeMapper;
import academy.devdojo.springboot2.util.AnimeUtil;
import academy.devdojo.springboot2.wrapper.PageableResponse;

/**
 * Testes de integaração irão ajudar a validar o projeto de forma geral em uma
 * configuração parecida com a em produção. Dessa forma testamos todas as etapas
 * das operações desde a consulta no banco até sua exibição.
 */
/**
 * @SpringBootTest e @AutoConfigureTestDatabase: indicando que a classe será
 *                 usada para testes porém podendo iniciar o serviço e o banco
 *                 de teste (H2)
 */
/**
 * @AutoConfigureTestDatabase: também instancia um banco para teste assim como
 *                             o @DataJpaTest, o @AutoConfigureTestDatabase não
 *                             reinstancia o banco para cada método.
 */
/**
 * @DirtiesContext(classMode =
 *                           DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD):
 *                           Informa que antes de cada execução de método as
 *                           configurações devem ser refeitas
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnimeControllerIT {

    /** Usaremos o TestRestTemplate para ter a perspectiva de um cliente externo */
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("List returns list of animes inside page object when successful")
    public void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime anime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Page<Anime> list = testRestTemplate.exchange("/anime", HttpMethod.GET, null,
                /**
                 * ParameterizedTypeReference instancia a paginação e o PageableResponse é o
                 * paginador local para anime
                 */
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(list).isNotNull();
        Assertions.assertThat(list.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(list.toList().get(0)).isEqualTo(anime);
    }

    @Test
    @DisplayName("List returns list of animes when successful")
    public void findByName_ReturnsListOfAnimes_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();

        String url = String.format("/anime/find?name=%s", expectedName);

        Page<Anime> animePage = testRestTemplate
                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull();
        Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);
        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
    }

    @Test
    @DisplayName("FindByName returns empty list of animes when anime name is not found")
    public void findByName_ReturnsEmptyListOfAnimes_WhenAnimeNameIsNotFound() {
        animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String url = String.format("/anime/find?name=%s", "");

        Page<Anime> animePage = testRestTemplate
                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("List returns anime when successful")
    public void findById_ReturnsAnime_WhenSuccessful() {
        Long expectedId = animeRepository.save(AnimeCreator.createAnimeToBeSaved()).getId();
        Anime anime = testRestTemplate.getForEntity("/anime/{id}", Anime.class, expectedId).getBody();
        Assertions.assertThat(anime).isNotNull();
        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
    }

    @Test
    @DisplayName("Save returns new anime persisted when successful")
    public void save_ReturnsNewAnimePersisted_WhenSuccessful() {
        ResponseEntity<Anime> response = testRestTemplate.postForEntity("/anime", AnimeUtil.postBodyfromValidAnime(),
                Anime.class);

        Assertions.assertThat(response.getStatusCode()).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
    }

    @Test
    @DisplayName("replace returns no content when successful")
    public void replace_ReturnsNoContent_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("Novo nome");
        AnimePutRequestBody putBody = AnimeMapper.INSTANCE.putBodyfromAnime(savedAnime);

        ResponseEntity<Void> response = testRestTemplate.exchange("/anime", HttpMethod.PUT,
                new HttpEntity<AnimePutRequestBody>(putBody), Void.class);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Anime> updatedAnime = animeRepository.findById(savedAnime.getId());

        Assertions.assertThat(updatedAnime).isPresent();
        Assertions.assertThat(updatedAnime.get().getId()).isEqualTo(savedAnime.getId());
        Assertions.assertThat(updatedAnime.get().getName()).isNotEqualTo(AnimeCreator.createAnimeToBeSaved());
    }

    @Test
    @DisplayName("delete returns no content when successful")
    public void delete_ReturnsNoContent_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> response = testRestTemplate.exchange("/anime/{id}", HttpMethod.DELETE, null, Void.class,
                savedAnime.getId());

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Optional<Anime> updatedAnime = animeRepository.findById(savedAnime.getId());
        Assertions.assertThat(updatedAnime).isEmpty();
    }

}
