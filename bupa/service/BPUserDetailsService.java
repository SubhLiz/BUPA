package com.incture.bupa.service;

import com.incture.bupa.dto.BPUserActionDto;
import com.incture.bupa.dto.BPUserDetailsDto;
import com.incture.bupa.entities.BPUserAction;
import com.incture.bupa.entities.BPUserDetails;
import com.incture.bupa.repository.BPDetailsRepository;
import com.incture.bupa.repository.BPUserActionRepository;
import com.incture.bupa.repository.BPUserDetailsRepository;
import com.incture.bupa.utils.DestinationUtil;
import com.incture.bupa.utils.ObjectMapperUtils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;

import lombok.extern.slf4j.Slf4j;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BPUserDetailsService {

    @Autowired
    private BPDetailsRepository detailsRepository;
    @Autowired
    private BPUserDetailsRepository userDetailsRepository;
    @Autowired
    private BPUserActionRepository userActionRepository;
    @Autowired
	private DestinationUtil destinationUtil;
    
    @Value("${ias.userFilter}")
	private String userFilter;
    
    
    public void getUserList() {

        try {
//            Set<String> list = detailsRepository.getCreatedByList();
//            System.out.println("list of Emails : " + list );
//
//            //remove emailId if already exist
//            Set<String> existingList = userDetailsRepository.getAllUserEmail();
//            list=list.stream().filter(emailId -> !existingList.contains(emailId)).collect(Collectors.toSet());
//            System.out.println("List after filter: " + list);
            saveUserName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveUserName() {

        try {
        	Map<String,String> userMap = fetchUserDetails();
            List<BPUserDetails> list = new ArrayList<>();
            for (Map.Entry<String,String> emails : userMap.entrySet()) {
                BPUserDetails entity = new BPUserDetails();
                entity.setUserEmail(emails.getKey());
                entity.setUserName(emails.getValue());
                list.add(entity);
            }
//            userMap.forEach((email,name)->{
//            	BPUserDetails entity = new BPUserDetails();
//            	entity.setUserEmail(email);
//                entity.setUserName(name);
//                list.add(entity);
//            	});
            userDetailsRepository.saveAll(list);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<BPUserDetailsDto> getAllUserDetails() {
        List<BPUserDetailsDto> list = new ArrayList<>();
        List<Object[]> result = new ArrayList<>();
        BPUserDetails entity;
        BPUserDetailsDto userDetailsDto;
        try {
            result = userDetailsRepository.getAllUserDetails();
            for (Object[] obj : result) {
                entity = new BPUserDetails();
                String email = obj[0] == null ? "" : obj[0].toString();
                if (email != "") {
                    entity.setUserEmail((obj[0] == null ? "" : obj[0].toString()));
                    entity.setUserName((obj[1] == null ? "" : obj[1].toString()));
                    userDetailsDto = ObjectMapperUtils.map(entity, BPUserDetailsDto.class);
                    list.add(userDetailsDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

	public JSONObject btpUserDetails(int startIndex,int itemsPerPage) {
		JSONObject body = new JSONObject();
		Map<String,String> usersMap = new HashMap<>();
		try {

//			String destDetails = destinationUtil.readMdgDestination("viatris-its-apiaccess", null, null);
			String destDetails = destinationUtil.readMdgDestination("viatris-mdg-users", null, null);

			System.out.println("Destination details"+destDetails);
			JSONObject resObj = new JSONObject(destDetails);

			System.out.println("auth array"+resObj.optJSONArray("authTokens").get(0));
			JSONObject tokenObj = (JSONObject)resObj.optJSONArray("authTokens").get(0);
			String token = tokenObj.optJSONObject("http_header").optString("value");

			String url = resObj.optJSONObject("destinationConfiguration").optString("URL");

			String unencodedFilter =userFilter;
			
			String encodedFilter = URLEncoder.encode(unencodedFilter, StandardCharsets.UTF_8.toString());

			String btpUrl = url  + "?filter=" + encodedFilter+"&startIndex="+startIndex+"&count="+itemsPerPage;
			System.out.println("Complete btp user api: "+ btpUrl);

			HttpResponse<JsonNode> responseJson = Unirest.get(btpUrl).header("Authorization", token).asJson();
			System.out.println("API response: "+responseJson.getBody());

			body = new JSONObject(responseJson.getBody().toString());

		} catch (Exception e) {
			log.info("[UserDetailsServiceImpl][btpUserDetails] error: "+e.getMessage());
			e.printStackTrace();
		}
		return body;
	}

	public Map<String, String> fetchUserDetails() {
		Map<String,String> usersMap = new HashMap<>();
		try {
			int startIndex = 1;
	        int itemsPerPage = 100;
	        JSONObject body;
			do {
				body = btpUserDetails(startIndex,itemsPerPage);
				JSONArray resources = (JSONArray) body.getJSONArray("Resources");
				System.out.println("resources: " + resources);

				for (int i = 0; i < resources.length(); i++) {

					JSONObject jsonObj = resources.getJSONObject(i);
					System.out.println("body: " + jsonObj.toString());
					System.out.println("email" + jsonObj.optString("emails"));

					JSONArray emailsArray = jsonObj.getJSONArray("emails");
					String emailValue = "";
					if (emailsArray.length() > 0) {
						JSONObject emailObject = emailsArray.getJSONObject(0);
						emailValue = emailObject.optString("value");
						System.out.println("Email Value: " + emailValue);
					}

					JSONObject nameObj = jsonObj.getJSONObject("name");
					String fullName = nameObj.optString("givenName") + " " + nameObj.optString("familyName");
					usersMap.put(emailValue, fullName);
				}
				startIndex += itemsPerPage;
			} while (startIndex <= body.getInt("totalResults"));

		} catch (Exception e) {
			log.info("[UserDetailsServiceImpl][fetchUserDetails] error: "+e.getMessage());
			e.printStackTrace();
		}
		return usersMap;
	}

	public String getUserMail(String list) {
		List<String> userList = new ArrayList<String>(Arrays.asList(list.split("\\s*,\\s*")));
		
		List<String> btpUserList=new ArrayList<>();
		Set<String> updatedUserList = new HashSet<>();
		List<BPUserDetailsDto> getUserDetails=getAllUserDetails();
		getUserDetails.forEach((userDetails)->{
			btpUserList.add(userDetails.getUserEmail());
		});
		userList.forEach((user)->{
			btpUserList.forEach((userMail)->{
				if(userMail.toLowerCase().equalsIgnoreCase(user)) {
					updatedUserList.add(userMail);
				}
			});
		});
		return String.join(",", updatedUserList);
	}
	public String saveUserAction(BPUserActionDto bpUserActionDto) {
		userActionRepository.save(ObjectMapperUtils.map(bpUserActionDto, BPUserAction.class));
		return "User Action Saved successfully!!";
	}
}
