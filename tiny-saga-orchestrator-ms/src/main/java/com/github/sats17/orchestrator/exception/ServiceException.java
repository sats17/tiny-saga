package com.github.sats17.orchestrator.exception;

public class ServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String service = "Orchestrator MS";
	private String subService;
	private Integer statusCode;
	private String message;

	public ServiceException(String subService, Integer statusCode, String message) {
		super();
		this.subService = subService;
		this.statusCode = statusCode;
		this.message = message;
	}

	public String getSubService() {
		return subService;
	}

	public void setSubService(String subService) {
		this.subService = subService;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getService() {
		return service;
	}

}
