package dev.local.gtm.api.web.exception;

import lombok.Value;

import java.io.Serializable;

@Value
public class FieldErrorVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;
}
