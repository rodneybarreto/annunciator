package br.com.rodneybarreto.annunciator.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailRequest(
        String from,

        @NotBlank
        String to,

        @NotBlank
        String subject,

        String content
) {
}
