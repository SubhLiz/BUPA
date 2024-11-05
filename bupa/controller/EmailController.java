package com.incture.bupa.controller;
//package com.incture.bupa.controller;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.incture.bupa.constants.AppConstants;
//import com.incture.bupa.dto.ServiceResponse;
//import com.incture.bupa.exceptions.InvalidInputFault;
//import com.incture.bupa.exceptions.NoResultFault;
//import com.incture.bupa.service.MailService;
//import com.incture.bupa.utils.MailRequestDto;
//
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiResponse;
//import io.swagger.annotations.ApiResponses;
//import springfox.documentation.annotations.ApiIgnore;
//
//@RestController
//@RequestMapping("email")
//@ApiIgnore
//public class EmailController {
//	@Autowired
//	private MailService mailService;
//	@ApiOperation(value = "Send Email", response = String.class)
//	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully load the document"),
//			@ApiResponse(code = 401, message = "You are not authorized to load document"),
//			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden")})
//	@PostMapping(path = "/sendEmail")
//	public ResponseEntity<ServiceResponse<String>> sendEmail(@RequestBody MailRequestDto mailRequestDto) throws InvalidInputFault, NoResultFault {
//		ServiceResponse<String> response = new ServiceResponse<>();
//		try {
//			mailService.sendMail(mailRequestDto);
//			response.setData("Mail Send Successfully");
//			return ResponseEntity.ok().body(response);
//		} catch (Exception e) {
//			response.setMessage(AppConstants.FAIL_MESSAGE); // TODO: REFINE
//																	// LATER
//			response.setError(e.getMessage());
//			response.setErrorCode(500);
//			response.setStatus("FAILED");
//			System.err.println("DocumentController.uploadDocument() " + e.getMessage());
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//		}
//	}
//}
