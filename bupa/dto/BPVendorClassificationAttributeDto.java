package com.incture.bupa.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BPVendorClassificationAttributeDto {
	private Integer vendorClassificationAttributeId;
	@JsonProperty("CharactDescr")
    private String CharactDescr;
	@JsonProperty("ValueNeutral")
	private String ValueNeutral;
	@JsonProperty("ToCharacterValue")
	private Map<String, Object> bpToCharacterValue;
	@JsonProperty("Charact")
    private String Charact;
}
