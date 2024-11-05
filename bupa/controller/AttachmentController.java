package com.incture.bupa.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.bupa.dto.BPDMSAttachmentsDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.service.BPFileService;

@CrossOrigin(origins = "*")
@RestController
//@ApiIgnore
@RequestMapping("/dms")
public class AttachmentController {
    @Autowired    
    private BPFileService bPFileService;
    Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    @PostMapping("/file/upload")
    public ServiceResponse<BPDMSAttachmentsDto> uploadFile(@RequestBody List<BPDMSAttachmentsDto> attachments) throws ClientProtocolException, IOException {
        return bPFileService.uploadFile(attachments,null,null);
    }
    @GetMapping(value = "/file/download/{documentId}")
    public ServiceResponse<BPDMSAttachmentsDto> downloadFile(@PathVariable String documentId) throws ClientProtocolException, IOException {
        return bPFileService.downloadFile(documentId);
    }
    @GetMapping(value = "/file/getAllFiles")
    public List<BPDMSAttachmentsDto> downloadAllFile() {
        return bPFileService.getAllFiles();
    }
    @GetMapping(value = "/getAttachmentsByRequestID")
    public List<BPDMSAttachmentsDto> getAttachmentsByRequestID(@RequestParam("requestID") String requestID) throws ClientProtocolException, IOException {
        return bPFileService.getAttachmentsByRequestID(requestID);
    }
}
