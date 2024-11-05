package com.incture.bupa.service;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.naming.NamingException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.incture.bupa.dto.BPAccountingInformationDto;
import com.incture.bupa.dto.BPAddressInfoDto;
import com.incture.bupa.dto.BPAdvanceRequestSearchCriteriaDto;
import com.incture.bupa.dto.BPBankInformationDto;
import com.incture.bupa.dto.BPBusinessPartnerOrderingAddressDto;
import com.incture.bupa.dto.BPBusinessPartnerRemittanceAddressDto;
import com.incture.bupa.dto.BPCommentsDto;
import com.incture.bupa.dto.BPCommunicationDto;
import com.incture.bupa.dto.BPCompanyCodeInfoDto;
import com.incture.bupa.dto.BPContactInformationDto;
import com.incture.bupa.dto.BPControlDataDto;
import com.incture.bupa.dto.BPCorrespondanceDto;
import com.incture.bupa.dto.BPEmailDto;
import com.incture.bupa.dto.BPFaxInfoDto;
import com.incture.bupa.dto.BPMobilePhoneDto;
import com.incture.bupa.dto.BPPaymentTransactionsDto;
import com.incture.bupa.dto.BPPurchaseOrgAdditionalDataDto;
import com.incture.bupa.dto.BPPurchasingOrgDetailDto;
import com.incture.bupa.dto.BPRequestAddressDto;
import com.incture.bupa.dto.BPRequestGeneralDataDto;
import com.incture.bupa.dto.BPRequestSearchCriteriaDto;
import com.incture.bupa.dto.BPTaskBenchDataDto;
import com.incture.bupa.dto.BPTelephoneDto;
import com.incture.bupa.dto.BPVendorClassificationAttributeDto;
import com.incture.bupa.dto.BPVendorClassificationEntityDto;
import com.incture.bupa.dto.BPWithholdingTaxDto;
import com.incture.bupa.dto.CreateorUpdateBPResponseDto;
import com.incture.bupa.dto.DateDto;
import com.incture.bupa.dto.RequestBenchDto;
import com.incture.bupa.dto.ResponseDto;
import com.incture.bupa.entities.BPAccountingInformation;
import com.incture.bupa.entities.BPAddressInfo;
import com.incture.bupa.entities.BPBankInformation;
import com.incture.bupa.entities.BPBusinessPartnerAddressInfo;
import com.incture.bupa.entities.BPComments;
import com.incture.bupa.entities.BPCommunication;
import com.incture.bupa.entities.BPCompanyCodeInfo;
import com.incture.bupa.entities.BPContactInformation;
import com.incture.bupa.entities.BPControlData;
import com.incture.bupa.entities.BPCorrespondance;
import com.incture.bupa.entities.BPEmail;
import com.incture.bupa.entities.BPFaxInfo;
import com.incture.bupa.entities.BPGeneralData;
import com.incture.bupa.entities.BPMobilePhone;
import com.incture.bupa.entities.BPPaymentTransactions;
import com.incture.bupa.entities.BPPurchaseOrg;
import com.incture.bupa.entities.BPPurchaseOrgAdditionalData;
import com.incture.bupa.entities.BPPurchasingOrgDetail;
import com.incture.bupa.entities.BPTelephone;
import com.incture.bupa.entities.BPVendorClassificationAttribute;
import com.incture.bupa.entities.BPVendorClassificationEntity;
import com.incture.bupa.entities.BPWithholdingTax;
import com.incture.bupa.exceptions.ExecutionFault;
import com.incture.bupa.exceptions.InvalidInputFault;
import com.incture.bupa.exceptions.NoResultFault;
import com.incture.bupa.repository.BPAccountingInformationRepository;
import com.incture.bupa.repository.BPAddressInfoRepository;
import com.incture.bupa.repository.BPBankInformationRepository;
import com.incture.bupa.repository.BPBusinessPartnerAddressInfoRepository;
import com.incture.bupa.repository.BPCommentsRepository;
import com.incture.bupa.repository.BPCommunicationRepository;
import com.incture.bupa.repository.BPCompanyCodeInfoRepository;
import com.incture.bupa.repository.BPContactInformationRepository;
import com.incture.bupa.repository.BPControlDataRepository;
import com.incture.bupa.repository.BPCorrespondanceRepository;
import com.incture.bupa.repository.BPDMSAttachmentsRepository;
import com.incture.bupa.repository.BPDetailsRepository;
import com.incture.bupa.repository.BPEmailRepository;
import com.incture.bupa.repository.BPFaxInfoRepository;
import com.incture.bupa.repository.BPMobilePhoneRepository;
import com.incture.bupa.repository.BPPaymentTransactionsRepository;
import com.incture.bupa.repository.BPPurchaseOrgAdditionalDataRepository;
import com.incture.bupa.repository.BPPurchaseOrgRepository;
import com.incture.bupa.repository.BPPurchasingOrgDetailRepository;
import com.incture.bupa.repository.BPTelephoneRepository;
import com.incture.bupa.repository.BPVendorClassificationAttributeRepository;
import com.incture.bupa.repository.BPVendorClassificationAttributeValueRepository;
import com.incture.bupa.repository.BPVendorClassificationEntityRepository;
import com.incture.bupa.repository.BPWithholdingTaxRepository;
import com.incture.bupa.repository.CustomBPGeneralDataRepository;
import com.incture.bupa.utils.DestinationUtil;
import com.incture.bupa.utils.HelperClass;
import com.incture.bupa.utils.ObjectMapperUtils;
import com.incture.bupa.utils.ServicesUtil;
import com.mashape.unirest.http.exceptions.UnirestException;


@Service
@Transactional(rollbackFor = {Exception.class})
/**
 *This service is for CRUD operations on vendor details
 */
public class BPDetailService {
	private static final Logger log = LoggerFactory.getLogger(BPDetailService.class);
    @Autowired
    private BPDetailsRepository bpVendorDetailsRepository;

    @Autowired
    private BPContactInformationRepository bpContactInformationRepository;

    @Autowired
    private BPAddressInfoRepository bpAddressInfoRepository;

    @Autowired
    private BPBankInformationRepository bpBankInformationRepository;

    @Autowired
    private BPControlDataRepository bpControlDataRepository;

    @Autowired
    private BPCommentsRepository bpCommentsRepository;

    @Autowired
    private BPCompanyCodeInfoRepository bpCompanyCodeInfoRepository;

    @Autowired
    private BPAccountingInformationRepository bpAccountingInformationRepository;

    @Autowired
    private BPPaymentTransactionsRepository bpPaymentTransactionsRepository;

    @Autowired
    private BPCorrespondanceRepository bpCorrespondanceRepository;

    @Autowired
    private BPWithholdingTaxRepository bpWithholdingTaxRepository;

    @Autowired
    private BPPurchasingOrgDetailRepository bpPurchasingOrgDetailRepository;

    @Autowired
    private BPPurchaseOrgRepository bpPurchaseOrgRepository;

    @Autowired
    private BPPurchaseOrgAdditionalDataRepository bpPurchaseOrgAdditionalDataRepository;

    @Autowired
    private BPCommunicationRepository bpCommunicationRepository;

    @Autowired
    private BPTelephoneRepository bpTelephoneRepository;

    @Autowired
    private BPMobilePhoneRepository bpMobilePhoneRepository;

    @Autowired
    private BPFaxInfoRepository bpFaxInfoRepository;

    @Autowired
    private BPEmailRepository bpEmailRepository;

    @Autowired
    private BPBusinessPartnerAddressInfoRepository bpBusinessPartnerAddressInfoRepository;
    
    @Autowired
    private BPVendorClassificationEntityRepository bpVendorClassificationRepository;
    
    @Autowired
    private BPDMSAttachmentsRepository bpdmsAttachmentsRepository;
    
    @Autowired
    private BPVendorClassificationEntityRepository bpVendorClassificationEntityRepository;
    
    @Autowired
    private BPVendorClassificationAttributeRepository bpVendorClassificationAttributeRepository;
    
    @Autowired
    private BPVendorClassificationAttributeValueRepository bpVendorClassificationAttributeValueRepository;
    
    @Autowired
	private DestinationUtil destinationUtil;
    
    @Autowired
    private AuditLogService auditLogService;
	
    @Autowired
	WebClient webClient;
    
    @Autowired
    private  CustomBPGeneralDataRepository customBPGeneralDataRepository;
    
//    @Autowired
//    private BPToCharacterValueRepository bpToCharacterValueRepository;

    @Autowired
    private BPFileService bPFileService;

    private ModelMapper modelMapper;
    
    int draftStatusId=1;


    //added comment
    public List<BPAddressInfoDto> getAddressList(BPRequestAddressDto bpRequestAddressDto) {
        List<BPAddressInfoDto> listOfBPAddressInfoDto = new ArrayList<>();

        if (!HelperClass.isEmpty(bpRequestAddressDto.getMainAddress())) {

            BPAddressInfoDto mainAddressDto = ObjectMapperUtils
                    .map(bpRequestAddressDto.getMainAddress(), BPAddressInfoDto.class);

            listOfBPAddressInfoDto.add(mainAddressDto);
        }
        if (bpRequestAddressDto.isOrderingChecked()
                && !HelperClass.isEmpty(bpRequestAddressDto.getOrderingAddress())) {

            BPAddressInfoDto orderingAddressDto = ObjectMapperUtils
                    .map(bpRequestAddressDto.getOrderingAddress(), BPAddressInfoDto.class);

            listOfBPAddressInfoDto.add(orderingAddressDto);
        }
        if (bpRequestAddressDto.isRemittanceChecked()
                && !HelperClass.isEmpty(bpRequestAddressDto.getRemittanceAddress())) {

            BPAddressInfoDto remittanceAddressDto = ObjectMapperUtils
                    .map(bpRequestAddressDto.getRemittanceAddress(), BPAddressInfoDto.class);

            listOfBPAddressInfoDto.add(remittanceAddressDto);
        }
        return listOfBPAddressInfoDto;
    }

    public void saveContactsInformation(List<BPContactInformationDto> bpContactInformationDto,
                                        BPGeneralData generalDataDo) {
        for (BPContactInformationDto contactInformationDto : bpContactInformationDto) {
            BPContactInformation bpContactInformationDo = ObjectMapperUtils.map(contactInformationDto,
                    BPContactInformation.class);
            bpContactInformationDo.setBpGeneralData(generalDataDo);
            bpContactInformationRepository.save(bpContactInformationDo);
        }
    }

