package com.incture.bupa.entities;


import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BP_DMS_ATTACHMENTS")
public class BPDMSAttachments{
	@Id    
    @Column(name = "BP_ATTACHMENT_ID")
    private String attachmentID=UUID.randomUUID().toString();
    private String requestID;
    @Column(name="BP_DOCUMENT_ID",length = 100)
    private String documentID;
    @Column(name = "BP_DOCUMENT_NAME",length = 255)
    private String documentName;
    @Column(name = "BP_DOCUMENT_TYPE",length = 100)
    private String documentType;
    @Column(name = "BP_DOCUMENT_URL",length = 200)
    private String documentUrl;
    @Column(name = "BP_UPDATED_BY",length = 100)
    private String updatedBy;
    @Column(name="BP_POSTED")
    private Boolean isPosted;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "BP_UPDATED_ON")
    private Date updatedOn;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "BP_EXPIRY_DATE")
    private Date bpExpiryDate;
    @ManyToOne
	 @JoinColumn(name = "BP_REQUEST_ID", referencedColumnName = "BP_REQUEST_ID")
//	 @JsonBackReference
    private BPGeneralData bpGeneralData;
}
