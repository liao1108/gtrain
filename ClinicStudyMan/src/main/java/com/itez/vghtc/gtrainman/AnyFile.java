package com.itez.vghtc.gtrainman;

import java.io.Serializable;

public class AnyFile implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String fileName = "";
	public String alfDocId = "";
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getAlfDocId() {
		return alfDocId;
	}
	public void setAlfDocId(String alfDocId) {
		this.alfDocId = alfDocId;
	}
	
	
}
