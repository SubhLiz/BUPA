package com.incture.bupa.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.BPAuditLogDto;
import com.incture.bupa.dto.BPRequestGeneralDataDto;
import com.incture.bupa.dto.CreateorUpdateBPResponseDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.service.AuditLogService;
import com.incture.bupa.service.CPIService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@CrossOrigin("*")
@RequestMapping("/auditLog")
public class AuditLogController {
	
	@Autowired
    private AuditLogService auditLogService;
	
	Logger logger = LoggerFactory.getLogger(AuditLogController.class);
	
	@ApiOperation(value = "Save and Delete Audit Log", response = String.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully added vendor details to the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@PostMapping("/saveAndDeleteAuditLogInfo")
	public ResponseEntity<ServiceResponse<String>> saveAndDeleteAuditLogInfo(@RequestBody List<BPAuditLogDto> bpAuditLogDto)  {
		ServiceResponse<String> response = new ServiceResponse<>();
		try {
			String saveAuditLogResponse= auditLogService.saveAndDeleteAuditLogInfo(bpAuditLogDto);
			response.setData(saveAuditLogResponse);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("AuditLogController.saveAndDeleteAuditLogInfo()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	@ApiOperation(value = "Get Audit Log by Request Id", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully added vendor details to the table"),
			@ApiResponse(code = 401, message = "You are not authorized to load document"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
	@GetMapping("/getAuditLogInfoByRequestId")
	public ResponseEntity<ServiceResponse<List<BPAuditLogDto>>> getAuditLogInfoByRequestId(@RequestParam("requestId") String requestId)  {
		ServiceResponse<List<BPAuditLogDto>> response = new ServiceResponse<>();
		try {
			List<BPAuditLogDto> saveAuditLogResponse= auditLogService.getAuditLogInfoByRequestId(requestId);
			response.setData(saveAuditLogResponse);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			e.printStackTrace();
			response.setMessage(AppConstants.FAIL_MESSAGE); 
			response.setError(e.getMessage());
			response.setErrorCode(500);
			response.setStatus("FAILED");
			System.err.println("AuditLogController.getAuditLogInfoByRequestId()" + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
