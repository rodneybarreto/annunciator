package br.com.rodneybarreto.annunciator.service;

import br.com.rodneybarreto.annunciator.configuration.properties.AppRedisProperty;
import br.com.rodneybarreto.annunciator.domain.entity.EmailEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.UnifiedJedis;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisEventService {

    private final AppRedisProperty redisProps;
    private final ObjectMapper objectMapper;

    public void addToQueue(EmailEntity emailEntity) {
        try (var jedis = new UnifiedJedis(redisProps.getServer())) {
            emailEntity.setPendingDate(LocalDateTime.now());
            log.info("Storaging pending email in redisProps queue");
            long count = jedis.rpush(redisProps.getQueue(), objectMapper.writeValueAsString(emailEntity));
            log.info("Queue [{}] has [{}] email(s) pending", redisProps.getQueue(), count);
        }
        catch (JsonProcessingException jpe) {
            log.error("Error to convert json string {}", jpe.getMessage());
        }
    }

    public void getFromQueue() {
        try (var jedis = new UnifiedJedis(redisProps.getServer())) {
            int timeout = 30;
            EmailEntity emailEntity = null;

            List<String> messages = jedis.blpop(timeout, redisProps.getQueue());
            while (messages != null) {
                String message = messages.get(1);
                try {
                    emailEntity = objectMapper.readValue(message, EmailEntity.class);
                    log.info("Processing pending email {}", emailEntity.toString());
                    messages = jedis.blpop(timeout, redisProps.getQueue());
                }
                catch (JsonProcessingException e) {
                    log.error("Error to processing json string {}", e.getMessage());
                }
            }
        }
    }

}
