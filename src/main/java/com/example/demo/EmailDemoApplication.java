package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.FileAttachmentRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;

import okhttp3.Request;

public class EmailDemoApplication{
	private final static String CLIENT_ID = "47300f86-73c6-4b95-a81e-b4c15fedc831";
    private final static String TENANT_ID = "b5c66e55-d7b0-405d-b4f8-9a6e27f8d531";
    private final static String SECRET_ID = "YHA8Q~llII13px61YW4LHER8in5LYpXn.92h4dl_";

    //Set the scopes for your ms-graph request
    private final static List<String> SCOPES = Arrays.asList("https://graph.microsoft.com/.default");
    //static String arr[]= { "Mail.Read","offline_access","openid" };
    //static List<String> scopes = new ArrayList<String>(Arrays.asList(arr));
    public static void main(String[] args) throws Exception {
        // Create the auth provider.        
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
        
       
        URL myUrl = new URL("https://graph.microsoft.com/v1.0/me/");
        final String accessToken = tokenCredAuthProvider.getAuthorizationTokenAsync(myUrl).get();
        System.out.println("Access token --> " + accessToken);
		
        
        // Just another optional step to get name of signed-in user.
        if (graphClient == null) throw new NullPointerException(
                "Graph client has not been initialized. Call initializeGraphAuth before calling this method");
        else {
	        try{
	        	final User me = graphClient
	        		.users("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53")
	                .buildRequest()
	                .get();
	        	System.out.println("Hello " + me.displayName );
	        	}
	        catch(Exception e) {
	        	System.out.print(e.toString());
	        	}
	        }   
       
        MessageCollectionPage messages = graphClient.users("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53").messages()
        	.buildRequest()
        	.expand("attachments")
        	.get();

        messages.getCurrentPage().forEach(msg->{
        	System.out.println(msg.sender.emailAddress.address);
        	if(msg.hasAttachments) {
        		msg.attachments.getCurrentPage().forEach(attach->{
        			/*
        			 Attachment stream = graphClient.users("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53").messages(msg.id).attachments(attach.id)
        			.buildRequest()
        			.get();
        			*/
        			 String requestUrl = graphClient
        		        		.users("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53")
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
        
        
        
            
            
           
           
          
    }

}

