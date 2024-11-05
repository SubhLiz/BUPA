package com.incture.bupa.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BPVendorClassificationEntityDto {
	private Integer vendorClassificationEntityId;
	@JsonProperty("Classnum")
	private String Classnum;
	@JsonProperty("Description")
	private String Description;
	private List<BPVendorClassificationAttributeDto> bpVendorClassificationAttribute;
}
