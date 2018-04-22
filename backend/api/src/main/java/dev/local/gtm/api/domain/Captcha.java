package dev.local.gtm.api.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Captcha implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("captcha_token")
    private String token;

    @JsonProperty("captcha_url")
    private String url;
}
