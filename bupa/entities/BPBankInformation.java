package com.incture.bupa.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

//@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_BANK_INFORMATION")
public class BPBankInformation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BP_BANK_INFO_ID")
	private Integer bankId;
	
//	@Column(name="BP_VENDOR_NO")
//	private String vendorNo;
	
	@Column(name="BP_BUPA_NO")
	private String bupaNo;
	
	@Column(name="BP_COUNTRY")
	private String country;
	
	@Column(name="BP_BANK_KEY")
	private String bankKey;
	
	@Column(name="BP_MESSAGE")
	private String message;
	
	@Column(name="BP_INDICATOR")
	private String indicator;
	
	@Column(name="BP_BANK_ACC_NO")
	private String bankAccountNo;
	
	@Column(name="BP_ACC_HOLDER")
	private String accHolderName;
	
	@Column(name="BP_BANK_CONTROL_KEY")
	private String bankControlKey;
	
	@Column(name="BP_IBAN")
	private String iban;
	
	@Column(name="BP_PARTNER_BANK_TYPE")
	private String bankType;
	
	@Column(name="BP_ALTERNATE_PAYEE")
	private String payee;
	
	@Column(name="BP_INSTRUCTION_KEY")
	private String instructionKey;
	
	@Column(name="BP_SWIFT_BIC")
	private String swift;
	
	@Column(name="BP_CURRENCY")
	private String currency;
	
	@Column(name="BP_BANK_NAME")
	private String bankName;
	
	@Column(name="BP_BRANCH_NAME")
	private String bankBranch;
	
	@Column(name="BP_STREET_NAME")
	private String street;
	
	@Column(name="BP_CITY")
	private String city;
	
	@Column(name="BP_REGION")
	private String region;
	
	@Column(name="BP_COLLECTION_AUTH_INDICATOR")
	private String collectionAuthInd;
	
	@Column(name="BP_REF_SPEC_BANK_DETAILS")
	private String refSpecBankDetails;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_VALID_FROM")
	private Date validFrom;

	@Column(name = "BP_BANK_COUNTRY_KEY")
	private String bankCountryKey;

	@Column(name = "BP_BANK_COUNTRY")
	private String bankCountry;

	@Column(name = "BP_CONTROL_KEY")
	private String controlKey;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_VALID_TO")
	private Date validTo;
	
	//added
	@Column(name="BP_AK")
	private String ak;
	
//	@Column(name="BP_IBAN_VALUE")
//	private String ibanValue;
	
	@Column(name="BP_BANKT")
	private String bankT;
	
	@Column(name="BP_REFERENCE_DETAILS")
	private String referenceDetails;
	
	@Column(name="BP_DEBIT_AUTHORIZATION")
	private Boolean debitAuthorization;
	
	@Column(name="BP_IS_NEW")
	private Boolean isNew;
	
	@Column(name="BP_IS_DELETED")
	private Boolean isDeleted;
	
	@Column(name="BP_DISPALY_PARAMETER")
	private String displayParameter;
	
	
	
	@ManyToOne
    @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//    @JsonBackReference
	private BPGeneralData bpGeneralData;

	@Override
	public String toString() {
		return "BPBankInformation [bankId=" + bankId + ", bupaNo=" + bupaNo + ", country=" + country + ", bankKey="
				+ bankKey + ", message=" + message + ", indicator=" + indicator + ", bankAccountNo=" + bankAccountNo
				+ ", accHolderName=" + accHolderName + ", bankControlKey=" + bankControlKey + ", iban=" + iban
				+ ", bankType=" + bankType + ", payee=" + payee + ", instructionKey=" + instructionKey + ", swift="
				+ swift + ", currency=" + currency + ", bankName=" + bankName + ", bankBranch=" + bankBranch
				+ ", street=" + street + ", city=" + city + ", region=" + region + ", collectionAuthInd="
				+ collectionAuthInd + ", refSpecBankDetails=" + refSpecBankDetails + ", validFrom=" + validFrom
				+ ", bankCountryKey=" + bankCountryKey + ", bankCountry=" + bankCountry + ", controlKey=" + controlKey
				+ ", validTo=" + validTo + ", ak=" + ak + ", bankT=" + bankT + ", referenceDetails=" + referenceDetails
				+ ", debitAuthorization=" + debitAuthorization + ", bpGeneralData=" + bpGeneralData + "]";
	}

	
}
