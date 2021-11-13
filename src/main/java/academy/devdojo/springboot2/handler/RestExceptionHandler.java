package academy.devdojo.springboot2.handler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ValidationException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.exception.BadRequestExceptionDetail;
import academy.devdojo.springboot2.exception.ValidationExceptionDetail;

/** Atravez dessa classe padronizamos as respostas de badrequests */
/** A anotação @ControllerAdvice diz que todos os controllers devem seguir o padrão */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler{
    /** A anotação @ExceptionHandler diz respeito a que tipo de handler estaremos construindo */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BadRequestExceptionDetail> handlerBadRequestException(BadRequestException bre){
        return new ResponseEntity<>(
            BadRequestExceptionDetail.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .title("Bad Requests Exception, check the Documentation")
            .details(bre.getMessage())
            .developerMessage(bre.getClass().getName())
            .build(), HttpStatus.BAD_REQUEST
        );
    }   

    /** Aquie estamaos validando os campos e entregando uma resposta customizada para esse erro */
    /** Método antigo */
    /*@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationExceptionDetail> handlerValidationExceptionDetail(MethodArgumentNotValidException manve){
        List<FieldError> fieldErrors = manve.getBindingResult().getFieldErrors();
        String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
        String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
            ValidationExceptionDetail.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .title("Not valid, check the field(s) below")
            .details(manve.getMessage())
            .developerMessage(manve.getClass().getName())
            .fields(fields)
            .fieldsMessage(fieldsMessage)
            .build(), HttpStatus.BAD_REQUEST
        );
    }*/

    /** Novo método sobrepondo o método específico */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String fields = fieldErrors.stream().map(FieldError::getField).collect(Collectors.joining(", "));
        String fieldsMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
            ValidationExceptionDetail.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .title("Not valid, check the field(s) below")
            .details(ex.getMessage())
            .developerMessage(ex.getClass().getName())
            .fields(fields)
            .fieldsMessage(fieldsMessage)
            .build(), HttpStatus.BAD_REQUEST
        );
    }
}
