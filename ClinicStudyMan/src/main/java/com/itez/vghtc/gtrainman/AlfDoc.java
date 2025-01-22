package com.itez.vghtc.gtrainman;

import java.io.Serializable;

public class AlfDoc implements Comparable<AlfDoc>, Serializable {
	private static final long serialVersionUID = 1L;
	
	public int idx = 0;
	
	public String fileName = "";
	public String alfFolderId = "";
	public String alfFileId = "";
	public String updatedTime = "";
	public String eye = "";
	public String fileKb = "";
	public String versions = "";		//歷史版本數
	public String lastModifier = "";	//最後更新人
	
	@Override
	public int compareTo(AlfDoc other) {
		if(this.updatedTime.compareTo(other.updatedTime) < 0) {
			return 1;
		}else if(this.updatedTime.compareTo(other.updatedTime) >  0) {
			return -1;
		}else {
			return 0;
		}
	}
	
	public String getLastModifier() {
		return lastModifier;
	}
	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}
	public String getFileKb() {
		return fileKb;
	}
	public void setFileKb(String fileKb) {
		this.fileKb = fileKb;
	}
	public String getEye() {
		return eye;
	}
	public void setEye(String eye) {
		this.eye = eye;
	}
	public String getUpdatedTime() {
		return updatedTime;
	}
	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}
	public int getIdx() {
		return idx;
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getVersions() {
		return versions;
	}
	public void setVersions(String versions) {
		this.versions = versions;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAlfFolderId() {
		return alfFolderId;
	}
	public void setAlfFolderId(String alfFolderId) {
		this.alfFolderId = alfFolderId;
	}
	public String getAlfFileId() {
		return alfFileId;
	}
	public void setAlfFileId(String alfFileId) {
		this.alfFileId = alfFileId;
	}
}
