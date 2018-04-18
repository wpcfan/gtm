package dev.local.gtm.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyAndPassword {
    private String mobile;
    private String resetKey;
    private String password;
}