    public void saveCommunication(BPCommunicationDto bpCommunicationDto, BPGeneralData generalDataDo) {
    	BPCommunicationDto commResponseDto=new BPCommunicationDto();
    	
        BPCommunication bpCommunicationDo = ObjectMapperUtils.map(bpCommunicationDto, BPCommunication.class);
        bpCommunicationDo.setBpGeneralData(generalDataDo);
        bpCommunicationDo.setBpEmail(null);
        bpCommunicationDo.setBpFaxInfo(null);
        bpCommunicationDo.setBpMobilePhone(null);
        bpCommunicationDo.setBpTelephone(null);
//        if(!ServicesUtil.isEmpty(bpCommunicationDto.getComments())){
//        bpCommunicationDo.setComments(bpCommunicationDto.getComments());
//        }
        
        BPCommunication bpCommunication = bpCommunicationRepository.save(bpCommunicationDo);

        if (!ServicesUtil.isEmpty(bpCommunicationDto.getBpTelephone())) {
            for (BPTelephoneDto bpTelephoneDto : bpCommunicationDto.getBpTelephone()) {
                BPTelephone bpTelephone = ObjectMapperUtils.map(bpTelephoneDto, BPTelephone.class);
                bpTelephone.setBpCommunication(bpCommunication);
                BPTelephone bpTelephoneDo= bpTelephoneRepository.save(bpTelephone);
                BPTelephoneDto telephoneDto=ObjectMapperUtils.map(bpTelephoneDo, BPTelephoneDto.class);
                }
        }

        if (!ServicesUtil.isEmpty(bpCommunicationDto.getBpMobilePhone())) {
            for (BPMobilePhoneDto bpMobilePhoneDto : bpCommunicationDto.getBpMobilePhone()) {
                BPMobilePhone bpMobilePhone = ObjectMapperUtils.map(bpMobilePhoneDto, BPMobilePhone.class);
                bpMobilePhone.setBpCommunication(bpCommunication);
                bpMobilePhoneRepository.save(bpMobilePhone);
            }
        }

        if (!ServicesUtil.isEmpty(bpCommunicationDto.getBpFaxInfo())) {
            for (BPFaxInfoDto bpFaxInfoDto : bpCommunicationDto.getBpFaxInfo()) {
                BPFaxInfo bpFaxInfo = ObjectMapperUtils.map(bpFaxInfoDto, BPFaxInfo.class);
                bpFaxInfo.setBpCommunication(bpCommunication);
                bpFaxInfoRepository.save(bpFaxInfo);
            }
        }

        if (!ServicesUtil.isEmpty(bpCommunicationDto.getBpEmail())) {
            for (BPEmailDto bpEmailDto : bpCommunicationDto.getBpEmail()) {
                BPEmail bpEmail = ObjectMapperUtils.map(bpEmailDto, BPEmail.class);
                bpEmail.setBpCommunication(bpCommunication);
                bpEmailRepository.save(bpEmail);
            }
        }
    }

    private DateDto getDateFormatted(BPBankInformationDto bpBankInfoDto) {
        DateDto dateDTO = new DateDto();
//        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date validFrom = bpBankInfoDto.getValidFrom() == null ? null : formatter.parse(bpBankInfoDto.getValidFrom());
            Date validTo = bpBankInfoDto.getValidTo() == null ? null : formatter.parse(bpBankInfoDto.getValidTo());
            dateDTO.setValidFrom(validFrom);
            dateDTO.setValidTo(validTo);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return dateDTO;
    }

