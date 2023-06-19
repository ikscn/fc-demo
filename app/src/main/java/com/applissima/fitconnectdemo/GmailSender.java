package com.applissima.fitconnectdemo;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import io.realm.Realm;


/**
 * Created by ilkerkuscan on 09/04/17.
 */

public class GmailSender {

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";


    String fromEmail;
    String fromPassword;
    List<String> toEmailList;
    String emailSubject;
    String emailBody;
    List<File> fileList;

    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public GmailSender() {

    }

    public GmailSender(MailData mailData) {

        this.fromEmail = mailData.getFromEmail();
        this.fromPassword = mailData.getFromPassword();
        this.toEmailList = mailData.getToEmailList();
        this.emailSubject = mailData.getEmailSubject();
        this.emailBody = mailData.getEmailBody();
        this.fileList = mailData.getFileList();

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
        for (String toEmail : toEmailList) {
            Log.i("GMail", "toEmail: " + toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }

        emailMessage.setSubject(emailSubject);

        if(fileList!=null && fileList.size()>0){

            // Create a multipart message
            Multipart multipart = new MimeMultipart();
            //BodyPart mainBody = new MimeBodyPart();
            //mainBody.setContent(emailBody, "text/html");
            //multipart.addBodyPart(mainBody);
            for(File file :fileList){
                DataSource source = new FileDataSource(file);
                BodyPart messageBodyPart = new MimeBodyPart();
                try {
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(messageBodyPart);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            // Send the complete message parts
            emailMessage.setContent(multipart);

        } else {
            emailMessage.setContent(emailBody, "text/html");
        }

        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Transport transport = null;
                try {
                    transport = mailSession.getTransport("smtp");
                    transport.connect(emailHost, fromEmail, fromPassword);
                    Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
                    transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
                    transport.close();
                    Log.i("GMail", "Email sent successfully.");
                } catch (NoSuchProviderException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


}
