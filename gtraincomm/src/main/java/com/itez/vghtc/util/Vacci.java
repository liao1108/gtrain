package com.itez.vghtc.util;

//import org.apache.pdfbox.pdmodel.PDDocument;

public class Vacci implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

	private String timeStamp = "";
	private String vacciDate = null;		//接種日期
	private String vender = "";				//疫苗廠牌
	private String placeVacci = "";			//接種地點
	private String elseVender = "";			//其他廠牌疫苗名稱
	
	//private PDDocument proofVacci = null;	//接種證明
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getElseVender() {
		return elseVender;
	}
	public void setElseVender(String elseVender) {
		this.elseVender = elseVender;
	}
	public String getVacciDate() {
		return vacciDate;
	}
	public void setVacciDate(String vacciDate) {
		this.vacciDate = vacciDate;
	}
	public String getVender() {
		return vender;
	}
	public void setVender(String vender) {
		this.vender = vender;
	}
	public String getPlaceVacci() {
		return placeVacci;
	}
	public void setPlaceVacci(String placeVacci) {
		this.placeVacci = placeVacci;
	}
	
		
}
