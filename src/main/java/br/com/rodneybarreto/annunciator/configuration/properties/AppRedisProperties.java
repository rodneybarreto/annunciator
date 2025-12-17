package br.com.rodneybarreto.annunciator.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "app.redis")
public class AppRedisProperties {

    @NotBlank
    private String server;

    @NotBlank
    private String queue;

}
