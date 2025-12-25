package br.com.rodneybarreto.annunciator.service;

import br.com.rodneybarreto.annunciator.domain.dto.EmailRequest;
import br.com.rodneybarreto.annunciator.domain.entity.EmailEntity;
import br.com.rodneybarreto.annunciator.mapper.EmailMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailMapper mapper;
    private final JavaMailSender sender;
    private final RedisEventService redisEventService;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();

    public void sendMail(EmailRequest emailRequest) {
        EmailEntity emailEntity = mapper.toEntity(emailRequest);
        CompletableFuture
                .supplyAsync(() -> this.send(emailEntity), executor)
                .thenAccept(message -> emailEntity.setSentDate(LocalDateTime.now()))
                .exceptionally(throwable -> {
                    log.error("Error to sent email {}", throwable.getMessage());
                    redisEventService.addToQueue(emailEntity);
                    return null;
                });
    }

    @Retryable(
            retryFor = { MailSendException.class },
            maxAttemptsExpression = "3",
            backoff = @Backoff(delayExpression = "1000", maxDelayExpression = "5000", multiplierExpression = "2.0")
    )
    public void sendMailWithRetry(EmailRequest emailRequest) {
        EmailEntity emailEntity = mapper.toEntity(emailRequest);
        this.send(emailEntity);
    }

    @Recover
    public void fallback(Exception exception, String url, Object request) {
        log.error("Error to sent email {}", exception.getMessage());
    }

    private SimpleMailMessage send(EmailEntity emailEntity) {
        var message = new SimpleMailMessage();
        message.setFrom(emailEntity.getFrom());
        message.setTo(emailEntity.getTo());
        message.setSubject(emailEntity.getSubject());
        message.setText(emailEntity.getContent());
        log.info("Sending email");
        sender.send(message);
        return message;
    }

}
