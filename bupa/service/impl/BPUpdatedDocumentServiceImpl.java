package com.incture.bupa.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.incture.bupa.dto.BPDMSAttachmentsDto;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.entities.BPDMSAttachments;
import com.incture.bupa.service.BPUpdatedDocumentService;
import com.incture.bupa.utils.ApplicationConstants;
import com.incture.bupa.utils.DateUtil;

import io.pivotal.cfenv.core.CfCredentials;
import io.pivotal.cfenv.jdbc.CfJdbcEnv;

@Service
public class BPUpdatedDocumentServiceImpl implements BPUpdatedDocumentService{
	private static final Logger logger = LoggerFactory.getLogger(BPUpdatedDocumentServiceImpl.class);
    private String TOKEN_ENDPOINT;
    private String CLIENT_ID;
    private String CLIENT_SECRET;   
    private String BROWSER_URL=ApplicationConstants.DMS.BROWSER_URL;
    private String GRANT_TYPE=ApplicationConstants.DMS.GRANT_TYPE;
    private String SCOPE=ApplicationConstants.DMS.SCOPE;
    @Autowired    
    private ModelMapper modelMapper;
    @Override    
    public BPDMSAttachmentsDto entityToDto(BPDMSAttachments attachementEntity) {
    	BPDMSAttachmentsDto attachmentDto = this.modelMapper.map(attachementEntity, BPDMSAttachmentsDto.class);
    	attachmentDto.setUpdatedOn(DateUtil.dateToString(attachementEntity.getUpdatedOn()));
        return attachmentDto;
    }
    @Override    
	public BPDMSAttachments dtoToEntity(BPDMSAttachmentsDto attachmentDto) {
		modelMapper.typeMap(BPDMSAttachmentsDto.class, BPDMSAttachments.class).addMappings(mp -> {
			mp.skip(BPDMSAttachments::setUpdatedOn);
		});
		BPDMSAttachments attachmentEntity = this.modelMapper.map(attachmentDto, BPDMSAttachments.class);
		attachmentEntity.setUpdatedOn(DateUtil.stringToDate(attachmentDto.getUpdatedOn()));
		return attachmentEntity;
	}
    @SuppressWarnings({ "unchecked", "unused" })
    private String getMIMEtype(String extension) {
        @SuppressWarnings("rawtypes")
        Map map = new HashMap();
        map.put("3gp", "video/3gpp");
        map.put("3g2", "video/3gpp2");
        map.put("7z", "application/x-7z-compressed");
        map.put("aac", "audio/aac");
        map.put("abw", "application/x-abiword");
        map.put("arc", "application/x-freearc");
        map.put("avi", "video/x-msvideo");
        map.put("azw", "application/vnd.amazon.ebook");
        map.put("bin", "application/octet-stream");
        map.put("bmp", "image/bmp");
        map.put("bz", "application/x-bzip");
        map.put("bz2", "application/x-bzip2");
        map.put("csh", "application/x-csh");
        map.put("css ", "text/css");
        map.put("csv", "text/csv");
        map.put("doc", "application/msword");
        map.put("docx", "application/vnd.openxmlformats officedocument.wordprocessingml.document");
        map.put("eot", "application/vnd.ms-fontobject");
        map.put("epub", "application/epub+zip");
        map.put("gif", "image/gif");
        map.put("htm/.html", "text/html");
        map.put("ico", "image/vnd.microsoft.icon");
        map.put("ics", "text/calendar");
        map.put("jar", "application/java-archive");
        map.put("jpg/.jpeg", "image/jpeg");
        map.put("js", "text/javascript");
        map.put("json", "application/json");
        map.put("mid/.midi", "audio/midi, audio/x-midi");
        map.put("mjs", "text/javascript");
        map.put("mp3", "audio/mpeg");
        map.put("mpeg", "video/mpeg");
        map.put("mpkg", "application/vnd.apple.installer+xml");
        map.put("odp", "application/vnd.oasis.opendocument.presentation");
        map.put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        map.put("odt", "application/vnd.oasis.opendocument.text");
        map.put("oga", "audio/ogg");
        map.put("ogv", "video/ogg");
        map.put("ogx", "application/ogg");
        map.put("otf", "font/otf");
        map.put("png", "image/png");
        map.put("pdf", "application/pdf");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        map.put("rar", "application/x-rar-compressed");
        map.put("rtf", "application/rtf");
        map.put("sh", "application/x-sh");
        map.put("svg", "image/svg+xml");
        map.put("swf", "application/x-shockwave-flash");
        map.put("tar", "application/x-tar");
        map.put("tif", "image/tiff");
        map.put("ttf", "font/ttf");
        map.put("txt", "text/plain");
        map.put("vsd", "application/vnd.visio");
        map.put("wav", "audio/wav");
        map.put("weba", "audio/webm");
        map.put("webm", "video/webm");
        map.put("webp", "image/webm");
        map.put("woff", "font/woff");
        map.put("woff2", "font/woff2");
        map.put("xhtml", "application/xhtml+xml");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        map.put("xml", "application/xml");
        map.put("xul", "application/vnd.mozilla.xul+xml");
        map.put("zip", "application/zip");
        return map.get(extension).toString();
    }
    private String getAccessToken()
    {
        CfJdbcEnv cfJdbcEnv = new CfJdbcEnv();
        CfCredentials cfCredentials = cfJdbcEnv.findCredentialsByTag("sdm");
        Map<String, Object> map = cfCredentials.getMap();
        logger.info(" MAPPING :"+map);
        BROWSER_URL = (String) map.get("uri")+"browser";
        logger.info("uri:"+BROWSER_URL);
        LinkedHashMap<String, String> lhm = (LinkedHashMap<String, String>) map.get("uaa");
        TOKEN_ENDPOINT= lhm.get("url");
        logger.info("tokenUrl:"+TOKEN_ENDPOINT);
        CLIENT_ID = lhm.get("clientid");
        logger.info("clientId:"+CLIENT_ID);
        CLIENT_SECRET = lhm.get("clientsecret");
        logger.info("clientSecret:"+CLIENT_SECRET);
        /* HTTPCLIENT AND HTTPPOST OOBJECT */        
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT+"/oauth/token");
        /* AUTHENTICATION CREDENTIALS ENCODING */        
        String base64Credentials = Base64.getEncoder().encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        /* HEADER INFO */        
        httpPost.addHeader("Authorization", "Basic " + base64Credentials);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        // /* PROXY CONFIG */        
        // HttpHost target = new HttpHost("proxy", 8080, "http");        
        // RequestConfig config = RequestConfig.custom().setProxy(target).build();        
        // httpPost.setConfig(config);        
        /* OAUTH PARAMETERS ADDED TO BODY */        
        StringEntity input = null;
        try        
        {
            input = new StringEntity("grant_type=" + GRANT_TYPE);
            httpPost.setEntity(input);
        }
        catch(UnsupportedEncodingException e)
        {
            e.getMessage();
        }
        /* SEND AND RETRIEVE RESPONSE */        
        HttpResponse response = null;
        try        
        {
            response = httpClient.execute(httpPost);
        }
        catch(IOException e)
        {
            e.getMessage();
        }
        /* RESPONSE AS STRING */        
        String result = null;
        try        
        {
            result = IOUtils.toString(response.getEntity().getContent(), "UTF-8");
        }
        catch(IOException e)
        {
            e.getMessage();
        }
        JSONObject o = new JSONObject(result);
        return o.getString("access_token").toString();
    }
    @Override
    public ServiceResponse<String> uploadDoc(BPDMSAttachmentsDto attachment,String repositoryId,String folderName){
    	ServiceResponse<String> response = new ServiceResponse<>();
    	 int count = 0;
         int found = 0;
    	try {
    		System.out.println("started 1 ");
            String[] fileType = attachment.getDocumentType().split("/");
            String filePath = (attachment.getDocumentName().contains(".") ? attachment.getDocumentName().split("\\.")[0]
                    : attachment.getDocumentName()) + "." + fileType[1];
            System.out.println("started 2 ");
            byte[] data = Base64.getDecoder().decode(attachment.getEncodedFileContent());
            try(OutputStream stream = new FileOutputStream(filePath))
            {
                stream.write(data);
            }
            File file = new File(filePath);
            System.out.println("started 3 ");
            // default factory implementation
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameter = new HashMap<>();
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
            parameter.put(SessionParameter.BROWSER_URL, BROWSER_URL);
            parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,getAccessToken());
            //parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,"eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS0tMTIyMTc1MDM4MCIsInR5cCI6IkpXVCIsImppZCI6ICJ0N0hSWXJhUWY0SmFobURNL2didkpXRy94ZWd3ZlFlRVkvVVdJVHUwQk9VPSJ9.eyJqdGkiOiIwYjg0ZWRlN2I0OGE0ZjMzYmZmODY1ODRkNDcwZjE4NSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJ6ZG4iOiJ2aWF0cmlzLW1kZy1kZXYiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6ImE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiJ9LCJzdWIiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhdXRob3JpdGllcyI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJzY29wZSI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJjbGllbnRfaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJjaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhenAiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6IjEyOWI5NjFhIiwiaWF0IjoxNzEzOTQzMjAzLCJleHAiOjE3MTM5ODY0MDMsImlzcyI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vb2F1dGgvdG9rZW4iLCJ6aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJhdWQiOlsic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSIsInVhYSIsInNiLWE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiFiMTEyOTl8c2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSJdfQ.UD-fJm2gsxxz6jKLnq6pFg33CJBfzZuQBi2oolpyhwihxa-sVPmQrRmVs7HoGwDlGVtNnuY3CxOJOScUBliT37GGJYJWNUD8MFF_iZvmuF8yd0FXAGfuGshd1WW_0oBCckeIfzDCSi3FnbeRmzC15JzHDzhHcWIWgWRjwLuUG9HP_F8cvSTToVAsGXzZ578u16xnwp_HUktF0Zl5Ef1yBGP-nUKm9FSBRcvvPQwy40AG1-wdpgC0yoN3ugmhFK-rZZmob0lnAY6Y9BChisSkvzEaUuMevc9NXM2CVr85u1cu4mkz80qQ1ANjX6R-oP1m8SZcvnJTwunBe_yENLic6A");
            parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
            parameter.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
            parameter.put(SessionParameter.AUTH_OAUTH_BEARER, "true");
            parameter.put(SessionParameter.USER_AGENT,
                    "OpenCMIS-Workbench/1.1.0 Apache-Chemistry-OpenCMIS/1.1.0 (Java 1.8.0_271; Windows 10 10.0)");
            parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
            Session session = factory.createSession(parameter);
            Folder parent = getOrCreateFolder(session.getRootFolder(), folderName);
    	}
    	catch(Exception e) {
    		
    	}
    	return null;
    }
    private Folder getOrCreateFolder(Folder parentFolder, String folderName) {
        for (CmisObject child : parentFolder.getChildren()) {
            if (child.getName().equals(folderName)) {
                return (Folder) child;
            }
        }

        // Create the folder if it doesn't exist
        Map<String, Object> properties = new HashMap<>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        properties.put(PropertyIds.NAME, folderName);

        return parentFolder.createFolder(properties);
    }
    
    @Override   
    public ServiceResponse<String> uploadDocument(BPDMSAttachmentsDto attachment, String repositoryId, String folderName,Long millis)
            throws Exception {
        ServiceResponse<String> response = new ServiceResponse<>();
        int count = 0;
        int found = 0;
        try        
        {   
        	System.out.println("started 1 ");
            String[] fileType = attachment.getDocumentType().split("/");
            String filePath = (attachment.getDocumentName().contains(".") ? attachment.getDocumentName().split("\\.")[0]
                    : attachment.getDocumentName()) + "." + fileType[1];
            System.out.println("started 2 ");
            byte[] data = Base64.getDecoder().decode(attachment.getEncodedFileContent());
            try(OutputStream stream = new FileOutputStream(filePath))
            {
                stream.write(data);
            }
            File file = new File(filePath);
            System.out.println("started 3 ");
            // default factory implementation
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameter = new HashMap<>();
            // connection settings            
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
            parameter.put(SessionParameter.BROWSER_URL, BROWSER_URL);
            parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,getAccessToken());
            //parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,"eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS0tMTIyMTc1MDM4MCIsInR5cCI6IkpXVCIsImppZCI6ICJ0N0hSWXJhUWY0SmFobURNL2didkpXRy94ZWd3ZlFlRVkvVVdJVHUwQk9VPSJ9.eyJqdGkiOiIwYjg0ZWRlN2I0OGE0ZjMzYmZmODY1ODRkNDcwZjE4NSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJ6ZG4iOiJ2aWF0cmlzLW1kZy1kZXYiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6ImE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiJ9LCJzdWIiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhdXRob3JpdGllcyI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJzY29wZSI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJjbGllbnRfaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJjaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhenAiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6IjEyOWI5NjFhIiwiaWF0IjoxNzEzOTQzMjAzLCJleHAiOjE3MTM5ODY0MDMsImlzcyI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vb2F1dGgvdG9rZW4iLCJ6aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJhdWQiOlsic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSIsInVhYSIsInNiLWE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiFiMTEyOTl8c2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSJdfQ.UD-fJm2gsxxz6jKLnq6pFg33CJBfzZuQBi2oolpyhwihxa-sVPmQrRmVs7HoGwDlGVtNnuY3CxOJOScUBliT37GGJYJWNUD8MFF_iZvmuF8yd0FXAGfuGshd1WW_0oBCckeIfzDCSi3FnbeRmzC15JzHDzhHcWIWgWRjwLuUG9HP_F8cvSTToVAsGXzZ578u16xnwp_HUktF0Zl5Ef1yBGP-nUKm9FSBRcvvPQwy40AG1-wdpgC0yoN3ugmhFK-rZZmob0lnAY6Y9BChisSkvzEaUuMevc9NXM2CVr85u1cu4mkz80qQ1ANjX6R-oP1m8SZcvnJTwunBe_yENLic6A");
            parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
            parameter.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
            parameter.put(SessionParameter.AUTH_OAUTH_BEARER, "true");
            parameter.put(SessionParameter.USER_AGENT,
                    "OpenCMIS-Workbench/1.1.0 Apache-Chemistry-OpenCMIS/1.1.0 (Java 1.8.0_271; Windows 10 10.0)");
//            List<Repository> repositories = factory.getRepositories(parameter);
            parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
            System.out.println("started 4 ");
//            for(Repository r : repositories)
//            {
//                System.out.println(count + " \t " + r.getId());
//                if(r.getId().equals(repositoryId))
//                {
                    System.out.println("started 5 ");
                    found = 1;
                    Session session = factory.getRepositories(parameter).get(count).createSession();
                    Folder root = session.getRootFolder();
                    // get Selected Folder                    
                    Folder parent = null;
                    String folderId = null;
                    ItemIterable<CmisObject> child = root.getChildren();
                    for(CmisObject O : child)
                    {
                        System.out.println("started 6 ");
                        if(O.getName().equalsIgnoreCase(folderName))
                        {
                            System.out.println("started 7 ");
                            folderId = O.getId();
                            parent = (Folder) O;
                        }
                    }
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    System.out.println("started 8 ");
                    Map<String, Object> properties2 = new HashMap<>();
                    properties2.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                    System.out.println("***");
                    System.out.println(timestamp.getTime());
                    System.out.println("***");
                    properties2.put(PropertyIds.NAME, attachment.getDocumentName()+"_"+timestamp.getTime());
                    // Creating a folder if it's not present                    
                    if(parent == null)
                    {
                        System.out.println("started 9 ");
                        Map<String, Object> properties = new HashMap<>();
                        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
                        properties.put(PropertyIds.NAME, folderName); // folder name                        
                        parent = root.createFolder(properties);
                    }
//                    else                    
//                    {
//                        System.out.println("started 10 ");
//                        count = 0;
//                        CmisObject o = session.getObject(folderId);
//                        parent = (Folder) o;
//                        ItemIterable<CmisObject> documents = parent.getChildren();
//                        for(CmisObject O : documents)
//                        {
////                          CmisObject document = session.getObject(O.getId());                            
//                        	System.out.println("started 11 ");
//                            Document doc = (Document) session.getObject(O.getId());
////                          ContentStream contentStream = doc.getContentStream(); // returns null if the document has no content                            
//                            if(doc.getName().equals(attachment.getDocumentName()))
//                            {
//                                System.out.println("Deleting document " + doc.getName());
//                                doc.delete(true);
//                            }
//                        }
//                    }
                    System.out.println("[SIG]: DocumentServiceImpl.uploadDocument(): File name : " + attachment.getDocumentName());
                    InputStream targetStream = new FileInputStream(file);
                    ContentStream contentStream = new ContentStreamImpl(file.getName(), BigInteger.valueOf(file.length()), attachment.getDocumentType(),
                            targetStream);
                    // create a major version                    
                    Document newDoc = parent.createDocument(properties2, contentStream, VersioningState.MAJOR);
                    System.err.println("[SIG]: DocumentServiceImpl.uploadDocument(): ObjectID returned by DMS : " + newDoc.getId());
                    response.setData(newDoc.getId());
                    response.setMessage(ApplicationConstants.SUCCESS);
                    response.setStatus(HttpStatus.OK.getReasonPhrase());
                    response.setErrorCode(HttpStatus.OK.value());
//                }
                count = count + 1;
        }
        catch(Exception e)
        {
            System.err.println("[Error]: " + e.getMessage());
            StackTraceElement[] stktrace = e.getStackTrace();
            for(int i = 0; i < stktrace.length; i++)
            {
                System.err.println("[Error]: Line " + i + " of error: " + stktrace[i].toString());
            }
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            response.setMessage(e.getMessage());
            response.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return response;
        }
        if(found == 0)
        {
            response.setMessage("Repository Doesn't Exist!");
            response.setStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
            response.setErrorCode(HttpStatus.NOT_FOUND.value());
        }
        logger.info("Response: "+response);
        
        return response;
    }
    @Override    
    public BPDMSAttachmentsDto getDocument(String repositoryId, String folderName, String documentId) throws Exception {
    	BPDMSAttachmentsDto dto = new BPDMSAttachmentsDto();
        SessionFactory factory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<>();
        // connection settings        
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
        parameter.put(SessionParameter.BROWSER_URL, "https://api-sdm-di.cfapps.us21.hana.ondemand.com/browser");
        parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,getAccessToken());
       // parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,"eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS0tMTIyMTc1MDM4MCIsInR5cCI6IkpXVCIsImppZCI6ICJ0N0hSWXJhUWY0SmFobURNL2didkpXRy94ZWd3ZlFlRVkvVVdJVHUwQk9VPSJ9.eyJqdGkiOiIwYjg0ZWRlN2I0OGE0ZjMzYmZmODY1ODRkNDcwZjE4NSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJ6ZG4iOiJ2aWF0cmlzLW1kZy1kZXYiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6ImE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiJ9LCJzdWIiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhdXRob3JpdGllcyI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJzY29wZSI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJjbGllbnRfaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJjaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhenAiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6IjEyOWI5NjFhIiwiaWF0IjoxNzEzOTQzMjAzLCJleHAiOjE3MTM5ODY0MDMsImlzcyI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vb2F1dGgvdG9rZW4iLCJ6aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJhdWQiOlsic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSIsInVhYSIsInNiLWE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiFiMTEyOTl8c2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSJdfQ.UD-fJm2gsxxz6jKLnq6pFg33CJBfzZuQBi2oolpyhwihxa-sVPmQrRmVs7HoGwDlGVtNnuY3CxOJOScUBliT37GGJYJWNUD8MFF_iZvmuF8yd0FXAGfuGshd1WW_0oBCckeIfzDCSi3FnbeRmzC15JzHDzhHcWIWgWRjwLuUG9HP_F8cvSTToVAsGXzZ578u16xnwp_HUktF0Zl5Ef1yBGP-nUKm9FSBRcvvPQwy40AG1-wdpgC0yoN3ugmhFK-rZZmob0lnAY6Y9BChisSkvzEaUuMevc9NXM2CVr85u1cu4mkz80qQ1ANjX6R-oP1m8SZcvnJTwunBe_yENLic6A");
        parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
        parameter.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
        parameter.put(SessionParameter.AUTH_OAUTH_BEARER, "true");
        parameter.put(SessionParameter.USER_AGENT,
                "OpenCMIS-Workbench/1.1.0 Apache-Chemistry-OpenCMIS/1.1.0 (Java 1.8.0_271; Windows 10 10.0)");
        parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
        List<Repository> repositories = factory.getRepositories(parameter);
        int count = 0;
