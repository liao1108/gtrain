package com.itez.vghtc.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Settings implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Nation> listNation = new ArrayList<>();
	private List<Dept> listDept = new ArrayList<>();
	private LinkedHashMap<String, List<Divi>> mapDeptDivi = new LinkedHashMap<>();
	
	public List<Nation> getListNation() {
		return listNation;
	}
	public void setListNation(List<Nation> listNation) {
		this.listNation = listNation;
	}
	public List<Dept> getListDept() {
		return listDept;
	}
	public void setListDept(List<Dept> listDept) {
		this.listDept = listDept;
	}
	public LinkedHashMap<String, List<Divi>> getMapDeptDivi() {
		return mapDeptDivi;
	}
	public void setMapDeptDivi(LinkedHashMap<String, List<Divi>> mapDeptDivi) {
		this.mapDeptDivi = mapDeptDivi;
	}
	
}
