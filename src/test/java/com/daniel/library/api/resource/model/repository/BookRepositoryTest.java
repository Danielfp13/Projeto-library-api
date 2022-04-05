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

import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id.")
    public void findByIdTest(){
        //cenario
        Book book = new Book(null, "Maria", "As Calunias", "12ws");
        entityManager.persist(book);
        //execuçaõ
        Optional<Book> foundBook = repository.findById(book.getId());
        //verificação
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest(){

        Book book = new Book(null, "Maria", "As Calunias", "12ws");

        Book savedBook = repository.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest(){

        Book book = new Book(null, "Maria", "As Calunias", "12ws");
        entityManager.persist(book);
        Book foundBook = entityManager.find( Book.class, book.getId() );

        repository.delete(foundBook);

        Book deletedBook = entityManager.find(Book.class, book.getId());
        Assertions.assertThat(deletedBook).isNull();

    }

}
