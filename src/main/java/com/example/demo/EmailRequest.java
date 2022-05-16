package com.example.demo;

import java.time.OffsetDateTime;
import java.util.List;

public class EmailRequest {
	private String userId ;
	private OffsetDateTime startDate;	
	private OffsetDateTime endDate;	
	private String destinationFolder;
	private boolean isResponseRequired;
	private boolean download;
	private String downaloadPath;
	
	public String getUserId() {
		return userId;
	}
	
	public OffsetDateTime getStartDate() {
		return startDate;
	}
	
	public OffsetDateTime getEndDate() {
		return endDate;
	}
	
	public String getDestinationFolder() {
		return destinationFolder;
	}
	
	public boolean isResponseRequired() {
		return isResponseRequired;
	}
	
	public boolean Download() {
		return download;
	}

	public String getDownaloadPath() {
		return downaloadPath;
	}


	public static EmailRequest newInstance() {
		return new EmailRequest();
	}
	public EmailRequest userId(String userId) {
		this.userId = userId;
		return this;
	}
	public EmailRequest destinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
		return this;
	}
	public EmailRequest responseRequired(boolean isResponseRequired) {
		this.isResponseRequired = isResponseRequired;
		return this;
	}
	public EmailRequest endDate(OffsetDateTime endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public EmailRequest startDate(OffsetDateTime startDate) {
		this.startDate = startDate;
		return this;
	}

	public EmailRequest Download(boolean download) {
		this.download = download;
		return this;
	}

	public EmailRequest DownaloadPath(String downaloadPath) {
		this.downaloadPath = downaloadPath;
		return this;
	}

	public List<Email> get() {
		EmailService2 emailService =new EmailService2();
		return emailService.processMailbox(this);
		 
	}

    
}
