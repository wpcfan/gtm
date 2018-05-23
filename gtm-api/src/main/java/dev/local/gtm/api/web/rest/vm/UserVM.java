package dev.local.gtm.api.web.rest.vm;

import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.service.dto.UserDTO;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVM {
    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = 1, max = 50)
    private String login;

    @Pattern(regexp = Constants.MOBILE_REGEX)
    private String mobile;

    @Size(max = 50)
    private String name;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @Size(max = 256)
    private String avatar;

    @Getter
    @Setter
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Getter
    @Setter
    @NotNull
    private String validateToken;

    public UserDTO toUserDTO() {
        return UserDTO.builder().login(login).mobile(mobile).avatar(avatar).email(email).name(name).build();
    }
}
