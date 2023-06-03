package com.example.inovation.service.Error;

import org.springframework.http.HttpStatus;

public class NewsApiException extends RuntimeException {

    private HttpStatus httpStatus;

    public NewsApiException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}