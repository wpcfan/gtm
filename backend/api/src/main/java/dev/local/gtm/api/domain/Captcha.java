package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
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
