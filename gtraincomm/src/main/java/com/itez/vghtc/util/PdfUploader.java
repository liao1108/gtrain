package com.itez.vghtc.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.itez.vghtc.util.FrameFile;
import com.itez.vghtc.util.Gutil;
import com.itez.vghtc.util.MyException;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class PdfUploader implements Receiver, SucceededListener {
	private static final long serialVersionUID = 1L;
	//
	private File file = null;
	private com.vaadin.ui.BrowserFrame frame = null;
    private FrameFile frameFile = null;
    private File dirUpload = null;
    private SimpleDateFormat sdfSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    
    public PdfUploader(com.vaadin.ui.BrowserFrame frame,
    		FrameFile frameFile,
    		File dirUpload){
    	this.frame = frame;
    	this.frameFile = frameFile;
    	this.dirUpload = dirUpload;
    }
    //
    public OutputStream receiveUpload(String fileName, String mimeType) {
    	FileOutputStream fos = null; // Stream to write to
        try{
        	int idx = fileName.lastIndexOf(".");
        	String pureName = fileName.substring(0,  idx);
        	String extName = fileName.substring(idx + 1);
        	//
			String _name = pureName + "_" + sdfSSS.format(new Date()) + "." + extName;
			file = new File(dirUpload.getCanonicalPath() + "\\" + _name);
        	fos = new FileOutputStream(file);
        }catch (Exception ex) {
        	Gutil.handleException(ex);
            return null;
        }
        return fos; // Return the output stream to write to
    }

    public void uploadSucceeded(SucceededEvent event) {
    	try {
    		if(!Gutil.isPdfFile(file.getName()) 
    				&& !Gutil.isImageFile(file.getName())) {
    			file.delete();
				throw new MyException("Only pdf,png or jpg file format acceptable.");
    		}
    		//
    		//FileInputStream fis = new FileInputStream(file);
    		//
    		if(frameFile.getPdfDoc() != null) {
    			PDFMergerUtility pdfMerger = new PDFMergerUtility();
    			PDDocument doc = null;
    			if(Gutil.isPdfFile(file.getName())){
    				doc = PDDocument.load(file);
    			}else {
    				doc = this.convertImageToPdf(file);
    			}
    			pdfMerger.appendDocument(frameFile.getPdfDoc(), doc);
    		}else {
    			if(Gutil.isPdfFile(file.getName())){
    				frameFile.setPdfDoc(PDDocument.load(file));
    			}else {
    				frameFile.setPdfDoc(this.convertImageToPdf(file));
    			}
    		}
    		//標記有修改
    		frameFile.setUpdated(true);
    		//
    		StreamSource source = new StreamSource(){
				private static final long serialVersionUID = 1L;
				public java.io.InputStream getStream(){
		        	try{
		        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        		frameFile.getPdfDoc().save(baos);
		        		return new ByteArrayInputStream(baos.toByteArray());
		        	} catch (Exception _ex){
		        		_ex.printStackTrace();
		        	}
		        	return null;
		        }
		    };
            //
		    String fileName = frameFile.getHeaderName() + "_" + sdfSSS.format(new java.util.Date());
		    StreamResource resource = new StreamResource(source, fileName);
		    resource.setMIMEType("application/pdf");
		    //
			frame.setSource(resource);
			//streamNow.close();
        } catch (Exception ex) {
        	Gutil.handleException(ex);
        }
    }

    private PDDocument convertImageToPdf(File fileImage) throws Exception{
    	PDDocument doc = new PDDocument();
    	InputStream is = new FileInputStream(fileImage);
    	BufferedImage bi = ImageIO.read(is);
    	float width = bi.getWidth();
    	float height = bi.getHeight();
    	PDPage page = new PDPage(new PDRectangle(width, height));
    	doc.addPage(page);
    	
    	PDImageXObject pdImage = PDImageXObject.createFromFile(fileImage.getCanonicalPath(), doc);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
    	contentStream.drawImage(pdImage, 0, 0);
    	contentStream.close();
    	is.close();
    	//
    	return doc;
    }
    
    private ByteArrayOutputStream mergePdf(List<InputStream> listIS) throws Exception{
    	PDFMergerUtility pdfMerger = new PDFMergerUtility();
    	//
    	PDDocument doc = PDDocument.load(listIS.get(0));
    	if(listIS.size() > 1) {
    		for(int i=1; i < listIS.size(); i++) {
    			PDDocument doc2 = PDDocument.load(listIS.get(i));
    			pdfMerger.appendDocument(doc, doc2);
    		}
    	}
    	//
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	doc.save(baos);
    	return baos;
    }
    
}