	public CreateorUpdateBPResponseDto createAndUpdateBPDetails(BPRequestGeneralDataDto bpRequestVendorDetailsDto)
			throws ParseException, NamingException, IOException, UnirestException, ExecutionFault, InvalidInputFault,
			NoResultFault {
		BPGeneralData bpGeneralDataDo = bpVendorDetailsRepository.findById(bpRequestVendorDetailsDto.getRequestId())
				.orElse(null);
		if (bpRequestVendorDetailsDto.getScenario() == 1 && bpGeneralDataDo == null) {
			return createBPDetails(bpRequestVendorDetailsDto);
		} else {
			return updateBPDetails(bpRequestVendorDetailsDto.getRequestId(), bpRequestVendorDetailsDto);
		}
	}
	/**
	 * this method is to save vendor details
	 * 
	 * @param bpRequestVendorDetailsDto
	 * @return createBPResponseDto
	 * @throws ParseException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * 
	 * @author Vaibhav Anand
	 */
    public CreateorUpdateBPResponseDto createBPDetails(BPRequestGeneralDataDto bpRequestVendorDetailsDto) throws ParseException, ClientProtocolException, IOException {
        CreateorUpdateBPResponseDto createBPResponseDto=new CreateorUpdateBPResponseDto();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /*    Saving data in BPGeneralData(parent table) Table only using GETTERS & SETTERS     */

        BPGeneralData bpGeneralDataDo = new BPGeneralData();

        if(bpRequestVendorDetailsDto.getIsDraft() && !HelperClass.isEmpty(bpRequestVendorDetailsDto.getRequestId())) {
            bpGeneralDataDo.setRequestId(bpRequestVendorDetailsDto.getRequestId());
            bpVendorDetailsRepository.deleteById(bpRequestVendorDetailsDto.getRequestId());
        }
        else if(bpRequestVendorDetailsDto.getIsDraft() && HelperClass.isEmpty(bpRequestVendorDetailsDto.getRequestId())){
        	bpGeneralDataDo.setRequestId(getRequestID());
        }
        else {
            bpGeneralDataDo.setRequestId(getRequestID());
        }

        bpGeneralDataDo.setBirthDate(bpRequestVendorDetailsDto.getBirthDate() == null ? null : formatter.parse(bpRequestVendorDetailsDto.getBirthDate()));
        bpGeneralDataDo.setCreatedOn(bpRequestVendorDetailsDto.getCreatedOn() == null ? null : formatter.parse(bpRequestVendorDetailsDto.getCreatedOn()));
        bpGeneralDataDo.setName1(bpRequestVendorDetailsDto.getName1());
        bpGeneralDataDo.setName2(bpRequestVendorDetailsDto.getName2());
        bpGeneralDataDo.setName3(bpRequestVendorDetailsDto.getName3());
        bpGeneralDataDo.setName4(bpRequestVendorDetailsDto.getName4());
        bpGeneralDataDo.setRequestTypeId(bpRequestVendorDetailsDto.getRequestTypeId());
        bpGeneralDataDo.setStatusId(bpRequestVendorDetailsDto.getStatusId());
        bpGeneralDataDo.setSearchTerm1(bpRequestVendorDetailsDto.getSearchTerm1());
        bpGeneralDataDo.setSearchTerm2(bpRequestVendorDetailsDto.getSearchTerm2());
        bpGeneralDataDo.setBupaNo(bpRequestVendorDetailsDto.getBupaNo());
        bpGeneralDataDo.setBupaAccountGrp(bpRequestVendorDetailsDto.getBupaAccountGrp());
        bpGeneralDataDo.setTitle(bpRequestVendorDetailsDto.getTitle());
        bpGeneralDataDo.setNaturalPer(bpRequestVendorDetailsDto.getNaturalPer());
        bpGeneralDataDo.setIndustry(bpRequestVendorDetailsDto.getIndustry());
        bpGeneralDataDo.setSupplierAccountId(bpRequestVendorDetailsDto.getSupplierAccountId());
        bpGeneralDataDo.setCentralDeletionFlag(bpRequestVendorDetailsDto.getCentralDeletionFlag());
        bpGeneralDataDo.setCentralPostingBlock(bpRequestVendorDetailsDto.getCentralPostingBlock());
        bpGeneralDataDo.setCentralPurchasingBlock(bpRequestVendorDetailsDto.getCentralPurchasingBlock());
        bpGeneralDataDo.setCentralDeletionBlock(bpRequestVendorDetailsDto.getCentralDeletionBlock());
        bpGeneralDataDo.setPoBox(bpRequestVendorDetailsDto.getPoBox());
        bpGeneralDataDo.setPoPostalCode(bpRequestVendorDetailsDto.getPoPostalCode());
        bpGeneralDataDo.setSupplierURL(bpRequestVendorDetailsDto.getSupplierURL());
        bpGeneralDataDo.setCorporateGroupKey(bpRequestVendorDetailsDto.getCorporateGroupKey());
        bpGeneralDataDo.setCreditInformationNumber(bpRequestVendorDetailsDto.getCreditInformationNumber());
        bpGeneralDataDo.setRecordCreationUser(bpRequestVendorDetailsDto.getRecordCreationUser());
        bpGeneralDataDo.setRecordCreationDate(bpRequestVendorDetailsDto.getRecordCreationDate());
        bpGeneralDataDo.setSecondTelephoneNumber(bpRequestVendorDetailsDto.getSecondTelephoneNumber());
        bpGeneralDataDo.setBlockFunction(bpRequestVendorDetailsDto.getBlockFunction());
        bpGeneralDataDo.setContactPersonName(bpRequestVendorDetailsDto.getContactPersonName());
        bpGeneralDataDo.setSystemId(bpRequestVendorDetailsDto.getSystemId());
        bpGeneralDataDo.setVendorType(bpRequestVendorDetailsDto.getVendorType());
        bpGeneralDataDo.setExtendCompanyCode(bpRequestVendorDetailsDto.getExtendCompanyCode());
        bpGeneralDataDo.setExtendPurchaseOrg(bpRequestVendorDetailsDto.getExtendPurchaseOrg());
        bpGeneralDataDo.setExtendAdditionalData(bpRequestVendorDetailsDto.getExtendAdditionalData());
        bpGeneralDataDo.setIndividualEntries(bpRequestVendorDetailsDto.getIndividualEntries());        
        bpGeneralDataDo.setEntriesForReference(bpRequestVendorDetailsDto.getEntriesForReference());
        bpGeneralDataDo.setDmeIndicator(bpRequestVendorDetailsDto.getDmeIndicator());
        bpGeneralDataDo.setIsrNumber(bpRequestVendorDetailsDto.getIsrNumber());
        bpGeneralDataDo.setInstructionKey(bpRequestVendorDetailsDto.getInstructionKey());
        bpGeneralDataDo.setAlternativePayee(bpRequestVendorDetailsDto.getAlternativePayee());
        
        
        bpGeneralDataDo.setCreatedBy(bpRequestVendorDetailsDto.getCreatedBy());
        bpGeneralDataDo.setRequestorEmail(bpRequestVendorDetailsDto.getRequestorEmail());
        
        bpGeneralDataDo.setVendorType(bpRequestVendorDetailsDto.getVendorType());
        bpGeneralDataDo.setPoType(bpRequestVendorDetailsDto.getPoType());
        bpGeneralDataDo.setIsRequestDetail(bpRequestVendorDetailsDto.getIsRequestDetail());
        bpGeneralDataDo.setSkipBankValidation(bpRequestVendorDetailsDto.getSkipBankValidation());
        bpGeneralDataDo.setSubProcessType(bpRequestVendorDetailsDto.getSubProcessType());
        bpGeneralDataDo.setPoCompanyPostalCode(bpRequestVendorDetailsDto.getPoCompanyPostalCode());
        
    
        BPGeneralData generalDataDo = bpVendorDetailsRepository.save(bpGeneralDataDo);
        
        long millis = formatter.parse(bpRequestVendorDetailsDto.getCreatedOn()).getTime();

        
                               /*    Saving data in BPAddressInfo     */
        
		if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpAddressInfo())) {
			saveAddressInfo(bpRequestVendorDetailsDto.getBpAddressInfo(),generalDataDo);
		}
        
		                      /*    Saving data in BPCommunication     */

        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpCommunication())) {
            saveCommunication(bpRequestVendorDetailsDto.getBpCommunication(), generalDataDo);
        }
                              /*    Saving data in BPControData     */
        
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpControlData())) {
                saveControlData(bpRequestVendorDetailsDto.getBpControlData(),generalDataDo);
        }
        
                             /*    Saving data in BPBankInformation     */
        
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpBankInformation())) {
                saveBankInformation(bpRequestVendorDetailsDto.getBpBankInformation(),generalDataDo);
        }
        
                            /*    Saving data in BPVendorClassification     */
        
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpVendorClassificationEntity())) {
            saveVendorClassification(bpRequestVendorDetailsDto.getBpVendorClassificationEntity(), generalDataDo);
        }
		
		
                  /*    Saving data in BPContactInformation Table only using modelmapper 
               and setting object of BPGeneralData(parent table) table to set the requestID as foreign key data     */


        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpContactInformation())) {
            saveContactsInformation(bpRequestVendorDetailsDto.getBpContactInformation(), generalDataDo);
        }
        
                 /*    Saving data in BPCompanyCodeInfo     */
        
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpCompanyCodeInfo())) {
            saveNewCompanyCode(bpRequestVendorDetailsDto.getBpCompanyCodeInfo(), generalDataDo);
        }
        
                 /*    Saving data in BPPurchasingOrg    */
        
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpPurchasingOrgDetail())) {
            saveNewPurchasingOrg(bpRequestVendorDetailsDto.getBpPurchasingOrgDetail(), generalDataDo);
        }
        
                 /*    Saving document in DMS(optimesd code)    */
        
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpDmsAttachments())) {
            bPFileService.uploadFile(bpRequestVendorDetailsDto.getBpDmsAttachments(), generalDataDo,millis);
        }
        
         
        
        if(bpRequestVendorDetailsDto.getBupaNo() != null) {
        	createBPResponseDto.setRequestId(generalDataDo.getRequestId());
            createBPResponseDto.setMessage("Data for " + generalDataDo.getRequestId() + " changed!!");
        }
        
        /*    Saving data in BPComments    */
        //Author - Yogita (For Comments)
        if (!ServicesUtil.isEmpty(bpRequestVendorDetailsDto.getBpComments())) {
                saveComments(bpRequestVendorDetailsDto.getBpComments(),generalDataDo);
        }
        
        else {
        createBPResponseDto.setRequestId(generalDataDo.getRequestId());
        createBPResponseDto.setMessage("Data for " + generalDataDo.getRequestId() + " saved successfully!!");
        }
        return createBPResponseDto;
        
    }

	private void saveComments(List<BPCommentsDto> bpComments, BPGeneralData generalDataDo) {
		for (BPCommentsDto bpCommentDto : bpComments) {
            BPComments bpComment = ObjectMapperUtils.map(bpCommentDto, BPComments.class);
            bpComment.setBpGeneralData(generalDataDo);
            bpCommentsRepository.save(bpComment);
        }
	}

	private void saveBankInformation(ArrayList<BPBankInformationDto> bpBankInformation, BPGeneralData generalDataDo) {
		for (BPBankInformationDto bpBankInformationDto : bpBankInformation) {
            DateDto dateDTO = getDateFormatted(bpBankInformationDto);
            bpBankInformationDto.setValidFrom(null);
            bpBankInformationDto.setValidTo(null);
            BPBankInformation saveBpBankInformation = ObjectMapperUtils.map(bpBankInformationDto, BPBankInformation.class);
            saveBpBankInformation.setValidFrom(dateDTO.getValidFrom());
            saveBpBankInformation.setValidTo(dateDTO.getValidTo());
            saveBpBankInformation.setBpGeneralData(generalDataDo);
            bpBankInformationRepository.save(saveBpBankInformation);
        }
		
	}

	private void saveControlData(ArrayList<BPControlDataDto> bpControlData, BPGeneralData generalDataDo) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (BPControlDataDto bpControlDataDto : bpControlData) {
        	Date dob = bpControlDataDto.getDob() == null ? null : formatter.parse(bpControlDataDto.getDob());
        	Date qmSystemTo=bpControlDataDto.getQmSystemTo()==null?null:formatter.parse(bpControlDataDto.getQmSystemTo());
        	Date lastExtReview=bpControlDataDto.getLastExtReview()==null?null:formatter.parse(bpControlDataDto.getLastExtReview());
        	bpControlDataDto.setDob(null);
        	bpControlDataDto.setQmSystemTo(null);
        	bpControlDataDto.setLastExtReview(null);
        	
            BPControlData saveBpControlData = ObjectMapperUtils.map(bpControlDataDto, BPControlData.class);
            
            saveBpControlData.setDob(dob);
            saveBpControlData.setQmSystemTo(qmSystemTo);
            saveBpControlData.setLastExtReview(lastExtReview);
            saveBpControlData.setBpGeneralData(generalDataDo);
            bpControlDataRepository.save(saveBpControlData);
        }
		
	}

	private void saveAddressInfo(BPAddressInfoDto bpAddressInfo, BPGeneralData generalDataDo) {
		BPAddressInfo savebpAddressInfo = ObjectMapperUtils.map(bpAddressInfo,
				BPAddressInfo.class);
		savebpAddressInfo.setBpGeneralData(generalDataDo);
		bpAddressInfoRepository.save(savebpAddressInfo);
		
	}

	private void saveVendorClassification(ArrayList<BPVendorClassificationEntityDto> bpVendorClassificationEntity,
			BPGeneralData generalDataDo) {

    	if (!HelperClass.isEmpty(bpVendorClassificationEntity)) {
        for (BPVendorClassificationEntityDto bpVendorClassificationEntityDto : bpVendorClassificationEntity) {
            
                List<BPVendorClassificationAttributeDto> bpVendorClassificationAttributeDto=new ArrayList<>();
                for(BPVendorClassificationAttributeDto classAttrDto:bpVendorClassificationEntityDto.getBpVendorClassificationAttribute()) {
                	BPVendorClassificationAttributeDto vendorClassAttrDto=new BPVendorClassificationAttributeDto();
                	vendorClassAttrDto=ObjectMapperUtils.map(classAttrDto,vendorClassAttrDto);
                	bpVendorClassificationAttributeDto.add(vendorClassAttrDto);
                }
              
                BPVendorClassificationEntity bpVendorClassificationEntityDo= ObjectMapperUtils.map(bpVendorClassificationEntityDto, BPVendorClassificationEntity.class);
                bpVendorClassificationEntityDo.setVendorClassificationEntityId(bpVendorClassificationEntityDto.getVendorClassificationEntityId());
                bpVendorClassificationEntityDo.setBpVendorClassificationAttribute(null);
                bpVendorClassificationEntityDo.setBpGeneralData(generalDataDo);

                BPVendorClassificationEntity saveBpVendorClassificationEntity = bpVendorClassificationEntityRepository.save(bpVendorClassificationEntityDo);
                
                if (!ServicesUtil.isEmpty(bpVendorClassificationEntityDto.getBpVendorClassificationAttribute())) {
                	
                    for (BPVendorClassificationAttributeDto classificationAttributeDto : bpVendorClassificationAttributeDto) {
                    	
                        BPVendorClassificationAttribute bpVendorClassificationAttributeDo = ObjectMapperUtils.map(classificationAttributeDto,
                        		BPVendorClassificationAttribute.class);
                        
                        bpVendorClassificationAttributeDo.setBpVendorClassificationEntity(saveBpVendorClassificationEntity);
                        
                        bpVendorClassificationAttributeDo.setResults((String) classificationAttributeDto.getBpToCharacterValue().get("results")==""?"":(String) classificationAttributeDto.getBpToCharacterValue().get("results"));
                        bpVendorClassificationAttributeRepository.save(bpVendorClassificationAttributeDo);
                        
                    }
                }

            }
        }
    
		
	}

	private void saveNewPurchasingOrg(ArrayList<BPPurchasingOrgDetailDto> bpPurchasingOrgDetail,
			BPGeneralData generalDataDo) {
		if (!HelperClass.isEmpty(bpPurchasingOrgDetail)) {
			for (BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto : bpPurchasingOrgDetail) {
                List<BPPurchaseOrgAdditionalDataDto> purchaseOrgAdditionalDataDto = new ArrayList<>();
				BPPurchasingOrgDetail bpPurchasingOrgDetailDo = ObjectMapperUtils.map(bpPurchasingOrgDetailDto,
						BPPurchasingOrgDetail.class);
				bpPurchasingOrgDetailDo.setPurchaseId(bpPurchasingOrgDetailDto.getPurchaseId());
				bpPurchasingOrgDetailDo.setBpGeneralData(generalDataDo);
				bpPurchasingOrgDetailDo.setExtend(bpPurchasingOrgDetailDto.getExtend());
				bpPurchasingOrgDetailDo.setOrderingAddressCheck(bpPurchasingOrgDetailDto.getOrderingAddressCheck());
				bpPurchasingOrgDetailDo.setRemittanceAddressCheck(bpPurchasingOrgDetailDto.getRemittanceAddressCheck());
                bpPurchasingOrgDetailDo.setEstimatedAnnualSpend(bpPurchasingOrgDetailDto.getEstimatedAnnualSpend());
                bpPurchasingOrgDetailDo.setSourcingCategory(bpPurchasingOrgDetailDto.getSourcingCategory());
				bpPurchasingOrgDetailDo.setBpPurchaseOrg(null);
				bpPurchasingOrgDetailDo.setBpPurchaseOrgAdditionalData(null);

				BPPurchasingOrgDetail saveBpPurchasingOrgDetail = bpPurchasingOrgDetailRepository
						.save(bpPurchasingOrgDetailDo);

				if (!ServicesUtil.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrg())) {
					BPPurchaseOrg bpPurchaseOrgDo = ObjectMapperUtils.map(bpPurchasingOrgDetailDto.getBpPurchaseOrg(),
							BPPurchaseOrg.class);
					bpPurchaseOrgDo.setBpPurchasingOrgDetail(saveBpPurchasingOrgDetail);
					bpPurchaseOrgRepository.save(bpPurchaseOrgDo);

				}
                if(!ServicesUtil.isEmpty(bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData())) {
                    for(BPPurchaseOrgAdditionalDataDto bpPurchaseOrgAdditionalDataDto : bpPurchasingOrgDetailDto.getBpPurchaseOrgAdditionalData()) {
                        BPPurchaseOrgAdditionalData bpPurchaseOrgAdditionalDataDo = ObjectMapperUtils.map(bpPurchaseOrgAdditionalDataDto,
							BPPurchaseOrgAdditionalData.class);
                        bpPurchaseOrgAdditionalDataDo.setBpPurchasingOrgDetail(saveBpPurchasingOrgDetail);
                        bpPurchaseOrgAdditionalDataRepository.save(bpPurchaseOrgAdditionalDataDo);
                    }
                }

                if (!ServicesUtil.isEmpty(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress())) {
                    BPBusinessPartnerAddressInfo bpBusinessPartnerAddressInfo = ObjectMapperUtils.map(bpPurchasingOrgDetailDto.getBpBusinessPartnerOrderingAddress(), BPBusinessPartnerAddressInfo.class);
                    bpBusinessPartnerAddressInfo.setBpPurchasingOrgDetail(saveBpPurchasingOrgDetail);
                    bpBusinessPartnerAddressInfoRepository.save(bpBusinessPartnerAddressInfo);
                }

                if (!ServicesUtil.isEmpty(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress())) {
                    BPBusinessPartnerAddressInfo bpBusinessPartnerAddressInfo = ObjectMapperUtils.map(bpPurchasingOrgDetailDto.getBpBusinessPartnerRemittanceAddress(), BPBusinessPartnerAddressInfo.class);
                    bpBusinessPartnerAddressInfo.setBpPurchasingOrgDetail(saveBpPurchasingOrgDetail);
                    bpBusinessPartnerAddressInfoRepository.save(bpBusinessPartnerAddressInfo);
                }
            }
        }
    }

    private void saveNewCompanyCode(ArrayList<BPCompanyCodeInfoDto> bpCompanyCodeInfo, BPGeneralData generalDataDo) throws ParseException {
    	if (!HelperClass.isEmpty(bpCompanyCodeInfo)) {
        for (BPCompanyCodeInfoDto bpCompanyCodeInfoDto : bpCompanyCodeInfo) {
            
                BPAccountingInformationDto accountingInfoDto = new BPAccountingInformationDto();
                BPCorrespondanceDto correspondanceDto = new BPCorrespondanceDto();
                List<BPWithholdingTaxDto>withholdingTaxDto=new ArrayList<>();
                accountingInfoDto = ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpAccountingInformation(), accountingInfoDto);
                correspondanceDto=ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpCorrespondance(), correspondanceDto);
                for(BPWithholdingTaxDto whTaxDto:bpCompanyCodeInfoDto.getBpWithholdingTax()) {
                	BPWithholdingTaxDto taxDto=new BPWithholdingTaxDto();
                	taxDto=ObjectMapperUtils.map(whTaxDto,taxDto);
                	withholdingTaxDto.add(taxDto);
                }
                
                bpCompanyCodeInfoDto.getBpAccountingInformation().setCertificationDate(null);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setLastKeyDate(null);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setValidUntil(null);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setLastInterestRun(null);
                
                bpCompanyCodeInfoDto.getBpCorrespondance().setLastDunned(null);
                bpCompanyCodeInfoDto.getBpCorrespondance().setLegalDunnProc(null);
                
				for (BPWithholdingTaxDto dto : bpCompanyCodeInfoDto.getBpWithholdingTax()) {
					dto.setExemptFrom(null);
					dto.setExemptTo(null);
				}
                BPCompanyCodeInfo bpCompanyCodeInfoDo = ObjectMapperUtils.map(bpCompanyCodeInfoDto, BPCompanyCodeInfo.class);
                bpCompanyCodeInfoDo.setCompanyCodeId(bpCompanyCodeInfoDto.getCompanyCodeId());
                bpCompanyCodeInfoDo.setCompanyCode(bpCompanyCodeInfoDto.getCompanyCode());
                bpCompanyCodeInfoDo.setExtend(bpCompanyCodeInfoDto.getExtend());
                bpCompanyCodeInfoDo.setCompanyCountry(bpCompanyCodeInfoDto.getCompanyCountry());
                bpCompanyCodeInfoDo.setBranchCode(bpCompanyCodeInfoDto.getBranchCode());
                bpCompanyCodeInfoDo.setBranchCodeDescription(bpCompanyCodeInfoDto.getBranchCodeDescription());
                bpCompanyCodeInfoDo.setBpGeneralData(generalDataDo);
                bpCompanyCodeInfoDo.setBpAccountingInformation(null);
                bpCompanyCodeInfoDo.setBpPaymentTransaction(null);
                bpCompanyCodeInfoDo.setBpCorrespondance(null);
                bpCompanyCodeInfoDo.setBpWithholdingTax(null);


                BPCompanyCodeInfo saveBpCompanyCodeInfo = bpCompanyCodeInfoRepository.save(bpCompanyCodeInfoDo);
                if (!ServicesUtil.isEmpty(accountingInfoDto)) {
//                    SimpleDateFormat formatter1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date certificationDate = accountingInfoDto.getCertificationDate() == null ? null : formatter.parse(accountingInfoDto.getCertificationDate());
                    Date lastKeyDate = accountingInfoDto.getLastKeyDate() == null ? null : formatter.parse(accountingInfoDto.getLastKeyDate());
                    Date validUntil = accountingInfoDto.getValidUntil() == null ? null : formatter.parse(accountingInfoDto.getValidUntil());
                    Date lastInterestRun=accountingInfoDto.getLastInterestRun()==null?null:formatter.parse(accountingInfoDto.getLastInterestRun());
                    accountingInfoDto.setCertificationDate(null);
                    accountingInfoDto.setLastKeyDate(null);
                    accountingInfoDto.setValidUntil(null);
                    accountingInfoDto.setLastInterestRun(null);
                    BPAccountingInformation bpAccountingInformationDo = ObjectMapperUtils.map(accountingInfoDto,
                            BPAccountingInformation.class);
                    bpAccountingInformationDo.setCertificationDate(certificationDate);
                    bpAccountingInformationDo.setLastKeyDate(lastKeyDate);
                    bpAccountingInformationDo.setValidUntil(validUntil);
                    bpAccountingInformationDo.setLastInterestRun(lastInterestRun);
                    bpAccountingInformationDo.setBpCompanyCodeInfo(saveBpCompanyCodeInfo);
                    bpAccountingInformationRepository.save(bpAccountingInformationDo);
                }
                if (!ServicesUtil.isEmpty(bpCompanyCodeInfoDto.getBpPaymentTransaction())) {
                    BPPaymentTransactions bpPaymentTransactionsDo = ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpPaymentTransaction(),
                            BPPaymentTransactions.class);

                    bpPaymentTransactionsDo.setBpCompanyCodeInfo(saveBpCompanyCodeInfo);
                    bpPaymentTransactionsRepository.save(bpPaymentTransactionsDo);
                }
                if (!ServicesUtil.isEmpty(correspondanceDto)) {
//                	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date lastDunned = correspondanceDto.getLastDunned() == null ? null : formatter.parse(correspondanceDto.getLastDunned());
                    Date legalDunnProc = correspondanceDto.getLegalDunnProc() == null ? null : formatter.parse(correspondanceDto.getLegalDunnProc());
                    correspondanceDto.setLastDunned(null);
                    correspondanceDto.setLegalDunnProc(null);
                    
                    BPCorrespondance bpCorrespondanceDo = ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpCorrespondance(),
                            BPCorrespondance.class);

                    bpCorrespondanceDo.setLastDunned(lastDunned);                    
                    bpCorrespondanceDo.setLegalDunnProc(legalDunnProc);
                    
                    bpCorrespondanceDo.setBpCompanyCodeInfo(saveBpCompanyCodeInfo);
                    bpCorrespondanceRepository.save(bpCorrespondanceDo);
                }
                if (!ServicesUtil.isEmpty(bpCompanyCodeInfoDto.getBpWithholdingTax())) {
                    for (BPWithholdingTaxDto bpWithholdingTaxDto : withholdingTaxDto) {
//                    	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date exemptFrom = bpWithholdingTaxDto.getExemptFrom() == null ? null : formatter.parse(bpWithholdingTaxDto.getExemptFrom());
                        Date exemptTo = bpWithholdingTaxDto.getExemptTo() == null ? null : formatter.parse(bpWithholdingTaxDto.getExemptTo());
                        bpWithholdingTaxDto.setExemptFrom(null);
                        bpWithholdingTaxDto.setExemptTo(null);
                        BPWithholdingTax bpWithholdingTaxDo = ObjectMapperUtils.map(bpWithholdingTaxDto,
                                BPWithholdingTax.class);
                        
                        bpWithholdingTaxDo.setExemptFrom(exemptFrom);
                        bpWithholdingTaxDo.setExemptTo(exemptTo);
                        bpWithholdingTaxDo.setBpCompanyCodeInfo(saveBpCompanyCodeInfo);
                        
                        bpWithholdingTaxRepository.save(bpWithholdingTaxDo);
                    }
                }

            }
        }
    }

    public BPRequestGeneralDataDto getBPDetailsByRequestId(String requestId) throws ClientProtocolException, IOException {

		/*    Get  all the details from all the tables using find by Id in BPGeneralData table(parent table)      */

		BPGeneralData bpGeneralDataDo = bpVendorDetailsRepository.findById(requestId).orElse(null);
		String formatPattern = "yyyy-MM-dd HH:mm:ss";
		DateFormat df = new SimpleDateFormat(formatPattern);
		
		BPRequestGeneralDataDto bpDetailsDto = new BPRequestGeneralDataDto();
		
		/*    Getting details only if the data is present and it is not deleted      */
		
		if (bpGeneralDataDo != null && bpGeneralDataDo.getIsDeleted().compareTo('N') == 0) {
			ArrayList<BPBankInformationDto> bankInformationArrayDto = new ArrayList<>();
			ArrayList<BPControlDataDto> controlDataDto=new ArrayList<>();
			if (!HelperClass.isEmpty(bpGeneralDataDo.getBpBankInformation())) {
				for (BPBankInformation bankInformation : bpGeneralDataDo.getBpBankInformation()) {
					BPBankInformationDto bankInformationDto = new BPBankInformationDto();
					bankInformationDto = ObjectMapperUtils.map(bankInformation, BPBankInformationDto.class);
					bankInformationDto.setValidFrom(
							bankInformation.getValidFrom() == null ? null : df.format(bankInformation.getValidFrom())); //setting date by formating 
					bankInformationDto.setValidTo(
							bankInformation.getValidTo() == null ? null : df.format(bankInformation.getValidTo()));
					bankInformationArrayDto.add(bankInformationDto);
				}
			}
			
			/*    Setting control data starts     */
			
			if (!HelperClass.isEmpty(bpGeneralDataDo.getBpControlData())) {
				for (BPControlData bpControlData : bpGeneralDataDo.getBpControlData()) {
					BPControlDataDto bpControlDataDto = new BPControlDataDto();
					bpControlDataDto = ObjectMapperUtils.map(bpControlData, BPControlDataDto.class);
					bpControlDataDto.setDob(
							bpControlData.getDob() == null ? null : df.format(bpControlData.getDob()));
					bpControlDataDto.setQmSystemTo(bpControlData.getQmSystemTo()==null?null:df.format(bpControlData.getQmSystemTo()));
					bpControlDataDto.setLastExtReview(bpControlData.getLastExtReview()==null?null:df.format(bpControlData.getLastExtReview()));
					
							controlDataDto.add(bpControlDataDto);
				}
			}
			
			/*    Setting control data ends     */
			
			/*    Setting classification data starts     */
			ArrayList<BPVendorClassificationEntityDto> bpVendorClassEntityDto = new ArrayList<>();
			if(!HelperClass.isEmpty(bpGeneralDataDo.getBpVendorClassificationEntity())) {
				
				
				
				for (BPVendorClassificationEntity vendorClassEntity : bpGeneralDataDo.getBpVendorClassificationEntity()) {
					BPVendorClassificationEntityDto vendorClassEntityDto = new BPVendorClassificationEntityDto();

			
					ArrayList<BPVendorClassificationAttributeDto> vendorClassAttrDto=new ArrayList<>();
					for(BPVendorClassificationAttribute bpVendorClassAttr:vendorClassEntity.getBpVendorClassificationAttribute()) {
						BPVendorClassificationAttributeDto bpVendorClassAttrDto=new BPVendorClassificationAttributeDto();
						bpVendorClassAttrDto=ObjectMapperUtils.map(bpVendorClassAttr,BPVendorClassificationAttributeDto.class);
						HashMap<String, Object> map = new HashMap<>();
						map.put("results",bpVendorClassAttr.getResults());
						bpVendorClassAttrDto.setBpToCharacterValue(map);	
						vendorClassAttrDto.add(bpVendorClassAttrDto);
					}
		
					vendorClassEntityDto=ObjectMapperUtils.map(vendorClassEntityDto,BPVendorClassificationEntityDto.class);
					vendorClassEntityDto.setVendorClassificationEntityId(vendorClassEntity.getVendorClassificationEntityId()); 
					vendorClassEntityDto.setClassnum(vendorClassEntity.getClassnum());
					vendorClassEntityDto.setDescription(vendorClassEntity.getDescription());
					vendorClassEntityDto.setBpVendorClassificationAttribute(vendorClassAttrDto);
					bpVendorClassEntityDto.add(vendorClassEntityDto);
				}			
			}
			
			/*    Setting classification data ends     */
			/*    Setting company code data starts     */
			
			
			ArrayList<BPCompanyCodeInfoDto> bpCompanyCodeInfoDto = new ArrayList<>();
			BPAccountingInformationDto accountingInformationDto = new BPAccountingInformationDto();
			if (!HelperClass.isEmpty(bpGeneralDataDo.getBpCompanyCodeInfo())) {
				for (BPCompanyCodeInfo bpCompanyCodeInfo : bpGeneralDataDo.getBpCompanyCodeInfo()) {
					BPCompanyCodeInfoDto codeInfoDto = new BPCompanyCodeInfoDto();

					BPAccountingInformationDto bpAccountingInformationDto = new BPAccountingInformationDto();
					bpAccountingInformationDto=ObjectMapperUtils.map(bpCompanyCodeInfo.getBpAccountingInformation(),BPAccountingInformationDto.class);
					
					BPCorrespondanceDto bpCorrespondanceDto=new BPCorrespondanceDto();
					bpCorrespondanceDto=ObjectMapperUtils.map(bpCompanyCodeInfo.getBpCorrespondance(),BPCorrespondanceDto.class);
					
					ArrayList<BPWithholdingTaxDto> withholdingTaxDto=new ArrayList<>();
					for(BPWithholdingTax bpWithholdingTax:bpCompanyCodeInfo.getBpWithholdingTax()) {
						BPWithholdingTaxDto bpWithholdingTaxDto=new BPWithholdingTaxDto();
						bpWithholdingTaxDto=ObjectMapperUtils.map(bpWithholdingTax,BPWithholdingTaxDto.class);
						bpWithholdingTaxDto.setExemptFrom((bpWithholdingTax.getExemptFrom()==null?null:df.format(bpWithholdingTax.getExemptFrom())));
						bpWithholdingTaxDto.setExemptTo((bpWithholdingTax.getExemptTo()==null?null:df.format(bpWithholdingTax.getExemptTo())));
						withholdingTaxDto.add(bpWithholdingTaxDto);
					}
					
					
					bpAccountingInformationDto.setCertificationDate((bpCompanyCodeInfo.getBpAccountingInformation().getCertificationDate()==null?null:df.format(bpCompanyCodeInfo.getBpAccountingInformation().getCertificationDate())));
					bpAccountingInformationDto.setLastKeyDate((bpCompanyCodeInfo.getBpAccountingInformation().getLastKeyDate()==null?null:df.format(bpCompanyCodeInfo.getBpAccountingInformation().getLastKeyDate())));
					bpAccountingInformationDto.setLastInterestRun((bpCompanyCodeInfo.getBpAccountingInformation().getLastInterestRun()==null?null:df.format(bpCompanyCodeInfo.getBpAccountingInformation().getLastInterestRun())));;
					bpAccountingInformationDto.setValidUntil((bpCompanyCodeInfo.getBpAccountingInformation().getValidUntil()==null?null:df.format(bpCompanyCodeInfo.getBpAccountingInformation().getValidUntil())));
					
					bpCorrespondanceDto.setLastDunned((bpCompanyCodeInfo.getBpCorrespondance().getLastDunned()==null?null:df.format(bpCompanyCodeInfo.getBpCorrespondance().getLastDunned())));					
					bpCorrespondanceDto.setLegalDunnProc((bpCompanyCodeInfo.getBpCorrespondance().getLegalDunnProc()==null?null:df.format(bpCompanyCodeInfo.getBpCorrespondance().getLegalDunnProc())));
					
					
					
					codeInfoDto=ObjectMapperUtils.map(codeInfoDto,BPCompanyCodeInfoDto.class);
                    codeInfoDto.setCompanyCodeId(bpCompanyCodeInfo.getCompanyCodeId());
                    codeInfoDto.setCompanyCode(bpCompanyCodeInfo.getCompanyCode());
                    codeInfoDto.setExtend(bpCompanyCodeInfo.getExtend());
                    codeInfoDto.setCompanyCountry(bpCompanyCodeInfo.getCompanyCountry());
                    codeInfoDto.setWhTaxCountry(bpCompanyCodeInfo.getWhTaxCountry());
                    codeInfoDto.setBranchCode(bpCompanyCodeInfo.getBranchCode());
                    codeInfoDto.setBranchCodeDescription(bpCompanyCodeInfo.getBranchCodeDescription());
                    codeInfoDto.setBpAccountingInformation(bpAccountingInformationDto);
                    codeInfoDto.setBpPaymentTransaction(ObjectMapperUtils.map(bpCompanyCodeInfo.getBpPaymentTransaction(), BPPaymentTransactionsDto.class));
                    codeInfoDto.setBpCorrespondance(bpCorrespondanceDto);
                    codeInfoDto.setBpWithholdingTax(withholdingTaxDto);
					bpCompanyCodeInfoDto.add(codeInfoDto);
				}
			}
			
			/*    Setting company code data ends     */
			
				bpDetailsDto.setBpBankInformation(null);
				bpDetailsDto.setBpControlData(null);
			    bpDetailsDto.setBpCompanyCodeInfo(null);
			    bpDetailsDto.setBpVendorClassificationEntity(null);	
			    bpDetailsDto.setBpDmsAttachments(null);
			    
			    /*    Setting data from entity to dto using model mapper     */
			    
				bpDetailsDto = ObjectMapperUtils.map(bpGeneralDataDo, BPRequestGeneralDataDto.class);
				
				
				bpDetailsDto.setBirthDate(
						bpGeneralDataDo.getBirthDate() == null ? null : df.format(bpGeneralDataDo.getBirthDate()));
				bpDetailsDto.setCreatedOn(
						bpGeneralDataDo.getCreatedOn() == null ? null : df.format(bpGeneralDataDo.getCreatedOn()));
				
				
				bpDetailsDto.setBpBankInformation(bankInformationArrayDto);
				bpDetailsDto.setBpControlData(controlDataDto);
				bpDetailsDto.setBpCompanyCodeInfo(bpCompanyCodeInfoDto);
				bpDetailsDto.setBpVendorClassificationEntity(bpVendorClassEntityDto);
				bpDetailsDto.setBpDmsAttachments(bPFileService.getAttachmentsByRequestID(requestId));

                //setting the Ordering address and Remittance address
				
            if(!HelperClass.isEmpty(bpGeneralDataDo.getBpPurchasingOrgDetail())) {
                for (BPPurchasingOrgDetail bpPurchasingOrgDetail : bpGeneralDataDo.getBpPurchasingOrgDetail()) {
                    for (BPBusinessPartnerAddressInfo bpBusinessPartnerAddressInfo : bpPurchasingOrgDetail.getBpBusinessPartnerAddressInfo()) {
                        if(bpBusinessPartnerAddressInfo.getBusinessPartnerAddressType().equalsIgnoreCase("Ordering address")) {
                            BPBusinessPartnerOrderingAddressDto bpBusinessPartnerOrderingAddressDto = new BPBusinessPartnerOrderingAddressDto();
                            bpBusinessPartnerOrderingAddressDto = ObjectMapperUtils.map(bpBusinessPartnerAddressInfo, BPBusinessPartnerOrderingAddressDto.class);
                            for (BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto : bpDetailsDto.getBpPurchasingOrgDetail() ) {
                                bpPurchasingOrgDetailDto.setBpBusinessPartnerOrderingAddress(bpBusinessPartnerOrderingAddressDto);
                            }
                        } else if (bpBusinessPartnerAddressInfo.getBusinessPartnerAddressType().equalsIgnoreCase("Remittance address")) {
                            BPBusinessPartnerRemittanceAddressDto bpBusinessPartnerRemittanceAddressDto = new BPBusinessPartnerRemittanceAddressDto();
                            bpBusinessPartnerRemittanceAddressDto = ObjectMapperUtils.map(bpBusinessPartnerAddressInfo, BPBusinessPartnerRemittanceAddressDto.class);
                            for (BPPurchasingOrgDetailDto bpPurchasingOrgDetailDto : bpDetailsDto.getBpPurchasingOrgDetail() ) {
                                bpPurchasingOrgDetailDto.setBpBusinessPartnerRemittanceAddress(bpBusinessPartnerRemittanceAddressDto);
                            }
                        }
                    }
                }
            }
			}
		else {
				bpDetailsDto = null;
			}
			System.out.println("BpDetailsDto Payload " + new Gson().toJson(bpDetailsDto));
			return bpDetailsDto;
		}

