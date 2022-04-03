package com.daniel.library.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StandardError implements Serializable {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
