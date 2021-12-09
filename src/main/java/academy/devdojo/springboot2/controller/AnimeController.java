package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import javax.validation.Valid;

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

    /**
     * Page (pacote springframework.data.domain) é o padrão do spring para
     * paginação.
     */
    /**
     * Pageable (pacote springframework.data.domain) captura os dados de paginação
     * da request. Por exemplo os parametros size, sort e page
     */
    @GetMapping
    public ResponseEntity<Page<Anime>> list(@ParameterObject Pageable pageable) {
        log.info(this.dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return ResponseEntity.ok(animeService.listAll(pageable));
    }

    @GetMapping("find")
    public ResponseEntity<Page<Anime>> findByName(@RequestParam(name = "name") String name, Pageable pageable) {
        return ResponseEntity.ok(animeService.findByName(name, pageable));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.animeService.findByIdOrThrowBadRequestException(id));
    }

    /** @AuthenticationPrincipal instancia os dados do usuário logado */
    @GetMapping(path = "/user/{id}")
    public ResponseEntity<Anime> findById(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(this.animeService.findByIdOrThrowBadRequestException(id));
    }

    /**
     * Para posts o retorno varia de acordo com o projeto. Nesse caso retornaremos o
     * objeto inserido inteiro. O código é 201 (criado)
     */
    /**
     * @Valid retorna error caso algum critério anotado no dto não seja preenchido
     */
    @PostMapping
    /** Verifica se o usuário logado tem o acesso ADMIN, se não retorna 403 */
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Anime> save(@RequestBody @Valid AnimePostRequestBody animeBody) {
        return new ResponseEntity<Anime>(this.animeService.save(animeBody), HttpStatus.CREATED);
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
    public ResponseEntity<Void> replace(@RequestBody AnimePutRequestBody animeBody) {
        this.animeService.replace(animeBody);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
