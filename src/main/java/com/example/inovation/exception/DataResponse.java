package com.example.inovation.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "status", "message", "data"})
public class DataResponse<T> extends BaseResponse{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    /**
     * Response 생성자
     * BaseState와 전달/저장할 data를 저장한다.
     * @param status 성공 여부, 상태 코드, 메시지를 담고 있는 클래스 객체
     * @param data 전달 및 저장할 데이터 객체
     */
    private DataResponse(BaseStatus status, T data) {
        super(status);
        this.data = data;
    }

    /**
     * DataResponse 생성
     * @param status HttpStatus
     * @param data 따로 저장할 데이터
     */
    public static <T> DataResponse<?> from(BaseStatus status, T data) {
        return new DataResponse<>(status, data);
    }
}

