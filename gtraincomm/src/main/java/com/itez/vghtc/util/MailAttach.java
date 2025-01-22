package com.itez.vghtc.util;

import java.io.InputStream;

public class MailAttach {
	private String fileName = "";
	private InputStream inStream = null;
	
	public MailAttach(String fileName, InputStream inStream) {
		this.fileName = fileName;
		this.inStream = inStream;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	
}
