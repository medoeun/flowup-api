package com.flowup.api.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseApiResponse<T> {

	private boolean success;
	private String message;
	private T data;

	// 성공 응답 생성
	public static <T> BaseApiResponse<T> success(String message, T data) {
		return new BaseApiResponse<>(true, message, data);
	}

	// 실패 응답 생성
	public static <T> BaseApiResponse<T> failure(String message) {
		return new BaseApiResponse<>(false, message, null);
	}
}