package com.itez.vghtc.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.DataHandler;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.servlet.ServletContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.PassthroughTrustManager;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itez.minioutils.MinioUtils;
import com.itez.vghtc.util.Gutil.DeviceType;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.ValidationResult;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.server.WebBrowser;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.server.Sizeable.Unit;

import de.steinwedel.messagebox.MessageBox;
import io.minio.MinioClient;
//import io.minio.MinioClient;
import io.minio.messages.Item;

public class Gutil {

	public static SimpleDateFormat sdfss = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat sdfSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

	public static final String COOKIE_NAME = "GlobalClinicStudy";
	public static final String PROFILE_NAME = "Profile.json";
	
	public static enum DeviceType {
	    Desktop, Pad, Mobile
	}
	
	public static final String FILE_HEADER_PHOTO = "大頭照";
	
	public static final String FILE_HEADER_PASSPORT = "個人護照";
	public static final String FILE_HEADER_LICENSURE = "專業證書";
	public static final String FILE_HEADER_DIPLOMA = "學歷證明";
	public static final String FILE_HEADER_RECOMMEND = "工作證明（推薦信）";
	public static final String FILE_HEADER_CV = "個人履歷";
	public static final String FILE_HEADER_HEALTHCHECK = "體檢報告";
	public static final String FILE_HEADER_FLIGHT = "飛航資訊";
	public static final String FILE_HEADER_INSURANCE = "旅平險";
	
	public static final String FILE_HEADER_COVID = "Covid19施打證明";
	//
	public static final String PARAM_NATION = "國別列表";
	public static final String PARAM_CLINIC_ORG = "部科別列表";
	
	public static final String FILE_HEADER_MOHW_APPLY = "報衛福部申請書";
	
	public static List<String> listVacciVender = 
			Arrays.asList(new String[]{
					"AstraZeneca(AZ)",
					"Moderna",
					"Pfizer/BioNTech(BNT)",
					"Janssen",
					"Sinopharm",
					"Sinovac",
					"Medigen",
					"Other"});	//COVID-19 疫苗廠牌
	
	public static List<String> listStage = 
			Arrays.asList(new String[]{
					"A.暫存區",
					"B.補件區",
					"C.收件區",
					"D.同意區",
					"E.待報到區",
					"F.已報到區",
					"R.退件區",
					"Y.歸檔區",
					"Z.不區分"
			});
	
	/*
	private static ResteasyClient getResteasyClient() throws Exception{
		//TrustStrategy acceptAll = new TrustStrategy() {
	    //    public boolean isTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {
	    //        return true;
	    //    }
	    //};
	    //SSLContextBuilder sslBuilder = new SSLContextBuilder();
	    //sslBuilder.loadTrustMaterial(acceptAll);
	    //return (ResteasyClient)ClientBuilder.newBuilder().sslContext(sslBuilder.build()).build();
	    
		return (ResteasyClient)ClientBuilder.newClient();
	}
	*/
	
	public static String getDocumentNameEN(String docNameCh) {
		if(docNameCh.equals(FILE_HEADER_PHOTO)){
			return "Photo";
		}else if(docNameCh.equals(FILE_HEADER_PASSPORT)) {
			return "Passport";
		}else if(docNameCh.equals(FILE_HEADER_LICENSURE)) {
			return "Professional Licensure";
		}else if(docNameCh.equals(FILE_HEADER_DIPLOMA)) {
			return "Diploma";
		}else if(docNameCh.equals(FILE_HEADER_RECOMMEND)) {
			return "Recommendation Letter";
		}else if(docNameCh.equals(FILE_HEADER_CV)) {
			return "Curriculum Vitae";
		}else if(docNameCh.equals(FILE_HEADER_HEALTHCHECK)) {
			return "Health Check Report";
		}else if(docNameCh.equals(FILE_HEADER_FLIGHT)) {
			return "Flight Schedule";
		}else if(docNameCh.equals(FILE_HEADER_INSURANCE)) {
			return "Travel Insurance";
		}else if(docNameCh.equals(FILE_HEADER_COVID)) {
			return "Proof of Covid-19 Vaccination";
		}else {
			return docNameCh;
		}
	}
	
	public static String getStageName(String stageNo) throws Exception{
		for(String stageFullName: Gutil.listStage) {
			if(stageFullName.startsWith(stageNo)) {
				return stageFullName;
			}
		}
		return "";
	}
	
	public static ResteasyClient getResteasyClient() throws Exception{
		//SSLContext sslContext = SSLContext.getInstance("SSL");
	    //sslContext.init(null, 
	    //        		new TrustManager[] { new PassthroughTrustManager() },
	    //        		new SecureRandom());
	    //    
		//return (ResteasyClient)ClientBuilder.newBuilder().sslContext(sslContext).build();
		ResteasyClient client = null;
		try {
			ClientBuilder clientBuilder = ClientBuilder.newBuilder();
			clientBuilder.connectTimeout(30, TimeUnit.SECONDS);
			clientBuilder.readTimeout(120, TimeUnit.SECONDS);
			client = (ResteasyClient)clientBuilder.build();
			//client = (ResteasyClient)ClientBuilder.newBuilder().build();
			//client = (ResteasyClient)ClientBuilder.newClient();
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		if(client == null)
			throw new MyException("Our web service is currently busy, please re-try again.");
		//
		return client;
	}
	
	public static DeviceType getDeviceType(WebBrowser browser) {
		DeviceType deviceType = DeviceType.Desktop;
		if(browser.isTouchDevice()){
			//System.out.println("Current device is touchable.");
			if(browser.isIPhone()
					|| browser.isWindowsPhone()) {
				deviceType = DeviceType.Mobile;
			}else if(browser.isIPad()){
				deviceType = DeviceType.Pad;
			}else{
				double ratio = 0.0d;
				if(browser.getScreenWidth() > browser.getScreenHeight()) {
					ratio = (double)browser.getScreenHeight() / (double)browser.getScreenWidth();
				}else {
					ratio = (double)browser.getScreenWidth() / (double)browser.getScreenHeight();
				}
				System.out.println("Device screen size ratio: " + ratio);
				
				if(ratio > 0.6d) {
					deviceType = DeviceType.Pad;
				}else {
					deviceType = DeviceType.Mobile;
				}
			}
		}
		return deviceType;
	}
	
	public static void loadServletParameter(CtxProperty ctxProp,
			ServletContext servletContext) throws Exception{
		if(servletContext.getInitParameter("minio-url") != null) 
			ctxProp.setMinioHost(servletContext.getInitParameter("minio-url"));
		if(servletContext.getInitParameter("minio-user") != null)
			ctxProp.setMinioAgentAcc(servletContext.getInitParameter("minio-user"));
		if(servletContext.getInitParameter("minio-passwd") != null)
			ctxProp.setMinioAgentPwd(servletContext.getInitParameter("minio-passwd"));
		
		if(servletContext.getInitParameter("smtp-host") != null)
			ctxProp.setSmtpHost(servletContext.getInitParameter("smtp-host"));
		if(servletContext.getInitParameter("smtp-port") != null)
			ctxProp.setSmtpPort(servletContext.getInitParameter("smtp-port"));
		if(servletContext.getInitParameter("smtp-starttls") != null) {
			String startTls = servletContext.getInitParameter("smtp-starttls");
			if(startTls != null && !startTls.trim().isEmpty()) {
				ctxProp.setStarttls(startTls);
			}
		}
		if(servletContext.getInitParameter("smtp-auth") != null) 
			ctxProp.setSmtpAuth(Boolean.valueOf(servletContext.getInitParameter("smtp-auth")));
		if(servletContext.getInitParameter("smtp-user") != null)
			ctxProp.setSmtpUser(servletContext.getInitParameter("smtp-user"));
		if(servletContext.getInitParameter("smtp-passwd") != null)
			ctxProp.setSmtpPwd(servletContext.getInitParameter("smtp-passwd"));
		if(servletContext.getInitParameter("smtp-frommail") != null)
			ctxProp.setSmtpFromMail(servletContext.getInitParameter("smtp-frommail"));
		
		//
		ctxProp.setMinioClient(MinioUtils.getMinioClient(ctxProp.getMinioHost(),
				ctxProp.getMinioAgentAcc(), 
				ctxProp.getMinioAgentPwd()));
		//讀取設定檔
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(),
													true,
													"設定區",
													ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			//
			if(item.objectName().toLowerCase().endsWith("txt")
					&& item.objectName().contains("郵件帳號")) {
				InputStream is = MinioUtils.getObjectStream(ctxProp.getSiteId(), item.objectName(), ctxProp.getMinioClient());
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		        String line;
		        while ((line = br.readLine()) != null) {
		        	if(!line.contains("=")) continue;
		            if(line.contains("發信帳號")) {
		            	ctxProp.setFromAccount(new InternetAddress(line.substring(line.indexOf("=") + 1).trim(), "Taichung Veterans General Hospital"));
		            }else if(line.contains("副本帳號")) {
		            	ctxProp.setCcAccount(line.substring(line.indexOf("=") + 1).trim());
		            }
		        }
		        br.close();
			}			
		}
		//
		if(ctxProp.getMinioHost().isEmpty() ||
				ctxProp.getMinioAgentAcc().isEmpty() ||
				ctxProp.getMinioAgentPwd().isEmpty()) 
			throw new MyException("後台初始設定尚未設定完成。");
	}
	
	public static StreamResource createStreamResource(BufferedImage bi) {
	    return new StreamResource(new StreamSource() {
			private static final long serialVersionUID = 1L;
			@Override
	        public InputStream getStream() {
	            try {
	                ByteArrayOutputStream bos = new ByteArrayOutputStream();
	                ImageIO.write(bi, "png", bos);
	                return new ByteArrayInputStream(bos.toByteArray());
	            } catch (IOException e) {
	                e.printStackTrace();
	                return null;
	            }
	        }
	    }, "image-" + sdfss.format(new java.util.Date())+ ".png");
	}
	
