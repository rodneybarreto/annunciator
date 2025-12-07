package br.com.rodneybarreto.annunciator.service;

import br.com.rodneybarreto.annunciator.domain.dto.EmailRequest;
import br.com.rodneybarreto.annunciator.domain.entity.EmailEntity;
import br.com.rodneybarreto.annunciator.mapper.EmailMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String REDIS_SERVER = "redis://localhost:6379";
    private static final String QUEUE = "annunciator:email:sending-pending";

    private final EmailMapper mapper;
    private final JavaMailSender sender;
    private final ObjectMapper objectMapper;

    public void send(EmailRequest emailRequest) {
        try (var jedis = new UnifiedJedis(REDIS_SERVER)) {
            EmailEntity emailEntity = mapper.toEntity(emailRequest);
            try {
                var message = new SimpleMailMessage();
                message.setFrom(emailEntity.getFrom());
                message.setTo(emailEntity.getTo());
                message.setSubject(emailEntity.getSubject());
                message.setText(emailEntity.getContent());
                sender.send(message);
            }
            catch (MailSendException mse) {
                log.error("Error to sent e-mail {}", mse.getMessage());
                try {
                    log.info("Storagging pending email in redis queue");
                    jedis.lpush(QUEUE, objectMapper.writeValueAsString(emailEntity));
                }
                catch (JsonProcessingException jpe) {
                    log.error("Error to convert json string {}", jpe.getMessage());
                }
            }
        }
    }

}
