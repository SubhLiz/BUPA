package com.incture.bupa.exceptions;

/**
 * <code>ExecutionException</code> is wrapper over different kinds of
 * exceptions, generally indicate a fatal error eg: resource missing, data
 * source connection failed
 * 
 * @version 1, 04-April-2017
 * @since Murphy
 */
public class ExecutionFault extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 627683056372690666L;
	/**
	 * Java type that goes as soapenv:Fault detail element.
	 */
	private FaultMessage faultInfo;

	public ExecutionFault() {
		// TODO Auto-generated constructor stub
	}

	public ExecutionFault(String faultMessage) {
		super(faultMessage);
//		super("Execution on Server failed, please retry later");
		faultInfo = new FaultMessage();
		faultInfo.setMessage(faultMessage);
	}

	public ExecutionFault(String message, FaultMessage faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public ExecutionFault(String message, FaultMessage faultInfo,
			Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public FaultMessage getFaultInfo() {
		return faultInfo;
	}
}