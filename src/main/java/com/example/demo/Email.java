package com.example.demo;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.MimeContent;

public class Email {
	String senderEmailId;
	String subject;
	String date;
	List<Attachment> attachments =new ArrayList();
	String content ;
	public Email() {
		super();
	}
	public Email(String senderEmailId, String subject, String date, List<Attachment> attachments, String content) {
		super();
		this.senderEmailId = senderEmailId;
		this.subject = subject;
		this.date = date;
		this.attachments = attachments;
		this.content = content;
	}
	
	
	
}
