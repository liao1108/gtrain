package com.itez.vghtc.gtrainman;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.imgscalr.Scalr;

import com.aspose.words.HtmlSaveOptions;
import com.aspose.words.SaveFormat;
import com.google.gson.Gson;
import com.itez.minioutils.MinioUtils;
import com.itez.vghtc.util.CtxProperty;
import com.itez.vghtc.util.Gutil;
import com.itez.vghtc.util.MyException;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletService;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import de.steinwedel.messagebox.MessageBox;
import io.minio.messages.Item;

public class MyUtil {

	public static SimpleDateFormat sdfss = new SimpleDateFormat("yyyyMMddHHmmss");

	public static final String COOKIE_NAME = "GlobalClinicStudy2";
	public static final String SPACE_SETTING = "設定區";
	
	public static void createBannerLayout(HorizontalLayout hlTitle,
			String formTitle,
			Button btnLogout,
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
			//
			btnLogout.setStyleName(ValoTheme.BUTTON_LINK);
			btnLogout.addClickListener(e ->{
				MessageBox.createQuestion().withCaption("登出")
				.withMessage("確定登出本系統?")
				.withYesButton(() ->{
					try {
						MyUtil.logout(UI.getCurrent(),
								MyUtil.COOKIE_NAME);
					}catch(Exception ex) {
						Gutil.handleException(ex);
					}
				})
				.withCancelButton()
				.open();
			});
			hlTitle.addComponent(btnLogout);
			hlTitle.setComponentAlignment(btnLogout, Alignment.MIDDLE_RIGHT);
			btnLogout.setVisible(false);
		}
	}

    public static User getMyInfoFromCookie(String cookieName) throws Exception{
    	Gson gson = new Gson();
    	//
    	Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
    	if(cookies == null) return null;
    	
		// Iterate to find cookie by its name
		for (Cookie cookie : cookies) {
		    if (cookie.getName().equalsIgnoreCase(cookieName)) {
		    	if(cookie.getValue() != null && !cookie.getValue().isEmpty()) {
		    		String strBase64 = new String(java.util.Base64.getDecoder().decode(cookie.getValue().trim()));
		    		return gson.fromJson(strBase64, User.class);
		    	}
		    }
		}
		return null;
    }
	
    /*
	public static Session getAlfrescoSession(String alfHost, String uid, String pwd) throws Exception{
		Map<String, String> parameter = new HashMap<String, String>();

		// Set the user credentials
		parameter.put(SessionParameter.USER, uid);
		parameter.put(SessionParameter.PASSWORD, pwd);

		// Specify the connection settings
		//parameter.put(SessionParameter.ATOMPUB_URL, PropsUtil.get("alfresco.host") + "/alfresco/service/cmis");
		parameter.put(SessionParameter.ATOMPUB_URL, alfHost + "/alfresco/cmisatom");
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		// Set the alfresco object factory
		parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

		// Create a session
		SessionFactory factory = SessionFactoryImpl.newInstance();
		List<Repository> list = null;
		try {
			list = factory.getRepositories(parameter);
		}catch(Exception _ex) {
		}
		if(list == null) return null;
		//
		return list.get(0).createSession();
	}
    */
    
    public static void saveAuthCookieInSession(String cookieName, User user) throws Exception {
    	Gson gson = new Gson();
    	//
    	String val = java.util.Base64.getEncoder().encodeToString(gson.toJson(user).getBytes());
    	//
    	Cookie cookie = new Cookie(cookieName, val);
    	cookie.setMaxAge(60 * 60 * 24);		//一天
    	//cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
    	cookie.setPath("/");
    	VaadinService.getCurrentResponse().addCookie(cookie);    	
    }

    public static void logout(com.vaadin.ui.UI ui,
					String cookieName) throws Exception{
		MyUtil.removeAuthCookieFromSession(cookieName);
		ui.getSession().close();
		//
		java.net.URI uri = Page.getCurrent().getLocation();
		java.net.URI uriDest = new java.net.URI(uri.getScheme(),
									uri.getAuthority(),
									uri.getPath(),
									null, // Ignore the query part of the input url
									uri.getFragment());
		ui.getPage().setLocation(uriDest);
	}
    
    public static void removeAuthCookieFromSession(String name) throws Exception {
    	if(null == VaadinService.getCurrentRequest().getCookies()) {
    		//throw new MyException("VaadinService.getCurrentRequest().getCookies() 是空值。");
    		return;
    	}
    	//
    	Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
   	  	boolean found = false;
    	for (Cookie cookie : cookies) {
    	    if (!name.equalsIgnoreCase(cookie.getName())) continue;
    	    //
    	    cookie.setValue(null);
    	    // By setting the cookie maxAge to 0 it will deleted immediately
    	    cookie.setMaxAge(0);
    	    cookie.setPath("/");
    	    VaadinService.getCurrentResponse().addCookie(cookie);
    	    found = true;
    	    break;
    	}
    	if(!found) System.out.println("Cookie: " + name + " 不存在！");
    }

	public static Item findImageItemByHeader(String caseId, String header, CtxProperty ctxProp) throws Exception{
		Item ret  =null;
		//
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			if(!Gutil.isImageFile(item.objectName())) continue;
			//
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.startsWith(header)) {
				ret = item;
				break;
			}
		}
		//
		return ret;
	}
	
	public static Item findPdfItemByHeader(String caseId, String header, CtxProperty ctxProp) throws Exception{
		Item ret  =null;
		//
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			if(!Gutil.isPdfFile(item.objectName())) continue;
			//
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.startsWith(header)) {
				ret = item;
				break;
			}
		}
		//
		return ret;
	}

	public static Item findWordItemByHeader(String caseId, String header, CtxProperty ctxProp) throws Exception {
		Item ret  =null;
		//
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			if(!Gutil.isWordFile(item.objectName())) continue;
			//
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.startsWith(header)) {
				ret = item;
				break;
			}
		}
		//
		return ret;
	}
	
	public static Item findMailItemByHeader(String caseId, String header, CtxProperty ctxProp) throws Exception {
		Item ret  =null;
		//
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			if(!item.objectName().endsWith("eml")) continue;
			//
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.startsWith(header)) {
				ret = item;
				break;
			}
		}
		//
		return ret;
	}		
	
	public static Item findAnyItemByHeader(String caseId, String header, CtxProperty ctxProp) throws Exception {
		Item ret  =null;
		//
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.startsWith(header)) {
				ret = item;
				break;
			}
		}
		//
		return ret;
	}
	
	public static Item findAnyItemByFileName(String caseId, String fileName, CtxProperty ctxProp) throws Exception {
		Item ret  =null;
		//
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
		for(Item item: listItem) {
			if(item.isDir()) continue;
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.equalsIgnoreCase(fileName)) {
				ret = item;
				break;
			}
		}
		//
		return ret;
	}
	
	public static Item getWordItemFromSetting(String name, CtxProperty ctxProp) throws Exception{
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
		for(Item item: listItem){
			if(item.isDir()) continue;
			if(!Gutil.isWordFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name)) {
				return item;
			}
    	}
    	return null;
    }
    
	public static Item getWordItemFromSetting(String name1, String name2, CtxProperty ctxProp) throws Exception{
    	List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
		for(Item item: listItem){
			if(item.isDir()) continue;
			if(!Gutil.isWordFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name1) 
					&& item.objectName().contains(name2)) {
				return item;
			}
    	}
    	return null;
    }
	
	public static Item getTxtItemFromSetting(String name, CtxProperty ctxProp) throws Exception {
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
		for(Item item: listItem){
			if(item.isDir()) continue;
			if(!Gutil.isTxtFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name)) {
				return item;
			}
    	}
    	return null;
    }
	
	public static Item getTxtItemFromSetting(String name1, String name2, CtxProperty ctxProp) throws Exception{
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
		for(Item item: listItem){
			if(item.isDir()) continue;
			if(!Gutil.isTxtFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name1)
					&&  item.objectName().contains(name2)) {
				return item;
			}
    	}
    	return null;
    }
	
	public static Item getPdfItemFromSetting(String name, CtxProperty ctxProp) throws Exception{
		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
		for(Item item: listItem){
			if(item.isDir()) continue;
			if(!Gutil.isPdfFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name)) {
				return item;
			}
    	}
    	return null;
    }
    
    public static Item getExcelItemFromSetting(String name, CtxProperty ctxProp) throws Exception{
    	List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
    	for(Item item: listItem){
    		if(item.isDir()) continue;
			if(!Gutil.isExcelFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name)) {
				return item;
			}
    	}
    	return null;
    }
    
    public static Item getExcelItemFromSetting(String name1, String name2, CtxProperty ctxProp) throws Exception {
    	List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, MyUtil.SPACE_SETTING, ctxProp.getMinioClient());
    	for(Item item: listItem){
    		if(item.isDir()) continue;
			if(!Gutil.isExcelFile(item.objectName())) continue;
			//
			if(item.objectName().contains(name1)
					&& item.objectName().contains(name2)) {
				return item;
			}
    	}
    	return null;
    }
	
    public static Item getPdfDocByHeaderName(String alfDocId, String headerName, CtxProperty ctxProp) throws Exception {
    	List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, alfDocId, ctxProp.getMinioClient());
    	for(Item item: listItem){
    		if(item.isDir()) continue;
			if(!Gutil.isPdfFile(item.objectName())) continue;
			//
			String pureName = item.objectName().substring(item.objectName().lastIndexOf("/") + 1);
			if(pureName.startsWith(headerName)) {
				return item;
			}
    	}
    	return null;
    }
    
	
	//刪除 Profile.json
	public static int removeProfileFromDB(String siteId,
										  String caseId,
										  Connection conn) throws Exception{
		String sql = "DELETE FROM tams WHERE module_name=? AND doc_id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, siteId);
		ps.setString(2, caseId);
		ps.execute();
		//
		return ps.getUpdateCount();
	}
	
	public static int updateProfileAndStage(String stageNo,
			 String siteId,
			 String caseId,
			 Connection conn) throws Exception{
		if(stageNo.equals("") || siteId.equals("") || caseId.equals("")) throw new MyException("階段、站台與案件ID不能空白。");
		//
		SimpleDateFormat sdfss = new SimpleDateFormat("yyyyMMddHHmmss");
		sdfss.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		String sql = "UPDATE tams SET stage_name = ?,"					//1
					+ " last_update = ? "					//2
					//+ " profile_json = TO_JSON(?::JSON) "	//3
					+ " WHERE module_name=? AND doc_id = ?";	//4、5
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, stageNo);
		ps.setString(2, sdfss.format(new java.util.Date()));
		//ps.setString(3, json);
		ps.setString(3, siteId);
		ps.setString(4, caseId);
		int cc = ps.executeUpdate();
		ps.close();
		//
		return cc;
	}
	
	public static void removeCaseFile(String caseId, String fileName, CtxProperty ctxProp) throws Exception{
		MinioUtils.deleteObject(ctxProp.getSiteId(), caseId + "/" + fileName, ctxProp.getMinioClient());
	}
	
	public static float getFileKb(Item item){
		try{
			return Math.round( item.size() / 1024f * 100f ) / 100f;
		}catch(Exception ex){
			ex.printStackTrace();
			return 0f;
		}
	}

    public static void drawScaledImage2(BufferedImage biSrc, BufferedImage biCanvas) throws Exception{
        int srcW = biSrc.getWidth();
        int srcH = biSrc.getHeight();
         
        double imgAspect = (double)srcH / (double)srcW;
 
        int canvasWidth = biCanvas.getWidth();
        int canvasHeight = biCanvas.getHeight();
         
        double canvasAspect = (double)canvasHeight / (double)canvasWidth;
 
        int x1 = 0; // top left X position
        int y1 = 0; // top left Y position
        int x2 = 0; // bottom right X position
        int y2 = 0; // bottom right Y position
        //------------------ 無須壓縮 --------------------
        if (srcW < canvasWidth && srcH < canvasHeight) {
            // the image is smaller than the canvas
            x1 = (canvasWidth - srcW)  / 2;
            y1 = (canvasHeight - srcH) / 2;
            x2 = x1 + srcW;
            y2 = y1 + srcH;
            //
            biCanvas.getGraphics().drawImage(biSrc, x1, y1, x2, y2, 0, 0, srcW, srcH, null);
            return;
        }
        //---------------------- 需壓縮--------------------
    	int destW = 0;
    	int destH = 0;
    	if (canvasAspect > imgAspect) {	//相框較高，相片充滿框寬
        	destW = canvasWidth;
        	destH = (int)Math.round((double)srcH * (double)destW / (double)srcW);
        	//
        	y1 = (canvasHeight - destH) / 2;
        	y2 = y1 + destH;
        	x2 = canvasWidth;
        } else {	//相框較扁，相片充滿框高
        	destH = canvasHeight;
        	destW = (int)Math.round((double)srcW * (double)destH / (double)srcH);
            //
        	x1 = (canvasWidth - destW) / 2;
        	x2 = x1 + destW;
        	y2 = canvasHeight;
        }
    	//錯誤
        if(x2 > canvasWidth || y2 > canvasHeight || destW == 0 || destH == 0) {
        	String msg = "相框高寬(" + canvasHeight + "x" + canvasWidth + ")、原相片高寬(" + srcH + "x" + srcW + ")；轉換後相片高寬(" + destH + "x" + destW + ")";  
        	throw new MyException(msg);
        }
        //
        int ww = x2 - x1 + 1;
        int hh = y2 - y1 + 1;
        BufferedImage scaledBi = Scalr.resize(biSrc,
        										Scalr.Method.ULTRA_QUALITY,
        										Scalr.Mode.FIT_EXACT,
        										ww,
        										hh);
        biCanvas.getGraphics().drawImage(scaledBi, x1, y1, x2, y2, 0, 0, ww, hh, null);
    }
	
	/*
    public static org.apache.chemistry.opencmis.client.api.Document saveToAlfresco(File file,
			String fileNameDest,				
			AlfrescoFolder fdDest,
			org.apache.chemistry.opencmis.client.api.Session alfSession) throws Exception{
		if(fdDest == null) throw new MyException("後端檔案夾不存在！");
		//
		byte[] bytes = Files.readAllBytes(file.toPath());
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
		baos.write(bytes, 0, bytes.length);
		//
		MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
		return MyUtil.saveToAlfresco(fileNameDest,
					baos,
					fileTypeMap.getContentType(file.getName()),
					fdDest,
					alfSession);
	}

	public static org.apache.chemistry.opencmis.client.api.Document saveToAlfresco(String fileName,
			ByteArrayOutputStream baos,
			String mimeType,
			AlfrescoFolder fdDest,
			org.apache.chemistry.opencmis.client.api.Session alfSession) throws Exception{
		if(fdDest == null) throw new MyException("檔案 " + fileName + " 存入之檔案夾為空值。");
		//
		AlfrescoDocument alfDoc = null;
		String filePath = fdDest.getPath() + "/" + fileName;
		try{
			alfDoc = (AlfrescoDocument)alfSession.getObjectByPath(filePath);
		}catch(Exception _ex){}
		//
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ContentStream cs = alfSession.getObjectFactory().createContentStream(fileName, baos.toByteArray().length, mimeType, bais);
		if(alfDoc == null){
			Map<String, String> props = new HashMap<String, String>();
			props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
			props.put(PropertyIds.NAME, fileName);
			//
			org.apache.chemistry.opencmis.client.api.Document doc = null;
			try {
				doc = fdDest.createDocument(props, cs, VersioningState.MAJOR);
			}catch(Exception ex) {
				throw new MyException(filePath + " 建立失敗。");
			}
			return doc;
		}else{
			if(alfDoc.hasAspect("P:cm:lockable")){
				TimeUnit.SECONDS.sleep(3);
				alfDoc.cancelCheckOut();
			}
			//
			org.apache.chemistry.opencmis.client.api.Document pwc = null;
			int ccTimes = 0;
			while(pwc == null && ccTimes < 5) {
				try{
					pwc = (org.apache.chemistry.opencmis.client.api.Document)alfSession.getObject(alfDoc.checkOut());
				}catch(Exception _ex){
					//if(ccTimes == 5) throw new MyException("檔案【" + fdDest.getPath() + "/" + fileName + "】無法簽出，原因為" + _ex.getMessage() + "...");
					//System.out.println(_ex.getMessage());
					Thread.sleep(2000);
					if(alfDoc.isVersionSeriesCheckedOut()) alfDoc.cancelCheckOut();
					ccTimes++;
				}
			}

			if(pwc == null) throw new MyException("檔案【" + fdDest.getPath() + "/" + fileName + "】正被其他用戶修改中，請稍後再試...");
			//
			ObjectId objId = pwc.checkIn(false, null, cs, null);
			return (org.apache.chemistry.opencmis.client.api.Document)alfSession.getObject(objId);
		}
	}   
	*/
	
	public static void createInfoMessage(String caption, String msg) {
		MessageBox.createInfo().withCaption(caption)
				.withMessage(msg)
				.withOkButton()
				.open();
	}
	
	public static void createWarningMessage(String caption, String msg) {
		MessageBox.createWarning().withCaption(caption)
				.withMessage(msg)
				.withOkButton()
				.open();
	}
	
	public static String toTWDate(LocalDate date){
		if(date == null) return "";
		//
		int year = date.getYear() - 1911;
		int month = date.getMonth().getValue();
		int day = date.getDayOfMonth();
		return year + " 年 " + month + " 月 " +  day + " 日"; 
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

	//取得應用程式URL根路徑
	public static String getContextPath(){
		HttpServletRequest req = VaadinServletService.getCurrentServletRequest();
		//String scheme = req.getScheme();             	// http
		String scheme = "https";
		String serverName = req.getServerName();     	// hostname.com
		int serverPort = req.getServerPort();        	// 80
		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);
		if (serverPort != 80 && serverPort != 443) url.append(":").append(serverPort);
		//
		return url.toString();
	}
	
	public static String getServletName(){
		HttpServletRequest req = VaadinServletService.getCurrentServletRequest();
		return req.getContextPath();
	}
    
	public static String convertDocToHTML(com.aspose.words.Document docWord) throws Exception{
		HtmlSaveOptions saveOptions = new HtmlSaveOptions(SaveFormat.HTML);
        saveOptions.setExportImagesAsBase64(true);
        
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	docWord.save(baos, saveOptions);
    	String strHTML = new String(baos.toByteArray(), StandardCharsets.UTF_8);
    	
    	//字型轉成英文名稱, webView 才可正確顯示
    	//Iterator<String> it = mapFont.keySet().iterator();
    	//while(it.hasNext()){
    	//	String cName = it.next();
    	//	String eName = mapFont.get(cName);
    	//	//
    	//	strHTML = strHTML.replaceAll(cName, eName);
    	//}
    	//
    	return strHTML;
	}
	
	public static void performDownloadWithDialog(InputStream is, String fileName) throws Exception{
		if(is == null) throw new MyException("檔案不存在。");
		//
		StreamResource res1 = new StreamResource(new StreamResource.StreamSource() {
			private static final long serialVersionUID = 1L;
			@Override
            public InputStream getStream() {
				return is;
            }
        }, fileName);
		
		String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
		StreamResource res2 = new StreamResource(new StreamResource.StreamSource() {
			private static final long serialVersionUID = 1L;
			@Override
            public InputStream getStream() {
				return is;
            }
        }, "F" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "." + extName );
		
		//
		FileDownloader fdw1 = new FileDownloader(res1);
		fdw1.setOverrideContentType(false);
		
		FileDownloader fdw2 = new FileDownloader(res2);
		fdw2.setOverrideContentType(false);
		//
		final Window window = new Window("下載「" + fileName + "」");
		window.setWidth(600, Unit.PIXELS);
		window.setHeight(160, Unit.PIXELS);
		window.setModal(true);
		window.center();
		
		VerticalLayout vlWin = new VerticalLayout();
		vlWin.setMargin(true);
		vlWin.setSpacing(false);
		vlWin.setStyleName("halftone-whip");
		vlWin.setSizeFull();
		{
			Button btn1 = new Button("開始下載");
			btn1.setIcon(new ClassResource("/download32.png"));
			fdw1.extend(btn1);
			
			vlWin.addComponent(btn1);
			vlWin.setComponentAlignment(btn1, Alignment.MIDDLE_CENTER);
			//
			Button btn2 = new Button("如無法下載，請點此處以非中文檔名下載");
			fdw2.extend(btn2);
			btn2.addStyleName(ValoTheme.BUTTON_LINK);
			vlWin.addComponent(btn2);
			vlWin.setComponentAlignment(btn2, Alignment.MIDDLE_CENTER);
		}
		window.setContent(vlWin);
		//
		UI.getCurrent().addWindow(window);
	}
	
	public static void performDownloadWithDialog(ByteArrayOutputStream baos, String fileName) throws Exception{
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		MyUtil.performDownloadWithDialog(bais, fileName);
		bais.close();
	}

}
