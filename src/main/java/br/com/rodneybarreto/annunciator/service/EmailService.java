package br.com.rodneybarreto.annunciator.service;

import br.com.rodneybarreto.annunciator.domain.dto.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String DEFAULT_EMAIL_FROM = "noreply@annunciator.dev";

    private final JavaMailSender javaMailSender;

    public void send(Email email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(Optional.ofNullable(email.from()).orElse(DEFAULT_EMAIL_FROM));
        message.setTo(email.to());
        message.setSubject(email.subject());
        message.setText(email.content());
        javaMailSender.send(message);
    }

}
