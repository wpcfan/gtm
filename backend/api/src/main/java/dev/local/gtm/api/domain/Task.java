package dev.local.gtm.api.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Task {
    @Id
    private String id;
    private String desc;
    private boolean completed;
    private String userId;
}
