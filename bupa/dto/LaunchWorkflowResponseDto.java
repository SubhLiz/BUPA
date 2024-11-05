package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "definitionId", "definitionVersion", "subject", "status", "businessKey", "startedAt",
		"startedBy", "completedAt" })
public class LaunchWorkflowResponseDto {

	@JsonProperty("id")
	private String id;
	@JsonProperty("definitionId")
	private String definitionId;
	@JsonProperty("definitionVersion")
	private String definitionVersion;
	@JsonProperty("subject")
	private String subject;
	@JsonProperty("status")
	private String status;
	@JsonProperty("businessKey")
	private String businessKey;
	@JsonProperty("startedAt")
	private String startedAt;
	@JsonProperty("startedBy")
	private String startedBy;
	@JsonProperty("version")
	private Integer version;

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("definitionId")
	public String getDefinitionId() {
		return definitionId;
	}

	@JsonProperty("definitionId")
	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}

	@JsonProperty("definitionVersion")
	public String getDefinitionVersion() {
		return definitionVersion;
	}

	@JsonProperty("definitionVersion")
	public void setDefinitionVersion(String definitionVersion) {
		this.definitionVersion = definitionVersion;
	}

	@JsonProperty("subject")
	public String getSubject() {
		return subject;
	}

	@JsonProperty("subject")
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@JsonProperty("status")
	public String getStatus() {
		return status;
	}

	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("businessKey")
	public String getBusinessKey() {
		return businessKey;
	}

	@JsonProperty("businessKey")
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	@JsonProperty("startedAt")
	public String getStartedAt() {
		return startedAt;
	}

	@JsonProperty("startedAt")
	public void setStartedAt(String startedAt) {
		this.startedAt = startedAt;
	}

	@JsonProperty("startedBy")
	public String getStartedBy() {
		return startedBy;
	}

	@JsonProperty("startedBy")
	public void setStartedBy(String startedBy) {
		this.startedBy = startedBy;
	}

	@JsonProperty("version")
	public Integer getVersion() {
		return version;
	}

	@JsonProperty("version")
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "LaunchWorkflowResponseDto [id=" + id + ", definitionId=" + definitionId + ", definitionVersion="
				+ definitionVersion + ", subject=" + subject + ", status=" + status + ", businessKey=" + businessKey
				+ ", startedAt=" + startedAt + ", startedBy=" + startedBy + ", version=" + version + "]";
	}
	
}
