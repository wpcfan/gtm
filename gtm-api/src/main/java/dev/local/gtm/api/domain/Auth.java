package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 鉴权对象，只包括登录名、密码和 ValidateToken
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Data
public class Auth {
    @ApiModelProperty(value = "登录名")
    private String login;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "图形验证码的 validate_token")
    @JsonProperty("validate_token")
    private String validateToken;
}
