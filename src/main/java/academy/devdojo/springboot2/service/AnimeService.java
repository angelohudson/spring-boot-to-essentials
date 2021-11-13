package academy.devdojo.springboot2.service;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
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

    public Page<Anime> listAll(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    public Anime findByIdOrThrowBadRequestException(Long id) {
        return this.repository.findById(id)
                /**
                 * Desta forma podemos "vazar" a exceção deixando a resposta mais completa ao
                 * cliente
                 */
                .orElseThrow(() -> new BadRequestException("Anime not found"));
    }

    /** A anotação @Transactional só permite que as transações sejam finalizadas quando o método for finalizado.
     * No caso se houver um erro o Spring faz um rollback nas transações.
     * Obs: apenas se o banco oferece suporte a transação.
     */
    @Transactional
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

    public Page<Anime> findByName(String name, Pageable pageable) {
        return this.repository.findByName(name, pageable);
    }
}
