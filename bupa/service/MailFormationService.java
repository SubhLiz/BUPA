package com.incture.bupa.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.incture.bupa.utils.MailDto;
import com.incture.bupa.utils.MailRequestDto;
@Service
public class MailFormationService {

	public MailRequestDto setMailParameters(JsonNode ruleResponse, MailRequestDto mailRequestDto, MailDto mailDto) {
		// TODO Auto-generated method stub
		mailRequestDto.setBodyMessage("Hi There testing mail!!");
		mailRequestDto.setSubject("Hi!!");
		List<String> list=new ArrayList<>();
		list.add(ruleResponse.get("data").get("result").get(0).get("WORKFLOW_EMAIL").get(0).get("WORKFLOW_EMAIL_ID").asText().toLowerCase());
//		list.add("vaibhav.anand@incture.com");
		mailRequestDto.setTo(list);
//		mailRequestDto=formatMail(mailRequestDto);
		return mailRequestDto;
	}

	private MailRequestDto formatMail(MailRequestDto mailRequestDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
