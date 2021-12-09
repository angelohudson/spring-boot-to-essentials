package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.User;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.UserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimeUtil;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {
        @Autowired
        /**
         * @Qualifier: determina qual o id do bean que será responsável por instanciar
         *             essa dependência
         */
        @Qualifier(value = "testRestTemplateRoleUser")
        private TestRestTemplate testRestTemplate;

        @Autowired
        private AnimeRepository animeRepository;

        @Autowired
        private UserRepository userRepository;

        /** @Lazy: determina que o bean será inicializado após a aplicação */
        @Lazy
        /** @TestConfiguration: tona a classe um bean */
        @TestConfiguration
        static class Config {
                /**
                 * @Bean(name = "id"): faz-se necessário destacar o bean com um name/id quando
                 *            possui mais de uma possível instância
                 */
                @Bean(name = "testRestTemplateRoleUser")
                public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
                        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                                        .rootUri("http://localhost:" + port).basicAuthentication("root", "root");
                        return new TestRestTemplate(restTemplateBuilder);
                }
        }

        @BeforeEach
        void setup() {
                this.userRepository.save(User.builder().username("root")
                                .password("{bcrypt}$2a$04$bQ120F37vJ8ahyX3Kh3CGeMMdz7YHFsXMe5gzDfRrIqXnMpip2n2a")
                                .authorities("ROLE_USER").build());
        }

        @Test
        @DisplayName("list returns list of anime inside page object when successful")
        void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
                Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

                String expectedName = savedAnime.getName();

                PageableResponse<Anime> animePage = testRestTemplate.exchange("/anime", HttpMethod.GET, null,
                                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                                }).getBody();

                Assertions.assertThat(animePage).isNotNull();

                Assertions.assertThat(animePage.toList()).isNotEmpty().hasSize(1);

                Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
        }

        @Test
        @DisplayName("findById returns anime when successful")
        void findById_ReturnsAnime_WhenSuccessful() {
                Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

                Long expectedId = savedAnime.getId();

                Anime anime = testRestTemplate.getForObject("/anime/{id}", Anime.class, expectedId);

                Assertions.assertThat(anime).isNotNull();

                Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);
        }

        @Test
        @DisplayName("findByName returns a list of anime when successful")
        void findByName_ReturnsListOfAnime_WhenSuccessful() {
                Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

                String expectedName = savedAnime.getName();

                String url = String.format("/anime/find?name=%s", expectedName);

                PageableResponse<Anime> page = testRestTemplate
                                .exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<PageableResponse<Anime>>() {
                                }).getBody();
                
                List<Anime> animes = page.getContent();

                Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

                Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);
        }

        @Test
        @DisplayName("findByName returns an empty list of anime when anime is not found")
        void findByName_ReturnsEmptyListOfAnime_WhenAnimeIsNotFound() {
                PageableResponse<Anime> animes = testRestTemplate.exchange("/anime/find?name=dbz", HttpMethod.GET, null,
                                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                                }).getBody();

                Assertions.assertThat(animes).isNotNull().isEmpty();

        }

        @Test
        @DisplayName("save returns anime when successful")
        void save_ReturnsAnime_WhenSuccessful() {
                AnimePostRequestBody animePostRequestBody = AnimeUtil.postBodyfromValidAnime();

                ResponseEntity<Anime> animeResponseEntity = testRestTemplate.exchange("/anime", HttpMethod.POST, new HttpEntity<>(animePostRequestBody), Anime.class);

                Assertions.assertThat(animeResponseEntity).isNotNull();
                Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                Assertions.assertThat(animeResponseEntity.getBody()).isNotNull();
                Assertions.assertThat(animeResponseEntity.getBody().getId()).isNotNull();

        }

        @Test
        @DisplayName("replace updates anime when successful")
        void replace_UpdatesAnime_WhenSuccessful() {
                Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

                savedAnime.setName("new name");

                ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/anime", HttpMethod.PUT,
                                new HttpEntity<>(savedAnime), Void.class);

                Assertions.assertThat(animeResponseEntity).isNotNull();

                Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("delete removes anime when successful")
        void delete_RemovesAnime_WhenSuccessful() {
                Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

                ResponseEntity<Void> animeResponseEntity = testRestTemplate.exchange("/anime/{id}", HttpMethod.DELETE,
                                null, Void.class, savedAnime.getId());

                Assertions.assertThat(animeResponseEntity).isNotNull();

                Assertions.assertThat(animeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

}
