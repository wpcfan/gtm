package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.local.gtm.api.config.Constants;
import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(callSuper = false, of = { "id" })
@ToString(exclude = "authorities")
@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "api_users")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "users")
public class User extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @org.springframework.data.mongodb.core.index.Indexed
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    private String password;

    @NotNull
    @Pattern(regexp = Constants.MOBILE_REGEX)
    @Size(min = 10, max = 15)
    @org.springframework.data.mongodb.core.index.Indexed
    private String mobile;

    @Size(max = 50)
    private String name;

    @Email
    @Size(min = 5, max = 254)
    @org.springframework.data.mongodb.core.index.Indexed
    private String email;

    @Size(max = 256)
    private String avatar;

    @Size(max = 20)
    @org.springframework.data.mongodb.core.mapping.Field("reset_key")
    @JsonIgnore
    private String resetKey;

    @org.springframework.data.mongodb.core.mapping.Field("reset_date")
    @Builder.Default
    private Instant resetDate = null;

    @Builder.Default
    private boolean activated = false;

    @JsonIgnore
    @Singular("authority")
    @org.springframework.data.mongodb.core.mapping.DBRef(lazy = true)
    @org.springframework.data.mongodb.core.mapping.Field("authority_ids")
    private Set<Authority> authorities;
}
