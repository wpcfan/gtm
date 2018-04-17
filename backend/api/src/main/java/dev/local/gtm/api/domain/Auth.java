package dev.local.gtm.api.domain;

import lombok.Data;

@Data
public class Auth {
    private String login;
    private String password;
}
