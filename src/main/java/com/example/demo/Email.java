package com.example.demo;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.graph.models.MimeContent;

public class Email {
	String senderEmailId;
	OffsetDateTime date;
	List<byte[]> attachments =new ArrayList();
	MimeContent content ;
	public Email() {
		super();
	}
	public Email(String senderEmailId, OffsetDateTime date, List<byte[]> attachments, MimeContent content) {
		super();
		this.senderEmailId = senderEmailId;
		this.date = date;
		this.attachments = attachments;
		this.content = content;
	}
	public String getSenderEmailId() {
		return senderEmailId;
	}
	public void setSenderEmailId(String senderEmailId) {
		this.senderEmailId = senderEmailId;
	}
	public OffsetDateTime getDate() {
		return date;
	}
	public void setDate(OffsetDateTime date) {
		this.date = date;
	}
	public List<byte[]> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<byte[]> attachments) {
		this.attachments = attachments;
	}
	public MimeContent getContent() {
		return content;
	}
	public void setContent(MimeContent content) {
		this.content = content;
	}
	
	
	
	
	
}
