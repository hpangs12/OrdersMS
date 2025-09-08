package com.orderms.exception;

/**
 * Custom Exception class for Out Of Stock Exception
 */
public class OutOfStockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutOfStockException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OutOfStockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public OutOfStockException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public OutOfStockException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public OutOfStockException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
