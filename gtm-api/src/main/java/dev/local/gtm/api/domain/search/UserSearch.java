package dev.local.gtm.api.domain.search;

import dev.local.gtm.api.domain.AbstractAuditingEntity;
import dev.local.gtm.api.domain.Authority;
import dev.local.gtm.api.domain.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(of = { "id" }, callSuper = false)
@ToString
@NoArgsConstructor
@Document(indexName = "users", type = "user")
public class UserSearch extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String login;

    private String mobile;

    private String name;

    private String email;

    private String avatar;

    private boolean activated;

    private Set<String> authorities;

    public UserSearch(User user) {
        this.id = user.getId();
        this.activated = user.isActivated();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.mobile = user.getMobile();
        this.name = user.getName();
        this.authorities = user.getAuthorities().stream()
                .map(Authority::getName)
                .collect(Collectors.toSet());
    }
}
