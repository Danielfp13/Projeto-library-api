package com.daniel.library.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class Validation extends StandardError {
    private List<FielMessage> errors = new ArrayList<>();

    public Validation(LocalDateTime timestamp, Integer status, String error, String message, String path) {
        super(timestamp, status, error, message, path);
    }

    public List<FielMessage> getErrors() {
        return errors;
    }

    public void addErrors(String field, String message) {
        this.errors.add(new FielMessage(field, message));
    }
}
