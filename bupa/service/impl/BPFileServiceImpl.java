package com.incture.bupa.service.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.incture.bupa.entities.BPGeneralData;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.dto.BPDMSAttachmentsDto;
import com.incture.bupa.entities.BPDMSAttachments;
import com.incture.bupa.repository.BPDMSAttachmentsRepository;
import com.incture.bupa.service.BPFileService;
import com.incture.bupa.service.BPUpdatedDocumentService;
import com.incture.bupa.service.TestService;
import com.incture.bupa.utils.ApplicationConstants;
import com.incture.bupa.utils.DestinationUtil;

import org.springframework.http.HttpStatus;
import java.util.Date;
import java.util.HashMap;
@Service
public class BPFileServiceImpl implements BPFileService {
	 private static final Logger logger = LoggerFactory.getLogger(BPFileServiceImpl.class);
//	@Value("${dms.REPOSITORY_ID}")
//	private String REPOSITORY_ID;
	
	private static String repositoryId;
    
//    @Value("${dms.FOLDER_NAME}")
    private String FOLDER_NAME="VOB_ATTACHMENT_FOLDER";
    @Autowired    
    BPDMSAttachmentsRepository bpAttachmentsRepository;
    @Autowired    
    BPUpdatedDocumentService documentService;
    
    @Autowired
	private DestinationUtil destinationUtil;
//    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//    String formatPattern = "MM/dd/yyyy HH:mm:ss";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String formatPattern = "yyyy-MM-dd HH:mm:ss";
    DateFormat df = new SimpleDateFormat(formatPattern);
    
    private HashMap<String,String> getDestinationDetails() throws ClientProtocolException, IOException {
		HashMap<String, String> hashMap = new HashMap<>();
		String destDetails = destinationUtil.readMdgDestination("viatris-vm-java", null, null);
		System.out.println("****");

        org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
        logger.info("Json object from destination :"+resObj);
        
        repositoryId =resObj.optJSONObject("destinationConfiguration").optString("repository_id");
        hashMap.put("repositoryId",repositoryId);
        
        System.out.println("****");
		return hashMap;
	}
    @Override    
    public ServiceResponse<BPDMSAttachmentsDto> uploadFile(List<BPDMSAttachmentsDto> attachments, BPGeneralData bpGeneralData,Long millis) throws ClientProtocolException, IOException {
    	HashMap<String, String> hashMap = getDestinationDetails();
        ServiceResponse<BPDMSAttachmentsDto> response = new ServiceResponse<>();
        try        {
        	BPDMSAttachmentsDto attachmentsDto = new BPDMSAttachmentsDto();
            List<BPDMSAttachmentsDto> responseData = new ArrayList<>();
            
            for(BPDMSAttachmentsDto attachment : attachments)
            {
//                System.out.println("started 1 file Service impl ");
                ServiceResponse<String> documentResponse = documentService.uploadDocument(attachment, hashMap.get("repositoryId"),
                        FOLDER_NAME,millis);
                if(documentResponse.getErrorCode() == HttpStatus.OK.value())
                {
                    System.out.println("[VM]: BPFileServiceImpl.uploadFile(): File uploaded successfully! Attachment ID: "                            + documentResponse.getData());
                    attachment.setDocumentId(documentResponse.getData());
                    BPDMSAttachments attachmentEntity = documentService.dtoToEntity(attachment);
                    attachmentEntity.setBpGeneralData(bpGeneralData);
                    BPDMSAttachments attachDoc=bpAttachmentsRepository.save(attachmentEntity);
                    attachmentsDto=documentService.entityToDto(attachDoc);
                    attachment.setEncodedFileContent("");
                    responseData.add(attachment);
                }   
            }
            response.setDataList(responseData);
            response.setMessage(ApplicationConstants.SUCCESS);
            response.setStatus(HttpStatus.OK.getReasonPhrase());
            response.setErrorCode(HttpStatus.OK.value());
            return response;
        }
        catch(Exception e)
        {
            System.err.println("[Error]: " + e.getMessage());
            StackTraceElement[] stktrace = e.getStackTrace();
            for(int i = 0; i < stktrace.length; i++)
            {
                System.err.println("[Error]: Line " + i + " of error: " + stktrace[i].toString());
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            response.setMessage(e.getMessage());
            response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }
    }
    @Override    
    public ServiceResponse<BPDMSAttachmentsDto> downloadFile(String attachmentId) throws ClientProtocolException, IOException {
        ServiceResponse<BPDMSAttachmentsDto> response = new ServiceResponse<>();
        HashMap<String, String> hashMap = getDestinationDetails();
        try {
        	BPDMSAttachments entity = bpAttachmentsRepository.findByDocumentID(attachmentId);
        	BPDMSAttachmentsDto attachDto = documentService.entityToDto(entity);
            attachDto.setEncodedFileContent(documentService.getDocument(hashMap.get("repositoryId"),
                    FOLDER_NAME, attachmentId).getEncodedFileContent());
//            attachDto.setUpdatedOn(df.format(new Date()));
            response.setData(attachDto);
            response.setMessage(ApplicationConstants.SUCCESS);
            response.setStatus(HttpStatus.OK.getReasonPhrase());
            response.setErrorCode(HttpStatus.OK.value());
            return response;
        } catch (Exception e) {
            System.err.println("[Error]: " + e.getMessage());
            StackTraceElement[] stktrace = e.getStackTrace();
            for (int i = 0; i < stktrace.length; i++) {
                System.err.println("[Error]: Line " + i + " of error: " + stktrace[i].toString());
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            response.setMessage(e.getMessage());
            response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }
    }
    @Override    public List<BPDMSAttachmentsDto> getAllFiles() {
        List<BPDMSAttachmentsDto> cmAttachmentsDtoList = new ArrayList<>();
      for(BPDMSAttachments cmAttachments : bpAttachmentsRepository.findAll()){
          cmAttachmentsDtoList.add(documentService.entityToDto(cmAttachments));
      }
      return  cmAttachmentsDtoList;
    }
    @Override    public List<BPDMSAttachmentsDto> getAttachmentsByRequestID(String requestID) throws ClientProtocolException, IOException {
        List<BPDMSAttachments> attachments = bpAttachmentsRepository.findByBpRequestID(requestID);
        List<BPDMSAttachmentsDto> attachmentsDtos = new ArrayList<>();
        HashMap<String, String> hashMap = getDestinationDetails();
        if(attachments !=null){
            for(BPDMSAttachments entity : attachments) {
            	BPDMSAttachmentsDto attachDto = documentService.entityToDto(entity);
            	try {
					attachDto.setEncodedFileContent(documentService.getDocument(hashMap.get("repositoryId"),
					        FOLDER_NAME, entity.getDocumentID()).getEncodedFileContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//                attachDto.setUpdatedOn(df.format(new Date()));
                attachmentsDtos.add(attachDto);
            }
        }
        return attachmentsDtos;
    }
}
