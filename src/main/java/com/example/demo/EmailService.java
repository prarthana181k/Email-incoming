package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.requests.FileAttachmentRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.requests.MessageCollectionRequestBuilder;

import okhttp3.Request;

public class EmailService {
	
	private final static String CLIENT_ID = "47300f86-73c6-4b95-a81e-b4c15fedc831";
    private final static String TENANT_ID = "b5c66e55-d7b0-405d-b4f8-9a6e27f8d531";
    private final static String SECRET_ID = "YHA8Q~llII13px61YW4LHER8in5LYpXn.92h4dl_";

    //Set the scopes for your ms-graph request
    private final static List<String> SCOPES = Arrays.asList("https://graph.microsoft.com/.default");
    
	public static ZipOutputStream conhub (String userId,@Nullable String date) {
		GraphServiceClient graphClient= getAuthorization();
		MessageCollectionPage messages= getAttachment(graphClient,userId,date);
		
		return null;
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
	
	private static MessageCollectionPage getAttachment(GraphServiceClient graphClient, String userId, @Nullable String date ) {
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("Prefer", "outlook.body-content-type=\"text\""));
		List<Email> mailList= new ArrayList<>();
		if (date==null) {
			MessageCollectionPage messages = graphClient.users(userId).mailFolders("Inbox").messages()
		        	.buildRequest(requestOptions)
		        	.expand("attachments")
		        	.get();
			mailList=processEmail( messages, graphClient, userId );
			return messages;
		}
		else {
			MessageCollectionPage messages = graphClient.users(userId).mailFolders("Inbox").messages()
		        	.buildRequest(requestOptions)
		        	.expand("attachments")
		        	.filter("receivedDateTime ge "+date)
		        	.get();
			mailList=processEmail( messages,  graphClient,  userId );
			return messages;
		}
		
		
	}
	
	private static List<Email> processEmail(MessageCollectionPage messages, GraphServiceClient graphClient, String userId ) {
		List<Email> mailList= new ArrayList<>();
		messages.getCurrentPage().forEach(msg->{
        Email email =new Email(msg.sender.emailAddress.address,msg.subject,msg.receivedDateTime.toString(),msg.attachments.getCurrentPage(),msg.body.content.toString());
        System.out.println(msg.receivedDateTime.toString());
        if(msg.hasAttachments) {
        	msg.attachments.getCurrentPage().forEach(attach->{
        		String requestUrl = graphClient
        				.users(userId)
        		        .mailFolders("Inbox")
        		        .messages(msg.id)
        		        .attachments(attach.id)
        		        .buildRequest()
        		        .getRequestUrl()
        		        .toString();
        		FileAttachmentRequestBuilder requestBuilder = new FileAttachmentRequestBuilder(requestUrl, graphClient, null);
        		FileAttachment fileAttachment = requestBuilder.buildRequest().get();
        		byte[] content = fileAttachment.contentBytes;
        		downloadEmails(content,fileAttachment.name);
        		});
        	}
        	mailList.add(email);
		});
		return mailList;
	}

	private static void downloadEmails( byte[] content, String name) {
		try (FileOutputStream stream = new FileOutputStream("C:\\Users\\DELL\\New\\"+name)) {
            stream.write(content);
        } catch (IOException exception) {
            // Handle it
        	System.out.println(exception.toString());
        }
	}
	
	
}
