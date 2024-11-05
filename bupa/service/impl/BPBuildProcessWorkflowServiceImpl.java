//package com.incture.bupa.service.impl;
//
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.ResponseEntity;
//
//import com.incture.bupa.dto.BPBuildProcessWorkflowContextDto;
//import com.incture.bupa.dto.BPRequestGeneralDataDto;
//import com.incture.bupa.service.BPBuildProcessWorkflowService;
//import com.incture.bupa.utils.ApplicationConstants;
//
//public class BPBuildProcessWorkflowServiceImpl implements BPBuildProcessWorkflowService{
//	Logger logger = LoggerFactory.getLogger(this.getClass());
//	@Override
//	public ResponseEntity<Object> triggerWorkflow(BPBuildProcessWorkflowContextDto bpBuildProcessWorkflowContextDto) {
//		try {
//			HttpClient client = HttpClientBuilder.create().build();
//			HttpPost httpPost = new HttpPost(ApplicationConstants.WORKFLOW_BASE_URL);
//
//			String token = bpServiceUtils.generateTokenForOauth(BPDestinationConstants.WORKFLOW_TOKEN_URL,
//					BPDestinationConstants.WORKFLOW_CLIENT_ID, BPDestinationConstants.WORKFLOW_CLIENT_SECRET);
//
//			BPWorkFlowTriggerDto dto = new BPWorkFlowTriggerDto();
//			dto.setContext(bpWorkflowContextDto);
//			dto.setDefinitionId(BPDestinationConstants.WORKFLOW_DEFINITION_ID);
//
//			ObjectMapper obj = new ObjectMapper();
//			String payload = obj.writeValueAsString(dto);
//			logger.info("[BPWorkflowServiceImpl][triggerWorkflow] Payload : {}", payload);
//
//			StringEntity entity = new StringEntity(payload);
//			entity.setContentType(BPApplicationConstants.APPLICATION_JSON);
//
//			httpPost.addHeader(BPApplicationConstants.CONTENT_TYPE, BPApplicationConstants.APPLICATION_JSON);
//			httpPost.addHeader(BPApplicationConstants.AUTHORIZATION, BPApplicationConstants.BEARER_AUTH + token);
//			httpPost.setEntity(entity);
//
//			HttpResponse httpResponse = client.execute(httpPost);
//
//			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.CREATED.value()) {
//				return ResponseEntity.ok("Workflow triggered successfully");
//			} else {
//				logger.error("[BPWorkflowServiceImpl][triggerWorkflow] Error : {}", httpResponse.toString());
//				return new ResponseEntity<>("Unable to trigger workflow", HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		} catch (Exception e) {
//			logger.error("[BPWorkflowServiceImpl][triggerWorkflow] Exception : {}", e.toString());
//			return new ResponseEntity<>(e.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	@Override
//	public void triggerOnboardingWorkflow(String requestId, BPRequestGeneralDataDto bpRequestGeneralDataDto ) {
//		BPBuildProcessWorkflowContextDto bpBuildProcessWorkflowContextDto = prepareWorkflowPayload(requestId, bpRequestGeneralDataDto);
//		triggerWorkflow(bpBuildProcessWorkflowContextDto);
//	}
//
//	private BPBuildProcessWorkflowContextDto prepareWorkflowPayload(String requestId, BPRequestGeneralDataDto bpRequestGeneralDataDto) {
//		try {
//			BPBuildProcessWorkflowContextDto bpBuildProcessWorkflowContextDto = new BPBuildProcessWorkflowContextDto();
//
//			StringBuilder nameBuilder = new StringBuilder();
//
//				nameBuilder.append(bpRequestGeneralDataDto.getName1());
//			String customerName = nameBuilder.toString();
//
//			Date date = new Date();
//			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//
//			
//			List<String> localTeamEmail = Arrays.asList("vaibhav.anand@incture.com");
//			List<String> gmdmTeamEmail = Arrays.asList("vaibhav.anand@incture.com");
//			
//			bpBuildProcessWorkflowContextDto.setRequestId(requestId);
//			bpBuildProcessWorkflowContextDto.setRequestorName(bpRequestGeneralDataDto.getName1());
//			bpBuildProcessWorkflowContextDto.setEmail(bpRequestGeneralDataDto.getBpContactInformation().get(0).getUserEmail());
//			bpBuildProcessWorkflowContextDto.setCreatedDate(dateFormat.format(date));
//			bpBuildProcessWorkflowContextDto.setLocalTeamEmailGroup(localTeamEmail);
//			bpBuildProcessWorkflowContextDto.setGmdmTeamEmailGroup(gmdmTeamEmail);
//
//			return bpBuildProcessWorkflowContextDto;
//		} catch (Exception e) {
//			logger.error("[BPWorkflowServiceImpl][prepareWorkflowPayload] Exception : {}", e.toString());
//		}
//		return null;
//	}
//
//}
