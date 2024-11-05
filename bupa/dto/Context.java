package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "email", "supplier", "smtTeam", "cmtTeam", "isRejectedBySmt", "isRejectedByCmt",
		"isQualifiedBySmt", "isQualifiedByCmt", "isDisqualifiedBySmt", "isDisqualifiedByCmt", "requestId",
		"rosVendorNumber", "version", "businessPartnerRequest" })
public class Context {

	@JsonProperty("email")
	private String email;
	@JsonProperty("supplier")
	private String supplier;
	@JsonProperty("smtTeam")
	private String smtTeam;
	@JsonProperty("cmtTeam")
	private String cmtTeam;
	@JsonProperty("isRejectedBySmt")
	private boolean isRejectedBySmt;
	@JsonProperty("isRejectedByCmt")
	private boolean isRejectedByCmt;
	@JsonProperty("isQualifiedBySmt")
	private boolean isQualifiedBySmt;
	@JsonProperty("isQualifiedByCmt")
	private boolean isQualifiedByCmt;
	@JsonProperty("isDisqualifiedBySmt")
	private boolean isDisqualifiedBySmt;
	@JsonProperty("isDisqualifiedByCmt")
	private boolean isDisqualifiedByCmt;
	@JsonProperty("requestId")
	private int requestId;
	@JsonProperty("rosVendorNumber")
	private String rosVendorNumber;
	@JsonProperty("version")
	private int version;
	@JsonProperty("businessPartnerRequest")
	private BusinessPartnerRequest businessPartnerRequest;
	
	
	@JsonProperty("email")
	public String getEmail() {
		return email;
	}

	@JsonProperty("email")
	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty("supplier")
	public String getSupplier() {
		return supplier;
	}

	@JsonProperty("supplier")
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@JsonProperty("smtTeam")
	public String getSmtTeam() {
		return smtTeam;
	}

	@JsonProperty("smtTeam")
	public void setSmtTeam(String smtTeam) {
		this.smtTeam = smtTeam;
	}

	@JsonProperty("cmtTeam")
	public String getCmtTeam() {
		return cmtTeam;
	}

	@JsonProperty("cmtTeam")
	public void setCmtTeam(String cmtTeam) {
		this.cmtTeam = cmtTeam;
	}

	@JsonProperty("isRejectedBySmt")
	public boolean isIsRejectedBySmt() {
		return isRejectedBySmt;
	}

	@JsonProperty("isRejectedBySmt")
	public void setIsRejectedBySmt(boolean isRejectedBySmt) {
		this.isRejectedBySmt = isRejectedBySmt;
	}

	@JsonProperty("isRejectedByCmt")
	public boolean isIsRejectedByCmt() {
		return isRejectedByCmt;
	}

	@JsonProperty("isRejectedByCmt")
	public void setIsRejectedByCmt(boolean isRejectedByCmt) {
		this.isRejectedByCmt = isRejectedByCmt;
	}

	@JsonProperty("isQualifiedBySmt")
	public boolean isIsQualifiedBySmt() {
		return isQualifiedBySmt;
	}

	@JsonProperty("isQualifiedBySmt")
	public void setIsQualifiedBySmt(boolean isQualifiedBySmt) {
		this.isQualifiedBySmt = isQualifiedBySmt;
	}

	@JsonProperty("isQualifiedByCmt")
	public boolean isIsQualifiedByCmt() {
		return isQualifiedByCmt;
	}

	@JsonProperty("isQualifiedByCmt")
	public void setIsQualifiedByCmt(boolean isQualifiedByCmt) {
		this.isQualifiedByCmt = isQualifiedByCmt;
	}

	@JsonProperty("isDisqualifiedBySmt")
	public boolean isIsDisqualifiedBySmt() {
		return isDisqualifiedBySmt;
	}

	@JsonProperty("isDisqualifiedBySmt")
	public void setIsDisqualifiedBySmt(boolean isDisqualifiedBySmt) {
		this.isDisqualifiedBySmt = isDisqualifiedBySmt;
	}

	@JsonProperty("isDisqualifiedByCmt")
	public boolean isIsDisqualifiedByCmt() {
		return isDisqualifiedByCmt;
	}

	@JsonProperty("isDisqualifiedByCmt")
	public void setIsDisqualifiedByCmt(boolean isDisqualifiedByCmt) {
		this.isDisqualifiedByCmt = isDisqualifiedByCmt;
	}

	@JsonProperty("requestId")
	public int getRequestId() {
		return requestId;
	}

	@JsonProperty("requestId")
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

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

	@JsonProperty("businessPartnerRequest")
	public BusinessPartnerRequest getBusinessPartnerRequest() {
		return businessPartnerRequest;
	}

	@JsonProperty("businessPartnerRequest")
	public void setBusinessPartnerRequest(BusinessPartnerRequest businessPartnerRequest) {
		this.businessPartnerRequest = businessPartnerRequest;
	}

}
