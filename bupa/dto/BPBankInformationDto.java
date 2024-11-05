package com.incture.bupa.dto;

import lombok.Data;
@Data
public class BPBankInformationDto {
	private Integer bankId;
//	private String vendorNo;
	private String bupaNo;
	private String country;
	private String bankKey;
	private String message;
	private String indicator;
	private String bankAccountNo;
	private String accHolderName;
	private String controlKey;
	private String bankControlKey;
	private String iban;
	private String bankType;
	private String payee;
	private String instructionKey;
	private String swift;
	private String currency;
	private String bankName;
	private String bankBranch;
	private String street;
	private String city;
	private String region;
	private String collectionAuthInd;
	private String refSpecBankDetails;
	private String bankCountryKey;
	private String bankCountry;
	private String validFrom;
	private String validTo;
	private String ak;
//	private String ibanValue;
	private String bankT;
	private String referenceDetails;
	private Boolean debitAuthorization;
	private Boolean isNew;
	private Boolean isDeleted;
	private String displayParameter;
	
}
