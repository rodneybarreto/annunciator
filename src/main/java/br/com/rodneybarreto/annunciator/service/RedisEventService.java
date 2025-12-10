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

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisEventService {

    private final AppRedisProperty redis;
    private final ObjectMapper objectMapper;

    public void addToQueue(EmailEntity emailEntity) {
        try (var jedis = new UnifiedJedis(redis.getServer())) {
            emailEntity.setPendingDate(LocalDateTime.now());
            log.info("Storaging pending email in redis queue");
            long count = jedis.rpush(redis.getQueue(), objectMapper.writeValueAsString(emailEntity));
            log.info("Queue [{}] has [{}] email(s) pending", redis.getQueue(), count);
        }
        catch (JsonProcessingException jpe) {
            log.error("Error to convert json string {}", jpe.getMessage());
        }
    }

    public void getFromQueue() {
        try (var jedis = new UnifiedJedis(redis.getServer())) {
            int timeout = 30;
            jedis.blpop(timeout, redis.getQueue()).stream().iterator().forEachRemaining(message -> {
                EmailEntity emailEntity = null;
                try {
                    emailEntity = objectMapper.readValue(message, EmailEntity.class);
                    log.info("Processing pending email {}", emailEntity.toString());
                }
                catch (JsonProcessingException e) {
                    log.error("Error to processing json string {}", e.getMessage());
                }
            });
        }
    }

}
