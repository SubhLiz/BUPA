package com.incture.bupa.dto;

import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;

public class HttpResponseDto {
	private Object responseData;
	private Integer statuscode;
	private CloseableHttpClient client;
	private Map<String, String> headers;
	public Object getResponseData() {
		return responseData;
	}
	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}
	public Integer getStatuscode() {
		return statuscode;
	}
	public void setStatuscode(Integer statuscode) {
		this.statuscode = statuscode;
	}
	public CloseableHttpClient getClient() {
		return client;
	}
	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	@Override
	public String toString() {
		return "HttpResponseDto [responseData=" + responseData + ", statuscode=" + statuscode + ", client=" + client
				+ ", headers=" + headers + "]";
	}
	
}
