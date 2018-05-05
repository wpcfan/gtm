package dev.local.gtm.api.config;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.local.gtm.api.interceptor.LeanCloudAuthHeaderInterceptor;
import dev.local.gtm.api.web.exception.InternalServerErrorException;
import dev.local.gtm.api.web.exception.OutgoingBadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 为应用访问外部 Http Rest API 提供配置
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Log4j2
@RequiredArgsConstructor
@Configuration
public class OutgoingRestTemplateConfig {

    private static final int TIMEOUT = 10000;
    private final AppProperties appProperties;

    @Bean("leanCloudTemplate")
    public RestTemplate getLeanCloudRestTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(TIMEOUT)
                .setReadTimeout(TIMEOUT)
                .errorHandler(new LeanCloudRequestErrorHandler())
//                .requestFactory(new HttpComponentsAsyncClientHttpRequestFactory()) // Use Apache HttpComponent
                .interceptors(new LeanCloudAuthHeaderInterceptor(appProperties)) // new RequestLoggingInterceptor()
                .build();
    }

    public class LeanCloudRequestErrorHandler implements ResponseErrorHandler {

        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode() != HttpStatus.OK;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            if (response.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new OutgoingBadRequestException("403 Forbidden");
            }
            val err = extractErrorFromResponse(response);
            if (err == null) {
                throw new InternalServerErrorException("从 Response 中提取 json 返回 null");
            }
            throw new OutgoingBadRequestException(err.getError());
        }

        private LeanCloudError extractErrorFromResponse(ClientHttpResponse response) throws IOException {
            String json = readResponseJson(response);
            try {
                ObjectMapper mapper = new ObjectMapper(new JsonFactory());
                JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
                Integer code = jsonNode.has("code") ? jsonNode.get("code").intValue() : null;
                String err = jsonNode.has("error") ? jsonNode.get("error").asText() : null;

                val error = new LeanCloudError(code, err);
                log.debug("LeanCloud error: ");
                log.debug("   CODE        : " + error.getCode());
                log.debug("   ERROR       : " + error.getError());
                return error;
            } catch (JsonParseException e) {
                return null;
            }
        }

        private String readResponseJson(ClientHttpResponse response) throws IOException {
            val json = readFully(response.getBody());
            log.debug("LeanCloud 返回的错误: " + json);
            return json;
        }

        private String readFully(InputStream in) throws IOException {
            val reader = new BufferedReader(new InputStreamReader(in));
            val sb = new StringBuilder();
            while (reader.ready()) {
                sb.append(reader.readLine());
            }
            return sb.toString();
        }
    }

    @Data
    @AllArgsConstructor
    private class LeanCloudError {
        private Integer code;
        private String error;
    }
}
