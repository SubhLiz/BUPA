package com.incture.bupa.dto;

import lombok.Data;

@Data
public class BPWithholdingTaxDto {
	private Integer withholdingTaxId;
	private String withholdingTaxType;
	private String withholdingTaxCode;
	private String whTaxCountry;
	private Boolean liable;
	private String recipientType;
	private String wTaxId;
	private String exemPercentage;
	private String exemResn;
	private String exemptFrom;
	private String exemptTo;
	private String description;
	private String exemptionNo;
	private Boolean isNew;
}
