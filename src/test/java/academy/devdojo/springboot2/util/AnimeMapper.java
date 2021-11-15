package academy.devdojo.springboot2.util;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;

@Mapper
public interface AnimeMapper {

    AnimeMapper INSTANCE = Mappers.getMapper(AnimeMapper.class);

    public AnimePostRequestBody postBodyfromAnime(Anime anime);

    public AnimePutRequestBody putBodyfromAnime(Anime anime);

}