package com.incture.bupa.utils;

public interface AppErrorMsgConstants {
	String CONFLICT_MSG = "Record already exists.Please perform a search";
	String DATA_NOT_FOUND = "Data not available in our record.";
	String PERMISSION_NOT_FOUND = "Permission object not found.";
	String NO_REQUEST_BODY = "Request body not found.";
	String INVALID_INPUT = "Invalid Input, ";
	String EXCEPTION_POST_MSG = "Error occured due to : ";
	String TOKEN_VALIDATION = "Invalid token";

	String NO_RESPONSE = "Awaiting Response from MDG";

	String JDBC_EXCEPTION_MESSAGE = "Please provide a valid data";
}