//        for(Repository repo : repositories)
//        {
            Session session = factory.getRepositories(parameter).get(count).createSession();
//            Folder root = session.getRootFolder();
//            Folder parent = null;
//            ItemIterable<CmisObject> child = root.getChildren();
//            if(repo.getId().equals(repositoryId))
//            {
//                for(CmisObject ob : child)
//                {
//                    if(ob.getName().equals(folderName))
//                    {
//                        parent = (Folder) ob;
//                        ItemIterable<CmisObject> documents = parent.getChildren();
                        try                        {
//                            for(CmisObject obj : documents)
//                            {
//                                System.out.println(obj.getId());
//                                if(obj.getId().equals(documentId))
//                                {
                        	       
                                	CmisObject document = session.getObject(documentId);
//                                    System.out.println(document);
                                    Document doc = (Document) session.getObject(document.getId());
                                    System.out.println(doc.getId());
                                    dto.setDocumentId(doc.getId());
                                    dto.setDocumentName(doc.getName());
//                                    CmisObject document = session.getObject(obj.getId());
//                                    System.out.println(document);
//                                    Document doc = (Document) session.getObject(document.getId());
                                    // Returns null if the document has no content                                    
                                    ContentStream contentStream = doc.getContentStream();
                                    if(contentStream != null)
                                    {
                                        InputStream is = contentStream.getStream();
                                        byte[] bytes = IOUtils.toByteArray(is);
                                        String encoded = Base64.getEncoder().encodeToString(bytes);
                                        dto.setEncodedFileContent(encoded);
                                        System.out.println("Contents of " + doc.getName() + " are: " + encoded);
                                    }
                                    else                                    {
                                        System.out.println("No content.");
                                    }
                                    return dto;
//                                }
//                            }
                        }
                        catch(Exception e)
                        {
                            System.out.println(e.getMessage());
                            e.getStackTrace();
                        }
