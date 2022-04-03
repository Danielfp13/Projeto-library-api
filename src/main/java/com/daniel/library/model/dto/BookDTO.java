package com.daniel.library.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookDTO {

    @EqualsAndHashCode.Include
    private Long id;
    private String author;
    private String title;
    private String isbn;
}
