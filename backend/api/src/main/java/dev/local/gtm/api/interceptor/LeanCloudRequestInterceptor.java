package dev.local.gtm.api.interceptor;

import dev.local.gtm.api.config.propsupport.LeanCloudProperties;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * 为 LeanCloud 云服务配置在 Request Header 中写入鉴权信息
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@RequiredArgsConstructor
public class LeanCloudRequestInterceptor implements ClientHttpRequestInterceptor {

    private final LeanCloudProperties leanCloudProperties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        val headers = request.getHeaders();
        headers.add("X-LC-Id", leanCloudProperties.getAppId());
        headers.add("X-LC-Key", leanCloudProperties.getAppKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return execution.execute(request, body);
    }
}
