package dev.local.gtm.api.config.audit;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.stereotype.Component;

import dev.local.gtm.api.config.Constants;
import dev.local.gtm.api.security.SecurityUtils;

@Component
public class JaversAuthorProvider implements AuthorProvider {

  @Override
  public String provide() {
    return SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT);
  }
}
