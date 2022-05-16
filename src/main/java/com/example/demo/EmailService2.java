package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipOutputStream;

import org.springframework.lang.Nullable;
import org.springframework.security.web.header.writers.frameoptions.StaticAllowFromStrategy;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.ctc.wstx.cfg.ParsingErrorMsgs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.JsonAdapter;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.http.BaseCollectionPage;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.MessageMoveParameterSet;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.requests.FileAttachmentRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.MessageCollectionRequest;
import com.microsoft.graph.requests.MessageCollectionRequestBuilder;
import com.microsoft.graph.requests.UserRequestBuilder;

import okhttp3.Request;

public class EmailService2 {
	
	private final static String CLIENT_ID = "47300f86-73c6-4b95-a81e-b4c15fedc831";
    private final static String TENANT_ID = "b5c66e55-d7b0-405d-b4f8-9a6e27f8d531";
    private final static String SECRET_ID = "YHA8Q~llII13px61YW4LHER8in5LYpXn.92h4dl_";

    //Set the scopes for your ms-graph request
    private final static List<String> SCOPES = Arrays.asList("https://graph.microsoft.com/.default");
    
	public List<Email> processMailbox (EmailRequest request) {
		GraphServiceClient graphClient= getAuthorization();
		
		return processEmessage(graphClient,request);
	}
	private static GraphServiceClient getAuthorization() {
		final ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(CLIENT_ID)
                .clientSecret(SECRET_ID)
                .tenantId(TENANT_ID)
                .build();

        
        final TokenCredentialAuthProvider tokenCredAuthProvider = new TokenCredentialAuthProvider(SCOPES, clientSecretCredential);

        // Build a Graph client
        GraphServiceClient<Request> graphClient = GraphServiceClient.builder()
                .authenticationProvider(tokenCredAuthProvider)
                .buildClient();
        
		return graphClient;
	}
	
	private  List<Email> processEmessage(GraphServiceClient graphClient, EmailRequest request ) {
		UserRequestBuilder userRequestBuilder=graphClient.users(request.getUserId());
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("Prefer", "outlook.body-content-type=\"text\""));
		List<Email> mailList= new ArrayList<>();
		MessageCollectionRequest builder=userRequestBuilder.mailFolders("Inbox").messages().buildRequest(requestOptions).expand("attachments");
		Optional.ofNullable(request.getStartDate()).map(date->builder.filter("receivedDateTime ge "+date));
		Optional.ofNullable(request.getEndDate()).map(date->builder.filter("receivedDateTime le "+date));
		try{
			MessageCollectionPage messages=builder.get();
			mailList=processEmail( builder.get(), graphClient, request );
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
		return mailList;
		
	}
	
	private static List<Email> processEmail(MessageCollectionPage messages,GraphServiceClient graphClient ,EmailRequest request) {
		List<Email> mailList = new ArrayList<>();

		messages.getCurrentPage().forEach(msg->{
			String 	destinationFolder=request.getDestinationFolder();
			if (request.isResponseRequired()) {
				if(destinationFolder!=null || !destinationFolder.isEmpty()) {
					graphClient.users(request.getUserId()).messages(msg.id)
					.move(MessageMoveParameterSet
						.newBuilder()
						.withDestinationId(request.getDestinationFolder())
						.build())
					.buildRequest()
					.post();
				}
				else {
					Email email =new Email();
			        email.setSenderEmailId(Optional.ofNullable(msg.sender.emailAddress.address).map(senderemail->senderemail).orElse(null));
			        email.setSubject(msg.subject);
			        email.setDate(msg.receivedDateTime);
			        if(msg.hasAttachments) {
			        	List<Attachment> attachmentList= new ArrayList<>();
			        	msg.attachments.getCurrentPage().forEach(attach->{
			        		Attachment attachment=new Attachment();
			        		String requestUrl =graphClient.users(request.getUserId())
			        		        .messages(msg.id)
			        		        .attachments(attach.id)
			        		        .buildRequest()
			        		        .getRequestUrl()
			        		        .toString();
			        		FileAttachment fileAttachment =new FileAttachmentRequestBuilder(requestUrl,graphClient,null).buildRequest().get();
			        		attachment.setFileName(fileAttachment.name);
			        		attachment.setFileType(fileAttachment.contentType);
			        		attachment.setContent( new FileAttachmentRequestBuilder(requestUrl,graphClient,null).content().buildRequest().get());
			        		if(request.Download()) {
			        			byte[] content = fileAttachment.contentBytes;
				        		downloadEmails(content,fileAttachment.name,request);
			        		}
			        		});
			        	}
			        
			        	mailList.add(email);
					}
				}
		        
			});
		return mailList;
	}
	//path="C:\\Users\\DELL\\New\\"
	private static void downloadEmails( byte[] content, String name, EmailRequest request) {
		try (FileOutputStream stream = new FileOutputStream(request.getDownaloadPath()+name)) {
            stream.write(content);
        } catch (IOException exception) {
            // Handle it
        	System.out.println(exception.toString());
        }
	}
	
	
}
