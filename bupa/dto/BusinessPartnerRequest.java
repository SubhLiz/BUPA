package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "rosVendorNumber", "version" })
public class BusinessPartnerRequest {

	@JsonProperty("rosVendorNumber")
	private String rosVendorNumber;
	@JsonProperty("version")
	private int version;

	@JsonProperty("rosVendorNumber")
	public String getRosVendorNumber() {
		return rosVendorNumber;
	}

	@JsonProperty("rosVendorNumber")
	public void setRosVendorNumber(String rosVendorNumber) {
		this.rosVendorNumber = rosVendorNumber;
	}

	@JsonProperty("version")
	public int getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(int version) {
		this.version = version;
	}

}
