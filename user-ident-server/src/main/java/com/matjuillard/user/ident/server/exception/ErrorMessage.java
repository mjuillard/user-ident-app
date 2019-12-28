package com.matjuillard.user.ident.server.exception;

import java.util.Date;

public class ErrorMessage {

	private Date timestamp;
	private String message;

	public ErrorMessage() {
		super();
	}

	public ErrorMessage(Date timestamp, String messgage) {
		super();
		this.timestamp = timestamp;
		this.message = messgage;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
