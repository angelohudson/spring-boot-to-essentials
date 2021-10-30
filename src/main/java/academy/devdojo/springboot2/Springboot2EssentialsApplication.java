package academy.devdojo.springboot2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A classe que inicia a aplicação;
 * Deve estar localizada na raiz da aplicação;
 * Se não estiver na raiz deve-se anotar com @ComponentScan(basePackages="pacote.raiz")
 */
/**
 * Algumas anotações podem ser usadas nessa sessão de aplicação
 * @ComponentScan: Indica a nescessidade de conferir os componentes e rotas antes do start;
 * @EnableAutoConfiguration: define configurações essenciais para o spring;
 * @Configuration: serve para trabalhar com filtros de segurança;
 * @SpringBootApplication: engloba o @ComponentScan, @EnableAutoConfiguration e @Configuration;
 */
@SpringBootApplication
public class Springboot2EssentialsApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springboot2EssentialsApplication.class, args);
	}

}