	public static Settings getSettings(Connection conn) throws Exception{
		Settings settings = new Settings();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//國別
		String sql = "SELECT str_json FROM tams_param WHERE param_name = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, Gutil.PARAM_NATION);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			settings.setListNation(gson.fromJson(rs.getString(1), 
												new TypeToken<List<Nation>>(){}.getType()));
		}
		rs.close();
		ps.close();
		//
		List<Divi> listDivi = new ArrayList<>();
		sql = "SELECT str_json FROM tams_param WHERE param_name = ?";
		ps = conn.prepareStatement(sql);
		ps.setString(1, Gutil.PARAM_CLINIC_ORG);
		rs = ps.executeQuery();
		if(rs.next()) {
			listDivi = gson.fromJson(rs.getString(1), 
										new TypeToken<List<Divi>>(){}.getType());
		}
		rs.close();
		ps.close();
		//
		LinkedHashMap<String, Dept> mapDept = new LinkedHashMap<>();
		LinkedHashMap<String, List<Divi>> mapDeptDivi = new LinkedHashMap<>();
		
		for(Divi divi: listDivi) {
			Dept dept = new Dept();
			dept.setDeptNameEn(divi.getDeptNameEn());
			dept.setDeptNameCh(divi.getDeptNameCh());
			//
			if(!mapDept.containsKey(dept.getDeptNameEn()))
				mapDept.put(dept.getDeptNameEn(), dept);
			
			List<Divi> listDivi2 = new ArrayList<>();
			if(mapDeptDivi.containsKey(dept.getDeptNameEn()))
				listDivi2 = mapDeptDivi.get(dept.getDeptNameEn());
			boolean existed = false;
			for(Divi divi2: listDivi2) {
				if(divi.getDivNameEn().equalsIgnoreCase(divi2.getDivNameEn())) {
					existed = true;
					break;
				}
			}
			if(!existed) listDivi2.add(divi);
			mapDeptDivi.put(dept.getDeptNameEn(), listDivi2);
		}
		settings.setMapDeptDivi(mapDeptDivi);
		//
		List<Dept> listDept = new ArrayList<>();
		for(String deptName: mapDept.keySet()) {
			listDept.add(mapDept.get(deptName));
		}
		settings.setListDept(listDept);
		//
		return settings;
	}
	
	public static Settings getSettingsWithAPI(CtxProperty ctxProp) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/getSettings";
		//
		ResteasyClient client = getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
    	Response response = null;
    	try{
   			String strResponse = null;
    		//嘗試
   			boolean responseOk = false;
   			int cc = 0;
   			while(cc < 5) {
   				response = builder.get();
   				if(response.getStatus() == Response.Status.OK.getStatusCode()) {
   					strResponse = response.readEntity(String.class);
   					if(!strResponse.isEmpty()) {
   						responseOk = true;
   						break;
   					}
   				}
   				cc += 1;
   				Thread.sleep(500);
   			}
   			//
   			if(!responseOk)
   				throw new Exception(strResponse);
   			//
   			Gson gson = new Gson();
   			Settings settings = new Settings();
   			//
   			JSONObject job = new JSONObject(strResponse);
   			if(job.has("listNation")) {
   				settings.setListNation(gson.fromJson(job.get("listNation").toString(),
   						new TypeToken<List<Nation>>(){}.getType()));
   			}
   			if(job.has("listDept")) {
   				settings.setListDept(gson.fromJson(job.get("listDept").toString(),
   						new TypeToken<List<Dept>>(){}.getType()));
   			}
   			if(job.has("mapDeptDivi")) {
   				settings.setMapDeptDivi(gson.fromJson(job.get("mapDeptDivi").toString(),
   						new TypeToken<LinkedHashMap<String, List<Divi>>>(){}.getType()));
   			}
   			return settings;
    	}catch(Exception ex) {
    		throw new MyException(ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
	}
	
	//下載 PDF 檔案
	public static HashMap<String, PDDocument> getCasePdfs(CtxProperty ctxProp, String alfDocId) throws Exception{
		HashMap<String, PDDocument> mapPdf = new HashMap<>();
		//
		List<String> listHeader = new ArrayList<>();
		listHeader.add(Gutil.FILE_HEADER_COVID);
		listHeader.add(Gutil.FILE_HEADER_CV);
		listHeader.add(Gutil.FILE_HEADER_DIPLOMA);
		listHeader.add(Gutil.FILE_HEADER_FLIGHT);
		listHeader.add(Gutil.FILE_HEADER_HEALTHCHECK);
		listHeader.add(Gutil.FILE_HEADER_INSURANCE);
		listHeader.add(Gutil.FILE_HEADER_LICENSURE);
		listHeader.add(Gutil.FILE_HEADER_PASSPORT);
		listHeader.add(Gutil.FILE_HEADER_PHOTO);
		listHeader.add(Gutil.FILE_HEADER_RECOMMEND);
		
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, alfDocId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(!item.objectName().toLowerCase().endsWith("pdf")) continue;
			//
			String pureName = item.objectName().substring(0, item.objectName().lastIndexOf("."));
			if(pureName.contains("/"))
				pureName = pureName.substring(pureName.lastIndexOf("/") + 1);
					
			if(!listHeader.contains(pureName)) continue;
			//
			mapPdf.put(pureName, PDDocument.load(MinioUtils.getObjectStream(ctxProp.getSiteId(), item.objectName(), ctxProp.getMinioClient())));
		}
		//
		return mapPdf;
	}
	
	public static HashMap<String, PDDocument> getCasePdfsWithAPI(CtxProperty ctxProp,
			String alfDocId) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/getCasePdfs/" + Gutil.getPureNodeId(alfDocId); 
		
		HashMap<String, PDDocument> mapPdf = new HashMap<>();
		//
		ResteasyClient client = getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request();
    	Response response = null;
    	try{
    		response = builder.get();
    		ZipInputStream zis = new ZipInputStream((InputStream)response.getEntity());
    		
    		byte[] buffer = new byte[2048];
    		ZipEntry entry;
            while((entry = zis.getNextEntry()) != null) {
            	ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	int len = 0;
                while ((len = zis.read(buffer)) > 0) {
                    baos.write(buffer, 0, len);
                }
                //
                String headerName = entry.getName().substring(0, entry.getName().lastIndexOf("."));
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                mapPdf.put(headerName, PDDocument.load(bais));
            }
    		return mapPdf;
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
	}
	
	public static String getPureNodeId(String fullNodeId) {
		String pureId = fullNodeId.replace("workspace://SpacesStore/", "");
		if(pureId.contains(";")) pureId = pureId.substring(0, pureId.lastIndexOf(";"));
		//
		return pureId;
	}
	
	public static String getFullNodeId(String nodeId) {
		if(!nodeId.startsWith("workspace://SpacesStore/")) {
			return "workspace://SpacesStore/" + nodeId;
		}else {
			return nodeId;
		}
	}
	
	//上傳 PDF 檔案

	public static void postCasePdfs(CtxProperty ctxProp,
			HashMap<String, PDDocument> mapToUpload,
			String alfDocId) throws Exception{
		for(String headerName: mapToUpload.keySet()) {
			PDDocument pdf = mapToUpload.get(headerName);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	pdf.save(baos);
        	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        	String objPath = alfDocId + "/" + headerName + ".pdf";
        	MinioUtils.uploadObject(ctxProp.getSiteId(), objPath, bais, ctxProp.getMinioClient());
		}
	}
	
	public static void postCasePdfsWithAPI(CtxProperty ctxProp,
			HashMap<String, PDDocument> mapToUpload,
			String alfDocId) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/uploadCasePdfs";
        //
		MultipartFormDataOutput mfdo = new MultipartFormDataOutput();
		mfdo.addFormData("alfDocId", alfDocId, MediaType.TEXT_PLAIN_TYPE);
        for(String pdfName: mapToUpload.keySet()) {
        	String fileName = pdfName + ".pdf"; 
        	
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	mapToUpload.get(pdfName).save(baos);
			
        	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        	mfdo.addFormData("file", bais, MediaType.APPLICATION_OCTET_STREAM_TYPE, URLEncoder.encode(fileName, "UTF-8"));
        }
        
        GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(mfdo) {};
        //
        ResteasyClient client = getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request();
    	Response response = null;
    	try{
    		response = builder.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
    		
    		if(response.getStatus() != Response.Status.OK.getStatusCode())
    			throw new Exception(response.getStatus() + ":" + response.readEntity(String.class));
    	}catch(Exception ex) {
    		throw new Exception(ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
	}
	
	//上傳 機票 檔案
	public static void postFlighPdfs(CtxProperty ctxProp,
			HashMap<String, PDDocument> mapToUpload,
			String alfDocId) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/uploadFlightPdfs";
        //
		MultipartFormDataOutput mfdo = new MultipartFormDataOutput();
		mfdo.addFormData("alfDocId", alfDocId, MediaType.TEXT_PLAIN_TYPE);
        for(String pdfName: mapToUpload.keySet()) {
        	String fileName = pdfName + ".pdf";
        	//
        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	mapToUpload.get(pdfName).save(baos);
			
        	ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        	mfdo.addFormData("file", bais, MediaType.APPLICATION_OCTET_STREAM_TYPE, URLEncoder.encode(fileName, "UTF-8"));
        }
        
        GenericEntity<MultipartFormDataOutput> entity = new GenericEntity<MultipartFormDataOutput>(mfdo) {};
        //
        ResteasyClient client = getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request();
    	Response response = null;
    	try{
    		response = builder.post(Entity.entity(entity, MediaType.MULTIPART_FORM_DATA_TYPE));
    		
    		if(response.getStatus() != Response.Status.OK.getStatusCode())
    			throw new Exception(response.getStatus() + ":" + response.readEntity(String.class));
    	}catch(Exception ex) {
    		throw new Exception("2:" + ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
	}
	
	public static CaseData getCaseByPassport(String passportNo,
			String siteId,
			Connection conn) throws Exception{
		if(passportNo.length() < 3)
			throw new Exception("Passport number cannot be empty !");
		//
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//
		CaseData theCase = new CaseData();
		theCase.setPassportNo(passportNo);
		//
		String sql = "SELECT doc_id, profile_json, stage_name FROM tams WHERE module_name=? "
				+ " AND profile_json LIKE ? ORDER BY stage_name";
		PreparedStatement ps = conn.prepareCall(sql);
		ps.setString(1, siteId);
		ps.setString(2, "%\"passportNo\":\"" + passportNo + "\"%");
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			theCase = gson.fromJson(rs.getString(2), CaseData.class);
			//
			theCase.setCaseId(rs.getString(1));
			theCase.setStageNo(rs.getString(3));
		}
		if(theCase.getCaseDate().isEmpty())
			theCase.setCaseDate(Gutil.toLocalDate(new java.util.Date()).toString());
		rs.close();
		ps.close();		
		//
		return theCase;
	}

	public static CaseData getCaseByCaseId(String caseId,
			String siteId,
			Connection conn) throws Exception{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//
		CaseData theCase = new CaseData();
		//
		String sql = "SELECT profile_json, stage_name FROM tams WHERE module_name=? "
				+ " AND doc_id = ?";
		PreparedStatement ps = conn.prepareCall(sql);
		ps.setString(1, siteId);
		ps.setString(2, caseId);
		ResultSet rs = ps.executeQuery();
		if(rs.next()){
			theCase = gson.fromJson(rs.getString(1), CaseData.class);
			//
			theCase.setCaseId(caseId);
			theCase.setStageNo(rs.getString(2));
		}
		rs.close();
		ps.close();		
		//
		return theCase;
	}

	public static CaseData getCaseByPassportWithAPI(CtxProperty ctxProp,
			String passportNo) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/getCaseByPassport/" + passportNo;
		//String uri = "https://sedu.vghtc.gov.tw/gtrainapi/getCaseByPassport/" + passportNo;
		//
		ResteasyClient client = Gutil.getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
    	Response response = null;
    	try{
    		String strResponse = null;
    		//嘗試
    		boolean responseOk = false;
    		int cc = 0;
   			while(cc < 5) {
   				response = builder.get();
   				if(response.getStatus() == Response.Status.OK.getStatusCode()) {
   					strResponse = response.readEntity(String.class);
   					if(!strResponse.isEmpty()) {
   						responseOk = true;
   						break;
   					}
   				}
   				cc += 1;
   				Thread.sleep(1000);
   			}    		
    		//
   			if(!responseOk)
   				throw new MyException(strResponse);
    		//
   			if(strResponse.isEmpty()) {
   				return new CaseData();
   			}else {
   				Gson gson = new GsonBuilder().create();
   				return gson.fromJson(strResponse, CaseData.class);
   			}
    	}catch(Exception ex) {
    		throw new MyException("Exception from getCaseByPassport: " + ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
		//return null;
	}

	public static void saveCaseData(CtxProperty ctxProp,
			CaseData theCase,
			Connection conn) throws Exception{
		if(theCase.getCaseNo().isEmpty()) 
			throw new Exception("Case number cannot be empty !");
		//
		theCase.setCaseId(theCase.getPassportNo() + "-" + theCase.getCaseNo());
		if(theCase.getStageNo().isEmpty())
			theCase.setStageNo("A");		//暫存區
		//
		LocalDate dateToday = Gutil.toLocalDate(new java.util.Date());
		if(theCase.getCaseDate().isEmpty()) theCase.setCaseDate(dateToday.toString());
		//
		Gson gson = new Gson();
		
		String strJson = gson.toJson(theCase);
		//ByteArrayOutputStream baos = Gutil.convertJsonToByteArrayOutputStream(strJson);
		//
		String pathProfile = theCase.getCaseId() + "/" + Gutil.PROFILE_NAME;
		//List<Item> listItem = MinioUtils.listObjects(siteId, false, pathProfile, client);
		//if(listItem.isEmpty()) isNewCase = true;
		//
		MinioUtils.uploadObject(ctxProp.getSiteId(),
								pathProfile,
								new ByteArrayInputStream(strJson.getBytes(StandardCharsets.UTF_8)),
								ctxProp.getMinioClient());
		//	
		SimpleDateFormat sdfss = new SimpleDateFormat("yyyyMMddHHmmss");
		sdfss.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		
		boolean isNewCase = true;
		String sql = "SELECT 1 FROM tams WHERE module_name = ? AND doc_id = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, ctxProp.getSiteId());
		ps.setString(2, theCase.getCaseId());
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			isNewCase = false;
		}
		rs.close();
		ps.close();
		//
		if(isNewCase) {
			sql = "INSERT INTO tams (profile_json,"
							+ "stage_name,"
							+ "last_update,"
							+ "module_name,"
							+ "doc_id) "
						+ "VALUES (?,?,?,?,?)";
		}else {
			sql = "UPDATE tams SET profile_json = TO_JSON(?::JSON),"
							+ "stage_name=?,"
							+ "last_update = ? "
						+ " WHERE module_name=? "
							+ " AND doc_id=?";
		}
		ps = conn.prepareStatement(sql);
		ps.setString(1, strJson);
		ps.setString(2, theCase.getStageNo());
		ps.setString(3, sdfss.format(new java.util.Date()));
		ps.setString(4, ctxProp.getSiteId());
		ps.setString(5, theCase.getCaseId());
		ps.execute();
		//
		ps.close();
	}
	
	public static CaseData saveCaseDataWithAPI(CtxProperty ctxProp,
			CaseData theCase) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/saveCaseData";
		
		Gson gson = new GsonBuilder().create();
		//String strJson = gson.toJson(theCase);
		//
		ResteasyClient client = getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
    	Response response = null;
    	try{
    		response = builder.post(Entity.json(theCase));
    		if(response.getStatus() != Response.Status.OK.getStatusCode())
    			throw new Exception(response.getStatus() + ":" + response.readEntity(String.class));
    		//
    		return gson.fromJson(response.readEntity(String.class), CaseData.class);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		throw new MyException(ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
	}
	
	public static void submitCaseData(CtxProperty ctxProp,
			CaseData theCase,
			Connection conn) throws Exception{
		boolean doNotice = false;		//通知承辦人
		if(theCase.getStageNo().isEmpty() 
				|| theCase.getStageNo().equals("A")
				|| theCase.getStageNo().equals("B")) {
			theCase.setStageNo("C");			//新申請者 放在 C.收件區
			doNotice = true;
		}
		//
		Gutil.saveCaseData(ctxProp, theCase, conn);
		//通知承辦人
		if(doNotice) {
			try {
				javax.mail.Session mailSession = Gutil.getMailSession(ctxProp);
				Gutil.sendMailTLS(ctxProp.getCcAccount(),
						"外籍醫事人員來院進修申請通知",
						"已接收 " + theCase.getPersonNameEn() + " 上傳之來院進修申請（申請時間：" + Gutil.getCurrentTime() + "）。",
						mailSession,
						ctxProp.getFromAccount(),
						null);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}		
		
	}
	
	public static CaseData submitCaseDataWithAPI(CtxProperty ctxProp,
			CaseData theCase) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/submitCaseData";
		
		Gson gson = new GsonBuilder().create();
		//String strJson = gson.toJson(theCase);
		//
		ResteasyClient client = getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
    	Response response = null;
    	try{
    		//response = builder.post(Entity.json(strJson));
    		boolean responseOk = false;
    		String strResponse = "";
    		int cc = 0;
    		while(cc < 3) {
    			response = builder.post(Entity.json(theCase));
    			if(response.getStatus() == Response.Status.OK.getStatusCode()) {
    				strResponse = response.readEntity(String.class);
    				responseOk = true;
    				break;
    			}
    			cc += 1;
    		}
    		if(!responseOk) {
    			if(strResponse.trim().isEmpty()) {
    				throw new Exception("submitCaseData api is not response ok !");
    			}else {
    				throw new Exception(strResponse);
    			}
    		}
    		//
    		return gson.fromJson(strResponse, CaseData.class);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		throw new MyException(ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
	}

	public static Nation getNationByNameEN(String nameEn, Settings settings) throws Exception{
		for(Nation nation: settings.getListNation()) {
			if(nation.getNationNameEn().equalsIgnoreCase(nameEn)) {
				return nation;
			}
		}
		return null;
	}
	
	public static Dept getDept(Dept deptVal, List<Dept> listDept) throws Exception{
		if(deptVal != null) {
			for(Dept dept: listDept) {
				if(dept.getDeptNameEn().equalsIgnoreCase(deptVal.getDeptNameEn())) {
					return dept;
				}
			}
		}
		return null;
	}
	
	public static Divi getDivi(Divi diviVal, List<Divi> listDivi) throws Exception{
		if(diviVal != null) {
			for(Divi divi: listDivi) {
				if(divi.getDivNameEn().equalsIgnoreCase(diviVal.getDivNameEn())) {
					return divi;
				}
			}
		}
		return null;
	}
	
	public static PDDocument getPdfDocument(String headerName, HashMap<String, PDDocument> mapFramePdf) throws Exception{
		PDDocument pdfDoc = null;
		for(String key: mapFramePdf.keySet()) {
			if(key.equalsIgnoreCase(headerName)) {
				pdfDoc = mapFramePdf.get(key);
				break;
			}
		}
		//
		return pdfDoc;
	}
	
    public static boolean isEmpty(String s) {
    	if(s == null || s.trim().equals("")) {
    		return true;
    	}else {
    		return false;
    	}
    }
	
	public static void handleException(Exception ex) {
		if(ex instanceof MyException) {
			MessageBox.createWarning().withCaption("Warning")
					.withMessage(ex.getMessage()).withOkButton().open();
		}else {
			MessageBox.createWarning().withCaption("Warning")
					.withMessage(Gutil.getErrotMessageITEZ(ex)).withOkButton().open();
		}
	} 	
	
	public static String getErrotMessageITEZ(Exception ex) {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		ex.printStackTrace(printWriter);
		//
		String msg = ex.getMessage();
		BufferedReader reader = new BufferedReader(new StringReader(stringWriter.toString()));
		try{
			String str = "";
			while((str = reader.readLine()) != null){
				if(str.contains("com.itez")){
					msg += ("\n" + str);
					break;
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		return msg;
	}

	public static void close(Connection conn) {
    	try {
    		if(conn != null) conn.close();
    	}catch(Exception ex) {
    	}
    }
	
	public static void close(PreparedStatement ps, Connection conn) {
		try {
    		if(ps != null) ps.close();
    	}catch(Exception ex) {
    	}
		try {
    		if(conn != null) conn.close();
    	}catch(Exception ex) {
    	}
    }
	
	//判斷是否為系統允許之影像檔名
	public static boolean isImageFile(String fileName){
		if(fileName.toLowerCase().endsWith("jpg")
				|| fileName.toLowerCase().endsWith("jpeg")
				|| fileName.toLowerCase().endsWith("png")){
			return true;
		}else{
			return false;
		}
	}
	
	//判斷是否為系統允許之Word檔名
	public static boolean isWordFile(String fileName){
		if(fileName.toLowerCase().endsWith("doc") || fileName.toLowerCase().endsWith("docx")){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isTxtFile(String fileName){
		if(fileName.toLowerCase().endsWith("txt")){
			return true;
		}else{
			return false;
		}
	}

	public static boolean isExcelFile(String fileName){
		if(fileName.toLowerCase().endsWith("xls") || fileName.toLowerCase().endsWith("xlsx")){
			return true;
		}else{
			return false;
		}
	}
	
	//判斷是否為系統允許之 Pdf 檔名
	public static boolean isPdfFile(String fileName) {
		if(fileName.toLowerCase().endsWith("pdf")){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isMailFile(String fileName) {
		if(fileName.toLowerCase().endsWith("eml")){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isValidEmail(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		}catch(AddressException ex) {
			result = false;
		}
		return result;
	}
	
	public static String getJsonMimeType(){
    	return "application/json; charset=UTF-8";
    }
    
    public static String getPdfMimeType(){
    	return "application/pdf";
    }
    
    public static String getZipMimeType(){
    	return "application/zip";
    }
    
    public static String getExcelMimeType(){
    	return "application/vnd.ms-excel";
    }
    
    public static String getWordMimeType(){
    	return "application/msword";
    }

	public static ByteArrayOutputStream convertJsonToByteArrayOutputStream(String json) throws Exception{
		byte[] bytes = json.getBytes("UTF-8");
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
		baos.write(bytes, 0, bytes.length);
		return baos;
    }
	
	public static LocalDate toLocalDate(Date dateToConvert) {
	    return dateToConvert.toInstant()
	    			.atZone(ZoneId.of("Asia/Taipei"))
	    			.toLocalDate();
	}
	
	public static LocalDate toLocalDate(String strToConvert) {
	    try {
			if(strToConvert.contains("-")) {
				String[] ss = strToConvert.split("-");
				return LocalDate.of(Integer.parseInt(ss[0]),
						Integer.parseInt(ss[1]),
						Integer.parseInt(ss[2]));
		    }else if(strToConvert.length() == 8) {
		    	return LocalDate.of(Integer.parseInt(strToConvert.substring(0, 4)),
						Integer.parseInt(strToConvert.substring(4, 6)),
						Integer.parseInt(strToConvert.substring(6)));
		    }else if(strToConvert.length() == 7) {
		    	return LocalDate.of(Integer.parseInt(strToConvert.substring(0, 3)) + 1911,
						Integer.parseInt(strToConvert.substring(3, 5)),
						Integer.parseInt(strToConvert.substring(5)));
		    }
		}catch(Exception ex) {
		}
		return null;
	}
	
	//開啟申請網頁
	public static void openApplyForm(VerticalLayout layoutHero,
			CtxProperty ctxProp,
			CaseData theCase,
			Connection conn,
			javax.sql.DataSource ds) throws Exception{
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
		sdfss.setTimeZone(timeZone);
		sdfSSS.setTimeZone(timeZone);
		//
		Settings settings = Gutil.getSettings(conn);
		//
		File dirCurr = new File(".");
		File dirUpload = new File(dirCurr.getCanonicalPath() + "/" + "upload");
		if(!dirUpload.exists()) dirUpload.mkdirs();
		//
		Panel panel = new Panel();
		panel.setSizeFull();
		//讀取 國別、部科別
		//settings = Gutil.getSettings(ctxProp);
		//載入 案件相關檔案
		//MinioClient client = MinioUtils.getMinioClient();
		//
		HashMap<String, PDDocument> mapFramePdf = new HashMap<>();
		if(!theCase.getCaseId().isEmpty()) {
			mapFramePdf = Gutil.getCasePdfs(ctxProp, theCase.getCaseId());
		}
		//
		ApplyForm form = new ApplyForm();
		//
		form.txtPersonNameEn.setReadOnly(true);
		form.txtPassportNo.setReadOnly(true);
		//
		form.combNation.setItems(settings.getListNation());
		form.combNation.setItemCaptionGenerator(na -> na.getNationNameEn());
		form.combNation.setEmptySelectionAllowed(false);
		form.combNation.setPageLength(settings.getListNation().size());
		form.combNation.setPopupWidth(null);
		form.combNation.setValue(Gutil.getNationByNameEN(theCase.getNationality(), settings));
		
		form.combSchoolLocation.setItems(settings.getListNation());
		form.combSchoolLocation.setItemCaptionGenerator(na -> na.getNationNameEn());
		form.combSchoolLocation.setEmptySelectionAllowed(false);
		form.combSchoolLocation.setPageLength(settings.getListNation().size());
		form.combSchoolLocation.setPopupWidth(null);
		form.combSchoolLocation.setValue(Gutil.getNationByNameEN(theCase.getSchoolLocation(), settings));
		//
		List<String> listHeader = new ArrayList<>();
		listHeader.add(Gutil.FILE_HEADER_PHOTO);
		listHeader.add(Gutil.FILE_HEADER_PASSPORT);
		listHeader.add(Gutil.FILE_HEADER_LICENSURE);
		listHeader.add(Gutil.FILE_HEADER_DIPLOMA);
		listHeader.add(Gutil.FILE_HEADER_RECOMMEND);
		listHeader.add(Gutil.FILE_HEADER_CV);
		listHeader.add(Gutil.FILE_HEADER_HEALTHCHECK);
		listHeader.add(Gutil.FILE_HEADER_COVID);
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
		form.framePhoto.setId(Gutil.FILE_HEADER_PHOTO);
		form.framePassport.setId(Gutil.FILE_HEADER_PASSPORT);
		form.framelicensure.setId(Gutil.FILE_HEADER_LICENSURE);
		form.frameDiploma.setId(Gutil.FILE_HEADER_DIPLOMA);
		form.frameRecommend.setId(Gutil.FILE_HEADER_RECOMMEND);
		form.frameCV.setId(Gutil.FILE_HEADER_CV);
		form.frameHealthCheck.setId(Gutil.FILE_HEADER_HEALTHCHECK);
		form.frameCovid.setId(Gutil.FILE_HEADER_COVID);
		form.frameFlight.setId(Gutil.FILE_HEADER_FLIGHT);
		form.frameInsurance.setId(Gutil.FILE_HEADER_INSURANCE);
		
		List<BrowserFrame> listBrowserFrame = new ArrayList<>();
		listBrowserFrame.add(form.framePhoto);
		listBrowserFrame.add(form.framePassport);
		listBrowserFrame.add(form.framelicensure);
		listBrowserFrame.add(form.frameDiploma);
		listBrowserFrame.add(form.frameRecommend);
		listBrowserFrame.add(form.frameCV);
		listBrowserFrame.add(form.frameHealthCheck);
		listBrowserFrame.add(form.frameFlight);
		listBrowserFrame.add(form.frameInsurance);
		listBrowserFrame.add(form.frameCovid);
		
		for(BrowserFrame frame: listBrowserFrame) {
			FrameFile frameFile = mapFrameFile.get(frame.getId());
			frameFile.setHeaderName(frame.getId());
			//
			Gutil.addUploaderToFrame(frame, frameFile, dirUpload);
			//載入Pdf
			if(frameFile.getPdfDoc() != null) {
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
			    String fileName = frame.getId() + ".pdf";
			    StreamResource resource = new StreamResource(source, fileName);
			    resource.setMIMEType("application/pdf");
				frame.setSource(resource);
			}
			
			/*
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
						    String fileName = frame.getId() + ".pdf";
						    StreamResource resource = new StreamResource(source, fileName);
						    resource.setMIMEType("application/pdf");
							frame.setSource(resource);
						}						
						//
						break;
					}
				}
			//}
			 */
		}
		//部科別
		if(theCase.getListChoice().size() > 1) {
			for(int i=0; i < theCase.getListChoice().size(); i++) {
				DiviChoice choice = theCase.getListChoice().get(i);
				if(i==0) {
					Gutil.prepareDiviChoiceEvents(form.combDept,
							form.combDivi,
							form.dateFromChoice,
							form.dateEndChoice,
							form.txtTeacherNameEn1,
							form.txtTeacherNameCh1,
							form.txtTeacherEmail1,
							form.txtTeacherPhone1,
							form.csslayoutTeacher1,
							form.txtTeacherNameEn2,
							form.txtTeacherNameCh2,
							form.txtTeacherEmail2,
							form.txtTeacherPhone2,
							form.csslayoutTeacher2,
							form.txtTeacherNameEn3,
							form.txtTeacherNameCh3,
							form.txtTeacherEmail3,
							form.txtTeacherPhone3,
							form.csslayoutTeacher3,
							form.layoutRowChoice,
							form.btnNewChoice,
							form.gridlayoutChoice,
							choice,
							settings,
							theCase,
							ctxProp.isAdmin());
				}else {
					Gutil.addDiviChoiceRow(form.gridlayoutChoice,
							choice,
							settings,
							theCase,
							ctxProp.isAdmin());
				}
			}
		}else {
			DiviChoice choice = new DiviChoice();
			if(theCase.getListChoice().size() > 0) {
				choice = theCase.getListChoice().get(0);
			}else {
				choice.setTimeStamp(sdfSSS.format(new java.util.Date()));
				theCase.getListChoice().add(choice);
			}
			form.combDept.setId(choice.getTimeStamp());
			Gutil.prepareDiviChoiceEvents(form.combDept,
					form.combDivi,
					form.dateFromChoice,
					form.dateEndChoice,
					form.txtTeacherNameEn1,
					form.txtTeacherNameCh1,
					form.txtTeacherEmail1,
					form.txtTeacherPhone1,
					form.csslayoutTeacher1,
					form.txtTeacherNameEn2,
					form.txtTeacherNameCh2,
					form.txtTeacherEmail2,
					form.txtTeacherPhone2,
					form.csslayoutTeacher2,
					form.txtTeacherNameEn3,
					form.txtTeacherNameCh3,
					form.txtTeacherEmail3,
					form.txtTeacherPhone3,
					form.csslayoutTeacher3,
					form.layoutRowChoice,
					form.btnNewChoice,
					form.gridlayoutChoice,
					choice,
					settings,
					theCase,
					ctxProp.isAdmin());
		}
		//COVID-19注射記錄
		if(theCase.getListVacci().size() > 1) {
			for(int i=0; i < theCase.getListVacci().size(); i++) {
				Vacci vacci = theCase.getListVacci().get(i);
				if(i==0) {
					Gutil.prepareVacciEvents(form.dateVaccin,
							form.txtPlaceVacci,
							form.combVender,
							form.txtElseVender,
							form.layoutRowVacci,
							form.btnNewVacci,
							form.gridlayoutVacci,
							vacci,
							theCase);
				}else {
					Gutil.addVacciRow(form.gridlayoutVacci,
							vacci,
							theCase);
				}
			}
		}else {
			Vacci vacci = new Vacci();
			if(theCase.getListVacci().size() > 0) {
				vacci = theCase.getListVacci().get(0);
			}else {
				vacci.setTimeStamp(sdfSSS.format(new java.util.Date()));
				theCase.getListVacci().add(vacci);
			}
			form.dateVaccin.setId(vacci.getTimeStamp());
			Gutil.prepareVacciEvents(form.dateVaccin,
					form.txtPlaceVacci,
					form.combVender,
					form.txtElseVender,
					form.layoutRowVacci,
					form.btnNewVacci,
					form.gridlayoutVacci,
					vacci,
					theCase);
		}
		//需等同意後才傳
		form.panelFlight.setVisible(false);
		form.layoutTuition.setVisible(false);
		if(ctxProp.isAdmin()) {
			form.panelFlight.setVisible(true);
			form.layoutTuition.setVisible(true);
		}
		//
		final Binder<CaseData> binder = new Binder<>(CaseData.class);
		//Gutil.addValidatorApplyForm(form, binder);
		binder.bindInstanceFields(form);
		binder.readBean(theCase);
		//
		if(!theCase.getBirthday().isEmpty())
			form.dateBirth.setValue(Gutil.toLocalDate(theCase.getBirthday()));
		
		if(ctxProp.isAdmin()) {
			form.btnSave.setCaption("存檔");
			form.btnSubmit.setVisible(false);
		}
		//
		form.btnSave.addClickListener(e ->{
			Connection conn2 = null;
			try {
				binder.writeBean(theCase);
				//
				if(form.combNation.getValue() != null)
					theCase.setNationality(form.combNation.getValue().getNationNameEn());
				if(form.dateBirth.getValue() != null)
					theCase.setBirthday(form.dateBirth.getValue().toString());
				if(form.combSchoolLocation.getValue() != null)
				theCase.setSchoolLocation(form.combSchoolLocation.getValue().getNationNameEn());
				//
				//先給號，避免重複建檔
				if(theCase.getCaseNo().isEmpty())
					theCase.setCaseNo(sdfSSS.format(new java.util.Date()));
				//
				conn2 = ds.getConnection();
				Gutil.saveCaseData(ctxProp, theCase, conn2);
				//if(theCase.getCaseNo().isEmpty())
				//	throw new MyException("Application is interrupted with system exception, please inform our administrator, thanks for your help.");
				//上傳檔案
				HashMap<String, PDDocument> mapToUpload = new HashMap<>();
				for(String pdfName: mapFrameFile.keySet()) {
					FrameFile ff = mapFrameFile.get(pdfName);
					if(!ff.isUpdated()) continue;
					if(ff.getPdfDoc() != null) {
						mapToUpload.put(pdfName, ff.getPdfDoc());
					}
				}
				if(!mapToUpload.isEmpty())
					Gutil.postCasePdfs(ctxProp, mapToUpload, theCase.getCaseId());
				//存檔完成後，將 update 的 flag 清除
				for(String pdfName: mapFrameFile.keySet()) {
					FrameFile ff = mapFrameFile.get(pdfName);
					if(ff.isUpdated()) 
						ff.setUpdated(false);
				}
				//
				if(ctxProp.isAdmin()) {
					MessageBox.createInfo().withCaption("資料存檔")
					.withMessage("資料存檔完成。")
					.withOkButton()
					.open();
				}else {
					MessageBox.createInfo().withCaption("Temporary save")
					.withMessage("Your application data is saved successfully.")
					.withOkButton()
					.open();
				}
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}finally {
				Gutil.close(conn2);
			}
		});
		//
		form.btnSubmit.addClickListener(e ->{
			try {
				Gutil.addValidatorApplyForm(form, binder);
				//
				BinderValidationStatus<CaseData> status = binder.validate();
				if(!status.isOk()) {
					String msg = status.getValidationErrors().stream().map(ValidationResult::getErrorMessage).collect(Collectors.joining(","));
					throw new MyException(msg);
				}
				binder.writeBean(theCase);
				//
				if(theCase.getRoleStatus().toLowerCase().contains("others")
						&& theCase.getElseRole().isEmpty()) {
					form.txtElseRole.focus();
					throw new MyException("Role cannot be empty.");
				}
				//
				if(form.combNation.getValue() == null)
					throw new MyException("Nationality cannot be empty.");
				theCase.setNationality(form.combNation.getValue().getNationNameEn());
				//
				if(form.dateBirth.getValue() == null)
					throw new MyException("Birthday cannot be empty.");
				theCase.setBirthday(form.dateBirth.getValue().toString());
				//
				if(form.combSchoolLocation.getValue() == null)
					throw new MyException("School/Affiliation location cannot be empty.");
				theCase.setSchoolLocation(form.combSchoolLocation.getValue().getNationNameEn());
				
				boolean withDiviChoice = false;
				for(DiviChoice choice: theCase.getListChoice()) {
					if(choice.getDept() != null) {
						if(choice.getFromDate().isEmpty()
								|| choice.getEndDate().isEmpty())
							throw new MyException("Please fill in all columns inside Choice of Division.");
						//
						withDiviChoice = true;
					}
				}
				if(!withDiviChoice)
					throw new MyException("Please choose at least one department for your clinical study.");
				//檢查 必要檔案 是否上傳
				List<String> listNeed = new ArrayList<>();
				listNeed.add(Gutil.FILE_HEADER_PHOTO);
				listNeed.add(Gutil.FILE_HEADER_PASSPORT);
				listNeed.add(Gutil.FILE_HEADER_LICENSURE);
				listNeed.add(Gutil.FILE_HEADER_DIPLOMA);
				listNeed.add(Gutil.FILE_HEADER_RECOMMEND);
				listNeed.add(Gutil.FILE_HEADER_CV);
				listNeed.add(Gutil.FILE_HEADER_HEALTHCHECK);
				
				String strLack = "";
				for(String fileHeader: listNeed) {
					if(mapFrameFile.get(fileHeader).getPdfDoc() == null) {
						if(!strLack.isEmpty()) strLack += ",";
						strLack += Gutil.getDocumentNameEN(fileHeader);
					}
				}
				if(!strLack.isEmpty())
					throw new MyException("Please upload the necessary files (" + strLack + ") before submittion.");
				//
				String msg = "Are you sure to submit the application now ?";
				//
				MessageBox.createQuestion().withCaption("Submit")
				.withMessage(msg)
				.withYesButton(() ->{
					try {
						final EmailCode ec = new EmailCode();
						ec.setEmail(theCase.getEmail());
						Gutil.getEmailCode(ec, ctxProp);
						//
						final Window window = new Window(" Email confirmation");
						window.setWidth(800, Unit.PIXELS);
						window.setHeight(300, Unit.PIXELS);
						if(ctxProp.getDeviceType() == DeviceType.Pad || ctxProp.getDeviceType() == DeviceType.Mobile) {
							window.setWidth(100, Unit.PERCENTAGE);
						}
						window.setModal(false);
						window.setClosable(false);
						window.center();
						window.setIcon(VaadinIcons.USER_CHECK);
						{
							VerticalLayout layout = new VerticalLayout();
							layout.setSizeFull();
							layout.addStyleName("halftone-whip");
							{
								TextField txtCode = new TextField("Please check your e-mail and enter the number within e-mail here.");
								txtCode.setWidth(100, Unit.PERCENTAGE);
								layout.addComponent(txtCode);
								//
								Button btnCode = new Button("Continue");
								btnCode.addClickListener(ev ->{
									Connection conn2 = null;
									try {
										if(txtCode.getValue().trim().isEmpty())
											throw new MyException("Please check your e-mail and enter the number within e-mail here.");
										//
										if(!txtCode.getValue().trim().equals(ec.getCode()))
											throw new MyException("The code you enter is not coincide with number within your e-mail.");
										//
										window.close();
										//
										//先給號，避免重複建檔
										if(theCase.getCaseNo().isEmpty())
											theCase.setCaseNo(sdfSSS.format(new java.util.Date()));
										//
										conn2 = ds.getConnection();
										Gutil.submitCaseData(ctxProp, theCase, conn2);
										//上傳檔案
										HashMap<String, PDDocument> mapToUpload = new HashMap<>();
										for(String pdfName: mapFrameFile.keySet()) {
											FrameFile ff = mapFrameFile.get(pdfName);
											if(!ff.isUpdated()) continue;
											if(ff.getPdfDoc() != null) {
												mapToUpload.put(pdfName, ff.getPdfDoc());
											}
										}
										if(!mapToUpload.isEmpty())
											Gutil.postCasePdfs(ctxProp, mapToUpload, theCase.getCaseId());
										//存檔完成後，將 update 的 flag 清除
										for(String pdfName: mapFrameFile.keySet()) {
											FrameFile ff = mapFrameFile.get(pdfName);
											if(ff.isUpdated()) 
												ff.setUpdated(false);
										}
										//
										MessageBox.createInfo().withCaption("Submition result")
										//.withMessage("Your application has been received, we will answer your request through email as soon as possible.")
										.withMessage("Your application has been received, we will response to you as soon as possible.")
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
									}finally {
										Gutil.close(conn2);
									}
								});
								layout.addComponent(btnCode);
								layout.setComponentAlignment(btnCode, Alignment.TOP_RIGHT);
							}
							window.setContent(layout);
						}
						UI.getCurrent().addWindow(window);
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
		
		//
		panel.setContent(form);
		layoutHero.addComponent(panel);
	}
   
	
	public static void getEmailCode(EmailCode ec, CtxProperty ctxProp) throws Exception{
		String uri = ctxProp.getUriGtrain() + "/postEmailCode";
		//
		ResteasyClient client = Gutil.getResteasyClient();
		ResteasyWebTarget target = client.target(uri);
    	Invocation.Builder builder = target.request(MediaType.APPLICATION_JSON);
    	Response response = null;
    	try{
    		response = builder.post(Entity.json(ec));
   			if(response.getStatus() != Response.Status.OK.getStatusCode()) 
    			throw new Exception(response.getStatus() + ":" + response.readEntity(String.class));
    		//
   			Gson gson = new Gson();
   			EmailCode ec2 = gson.fromJson(response.readEntity(String.class), EmailCode.class);
   			ec.setCode(ec2.getCode());
    	}catch(Exception ex) {
    		throw new MyException("Exception from getEmailCode: " + ex.getMessage());
    	}finally {
    		if(response != null) response.close();
    		client.close();
    	}
		//return null;
	}
	
	//事件回應 與 輸入資料驗證
	private static void addValidatorApplyForm(ApplyForm form, Binder<CaseData> binder) throws Exception{
		//數值欄位
		//binder.forMemberField(form.txtFeeMisc).withConverter(new StringToIntegerConverter(0, "請輸入數字"));
		//相依欄位驗證
		//binder.forMemberField(form.txtPid).withValidator(s -> Utils.isValidPid(s), "身分(居留)證字號錯誤");
		//管理人員不必驗證資料
		//if(auth.isSiteAdmin) {
			//form.txtCardNo.setVisible(true);
			//
			//return;
		//}
		
		
		//
		List<Component> list = new ArrayList<>();
		//唯讀欄位
		
		//必填欄位
		list.clear();
		list.add(form.txtPersonNameEn);
		list.add(form.txtPassportNo);
		//list.add(form.combNation);
		list.add(form.rbgGender);
		//list.add(form.dateBirth);
		list.add(form.rbgRoleStatus);
		list.add(form.txtAffiliation);
		//list.add(form.combSchoolLocation);
		list.add(form.rbgChListening);
		list.add(form.rbgChSpeaking);
		list.add(form.rbgChReading);
		list.add(form.rbgChWriting);
		list.add(form.txtEmail);
		list.add(form.txtTrainGoal);
		list.add(form.rbgAccommdation);
		
		Gutil.addEmptyCheckerRuleEnglish(list, binder);
	}
		
	private static void prepareDiviChoiceEvents(ComboBox<Dept> combDept,
			ComboBox<Divi> combDivi,
			DateField dateFromChoice,
			DateField dateEndChoice,
			TextField txtTeacherNameEn1,
			TextField txtTeacherNameCh1,
			TextField txtTeacherEmail1,
			TextField txtTeacherPhone1,
			CssLayout cssLayoutTeacher1,
			TextField txtTeacherNameEn2,
			TextField txtTeacherNameCh2,
			TextField txtTeacherEmail2,
			TextField txtTeacherPhone2,
			CssLayout cssLayoutTeacher2,
			TextField txtTeacherNameEn3,
			TextField txtTeacherNameCh3,
			TextField txtTeacherEmail3,
			TextField txtTeacherPhone3,
			CssLayout cssLayoutTeacher3,
			VerticalLayout layoutRow,
			Button btn,
			GridLayout gridLayout,
			DiviChoice choice,
			Settings settings,
			CaseData theCase,
			boolean isAdmin) throws Exception{
		if(choice.getTimeStamp().isEmpty())
			throw new MyException("Timestamp for this choice is empty !");
		//
		if(choice.getTeacher1() == null) choice.setTeacher1(new Teacher());
		if(choice.getTeacher2() == null) choice.setTeacher2(new Teacher());
		if(choice.getTeacher3() == null) choice.setTeacher3(new Teacher());
		//
		combDept.setWidth(100, Unit.PERCENTAGE);
		combDept.setCaption("Department");
		combDept.setId(choice.getTimeStamp());
		//combDept.setValue(choice.getDept());
		
		combDivi.setWidth(100, Unit.PERCENTAGE);
		combDivi.setCaption("Division");
		//combDivi.setValue(choice.getDivi());
		
		dateFromChoice.setWidth(100, Unit.PERCENTAGE);
		dateFromChoice.setCaption("Period from");
		dateFromChoice.setValue(Gutil.toLocalDate(choice.getFromDate()));
		
		dateEndChoice.setWidth(100, Unit.PERCENTAGE);
		dateEndChoice.setCaption("Period end");
		dateEndChoice.setValue(Gutil.toLocalDate(choice.getEndDate()));
		//
		txtTeacherNameEn1.setWidth(100, Unit.PERCENTAGE);
		txtTeacherNameEn1.setCaption("醫師姓名(英文)");
		txtTeacherNameEn1.setValue(choice.getTeacher1().getTeacherNameEn());
		
		txtTeacherNameCh1.setWidth(100, Unit.PERCENTAGE);
		txtTeacherNameCh1.setCaption("醫師姓名(中文)");
		txtTeacherNameCh1.setValue(choice.getTeacher1().getTeacherNameCh());
		
		txtTeacherEmail1.setWidth(100, Unit.PERCENTAGE);
		txtTeacherEmail1.setCaption("醫師電子郵件");
		txtTeacherEmail1.setValue(choice.getTeacher1().getTeacherEmail());
		
		txtTeacherPhone1.setWidth(100, Unit.PERCENTAGE);
		txtTeacherPhone1.setCaption("公務機號碼");
		txtTeacherPhone1.setValue(choice.getTeacher1().getTeacherPhoneNo());
		
		if(!isAdmin) cssLayoutTeacher1.setVisible(false);
		//
		txtTeacherNameEn2.setWidth(100, Unit.PERCENTAGE);
		txtTeacherNameEn2.setCaption("醫師姓名(英文)");
		txtTeacherNameEn2.setValue(choice.getTeacher2().getTeacherNameEn());
		
		txtTeacherNameCh2.setWidth(100, Unit.PERCENTAGE);
		txtTeacherNameCh2.setCaption("醫師姓名(中文)");
		txtTeacherNameCh2.setValue(choice.getTeacher2().getTeacherNameCh());
		
		txtTeacherEmail2.setWidth(100, Unit.PERCENTAGE);
		txtTeacherEmail2.setCaption("醫師電子郵件");
		txtTeacherEmail2.setValue(choice.getTeacher2().getTeacherEmail());
		
		txtTeacherPhone2.setWidth(100, Unit.PERCENTAGE);
		txtTeacherPhone2.setCaption("公務機號碼");
		txtTeacherPhone2.setValue(choice.getTeacher2().getTeacherPhoneNo());
		
		if(!isAdmin) cssLayoutTeacher2.setVisible(false);
		//
		txtTeacherNameEn3.setWidth(100, Unit.PERCENTAGE);
		txtTeacherNameEn3.setCaption("醫師姓名(英文)");
		txtTeacherNameEn3.setValue(choice.getTeacher3().getTeacherNameEn());
		
		txtTeacherNameCh3.setWidth(100, Unit.PERCENTAGE);
		txtTeacherNameCh3.setCaption("醫師姓名(中文)");
		txtTeacherNameCh3.setValue(choice.getTeacher3().getTeacherNameCh());
		
		txtTeacherEmail3.setWidth(100, Unit.PERCENTAGE);
		txtTeacherEmail3.setCaption("醫師電子郵件");
		txtTeacherEmail3.setValue(choice.getTeacher3().getTeacherEmail());
		
		txtTeacherPhone3.setWidth(100, Unit.PERCENTAGE);
		txtTeacherPhone3.setCaption("公務機號碼");
		txtTeacherPhone3.setValue(choice.getTeacher3().getTeacherPhoneNo());
		
		if(!isAdmin) cssLayoutTeacher3.setVisible(false);
		//
		layoutRow.setWidth(100, Unit.PERCENTAGE);
		//
		combDept.setItems(settings.getListDept());
		combDept.setItemCaptionGenerator(dep -> dep.getDeptNameEn());
		combDept.setEmptySelectionAllowed(false);
		combDept.setPageLength(settings.getListDept().size());
		combDept.setPopupWidth(null);
		//先事件、再給值
		combDept.addValueChangeListener(e -> {
			try {
				choice.setDept(e.getValue());
				//
				combDivi.setValue(null);
				if(e.getValue() == null) return;
				//
				List<Divi> listDivi = settings.getMapDeptDivi().get(e.getValue().getDeptNameEn());
				combDivi.setItems(listDivi);
				combDivi.setItemCaptionGenerator(div -> div.getDivNameEn());
				combDivi.setEmptySelectionAllowed(true);
				combDivi.setPageLength(listDivi.size());
				combDivi.setPopupWidth(null);
				//
				combDivi.setValue(Gutil.getDivi(choice.getDivi(), listDivi));
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
		combDept.setValue(Gutil.getDept(choice.getDept(), settings.getListDept()));
		
		combDivi.addValueChangeListener(e -> {
			choice.setDivi(e.getValue());
		});
		
		dateFromChoice.addValueChangeListener(e ->{
			choice.setFromDate(e.getValue().toString());
		});
		
		dateEndChoice.addValueChangeListener(e ->{
			choice.setEndDate(e.getValue().toString());
		});
		
		txtTeacherNameEn1.addValueChangeListener(e ->{
			choice.getTeacher1().setTeacherNameEn(e.getValue().trim());
		});
		
		txtTeacherNameCh1.addValueChangeListener(e ->{
			choice.getTeacher1().setTeacherNameCh(e.getValue().trim());
		});
		
		txtTeacherEmail1.addValueChangeListener(e ->{
			choice.getTeacher1().setTeacherEmail(e.getValue().trim());
		});
		
		txtTeacherPhone1.addValueChangeListener(e ->{
			choice.getTeacher1().setTeacherPhoneNo(e.getValue().trim());
		});
		//
		txtTeacherNameEn2.addValueChangeListener(e ->{
			choice.getTeacher2().setTeacherNameEn(e.getValue().trim());
		});
		
		txtTeacherNameCh2.addValueChangeListener(e ->{
			choice.getTeacher2().setTeacherNameCh(e.getValue().trim());
		});
		
		txtTeacherEmail2.addValueChangeListener(e ->{
			choice.getTeacher2().setTeacherEmail(e.getValue().trim());
		});
		
		txtTeacherPhone2.addValueChangeListener(e ->{
			choice.getTeacher2().setTeacherPhoneNo(e.getValue().trim());
		});
		//
		txtTeacherNameEn3.addValueChangeListener(e ->{
			choice.getTeacher3().setTeacherNameEn(e.getValue().trim());
		});
		
		txtTeacherNameCh3.addValueChangeListener(e ->{
			choice.getTeacher3().setTeacherNameCh(e.getValue().trim());
		});
		
		txtTeacherEmail3.addValueChangeListener(e ->{
			choice.getTeacher3().setTeacherEmail(e.getValue().trim());
		});
		
		txtTeacherPhone3.addValueChangeListener(e ->{
			choice.getTeacher3().setTeacherPhoneNo(e.getValue().trim());
		});
		
		//新增或刪除列
		btn.addClickListener(e ->{
			try {
				if(btn.getIcon() == VaadinIcons.MINUS
						|| btn.getIcon() == VaadinIcons.TRASH) {
					theCase.getListChoice().remove(choice);
					//
					gridLayout.removeComponent(layoutRow);
					gridLayout.removeComponent(btn);
					//
					gridLayout.setHideEmptyRowsAndColumns(true);
				}else {
					gridLayout.setRows(gridLayout.getRows() + 1);
					DiviChoice choice2 = new DiviChoice();
					choice2.setTimeStamp(sdfSSS.format(new java.util.Date()));
					theCase.getListChoice().add(choice2);
					//
					Gutil.addDiviChoiceRow(gridLayout, choice2, settings, theCase, isAdmin);
				}
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
	}	
	
	private static void addDiviChoiceRow(GridLayout gridLayout,
			DiviChoice choice,
			Settings settings,
			CaseData theCase,
			boolean isAdmin) throws Exception{
		gridLayout.setRows(gridLayout.getRows() + 1);
		//資料元件
		ComboBox<Dept> combDept2 = new ComboBox<>();
		ComboBox<Divi> combDivi2 = new ComboBox<>(); 
		DateField dateFromChoice2 = new DateField();
		DateField dateEndChoice2 = new DateField();
		Button btnDel = new Button(null, VaadinIcons.TRASH);
		btnDel.setDescription("Remove this choice");
		//排 版面
		VerticalLayout layoutRow2 = new VerticalLayout();
		layoutRow2.setWidth(100, Unit.PERCENTAGE);
		layoutRow2.setMargin(false);
		layoutRow2.setSpacing(false);
		//
		CssLayout csslayout = new CssLayout();
		csslayout.setResponsive(true);
		csslayout.setWidth(100, Unit.PERCENTAGE);
		csslayout.addStyleName("cols-4");
		{
			VerticalLayout vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(combDept2);
			csslayout.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(combDivi2);
			csslayout.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(dateFromChoice2);
			csslayout.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(dateEndChoice2);
			csslayout.addComponent(vl);
		}
		layoutRow2.addComponent(csslayout);
		//
		VerticalLayout layoutTeacher = new VerticalLayout();
		layoutTeacher.setWidth(100, Unit.PERCENTAGE);
		layoutTeacher.setMargin(new MarginInfo(false, false, false, true));
		
		//指導醫師一
		TextField txtTeacherNameEn21 = new TextField();
		txtTeacherNameEn21.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherNameCh21 = new TextField();
		txtTeacherNameCh21.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherEmail21 = new TextField();
		txtTeacherEmail21.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherPhone21 = new TextField();
		txtTeacherPhone21.setWidth(100, Unit.PERCENTAGE);
			
		CssLayout csslayoutTeacher21 = new CssLayout();
		csslayoutTeacher21.setResponsive(true);
		csslayoutTeacher21.setWidth(100, Unit.PERCENTAGE);
		csslayoutTeacher21.addStyleName("cols-4");
		csslayoutTeacher21.setCaption("<strong>指導醫師一</strong>");
		csslayoutTeacher21.setCaptionAsHtml(true);
		{
			VerticalLayout vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherNameEn21);
			csslayoutTeacher21.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherNameCh21);
			csslayoutTeacher21.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherEmail21);
			csslayoutTeacher21.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherPhone21);
			csslayoutTeacher21.addComponent(vl);
		}
		layoutTeacher.addComponent(csslayoutTeacher21);
		//
		//指導醫師二
		TextField txtTeacherNameEn22 = new TextField();
		txtTeacherNameEn22.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherNameCh22 = new TextField();
		txtTeacherNameCh22.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherEmail22 = new TextField();
		txtTeacherEmail22.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherPhone22 = new TextField();
		txtTeacherPhone22.setWidth(100, Unit.PERCENTAGE);
			
		CssLayout csslayoutTeacher22 = new CssLayout();
		csslayoutTeacher22.setResponsive(true);
		csslayoutTeacher22.setWidth(100, Unit.PERCENTAGE);
		csslayoutTeacher22.addStyleName("cols-4");
		csslayoutTeacher22.setCaption("<strong>指導醫師二</strong>");
		csslayoutTeacher22.setCaptionAsHtml(true);
		{
			VerticalLayout vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherNameEn22);
			csslayoutTeacher22.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherNameCh22);
			csslayoutTeacher22.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherEmail22);
			csslayoutTeacher22.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherPhone22);
			csslayoutTeacher22.addComponent(vl);
		}
		layoutTeacher.addComponent(csslayoutTeacher22);
		//
		//指導醫師三
		TextField txtTeacherNameEn23 = new TextField();
		txtTeacherNameEn23.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherNameCh23 = new TextField();
		txtTeacherNameCh23.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherEmail23 = new TextField();
		txtTeacherEmail23.setWidth(100, Unit.PERCENTAGE);
			
		TextField txtTeacherPhone23 = new TextField();
		txtTeacherPhone23.setWidth(100, Unit.PERCENTAGE);
			
		CssLayout csslayoutTeacher23 = new CssLayout();
		csslayoutTeacher23.setResponsive(true);
		csslayoutTeacher23.setWidth(100, Unit.PERCENTAGE);
		csslayoutTeacher23.addStyleName("cols-4");
		csslayoutTeacher23.setCaption("<strong>指導醫師三</strong>");
		csslayoutTeacher23.setCaptionAsHtml(true);
		{
			VerticalLayout vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherNameEn23);
			csslayoutTeacher23.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherNameCh23);
			csslayoutTeacher23.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherEmail23);
			csslayoutTeacher23.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtTeacherPhone23);
			csslayoutTeacher23.addComponent(vl);
		}
		layoutTeacher.addComponent(csslayoutTeacher23);		
		//
		if(isAdmin)
			layoutRow2.addComponent(layoutTeacher);
		//
		gridLayout.addComponent(layoutRow2);
		gridLayout.addComponent(btnDel);
		gridLayout.setComponentAlignment(btnDel, Alignment.BOTTOM_CENTER);
		//Keypress 事件處理
		try {
			Gutil.prepareDiviChoiceEvents(combDept2,
					combDivi2,
					dateFromChoice2,
					dateEndChoice2,
					txtTeacherNameEn21,
					txtTeacherNameCh21,
					txtTeacherEmail21,
					txtTeacherPhone21,
					csslayoutTeacher21,
					txtTeacherNameEn22,
					txtTeacherNameCh22,
					txtTeacherEmail22,
					txtTeacherPhone22,
					csslayoutTeacher22,
					txtTeacherNameEn23,
					txtTeacherNameCh23,
					txtTeacherEmail23,
					txtTeacherPhone23,
					csslayoutTeacher23,
					layoutRow2,
					btnDel,
					gridLayout,
					choice,
					settings,
					theCase,
					isAdmin);
		}catch(Exception ex) {
			Gutil.handleException(ex);
		}
	}
	
	private static void prepareVacciEvents(DateField dateVacci,
			TextField txtPlaceVacci,
			ComboBox<String> combVender,
			TextField txtElseVender,
			VerticalLayout layoutRow,
			Button btn,
			GridLayout gridLayout,
			Vacci vacci,
			CaseData theCase) throws Exception{
		if(vacci.getTimeStamp().isEmpty())
			throw new MyException("Timestamp for this row is empty !");
		//
		dateVacci.setWidth(100, Unit.PERCENTAGE);
		dateVacci.setCaption("Vaccination date");
		dateVacci.setId(vacci.getTimeStamp());
		dateVacci.setValue(Gutil.toLocalDate(vacci.getVacciDate()));
		
		txtPlaceVacci.setWidth(100, Unit.PERCENTAGE);
		txtPlaceVacci.setCaption("Vaccination place/hospital");
		txtPlaceVacci.setValue(vacci.getPlaceVacci());
		
		combVender.setWidth(100, Unit.PERCENTAGE);
		combVender.setCaption("Vaccine vender");
		combVender.setItems(Gutil.listVacciVender);
		combVender.setValue(vacci.getVender());
		
		txtElseVender.setWidth(100, Unit.PERCENTAGE);
		txtElseVender.setCaption("Other vender, please specify");
		txtElseVender.setValue(vacci.getElseVender());
		
		//
		layoutRow.setWidth(100, Unit.PERCENTAGE);
		//
		dateVacci.addValueChangeListener(e -> {
			if(e.getValue() != null) {
				vacci.setVacciDate(e.getValue().toString());
			}else {
				vacci.setVacciDate("");
			}
		});
		
		combVender.addValueChangeListener(e -> {
			vacci.setVender(e.getValue());
		});
		
		txtElseVender.addValueChangeListener(e ->{
			vacci.setElseVender(e.getValue().toString());
		});
		
		txtPlaceVacci.addValueChangeListener(e ->{
			vacci.setPlaceVacci(e.getValue().toString());
		});
		
		//新增或刪除列
		btn.addClickListener(e ->{
			try {
				if(btn.getIcon() == VaadinIcons.MINUS
						|| btn.getIcon() == VaadinIcons.TRASH) {
					theCase.getListVacci().remove(vacci);
					//
					gridLayout.removeComponent(layoutRow);
					gridLayout.removeComponent(btn);
					//
					gridLayout.setHideEmptyRowsAndColumns(true);
				}else {
					gridLayout.setRows(gridLayout.getRows() + 1);
					Vacci vacci2 = new Vacci();
					vacci2.setTimeStamp(sdfSSS.format(new java.util.Date()));
					theCase.getListVacci().add(vacci2);
					//
					Gutil.addVacciRow(gridLayout, vacci2, theCase);
				}
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
	}	

	private static void addVacciRow(GridLayout gridLayout,
			Vacci vacci,
			CaseData theCase) throws Exception{
		gridLayout.setRows(gridLayout.getRows() + 1);
		//資料元件
		DateField dateVacci2 = new DateField();
		TextField txtPlaceVacci2 = new TextField();
		ComboBox<String> combVender2 = new ComboBox<>();
		combVender2.setItems(Gutil.listVacciVender);
		TextField txtElseVender2 = new TextField();
		
		Button btnDel = new Button(null, VaadinIcons.TRASH);
		btnDel.setDescription("Remove this row");
		//排 版面
		VerticalLayout layoutRow2 = new VerticalLayout();
		layoutRow2.setWidth(100, Unit.PERCENTAGE);
		layoutRow2.setMargin(false);
		layoutRow2.setSpacing(false);
		//
		CssLayout csslayout = new CssLayout();
		csslayout.setResponsive(true);
		csslayout.setWidth(100, Unit.PERCENTAGE);
		csslayout.addStyleName("cols-4");
		{
			VerticalLayout vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(dateVacci2);
			csslayout.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtPlaceVacci2);
			csslayout.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(combVender2);
			csslayout.addComponent(vl);
			//
			vl = new VerticalLayout();
			vl.setWidth(100, Unit.PERCENTAGE);
			vl.setMargin(false);
			vl.addComponent(txtElseVender2);
			csslayout.addComponent(vl);
		}
		layoutRow2.addComponent(csslayout);
		//
		gridLayout.addComponent(layoutRow2);
		gridLayout.addComponent(btnDel);
		gridLayout.setComponentAlignment(btnDel, Alignment.BOTTOM_CENTER);
		//Keypress 事件處理
		try {
			Gutil.prepareVacciEvents(dateVacci2,
					txtPlaceVacci2,
					combVender2,
					txtElseVender2,
					layoutRow2,
					btnDel,
					gridLayout,
					vacci,
					theCase);
		}catch(Exception ex) {
			Gutil.handleException(ex);
		}
	}
	
	public static void addUploaderToFrame(BrowserFrame frame,
			FrameFile frameFile,
			File dirUpload) {
		VerticalLayout paLayout = (VerticalLayout)frame.getParent();
		{
			HorizontalLayout hLayout = new HorizontalLayout();
			hLayout.setWidth(100, Unit.PERCENTAGE);
			hLayout.setMargin(false);
			hLayout.setSpacing(true);
			{
				PdfUploader uploader = new PdfUploader(frame,
						frameFile,
						dirUpload);
				
				Upload upload = new Upload(null, uploader);
				if(frame.getId().equals(Gutil.FILE_HEADER_PHOTO)) {
					upload.setButtonCaption("Upload");
				}else {
					upload.setButtonCaption("Select file to upload");
				}
				upload.addStyleName(ValoTheme.BUTTON_LINK);
				upload.addSucceededListener(uploader);

				hLayout.addComponent(upload);
				hLayout.setComponentAlignment(upload, Alignment.TOP_RIGHT);
				//
				Button btnDel = new Button("Clear");
				btnDel.addStyleName(ValoTheme.BUTTON_LINK);
				btnDel.addClickListener(e ->{
					if(frame.getSource() == null) return;
					//
					MessageBox.createQuestion().withCaption("Remove content").withMessage("Are you sure to remove existed contents ?")
						.withYesButton(()->{
							try {
								frameFile.setHeaderName("");
								frameFile.setPdfDoc(null);
								//
								frame.setSource(null);
							}catch(Exception ex) {
								Gutil.handleException(ex);
							}
						})
						.withCancelButton()
						.open();
				});
				hLayout.addComponent(btnDel);
				hLayout.setComponentAlignment(btnDel, Alignment.TOP_RIGHT);
			}
			paLayout.addComponent(hLayout);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addEmptyCheckerRuleEnglish(List<com.vaadin.ui.Component> list, Binder<?> binder) {
		for(Component c: list) {
			if(c instanceof com.vaadin.ui.TextField) {
				((com.vaadin.ui.TextField)c).setRequiredIndicatorVisible(true);
				binder.forMemberField((com.vaadin.ui.TextField)c).withValidator(s -> !Gutil.isEmpty(s), (c.getCaption()==null ? "column in red " : c.getCaption()) + " cannot be empty !");
			}else if(c instanceof com.vaadin.ui. DateField) {
				((com.vaadin.ui.DateField)c).setRequiredIndicatorVisible(true);
				binder.forMemberField((com.vaadin.ui.DateField)c).withValidator(s -> s != null, (c.getCaption()==null ? "date column" : c.getCaption()) + " is empty !");
			}else if(c instanceof com.vaadin.ui.ComboBox) {
				((com.vaadin.ui.ComboBox<String>)c).setRequiredIndicatorVisible(true);
				binder.forMemberField((com.vaadin.ui.ComboBox<String>)c).withValidator(s -> s != null && !Gutil.isEmpty(s), (c.getCaption()==null ? "column in red " : c.getCaption()) + " cannot be empty !");
			}else if(c instanceof com.vaadin.ui.RadioButtonGroup) {
				((com.vaadin.ui.RadioButtonGroup<String>)c).setRequiredIndicatorVisible(true);
				binder.forMemberField((com.vaadin.ui.RadioButtonGroup<String>)c).withValidator(s -> s != null && !Gutil.isEmpty(s), (c.getCaption()==null ? "column in red " : c.getCaption()) + " cannot be empty !");
			}else if(c instanceof com.vaadin.ui.TextArea) {
				((com.vaadin.ui.TextArea)c).setRequiredIndicatorVisible(true);
				binder.forMemberField((com.vaadin.ui.TextArea)c).withValidator(s -> !Gutil.isEmpty(s), (c.getCaption()==null ? "column in red " : c.getCaption()) + " cannot be empty !");
			}
		}
	}

	public static javax.mail.Session getMailSession(CtxProperty ctxProp) throws Exception{
		if(ctxProp.smtpAuth) {
			return Gutil.getMailSessionAuth(ctxProp.getSmtpHost(),
											ctxProp.getSmtpPort(),
											ctxProp.getStarttls(),
											ctxProp.getSmtpUser(),
											ctxProp.getSmtpPwd());
		}else {
			return Gutil.getMailSessionNoAuth(ctxProp.getSmtpHost(),
											  ctxProp.getSmtpPort(),
											  ctxProp.getStarttls());
		}
	}

	public static javax.mail.Session getMailSessionAuth(String smtpHost,
					String smtpPort,
					String starttls,
					final String smtpUser,
					final String smtpPwd) throws Exception{
		Properties properties = new Properties();
		//properties.put("mail.smtp.submitter", authenticator.getPasswordAuthentication().getUserName());
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", starttls);
		properties.put("mail.smtp.starttls.required", starttls);
		properties.put("mail.smtp.host", smtpHost);
		properties.put("mail.smtp.port", smtpPort);
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
		
		javax.mail.Session mailSession = javax.mail.Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpUser, smtpPwd);
			}
		});
		//return javax.mail.Session.getInstance(properties, authenticator);
		return mailSession;
	}
	
	public static javax.mail.Session getMailSessionNoAuth(String smtpHost, String smtpPort, String startTls) throws Exception{
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "false");
	    //Put below to false, if no https is needed
	    properties.put("mail.smtp.starttls.enable", startTls);
	    properties.put("mail.smtp.host", smtpHost);
	    properties.put("mail.smtp.port", smtpPort);
	    properties.put("mail.smtp.ssl.trust", "*");
	    //
		return javax.mail.Session.getInstance(properties);
	}	
	
	public static javax.mail.Session getMailSessionNoAuth(String smtpHost, String smtpPort) throws Exception{
		Properties properties = new Properties();
		properties.put("mail.smtp.auth", "false");
	    //Put below to false, if no https is needed
	    //properties.put("mail.smtp.starttls.enable", startTls);
	    properties.put("mail.smtp.host", smtpHost);
	    properties.put("mail.smtp.port", smtpPort);
	    properties.put("mail.smtp.ssl.trust", "*");
	    //
		return javax.mail.Session.getInstance(properties);
	}	
	
	//多個 mail 時以 ; 區隔
	public static Message sendMailTLS(String toMailAddr,
					String subject,
					String strHTML,
					javax.mail.Session session,
					InternetAddress fromMailAddr,
					String strCcAddr) throws Exception{
		List<String> listTo = new ArrayList<>();
		if(toMailAddr.trim().contains(";")) {
			String[] ss = toMailAddr.split(";");
			for(String s: ss) {
				if(s.trim().isEmpty()) continue;
				//
				//if(!Gutil.isValidEmail(s.trim().replaceAll(" ", "")))
				//	throw new MyException(s + " 不是有效的郵件帳號。");
				//
				listTo.add(s.trim().replaceAll(" ", ""));
			}
		}else {
			listTo.add(toMailAddr.trim().replaceAll(" ", ""));
		}
		
		List<String> listCC = new ArrayList<>();
		if(strCcAddr != null && !strCcAddr.trim().isEmpty()){
			if(strCcAddr.contains(";")) {
				String[] ss = strCcAddr.split(";");
				for(String s: ss) {
					if(s.trim().isEmpty()) continue;
					listCC.add(s.trim().replaceAll(" ", ""));
				}
			}else {
				listCC.add(strCcAddr.trim().replaceAll(" ", ""));
			}
		}
		return sendMailTLS(listTo, subject, strHTML, session, fromMailAddr, listCC, null);
	}

	public static Message sendMailTLS(List<String> listMailTo,
					String subject,
					String strHTML,
					javax.mail.Session session,
					InternetAddress fromMailAddr,
					List<String> listCC,
					List<MailAttach> listAttach) throws Exception{
		Message message = new MimeMessage(session);
		message.setFrom(fromMailAddr);
		for(String toMailAddr: listMailTo) {
			try {
				message.addRecipients(Message.RecipientType.TO,	InternetAddress.parse(toMailAddr.trim().replaceAll(" ", ""), false));
			}catch(Exception ex) {
			}
		}
		if(listCC != null && !listCC.isEmpty()){
			for(String toCC: listCC) {
				try {
					message.addRecipients(Message.RecipientType.CC,	InternetAddress.parse(toCC.trim().replace(" ", "")));
				}catch(Exception ex) {
				}
			}
		}
		message.setSubject(MimeUtility.encodeText(subject, MimeUtility.mimeCharset("UTF-8"), null));
		if(listAttach == null || listAttach.isEmpty()) {
			message.setContent(strHTML, "text/html; charset=utf-8");
		}else {
			BodyPart messageBodyPart = new MimeBodyPart();
			// Now set the actual message
			messageBodyPart.setContent(strHTML, "text/html; charset=utf-8");
			// Create a multipar message
			Multipart multipart = new MimeMultipart();
			// Set text message part
			multipart.addBodyPart(messageBodyPart);
			// Part two is attachment
			for(MailAttach ma: listAttach) {
				messageBodyPart = new MimeBodyPart();
				javax.activation.DataSource ds = new ByteArrayDataSource(ma.getInStream(), "application/x-any");
				messageBodyPart.setDataHandler(new DataHandler(ds));
				messageBodyPart.setFileName(MimeUtility.encodeText(ma.getFileName(), "UTF-8", "B"));
		
				multipart.addBodyPart(messageBodyPart);
			}
			//
			// Send the complete message parts
			message.setContent(multipart);
		}		
		Transport.send(message);
		//
		return message;
	}	
	
	public static String getCurrentTime() throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
		sdf.setTimeZone(timeZone);
		return sdf.format(new java.util.Date());
	}
}
