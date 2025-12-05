package br.com.rodneybarreto.annunciator.mapper;

import br.com.rodneybarreto.annunciator.domain.dto.EmailRequest;
import br.com.rodneybarreto.annunciator.domain.entity.EmailEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EmailMapper {

    private static final String DEFAULT_EMAIL_FROM = "no_reply@annunciator.com.br";

    public EmailEntity toEntity(EmailRequest emailRequest) {
        return EmailEntity.builder()
                .from(Optional.ofNullable(emailRequest.from()).orElse(DEFAULT_EMAIL_FROM))
                .to(emailRequest.to())
                .subject(emailRequest.subject())
                .content(emailRequest.content())
                .sentDate(LocalDateTime.now())
                .build();
    }

}
