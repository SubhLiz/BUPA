package com.incture.bupa.dto;

import java.util.List;

import com.incture.bupa.constants.AppConstants;

public class ServiceResponse<T> {
	private String message = AppConstants.SUCCESS;
	private String status = AppConstants.SUCCESS;
	private String error;
	private int errorCode;
	private Object content;
	private T data;
	private List<T> dataList;

	public ServiceResponse() {
	}

	public ServiceResponse(T data) {
		super();
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	@Override
	public String toString() {
		return "ServiceResponse [message=" + message + ", status=" + status + ", error=" + error + ", errorCode="
				+ errorCode + ", content=" + content + ", data=" + data + ", dataList=" + dataList + "]";
	}

	
	
}
