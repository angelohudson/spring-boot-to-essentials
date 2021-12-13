package academy.devdojo.springboot2.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AnimePostRequestBody {
    @NotEmpty(message = "O campo \"nome\" não pode ser vazio")
    @NotNull(message = "O campo \"nome\" não pode ser null")
    /** Definindo com o campo será apresentado no openApi */
    @Schema(description = "Anime name", example = "Naruto", required = true)
    private String name;
}
