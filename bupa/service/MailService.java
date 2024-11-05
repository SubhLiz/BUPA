package com.incture.bupa.service;

import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.incture.bupa.constants.AppConstants;
import com.incture.bupa.dto.ServiceResponse;
import com.incture.bupa.utils.MailRequestDto;

@Service
public class MailService {
	private final JavaMailSender mailSender;
	public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

	public ServiceResponse sendMail(MailRequestDto mailRequestDto) {
		ServiceResponse response=new ServiceResponse<>();
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.office365.com");
		prop.put("mail.smtp.port", 587);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.socketFactory.port", 587);
		prop.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("vaibhav.anand@incture.com", "Bbsr@2022");
			}
		});
		String newEmailTo = "";
		List<String> newlist = mailRequestDto.getTo();
		for (int i = 0; i < newlist.size(); i++) {
			newEmailTo += newlist.get(i);
			if (i != newlist.size() - 1)
				newEmailTo += ",";
		}
		if (newEmailTo.isEmpty())
			newEmailTo += "vaibhav.anand@incture.com";
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("vaibhav.anand@incture.com", "no_reply_demand"));
			if (newEmailTo.contains(",")) {
				String[] strArray = new String[] { newEmailTo };
				System.out.println(strArray.toString());
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(newEmailTo));
				message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("vaibhav.anand@incture.com"));
				message.setSubject(mailRequestDto.getSubject());
				BodyPart messageBodyPart = new MimeBodyPart();
				Multipart multipart = new MimeMultipart();
				messageBodyPart.setContent(mailRequestDto.getBodyMessage(), "text/html");
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
				Transport.send(message);
			} else {
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(newEmailTo));
				message.setSubject(mailRequestDto.getSubject());
				BodyPart messageBodyPart = new MimeBodyPart();
				Multipart multipart = new MimeMultipart();
				messageBodyPart.setContent(mailRequestDto.getBodyMessage(), "text/html");
				multipart.addBodyPart(messageBodyPart);
				message.setContent(multipart);
				Transport.send(message);
			}
		} catch (Exception e) {
			System.err.println("Error in sendMailToUser() of EmailSenderServiceImpl" + e);
		}
		response.setMessage("Send Mail Success!!");
        response.setStatus(AppConstants.SUCCESS);
        response.setError(null);
        return response;
	}
	
}
