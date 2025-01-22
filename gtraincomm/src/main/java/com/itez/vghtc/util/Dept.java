package com.itez.vghtc.util;

public class Dept implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	private String deptNameEn = "";
	private String deptNameCh = "";
	
	public String getDeptNameEn() {
		return deptNameEn;
	}

	public void setDeptNameEn(String deptNameEn) {
		this.deptNameEn = deptNameEn;
	}

	public String getDeptNameCh() {
		return deptNameCh;
	}

	public void setDeptNameCh(String deptNameCh) {
		this.deptNameCh = deptNameCh;
	}

}
