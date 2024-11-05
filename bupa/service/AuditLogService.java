package com.incture.bupa.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.incture.bupa.dto.BPAuditLogDto;
import com.incture.bupa.entities.BPAuditLog;
import com.incture.bupa.repository.BPAuditLogRepository;
import com.incture.bupa.utils.DateUtil;

@Service
public class AuditLogService {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BPAuditLogRepository bpAuditLogRepository;

    /*
     * Method to Covert CMAuditLog entity to CMAuditLogDto
     * */
    public BPAuditLogDto entityToDto(BPAuditLog bpAuditLog) {
    	BPAuditLogDto bpAuditLogDto = this.modelMapper.map(bpAuditLog, BPAuditLogDto.class);
        LOGGER.info("After converting the CMAuditLog entity to CMAuditLogDto :" + bpAuditLogDto.toString());
        return bpAuditLogDto;
    }

    /*
     * Method to Convert CMAuditLogDto to CMAuditLog entity and
     * */
    public BPAuditLog dtoToEntity(BPAuditLogDto bpAuditLogDto) {
    	modelMapper.typeMap(BPAuditLogDto.class, BPAuditLog.class).addMappings(mp -> {
    		mp.skip(BPAuditLog::setUpdatedOn);
    		});
    	BPAuditLog bpAuditLog = this.modelMapper.map(bpAuditLogDto, BPAuditLog.class);
    	bpAuditLog.setUpdatedOn(DateUtil.stringToDate(bpAuditLogDto.getUpdatedOn()));
        LOGGER.info("After converting the CMAuditLogDto to CMAuditLog entity :" + bpAuditLog.toString());
        return bpAuditLog;
    }
    
    public String saveAndDeleteAuditLogInfo(List<BPAuditLogDto> bpAuditLogDto) {
        String responseMessage="";

                if(bpAuditLogDto != null || bpAuditLogDto.size()!=0){ 
                    List<BPAuditLog> optional = bpAuditLogRepository.findByRequestId(bpAuditLogDto.get(0).getRequestId());

                   
                    	bpAuditLogRepository.deleteAll(optional);
                        LOGGER.info("Successfully deleted AuditLogInfo from DB ");
                        List<BPAuditLog> cmAuditLog = bpAuditLogDto.stream().filter(p -> !(p.getOldValue()==null?"":p.getOldValue()).equals((p.getNewValue()==null?"":p.getNewValue()))).map(p -> this.dtoToEntity(p)).collect(Collectors.toList());
//                        for (BPAuditLog auditLog : cmAuditLog) {
//                            auditLog.setSerialNo(bpAuditLogRepository.getNextRequestID());
//                        }
                        bpAuditLogRepository.saveAll(cmAuditLog);
                        LOGGER.info("Successfully Saved AuditLogInfo from DB ");
                        responseMessage="Successfully deletedAndSave AuditLogInfo from DB";
                  
                }


        return responseMessage;
    }

	public List<BPAuditLogDto> getAuditLogInfoByRequestId(String requestId) {
		List<BPAuditLog> bpAuditLogs = bpAuditLogRepository.findByRequestIdOrderByUpdatedOnDesc(requestId);

		List<BPAuditLogDto> bpAuditLogDtos = new ArrayList<>();
        if(bpAuditLogs !=null){
            for(BPAuditLog entity : bpAuditLogs) {
            	bpAuditLogDtos.add(this.entityToDto(entity));
            }
        }
        return bpAuditLogDtos;
	}

}
