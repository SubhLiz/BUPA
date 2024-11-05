package com.incture.bupa.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.incture.bupa.dto.BPAuditLogDto;
import com.incture.bupa.dto.BPCreationFromWorkflowRequest;
import com.incture.bupa.dto.BPFlexibleWorkflowDto;
import com.incture.bupa.dto.ErrorDetailsDto;
import com.incture.bupa.dto.FlexiWorkflowTaskDetailsDto;
import com.incture.bupa.dto.WorkflowConditionsDetailDto;
import com.incture.bupa.dto.WorkflowContextDto;
import com.incture.bupa.dto.WorkflowResponseDto;
import com.incture.bupa.dto.WorkflowTaskDetailsDto;
import com.incture.bupa.entities.BPFlexibleWorkflow;
import com.incture.bupa.repository.BPDetailsRepository;
import com.incture.bupa.repository.BPFlexibleWorkflowRepository;
import com.incture.bupa.utils.DestinationUtil;
import com.incture.bupa.utils.HelperClass;
import com.incture.bupa.utils.MailRequestDto;
import com.incture.bupa.utils.ObjectMapperUtils;
import com.mashape.unirest.http.exceptions.UnirestException;

 
@Service
public class WorkflowLauncher {
	private Logger logger = LoggerFactory.getLogger(WorkflowLauncher.class);
	@Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BPFlexibleWorkflowRepository bpFlexibleWorkflowRepository ;
    
    @Autowired
    private BPUserDetailsService bpUserDetailsService;
    
    @Autowired
	private BPDetailsRepository bpVendorDetailsRepository;
    
    @Autowired
    private EmailNotificationService emailNotificationService;
    
    @Autowired
    private AuditLogService auditLogService;
    
    
    @Autowired
	WebClient webClient;
    
//    @Value("${cpi-email.from}")
//	private String from;
    
    @Value("${spring.profiles.active}")
	private String profile;
    