//	public BPGeneralDataDto updateVendorDetails(Integer requestId,BPGeneralDataDto bpVendorDetailsDto) throws NamingException, IOException, UnirestException, ExecutionFault, InvalidInputFault, NoResultFault{
//		BPGeneralData existingBPGeneralDataDo = bpVendorDetailsRepository.findById(requestId).orElse(null);
//		
//		existingBPGeneralDataDo=ObjectMapperUtils.map(bpVendorDetailsDto, existingBPGeneralDataDo);
//		existingBPGeneralDataDo=bpVendorDetailsRepository.save(existingBPGeneralDataDo);
//		BPGeneralDataDto detailsDto=ObjectMapperUtils.map(existingBPGeneralDataDo, BPGeneralDataDto.class);
//		return detailsDto;
//	}


    public CreateorUpdateBPResponseDto updateBPDetails(String requestId, BPRequestGeneralDataDto bpDetailsDto) throws NamingException, IOException, UnirestException, ExecutionFault, InvalidInputFault, NoResultFault, ParseException {

        /*    Getting existing details by requestId      */
    	CreateorUpdateBPResponseDto updatebpResponseDto=new CreateorUpdateBPResponseDto();
    	BPRequestGeneralDataDto updatedBpRequestGeneralDataDto=new BPRequestGeneralDataDto();
        updatedBpRequestGeneralDataDto=ObjectMapperUtils.map(bpDetailsDto,updatedBpRequestGeneralDataDto);
        ArrayList<BPBankInformationDto> updatedBpBankInformationDto=new ArrayList<>();
        for(BPBankInformationDto bpBankInformationDto:bpDetailsDto.getBpBankInformation()){
            BPBankInformationDto bankInformationDto=new BPBankInformationDto();
            bankInformationDto=ObjectMapperUtils.map(bpBankInformationDto,bankInformationDto);
            updatedBpBankInformationDto.add(bankInformationDto);
        }
        ArrayList<BPCompanyCodeInfoDto> updatedBpCompanyCodeInfoDto=new ArrayList<>();
        for(BPCompanyCodeInfoDto bpCompanyCodeInfoDto:bpDetailsDto.getBpCompanyCodeInfo()){
            BPCompanyCodeInfoDto companyCodeInfoDto=new BPCompanyCodeInfoDto();
            BPAccountingInformationDto accountingInformationDto=new BPAccountingInformationDto();
            accountingInformationDto=ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpAccountingInformation(),accountingInformationDto);
            companyCodeInfoDto=ObjectMapperUtils.map(bpCompanyCodeInfoDto,companyCodeInfoDto);
            companyCodeInfoDto.setBpAccountingInformation(accountingInformationDto);
            updatedBpCompanyCodeInfoDto.add(companyCodeInfoDto);
        }
        BPGeneralData existingBPGeneralDataDo = bpVendorDetailsRepository.findById(requestId).orElse(null);
        bpDetailsDto.setBirthDate(null);
        bpDetailsDto.setCreatedOn(null);
        System.out.println(updatedBpRequestGeneralDataDto.toString());
        if(!HelperClass.isEmpty(bpDetailsDto.getBpBankInformation())) {
            for (BPBankInformationDto bpBankInformationDto: bpDetailsDto.getBpBankInformation()){
                bpBankInformationDto.setValidFrom(null);
                bpBankInformationDto.setValidTo(null);
            }
        }

        if(!HelperClass.isEmpty(bpDetailsDto.getBpCompanyCodeInfo())){
            for(BPCompanyCodeInfoDto bpCompanyCodeInfoDto:bpDetailsDto.getBpCompanyCodeInfo()){
                if(!HelperClass.isEmpty(bpCompanyCodeInfoDto.getBpAccountingInformation())){
                    bpCompanyCodeInfoDto.getBpAccountingInformation().setCertificationDate(null);
                    bpCompanyCodeInfoDto.getBpAccountingInformation().setLastKeyDate(null);
                    bpCompanyCodeInfoDto.getBpAccountingInformation().setValidUntil(null);
                    bpCompanyCodeInfoDto.getBpAccountingInformation().setLastInterestRun(null);
                }
            }
        }
        existingBPGeneralDataDo=ObjectMapperUtils.map(bpDetailsDto, existingBPGeneralDataDo);
