package com.incture.bupa.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BPControlDataDto {
	private Integer controlDataId;
//	private String vendorNo;
	private String bupaNo;
	private String taxNo1;
	private String taxNo2;
	private String taxNo3;
	private String taxNo4;
	private String taxNo5;
	private String taxNoType;
	private String taxType;
	private String taxJurisdiction;
	private String vatRegNo;
	private String transportZone;
	private String podRelevant;
	private String industry;
//	private String vendorCalendar;
	 private String bupaCalendar;
	private String taxNumAtResponsibleTaxAuthority;
	private String customer;
	private String authorization;
	private String tradingPartner;
//	private String requestId;
	private String corporateGroup;
	private String taxBase;
	private String fiscalAddress;
	private String socInsCode;
	private String repsName;
	private String typeOfBusiness;
	private String taxOffice;
	private String typeOfIndustr;
	private String taxNumber;
	private String locationNo1;
	private String locationNo2;
	private String checkDigit;
	private String credInfoNo;
	private String lastExtReview;
	private String trainStation;
	private String scac;
	private String carFreughtGrp;
	private String servAgntProcGrp;
	private String statGrService;
	private String actualQnSys;
	private String qmSystemTo;
	private String externalManuf;
	private String dob;
	private String placeOfBirth;
	private String sex;
	private String profession;
	private Boolean equalizationTax;
	private Boolean naturalPerson;
	private Boolean salesPurTax;
	private Boolean taxSplit;
	private Boolean socInsurance;

}
