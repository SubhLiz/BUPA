package com.incture.bupa.controller;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.BPFlexibleWorkflowDto;
import com.incture.bupa.dto.BPRequestGeneralDataDto;
import com.incture.bupa.dto.LaunchWorkflowResponseDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.dto.WorkflowContextDto;
import com.incture.bupa.dto.WorkflowResponseDto;
import com.incture.bupa.exceptions.InvalidInputFault;
import com.incture.bupa.service.BPDetailService;
import com.incture.bupa.service.WorkflowLauncher;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin("*")
@RequestMapping("/workflow")
@Transactional(propagation = Propagation.REQUIRED)
public class WorkflowController {
	@Autowired
    private WorkflowLauncher workflowLauncher ;

	private Logger logger=LoggerFactory.getLogger(WorkflowController.class);
//	@ApiOperation(value = " Save Flexible Workflow Rules Data and")
//	@PostMapping(path = "/saveFlexibleWorkflowRulesData")
//	public ResponseEntity<ServiceResponse<?>>saveFlexibleWorkflowRulesData(@RequestBody List<BPFlexibleWorkflowDto> bpFlexibleWorkflowDto)throws InvalidInputFault{
//		ServiceResponse<String>response=new ServiceResponse<String>();
//		try {
//			response.setData(workflowLauncher.saveFlexibleWorkflowRulesData(bpFlexibleWorkflowDto));
//			response.setMessage(AppConstants.SAVED_SUCCESS);
//			return ResponseEntity.ok().body(response);
//		} catch (Exception e) {
//			System.err.println("[Error]: " + e.getMessage());
//            StackTraceElement[] stktrace = e.getStackTrace();
//            for(int i = 0; i < stktrace.length; i++)
//            {
//                System.err.println("[Error]: Line " + i + " of error: " + stktrace[i].toString());
//            }
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            response.setMessage(e.getMessage());
//            response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}
//
//	@ApiOperation(value = " Response from Flexible Workflow Response Data")
//	@GetMapping(path = "/flexibleWorkflowResponseData")
//	public ResponseEntity<ServiceResponse<?>>submit()throws InvalidInputFault{
//		ServiceResponse<WorkflowResponseDto>response=new ServiceResponse<WorkflowResponseDto>();
//		try {
//			response.setData(workflowLauncher.workflowResponse());
//			response.setMessage(AppConstants.SAVED_SUCCESS);
//			return ResponseEntity.ok().body(response);
//		} catch (Exception e) {
//			System.err.println("[Error]: " + e.getMessage());
//            StackTraceElement[] stktrace = e.getStackTrace();
//            for(int i = 0; i < stktrace.length; i++)
//            {
//                System.err.println("[Error]: Line " + i + " of error: " + stktrace[i].toString());
//            }
//            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            response.setMessage(e.getMessage());
//            response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}
	@ApiOperation(value = " Response from Flexible Workflow Response Data")
	@PostMapping(path = "/contextResponseData")
	public ResponseEntity<ServiceResponse<?>>submit(@RequestBody WorkflowContextDto bpFlexibleWorkflowDto){
		ServiceResponse<WorkflowContextDto>response=new ServiceResponse<WorkflowContextDto>();
		try {
			response.setData(workflowLauncher.contextResponse(bpFlexibleWorkflowDto));
			response.setMessage(AppConstants.SAVED_SUCCESS);
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			System.err.println("[Error]: " + e.getMessage());
            StackTraceElement[] stktrace = e.getStackTrace();
            for(int i = 0; i < stktrace.length; i++)
            {
                System.err.println("[Error]: Line " + i + " of error: " + stktrace[i].toString());
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            response.setMessage(e.getMessage());
            response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
}
