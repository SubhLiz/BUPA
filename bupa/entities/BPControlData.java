package com.incture.bupa.entities;


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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_CONTROL_DATA")
public class BPControlData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BP_CONTROL_DATA_ID")
    private Integer controlDataId;

//    @Column(name = "BP_VENDOR_NO")
//    private String vendorNo;
    
    @Column(name="BP_BUPA_NO")
	private String bupaNo;

    @Column(name = "BP_TAX_NO_1")
    private String taxNo1;

    @Column(name = "BP_TAX_NO_2")
    private String taxNo2;

    @Column(name = "BP_TAX_NO_3")
    private String taxNo3;

    @Column(name = "BP_TAX_NO_4")
    private String taxNo4;

    @Column(name = "BP_TAX_NO_5")
    private String taxNo5;

    @Column(name = "BP_TAX_NO_TYPE")
    private String taxNoType;

    @Column(name = "BP_TAX_TYPE")
    private String taxType;

    @Column(name = "BP_TAX_JURISDICTION")
    private String taxJurisdiction;

    @Column(name = "BP_VAT_REG_NO")
    private String vatRegNo;

    @Column(name = "BP_TRANSPORT_ZONE")
    private String transportZone;

    @Column(name = "BP_POD_RELEVANT")
    private String podRelevant;

    @Column(name = "BP_INDUSTRY")
    private String industry;
    
    @Column(name = "BP_BUPA_CALENDAR")
    private String bupaCalendar;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BP_BIRTH_DATE")
    private Date birthDate;

    @Column(name = "BP_TAX_NUM_AT_RESPONSIBLE_TAX_AUTHORITY")
    private String taxNumAtResponsibleTaxAuthority;

    @Column(name = "BP_CUSTOMER")
    private String customer;

    @Column(name = "BP_AUTHORIZATION")
    private String authorization;

    @Column(name = "BP_TRADING_PARTNER")
    private String tradingPartner;

//    @Column(name = "BP_REQUEST_ID")
//    private String requestId;

    @Column(name = "BP_CORPORATE_GROUP")
    private String corporateGroup;

    @Column(name = "BP_TAX_BASE")
    private String taxBase;

    @Column(name = "BP_FISCAL_ADDRESS")
    private String fiscalAddress;

    @Column(name = "BP_SOC_INS_CODE")
    private String socInsCode;

    @Column(name =  "BP_REPS_NAME")
    private String repsName;

    @Column(name = "BP_TYPE_OF_BUSINESS")
    private String typeOfBusiness;

    @Column(name = "BP_TAX_OFFICE")
    private String taxOffice;

    @Column(name = "BP_TYPE_OF_INDUSTR")
    private String typeOfIndustr;

    @Column(name = "BP_TAX_NUMBER")
    private String taxNumber;

    @Column(name = "BP_LOCATION_NO_1")
    private String locationNo1;

    @Column(name = "BP_LOCATION_NO_2")
    private String locationNo2;

    @Column(name = "BP_CHECK_DIGIT")
    private String checkDigit;

    @Column(name = "BP_CRED_INFO_NO")
    private String credInfoNo;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BP_LAST_EXT_REVIEW")
    private Date lastExtReview;

    @Column(name = "BP_TRAIN_STATION")
    private String trainStation;

    @Column(name = "BP_SCAC")
    private String scac;

    @Column(name = "BP_CAR_FREIUGHT_GRP")
    private String carFreughtGrp;

    @Column(name = "BP_SERV_AGENT_PROC_GRP")
    private String servAgntProcGrp;

    @Column(name = "BP_STAT_GR_SERVICE")
    private String statGrService;

    @Column(name="BP_ACTUAL_QN_SYS")
    private String actualQnSys;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BP_QM_SYSTEM_TO")
    private Date qmSystemTo;

    @Column(name = "BP_EXTERNAL_MANUF")
    private String externalManuf;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BP_DOB")
    private Date dob;

    @Column(name = "BP_PLACE_OF_BIRTH")
    private String placeOfBirth;

    @Column(name = "BP_SEX")
    private String sex;

    @Column(name = "BP_PROFESSION")
    private String profession;
    
    @Column(name="BP_EQUALIZATION_TAX")
    private Boolean equalizationTax;
    
    @Column(name="BP_NATURAL_PERSON")
    private Boolean naturalPerson;
    
    @Column(name="BP_SALES_PYR_TAX")
    private Boolean salesPurTax;
    
    @Column(name="BP_TAX_SPLIT")
    private Boolean taxSplit;
    
    @Column(name="BP_SOC_INSURANCE")
    private Boolean socInsurance;
    
    
    @ManyToOne
    @JoinColumn(name = "BP_REQUEST_ID",referencedColumnName = "BP_REQUEST_ID")
//    @JsonBackReference
    private BPGeneralData bpGeneralData;
}
