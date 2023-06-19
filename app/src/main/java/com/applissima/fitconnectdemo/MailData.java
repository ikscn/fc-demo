package com.applissima.fitconnectdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilkerkuscan on 16/09/17.
 */

public class MailData {

    private String fromEmail;
    private String fromPassword;
    private List<String> toEmailList;
    private String emailSubject;
    private String emailBody;
    private List<File> fileList;
    private MailType mailType;

    public MailData(){
        this.toEmailList = new ArrayList<String>();
        this.fileList = new ArrayList<File>();
    }

    public MailData(String fromEmail, String fromPassword, List<String> toEmailList,
                    String emailSubject, String emailBody, List<File> fileList, MailType mailType) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
        this.fileList = fileList;
        this.mailType = mailType;
    }

    public void prepareDefault(){
        this.fromEmail = AppDefaults.GMAIL_USER;
        this.fromPassword = AppDefaults.GMAIL_PW;
        this.toEmailList.add(AppDefaults.GMAIL_USER);
    }

    public void attachFile(File file){
        this.fileList.add(file);
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromPassword() {
        return fromPassword;
    }

    public void setFromPassword(String fromPassword) {
        this.fromPassword = fromPassword;
    }

    public List<String> getToEmailList() {
        return toEmailList;
    }

    public void setToEmailList(List<String> toEmailList) {
        this.toEmailList = toEmailList;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public MailType getMailType() {
        return mailType;
    }

    public void setMailType(MailType mailType) {
        this.mailType = mailType;
    }
}
