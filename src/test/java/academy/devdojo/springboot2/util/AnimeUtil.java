package academy.devdojo.springboot2.util;

import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;

public class AnimeUtil {
    public static AnimePostRequestBody postBodyfromValidAnime() {
        return AnimeMapper.INSTANCE.postBodyfromAnime(AnimeCreator.createValidAnime());
    }

    public static AnimePutRequestBody putBodyfromValidAnime() {
        return AnimeMapper.INSTANCE.putBodyfromAnime(AnimeCreator.createValidUpdatedAnime());
    }
}