package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipOutputStream;


import org.springframework.lang.Nullable;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.requests.FileAttachmentRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;

import okhttp3.Request;

public class EmailService {
	
	private final static String CLIENT_ID = "47300f86-73c6-4b95-a81e-b4c15fedc831";
    private final static String TENANT_ID = "b5c66e55-d7b0-405d-b4f8-9a6e27f8d531";
    private final static String SECRET_ID = "YHA8Q~llII13px61YW4LHER8in5LYpXn.92h4dl_";

    //Set the scopes for your ms-graph request
    private final static List<String> SCOPES = Arrays.asList("https://graph.microsoft.com/.default");
    
	public static ZipOutputStream conhub (String userId,@Nullable OffsetDateTime date) {
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
	
	private static MessageCollectionPage getAttachment(GraphServiceClient graphClient, String userId, @Nullable OffsetDateTime date ) {
		MessageCollectionPage messages = graphClient.users(userId).messages()
	        	.buildRequest()
	        	.expand("attachments")
	        	.get();
		messages.getCurrentPage().forEach(msg->{
        	System.out.println(msg.sender.emailAddress.address);
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

        	            try (FileOutputStream stream = new FileOutputStream("C:\\Users\\DELL\\New\\"+fileAttachment.name)) {
        	                stream.write(content);
        	            } catch (IOException exception) {
        	                // Handle it
        	            	System.out.println(exception.toString());
        	            }
        		});
        	}
        });
		return messages;
	}
	
}
