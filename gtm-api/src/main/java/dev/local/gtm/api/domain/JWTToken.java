package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 简单返回 JWT token 对于非常简单的需要封装成 JSON 的类，可以直接定义在 Controller 中
 */
@Getter
@Setter
@AllArgsConstructor
public class JWTToken implements Serializable {

  private static final long serialVersionUID = 1L;

  @JsonProperty("id_token")
  private String idToken;

  @JsonProperty("refresh_token")
  private String refreshToken;
}
