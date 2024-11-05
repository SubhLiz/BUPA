package com.incture.bupa.service;

import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.dto.BPDMSAttachmentsDto;
import com.incture.bupa.entities.BPDMSAttachments;

public interface BPUpdatedDocumentService {
	ServiceResponse<String> uploadDoc(BPDMSAttachmentsDto attachment, String repositoryId, String folderName) throws Exception;
	
	ServiceResponse<String> uploadDocument(BPDMSAttachmentsDto attachment, String repositoryId, String folderName,Long millis) throws Exception;
	
	BPDMSAttachmentsDto getDocument(String repositoryId, String folderName, String documentId) throws Exception;
    
    boolean deleteDocument(String repositoryId, String folderName, String documentId);
    
    public BPDMSAttachmentsDto entityToDto(BPDMSAttachments attachementEntity);
    
    public BPDMSAttachments dtoToEntity(BPDMSAttachmentsDto attachmentDto);
}
