package academy.devdojo.springboot2.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * A anotação informa que a classe terá critérios e regras que serão processadas
 * antes do andamento da requisição. Ex: filtros, resolvers e etc.
 */
@Configuration
public class DevDojoWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * Podemos adicionar handler resolvers que intermediam as requisiçõs. Nesse
     * exemplo usamos para definir um paginamento padrão
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageable = new PageableHandlerMethodArgumentResolver();
        pageable.setFallbackPageable(PageRequest.of(0, 5));
        resolvers.add(pageable);
    }

}
