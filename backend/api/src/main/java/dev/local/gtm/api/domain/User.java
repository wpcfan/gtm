package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.local.gtm.api.config.Constants;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = {"id"})
@ToString(exclude = "authorities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Indexed
    private String login;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    private String password;

    @NotNull
    @Pattern(regexp = Constants.MOBILE_REGEX)
    @Size(min = 10, max = 15)
    private String mobile;

    @Size(max = 50)
    private String name;

    @Email
    @Size(min = 5, max = 254)
    @Indexed
    private String email;

    @Size(max = 256)
    private String avatar;

    @Size(max = 20)
    @Field("activation_key")
    @JsonIgnore
    private String activationKey;

    @Size(max = 20)
    @Field("reset_key")
    @JsonIgnore
    private String resetKey;

    @Field("reset_date") @Builder.Default
    private Instant resetDate = null;

    @Builder.Default
    private boolean activated = false;

    @JsonIgnore
    @Singular("authorities")
    private Set<Authority> authorities;
}
