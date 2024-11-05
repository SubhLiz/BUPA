package com.incture.bupa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.dto.BPDMSAttachmentsDto;
import com.incture.bupa.entities.BPGeneralData;

public interface BPFileService {
	public ServiceResponse<BPDMSAttachmentsDto> uploadFile(List<BPDMSAttachmentsDto> attachments, BPGeneralData bpGeneralData,Long millis) throws ClientProtocolException, IOException;
	
    public ServiceResponse<BPDMSAttachmentsDto> downloadFile(String attachmentId) throws ClientProtocolException, IOException;
    
    public List<BPDMSAttachmentsDto> getAllFiles();
    
    List<BPDMSAttachmentsDto> getAttachmentsByRequestID(String requestID) throws ClientProtocolException, IOException;

}
