package br.com.rodneybarreto.annunciator.configuration.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "service.retry")
public class ServiceRetryProperties {

    @NotNull
    private int maxAttempts;

    @NotNull
    private long initialDelay;

    @NotNull
    private long maxDelay;

    @NotNull
    private float multiplier;

    public String getMaxAttemptsAsString() {
        return String.valueOf(this.getMaxAttempts());
    }

}

