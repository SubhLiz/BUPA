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
@Table(name = "BP_WITHHOLDING_TAX")
public class BPWithholdingTax {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BP_WITHOLDING_TAX_ID")
	private Integer withholdingTaxId;
	
	@Column(name="BP_WITHOLDING_TAX_TYPE")
	private String withholdingTaxType;
	
	@Column(name="BP_WITHHOLDING_TAX_CODE")
	private String withholdingTaxCode;
	
	
	@Column(name="BP_WH_TAX_COUNTRY")
	private String whTaxCountry;
	
	@Column(name="BP_LIABLE")
	private Boolean liable;
	
	@Column(name = "BP_RECIPIENT_TYPE")
	private String recipientType;
	
	
	@Column(name="BP_WTAX_ID")
	private String wTaxId;
	
	@Column(name="BP_EXEM_PERCENTAGE")
	private String exemPercentage;
	
	@Column(name="BP_EXEM_RESN")
	private String exemResn;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_EXEMT_FROM")
	private Date exemptFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="BP_EXEMT_TO")
	private Date exemptTo;
	
	@Column(name="BP_DESCRIPTION")
	private String description;
	
	@Column(name="BP_EXEMPTION_NO")
	private String exemptionNo;
	
	@Column(name="BP_IS_NEW")
	private Boolean isNew;
	
	@ManyToOne
	@JoinColumn(name = "BP_COMP_CODE_INFO_ID", referencedColumnName = "BP_COMP_CODE_INFO_ID")
	private BPCompanyCodeInfo bpCompanyCodeInfo;
	
}