package br.com.rodneybarreto.annunciator.service;

import br.com.rodneybarreto.annunciator.configuration.properties.AppRedisProperty;
import br.com.rodneybarreto.annunciator.domain.dto.EmailRequest;
import br.com.rodneybarreto.annunciator.domain.entity.EmailEntity;
import br.com.rodneybarreto.annunciator.mapper.EmailMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final AppRedisProperty redis;
    private final EmailMapper mapper;
    private final JavaMailSender sender;
    private final ObjectMapper objectMapper;

    public void send(EmailRequest emailRequest) {
        EmailEntity emailEntity = mapper.toEntity(emailRequest);

        CompletableFuture<SimpleMailMessage> future = CompletableFuture.supplyAsync(() -> {
            var message = new SimpleMailMessage();
            message.setFrom(emailEntity.getFrom());
            message.setTo(emailEntity.getTo());
            message.setSubject(emailEntity.getSubject());
            message.setText(emailEntity.getContent());
            log.info("Sending email...");
            sender.send(message);
            return message;
        });

        future.thenAccept(message -> emailEntity.setSentDate(LocalDateTime.now()));

        future.exceptionally(throwable -> {
            log.error("Error to sent email {}", throwable.getMessage());
            try (var jedis = new UnifiedJedis(redis.getServer())) {
                log.info("Storaging pending email in redis queue");
                emailEntity.setPendingDate(LocalDateTime.now());
                jedis.lpush(redis.getQueue(), objectMapper.writeValueAsString(emailEntity));
            } catch (JsonProcessingException jpe) {
                log.error("Error to convert json string {}", jpe.getMessage());
            }
            return null;
        });
    }

}
