package com.itez.vghtc.util;

public class Teacher implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	private String teacherNameEn = "";
	private String teacherNameCh = "";
	private String teacherEmail = "";
	private String teacherPhoneNo = "";
	
	public String getTeacherNameEn() {
		return teacherNameEn;
	}
	public void setTeacherNameEn(String teacherNameEn) {
		this.teacherNameEn = teacherNameEn;
	}
	public String getTeacherNameCh() {
		return teacherNameCh;
	}
	public void setTeacherNameCh(String teacherNameCh) {
		this.teacherNameCh = teacherNameCh;
	}
	public String getTeacherEmail() {
		return teacherEmail;
	}
	public void setTeacherEmail(String teacherEmail) {
		this.teacherEmail = teacherEmail;
	}
	public String getTeacherPhoneNo() {
		return teacherPhoneNo;
	}
	public void setTeacherPhoneNo(String teacherPhoneNo) {
		this.teacherPhoneNo = teacherPhoneNo;
	}
	
}
