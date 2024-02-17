package com.github.sats17.orchestrator.exception;

public class InternalServerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	public InternalServerException(String message) {
		super();
		this.message = message;
	}
}
