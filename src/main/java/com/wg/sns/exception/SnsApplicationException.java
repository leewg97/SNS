package com.wg.sns.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SnsApplicationException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        }

        return String.format("%s. %s", errorCode.getMessage(), message);
    }
}
