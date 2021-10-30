package academy.devdojo.springboot2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import academy.devdojo.springboot2.domain.Anime;

/**
 * Ideal é ter um serviço que faça o intermédio entre os modelos e as
 * controllers;
 */
@Service
public class AnimeService {

    private static List<Anime> lista;

    static {
        lista = new ArrayList<>(List.of(new Anime(1l, "Boku No Hero"), new Anime(2l, "Berserk")));
    }

    public List<Anime> listAll() {
        return lista;
    }

    public Anime findById(Long id) {
        return lista.stream().filter(a -> a.getId().equals(id)).findFirst()
                /**
                 * Desta forma podemos "vazar" a exceção deixando a resposta mais completa ao
                 * cliente
                 */
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anime not found"));
    }

    public Anime save(Anime anime) {
        anime.setId(ThreadLocalRandom.current().nextLong(3, 100000));
        lista.add(anime);
        return anime;
    }

    public void delete(Long id) {
        lista.remove(this.findById(id));
    }

    public void replace(Anime anime) {
        this.delete(anime.getId());
        lista.add(anime);
    }
}
