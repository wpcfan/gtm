package dev.local.gtm.api.domain;

import lombok.Data;

/**
 * 鉴权对象，只包括登录名、密码
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Data
public class Auth {
    private String login;
    private String password;
}
