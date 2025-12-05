package br.com.rodneybarreto.annunciator.domain.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEntity {

    private String from;
    private String to;
    private String subject;
    private String content;
    private LocalDateTime sentDate;

}
