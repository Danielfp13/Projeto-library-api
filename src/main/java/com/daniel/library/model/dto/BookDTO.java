package com.daniel.library.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookDTO {

    @EqualsAndHashCode.Include
    private Long id;
    @NotBlank
    private String author;
    @NotBlank
    private String title;
    @NotBlank
    private String isbn;
}
