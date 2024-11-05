package com.incture.bupa.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data

public class BPRequestGeneralDataDto {
	private String requestId;
//	private String vendorNo;
//	private String vendorAccountGrp;
	private String bupaNo;
	private String bupaAccountGrp;
	private String name1;
	private String name2;
	private String name3;
	private String name4;
	private Integer requestTypeId;
	private Integer statusId;
	private String searchTerm1;
	private String searchTerm2;
	private String birthDate;
	private Character isDeleted = 'N';
	private String title;
	private String naturalPer;
	private String industry;
	private String supplierAccountId;
	private String centralDeletionFlag;
	private String centralPostingBlock;
	private String centralPurchasingBlock;
	private String centralDeletionBlock;
	private String poBox;
	private String poPostalCode;
	private String poCompanyPostalCode;
	private String supplierURL;
	private String corporateGroupKey;
	private String creditInformationNumber;
	private String recordCreationUser;
	private String recordCreationDate;
	private String secondTelephoneNumber;
	private String blockFunction;
	private String createdBy;
	private String requestorEmail;
	private String createdOn;
	private String alternativePayee;
	private String dmeIndicator;
	private String instructionKey;
	private String isrNumber;
	private Boolean individualEntries;
	private Boolean entriesForReference;
	private String contactPersonName;
	private Integer scenario;
	private String systemId;
	private String vendorType;
	private Boolean isDraft;
	private Boolean isRequestDetail;
	private String poType;
	private Boolean extendCompanyCode;
	private Boolean extendPurchaseOrg;
	private Boolean extendAdditionalData;
	private Boolean skipBankValidation;
	private String subProcessType;
	
	
//	private boolean validate;
//	private BPRequestAddressDto bpAddressInfo;
	private BPAddressInfoDto bpAddressInfo;
	private BPCommunicationDto bpCommunication;
	private ArrayList<BPControlDataDto> bpControlData;
	private ArrayList<BPBankInformationDto>bpBankInformation;
	private ArrayList<BPContactInformationDto>bpContactInformation;
	private ArrayList<BPCompanyCodeInfoDto>bpCompanyCodeInfo;
	private ArrayList<BPPurchasingOrgDetailDto> bpPurchasingOrgDetail;
//	private BPSupplierDto bpSupplier;
	private List<BPDMSAttachmentsDto> bpDmsAttachments;
	private List<BPCommentsDto> bpComments;
	private ArrayList<BPVendorClassificationEntityDto>bpVendorClassificationEntity;
}