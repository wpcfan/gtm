package dev.local.gtm.api.web.websocket.dto;

import lombok.Data;

import java.time.Instant;

/**
 * DTO for storing a user's activity.
 */
@Data
public class ActivityDTO {

    private String sessionId;

    private String userLogin;

    private String ipAddress;

    private String page;

    private Instant time;
}
