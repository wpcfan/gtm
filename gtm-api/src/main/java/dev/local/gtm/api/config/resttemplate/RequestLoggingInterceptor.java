package dev.local.gtm.api.config.resttemplate;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

@Slf4j
public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    logRequest(request, body);
    return execution.execute(request, body);
  }

  private void logRequest(HttpRequest request, byte[] body) throws IOException {
    if (log.isDebugEnabled()) {
      log.debug("===========================request begin================================================");
      log.debug("URI         : {}", request.getURI());
      log.debug("Method      : {}", request.getMethod());
      log.debug("Headers     : {}", request.getHeaders());
      log.debug("Request body: {}", new String(body, "UTF-8"));
      log.debug("==========================request end================================================");
    }
  }
}
