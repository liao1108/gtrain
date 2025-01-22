package com.itez.vghtc.util;

import java.util.ArrayList;
import java.util.List;

public class CaseData implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private int idx = 0;

	private String caseNo = "";
	private String caseDate = "";

	private String personNameEn = "";
	private String personNameCh = "";
	private String passportNo = "";
	private String nationality = "";
	private String gender = "";
	private String birthday = "";
	private String roleStatus = "";
	private String elseRole = "";
	private String affiliation = "";
	private String schoolLocation = "";
	
	private String chListening = "";
	private String chSpeaking = "";
	private String chReading = "";
	private String chWriting = "";
	
	private String email = "";
	private String mailAddress = "";
	private String phoneNo = "";
	
	private String contactName = "";
	private String contactRelation = "";
	private String contactAddress = "";
	private String contactPhone = "";
	
	private String trainGoal = "";		//Your training goals
	private String interestField = "";		//Your Interest fields
	
	private String accommType = "No Need";		//住宿選擇
	
	//private String photoDocId = "";			//大頭照 alfresco id
	
	private int tuition = 0;
	
	//PDF檔案與 alf_doc_id 的對照
	//private HashMap<String, String> mapPdfWithId = new HashMap<>();

	private List<DiviChoice> listChoice = new ArrayList<>();	//進修部科別
	private List<Vacci> listVacci = new ArrayList<>();			//COVID-19接種記錄	
	
	private String caseId = "";
	private String stageNo = "";
	
	private String udTakerNames = "";		//訓練單位承辦人
	private String udTakerEmails = "";		//訓練單位承辦人
	
	private boolean agreeByVghTc = false;
	private String rejectReason = "";		//不同意原因
	
	
	
	public boolean isAgreeByVghTc() {
		return agreeByVghTc;
	}

	public void setAgreeByVghTc(boolean agreeByVghTc) {
		this.agreeByVghTc = agreeByVghTc;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getUdTakerNames() {
		return udTakerNames;
	}

	public void setUdTakerNames(String udTakerNames) {
		this.udTakerNames = udTakerNames;
	}

	public String getUdTakerEmails() {
		return udTakerEmails;
	}

	public void setUdTakerEmails(String udTakerEmails) {
		this.udTakerEmails = udTakerEmails;
	}

	

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getTuition() {
		return tuition;
	}

	public void setTuition(int tuition) {
		this.tuition = tuition;
	}

	public String getTrainGoal() {
		return trainGoal;
	}

	public void setTrainGoal(String trainGoal) {
		this.trainGoal = trainGoal;
	}

	public String getInterestField() {
		return interestField;
	}

	public void setInterestField(String interestField) {
		this.interestField = interestField;
	}


	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getCaseDate() {
		return caseDate;
	}

	public void setCaseDate(String caseDate) {
		this.caseDate = caseDate;
	}

	public String getPersonNameEn() {
		return personNameEn;
	}

	public void setPersonNameEn(String personNameEn) {
		this.personNameEn = personNameEn;
	}

	public String getPersonNameCh() {
		return personNameCh;
	}

	public void setPersonNameCh(String personNameCh) {
		this.personNameCh = personNameCh;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getRoleStatus() {
		return roleStatus;
	}

	public void setRoleStatus(String roleStatus) {
		this.roleStatus = roleStatus;
	}

	public String getElseRole() {
		return elseRole;
	}

	public void setElseRole(String elseRole) {
		this.elseRole = elseRole;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getSchoolLocation() {
		return schoolLocation;
	}

	public void setSchoolLocation(String schoolLocation) {
		this.schoolLocation = schoolLocation;
	}

	public String getChListening() {
		return chListening;
	}

	public void setChListening(String chListening) {
		this.chListening = chListening;
	}

	public String getChSpeaking() {
		return chSpeaking;
	}

	public void setChSpeaking(String chSpeaking) {
		this.chSpeaking = chSpeaking;
	}

	public String getChReading() {
		return chReading;
	}

	public void setChReading(String chReading) {
		this.chReading = chReading;
	}

	public String getChWriting() {
		return chWriting;
	}

	public void setChWriting(String chWriting) {
		this.chWriting = chWriting;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getPhoneNo() {
		return phoneNo;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactRelation() {
		return contactRelation;
	}

	public void setContactRelation(String contactRelation) {
		this.contactRelation = contactRelation;
	}

	public String getContactAddress() {
		return contactAddress;
	}

	public void setContactAddress(String contactAddress) {
		this.contactAddress = contactAddress;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getAccommType() {
		return accommType;
	}

	public void setAccommType(String accommType) {
		this.accommType = accommType;
	}

	public List<DiviChoice> getListChoice() {
		return listChoice;
	}

	public void setListChoice(List<DiviChoice> listChoice) {
		this.listChoice = listChoice;
	}

	public List<Vacci> getListVacci() {
		return listVacci;
	}

	public void setListVacci(List<Vacci> listVacci) {
		this.listVacci = listVacci;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getStageNo() {
		return stageNo;
	}

	public void setStageNo(String stageNo) {
		this.stageNo = stageNo;
	}
		
}
