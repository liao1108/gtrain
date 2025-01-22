package com.itez.vghtc.gtrainman;

import java.io.Serializable;

public class MinioDoc implements Comparable<MinioDoc>, Serializable {
	private static final long serialVersionUID = 1L;
	
	public int idx = 0;
	
	public String pureName = "";
	public String objectName = "";
	public String updatedTime = "";
	public String eye = "";
	public String fileKb = "";
	public String versions = "";		//歷史版本數
	public String lastModifier = "";	//最後更新人
	
	@Override
	public int compareTo(MinioDoc other) {
		if(this.updatedTime.compareTo(other.updatedTime) < 0) {
			return 1;
		}else if(this.updatedTime.compareTo(other.updatedTime) >  0) {
			return -1;
		}else {
			return 0;
		}
	}
	
	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public String getPureName() {
		return pureName;
	}

	public void setPureName(String pureName) {
		this.pureName = pureName;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(String updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getEye() {
		return eye;
	}

	public void setEye(String eye) {
		this.eye = eye;
	}

	public String getFileKb() {
		return fileKb;
	}

	public void setFileKb(String fileKb) {
		this.fileKb = fileKb;
	}

	public String getVersions() {
		return versions;
	}

	public void setVersions(String versions) {
		this.versions = versions;
	}

	public String getLastModifier() {
		return lastModifier;
	}

	public void setLastModifier(String lastModifier) {
		this.lastModifier = lastModifier;
	}

}
