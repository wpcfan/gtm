package dev.local.gtm.api.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data @Builder
public class Task {
    @Id
    private String id;
    private String desc;
    private boolean completed;
    private String userId;
}
