package com.incture.bupa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE")
public class BPVendorClassificationAttribute {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BP_VENDOR_CLASSIFICATION_ATTRIBUTE_ID")
    private Integer vendorClassificationAttributeId;
	
	@Column(name="BP_CHARACTDESCR")
    private String CharactDescr;
    
    @Column(name="BP_VALUE_NEUTRAL")
    private String ValueNeutral;
    
    @Column(name="BP_RESULT")
    private String results;
    
    @Column(name="BP_CHARACT")
    private String Charact;
    
    //"ToCharcterValue": {

    //"results": [
    
    
//    @OneToOne(cascade = CascadeType.ALL, mappedBy = "bpVendorClassificationAttribute", fetch = FetchType.LAZY)
//	private BPToCharacterValue bpToCharacterValue;
    
    
	@ManyToOne
	@JoinColumn(name = "BP_VENDOR_CLASSIFICATION_ENTITY_ID", referencedColumnName = "BP_VENDOR_CLASSIFICATION_ENTITY_ID")
	private BPVendorClassificationEntity bpVendorClassificationEntity;
}
