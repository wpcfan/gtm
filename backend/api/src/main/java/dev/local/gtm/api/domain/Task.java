package dev.local.gtm.api.domain;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    private String id;
    private String desc;
    private boolean completed;
    private User owner;
}
