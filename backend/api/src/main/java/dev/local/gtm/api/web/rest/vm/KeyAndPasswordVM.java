package dev.local.gtm.api.web.rest.vm;

import lombok.*;

@Getter
@Setter
public class KeyAndPasswordVM {
    private String mobile;
    private String resetKey;
    private String password;
}
