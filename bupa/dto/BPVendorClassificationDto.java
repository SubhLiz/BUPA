package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPVendorClassificationDto {
	private Integer vendorClassificationId;
	private String entityName;
	private String attributeName;
	private String attributeValue;
	private String entityNameList;
}
