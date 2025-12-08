package br.com.rodneybarreto.annunciator.controller;

import br.com.rodneybarreto.annunciator.domain.dto.EmailRequest;
import br.com.rodneybarreto.annunciator.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> send(@RequestBody @Valid EmailRequest emailRequest) {
        emailService.send(emailRequest);
        return ResponseEntity.ok().build();
    }

}
