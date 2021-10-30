package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("anime")
/** Pode-se exibir logs com log.<logMethod>() */
@Log4j2
/** Cria um construtor que instancia todos as propriedades final */
@RequiredArgsConstructor
public class AnimeController {
    private final AnimeService animeService;
    private final DateUtil dateUtil;

    @GetMapping
    public ResponseEntity<List<Anime>> list() {
        log.info(this.dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.animeService.findById(id));
    }

    /**
     * Para posts o retorno varia de acordo com o projeto. Nesse caso retornaremos o
     * objeto inserido inteiro. O código é 201 (criado)
     */
    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody Anime anime) {
        return new ResponseEntity<Anime>(this.animeService.save(anime), HttpStatus.CREATED);
    }

    /**
     * O put e o delete devem ser idempotentes (segundo a RFC 7231), o que significa
     * que os métodos não podem ter resultados diferentes após a aplicação inicial.
     * Por exemplo, ao deletar um registro, qualquer retorno posterior deve retornar
     * um 404.
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.animeService.delete(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Void> replace(@RequestBody Anime anime) {
        this.animeService.replace(anime);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
