package com.incture.bupa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CPIContactDto {

    @JsonProperty("ChangeIndObject")
    public String changeIndObject;
    @JsonProperty("Vendor")
    public String vendor;
    @JsonProperty("ContactPerson")
    public String contactPerson;
    @JsonProperty("Department")
    public String department;
    @JsonProperty("HighLevelPerson")
    public String highLevelPerson;
    @JsonProperty("Function")
    public String function;
    @JsonProperty("Authority")
    public String authority;
    @JsonProperty("VIP")
    public String vIP;
    @JsonProperty("Gender")
    public String gender;
    @JsonProperty("Representno")
    public String representno;
    @JsonProperty("Callfrequency")
    public String callfrequency;
    @JsonProperty("Buyinghabits")
    public String buyinghabits;
    @JsonProperty("Notes")
    public String notes;
    @JsonProperty("MaritalStat")
    public String maritalStat;
    @JsonProperty("Title")
    public String title;
    @JsonProperty("Firstname")
    public String firstname;
    @JsonProperty("Lastname")
    public String lastname;
    @JsonProperty("NameatBirth")
    public String nameatBirth;
    public String familynameSecond;
    @JsonProperty("Completename")
    public String completename;
    @JsonProperty("AcademicTitle")
    public String academicTitle;
    @JsonProperty("Acadtitlesecond")
    public String acadtitlesecond;
    @JsonProperty("Prefix")
    public String prefix;
    public String prefixSecond;
    @JsonProperty("NameSupplement")
    public String nameSupplement;
    @JsonProperty("Nickname")
    public String nickname;
    @JsonProperty("Formatname")
    public String formatname;
    @JsonProperty("Formatcountry")
    public String formatcountry;
    @JsonProperty("ToContactPhone")
    public List<CPIContactPhoneDto> toContactPhone;
    @JsonProperty("ToContactEmail")
    public List<CPIContactEmailDto> toContactEmail;

}