//        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date birthDate =updatedBpRequestGeneralDataDto.getBirthDate()==null?null:formatter.parse(updatedBpRequestGeneralDataDto.getBirthDate());
        Date createdDate =updatedBpRequestGeneralDataDto.getCreatedOn()==null?null:formatter.parse(updatedBpRequestGeneralDataDto.getCreatedOn());
        existingBPGeneralDataDo.setBirthDate(birthDate);
        existingBPGeneralDataDo.setCreatedOn(createdDate);
        ArrayList<BPBankInformation>bankInformationDo=new ArrayList<>();
        if(!HelperClass.isEmpty(updatedBpRequestGeneralDataDto.getBpBankInformation())) {

            for (BPBankInformationDto bpBankInformationDto : updatedBpBankInformationDto) {
                BPBankInformation bpBankInformation =new BPBankInformation();
                Date validFrom=bpBankInformationDto.getValidFrom()==null?null:formatter.parse(bpBankInformationDto.getValidFrom());
                Date validTo=bpBankInformationDto.getValidTo()==null?null:formatter.parse(bpBankInformationDto.getValidTo());
                bpBankInformationDto.setValidFrom(null);
                bpBankInformationDto.setValidTo(null);
                bpBankInformation=ObjectMapperUtils.map(bpBankInformationDto,BPBankInformation.class);
                bpBankInformation.setValidFrom(validFrom);
                bpBankInformation.setValidTo(validTo);
                bpBankInformation.setBpGeneralData(existingBPGeneralDataDo);
                bankInformationDo.add(bpBankInformation);
            }
        }
        ArrayList<BPCompanyCodeInfo>companyCodeInfoDo=new ArrayList<>();
        if(!HelperClass.isEmpty(updatedBpCompanyCodeInfoDto)) {

            for (BPCompanyCodeInfoDto bpCompanyCodeInfoDto : updatedBpCompanyCodeInfoDto) {
                BPAccountingInformationDto accountingInfoDto = new BPAccountingInformationDto();
                accountingInfoDto = ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpAccountingInformation(), accountingInfoDto);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setCertificationDate(null);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setLastKeyDate(null);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setValidUntil(null);
                bpCompanyCodeInfoDto.getBpAccountingInformation().setLastInterestRun(null);
                BPCompanyCodeInfo bpCompanyCodeInfoDo = new BPCompanyCodeInfo();
                bpCompanyCodeInfoDo=ObjectMapperUtils.map(bpCompanyCodeInfoDto, BPCompanyCodeInfo.class);
                bpCompanyCodeInfoDo.setCompanyCodeId(bpCompanyCodeInfoDto.getCompanyCodeId());
                bpCompanyCodeInfoDo.setCompanyCode(bpCompanyCodeInfoDto.getCompanyCode());
                bpCompanyCodeInfoDo.setBpGeneralData(existingBPGeneralDataDo);


                if (!ServicesUtil.isEmpty(accountingInfoDto)) {

                    Date certificationDate = accountingInfoDto.getCertificationDate() == null ? null : formatter.parse(accountingInfoDto.getCertificationDate());
                    Date lastKeyDate = accountingInfoDto.getLastKeyDate() == null ? null : formatter.parse(accountingInfoDto.getLastKeyDate());
                    Date validUntil = accountingInfoDto.getValidUntil() == null ? null : formatter.parse(accountingInfoDto.getValidUntil());
                    Date lastInterestRun = accountingInfoDto.getLastInterestRun()==null?null:formatter.parse(accountingInfoDto.getValidUntil());                 
                    accountingInfoDto.setCertificationDate(null);
                    accountingInfoDto.setLastKeyDate(null);
                    accountingInfoDto.setValidUntil(null);
                    accountingInfoDto.setLastInterestRun(null);
                    
                    BPAccountingInformation bpAccountingInformationDo = ObjectMapperUtils.map(accountingInfoDto,
                            BPAccountingInformation.class);

                    bpAccountingInformationDo.setCertificationDate(certificationDate);
                    bpAccountingInformationDo.setLastKeyDate(lastKeyDate);
                    bpAccountingInformationDo.setValidUntil(validUntil);
                    bpAccountingInformationDo.setLastInterestRun(lastInterestRun);
                    bpAccountingInformationDo.setBpCompanyCodeInfo(bpCompanyCodeInfoDo);
                    bpCompanyCodeInfoDo.setBpAccountingInformation(bpAccountingInformationDo);
                }

                if (!ServicesUtil.isEmpty(bpCompanyCodeInfoDto.getBpPaymentTransaction())) {
                    BPPaymentTransactions bpPaymentTransactionsDo = ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpPaymentTransaction(),
                            BPPaymentTransactions.class);

                    bpPaymentTransactionsDo.setBpCompanyCodeInfo(bpCompanyCodeInfoDo);
                    bpCompanyCodeInfoDo.setBpPaymentTransaction(bpPaymentTransactionsDo);
                }

                if (!ServicesUtil.isEmpty(bpCompanyCodeInfoDto.getBpCorrespondance())) {
                    BPCorrespondance bpCorrespondanceDo = ObjectMapperUtils.map(bpCompanyCodeInfoDto.getBpCorrespondance(),
                            BPCorrespondance.class);

                    bpCorrespondanceDo.setBpCompanyCodeInfo(bpCompanyCodeInfoDo);
                    bpCompanyCodeInfoDo.setBpCorrespondance(bpCorrespondanceDo);
                }

                ArrayList<BPWithholdingTax>withholdingTaxes=new ArrayList<>();
                if (!ServicesUtil.isEmpty(bpCompanyCodeInfoDto.getBpWithholdingTax())) {

                    for (BPWithholdingTaxDto bpWithholdingTaxDto : bpCompanyCodeInfoDto
                            .getBpWithholdingTax()) {
                        BPWithholdingTax bpWithholdingTaxDo = ObjectMapperUtils.map(bpWithholdingTaxDto,
                                BPWithholdingTax.class);
                        bpWithholdingTaxDo.setBpCompanyCodeInfo(bpCompanyCodeInfoDo);
                        withholdingTaxes.add(bpWithholdingTaxDo);
                    }
                }
                bpCompanyCodeInfoDo.setBpWithholdingTax(withholdingTaxes);
                companyCodeInfoDo.add(bpCompanyCodeInfoDo);
            }
        }

        existingBPGeneralDataDo.setBpCompanyCodeInfo(companyCodeInfoDo);
        existingBPGeneralDataDo.setBpBankInformation(bankInformationDo);
        bpVendorDetailsRepository.save(existingBPGeneralDataDo);
        updatebpResponseDto.setRequestId(requestId);
        updatebpResponseDto.setMessage("Data Updated Successfully!!");
        return updatebpResponseDto;
    }

    public ResponseDto deleteBPDetails(String requestId) {
        ResponseDto responseMessage = new ResponseDto();
        BPGeneralData bpGeneralData = bpVendorDetailsRepository.findByRequestId(requestId);
        bpGeneralData.setIsDeleted('Y');
        bpVendorDetailsRepository.save(bpGeneralData);
        responseMessage.setMessage("Deleted successfully");
        responseMessage.setStatus(HttpStatus.CREATED.getReasonPhrase());
        responseMessage.setCode("200");
        return responseMessage;


    }

    public void findDetails(Integer requestId) {
        List<Object[]> obj = bpVendorDetailsRepository.findDetails(requestId);
        for (Object[] result : obj) {
            String vendorNo = (String) result[0];
            String accGrp = (String) result[1];
            System.out.println(vendorNo);
            System.out.println(accGrp);
        }


    }
    
    public List<BPRequestGeneralDataDto> filterGeneralData(String requestId) {
        List<BPGeneralData> obj = bpVendorDetailsRepository.filterGeneralData(requestId);
//        System.out.println(obj.toString());
        List<BPRequestGeneralDataDto> bpRequestGeneralDataDto=new ArrayList<>();
        bpRequestGeneralDataDto=ObjectMapperUtils.mapAll(obj, BPRequestGeneralDataDto.class);
        return bpRequestGeneralDataDto;
    }

    public String getRequestID() {
        return bpVendorDetailsRepository.getNextRequestID().toString();
    }
    private static String uri;

	private static String tokenUrl;

	private static String clientId;

	private static String clientSecret;
