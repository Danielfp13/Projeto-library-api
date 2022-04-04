package com.daniel.library.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FielMessage {
    private String field;
    private String message;
}
