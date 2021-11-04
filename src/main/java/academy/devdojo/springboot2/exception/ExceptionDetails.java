package academy.devdojo.springboot2.exception;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class ExceptionDetails {
    protected String title;
    protected String details;
    protected String developerMessage;
    protected LocalDateTime timestamp;
    protected Integer status;
}
