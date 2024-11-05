package com.incture.bupa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_TELEPHONE_INFO")
public class BPTelephone {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BP_TELEPHONE_ID")
    private Integer telephoneId;

    @Column(name = "BP_REQUEST_ID")
    private String requestId;

    @Column(name = "BP_COUNTRY")
    private String country;

    @Column(name = "BP_COUNTRY_CODE")
    private String countryCode;

    @Column(name = "BP_EXTENSION")
    private String extension;

    @Column(name = "BP_TELEPHONE")
    private String telephone;

    @Column(name = "BP_STANDARD_NUMBER")
    private Boolean standardNumber;

    @Column(name = "BP_SMS_ENABLED")
    private Boolean smsEnabled;

    @Column(name = "BP_DO_NOT_USE")
    private Boolean doNotUse;

    @Column(name = "BP_COMMENTS")
    private String comments;
    
    @Column(name="BP_TELEPHONE_CODE")
    private String telephoneCode;
    
    @Column(name="BP_MOB_PHONE")
    private String mobilePhone;
    
    @Column(name="BP_IS_NEW")
    private Boolean isNew;
    
    @ManyToOne
	@JoinColumn(name = "BP_COMMUNICATION_ID", referencedColumnName = "BP_COMMUNICATION_ID")
	private BPCommunication bpCommunication;
}
