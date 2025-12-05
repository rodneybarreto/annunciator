package br.com.rodneybarreto.annunciator.service;

import br.com.rodneybarreto.annunciator.domain.dto.EmailRequest;
import br.com.rodneybarreto.annunciator.domain.entity.EmailEntity;
import br.com.rodneybarreto.annunciator.mapper.EmailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailMapper mapper;
    private final JavaMailSender sender;

    public void send(EmailRequest emailRequest) {
        EmailEntity emailEntity = mapper.toEntity(emailRequest);

        try {
            var message = new SimpleMailMessage();
            message.setFrom(emailEntity.getFrom());
            message.setTo(emailEntity.getTo());
            message.setSubject(emailEntity.getSubject());
            message.setText(emailEntity.getContent());

            sender.send(message);
        }
        catch (MailSendException e) {
            log.error("Error to sent e-mail", e);
        }
    }

}
