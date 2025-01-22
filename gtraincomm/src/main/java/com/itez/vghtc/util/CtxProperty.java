package com.itez.vghtc.util;

import java.io.Serializable;

import javax.mail.internet.InternetAddress;

import com.itez.vghtc.util.Gutil.DeviceType;

import io.minio.MinioClient;

public class CtxProperty implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String alfHost= "";
	protected String alfAgentAcc = "";
	protected String alfAgentPwd = "";
	
	protected String smtpHost = "";
	protected String smtpPort = "";
	protected String starttls = "true";
	protected boolean smtpAuth = false;
	protected String smtpUser = "";
	protected String smtpPwd = "";
	protected String smtpFromMail = "";
	
	protected String uriGtrain = "";			//Gloabl Train REST API
	
	protected String minioHost= "http://localhost:9000";
	protected String minioAgentAcc = "minioadmin";
	protected String minioAgentPwd = "#Aa26687170";
	
	protected MinioClient minioClient = null;
	
	protected boolean admin = false;
	protected DeviceType deviceType = DeviceType.Desktop;
	protected String siteId = "";
	
	protected InternetAddress fromAccount = null;
	protected String ccAccount = "";
	
	public MinioClient getMinioClient() {
		return minioClient;
	}
	public void setMinioClient(MinioClient minioClient) {
		this.minioClient = minioClient;
	}
	
	public InternetAddress getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(InternetAddress fromAccount) {
		this.fromAccount = fromAccount;
	}
	public String getCcAccount() {
		return ccAccount;
	}
	public void setCcAccount(String ccAccount) {
		this.ccAccount = ccAccount;
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	public String getSiteId() {
		return siteId;
	}
	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}
	public String getMinioHost() {
		return minioHost;
	}
	public void setMinioHost(String minioHost) {
		this.minioHost = minioHost;
	}
	public String getMinioAgentAcc() {
		return minioAgentAcc;
	}
	public void setMinioAgentAcc(String minioAgentAcc) {
		this.minioAgentAcc = minioAgentAcc;
	}
	public String getMinioAgentPwd() {
		return minioAgentPwd;
	}
	public void setMinioAgentPwd(String minioAgentPwd) {
		this.minioAgentPwd = minioAgentPwd;
	}
	public String getUriGtrain() {
		return uriGtrain;
	}
	public void setUriGtrain(String uriGtrain) {
		this.uriGtrain = uriGtrain;
	}
	public String getSmtpFromMail() {
		return smtpFromMail;
	}
	public void setSmtpFromMail(String smtpFromMail) {
		this.smtpFromMail = smtpFromMail;
	}

	public boolean isSmtpAuth() {
		return smtpAuth;
	}
	public void setSmtpAuth(boolean smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
	public String getSmtpUser() {
		return smtpUser;
	}
	public void setSmtpUser(String smtpUser) {
		this.smtpUser = smtpUser;
	}
	public String getSmtpPwd() {
		return smtpPwd;
	}
	public void setSmtpPwd(String smtpPwd) {
		this.smtpPwd = smtpPwd;
	}

	public String getAlfHost() {
		return alfHost;
	}
	public void setAlfHost(String alfHost) {
		this.alfHost = alfHost;
	}
	public String getAlfAgentAcc() {
		return alfAgentAcc;
	}
	public void setAlfAgentAcc(String alfAgentAcc) {
		this.alfAgentAcc = alfAgentAcc;
	}
	public String getAlfAgentPwd() {
		return alfAgentPwd;
	}
	public void setAlfAgentPwd(String alfAgentPwd) {
		this.alfAgentPwd = alfAgentPwd;
	}
	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public String getStarttls() {
		return starttls;
	}
	public void setStarttls(String starttls) {
		this.starttls = starttls;
	}
}
