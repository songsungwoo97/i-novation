package com.example.inovation.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private BaseStatus status;
    // 내부 서버에 띄울 메시지, 자세한 메시지를 작성하여 내부 서버에 원인을 자세히 확인할 수 있도록 한다.
    private String message;
}