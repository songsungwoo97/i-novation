package com.example.inovation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseStatus {
    /** CODE 2xx */
    OK(true, HttpStatus.OK, "요청을 처리하였습니다."), // 200

    /** CODE 3xx */

    /** CODE 4xx */
    BAD_REQUEST(false, HttpStatus.BAD_REQUEST, "잘못된 요청입니다."), // 400
    FORBIDDEN(false, HttpStatus.FORBIDDEN, "권한이 없습니다."), // 403
    NOT_FOUND(false, HttpStatus.NOT_FOUND, "페이지를 찾을 수 없습니다."), // 404
    METHOD_NOT_ALLOWED(false, HttpStatus.METHOD_NOT_ALLOWED, "해당 메소드를 사용할 수 없습니다."), // 405

    /** CODE 5xx */
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버의 오류가 발생했습니다."); // 500

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;

    BaseStatus(boolean isSuccess, HttpStatus status, String message) {
        this.isSuccess = isSuccess;
        this.status = status;
        this.message = message;
    }
}
