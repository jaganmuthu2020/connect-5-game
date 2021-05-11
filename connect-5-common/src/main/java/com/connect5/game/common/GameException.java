package com.connect5.game.common;

public class GameException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -161348510519541542L;

	private String exceptionCode = "";

	public String getExceptionCode() {
		return exceptionCode;
	}

	public GameException() {
		super();
	}

	public GameException(String exceptionCode, String message) {
		super(message);
		this.exceptionCode = exceptionCode;
	}

	public GameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GameException(String message, Throwable cause) {
		super(message, cause);
	}

	public GameException(String message) {
		super(message);
	}

	public GameException(Throwable cause) {
		super(cause);
	}
}
