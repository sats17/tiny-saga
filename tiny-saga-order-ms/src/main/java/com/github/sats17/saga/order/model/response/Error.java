package com.github.sats17.saga.order.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Error {

	private Integer resultCode;
	private String resultType;
	private String message;
	private String httpMethod;
	private String httpRequestURI;

	public Integer getResultCode() {
		return resultCode;
	}

	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getHttpRequestURI() {
		return httpRequestURI;
	}

	public void setHttpRequestURI(String httpRequestURI) {
		this.httpRequestURI = httpRequestURI;
	}

	@Override
	public String toString() {
		return "Error [resultCode=" + resultCode + ", resultType=" + resultType + ", message=" + message
				+ ", httpMethod=" + httpMethod + ", httpRequestURI=" + httpRequestURI + "]";
	}

}