    @Autowired
	private DestinationUtil destinationUtil;
    public String getAccessTokenForRules(String clientid, String clientsecret, String tokenUrl) throws JsonMappingException, JsonProcessingException{

//        URI myURI = null;
//        try {
//            myURI = new URI(tokenUrl);
//        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
//        }
//        URI finalMyURI = myURI;
//        JSONObject authenticationResponseObject = WebClient.builder()
//                .filter(basicAuthentication(clientid,
//                        clientsecret))
//                .build().post()
//                .uri(uriBuilder -> uriBuilder.scheme(finalMyURI.getScheme())
//                        .host(finalMyURI.getHost())
//                        .path(finalMyURI.getPath())
//                        .queryParam("grant_type", "client_credentials").build())
//                .retrieve()
//                .bodyToMono(JSONObject.class)
//                .block();
//
//        System.out.println("Token " + authenticationResponseObject.toString());
//        return authenticationResponseObject.get("access_token").toString();
    	
    	RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientid + ":" + clientsecret);
		headers.add("Authorization", "Basic " + encode.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = template.postForEntity(tokenUrl+"?grant_type=client_credentials", entity, String.class);

		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
    }
    public JsonNode getDataFromEmailRules(int notificationCode) {
		 String accessToken,url=null;
		 try {
			 System.out.println("****");
			 System.out.println(notificationCode);
			 System.out.println("****");
	            String destDetails = destinationUtil.readMdgDestination("vm-wr-services", null, null);


	            org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
	            logger.info("Json object from destination :"+resObj);
	            logger.info("Client id: "+resObj.optJSONObject("destinationConfiguration").optString("clientId"));
	            logger.info("clientSecret : "+resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
	            logger.info("tokenServiceURL: "+resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
	            logger.info("url: "+resObj.optJSONObject("destinationConfiguration").optString("URL"));
	            accessToken =getAccessTokenForRules(resObj.optJSONObject("destinationConfiguration").optString("clientId")
	                    ,resObj.optJSONObject("destinationConfiguration").optString("clientSecret")
	                    ,resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

	            url=resObj.optJSONObject("destinationConfiguration").optString("URL")+"/rest/v1/invoke-rules";

	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
		 String json = "{\n" +
	                "    \"decisionTableId\": null,\n" +
	                "    \"decisionTableName\": \"VM_EMAIL_NOTIFICATION_RULES\",\n" +
	                "    \"version\": \"v1\",\n" +
	                "    \"conditions\": [\n" +
	                "        {\n" +
	                "            \"VM_CONDITIONS.VM_NOTIFICATION_CODE\": " + notificationCode + "\n" +
	                "        }\n" +
	                "    ],\n" +
	                "    \"systemFilters\": null,\n" +
	                "    \"rulePolicy\": null,\n" +
	                "    \"validityDate\": null\n" +
	                "}";
		 JsonNode ruleResponse = webClient.post()
	                .uri(url)
	                .headers(h -> h.setBearerAuth(accessToken))
	                .header("Content-Type", "application/json")
	                .body(BodyInserters.fromValue(json))
	                .retrieve()
	                .bodyToMono(JsonNode.class)
	                .block();

	        logger.info("Response : "+ ruleResponse);

	        return ruleResponse;
//		 return null;
	}
    public JsonNode getDataFromEmailRules(String taskName) {
		 String accessToken,url=null;
		 try {
			 System.out.println("****");
			 System.out.println(taskName);
			 System.out.println("****");
	            String destDetails = destinationUtil.readMdgDestination("vm-wr-services", null, null);


	            org.json.JSONObject resObj = new org.json.JSONObject(destDetails);
	            logger.info("Json object from destination :"+resObj);
	            logger.info("Client id: "+resObj.optJSONObject("destinationConfiguration").optString("clientId"));
	            logger.info("clientSecret : "+resObj.optJSONObject("destinationConfiguration").optString("clientSecret"));
	            logger.info("tokenServiceURL: "+resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));
	            logger.info("url: "+resObj.optJSONObject("destinationConfiguration").optString("URL"));
	            accessToken =getAccessTokenForRules(resObj.optJSONObject("destinationConfiguration").optString("clientId")
	                    ,resObj.optJSONObject("destinationConfiguration").optString("clientSecret")
	                    ,resObj.optJSONObject("destinationConfiguration").optString("tokenServiceURL"));

	            url=resObj.optJSONObject("destinationConfiguration").optString("URL")+"/rest/v1/invoke-rules";

	        } catch (IOException e) {
	            throw new RuntimeException(e);
	        }
		 String json = "{\n" +
	                "    \"decisionTableId\": null,\n" +
	                "    \"decisionTableName\": \"VM_WORKFLOW_APPROVER_EMAIL_IDS_RULE\",\n" +
	                "    \"version\": \"v1\",\n" +
	                "    \"conditions\": [\n" +
	                "        {\n" +
	                "            \"WORKFLOW_EMAIL_ID.VM_WOKFLOW_APPROVER\": " + "\""+taskName + "\""+ "\n" +
	                "        }\n" +
	                "    ],\n" +
	                "    \"systemFilters\": null,\n" +
	                "    \"rulePolicy\": null,\n" +
	                "    \"validityDate\": null\n" +
	                "}";
		 JsonNode ruleResponse = webClient.post()
	                .uri(url)
	                .headers(h -> h.setBearerAuth(accessToken))
	                .header("Content-Type", "application/json")
	                .body(BodyInserters.fromValue(json))
	                .retrieve()
	                .bodyToMono(JsonNode.class)
	                .block();

	        logger.info("Response : "+ ruleResponse);

	        return ruleResponse;
//		 return null;
	}
	
	public String saveFlexibleWorkflowRulesData(List<BPFlexibleWorkflowDto> bpFlexibleWorkflowDto) throws NamingException,IOException{
		List<BPFlexibleWorkflow> bpFlexibleWorkflow=new ArrayList<>();
		bpFlexibleWorkflow=ObjectMapperUtils.mapAll(bpFlexibleWorkflowDto, BPFlexibleWorkflow.class);
		bpFlexibleWorkflowRepository.saveAll(bpFlexibleWorkflow);
		return "Data saved Successfully";
	}
	public WorkflowResponseDto workflowResponse() throws NamingException,IOException{
		WorkflowResponseDto responseMessageWorkRules = new WorkflowResponseDto();
	    List<Map<String,Object>> totalMap = new ArrayList<>();
//		WorkflowResponseDto responseDto=new WorkflowResponseDto();
//		ArrayList<String>apList=new ArrayList<>();
//		ArrayList<String>emailList=new ArrayList<>();
//		int acCount=0;
		List<BPFlexibleWorkflow> rulesResponse=bpFlexibleWorkflowRepository.findAll();
		for(int i=0;i<rulesResponse.size();i++) {
			if(rulesResponse.get(i).getFlexibleTaskType().equalsIgnoreCase("YES")) {
				Map<String,Object> wfMap = new HashMap<>();
				wfMap.put("approverTaskName", rulesResponse.get(i).getWorkflowTaskName());
                wfMap.put("taskLevel", rulesResponse.get(i).getWorkflowApprovalLevel());
				JsonNode ruleResponse = getDataFromEmailRules(rulesResponse.get(i).getWorkflowApprover());
				if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
		            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
		            wfMap.put("userRecipient", ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
                    totalMap.add(wfMap);
		        }
			}
		}
		totalMap.sort(Comparator.comparing(m -> Integer.parseInt((String) m.get("taskLevel"))));
        responseMessageWorkRules.setApprovalFlexible(totalMap);
		return responseMessageWorkRules;
	}
	public WorkflowContextDto contextResponse(WorkflowContextDto bpFlexibleWorkflowDto) throws UnirestException, ClientProtocolException, IOException {
		WorkflowContextDto contextResponseDto=new WorkflowContextDto();
		BPCreationFromWorkflowRequest bpCreationFromWorkflowRequest=new BPCreationFromWorkflowRequest();
		WorkflowTaskDetailsDto workflowTaskDetailsDto = new WorkflowTaskDetailsDto();
		WorkflowConditionsDetailDto conditionsDetailDto=new WorkflowConditionsDetailDto();
		
		System.out.println("WorkFlow Dto  " + new Gson().toJson(bpFlexibleWorkflowDto));
		
		BPAuditLogDto bpAuditLogDto = new BPAuditLogDto();
		
		List<FlexiWorkflowTaskDetailsDto>flexiTaskList=new ArrayList<>();
		ErrorDetailsDto errorDetailsDto=new ErrorDetailsDto();
		String errorMessage="";
		boolean success=true;
		int draftStatusId=1;
		boolean financeApproverCheck=false;
		boolean sourcingApproverCheck=false;
		boolean qualityApproverCheck=false;
		boolean gmdmApproverCheck=false;
		boolean flexiApproverCheck=false;
		boolean sourcingTaskFlag=false;
		boolean qualityTaskFlag=false;
		boolean categoryLeadApproverCheck=false;
		int flexiCount=0;
		switch(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType()) {
		  case "Create":
				for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
					if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("Finance Appoval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
							workflowTaskDetailsDto.setFinanceApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
							workflowTaskDetailsDto.setFinanceApproverGroup(workflowDto.getWorkflowApprover());
							financeApproverCheck=true;
					}
						else {
							break;
						}
				}
					if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("GMDM Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
							workflowTaskDetailsDto.setGmdmApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
							workflowTaskDetailsDto.setGmdmApproverGroup(workflowDto.getWorkflowApprover());
							gmdmApproverCheck=true;
					}
						else {
							break;
						}
				}
					if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("SourcingTask")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						sourcingTaskFlag=true;
						
				}
					if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("QualityTask")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						qualityTaskFlag=true;
				}
				if (workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
					FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto = new FlexiWorkflowTaskDetailsDto();
					flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
					flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
					flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());
					JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
					if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
						System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0)
								.get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
						flexiWorkflowTaskDetailsDto.setUserRecipient(
								bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0)
										.get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
						flexiTaskList.add(flexiWorkflowTaskDetailsDto);
						flexiApproverCheck = true;
						flexiCount++;
					} else {
						flexiApproverCheck = false;
						break;
					}
				}
					
					
				}
				if(sourcingTaskFlag) {
					if(!HelperClass.isEmpty(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getSourcingApprover_RecipientUsers())) {
					workflowTaskDetailsDto.setSourcingApprover_RecipientUsers(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getSourcingApprover_RecipientUsers()));
					workflowTaskDetailsDto.setSourcingWorkflowApproverGroup(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getSourcingWorkflowApproverGroup());
					sourcingApproverCheck=true;
					}
					else {
						sourcingApproverCheck=false;
					}
				}
				if(qualityTaskFlag) {
					if(!HelperClass.isEmpty(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getQualityApprover_RecipientUsers())) {
					workflowTaskDetailsDto.setQualityApprover_RecipientUsers(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getQualityApprover_RecipientUsers()));
					workflowTaskDetailsDto.setQualityWorkflowApproverGroup(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getQualityWorkflowApproverGroup());
					qualityApproverCheck=true;
					}
					else {
						qualityApproverCheck=false;
					}
				}
				flexiTaskList.sort(Comparator
		         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
		         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
				workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
				if(!financeApproverCheck) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else if(!sourcingApproverCheck&&bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing()) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else if(!qualityApproverCheck&&bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide()) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else if(!gmdmApproverCheck) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else if(!flexiApproverCheck&&flexiCount>0) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else {
					
					success=true;
					errorMessage="Context set successfully!!";
				}				
				
				bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
				bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
				bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
				bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
				bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
				bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
				bpCreationFromWorkflowRequest.setCompanyCode(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCompanyCode());
				bpCreationFromWorkflowRequest.setPurchasingOrg(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getPurchasingOrg());
				//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
				bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
				
				conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
				conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
				conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
				conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
				conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
				conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
				conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
				conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
				conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
				conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
				
				errorDetailsDto.setSuccess(success);
				errorDetailsDto.setErrorMessage(errorMessage);
				workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
				contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
				contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
				contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
				contextResponseDto.setErrorDetails(errorDetailsDto);
		    
		    break;
		    
		    
		  case "Generic":
			  System.out.println("******Hello Generic********");
				for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
					
					if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("GMDM Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
							workflowTaskDetailsDto.setGmdmApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
							workflowTaskDetailsDto.setGmdmApproverGroup(workflowDto.getWorkflowApprover());
							gmdmApproverCheck=true;
					}
						else {
							break;
						}
				}
					if(workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
						FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto=new FlexiWorkflowTaskDetailsDto();
						flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
						flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
						flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());	
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
				            flexiWorkflowTaskDetailsDto.setUserRecipient(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
				            flexiTaskList.add(flexiWorkflowTaskDetailsDto);
				            flexiApproverCheck=true;
				            flexiCount++;
				        }
						else {
							flexiApproverCheck=false;
							break;
						}
					}
					
					
				}
				
				
				flexiTaskList.sort(Comparator
		         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
		         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
				workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
				
				
				if(!gmdmApproverCheck) {
					System.out.println("GMDM Check");
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else if(!flexiApproverCheck&&flexiCount>0) {
					System.out.println("Flexi Check");
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else {
					
					success=true;
					errorMessage="Context set successfully!!";
				}
				
				bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
				bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
				bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
				bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
				bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
				bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
				//bpCreationFromWorkflowRequest.setCompanyCode("");
				//bpCreationFromWorkflowRequest.setPurchasingOrg("");
				//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
				bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
				
				
				if (!HelperClass.isEmpty(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId())) {

					 
					Object auditLogInfo = auditLogService.getAuditLogInfoByRequestId(
							bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());

					ObjectMapper objectMapper = new ObjectMapper();
					String auditLogInfoJson = null;
					auditLogInfoJson = objectMapper.writeValueAsString(auditLogInfo);

					JSONArray dataList = new JSONArray(auditLogInfoJson);

					List<String> pathList = new ArrayList<>();

					for (int i = 0; i < dataList.length(); i++) {
						JSONObject dataObject = dataList.getJSONObject(i);
						String path = dataObject.getString("path");
						logger.info("path------>" + path);
						pathList.add(path);
					}

					List<Integer> companyCodeList = new ArrayList<>();
					List<Integer> purchasingOrgList = new ArrayList<>();

//					Pattern pattern = Pattern.compile("\\.(\\d+)\\.");
					 Pattern ccodepattern = Pattern.compile("bpCompanyCodeInfo\\.(\\d+)\\.");
					 Pattern purOrgpattern = Pattern.compile("bpPurchasingOrgDetail\\.(\\d+)\\.");

					for (String path : pathList) {
						Matcher matcher = ccodepattern.matcher(path);
						while (matcher.find()) {
							int index = Integer.parseInt(matcher.group(1));
							if (path.contains("bpCompanyCodeInfo")) {
								logger.info("index in path cc----->" + index);
								
								companyCodeList.add(index);

							} else {
								System.out.println("Skipping path " );
							}
						}
					}
					
					
					for (String path : pathList) {
						Matcher matcher = purOrgpattern.matcher(path);
						while (matcher.find()) {
							int index = Integer.parseInt(matcher.group(1));
							if (path.contains("bpPurchasingOrgDetail")) {
								logger.info("index in path po----->" + index);
								
								purchasingOrgList.add(index);

							} else {
								System.out.println("Skipping path " );
							}
						}
					}

					String companycodeAsList = bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest()
							.getCompanyCode();
					String purchasingOrgAsList = bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest()
							.getPurchasingOrg();

					String strCompCode2[] = companycodeAsList.split(",");
					String strPurchaseOrg[] = purchasingOrgAsList.split(",");

					List<String> companyCoderesult = new ArrayList<>();
					Set<Integer> companyCodeSet = new HashSet<>();

					for (int index : companyCodeList) {
						companyCodeSet.add(index);
					}

					for (int index : companyCodeSet) {
						companyCoderesult.add(strCompCode2[index]);
					}

					String companyCodeConcatResult = String.join(",", companyCoderesult);

					List<String> purchaseOrgResult = new ArrayList<>();
					Set<Integer> purchaseOrgSet = new HashSet<>();

					for (int index : purchasingOrgList) {
						purchaseOrgSet.add(index);
					}

					for (int index : purchaseOrgSet) {
						purchaseOrgResult.add(strPurchaseOrg[index]);
					}

					String purchaseOrgConcatResult = String.join(",", purchaseOrgResult);

					if (!companyCodeConcatResult.equals("")) {
						bpCreationFromWorkflowRequest.setCompanyCode(companyCodeConcatResult);
					} else {
						bpCreationFromWorkflowRequest.setCompanyCode("");
					}

					if (!purchaseOrgConcatResult.equals("")) {
						bpCreationFromWorkflowRequest.setPurchasingOrg(purchaseOrgConcatResult);
					} else {
						bpCreationFromWorkflowRequest.setPurchasingOrg("");
					}
				} else {
					bpCreationFromWorkflowRequest.setCompanyCode("");
					bpCreationFromWorkflowRequest.setPurchasingOrg("");
				}
				
				conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
				conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
				conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
				conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
				conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
				conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
				conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
				conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
				conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
				conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
				
				errorDetailsDto.setSuccess(success);
				errorDetailsDto.setErrorMessage(errorMessage);
				workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
				contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
				contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
				contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
				contextResponseDto.setErrorDetails(errorDetailsDto);
		        break;
		    case "Bank":
		    	System.out.println("******Hello Bank********");
               for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
            	   if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("Finance Appoval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
							workflowTaskDetailsDto.setFinanceApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
							workflowTaskDetailsDto.setFinanceApproverGroup(workflowDto.getWorkflowApprover());
							financeApproverCheck=true;
					}
						else {
							break;
						}
				}
					if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("GMDM Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
							workflowTaskDetailsDto.setGmdmApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
							workflowTaskDetailsDto.setGmdmApproverGroup(workflowDto.getWorkflowApprover());
							gmdmApproverCheck=true;
					}
						else {
							break;
						}
				}
					if(workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
						FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto=new FlexiWorkflowTaskDetailsDto();
						flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
						flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
						flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());	
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
				            flexiWorkflowTaskDetailsDto.setUserRecipient(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
				            flexiTaskList.add(flexiWorkflowTaskDetailsDto);
				            flexiApproverCheck=true;
				            flexiCount++;
				        }
						else {
							flexiApproverCheck=false;
							break;
						}
					}
					
					
				}
				
				
				flexiTaskList.sort(Comparator
		         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
		         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
				workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
				if(!financeApproverCheck) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				
				if(!gmdmApproverCheck) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else if(!flexiApproverCheck&&flexiCount>0) {
					bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
					success=false;
					errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
				}
				else {
					
					success=true;
					errorMessage="Context set successfully!!";
				}
				
				
				bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
				bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
				bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
				bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
				bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
				bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
				bpCreationFromWorkflowRequest.setCompanyCode("");
				bpCreationFromWorkflowRequest.setPurchasingOrg("");
				//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
				bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
				
				conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
				conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
				conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
				conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
				conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
				conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
				conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
				conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
				conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
				conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
				
				errorDetailsDto.setSuccess(success);
				errorDetailsDto.setErrorMessage(errorMessage);
				workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
				contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
				contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
				contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
				contextResponseDto.setErrorDetails(errorDetailsDto);
			    break;
		    case "PaymentTerms":
		    	System.out.println("******Hello Payment********");
		    	for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
	            	  
						if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("Category Lead Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
								workflowTaskDetailsDto.setCategoryLeadApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
								workflowTaskDetailsDto.setCategoryLeadApproverGroup(workflowDto.getWorkflowApprover());
								categoryLeadApproverCheck=true;
						}
							else {
								break;
							}
					}
						if(workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
							FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto=new FlexiWorkflowTaskDetailsDto();
							flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
							flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
							flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());	
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
					            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
					            flexiWorkflowTaskDetailsDto.setUserRecipient(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
					            flexiTaskList.add(flexiWorkflowTaskDetailsDto);
					            flexiApproverCheck=true;
					            flexiCount++;
					        }
							else {
								flexiApproverCheck=false;
								break;
							}
						}
						
						
					}
					
					
					flexiTaskList.sort(Comparator
			         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
			         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
					workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
					
					if(!categoryLeadApproverCheck) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!flexiApproverCheck&&flexiCount>0) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else {
						
						success=true;
						errorMessage="Context set successfully!!";
					}
					
					 
					
					
					 
					
	                
					bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
					bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
					bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
					bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
					bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
					bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
					//bpCreationFromWorkflowRequest.setCompanyCode(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCompanyCode());
					//bpCreationFromWorkflowRequest.setPurchasingOrg(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getPurchasingOrg());
					//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
					bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
					
					if (!HelperClass.isEmpty(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId())) {

						 
						Object auditLogInfo = auditLogService.getAuditLogInfoByRequestId(
								bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());

						ObjectMapper objectMapper = new ObjectMapper();
						String auditLogInfoJson = null;
						auditLogInfoJson = objectMapper.writeValueAsString(auditLogInfo);

						JSONArray dataList = new JSONArray(auditLogInfoJson);

						List<String> pathList = new ArrayList<>();

						for (int i = 0; i < dataList.length(); i++) {
							JSONObject dataObject = dataList.getJSONObject(i);
							String path = dataObject.getString("path");

							pathList.add(path);
						}

						List<Integer> companyCodeList = new ArrayList<>();
						List<Integer> purchasingOrgList = new ArrayList<>();
//						Pattern pattern = Pattern.compile("\\.(\\d+)\\.");
						  Pattern companycodepattern = Pattern.compile("bpCompanyCodeInfo\\.(\\d+)\\.");
						  Pattern purchaseorgpattern = Pattern.compile("bpPurchasingOrgDetail\\.(\\d+)\\.");

						for (String path : pathList) {
							Matcher matcher = companycodepattern.matcher(path);
							while (matcher.find()) {
								int index = Integer.parseInt(matcher.group(1));
								if (path.contains("bpCompanyCodeInfo")) {
									logger.info("path of companycode---->"+index);
									companyCodeList.add(index);

								}  else {
									System.out.println("Skipping path: " + path);
								}
							}
						}
						
						
						for (String path : pathList) {
							Matcher matcher = purchaseorgpattern.matcher(path);
							while (matcher.find()) {
								int index = Integer.parseInt(matcher.group(1));
								 if (path.contains("bpPurchasingOrgDetail")) {
									 logger.info("path of purchaseorg---->"+index);
									purchasingOrgList.add(index);

								} else {
									System.out.println("Skipping path: " + path);
								}
							}
						}


						String companycodeAsList = bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest()
								.getCompanyCode();
						String purchasingOrgAsList = bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest()
								.getPurchasingOrg();

						String strCompCode2[] = companycodeAsList.split(",");
						String strPurchaseOrg[] = purchasingOrgAsList.split(",");

						List<String> companyCoderesult = new ArrayList<>();
						Set<Integer> companyCodeSet = new HashSet<>();

						for (int index : companyCodeList) {
							companyCodeSet.add(index);
						}

						for (int index : companyCodeSet) {
							companyCoderesult.add(strCompCode2[index]);
						}

						String companyCodeConcatResult = String.join(",", companyCoderesult);

						List<String> purchaseOrgResult = new ArrayList<>();
						Set<Integer> purchaseOrgSet = new HashSet<>();

						for (int index : purchasingOrgList) {
							purchaseOrgSet.add(index);
						}

						for (int index : purchaseOrgSet) {
							purchaseOrgResult.add(strPurchaseOrg[index]);
						}

						String purchaseOrgConcatResult = String.join(",", purchaseOrgResult);

						if (!companyCodeConcatResult.equals("")) {
							bpCreationFromWorkflowRequest.setCompanyCode(companyCodeConcatResult);
						} else {
							bpCreationFromWorkflowRequest.setCompanyCode("");
						}

						if (!purchaseOrgConcatResult.equals("")) {
							bpCreationFromWorkflowRequest.setPurchasingOrg(purchaseOrgConcatResult);
						} else {
							bpCreationFromWorkflowRequest.setPurchasingOrg("");
						}
					} else {
						bpCreationFromWorkflowRequest.setCompanyCode("");
						bpCreationFromWorkflowRequest.setPurchasingOrg("");
					}
		
					
					
					
					conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
					conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
					conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
					conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
					conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
					conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
					conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
					conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
					conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
					conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
					
					errorDetailsDto.setSuccess(success);
					errorDetailsDto.setErrorMessage(errorMessage);
					workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
					contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
					contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
					contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
					contextResponseDto.setErrorDetails(errorDetailsDto);
				break;
		    case "CompanyCode":
		    	System.out.println("******Hello CompanyCode********");
		    	for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
	            	  
						if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("GMDM Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
								workflowTaskDetailsDto.setGmdmApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
								workflowTaskDetailsDto.setGmdmApproverGroup(workflowDto.getWorkflowApprover());
								gmdmApproverCheck=true;
						}
							else {
								break;
							}
					}
						if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("QualityTask")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
							qualityTaskFlag=true;
					}
						if(workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
							FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto=new FlexiWorkflowTaskDetailsDto();
							flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
							flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
							flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());	
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
					            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
					            flexiWorkflowTaskDetailsDto.setUserRecipient(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
					            flexiTaskList.add(flexiWorkflowTaskDetailsDto);
					            flexiApproverCheck=true;
					            flexiCount++;
					        }
							else {
								flexiApproverCheck=false;
								break;
							}
						}
						
						
					}
				if(qualityTaskFlag) {
					if(!HelperClass.isEmpty(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getQualityApprover_RecipientUsers())) {
					workflowTaskDetailsDto.setQualityApprover_RecipientUsers(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getQualityApprover_RecipientUsers()));
					workflowTaskDetailsDto.setQualityWorkflowApproverGroup(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getQualityWorkflowApproverGroup());
					qualityApproverCheck=true;
					}
					else {
						qualityApproverCheck=false;
					}
				}
					
					flexiTaskList.sort(Comparator
			         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
			         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
					workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
					
					if(!gmdmApproverCheck) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!qualityApproverCheck&&bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide()) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!flexiApproverCheck&&flexiCount>0) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else {
						
						success=true;
						errorMessage="Context set successfully!!";
					}
					
					
					bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
					bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
					bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
					bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
					bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
					bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
					bpCreationFromWorkflowRequest.setCompanyCode(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCompanyCode());
					bpCreationFromWorkflowRequest.setPurchasingOrg("");
					//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
					bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
					
					
					
		
					
					conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
					conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
					conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
					conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
					conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
					conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
					conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
					conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
					conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
					conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
					
					errorDetailsDto.setSuccess(success);
					errorDetailsDto.setErrorMessage(errorMessage);
					workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
					contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
					contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
					contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
					contextResponseDto.setErrorDetails(errorDetailsDto);
		    	break;
		    case "PurchaseOrg":
		    	System.out.println("******Hello PurchaseOrg********");
		    	for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
		    		if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("SourcingTask")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						sourcingTaskFlag=true;
						
				}
	            	  
						if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("GMDM Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
								workflowTaskDetailsDto.setGmdmApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
								workflowTaskDetailsDto.setGmdmApproverGroup(workflowDto.getWorkflowApprover());
								gmdmApproverCheck=true;
						}
							else {
								break;
							}
					}
						if(workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
							FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto=new FlexiWorkflowTaskDetailsDto();
							flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
							flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
							flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());	
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
					            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
					            flexiWorkflowTaskDetailsDto.setUserRecipient(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
					            flexiTaskList.add(flexiWorkflowTaskDetailsDto);
					            flexiApproverCheck=true;
					            flexiCount++;
					        }
							else {
								flexiApproverCheck=false;
								break;
							}
						}
						
						
					}
		    	if(sourcingTaskFlag) {
					if(!HelperClass.isEmpty(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRegionalSourcingApprover_RecipientUsers())) {
					workflowTaskDetailsDto.setRegionalSourcingApprover_RecipientUsers(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getSourcingApprover_RecipientUsers()));
					workflowTaskDetailsDto.setSourcingWorkflowApproverGroup(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getSourcingWorkflowApproverGroup());
					sourcingApproverCheck=true;
					}
					else {
						sourcingApproverCheck=false;
					}
				}
					
					flexiTaskList.sort(Comparator
			         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
			         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
					workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
					
					if(!gmdmApproverCheck) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!sourcingApproverCheck) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!flexiApproverCheck&&flexiCount>0) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else {
						
						success=true;
						errorMessage="Context set successfully!!";
					}
					
					
					bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
					bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
					bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
					bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
					bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
					bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
					bpCreationFromWorkflowRequest.setCompanyCode("");
 					bpCreationFromWorkflowRequest.setPurchasingOrg(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getPurchasingOrg());
					//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
					bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
					
					conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
					conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
					conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
					conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
					conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
					conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
					conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
					conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
					conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
					conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
					
					errorDetailsDto.setSuccess(success);
					errorDetailsDto.setErrorMessage(errorMessage);
					workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
					contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
					contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
					contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
					contextResponseDto.setErrorDetails(errorDetailsDto);
		    	break;
		    case "AddnData":
		    	System.out.println("******Hello AddnData********");
		    	for (FlexiWorkflowTaskDetailsDto workflowDto : bpFlexibleWorkflowDto.getWorkflowTaskDetails().getFlexiWorkflowTaskDetails()) {
		    		if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("Sourcing Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
						JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
						if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
							workflowTaskDetailsDto.setRegionalSourcingApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
							workflowTaskDetailsDto.setSourcingWorkflowApproverGroup(workflowDto.getWorkflowApprover());
							sourcingApproverCheck=true;
					}
						else {
							break;
						}
				}
	            	  
						if(workflowDto.getWorkflowTaskName().equalsIgnoreCase("GMDM Approval Workflow Task")&&workflowDto.getFlexibleTaskType().equalsIgnoreCase("NA")) {
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
								workflowTaskDetailsDto.setGmdmApprover_RecipientUsers(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
								workflowTaskDetailsDto.setGmdmApproverGroup(workflowDto.getWorkflowApprover());
								gmdmApproverCheck=true;
						}
							else {
								break;
							}
					}
						if(workflowDto.getFlexibleTaskType().equalsIgnoreCase("YES")) {
							FlexiWorkflowTaskDetailsDto flexiWorkflowTaskDetailsDto=new FlexiWorkflowTaskDetailsDto();
							flexiWorkflowTaskDetailsDto.setWorkflowApprovalLevel(workflowDto.getWorkflowApprovalLevel());
							flexiWorkflowTaskDetailsDto.setWorkflowTaskName(workflowDto.getWorkflowTaskName());
							flexiWorkflowTaskDetailsDto.setWorkflowApprover(workflowDto.getWorkflowApprover());	
							JsonNode ruleResponse = getDataFromEmailRules(workflowDto.getWorkflowApprover());
							if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
					            System.out.println(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText());
					            flexiWorkflowTaskDetailsDto.setUserRecipient(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));
					            flexiTaskList.add(flexiWorkflowTaskDetailsDto);
					            flexiApproverCheck=true;
					            flexiCount++;
					        }
							else {
								flexiApproverCheck=false;
								break;
							}
						}
						
						
					}
					
					flexiTaskList.sort(Comparator
			         		.comparing((FlexiWorkflowTaskDetailsDto m) -> Integer.parseInt((String) m.getWorkflowApprovalLevel()))
			         		.thenComparing(m -> (String) m.getWorkflowTaskName()));
					workflowTaskDetailsDto.setFlexiWorkflowTaskDetails(flexiTaskList);
					
					if(!gmdmApproverCheck) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!sourcingApproverCheck) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else if(!flexiApproverCheck&&flexiCount>0) {
						bpVendorDetailsRepository.updateStatusId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId(), draftStatusId);
						success=false;
						errorMessage="The approver for organization "+bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName()+" has not yet been assigned. Please contact the Global Vendor Master Data team (gvmd@viatris.com) for assistance.";
					}
					else {
						
						success=true;
						errorMessage="Context set successfully!!";
					}
					
					
					bpCreationFromWorkflowRequest.setRequestId(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
					bpCreationFromWorkflowRequest.setBpRequestType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType());
					bpCreationFromWorkflowRequest.setSubProcessType(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSubProcessType());
					bpCreationFromWorkflowRequest.setVendorAccountGroup(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorAccountGroup());
					bpCreationFromWorkflowRequest.setBusinessPartnerNumber(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerNumber());
					bpCreationFromWorkflowRequest.setBusinessPartnerName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBusinessPartnerName());
					bpCreationFromWorkflowRequest.setCompanyCode("");
 					bpCreationFromWorkflowRequest.setPurchasingOrg(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getPurchasingOrg());
					//Changes Done : Author - Dheeraj Kumar (Setting Country Name)
					bpCreationFromWorkflowRequest.setCountryName(bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryName());
					
					conditionsDetailDto.setChangePaymentTerms(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangePaymentTerms());
					conditionsDetailDto.setExtendPurchaseOrg(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getExtendPurchaseOrg());
					conditionsDetailDto.setCheckSourcing(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckSourcing());
					conditionsDetailDto.setCheckBonafide(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getCheckBonafide());
					conditionsDetailDto.setIsGeneric(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsGeneric());
					conditionsDetailDto.setChangeBankDetails(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getChangeBankDetails());
					conditionsDetailDto.setIsAddnDataPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsAddnDataPresent());
					conditionsDetailDto.setIsBankDetailsPresent(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsBankDetailsPresent());
					conditionsDetailDto.setIsExtend(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsExtend());
					conditionsDetailDto.setIsChange(bpFlexibleWorkflowDto.getWorkflowConditionsDetail().getIsChange());
					
					errorDetailsDto.setSuccess(success);
					errorDetailsDto.setErrorMessage(errorMessage);
					workflowTaskDetailsDto.setRequestor(bpUserDetailsService.getUserMail(bpFlexibleWorkflowDto.getWorkflowTaskDetails().getRequestor()));
					contextResponseDto.setBpCreationFromWorkflowRequest(bpCreationFromWorkflowRequest);
					contextResponseDto.setWorkflowConditionsDetail(conditionsDetailDto);
					contextResponseDto.setWorkflowTaskDetails(workflowTaskDetailsDto);
					contextResponseDto.setErrorDetails(errorDetailsDto);
		    	break;
		  default:
		    System.out.println("Not a valid scenario");
		}
		
		if(!errorDetailsDto.getSuccess()) {
			MailRequestDto mailRequestDto=new MailRequestDto();
			JsonNode ruleResponse = getDataFromEmailRules("Flexible_Workflow_Rule_Error_Notification");
			if (!ruleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				
				mailRequestDto.setEmailTo(bpUserDetailsService.getUserMail(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("VM_WORKFLOW_APPROVER_EMAIL_IDS").asText()));  
			}
			else {
				mailRequestDto.setEmailTo("Vaibhav.Anand@viatris.com");
			}
			JsonNode emailRuleResponse = getDataFromEmailRules(25);
			if (!emailRuleResponse.get("data").get("result").toString().equalsIgnoreCase("{}")) {
				String subject="";
				
				if(profile.toUpperCase(Locale.ROOT).equalsIgnoreCase("PROD")){
	            	subject=emailRuleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
	            }
	            else {
	            	subject=profile.toUpperCase(Locale.ROOT)+": "+emailRuleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_SUBJECT").asText();
	            }
	            subject=subject.replace("<Request Id>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId()==null?"":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getRequestId());
	            		       
	           
//	            String subject="Testing CPI Mail";
	            System.out.println("####");
	            System.out.println(emailRuleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText());
	            String body=emailRuleResponse.get("data").get("result").get(0).get("VM_EMAIL_NOTIFICATION_CODE_ACTION").get(0).get("VM_EMAIL_BODY").asText();
	            body=body.replace("<system Id>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSystemId()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getSystemId())
         		       .replace("<request Type>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getBpRequestType())
         		       .replace("<vendor Type>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorType()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getVendorType())
         		       .replace("<account Grp>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getAccountGrp()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getAccountGrp())
         		       .replace("<country Code>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryCode()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCountryCode())
         		       .replace("<company Code>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCompanyCode()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getCompanyCode())
         		       .replace("<purchasing Org>", bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getPurchasingOrg()==null?"NA":bpFlexibleWorkflowDto.getBpCreationFromWorkflowRequest().getPurchasingOrg())
       		           .replace("|", "<br>");
	            mailRequestDto.setSubject(subject);
	            mailRequestDto.setBodyMessage(body);
			}
			
			emailNotificationService.sendMailThroughCPI(mailRequestDto);
		}
		return contextResponseDto;
	}
}
