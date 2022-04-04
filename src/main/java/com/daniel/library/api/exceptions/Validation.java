package com.daniel.library.api.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
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
