package com.incture.bupa.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.incture.bupa.dto.ServiceResponse;

@Component
public class MailSenderUtil {

	private static final Logger logger = LoggerFactory.getLogger(MailSenderUtil.class);


	public ServiceResponse sendMailWithAttachment(MailRequestDto mailRequestDto) {
		ServiceResponse responseMessage = null;

		try {
			final String FROM_MAIL_ID = "vaibhav.anand@incture.com";
			final String FROM_MAIL_ID_PASSWORD = "Bbsr@2022";

			Session session = Session.getInstance(getOutlookProperties(), new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(FROM_MAIL_ID, FROM_MAIL_ID_PASSWORD);
				}
			});

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(FROM_MAIL_ID, false));
			msg.addRecipients(RecipientType.TO, getTo(mailRequestDto));
			if (mailRequestDto.getCc() != null && !mailRequestDto.getCc().isEmpty())
				msg.addRecipients(RecipientType.CC, getCC(mailRequestDto));
			msg.setSubject(mailRequestDto.getSubject());

			Multipart multipart = new MimeMultipart();
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			BodyPart bodyPartMessage = new MimeBodyPart();

			bodyPartMessage.setContent(mailRequestDto.getBodyMessage(), "text/html");
			multipart.addBodyPart(bodyPartMessage);

			msg.setContent(multipart);
			logger.info("Contect Set");
			msg.saveChanges();
			logger.info("Save done");

			Transport.send(msg);
			responseMessage = new ServiceResponse();
			responseMessage.setMessage("Mail Sent Successfully");
			responseMessage.setStatus(ApplicationConstants.SUCCESS);
			responseMessage.setErrorCode(200);
		} catch (AddressException e) {
			responseMessage = new ServiceResponse();
			responseMessage.setMessage("Sending mail failed");
			responseMessage.setStatus(ApplicationConstants.FAILURE);
			responseMessage.setErrorCode(500);
			logger.error("[VM][MailSenderUtil][sendmailWithAttachment][AddressException] =" + e.getMessage());
			e.getMessage();
		} catch (MessagingException e) {
			responseMessage = new ServiceResponse();
			responseMessage.setMessage("Sending mail failed");
			responseMessage.setStatus(ApplicationConstants.FAILURE);
			responseMessage.setErrorCode(500);
			logger.error("[VM][MailSenderUtil][sendmailWithAttachment][MessagingException] =" + e.getMessage());
			e.getMessage();
		} catch (Exception e) {
			responseMessage = new ServiceResponse();
			responseMessage.setMessage("Sending mail failed");
			responseMessage.setStatus(ApplicationConstants.FAILURE);
			responseMessage.setErrorCode(500);
			logger.error("[VM][MailSenderUtil][sendmailWithAttachment][Exception] =" + e.getMessage());
			e.getMessage();
		}

		return responseMessage;
	}
	public InternetAddress[] getTo(MailRequestDto mailRequestDto) throws AddressException {
		List<String> recipients = new ArrayList<>();
//		List<String>  emailadd=new ArrayList<>();
//		emailadd.add("vaibhav.anand@incture.com");
//		emailadd.add("subhra.priyadarshinee@incture.com");
		
		recipients.addAll(mailRequestDto.getTo());

		InternetAddress[] addressTo = new InternetAddress[recipients.size()];
		for (int i = 0; i < recipients.size(); i++) {
			System.err.println(recipients.get(i));
			addressTo[i] = new InternetAddress(recipients.get(i));
		}

		return addressTo;
	}

	public InternetAddress[] getCC(MailRequestDto mailRequestDto) throws AddressException {
		List<String> recipients = new ArrayList<>();
//		List<String>  emailadd=new ArrayList<>();
//		emailadd.add("vaibhav.anand@incture.com");
//		emailadd.add("subhra.priyadarshinee@incture.com");
		recipients.addAll(mailRequestDto.getCc());

		InternetAddress[] addressTo = new InternetAddress[recipients.size()];
		for (int i = 0; i < recipients.size(); i++) {
			System.err.println(recipients.get(i));
			addressTo[i] = new InternetAddress(recipients.get(i));
		}

		return addressTo;
	}

	public Properties getOutlookProperties() {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp-mail.outlook.com");
		props.put("mail.smtp.port", "587");
		return props;
	}
}
