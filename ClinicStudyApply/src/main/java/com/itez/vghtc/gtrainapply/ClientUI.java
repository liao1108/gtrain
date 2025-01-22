package com.itez.vghtc.gtrainapply;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.google.gson.Gson;
import com.itez.vghtc.util.CaseData;
import com.itez.vghtc.util.CtxProperty;
import com.itez.vghtc.util.FrameFile;
import com.itez.vghtc.util.Gutil;
import com.itez.vghtc.util.MyException;
import com.itez.vghtc.util.Gutil.DeviceType;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.themes.ValoTheme;

import de.steinwedel.messagebox.MessageBox;
import io.minio.MinioClient;

import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@StyleSheet({"https://fonts.googleapis.com/css?family=Open+Sans%7CRaleway%7CPoppins"})
@Theme("mytheme")
public class ClientUI extends UI {
	private static final long serialVersionUID = 1L;
	
	CtxProperty ctxProp = new CtxProperty();
	//final String PROFILE_NAME = "Profile.json";
	
	DataSource ds = null;
	static String JNDI_DB = "java:comp/env/jdbc/seduDB";
	//File dirUpload = null;
	//String siteId = "clinicstudy";
	Gson gson = new Gson();
	//
	//InternetAddress fromAccount = null;
	//String strCcAccount = "";			//副本收信人
	//
	//DeviceType deviceType = DeviceType.Desktop;
	
