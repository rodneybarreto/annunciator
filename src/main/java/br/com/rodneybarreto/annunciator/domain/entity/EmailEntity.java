package br.com.rodneybarreto.annunciator.domain.entity;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailEntity implements Serializable {

    private String from;
    private String to;
    private String subject;
    private String content;
    private LocalDateTime sentDate;
    private LocalDateTime pendingDate;

}
