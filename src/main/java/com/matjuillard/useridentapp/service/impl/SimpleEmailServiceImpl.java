package com.matjuillard.useridentapp.service.impl;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.matjuillard.useridentapp.model.dto.UserDto;
import com.matjuillard.useridentapp.service.SimpleEmailService;

@Service
public class SimpleEmailServiceImpl implements SimpleEmailService {

	protected static final String FROM = "matjuillard.useridentapp.com";
	protected static final String SUBJECT = "Complete your registration";

	protected final String HTML_BODY = "<h1>Please verify your email</h1>"
			+ "<br/>Click on the following link: <a href='$emailLink'>Complete registration link</a><br/>";

	protected final String BODY = "Please verify your email\n\n" + "Dear $firstName"
			+ "\nPlease go to the following link: $emailLink \nto complete your registration";

	protected final String PASSWORD_RESET_HTML_BODY = "<h1>Please change your email</h1>" + "<br/>Dear $firstName"
			+ "<br/>Click on the following link: <a href='$passwordReset'>Modify your password</a><br/>";

	protected final String PASSWORD_RESET_BODY = "Please change your email\n\n" + "Dear $firstName"
			+ "\nPlease go to the following link: $passwordReset \nto modify your password";

	@Value("${email-verification.link}")
	private String emailVerificationUrlLink;

	@Value("${password-reset.link}")
	private String passwordResetLink;

	public void verifyEmail(UserDto userDto) {

		String emailWithToken = emailVerificationUrlLink.concat(userDto.getEmailVerificationToken());
		String htmlBodyWithToken = BODY.replace("$emailLink", emailWithToken).replace("$firstName",
				userDto.getFirstName());

		/*
		 * Gmail email sender
		 */
		sendMail(userDto.getEmail(), SUBJECT, htmlBodyWithToken);

		/*
		 * Amazon Simple Email Service
		 */
//		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
//				.withRegion(Regions.EU_CENTRAL_1).build();
//		SendEmailRequest request = new SendEmailRequest()
//				.withDestination(new Destination().withToAddresses(userDto.getEmail()))
//				.withMessage(new Message()
//						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken)))
//						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
//				.withSource(FROM);
//		client.sendEmail(request);

	}

	public boolean sendPasswordResetRequest(String firstName, String email, String token) {

		String link = passwordResetLink.concat(token);
		String htmlBody = PASSWORD_RESET_BODY.replace("$passwordReset", link).replace("$firstName", firstName);
		String htmlBodyWithToken = htmlBody.concat(token);

		/*
		 * Gmail email sender
		 */
		return sendMail(email, SUBJECT, htmlBodyWithToken);

		/*
		 * Amazon Simple Email Service
		 */
//		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_3)
//				.build();
//		SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(email))
//				.withMessage(new Message()
//						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken)))
//						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
//				.withSource(FROM);
//		SendEmailResult result = client.sendEmail(request);
//		if (result != null && !StringUtils.isEmpty(result.getMessageId())) {
//			return true;
//		}
//
//		return false;
	}

	private boolean sendMail(String email, String subject, String body) {

		// 1. Deactivate avast
		// 2. Go to https://myaccount.google.com/security : Acces - securisé des apps
		// 3. Put gmail credentials below

		final String username = "XXXXXXXXX";
		final String password = "XXXXXXXXX";

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "465");
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.socketFactory.port", "465");
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(subject);
			message.setText(body);

			Transport.send(message);

			System.out.println("Done");

		} catch (MessagingException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
