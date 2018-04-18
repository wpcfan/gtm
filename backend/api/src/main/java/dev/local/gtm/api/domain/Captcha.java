package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 使用 LeanCloud 的 Captcha，属性定义和 LeanCloud API 直接相关
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Captcha {
    @JsonProperty("captcha_token")
    private String token;
    @JsonProperty("captcha_url")
    private String imgUrl;
    @JsonProperty("captcha_code")
    private String code;
    @JsonProperty("validate_token")
    private String validatedMsg;
}
