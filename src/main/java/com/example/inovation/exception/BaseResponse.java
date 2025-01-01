package com.example.inovation.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 반환에 기본이 되는 Response
 * Time, 성공여부, 상태코드 , 메시지를 담고 있다.
 * 다른 Response들은 이 클래스를 기반으로 확장한다.
 */
@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "status", "message"})
public class BaseResponse {
    private final LocalDateTime timestamp;
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final HttpStatus status;
    private final String message;

    protected BaseResponse(BaseStatus status) {
        this.timestamp = LocalDateTime.now();
        this.isSuccess = status.isSuccess();
        this.status = status.getStatus();
        this.message = status.getMessage();
    }

    /**
     * 상태 코드를 기반으로 BaseResponse를 생성하여 반환
     * @param status Response에 담을 상태코드
     * @return 상태 코드를 기반으로한 시간과 내용이 저장된 Response 객체
     */
    public static BaseResponse from(BaseStatus status) {
        return new BaseResponse(status);
    }
}
