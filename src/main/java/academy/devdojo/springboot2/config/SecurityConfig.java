package academy.devdojo.springboot2.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import academy.devdojo.springboot2.service.LocalUserDetailsService;
import lombok.RequiredArgsConstructor;

@EnableWebSecurity
/** Propriedade que abiliata o uso do @PreAuthorize */
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LocalUserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                /**
                 * Para protejer o app de tokens maliciosos, o Spring Security limita o acesso
                 * para realizar POSTS. No caso desabilitamos essa proteção mas uma maneira de
                 * manter a segurança é com o
                 * .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) on
                 * de cada requisição POST precisa de um token específico que é retornado nas
                 * requisições GET.
                 */
                .csrf().disable().authorizeRequests()
                // Atravéz do antMatchers podemos definir o nível de acesso de cada tipo de usuário;
                // É importante que a ordem seja do mais restrito ao menos.
                .antMatchers("/anime/admin/**").hasRole("ADMIN")
                .antMatchers("/anime/**").hasRole("USER")
                // Para qualquer requisição
                .anyRequest()
                // esteja autenticada
                .authenticated()
                // e
                .and()
                // o modo de autenticação é base 64
                .httpBasic()
                // e
                .and()
                // o modo de autenticação também é formulário
                .formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //auth.inMemoryAuthentication()
        //    .withUser("user").password(passwordEncoder.encode("user")).roles("USER")
        //    .and()
        //    .withUser("root").password(passwordEncoder.encode("root")).roles("ADMIN");
        /** Instanciamos o UserDetailsService para que o spring tenha o serviço específico para gerir os usuários */
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

}
