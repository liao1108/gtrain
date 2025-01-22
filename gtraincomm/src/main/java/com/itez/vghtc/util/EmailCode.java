package com.itez.vghtc.util;

public class EmailCode implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	private String email = "";
	private String code = "";
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
