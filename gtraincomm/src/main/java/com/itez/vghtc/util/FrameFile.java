package com.itez.vghtc.util;

import org.apache.pdfbox.pdmodel.PDDocument;

public class FrameFile implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	private String headerName = "";
	private PDDocument pdfDoc = null;
	private boolean updated = false;
	
	public boolean isUpdated() {
		return updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}
	public PDDocument getPdfDoc() {
		return pdfDoc;
	}
	public void setPdfDoc(PDDocument pdfDoc) {
		this.pdfDoc = pdfDoc;
	}
	
}
