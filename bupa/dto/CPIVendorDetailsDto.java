package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIVendorDetailsDto {

    @JsonProperty("Type")
    public String type;
    @JsonProperty("RequestId")
    public String requestId;
    @JsonProperty("Validation")
    public String validation;
    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("SystemId")
    public String systemId;
    @JsonProperty("Trainstation")
    public String trainstation;
    @JsonProperty("Locationno1")
    public String locationno1;
    @JsonProperty("Locationno2")
    public String locationno2;
    @JsonProperty("Authorization")
    public String authorization;
    @JsonProperty("Industry")
    public String industry;
    @JsonProperty("Checkdigit")
    public String checkdigit;
    @JsonProperty("DMEIndicator")
    public String dMEIndicator;
    @JsonProperty("Instructionkey")
    public String instructionkey;
    @JsonProperty("ISRNumber")
    public String iSRNumber;
    @JsonProperty("CorporateGroup")
    public String corporateGroup;
    @JsonProperty("Accountgroup")
    public String accountgroup;
    @JsonProperty("Customer")
    public String customer;
    @JsonProperty("Alternatpayee")
    public String alternatpayee;
    @JsonProperty("Deletionflag")
    public boolean deletionflag;
    @JsonProperty("PostingBlock")
    public boolean postingBlock;
    @JsonProperty("Purchblock")
    public boolean purchblock;
    @JsonProperty("TaxNumber1")
    public String taxNumber1;
    @JsonProperty("TaxNumber2")
    public String taxNumber2;
    @JsonProperty("Equalizatntax")
    public String equalizatntax;
    @JsonProperty("LiableforVAT")
    public boolean liableforVAT;
    @JsonProperty("Payeeindoc")
    public boolean payeeindoc;
    @JsonProperty("TradingPartner")
    public String tradingPartner;
    @JsonProperty("Fiscaladdress")
    public String fiscaladdress;
    @JsonProperty("VATRegNo")
    public String vATRegNo;
    @JsonProperty("Naturalperson")
    public String naturalperson;
    @JsonProperty("Blockfunction")
    public String blockfunction;
    @JsonProperty("Address")
    public String address;
    @JsonProperty("Placeofbirth")
    public String placeofbirth;
    @JsonProperty("Birthdate")
    public String birthdate;
    @JsonProperty("Sex")
    public String sex;
    @JsonProperty("Credinfono")
    public String credinfono;
    @JsonProperty("Lastextreview")
    public Object lastextreview;
    @JsonProperty("ActualQMsys")
    public String actualQMsys;
    @JsonProperty("Refacctgroup")
    public String refacctgroup;
    @JsonProperty("Plant")
    public String plant;
    @JsonProperty("VSRrelevant")
    public boolean vSRrelevant;
    @JsonProperty("Plantrelevant")
    public boolean plantrelevant;
    @JsonProperty("Factorycalend")
    public String factorycalend;
    @JsonProperty("SCAC")
    public String sCAC;
    @JsonProperty("Carfreightgrp")
    public String carfreightgrp;
    @JsonProperty("ServAgntProcGrp")
    public String servAgntProcGrp;
    @JsonProperty("Taxtype")
    public String taxtype;
    @JsonProperty("Taxnumbertype")
    public String taxnumbertype;
    @JsonProperty("SocialIns")
    public boolean socialIns;
    @JsonProperty("SocInsCode")
    public String socInsCode;
    @JsonProperty("TaxNumber3")
    public String taxNumber3;
    @JsonProperty("TaxNumber4")
    public String taxNumber4;
    @JsonProperty("Taxsplit")
    public boolean taxsplit;
    @JsonProperty("Taxbase")
    public String taxbase;
    @JsonProperty("Profession")
    public String profession;
    @JsonProperty("Statgrpagent")
    public String statgrpagent;
    @JsonProperty("Externalmanuf")
    public String externalmanuf;
    @JsonProperty("Deletionblock")
    public boolean deletionblock;
    @JsonProperty("RepsName")
    public String repsName;
    @JsonProperty("TypeofBusiness")
    public String typeofBusiness;
    @JsonProperty("TypeofIndustry")
    public String typeofIndustry;
    @JsonProperty("QMsystemto")
    public Object qMsystemto;
    @JsonProperty("PODrelevant")
    public String pODrelevant;
    @JsonProperty("Taxoffice")
    public String taxoffice;
    @JsonProperty("TaxNumber")
    public String taxNumber;
    @JsonProperty("TaxNumber5")
    public String taxNumber5;
    @JsonProperty("PurposeCompleteFlag")
    public String purposeCompleteFlag;
    @JsonProperty("AddressVersion")
    public String addressVersion;
    @JsonProperty("From")
    public String from;
    @JsonProperty("To")
    public String to;
    @JsonProperty("Title")
    public String title;
    @JsonProperty("Name")
    public String name;
    @JsonProperty("Name2")
    public String name2;
    @JsonProperty("Name3")
    public String name3;
    @JsonProperty("Name4")
    public String name4;
    @JsonProperty("Convname")
    public String convname;
    public String co;
    @JsonProperty("City")
    public String city;
    @JsonProperty("District")
    public String district;
    @JsonProperty("CityNo")
    public String cityNo;
    @JsonProperty("DistrictNo")
    public String districtNo;
    @JsonProperty("CheckStatus")
    public String checkStatus;
    @JsonProperty("RegStrGrp")
    public String regStrGrp;
    @JsonProperty("PostalCode")
    public String postalCode;
    @JsonProperty("POBoxPostCde")
    public String pOBoxPostCde;
    @JsonProperty("CompanyPostCd")
    public String companyPostCd;
    @JsonProperty("PostalCodeExt")
    public String postalCodeExt;
    @JsonProperty("PostalCodeExt2")
    public String postalCodeExt2;
    @JsonProperty("PostalCodeExt3")
    public String postalCodeExt3;
    @JsonProperty("POBox")
    public String pOBox;
    @JsonProperty("POBoxwono")
    public boolean pOBoxwono;
    @JsonProperty("POBoxCity")
    public String pOBoxCity;
    @JsonProperty("POCitNo")
    public String pOCitNo;
    @JsonProperty("PORegion")
    public String pORegion;
    @JsonProperty("POboxcountry")
    public String pOboxcountry;
    @JsonProperty("ISOcode")
    public String iSOcode;
    @JsonProperty("DeliveryDist")
    public String deliveryDist;
    @JsonProperty("Transportzone")
    public String transportzone;
    @JsonProperty("Street")
    public String street;
    @JsonProperty("StreetCode")
    public String streetCode;
    @JsonProperty("StreetAbbrev")
    public String streetAbbrev;
    @JsonProperty("HouseNumber")
    public String houseNumber;
    @JsonProperty("Supplement")
    public String supplement;
    @JsonProperty("NumberRange")
    public String numberRange;
    @JsonProperty("Street2")
    public String street2;
    @JsonProperty("Street3")
    public String street3;
    @JsonProperty("Street4")
    public String street4;
    @JsonProperty("Street5")
    public String street5;
    @JsonProperty("BuildingCode")
    public String buildingCode;
    @JsonProperty("Floor")
    public String floor;
    @JsonProperty("RoomNumber")
    public String roomNumber;
    @JsonProperty("Country")
    public String country;
    @JsonProperty("CountryISO")
    public String countryISO;
    @JsonProperty("Language")
    public String language;
    @JsonProperty("LangISO")
    public String langISO;
    @JsonProperty("Region")
    public String region;
    @JsonProperty("SearchTerm1")
    public String searchTerm1;
    @JsonProperty("SearchTerm2")
    public String searchTerm2;
    @JsonProperty("Dataline")
    public String dataline;
    @JsonProperty("Telebox")
    public String telebox;
    @JsonProperty("Timezone")
    public String timezone;
    @JsonProperty("TaxJurisdictn")
    public String taxJurisdictn;
    @JsonProperty("AddressID")
    public String addressID;
    @JsonProperty("Creationlang")
    public String creationlang;
    @JsonProperty("LangCRISO")
    public String langCRISO;
    @JsonProperty("CommMethod")
    public String commMethod;
    @JsonProperty("Addressgroup")
    public String addressgroup;
    @JsonProperty("DifferentCity")
    public String differentCity;
    @JsonProperty("CityCode")
    public String cityCode;
    @JsonProperty("Undeliverable")
    public String undeliverable;
    @JsonProperty("Undeliverable1")
    public String undeliverable1;
    @JsonProperty("POBoxLobby")
    public String pOBoxLobby;
    @JsonProperty("DelvryServType")
    public String delvryServType;
    @JsonProperty("DeliveryServiceNo")
    public String deliveryServiceNo;
    @JsonProperty("Countycode")
    public String countycode;
    @JsonProperty("County")
    public String county;
    @JsonProperty("Townshipcode")
    public String townshipcode;
    @JsonProperty("Township")
    public String township;
    @JsonProperty("PAN")
    public String pAN;
    @JsonProperty("ToContact")
    public List<CPIContactDto> toContact;
    @JsonProperty("ToClassification")
    public List<CPIClassificationDto>toClassification;
    @JsonProperty("ToAddressData")
    public ArrayList<Object> toAddressData;
    @JsonProperty("ToCompanyData")
    public List<CPICompanyDataDto> toCompanyData;
    @JsonProperty("ToEmail")
    public List<CPIEmailDto> toEmail;
    @JsonProperty("ToPhone")
    public List<CPIPhoneDto> toPhone;
//    @JsonProperty("ToReturnMessages")
//    public ArrayList<Object> toReturnMessages;
    @JsonProperty("ToPurchaseOrgData")
    public List<CPIPurchaseOrgDataDto> toPurchaseOrgData;
//    @JsonProperty("ToTaxData")
//    public ArrayList<Object> toTaxData;
    @JsonProperty("ToBank")
    public List<CPIBankDto> toBank;
    @JsonProperty("ToFax")
    public List<CPIFaxDto> toFax;
}
