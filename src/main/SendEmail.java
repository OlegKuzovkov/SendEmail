package main;
// File Name SendEmail.java

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class SendEmail {
	private String to;
	private String from;
	private String cc;
	private String bcc;
	private String subject;
	private String body;
	private String attachment;
	private String smtp;
	
public static void main(String [] args) throws InvalidFileFormatException, IOException {
	  String iniFile = args[0];
	  Ini ini = new Ini(new File(iniFile));
	  SendEmail SendEmail = new SendEmail();
      // Recipient's email ID needs to be mentioned.
	  String credentialsSection = "Credentials";
	  SendEmail.to=ini.get(credentialsSection,"To");
	  SendEmail.from=ini.get(credentialsSection,"From");
	  SendEmail.cc=ini.get(credentialsSection,"CC");
	  SendEmail.bcc=ini.get(credentialsSection,"BCC");
	  SendEmail.subject=ini.get(credentialsSection,"Subject");
	  SendEmail.body=ini.get(credentialsSection,"Body");
	  SendEmail.attachment=ini.get(credentialsSection,"Attachment");
	  SendEmail.smtp=ini.get(credentialsSection,"SMTP");

	  ReadFile readfile = new ReadFile();
      // Get system properties
      Properties properties = System.getProperties();
      // Setup mail server
      properties.setProperty("mail.smtp.host", SendEmail.smtp);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(SendEmail.from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO,
                                  new InternetAddress(SendEmail.to));
         // Set CC: header field of the header.
         if ((SendEmail.cc!=null)&&(SendEmail.cc.compareTo("")!=0)){
        	 String[] cc_recipients = readfile.Read(SendEmail.cc);
        	 InternetAddress[] internetAddresses = new InternetAddress[cc_recipients.length];
        	 int i=0;
        	 for (String s:cc_recipients){
        		 InternetAddress ia= new InternetAddress(s);
        		 internetAddresses[i]=ia;
        		 i++;
        	 }
        	 message.addRecipients(Message.RecipientType.CC,
        			 internetAddresses);
         }
         //Set BCC: header field of the header.
         if ((SendEmail.bcc!=null)&&(SendEmail.bcc.compareTo("")!=0)){
        	 String[] bcc_recipients = readfile.Read(SendEmail.bcc);
        	 InternetAddress[] internetAddresses = new InternetAddress[bcc_recipients.length];
        	 int i=0;
        	 for (String s:bcc_recipients){
        		 InternetAddress ia= new InternetAddress(s);
        		 internetAddresses[i]=ia;
        		 i++;
        	 }
        	 message.addRecipients(Message.RecipientType.BCC,
        			 internetAddresses);
         }
         // Set Subject: header field
         StringBuilder subjectContent = new StringBuilder();
         
         FileInputStream subjectFile = new FileInputStream(SendEmail.subject);
         BufferedReader sin = new BufferedReader(new InputStreamReader(subjectFile, "UTF-8"));
         
         try {
             String str;    
             while ((str = sin.readLine()) != null) {
            	 subjectContent.append(str);
             }
             sin.close();
         } catch (IOException e) {
         }
         String subject = subjectContent.toString();
         message.setSubject(subject,"UTF-8");
         

         // Now set the actual message
         // Create the message part
         BodyPart messageBodyPart = new MimeBodyPart();

         if ((SendEmail.body!=null)&&(SendEmail.body.compareTo("")!=0)){
        	 FileInputStream bodyFile = new FileInputStream(SendEmail.body);
             StringBuilder contentBuilder = new StringBuilder();
             BufferedReader bin = new BufferedReader(new InputStreamReader(bodyFile, "UTF-8"));
             
             try {
                 String str;    
                 while ((str = bin.readLine()) != null) {
                     contentBuilder.append(str);
                 }
                 bin.close();
             } catch (IOException e) {
             }
             String body = contentBuilder.toString();

             // Now set the actual message
             messageBodyPart.setContent(body, "text/html; charset=utf-8");
         }
         

         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         if ((SendEmail.attachment!=null)&&(SendEmail.attachment.compareTo("")!=0)){
        	// Part two is attachment
        	 String[] attachment_files = readfile.Read(SendEmail.attachment);
        	 for (String s:attachment_files){
        		 messageBodyPart = new MimeBodyPart();
                 String filename = s;
                 DataSource source = new FileDataSource(filename);
                 messageBodyPart.setDataHandler(new DataHandler(source));
                 messageBodyPart.setFileName(filename);
                 multipart.addBodyPart(messageBodyPart);	 
        	 }
         }
                  
         // Send the complete message parts
         message.setContent(multipart);
         // Send message
         Transport.send(message);
         System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
}
class SMTPAuthenticator extends javax.mail.Authenticator {
    public PasswordAuthentication getPasswordAuthentication() {
       String username = "kuzovkov.oleg@gmail.com";
       String password = "298620087";
       return new PasswordAuthentication(username, password);
    }
}
