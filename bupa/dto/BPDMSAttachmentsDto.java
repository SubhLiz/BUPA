package com.incture.bupa.dto;

import java.util.Date;

import lombok.Data;
@Data
public class BPDMSAttachmentsDto {
//	 private String attachmentID;
	    private String documentId;
	    private String requestID;
	    private String documentName;
	    private String documentType;
	    private String documentUrl;
	    private String updatedBy;
	    private String updatedOn;
	    private Date vmExpiryDate;
	    private String encodedFileContent;
	    private Boolean isPosted;
}
