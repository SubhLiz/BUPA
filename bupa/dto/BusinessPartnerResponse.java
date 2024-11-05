package com.incture.bupa.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class BusinessPartnerResponse {
	private String crNumber;
	private int statusCode;
	private JsonNode message;
	private String requestId;
	private String dbSaveMessage;
	private String vendor;
	private CPIVendorDetailsDto request;
	private String requestType;
}
