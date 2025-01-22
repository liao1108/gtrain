package com.itez.vghtc.gtrainman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.aspose.words.SaveFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itez.minioutils.MinioUtils;
import com.itez.vghtc.util.CaseData;
import com.itez.vghtc.util.CtxProperty;
import com.itez.vghtc.util.Dept;
import com.itez.vghtc.util.Divi;
import com.itez.vghtc.util.Gutil;
import com.itez.vghtc.util.MyException;
import com.itez.vghtc.util.Nation;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;

class MohwUploader implements Upload.Receiver, Upload.SucceededListener {
	private static final long serialVersionUID = -1L;
	private File file;
	private File dirUpload;
	private TextField txtFileName;
	private CaseData theCase = null;
	private CtxProperty ctxProp = null;
	
    public MohwUploader(CaseData theCase,
    		TextField txtFileName,
    		File dirUpload,
    		CtxProperty ctxProp) {
    	this.theCase = theCase;
    	this.txtFileName = txtFileName;
    	this.dirUpload = dirUpload;
    	this.ctxProp = ctxProp;
    }
	
    public OutputStream receiveUpload(String fileName, String mimeType) {
    	FileOutputStream fos = null; // Stream to write to
        try{
			file = new File(dirUpload.getCanonicalPath() + "\\" + fileName);
        	fos = new FileOutputStream(file);
        }catch (Exception ex) {
        	Gutil.handleException(ex);
        	return null;
        }
        return fos; // Return the output stream to write to
    }
    
	public void uploadSucceeded(SucceededEvent event) {
		Connection conn = null;
		try{
			if(!Gutil.isWordFile(file.getName())
					&& !Gutil.isPdfFile(file.getName())) {
				file.delete();
				throw new MyException("請以 MS Word 或 PDF 類型檔案(副檔名 docx、pdf)上傳。");
			}
			//
			ByteArrayOutputStream baos = null;
			String fileNameDest = Gutil.FILE_HEADER_MOHW_APPLY + "_" + theCase.getPersonNameEn();
			if(Gutil.isWordFile(file.getName())) {
				com.aspose.words.Document docWord = new com.aspose.words.Document(file.getCanonicalPath());
				baos = new ByteArrayOutputStream();
				docWord.save(baos, SaveFormat.DOCX);
				fileNameDest += ".docx";
			}else {
				byte[] bytes = IOUtils.toByteArray(new FileInputStream(file));
				baos = new ByteArrayOutputStream(bytes.length);
				baos.write(bytes, 0, bytes.length);
				fileNameDest += ".pdf";
			}
			//MyUtil.saveToAlfresco(file, fileNameDest, fdCase, alfSession);
			MinioUtils.uploadObject(ctxProp.getSiteId(), theCase.getCaseId() + "/" + fileNameDest, file, ctxProp.getMinioClient());
			txtFileName.setValue(fileNameDest);
			baos.close();
    	}catch(Exception ex){
    		Gutil.handleException(ex);
    	}finally {
    		Gutil.close(conn);
    	}
    }
	
}

