package academy.devdojo.springboot2.requests;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AnimePostRequestBody {
    @NotEmpty(message = "O campo \"nome\" não pode ser vazio")
    @NotNull(message = "O campo \"nome\" não pode ser null")
    private String name;
}