//                    }
//                }
//            }
            count = count + 1;
//        }
        return dto;
    }
    @Override    
    public boolean deleteDocument(String repositoryId, String folderName, String documentId) {
        try        
        {
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Map<String, String> parameter = new HashMap<>();
            // connection settings            
            parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
            parameter.put(SessionParameter.BROWSER_URL, BROWSER_URL);
             parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,getAccessToken());
     //       parameter.put(SessionParameter.OAUTH_ACCESS_TOKEN,"eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vdG9rZW5fa2V5cyIsImtpZCI6ImRlZmF1bHQtand0LWtleS0tMTIyMTc1MDM4MCIsInR5cCI6IkpXVCIsImppZCI6ICJ0N0hSWXJhUWY0SmFobURNL2didkpXRy94ZWd3ZlFlRVkvVVdJVHUwQk9VPSJ9.eyJqdGkiOiIwYjg0ZWRlN2I0OGE0ZjMzYmZmODY1ODRkNDcwZjE4NSIsImV4dF9hdHRyIjp7ImVuaGFuY2VyIjoiWFNVQUEiLCJzdWJhY2NvdW50aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJ6ZG4iOiJ2aWF0cmlzLW1kZy1kZXYiLCJzZXJ2aWNlaW5zdGFuY2VpZCI6ImE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiJ9LCJzdWIiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhdXRob3JpdGllcyI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJzY29wZSI6WyJzZG0tZGktRG9jdW1lbnRNYW5hZ2VtZW50LXNkbV9pbnRlZ3JhdGlvbiFiMTcxLnNkbWFkbWluIiwic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MS5zZG1taWdyYXRpb25hZG1pbiIsInVhYS5yZXNvdXJjZSIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtYnVzaW5lc3NhZG1pbiIsInNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEuc2RtdXNlciJdLCJjbGllbnRfaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJjaWQiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJhenAiOiJzYi1hNmQ1ZmMzMS01Mzc2LTQyYTktYTZlZi0wNjE1YTBkOTU0ZTYhYjExMjk5fHNkbS1kaS1Eb2N1bWVudE1hbmFnZW1lbnQtc2RtX2ludGVncmF0aW9uIWIxNzEiLCJncmFudF90eXBlIjoiY2xpZW50X2NyZWRlbnRpYWxzIiwicmV2X3NpZyI6IjEyOWI5NjFhIiwiaWF0IjoxNzEzOTQzMjAzLCJleHAiOjE3MTM5ODY0MDMsImlzcyI6Imh0dHBzOi8vdmlhdHJpcy1tZGctZGV2LmF1dGhlbnRpY2F0aW9uLnVzMjEuaGFuYS5vbmRlbWFuZC5jb20vb2F1dGgvdG9rZW4iLCJ6aWQiOiIwMGIxZTMzNy1mZjUxLTRiN2ItYWU0OC05Y2M2NGQ4ZWNjOTMiLCJhdWQiOlsic2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSIsInVhYSIsInNiLWE2ZDVmYzMxLTUzNzYtNDJhOS1hNmVmLTA2MTVhMGQ5NTRlNiFiMTEyOTl8c2RtLWRpLURvY3VtZW50TWFuYWdlbWVudC1zZG1faW50ZWdyYXRpb24hYjE3MSJdfQ.UD-fJm2gsxxz6jKLnq6pFg33CJBfzZuQBi2oolpyhwihxa-sVPmQrRmVs7HoGwDlGVtNnuY3CxOJOScUBliT37GGJYJWNUD8MFF_iZvmuF8yd0FXAGfuGshd1WW_0oBCckeIfzDCSi3FnbeRmzC15JzHDzhHcWIWgWRjwLuUG9HP_F8cvSTToVAsGXzZ578u16xnwp_HUktF0Zl5Ef1yBGP-nUKm9FSBRcvvPQwy40AG1-wdpgC0yoN3ugmhFK-rZZmob0lnAY6Y9BChisSkvzEaUuMevc9NXM2CVr85u1cu4mkz80qQ1ANjX6R-oP1m8SZcvnJTwunBe_yENLic6A");
            parameter.put(SessionParameter.AUTH_HTTP_BASIC, "false");
            parameter.put(SessionParameter.AUTH_SOAP_USERNAMETOKEN, "false");
            parameter.put(SessionParameter.AUTH_OAUTH_BEARER, "true");
            parameter.put(SessionParameter.USER_AGENT,
                    "OpenCMIS-Workbench/1.1.0 Apache-Chemistry-OpenCMIS/1.1.0 (Java 1.8.0_271; Windows 10 10.0)");
            List<Repository> repositories = factory.getRepositories(parameter);
            int count = 0;
            for(Repository repo : repositories)
            {
                Session session = factory.getRepositories(parameter).get(count).createSession();
                Folder root = session.getRootFolder();
                Folder parent = null;
                ItemIterable<CmisObject> child = root.getChildren();
                if(repo.getId().equals(repositoryId))
                {
                    for(CmisObject O : child)
                    {
                        if(O.getName().equals(folderName))
                        {
                            parent = (Folder) O;
                            ItemIterable<CmisObject> documents = parent.getChildren();
                            System.out.println(documents);
                            try                            {
                                System.out.println(documents);
                                for(CmisObject obj : documents)
                                {
                                    if(obj.getId().equals(documentId))
                                    {
                                        CmisObject document = session.getObject(obj.getId());
                                        System.out.println(document);
                                        Document doc = (Document) session.getObject(document.getId());
                                        // returns null if the document has no content                                        
                                        ContentStream contentStream = doc.getContentStream();
                                        if(contentStream != null)
                                        {
                                            String content = getContentAsString(contentStream);
                                            System.out.println("Contents of " + obj.getName() + " are: " + content);
                                        }
                                        else                                            System.out.println("No content.");
                                        System.out.println("Deleting document: " + doc.getName());
                                        doc.delete(true);
                                        return true;
                                    }
                                    else{
                                        return false;
                                    }
                                }
                            }
                            catch(Exception e)
                            {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    @SuppressWarnings("unused")
    private static String getFileExtension(String fullName)
    {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    private static String getContentAsString(ContentStream stream) throws IOException    {
        StringBuilder sb = new StringBuilder();
        Reader reader = new InputStreamReader(stream.getStream(), "UTF-8");
        try        
        {
            final char[] buffer = new char[4 * 1024];
            int b;
            while(true)
            {
                b = reader.read(buffer, 0, buffer.length);
                if(b > 0)
                {
                    sb.append(buffer, 0, b);
                }
                else if(b == -1)
                {
                    break;
                }
            }
        }
        finally        {
            reader.close();
        }
        return sb.toString();
    }
}
