package com.matjuillard.useridentapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.matjuillard.useridentapp.model.dto.UserDto;
import com.matjuillard.useridentapp.service.SimpleEmailService;

@Service
public class SimpleEmailServiceImpl implements SimpleEmailService {

	protected static final String FROM = "matjuillard.useridentapp.com";
	protected static final String EMAIL_LINK = "http://localhost:8080/email-verification?token=";
	protected static final String SUBJECT = "Complete your registration";

	protected final String HTML_BODY = "<h1>Please verify your email</h1>"
			+ "<br/>Click on the following link: <a href='$emailLink'>Complete registration link</a><br/>";

	protected final String PASSWORD_RESET_HTML_BODY = "<h1>Please verify your email</h1>" + "<br/>Hi $firstName"
			+ "<br/>Click on the following link: <a href='$emailLink'>Complete registration link</a><br/>";

	@Autowired
	public JavaMailSender emailSender;

	public void verifyEmail(UserDto userDto) {

		String htmlBody = HTML_BODY.replace("$emailLink", EMAIL_LINK);
		String htmlBodyWithToken = htmlBody.concat(userDto.getEmailVerificationToken());

		/*
		 * Gmail email sender
		 */
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(userDto.getEmail());
//		message.setSubject(SUBJECT);
//		message.setText(htmlBodyWithToken);
//		emailSender.send(message);

		/*
		 * Amazon Simple Email Service
		 */
		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
				.withRegion(Regions.EU_CENTRAL_1).build();
		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(userDto.getEmail()))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);
		client.sendEmail(request);

	}

	public boolean sendPasswordResetRequest(String firstName, String email, String token) {

		String htmlBody = PASSWORD_RESET_HTML_BODY.replace("$emailLink", email).replace("$firstName", firstName);
		String htmlBodyWithToken = htmlBody.concat(token);

		AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_WEST_3)
				.build();
		SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(email))
				.withMessage(new Message()
						.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken)))
						.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
				.withSource(FROM);
		SendEmailResult result = client.sendEmail(request);
		if (result != null && !StringUtils.isEmpty(result.getMessageId())) {
			return true;
		}

		return false;
	}
}
