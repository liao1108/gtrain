package com.itez.vghtc.util;

public class DiviChoice implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	private Dept dept = null;
	private Divi divi = null;
	private String fromDate = "";
	private String endDate = "";
	private Teacher teacher1 = new Teacher();		//指導醫師一
	private Teacher teacher2 = new Teacher();		//指導醫師一
	private Teacher teacher3 = new Teacher();		//指導醫師一
	
	public Teacher getTeacher1() {
		return teacher1;
	}

	public void setTeacher1(Teacher teacher1) {
		this.teacher1 = teacher1;
	}

	public Teacher getTeacher2() {
		return teacher2;
	}

	public void setTeacher2(Teacher teacher2) {
		this.teacher2 = teacher2;
	}

	public Teacher getTeacher3() {
		return teacher3;
	}

	public void setTeacher3(Teacher teacher3) {
		this.teacher3 = teacher3;
	}

	private String timeStamp = "";

	public Dept getDept() {
		return dept;
	}

	public void setDept(Dept dept) {
		this.dept = dept;
	}

	public Divi getDivi() {
		return divi;
	}

	public void setDivi(Divi divi) {
		this.divi = divi;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}

