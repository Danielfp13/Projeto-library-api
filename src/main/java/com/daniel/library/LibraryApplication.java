package com.daniel.library;

import com.daniel.library.model.service.EmailService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableScheduling
@SpringBootApplication

public class LibraryApplication {

//   @Autowired
//  private EmailService emailService;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

 //  @Bean
 //  public CommandLineRunner runner(){
 //      return args -> {
 //          List<String> emails = Arrays.asList("4beac634c7-ac8a61@inbox.mailtrap.io","daniel.si@outlook.com.br");
 //          emailService.sendMails("testando serviço de email",emails);
 //          System.out.println("Emails enviados");
 //      };
 //  }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

}


