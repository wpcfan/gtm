package dev.local.gtm.api.config.resttemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import dev.local.gtm.api.web.exception.InternalServerErrorException;
import dev.local.gtm.api.web.exception.OutgoingBadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeanCloudRequestErrorHandler implements ResponseErrorHandler {

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode() != HttpStatus.OK;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
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
    val reader = new InputStreamReader(in);
    val sb = new StringBuilder();
    val buffer = new char[4096];
    int read;
    while ((read = reader.read(buffer)) != -1) {
      sb.append(buffer, 0, read);
    }
    return sb.toString();
  }

  @Data
  @AllArgsConstructor
  private final class LeanCloudError {
    private Integer code;
    private String error;
  }
}
