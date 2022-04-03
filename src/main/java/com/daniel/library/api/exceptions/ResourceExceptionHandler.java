package com.daniel.library.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ResourceExceptionHandler {
    /*
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiErros> handleValidadionException(MethodArgumentNotValidException ex) {
            BindingResult bindingResult = ex.getBindingResult();
            // List<ObjectError> allErros = bindingResult.getAllErrors();
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiErros(bindingResult));
        }
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardError> handleValidadionException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Validation errors = new Validation(LocalDateTime.now(), HttpStatus.UNPROCESSABLE_ENTITY.value()
                , "Erro de validação.", "Um ou mais campo(s) inválido(s).", request.getRequestURI());
        for(FieldError x : ex.getBindingResult().getFieldErrors()) {
            errors.addErrors(x.getField(),x.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
    }

}
