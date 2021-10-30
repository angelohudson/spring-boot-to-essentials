package academy.devdojo.springboot2.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.mapper.AnimeMapper;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import lombok.RequiredArgsConstructor;

/**
 * Ideal é ter um serviço que faça o intermédio entre os modelos e as
 * controllers;
 */
@Service
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository repository;

    public List<Anime> listAll() {
        return this.repository.findAll();
    }

    public Anime findByIdOrThrowBadRequestException(Long id) {
        return this.repository.findById(id)
                /**
                 * Desta forma podemos "vazar" a exceção deixando a resposta mais completa ao
                 * cliente
                 */
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found"));
    }

    public Anime save(AnimePostRequestBody animeBody) {
        Anime anime = AnimeMapper.INSTANCE.toAnime(animeBody);
        return this.repository.save(anime);
    }

    public void delete(Long id) {
        this.repository.delete(this.findByIdOrThrowBadRequestException(id));
    }

    public void replace(AnimePutRequestBody animeBody) {
        this.findByIdOrThrowBadRequestException(animeBody.getId());
        Anime anime = AnimeMapper.INSTANCE.toAnime(animeBody);
        anime.setId(animeBody.getId());
        this.repository.save(anime);
    }
}
