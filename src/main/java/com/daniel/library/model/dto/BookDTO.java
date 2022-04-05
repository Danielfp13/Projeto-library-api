package com.daniel.library.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotBlank(message = "Campo author obrigatório.")
    private String author;

    @NotBlank(message = "Campo title obrigatório.")
    private String title;

    @NotBlank(message = "Campo isbn obrigatório.")
    private String isbn;

}
