package dev.local.gtm.api.interceptor;

import dev.local.gtm.api.config.AppProperties;
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
 * 关于 LeanCloud 短信服务的鉴权信息可以参考 <a>https://leancloud.cn/docs/rest_sms_api.html</a>
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@RequiredArgsConstructor
public class LeanCloudRequestInterceptor implements ClientHttpRequestInterceptor {

    private final AppProperties appProperties;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (!request.getURI().getHost().contains("api.lncld.net")){
            return execution.execute(request, body);
        }
        val headers = request.getHeaders();
        headers.add("X-LC-Id", appProperties.getLeanCloud().getAppId());
        headers.add("X-LC-Key", appProperties.getLeanCloud().getAppKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return execution.execute(request, body);
    }
}
