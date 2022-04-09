package com.daniel.library.model.service;

import com.daniel.library.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class
ScheduleService {

    private static final String CRON_LATE_LOANS = "0 49 22 1/1 * ?";


    @Value("${application.mail.lateloans.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans() {
        List<Loan> allLateLoans = loanService.findAllLateLoans();
        List<String> mailsList = allLateLoans.stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());
        if(mailsList.isEmpty()){
            System.out.println("nenhum e-mail à enviar.");
        }else{
            emailService.sendMails(message, mailsList);
            System.out.println("Email enviado ok.");

        }
    }
}