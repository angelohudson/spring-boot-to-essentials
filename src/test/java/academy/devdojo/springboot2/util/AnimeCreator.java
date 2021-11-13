package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.domain.Anime;

public class AnimeCreator {

    public static Anime createToSaved() {
        return Anime.builder().name("Teste").build();
    }

}