	private SimpleDateFormat sdfss = new SimpleDateFormat("yyyyMMddHHmmss");
	private SimpleDateFormat sdfSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		try {
			ctxProp.setSiteId("clinicstudy");
			//
			ctxProp.setDeviceType(Gutil.getDeviceType(Page.getCurrent().getWebBrowser()));
			if(ctxProp.getDeviceType() == DeviceType.Mobile) {
				UI.getCurrent().setTheme("mytheme-mobile");
			}else if(ctxProp.getDeviceType() == DeviceType.Pad){
				UI.getCurrent().setTheme("mytheme-pad");
			}
			//Servlet初始參數
			Gutil.loadServletParameter(ctxProp,
					VaadinServlet.getCurrent().getServletContext());
			//
			ds = (DataSource) new InitialContext().lookup(JNDI_DB);
			//
			String formTitle = "Application for Clinical Study";
			Page.getCurrent().setTitle(formTitle + "(" + ctxProp.getDeviceType() + ")");    	   
			//主要 Layout
			final VerticalLayout layoutMain = new VerticalLayout();
			layoutMain.setMargin(false);
			layoutMain.setSpacing(false);
			layoutMain.setSizeFull();
			setContent(layoutMain);
			//抬頭列
			HorizontalLayout hlTitle = new HorizontalLayout();
			if(ctxProp.getDeviceType() == DeviceType.Mobile) {
				this.createBannerLayout(hlTitle, formTitle, true);
			}else {
				this.createBannerLayout(hlTitle, formTitle, false);
			}
			layoutMain.addComponent(hlTitle);
			//
			VerticalLayout layoutHero = new VerticalLayout();
			layoutHero.setSizeFull();
			layoutHero.setMargin(false);
			layoutHero.setStyleName("halftone-snow");
			layoutMain.addComponent(layoutHero);
			layoutMain.setExpandRatio(layoutHero, 1);
			//URL參數
			//boolean isAdmin = false;
			String docId = "";
			Iterator<String> it = vaadinRequest.getParameterMap().keySet().iterator();
			while(it.hasNext()){
				//String param = Encode.forHtml(it.next());
				String param = it.next();
				//String val = Encode.forHtml(vaadinRequest.getParameter(param));
				//String val = vaadinRequest.getParameter(param);
				if(param.equalsIgnoreCase("docId")){
					docId = vaadinRequest.getParameter(param);
				}
			}
			//嘗試啟動 ResteasyClient 兩次
			try {
				//client = Gutil.getResteasyClient();
			}catch(Exception ex) {
			}
			//
			this.startApply(layoutHero, docId);
		}catch(Exception ex) {
    	   Gutil.handleException(ex);
       }
    }
	
	private void startApply(VerticalLayout layoutHero, String caseId) throws Exception{
		final Window window = new Window(" Sign In");
		window.setWidth(380, Unit.PIXELS);
		window.setHeight(400, Unit.PIXELS);
		if(ctxProp.getDeviceType() == DeviceType.Pad || ctxProp.getDeviceType() == DeviceType.Mobile) {
			window.setWidth(500, Unit.PIXELS);
			window.setHeight(740, Unit.PIXELS);
		}
		window.setModal(false);
		window.setClosable(false);
		window.center();
		window.setIcon(VaadinIcons.USER_CHECK);
		//
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(false);
		layout.setSpacing(true);
		layout.addStyleName("halftone-whip");
		{
			VerticalLayout vLayout = new VerticalLayout();
			vLayout.setSizeFull();
			vLayout.setMargin(true);
			vLayout.setSpacing(true);
			{
				final TextField txtName = new TextField("Your name in English");
				txtName.setRequiredIndicatorVisible(true);
				txtName.setWidth(100, Unit.PERCENTAGE);
				vLayout.addComponent(txtName);
				
				TextField txtPassport = new TextField("Your Passport number");
				txtPassport.setRequiredIndicatorVisible(true);
				txtPassport.setWidth(100, Unit.PERCENTAGE);
				vLayout.addComponent(txtPassport);
				//layout.setComponentAlignment(txtPassport, Alignment.MIDDLE_CENTER);
				
				ProgressBar spinner = new ProgressBar();
				spinner.setIndeterminate(true);
				spinner.setVisible(false);
				layoutHero.addComponent(spinner);
				layoutHero.setComponentAlignment(spinner, Alignment.MIDDLE_CENTER);
				//
				Button btnStart = new Button("Sign In");
				layout.addComponent(btnStart);
				layout.setComponentAlignment(btnStart, Alignment.MIDDLE_CENTER);
				btnStart.addStyleName(ValoTheme.BUTTON_PRIMARY);
				btnStart.addClickListener(e ->{
					Connection conn = null;
					try {
						if(txtName.getValue().trim().isEmpty()
								|| txtPassport.getValue().trim().isEmpty())
							throw new MyException("Please enter your name in English and Passport number.");
		            	String passportNo = txtPassport.getValue().trim();
		            	//檢查是否已申請
		            	conn = ds.getConnection();
					    CaseData theCase = Gutil.getCaseByPassport(passportNo, ctxProp.getSiteId(), conn);
					    if(!theCase.getCaseNo().isEmpty()) {
					    	//String stageFullName = Gutil.getStageName(theCase.getCaseNo());
					    	if(!theCase.getStageNo().equals("A")				//A
					    			&& !theCase.getStageNo().equals("B")		//B
					    			&& !theCase.getStageNo().equals("D")		//D
					    			&& !theCase.getStageNo().equals("E"))		//E
					    		throw new MyException("Your case is under processing or archived, please contact our administrator for further information. ");
					    	//
					    	if((theCase.getStageNo().equals("D") || theCase.getStageNo().equals("E"))
					        		&& !Gutil.getPureNodeId(theCase.getCaseId()).equalsIgnoreCase(caseId))
					    		throw new MyException("Please use the url link in the notice mail to login.");
					    }
					    //
					    if(!theCase.getPersonNameEn().isEmpty()
					       			&& !theCase.getPersonNameEn().equalsIgnoreCase(txtName.getValue().trim()))
							throw new MyException("Passport number is not matching English name You left last time !");
					    //
					    theCase.setPersonNameEn(txtName.getValue().trim());
					    //        	
					    window.close();
					    //
					    ctxProp.setAdmin(false);
					    if(!caseId.isEmpty()) {
					    	this.openFlightForm(layoutHero, ctxProp, ctxProp.isAdmin(), theCase);
					    }else if(theCase.getStageNo().isEmpty()
					       			|| theCase.getStageNo().equals("A")		//暫存區		
					       			|| theCase.getStageNo().equals("B")){	//補件區
					    	Gutil.openApplyForm(layoutHero,
					    						ctxProp,
					    						theCase,
					    						conn,
					    						ds); 
					    }
					}catch(Exception ex) {
						Gutil.handleException(ex);
					}finally {
						Gutil.close(conn);
					}
				});
				vLayout.addComponent(btnStart);
			}
			layout.addComponent(vLayout);
		}
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}

	private void createBannerLayout(HorizontalLayout hlTitle,
			String formTitle,
			boolean isMobile) {
		hlTitle.setWidth(98, Unit.PERCENTAGE);
		//if(wideLogo) hlTitle.setHeight(60, Unit.PIXELS);
		hlTitle.setMargin(false);
		hlTitle.setSpacing(true);
		{
			Label label = new Label();
			label.setWidth(16, Unit.PIXELS);
			hlTitle.addComponent(label);
			//
			com.vaadin.ui.Image imgIcon = new com.vaadin.ui.Image();
			imgIcon.setSource(new ClassResource("/logo.png"));

			hlTitle.addComponent(imgIcon);
			hlTitle.setComponentAlignment(imgIcon, Alignment.MIDDLE_LEFT);

			label = new Label();
			if(!isMobile) {
				label.setValue("<h3><Strong>" + formTitle + "</strong></h3>");
				label.setContentMode(ContentMode.HTML);
			}
			hlTitle.addComponent(label);
			hlTitle.setComponentAlignment(label, Alignment.MIDDLE_LEFT);
			hlTitle.setExpandRatio(label, 1);
		}
	}
	
	private void openFlightForm(VerticalLayout layoutHero,
			CtxProperty ctxProp,
			boolean isAdmin,
			CaseData theCase) throws Exception{
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
		sdfss.setTimeZone(timeZone);
		sdfSSS.setTimeZone(timeZone);
		//
		File dirCurr = new File(".");
		File dirUpload = new File(dirCurr.getCanonicalPath() + "/" + "upload");
		if(!dirUpload.exists()) dirUpload.mkdirs();
		//
		Panel panel = new Panel();
		panel.setSizeFull();
		
		//載入 案件相關檔案
		HashMap<String, PDDocument> mapFramePdf = new HashMap<>();
		//
		FlightUploadForm form = new FlightUploadForm();
		//
		List<String> listHeader = new ArrayList<>();
		listHeader.add(Gutil.FILE_HEADER_FLIGHT);
		listHeader.add(Gutil.FILE_HEADER_INSURANCE);
		//
		HashMap<String, FrameFile> mapFrameFile = new HashMap<>();
		for(String headerName: listHeader) {
			FrameFile ff = new FrameFile();
			ff.setHeaderName(headerName);
			ff.setPdfDoc(Gutil.getPdfDocument(headerName, mapFramePdf));
			//
			mapFrameFile.put(headerName, ff);
		}
		//
		form.frameFlight.setId(Gutil.FILE_HEADER_FLIGHT);
		form.frameInsurance.setId(Gutil.FILE_HEADER_INSURANCE);
		
		List<BrowserFrame> listFrame = new ArrayList<>();
		listFrame.add(form.frameFlight);
		listFrame.add(form.frameInsurance);
		
		for(BrowserFrame frame: listFrame) {
			FrameFile frameFile = mapFrameFile.get(frame.getId());
			frameFile.setHeaderName(frame.getId());
			//
			Gutil.addUploaderToFrame(frame, frameFile, dirUpload);
			//載入Pdf
			//if(mapFramePdf != null && !mapFramePdf.isEmpty()) {
				for(String headerName: mapFrameFile.keySet()) {
					if(headerName.startsWith(frame.getId())) {
						FrameFile ff = mapFrameFile.get(headerName);
						if(ff.getPdfDoc() != null) {
							StreamSource source = new StreamSource(){
								private static final long serialVersionUID = 1L;
								public java.io.InputStream getStream(){
						        	try{
						        		ByteArrayOutputStream baos = new ByteArrayOutputStream();
						        		ff.getPdfDoc().save(baos);
						        		return new ByteArrayInputStream(baos.toByteArray());
						        	} catch (Exception _ex){
						        		_ex.printStackTrace();
						        	}
						        	return null;
						        }
						    };
						    //
						    String fileName = frame.getId() + "_" + theCase.getPersonNameEn() + ".pdf";
						    StreamResource resource = new StreamResource(source, fileName);
						    resource.setMIMEType("application/pdf");
							frame.setSource(resource);
						}						
						//
						break;
					}
				}
			//}			
		}
		//
		form.btnSubmit.addClickListener(e ->{
			try {
				//檢查 必要檔案 是否上傳
				List<String> listNeed = new ArrayList<>();
				listNeed.add(Gutil.FILE_HEADER_FLIGHT);
				listNeed.add(Gutil.FILE_HEADER_INSURANCE);
				
				String strLack = "";
				for(String fileHeader: listNeed) {
					if(mapFrameFile.get(fileHeader).getPdfDoc() == null) {
						if(!strLack.isEmpty()) strLack += ",";
						strLack += fileHeader;
					}
				}
				if(!strLack.isEmpty() && !isAdmin)
					throw new MyException("Please upload the necessary files (" + strLack + ") before submittion.");
				//
				String msg = "Are you sure to submit the uploads now ?";
				//
				MessageBox.createQuestion().withCaption("Submit")
				.withMessage(msg)
				.withYesButton(() ->{
					try {
						//上傳檔案
						HashMap<String, PDDocument> mapToUpload = new HashMap<>();
						for(String pdfName: mapFrameFile.keySet()) {
							FrameFile ff = mapFrameFile.get(pdfName);
							if(!ff.isUpdated()) continue;
							if(ff.getPdfDoc() != null) {
								mapToUpload.put(pdfName, ff.getPdfDoc());
							}
						}
						//if(client == null) client = Gutil.getResteasyClient();
						Gutil.postFlighPdfs(ctxProp, mapToUpload, theCase.getCaseId());
						//
						MessageBox.createInfo().withCaption("Submition result")
						.withMessage("Thanks for your uploads, we are looking forward to your visiting.")
						.withOkButton(()->{
							try {
								java.net.URI uri = Page.getCurrent().getLocation();
								java.net.URI uriDest = new java.net.URI(uri.getScheme(),
																		uri.getAuthority(),
																		uri.getPath(),
																		null, // Ignore the query part of the input url
							                							uri.getFragment());
							    UI.getCurrent().getPage().setLocation(uriDest);
							}catch(Exception ex) {
								Gutil.handleException(ex);
							}
						}).open();
					}catch(Exception ex) {
						Gutil.handleException(ex);
					}
				})
				.withCancelButton()
				.open();
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
		//
		panel.setContent(form);
		layoutHero.addComponent(panel);
	}
	
	
	
	@WebServlet(urlPatterns = "/*", name = "ClinicStudyServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ClientUI.class, productionMode = false)
    public static class MenuUIServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
    }
}