package com.itez.vghtc.gtrainman;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String userNo = "";		//登入 Alfresco 的帳號
	public String passwd = "";		//登入 Alfresco 的密碼
	
	public String displayName = "";	//LDAP 取得的中文姓名
	public String email = "";		//LDAP 取得的 email
	
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
