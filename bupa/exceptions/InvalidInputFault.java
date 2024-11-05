package com.incture.bupa.exceptions;

/**
 * <code>InvalidInputFault</code> is to indicate application that the parameters
 * passed to the method is invalid w.r.t its implementation.
 * 
 * @version 1, 05-April-2017
 * @since Murphy
 */
public class InvalidInputFault extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5879608670346636459L;
	private FaultMessage faultInfo;

	public InvalidInputFault() {
		// TODO Auto-generated constructor stub
	}

	public InvalidInputFault(String faultMessage) {
		super(faultMessage);
		faultInfo = new FaultMessage();
		faultInfo.setMessage(faultMessage);
	}
	
	public InvalidInputFault(String message, FaultMessage faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public InvalidInputFault(String message, FaultMessage faultInfo,
			Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public FaultMessage getFaultInfo() {
		return faultInfo;
	}
}