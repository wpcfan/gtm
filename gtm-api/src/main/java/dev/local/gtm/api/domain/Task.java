package dev.local.gtm.api.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_tasks")
public class Task {
    @Id
    private String id;
    private String desc;
    private boolean completed;
    private User owner;
}
