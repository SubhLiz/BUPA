package com.incture.bupa.exceptions;

/**
 * <code>NonUniqueRecordFault</code> is to indicate application that the query
 * returns more than one record, sometimes its indicates an unique constraint
 * violation in the data source
 * 
 * @version 1, 04-April-2017
 * @since Murphy
 */
//@WebFault(name = "NonUniqueRecordFault", faultBean = "com.incture.u2k2.mmw.exception.MessageUIDto")
public class NonUniqueRecordFault extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3878903576084669146L;
	/**
	 * Java type that goes as soapenv:Fault detail element.
	 */
	private FaultMessage faultInfo;

	public NonUniqueRecordFault() {
		// TODO Auto-generated constructor stub
	}

	public NonUniqueRecordFault(String faultMessage) {
		super("Failed due to corrupt data, please contact db admin ");
		faultInfo = new FaultMessage();
		faultInfo.setMessage(faultMessage);
	}
	
	public NonUniqueRecordFault(String message, FaultMessage faultInfo) {
		super(message);
		this.faultInfo = faultInfo;
	}

	public NonUniqueRecordFault(String message, FaultMessage faultInfo,
			Throwable cause) {
		super(message, cause);
		this.faultInfo = faultInfo;
	}

	public FaultMessage getFaultInfo() {
		return faultInfo;
	}
}