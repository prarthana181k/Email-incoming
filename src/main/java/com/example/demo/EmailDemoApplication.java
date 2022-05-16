package com.example.demo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.OffsetDateTime;
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

    public static void main(String[] args) throws Exception {
        
//    	EmailRequest email= new EmailRequest();
//    	email.newInstance().userId("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53").DownaloadPath(null);
//    	//email.conhub("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53","2022-05-13T05:49:00Z");
    	System.out.println(OffsetDateTime.parse("2022-05-11T00:00:00Z"));
    	EmailRequest.newInstance().userId("bf03eeb7-b951-4a42-8eb4-0c2ca31bbb53").endDate(OffsetDateTime.parse("2022-05-12T00:00:00Z")).responseRequired(true)
    	.get()
    	.forEach(mail->{System.out.println(mail.subject);System.out.println(mail.date);});;
    	
    }

	
}

