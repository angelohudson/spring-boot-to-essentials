package academy.devdojo.springboot2.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Component, @Service, @Repository:
 * 
 * Serve para indicar que a classe é um spring bean
 * 
 * @Component: Indicado para anotar um componente;
 * @Service: Indicado para anotar um serviço;
 * @Repository: Indicado em caso de suspeita de dengue (e anotar repositório)
 */
@Component
public class DateUtil {
    public String formatLocalDateTimeToDatabaseStyle(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
    }
}
