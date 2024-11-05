package com.incture.bupa.entities;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import com.incture.bupa.dto.BPAuditLogDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BP_AUDIT_LOG")
public class BPAuditLog {
	@Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "BP_AUDIT_LOG_ID",length = 40)
    private UUID auditLogInfoId;


    @Column(name = "BP_REQUEST_ID",length = 40)
    private String requestId;

    @Column(name = "BP_FIELD_NAME",length = 40)
    private String fieldName;
    
    @Column(name="BP_UI_FIELD_NAME",length=256)
    private String uiFieldName;

    @Column(name = "BP_OLD_VALUE",length = 500)
    private String oldValue;

    @Column(name = "BP_NEW_VALUE",length = 500)
    private String newValue;
    
    @Column(name = "BP_COMPANY_CODE",length = 500)
    private String companyCode;

    @Column(name = "BP_PURCHASING_ORG",length = 500)
    private String purchasingOrg;
    
    @Column(name = "BP_PLANT",length = 500)
    private String plant;
    
    @Column(name="BP_PATH",length =500)
    private String path;
    
    @Column(name="BP_FLAG",length=500)
    private String flag;
    
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_ON")
    private Date updatedOn;

    @Column(name = "UPDATED_BY",length = 100)
    private String updatedBy;
    
    @Column(name="BP_SERIAL_NO")
    private Integer serialNo;
}
