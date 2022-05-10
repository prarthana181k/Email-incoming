package com.example.demo;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MessageCollectionPage;
import com.microsoft.graph.models.FileAttachment;

import okhttp3.Request;

public class EmailDemoApplication{

    private final static String CLIENT_ID = "051f07ee-ce7b-48c2-871d-f161d0999d91";
    private final static String TENANT_ID = "5a1b9e72-d2ee-459b-99fb-24061c5c665d";
    private final static String SECRET_ID = "EfK8Q~BZx6lEiJio6U6JpsLSjwcyXRiJprrQDdcA";

    //Set the scopes for your ms-graph request
    private final static List<String> SCOPES = Arrays.asList("https://graph.microsoft.com/.default");

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
	                .users("1986b82e-6286-4ac4-9d04-b34b6c74be1d")
	                .buildRequest()
	                .get();
	        	System.out.println("Hello " + me.displayName );
	        	}
	        catch(Exception e) {
	        	System.out.print(e.toString());
	        	}
	        }   
       
        MessageCollectionPage messages = graphClient.users("1986b82e-6286-4ac4-9d04-b34b6c74be1d").messages()
        	.buildRequest()
        	.filter("(from/emailAddress/address) eq '{user-mail}'")
        	.get();
        
		
    }

}

