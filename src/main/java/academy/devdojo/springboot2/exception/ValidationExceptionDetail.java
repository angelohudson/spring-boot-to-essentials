package academy.devdojo.springboot2.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ValidationExceptionDetail extends ExceptionDetails {
    private final String fields;
    private final String fieldsMessage;
}