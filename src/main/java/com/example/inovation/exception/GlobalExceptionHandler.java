package com.example.inovation.exception;

import com.sinor.cache.notuse.FailureResponse;
import com.sinor.cache.notuse.admin.AdminException;
import com.sinor.cache.notuse.main.MainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * AdminException Handler
	 */
	@ExceptionHandler(AdminException.class)
	public ResponseEntity<?> adminException(AdminException e) {
		log.error("Custom Exception Occurred : " + e.getStatus().getCode(), e);

		FailureResponse responseBody = FailureResponse.fromByAdmin(e.getStatus());
		return ResponseEntity.status((e).getStatus().getCode()).body(responseBody);
	}

	/**
	 * mainException Handler
	 */
	@ExceptionHandler(MainException.class)
	public ResponseEntity<?> mainException(MainException e) {
		log.error("Custom Exception Occurred : " + e.getStatus().getCode(), e);

		FailureResponse responseBody = FailureResponse.fromByMain(e.getStatus());

		return ResponseEntity.status((e).getStatus().getCode()).body(responseBody);
	}

	/**
	 * BaseException 기반 예외를 감지하여 Response를 반환하는 Handler
	 * exception 객체에 저장된 에러 원인을 출력하고 관련 상태코드를 기반으로 Response를 반환
	 * @param e 발생한 Exception 객체
	 * @return 발생한 Excetpion을 기반으로한 ResponseEntity
	 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<?> baseException(BaseException e) {
		log.error("Base Exception Occurred : " + e.getMessage());
		BaseResponse responseBody = BaseResponse.from(e.getStatus());
		return ResponseEntity.status((e).getStatus().getStatus()).body(responseBody);
	}

	/**
	 * Internal Server Error Exception Handler
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<?> Exception(Exception e) {
		log.error("Internal Server Error occurred : " + HttpStatus.INTERNAL_SERVER_ERROR.value(), e);

		FailureResponse responseBody = FailureResponse.from(
			com.sinor.cache.notuse.ResponseStatus.INTERNAL_SERVER_ERROR);

		return ResponseEntity.internalServerError().body(responseBody);
	}
}