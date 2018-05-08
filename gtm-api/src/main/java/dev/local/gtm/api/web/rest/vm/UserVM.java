package dev.local.gtm.api.web.rest.vm;

import dev.local.gtm.api.service.dto.UserDTO;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@ToString(callSuper = true)
public class UserVM extends UserDTO {
    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Getter
    @Setter
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Builder
    public UserVM(String id, String login, String mobile, String email, String name,
                  String avatar, boolean activated, Set<String> authorities, String password) {
        super(id, login, mobile, name, email, avatar, activated, authorities);
        this.password = password;
    }
}