//    public String accessToken() throws JsonMappingException, JsonProcessingException {
//		uri= ApplicationConstants.CPI_URI;
//		tokenUrl=ApplicationConstants.CPI_HOST;
//		clientId=ApplicationConstants.CPI_CLIENT_ID;
//		clientSecret=ApplicationConstants.CPI_CLIENT_SECRET;
//
//		String url ="https://"+ tokenUrl + "/oauth/token?grant_type=client_credentials";
//		RestTemplate template = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		com.nimbusds.jose.util.Base64 encode = com.nimbusds.jose.util.Base64.encode(clientId + ":" + clientSecret);
//		headers.add("Authorization", "Basic " + encode.toString());
//		HttpEntity<String> entity = new HttpEntity<>(headers);
//		ResponseEntity<String> response = template.postForEntity(url, entity, String.class);
//
//		return new ObjectMapper().readTree(response.getBody()).get("access_token").asText();
//	}
    public String getAccessToken(String clientid, String clientsecret, String tokenUrl){

        URI myURI = null;
        try {
            myURI = new URI(tokenUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        URI finalMyURI = myURI;
        JSONObject authenticationResponseObject = WebClient.builder()
                .filter(basicAuthentication(clientid,
                        clientsecret))
                .build().post()
                .uri(uriBuilder -> uriBuilder.scheme(finalMyURI.getScheme())
                        .host(finalMyURI.getHost())
                        .path(finalMyURI.getPath())
                        .queryParam("grant_type", "client_credentials").build())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        return authenticationResponseObject.get("access_token").toString();
    }
    
    public RequestBenchDto getRequestData(BPRequestSearchCriteriaDto bpRequestSearchCriteriaDto) {
        List<BPTaskBenchDataDto> listOfVendorInfoDto = new ArrayList<>();
        RequestBenchDto requestBenchDto = new RequestBenchDto();
        
        
//        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
//        Timestamp createdOnTimeStamp = null;
//        Timestamp currentTime = null;
//        String dateRange = searchHeaderRequestDto.getCreatedOn();
//        if (bpRequestSearchCriteriaDto.getCreatedOn() != null) {
//        	String fromStringDate = dateRange + " 00:00:00";
//        	Date fromDate;
//        	try {
//        		fromDate = parser.parse(fromStringDate);
//        		} catch (ParseException e) {
//        		throw new RuntimeException(e);
//        		}
//        		createdOnTimeStamp = new Timestamp(fromDate.getTime());
//        }
        Timestamp createdOnTimeStamp = null;
        Timestamp currentTime = null;
        if (bpRequestSearchCriteriaDto.getCreatedOn() != null) {
            Date date = new Date();
            currentTime = new Timestamp(date.getTime());
            String pattern = "yyyy-MM-dd";
            DateFormat df = new SimpleDateFormat(pattern);
            String todayAsString = df.format(bpRequestSearchCriteriaDto.getCreatedOn()) + " 00:00:00";
            Date fromDate;
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                fromDate = parser.parse(todayAsString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            createdOnTimeStamp = new Timestamp(fromDate.getTime());
        }
        System.out.println(createdOnTimeStamp+"****"+currentTime);
        Pageable paging = PageRequest.of(bpRequestSearchCriteriaDto.getPage(), bpRequestSearchCriteriaDto.getSize());
//        String []contactPerson=bpRequestSearchCriteriaDto.getContactPerson().split(" ");
//        List<String> contactPersonFullName=Arrays.asList(contactPerson);
        try {
            Page<BPGeneralData> listOfVendorsInfo = bpVendorDetailsRepository.filterRequest(bpRequestSearchCriteriaDto.getCreatedBy(),
                    bpRequestSearchCriteriaDto.getName1() ,
                    bpRequestSearchCriteriaDto.getRequestId(),
                    bpRequestSearchCriteriaDto.getRequestTypeId(),
                    bpRequestSearchCriteriaDto.getStatusId(),
                    bpRequestSearchCriteriaDto.getBupaNo(),
                    bpRequestSearchCriteriaDto.getSearchTerm1(),
                    bpRequestSearchCriteriaDto.getSearchTerm2(),
                    bpRequestSearchCriteriaDto.getDistrict(),
                    bpRequestSearchCriteriaDto.getRegion(),
                    bpRequestSearchCriteriaDto.getBankAccountNo(),
                    bpRequestSearchCriteriaDto.getIban(),
                    bpRequestSearchCriteriaDto.getCompanyCode(),
                    bpRequestSearchCriteriaDto.getPurchasingOrg(),
//                    contactPersonFullName,
                    bpRequestSearchCriteriaDto.getContactPerson(),
                    bpRequestSearchCriteriaDto.getTelephone(),
                    bpRequestSearchCriteriaDto.getEmail(),
                    bpRequestSearchCriteriaDto.getSystemId(),
                    createdOnTimeStamp,
                    currentTime,
                    paging
            );
            List<BPRequestGeneralDataDto> bpRequestGeneralDataDto=new ArrayList<>();
            bpRequestGeneralDataDto =  ObjectMapperUtils.mapAll(listOfVendorsInfo.getContent(), BPRequestGeneralDataDto.class);
            listOfVendorInfoDto = ObjectMapperUtils.mapAll(bpRequestGeneralDataDto,BPTaskBenchDataDto.class);
            requestBenchDto.setBpTaskBenchData(listOfVendorInfoDto);
            requestBenchDto.setTotalCount(listOfVendorsInfo.getTotalElements());
//            int totalPages = (int) Math.ceil((double) totalCount / pageSize);
            requestBenchDto.setTotalPages(listOfVendorsInfo.getTotalPages());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return requestBenchDto;
    }

	public RequestBenchDto getAdvanceSearchRequestData(
			BPAdvanceRequestSearchCriteriaDto bpAdvanceRequestSearchCriteriaDto) {
		List<BPTaskBenchDataDto> listOfVendorInfoDto = new ArrayList<>();
        RequestBenchDto requestBenchDto = new RequestBenchDto();

         SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
         Timestamp createdOnTimeStamp = null;
         Timestamp createdOnTimeRange = null; 
         String dateRange = bpAdvanceRequestSearchCriteriaDto.getCreatedOn();
         if(dateRange!=null) {
         // Split the date range string by the " - " delimiter
         String[] dateParts = dateRange.split(" - ");
         if (dateParts.length == 2) {
         String startDateStr = dateParts[0];
         String endDateStr = dateParts[1];
         String fromStringDate = startDateStr + " 00:00:00";
         String toStringDate = endDateStr + " 00:00:00";
         Date fromDate;
         Date toDate;
         try {
         fromDate = parser.parse(fromStringDate);
         toDate = parser.parse(toStringDate);
         } catch (ParseException e) {
         throw new RuntimeException(e);
         }
         createdOnTimeStamp = new Timestamp(fromDate.getTime());
         createdOnTimeRange = new Timestamp(toDate.getTime());
         }
         }

         
        Pageable paging = PageRequest.of(bpAdvanceRequestSearchCriteriaDto.getPage(), bpAdvanceRequestSearchCriteriaDto.getSize());

        try {
            Page<BPGeneralData> listOfVendorsInfo = customBPGeneralDataRepository.filterRequest(
            		bpAdvanceRequestSearchCriteriaDto.getCreatedBy(),
            		bpAdvanceRequestSearchCriteriaDto.getName1(),
                    bpAdvanceRequestSearchCriteriaDto.getBupaNo(),
                    bpAdvanceRequestSearchCriteriaDto.getRequestId(),
                    bpAdvanceRequestSearchCriteriaDto.getRequestTypeId(),
                    bpAdvanceRequestSearchCriteriaDto.getStatusId(),
                    bpAdvanceRequestSearchCriteriaDto.getSystemId(),
                    bpAdvanceRequestSearchCriteriaDto.getSearchTerm1(),
                    bpAdvanceRequestSearchCriteriaDto.getSearchTerm2(),
                    bpAdvanceRequestSearchCriteriaDto.getDistrict(),
                    bpAdvanceRequestSearchCriteriaDto.getRegion(),
                    bpAdvanceRequestSearchCriteriaDto.getCountry(),
                    bpAdvanceRequestSearchCriteriaDto.getBupaAccountGrp(),
                    bpAdvanceRequestSearchCriteriaDto.getEmail(),
                    bpAdvanceRequestSearchCriteriaDto.getTelephone(),
                    bpAdvanceRequestSearchCriteriaDto.getContactPerson(),
                    bpAdvanceRequestSearchCriteriaDto.getBankAccount(),
                    bpAdvanceRequestSearchCriteriaDto.getIban(),
                    bpAdvanceRequestSearchCriteriaDto.getPurchasingOrganization(),
                    bpAdvanceRequestSearchCriteriaDto.getCompanyCode(),
                    createdOnTimeStamp,
                    createdOnTimeRange,
                    paging,
                    bpAdvanceRequestSearchCriteriaDto.getSearchType()
            );

            List<BPTaskBenchDataDto> bpRequestGeneralDataDto = listOfVendorsInfo.getContent().stream()
                    .map(source -> {
                    	BPRequestGeneralDataDto destination = ObjectMapperUtils.map(source, BPRequestGeneralDataDto.class);
                        destination.setCreatedOn(source.getCreatedOn() == null ? "" : parser.format(source.getCreatedOn()));
                        String countryCode = source.getBpAddressInfo().getCountry();
                        // Convert List<BPCompanyCodeInfoDto> to comma-separated string
                        String bpCompanyCodeInfo ="";
//                        if(destination.getRequestTypeId()==1){
                        	bpCompanyCodeInfo = source.getBpCompanyCodeInfo().stream()
                                    .map(BPCompanyCodeInfo::getCompanyCode)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.joining(","));
//                        }
						/*
						 * else if(destination.getRequestTypeId()==2) { Object auditLogInfo =
						 * auditLogService.getAuditLogInfoByRequestId( destination.getRequestId());
						 * 
						 * ObjectMapper objectMapper = new ObjectMapper(); String auditLogInfoJson =
						 * null; try { auditLogInfoJson = objectMapper.writeValueAsString(auditLogInfo);
						 * } catch (JsonProcessingException e) { // TODO Auto-generated catch block
						 * e.printStackTrace(); }
						 * 
						 * JSONArray dataList = new JSONArray(auditLogInfoJson);
						 * 
						 * List<String> pathList = new ArrayList<>();
						 * 
						 * for (int i = 0; i < dataList.length(); i++) { JSONObject dataObject =
						 * dataList.getJSONObject(i); String path = dataObject.getString("path");
						 * pathList.add(path); }
						 * 
						 * List<Integer> companyCodeList = new ArrayList<>(); Pattern ccodepattern =
						 * Pattern.compile("bpCompanyCodeInfo\\.(\\d+)\\.");
						 * 
						 * for (String path : pathList) { Matcher matcher = ccodepattern.matcher(path);
						 * while (matcher.find()) { int index = Integer.parseInt(matcher.group(1)); if
						 * (path.contains("bpCompanyCodeInfo")) {
						 * 
						 * companyCodeList.add(index); } } }
						 * 
						 * 
						 * String companycodeAsList = source.getBpCompanyCodeInfo().stream()
						 * .map(BPCompanyCodeInfo::getCompanyCode) .filter(Objects::nonNull)
						 * .collect(Collectors.joining(","));
						 * 
						 * 
						 * 
						 * String strCompCode2[] = companycodeAsList.split(",");
						 * 
						 * List<String> companyCoderesult = new ArrayList<>(); Set<Integer>
						 * companyCodeSet = new HashSet<>();
						 * 
						 * for (int index : companyCodeList) { companyCodeSet.add(index); }
						 * 
						 * for (int index : companyCodeSet) {
						 * companyCoderesult.add(strCompCode2[index]); }
						 * System.out.println(companyCoderesult);
						 * 
						 * String companyCodeConcatResult = String.join(",", companyCoderesult);
						 * 
						 * bpCompanyCodeInfo=companyCodeConcatResult; } else
						 * if(destination.getRequestTypeId()==3&&destination.getSubProcessType().
						 * equalsIgnoreCase("CompanyCode")) { String
						 * bpCompanyCodeInfoList=source.getBpCompanyCodeInfo().stream()
						 * .map(BPCompanyCodeInfo::getCompanyCode) .filter(Objects::nonNull)
						 * .collect(Collectors.joining(",")); String[] parts =
						 * bpCompanyCodeInfoList.split(","); String lastPart = parts[parts.length - 1];
						 * bpCompanyCodeInfo=lastPart; } else
						 * if(destination.getRequestTypeId()==3&&destination.getSubProcessType().
						 * equalsIgnoreCase("PurchaseOrg")) { bpCompanyCodeInfo=""; } else
						 * if(destination.getRequestTypeId()==3&&destination.getSubProcessType().
						 * equalsIgnoreCase("AddnData")) { bpCompanyCodeInfo=""; }
						 */
                        
                        BPTaskBenchDataDto bpTaskBenchDataDto = new BPTaskBenchDataDto();
                        bpTaskBenchDataDto.setRequestId(destination.getRequestId());
                        bpTaskBenchDataDto.setRequestTypeId(destination.getRequestTypeId());
                        bpTaskBenchDataDto.setBupaNo(destination.getBupaNo());
                        bpTaskBenchDataDto.setName1(destination.getName1());
                        bpTaskBenchDataDto.setCreatedBy(destination.getCreatedBy());
                        bpTaskBenchDataDto.setCreatedOn(destination.getCreatedOn());
                        bpTaskBenchDataDto.setStatusId(destination.getStatusId());
                        bpTaskBenchDataDto.setSystemId(destination.getSystemId());
                        bpTaskBenchDataDto.setSubProcessType(destination.getSubProcessType());
                        bpTaskBenchDataDto.setBupaAccountGrp(destination.getBupaAccountGrp());
                        bpTaskBenchDataDto.setCountryCode(countryCode);
                        bpTaskBenchDataDto.setCompanyCode(bpCompanyCodeInfo);

                        return bpTaskBenchDataDto;
                    })
                    .collect(Collectors.toList());
            
            requestBenchDto.setBpTaskBenchData(bpRequestGeneralDataDto);
            requestBenchDto.setTotalCount(listOfVendorsInfo.getTotalElements());
            requestBenchDto.setTotalPages(listOfVendorsInfo.getTotalPages());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return requestBenchDto;
	}

	public ResponseDto updateDraftStatus(String requestId,int statusId) {
		ResponseDto dto=new ResponseDto();
				
		bpVendorDetailsRepository.updateStatusId(requestId, statusId);
		dto.setMessage("Updated successfully");
		dto.setStatus(HttpStatus.OK.getReasonPhrase());
		dto.setCode("200");
		return dto;
	}

//    @Transactional
//    public List<BPGeneralData> getRequestBenchData(RequestBenchDto requestBenchDto) {
//        List<BPRequestGeneralDataDto> data = new ArrayList<>();
    //    Pageable paging = PageRequest.of(requestBenchDto.getPage(), requestBenchDto.getSize());
//        try {
//            List<BPGeneralData> listOfVendorsInfo = bpVendorDetailsRepository.filterRequestBench(requestBenchDto.getRequestId(),
//                    requestBenchDto.getRequestType(),
//                    requestBenchDto.getCreatedBy());
//          //  System.out.println(listOfVendorsInfo.get());
//            return listOfVendorsInfo;
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e);
//        }

//         BPGeneralData listOfVendorsInfo = (BPGeneralData) bpVendorDetailsRepository.filterRequestBench(requestBenchDto.getRequestId(),
//                    requestBenchDto.getRequestType(),
//                   requestBenchDto.getCreatedBy());
//        System.out.println(listOfVendorsInfo);
//        return null;
//    }
    
}