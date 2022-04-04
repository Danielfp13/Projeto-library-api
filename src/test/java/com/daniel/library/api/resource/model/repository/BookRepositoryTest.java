package com.daniel.library.api.resource.model.repository;

import com.daniel.library.model.entity.Book;
import com.daniel.library.model.repository.BookRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeira quando existir um livro na base de dados com isbn informado.")
    public void returnTrueWhenIsbnExists(){

        Book book = new Book(null, "Fulano", "As aventuras", "54f4d");
        entityManager.persist(book);

        boolean exist =  repository.existsByIsbn(book.getIsbn());
        Assertions.assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando não existir um livro na base de dados com isbn informado.")
    public void returnFalseWhenIsbnExists(){

        String isbn ="1234";

        boolean exist =  repository.existsByIsbn(isbn);
        Assertions.assertThat(exist).isFalse();
    }

}
