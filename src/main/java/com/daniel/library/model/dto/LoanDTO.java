package com.daniel.library.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoanDTO {

    private Long id;

    @NotBlank(message = "Campo isbn obrigatório.")
    private String isbn;

    @NotBlank(message = "Campo customer obrigatório.")
    private String customer;

    @NotBlank(message = "Campo e-mail obrigatório.")
    private String email;

    private BookDTO book;
}