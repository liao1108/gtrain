package com.itez.vghtc.gtrainman;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.sql.DataSource;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.aspose.words.Bookmark;
import com.aspose.words.ControlChar;
import com.aspose.words.ImportFormatMode;
import com.aspose.words.License;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.PaperSize;
import com.aspose.words.Run;
import com.aspose.words.SaveFormat;
import com.aspose.words.Shape;
import com.aspose.words.StyleIdentifier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itez.minioutils.MinioUtils;
import com.itez.vghtc.util.CaseData;
import com.itez.vghtc.util.CtxProperty;
import com.itez.vghtc.util.DiviChoice;
import com.itez.vghtc.util.Gutil;
import com.itez.vghtc.util.MyException;
import com.itez.vghtc.util.MyLocalDateAdapter;
import com.itez.vghtc.util.Gutil.DeviceType;
import com.itez.vghtc.util.MailAttach;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.ShortcutListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Layout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import de.steinwedel.messagebox.MessageBox;
import io.minio.MinioClient;
import io.minio.messages.Item;

@Theme("mytheme")
public class ManagerUI extends UI {
	private static final long serialVersionUID = 1L;
	
	DataSource ds = null;
	final String JNDI_DB = "java:comp/env/jdbc/seduDB";
	
	CtxProperty ctxProp = new CtxProperty();
	
	final String DOC_HEAD_TRAIN_PLAN_GOV = "報衛福部進修計畫書";
	final String DOC_HEAD_TRAIN_APPLY_GOV = "報衛服部申請表";
	final String DOC_HEAD_APPLY_VGHTC = "來院進修申請表";
	//final String DOC_HEAD_PROFILE_CARD = "人員資料卡";
	//final String DOC_HEAD_APPLY_ATTACH = "證件資料表";
	//final String DOC_HEAD_ACCOMM_SHARE = "單身宿舍申請表";
	//final String DOC_HEAD_ACCOMM_SINGLE = "學人宿舍申請表";
	//final String DOC_HEAD_REVIEW_FORM_PROFESSIONAL = "外籍醫事人員會核單";
	final String DOC_HEAD_INVITE_LETTER = "邀請函";
	final String DOC_HEAD_XRAY_FLIGHT = "胸腔X光報告與機票";
	//
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdfss = new SimpleDateFormat("yyyyMMddHHmmss");
	SimpleDateFormat sdfSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	SimpleDateFormat sdfssDash = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	Button btnLogout = new Button("登出");

	User currUser = new User();		//登入者個人資料
	
	File dirUpload = null;				//圖檔上傳暫存區
	Gson gson = null;
	
	Tab tabList = null;					//目前案件列表
	ComboBox<String> combCmd = null;
	
	boolean isClinicalResponse = false;
	String caseIdResponse = "";
	
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
			//時區
			TimeZone timeZone = TimeZone.getTimeZone("Asia/Taipei");
			sdf.setTimeZone(timeZone);
			sdfss.setTimeZone(timeZone);
			sdfSSS.setTimeZone(timeZone);
			sdfssDash.setTimeZone(timeZone);
			
			//ASPOSE.Words for Java 授權碼
    	   	License license = new License();
    	   	license.setLicense(getClass().getClassLoader().getResourceAsStream("/Aspose.Words.lic"));
			//上傳 暫存目錄
			File dirCurr = new File(".");
			dirUpload = new File(dirCurr.getCanonicalPath() + "/" + "upload");
			if(!dirUpload.exists()) dirUpload.mkdirs();
			//
			//Servlet初始參數
			Gutil.loadServletParameter(ctxProp,
					VaadinServlet.getCurrent().getServletContext());
			//
			ds = (DataSource) new InitialContext().lookup(JNDI_DB);
			gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new MyLocalDateAdapter()).create();
			//
			String formTitle = "外籍專業人士來院進修管理";
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
				MyUtil.createBannerLayout(hlTitle, formTitle, btnLogout, true);
			}else {
				MyUtil.createBannerLayout(hlTitle, formTitle, btnLogout, false);
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
			Iterator<String> it = vaadinRequest.getParameterMap().keySet().iterator();
			while(it.hasNext()){
				//String param = Encode.forHtml(it.next());
				String param = it.next();
				//String val = Encode.forHtml(vaadinRequest.getParameter(param));
				String val = vaadinRequest.getParameter(param);
				if(param.equalsIgnoreCase("clinical")){
					this.isClinicalResponse = true;
				}else if(param.equalsIgnoreCase("docId")){
					this.caseIdResponse = val.trim();
				}
			}

			//臨床單位回覆
			if(this.isClinicalResponse && !this.caseIdResponse.isEmpty()) {
				Connection conn = null;
				try {
					conn = ds.getConnection();
					CaseData theCase = Gutil.getCaseByCaseId(this.caseIdResponse, ctxProp.getSiteId(), conn);
					if(theCase == null || theCase.getCaseId().isEmpty())
						throw new MyException("案件不存在！");
					//
					this.processClinicalResponse(theCase, layoutHero);
				}catch(Exception ex) {
					
				}finally {
					Gutil.close(conn);
				}
			}else {
				this.checkIfLogon(layoutHero);
			}
		}catch(Exception ex) {
    	   Gutil.handleException(ex);
       }
    }
	
	private void checkIfLogon(VerticalLayout layoutHero) throws Exception{
		currUser = MyUtil.getMyInfoFromCookie(MyUtil.COOKIE_NAME);
		if(currUser == null || currUser.userNo.isEmpty()) {
			currUser = new User();
			this.createLoginWindowWithCaptcha(ctxProp,
										currUser,
										ctxProp.getDeviceType(),
										layoutHero);
			return;
		}else {
			MinioClient client = null;
			try {
				client = MinioUtils.getMinioClient(ctxProp.getAlfHost(),
								currUser.userNo,
								currUser.passwd);
			}catch(Exception ex) {
			}
			if(client == null) {
				this.createLoginWindowWithCaptcha(ctxProp,
						currUser,
						ctxProp.getDeviceType(),
						layoutHero);
			}else {
				ctxProp.setMinioClient(client);
				this.jobsAfterAuthentication(layoutHero);
			}
		}
	}	
	
	//身分驗證後的作業
	private void jobsAfterAuthentication(VerticalLayout layoutHero) throws Exception{
		btnLogout.setVisible(true);
		btnLogout.setCaption("登出(" + currUser.getUserNo() + ")");
		btnLogout.setIcon(VaadinIcons.EXIT);
		//
		//this.findAlfrescoFolders(alfSession);
		//
		List<CaseData> listCase4Provider = new java.util.ArrayList<>();
		ListDataProvider<CaseData> ldpCase = new ListDataProvider<>(listCase4Provider);
		//
		ManageForm form = new ManageForm();
		form.setSizeFull();
		form.splitPane.setSplitPosition(100);
		//
		final Button btnSpace = new Button("前往本案儲存區", VaadinIcons.LINK);
		btnSpace.addStyleName(ValoTheme.BUTTON_LINK);
		btnSpace.addClickListener(e ->{
			if(btnSpace.getId().isEmpty()) return;
			//
			Page.getCurrent().open(btnSpace.getId(), "_blank");
		});
		//
		final Grid<CaseData> gridCase = new Grid<>();
		final Grid<MinioDoc> gridFile = new Grid<>();
		
		gridFile.setWidth(99, Unit.PERCENTAGE);
		gridFile.setHeight(100, Unit.PERCENTAGE);
		gridFile.setSelectionMode(SelectionMode.MULTI);
		gridFile.addStyleName("myGridFile");
		gridFile.addStyleName("gridfile-padding");
		gridFile.setResponsive(true);
		
		this.createMinioDocGrid(gridFile, true);
		//
		final ButtonRenderer<CaseData> rendererFiles = new ButtonRenderer<>(e -> {
			try{
				CaseData theCase = e.getItem();
				this.loadCaseFilesIntoGrid(theCase.getCaseId(),
						theCase.getCaseNo(),
						btnSpace,
						gridFile);
				//
				form.splitPane.setSplitPosition(55);
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
		rendererFiles.setHtmlContentAllowed(true);
		
		gridCase.setWidth(99, Unit.PERCENTAGE);
		gridCase.setHeight(100, Unit.PERCENTAGE);
		gridCase.setSelectionMode(SelectionMode.MULTI);
		gridCase.addStyleName("myGridFile");
		gridCase.setResponsive(true);
		//
		gridCase.addColumn(CaseData::getIdx).setId("idx").setCaption("序").setWidth(80d);
		gridCase.addColumn(f -> f.getCaseNo(), new ButtonRenderer<Object>(e -> {
			Connection conn = null;
			try{
				if(e.getItem() == null) throw new MyException("案號找不到。");
				//
				CaseData theCase = (CaseData)e.getItem();
				for(int i=0; i < form.tabSheet.getComponentCount(); i++) {
					if(form.tabSheet.getTab(i).getCaption().contains(theCase.getCaseNo())) {
						form.tabSheet.setSelectedTab(i);
						return;
					}
				}
				conn = ds.getConnection();
				Tab tab = form.tabSheet.addTab(this.createApplyLayout(theCase, conn),
						"案號：" + theCase.getCaseNo(),
						VaadinIcons.FILE_SEARCH);
				tab.setClosable(true);
				form.tabSheet.setSelectedTab(tab);
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}finally {
				Gutil.close(conn);
			}
		})).setCaption("案號").setId("caseNo").setWidth(220d);
		gridCase.addColumn(CaseData::getPersonNameEn).setCaption("申請人").setId("personNameEn").setWidth(230d);
		gridCase.addColumn(CaseData::getNationality).setCaption("國籍").setId("nationality").setWidth(150d);
		gridCase.addColumn(CaseData::getRoleStatus).setCaption("身分別").setId("roleStatus").setWidth(120d);
		gridCase.addColumn(CaseData::getEmail).setCaption("e-mail").setId("email").setWidth(200d);		
		gridCase.addColumn(m -> VaadinIcons.FOLDER_OPEN_O.getHtml(), rendererFiles).setCaption("檔案").setWidth(80d).setStyleGenerator(item -> "v-align-center");
		gridCase.addColumn(CaseData::getGender).setCaption("性別").setId("gender").setWidth(90d);
		gridCase.addColumn(CaseData::getCaseDate).setCaption("申請日期").setId("caseDate");
		gridCase.addItemClickListener(e ->{
			try {
				CaseData theCase = e.getItem();
				this.loadCaseFilesIntoGrid(theCase.getCaseId(),
						theCase.getCaseNo(),
						btnSpace,
						gridFile);
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
		gridCase.setFrozenColumnCount(2);
		this.addGridFooter(gridCase, ldpCase);	//加入Footer資料過濾列
		gridCase.setDataProvider(ldpCase);
		//
		form.layoutListCase.addComponent(gridCase);
		form.layoutListCase.setExpandRatio(gridCase, 1);
		form.layoutListCase.setComponentAlignment(gridCase, Alignment.TOP_LEFT);
		//
		HorizontalLayout hrSpace = new HorizontalLayout();
		hrSpace.setWidth(100, Unit.PERCENTAGE);
		{
			hrSpace.addComponent(btnSpace);
			hrSpace.setExpandRatio(btnSpace, 1);
			//
			Button btnHide = new Button();
			btnHide.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			btnHide.setIcon(VaadinIcons.SIGN_IN_ALT);
			btnHide.addClickListener(e ->{
				form.splitPane.setSplitPosition(100);
			});
			hrSpace.addComponent(btnHide);
		}
		form.layoutListFile.addComponent(hrSpace);
		//form.layoutListFile.setComponentAlignment(btnSpace, Alignment.BOTTOM_RIGHT);

		form.layoutListFile.addComponent(gridFile);
		form.layoutListFile.setExpandRatio(gridFile, 1);
		form.layoutListFile.setComponentAlignment(gridFile, Alignment.TOP_RIGHT);
		//
		
		form.rbgStage.setItems(Gutil.listStage);
		form.rbgStage.addValueChangeListener(e -> {
			gridCase.deselectAll(); //Vaadin 會 Cache 前次 select 的 row object
			//
			String stageNo = String.valueOf(e.getValue().charAt(0));
			this.fetchStageCases(stageNo, gridCase, gridFile, listCase4Provider, ldpCase);
			//
			this.setStageCommands(stageNo, form.combCmd);
		});
		//-------------------- 命令選項 --------------------
		form.combCmd.setPopupWidth(null);
		this.setStageCommands(form.rbgStage.getValue(), form.combCmd);
		//
		form.btnRun.addClickListener(e ->{
			try {
				if(form.combCmd.getValue() == null) return;
				//
				String strCmd = form.combCmd.getValue().trim();
				if(strCmd == null || strCmd.isEmpty()) return;
				//
				this.executeCmd(strCmd,
						form.rbgStage.getValue(),
						gridCase,
						listCase4Provider,
						ldpCase);
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
		//
		layoutHero.removeAllComponents();
		layoutHero.addComponent(form);
	}
	
	/*
	private void findAlfrescoFolders(Session alfSession) throws Exception{
		fdRoot = null;
		try {
			fdRoot = (AlfrescoFolder)alfSession.getObjectByPath("/sites/" + siteName + "/documentlibrary");
		}catch(Exception ex) {
		}
		if(fdRoot == null) throw new MyException("您沒有本站台的權限，請洽系統管理人員處理。");
		//
		for (CmisObject co : fdRoot.getChildren()) {
			if(!(co instanceof org.apache.chemistry.opencmis.client.api.Folder)) continue;
			//
			if(co.getName().contains("設定")){
				fdSetting = (AlfrescoFolder)co;
			}else if(co.getName().contains("資料區")){
				fdData = (AlfrescoFolder)co;
			}else if(co.getName().contains("暫存") || co.getName().contains("下載")){
				fdDownload = (AlfrescoFolder)co;
			}
		}
		if(fdSetting == null) throw new MyException("後台「設定區」找不到。");
		if(fdData == null) throw new MyException("後台「資料區」找不到。");
		if(fdDownload == null) {
			Map<String, String> props = new HashMap<String, String>(2);
			props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
			props.put(PropertyIds.NAME, "暫存區");
			fdDownload = (AlfrescoFolder)fdRoot.createFolder(props);
		}
		//搜尋必要文件樣板
		for(CmisObject co2: fdSetting.getChildren()) {
			if(!(co2 instanceof org.apache.chemistry.opencmis.client.api.Document)) continue;
			//
			if(co2.getName().toLowerCase().endsWith("txt")
					&& co2.getName().contains("郵件帳號")) {
				BufferedReader br = new BufferedReader(new InputStreamReader(((AlfrescoDocument)co2).getContentStream().getStream(), "UTF-8"));
		        String line;
		        while ((line = br.readLine()) != null) {
		        	if(!line.contains("=")) continue;
		            if(line.contains("發信帳號")) {
		            	this.fromAccount = new InternetAddress(line.substring(line.indexOf("=") + 1).trim(), "臺中榮總教學部");
		            }else if(line.contains("副本帳號")) {
		            	this.strCcAccount = line.substring(line.indexOf("=") + 1).trim();
		            }
		        }
		        br.close();
			}
		}
	}
   	*/
   
	private void setStageCommands(String stageName, ComboBox<String> combCmd) {
		combCmd.clear();
		//
		List<String> listCmd = new ArrayList<>();
		if(stageName != null && !stageName.isEmpty()) {
			listCmd.add("02.產出申請人員統計表");
			if(stageName.startsWith("A")) {
				
			}else if(stageName.startsWith("B")) {
				listCmd.add("B1.發送補件通知函");
			}else if(stageName.startsWith("C")) {
				//listCmd.add("C1.產出簽稿會核單");
				listCmd.add("C1.產出來院進修申請表");
				listCmd.add("C2.知會臨床單位承辦人");
			}else if(stageName.startsWith("D")) {
				listCmd.add("D1.產出報衛福部進修申請表");
				listCmd.add("D2.產出邀請函");
				listCmd.add("D3.發送邀請函電子郵件");
				//listCmd.add("C3.產出宿舍申請名單");
			}
			listCmd.add("Y2.案件移至其他階段");
			listCmd.add("Y3.刪除所選案件");
		}
		//
		listCmd.add("Z1.系統設定管理");
		combCmd.setItems(listCmd);
		
	}

	private void executeCmd(String strCmd,
			String stageName,
			Grid<CaseData> gridCase,
			List<CaseData> listCase4Provider,
			ListDataProvider<CaseData> ldpCase) throws Exception{
		if(strCmd.contains("系統設定")) {
			this.openSettingForm();
			return;
		}else if(strCmd.contains("人員統計表")) {
			this.genPeriodReportDialog();
			return;
		}
		//
		if(gridCase.getSelectedItems().isEmpty())
			throw new MyException("請先勾選案件。");
		//
		if(strCmd.contains("產出")) {
			if(strCmd.contains("來院") && strCmd.contains("進修申請表")) {
				this.genVghTcApplyFormDialog(gridCase.getSelectedItems());
			}else if(strCmd.contains("衛福部") && strCmd.contains("進修申請表")) {
				this.genTrainApply4GovDialog(gridCase.getSelectedItems());
			//}else if(strCmd.contains("衛福部") && strCmd.contains("進修計劃書")) {
			//	this.genTrainPlanGovDialog(gridCase.getSelectedItems(), alfSession);
			//}else if(strCmd.contains("會核單")) {
			//	this.genReviewFormDialog(gridCase.getSelectedItems(), alfSession);
			}else if(strCmd.contains("邀請函")) {
				this.genInviteLetterDialog(gridCase.getSelectedItems());
			}
		}else if(strCmd.contains("發送") && 
				(strCmd.contains("郵件") || strCmd.contains("函"))) {
			if(strCmd.contains("補件通知")) {
				this.mailSupplementartLetter(gridCase.getSelectedItems());
			}else if(strCmd.contains("邀請函")) {
				this.mailInviteLetterDialog(gridCase.getSelectedItems());
			}
		}else if(strCmd.contains("移至其他階段")) {
			this.moveCaseDialog(gridCase.getSelectedItems(),
					stageName,
					listCase4Provider,
					ldpCase);
			//取消勾選Cache
			gridCase.deselectAll();
		}else if(strCmd.contains("刪除所選案件")) {
			this.deleteCaseDialog(gridCase.getSelectedItems(),
					listCase4Provider,
					ldpCase);
			//取消勾選Cache
			gridCase.deselectAll();
		}else if(strCmd.contains("知會臨床單位")) {
			this.mailUdTakerLetter(gridCase.getSelectedItems(),
					listCase4Provider,
					ldpCase);
			//取消勾選Cache
			gridCase.deselectAll();
		}
		
	}
	
	private void openSettingForm() {
		final Window window = new Window("系統設定");
		window.setWidth(95, Unit.PERCENTAGE);
		window.setHeight(99, Unit.PERCENTAGE);
		if(ctxProp.getDeviceType() == DeviceType.Pad 
				|| ctxProp.getDeviceType() == DeviceType.Mobile) {
			window.setWidth(100, Unit.PIXELS);
			window.setHeight(100, Unit.PIXELS);
		}
		window.setModal(true);
		window.setClosable(true);
		window.center();
		window.setIcon(VaadinIcons.COG_O);
		//
		SettingForm form = new SettingForm();
		form.setSizeFull();
		form.addStyleName("halftone-blue");
		
		List<String> listParam = new ArrayList<>();
		listParam.add(Gutil.PARAM_CLINIC_ORG);
		listParam.add(Gutil.PARAM_NATION);
		form.rbgParams.setItems(listParam);
		form.rbgParams.addValueChangeListener(e ->{
			Connection conn = null;
			try {
				String paramName = e.getValue();
				//
				conn = ds.getConnection();
				String strJson = this.getSettingFromDB(paramName, conn);
				form.txtJson.setValue(strJson);
				//
				form.layoutUpload.removeAllComponents();
				
				SettingUploader uploader = new SettingUploader(paramName,
						form.txtJson,
						dirUpload,
						ds);
				Upload upload = new Upload(null, uploader);
				upload.setButtonCaption("上傳設定檔");
				upload.addSucceededListener(uploader);
				form.layoutUpload.addComponent(upload);
				form.layoutUpload.setComponentAlignment(upload, Alignment.TOP_RIGHT);
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}finally {
				Gutil.close(conn);
			}
		});
		//
		window.setContent(form);
		UI.getCurrent().addWindow(window);
	}
	
	private String getSettingFromDB(String paramName, Connection conn) throws Exception{
		String strJson = "";
		String sql = "SELECT str_json FROM tams_param WHERE param_name = ?";
		PreparedStatement ps = conn.prepareCall(sql);
		ps.setString(1, paramName);
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			strJson = rs.getString(1);
		}
		rs.close();		
		return strJson;
	}
	
	//案件移至其他階段
	private void moveCaseDialog(Set<CaseData> listCase,
			String currStage,
			List<CaseData> listCase4Provider,
			ListDataProvider<CaseData> ldpCase){
		try {
			final Window _window = new Window("資料移轉");
			_window.setWidth(800, Unit.PIXELS);
			_window.setHeight(300, Unit.PIXELS);
			if(ctxProp.getDeviceType() == DeviceType.Mobile) {
				_window.setWidth(100, Unit.PERCENTAGE);
				_window.setHeight(100, Unit.PERCENTAGE);
			}
			_window.setModal(true);
			_window.center();
			
			VerticalLayout _layout = new VerticalLayout();
			_layout.setMargin(true);
			_layout.setSpacing(true);
			_layout.setSizeFull();
			_layout.addStyleName("halftone-yellow");
			{
				List<String> _list = new ArrayList<>();
				RadioButtonGroup<String> option = new RadioButtonGroup<>();
				option.setCaption("請指定所要移入的階段");
				option.setIcon(VaadinIcons.TRAIN);
				for(String stageName: Gutil.listStage){
					if(stageName.equalsIgnoreCase(currStage) || stageName.contains("不區分"))
						continue;
					//
					_list.add(stageName);
				}
				option.setWidth(100, Unit.PERCENTAGE);
				option.setItems(_list);
				option.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
				_layout.addComponent(option);
				_layout.setComponentAlignment(option, Alignment.TOP_LEFT);
				//-------------------------
				Button btnGo = new Button("開始執行");
				btnGo.addClickListener(e -> {
					try{
						String val = option.getValue().toString();
						if(Gutil.isEmpty(val)) throw new MyException("請指定目的階段。");
						//
						MessageBox.createQuestion().withCaption("案件移轉")
						.withMessage("確定將所勾選的" + listCase.size() + "件案件移至" + val + "？")
						.withYesButton(()->{
							try {
								this.moveCaseImpl(listCase, val, listCase4Provider, ldpCase);
							}catch(Exception ex) {
								Gutil.handleException(ex);
							}
						})
						.withCancelButton()
						.open();
					
						//
						_window.close();
					}catch(Exception ex){
						Gutil.handleException(ex);
					}
				});
				
				_layout.addComponent(btnGo);
				_layout.setComponentAlignment(btnGo, Alignment.TOP_CENTER);
			}
			_window.setContent(_layout);
			UI.getCurrent().addWindow(_window);
		}catch(Exception ex) {
			Gutil.handleException(ex);
		}
	}
	
	private void moveCaseImpl(Set<CaseData> listCase,
			String stageNameNext,
			List<CaseData> listCase4Provider,
			ListDataProvider<CaseData> ldpCase){
		Connection conn = null;
    	try{
    		conn = ds.getConnection();
			conn.setAutoCommit(true);
			
        	for(CaseData theCase: listCase){
    			String stageNo = String.valueOf(stageNameNext.charAt(0)).toUpperCase();
    			MyUtil.updateProfileAndStage(stageNo, ctxProp.getSiteId(), theCase.getCaseId(), conn);
				//
				listCase4Provider.remove(theCase);
			}
        	conn.close();
        	//
        	ldpCase.refreshAll();
    	}catch(Exception ex){
    		Gutil.handleException(ex);
    	}finally{
    		Gutil.close(conn);
		}
	}
	
	//刪除所選案件
	private void deleteCaseDialog(Set<CaseData> listCase,
			List<CaseData> listCase4Provider,
			ListDataProvider<CaseData> ldpCase) throws Exception{
		String names = "";
		for(CaseData theCase: listCase){
			if(!names.equals("")) names += "、";
			if(!theCase.getPersonNameEn().isEmpty()) {
				names += theCase.getPersonNameEn();
			}else if(!theCase.getPersonNameCh().isEmpty()){
				names += theCase.getPersonNameCh();
			}else {
				names += theCase.getCaseNo();
			}
		}
		//
		String msg = "確定將本階段「" + names + "」等" + listCase.size() + " 位人員的資料刪除?"; 
		MessageBox.createQuestion().withCaption("案件刪除")
		.withMessage(msg)
		.withYesButton(() -> {
			MessageBox.createQuestion().withCaption("資料刪除")
			.withMessage("資料刪除後不可復原，確定刪除嗎?")
			.withYesButton(() -> {
				deleteCaseImpl(listCase, listCase4Provider, ldpCase);
			})
			.withCancelButton()
			.open();
		})
		.withCancelButton()
		.open();
	}
	
	//刪除案件
	private void deleteCaseImpl(Set<CaseData> listCase,
			List<CaseData> listCase4Provider,
			ListDataProvider<CaseData> ldpCase){
		Connection conn = null;
    	try{
			conn = ds.getConnection();
			conn.setAutoCommit(true);
			//
			for(CaseData theCase: listCase){
				try{
					List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), false, theCase.getCaseId(), ctxProp.getMinioClient());
					for(Item item: listItem) {
						MinioUtils.deleteObject(ctxProp.getSiteId(), item.objectName(), ctxProp.getMinioClient());
					}
					MinioUtils.deleteObject(ctxProp.getSiteId(), theCase.getCaseId(), ctxProp.getMinioClient());
				}catch(Exception _ex){
				}
				//資料DB記錄
				MyUtil.removeProfileFromDB(ctxProp.getSiteId(),
						theCase.getCaseId(),
						conn);
				//
				for(CaseData theCase2: listCase4Provider) {
					if(theCase2.getCaseNo().equalsIgnoreCase(theCase.getCaseNo())) {
						listCase4Provider.remove(theCase2);
						break;
					}
				}
			}
			ldpCase.refreshAll();
			//
			conn.close();
    	}catch(Exception ex){
    		Gutil.handleException(ex);
    	}finally{
    		Gutil.close(conn);
    	}
	}	
	
	//產出申請表
	private void genVghTcApplyFormDialog(Set<CaseData> listCase) throws Exception{
		String strGened = "";
		for(CaseData theCase: listCase) {
			Item item = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_APPLY_VGHTC, ctxProp);
			if(item != null) {
				if(!strGened.isEmpty()) strGened += "、";
				strGened += theCase.getPersonNameEn();
			}
		}
		//
		String msg = "確定產出 " + listCase.size() + " 位人員之申請表？";
		if(!strGened.isEmpty()) {
			msg = "所選人員中「" + strGened + "」已產出過申請表（系統將重新產出），確定執行嗎？";
		}
		MessageBox.createQuestion().withCaption("產出申請表")
			.withMessage(msg)
			.withYesButton(()->{
				try {
					this.genVghTcApplyFormBatch(listCase);
					MyUtil.createInfoMessage("產出申請表", "已產出 " + listCase.size() + " 位申請人員之申請表。");
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			})
			.withCancelButton()
			.open();
	}
	
	private void genVghTcApplyFormBatch(Set<CaseData> listCase) throws Exception{
		for(CaseData theCase: listCase) {
			Item itemVghApplyTempl = MyUtil.getWordItemFromSetting("臨床見習", "申請表", ctxProp);
			if(itemVghApplyTempl == null) throw new MyException("後台設定區找不到「國外醫事人員臨床見習申請表」文件樣板檔案。");
			
			//---------------- 院內申請表 -----------------
			com.aspose.words.Document docWord = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemVghApplyTempl.objectName(), ctxProp.getMinioClient()));
			for(Bookmark bk: docWord.getRange().getBookmarks()){
				if(bk.getName().contains("姓名")){
					if(bk.getName().contains("英文")) {
						bk.setText(theCase.getPersonNameEn());	
					}else if(bk.getName().contains("中文")) {
						bk.setText(theCase.getPersonNameCh());
					}else if(bk.getName().contains("聯絡")) {
						bk.setText(theCase.getContactName());
					}else {
						bk.setText("");
					}
				}else if(bk.getName().equalsIgnoreCase("male") && theCase.getGender().equalsIgnoreCase("male")){
					bk.setText("■");
				}else if(bk.getName().equalsIgnoreCase("female") && theCase.getGender().equalsIgnoreCase("female")){
					bk.setText("■");
				}else if(bk.getName().contains("國籍")){
					bk.setText(theCase.getNationality());
				}else if(bk.getName().contains("服務單位")){
					bk.setText(theCase.getAffiliation());
				}else if(bk.getName().startsWith("status")){
					if(bk.getName().toLowerCase().contains("resident")) {
						if(theCase.getRoleStatus().toLowerCase().contains("resident")) bk.setText("■");
					}else if(bk.getName().toLowerCase().contains("doctor")) {
						if(theCase.getRoleStatus().toLowerCase().contains("doctor")) bk.setText("■");
					}else if(bk.getName().toLowerCase().contains("nurse")) {
						if(theCase.getRoleStatus().toLowerCase().contains("nurse")) bk.setText("■");
					}else if(bk.getName().toLowerCase().contains("pharmacist")) {
						if(theCase.getRoleStatus().toLowerCase().contains("pharmacist")) bk.setText("■");
					}else if(bk.getName().toLowerCase().contains("nutritionist")) {
						if(theCase.getRoleStatus().toLowerCase().contains("nutritionist")) bk.setText("■");
					}else if(bk.getName().toLowerCase().contains("other")) {
						if(theCase.getRoleStatus().toLowerCase().contains("others")) {
							if(bk.getName().contains("Desc")) {
								bk.setText(theCase.getElseRole());
							}else {
								bk.setText("■");
							}
						}
					}
				}else if(bk.getName().contains("地址")){
					if(bk.getName().contains("通訊")){
						bk.setText(theCase.getMailAddress());
					}else if(bk.getName().contains("聯絡人")) {
						bk.setText(theCase.getContactName());
					}else {
						bk.setText("");
					}
				}else if(bk.getName().equalsIgnoreCase("email")){
					bk.setText(theCase.getEmail());
				}else if(bk.getName().contains("電話")){
					if(bk.getName().contains("聯絡人")){
						bk.setText(theCase.getContactPhone());
					}else {
						bk.setText(theCase.getPhoneNo());
					}
				}else if(bk.getName().contains("關係")){
					bk.setText(theCase.getContactRelation());
				}else if(bk.getName().contains("部科別") || bk.getName().contains("起迄日")){
					DiviChoice dc = null;
					if(bk.getName().contains("一")) {
						dc = theCase.getListChoice().get(0);
					}else if(bk.getName().contains("二")) {
						if(theCase.getListChoice().size() > 1) dc = theCase.getListChoice().get(1); 
					}else if(bk.getName().contains("三")) {
						if(theCase.getListChoice().size() > 2) dc = theCase.getListChoice().get(2);
					}else if(bk.getName().contains("四")) {
						if(theCase.getListChoice().size() > 3) dc = theCase.getListChoice().get(3);
					}
					if(dc != null) {
						if(bk.getName().contains("部科別")) {
							String s = dc.getDept().getDeptNameCh();
							if(dc.getDivi() != null && !dc.getDivi().getDivNameCh().isEmpty()) s += "/" + dc.getDivi().getDivNameCh();
							bk.setText(s);
						}else {
							bk.setText(dc.getFromDate() + "~" + dc.getEndDate());
						}
					}else {
						bk.setText("");
					}
				}else if(bk.getName().contains("週數")){
					String[] ss = theCase.getListChoice().get(0).getFromDate().split("-");
					LocalDate dateFrom = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
					ss = theCase.getListChoice().get(theCase.getListChoice().size() - 1).getEndDate().split("-");
					LocalDate dateEnd = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
					bk.setText(String.valueOf(this.calcWeeks(dateFrom, dateEnd)));
				}else if(bk.getName().contains("起始日")){
					bk.setText(theCase.getListChoice().get(0).getFromDate());
				}else if(bk.getName().contains("截止日")){
					bk.setText(theCase.getListChoice().get(theCase.getListChoice().size() - 1).getEndDate());
				}else if(bk.getName().equalsIgnoreCase("YES") && !theCase.getAccommType().toLowerCase().contains("need")){
					bk.setText("■");
				}else if(bk.getName().equalsIgnoreCase("NO") && theCase.getAccommType().toLowerCase().contains("need")){
					bk.setText("■");
				}else if(bk.getName().contains("TrainingGoals")){
					bk.setText(theCase.getTrainGoal());
				}else if(bk.getName().contains("InterestedFields")) {
					bk.setText(theCase.getInterestField());
				}
			}
			docWord.getRange().getBookmarks().clear();
			//大頭照
			Item itemPhoto = MyUtil.getPdfDocByHeaderName(theCase.getCaseId(), Gutil.FILE_HEADER_PHOTO, ctxProp);
			if(itemPhoto != null) {
				PDDocument pdfDoc = PDDocument.load(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemPhoto.objectName(), ctxProp.getMinioClient()));
				PDFRenderer pr = new PDFRenderer (pdfDoc);
			    BufferedImage bi = pr.renderImageWithDPI (0, 300);
				@SuppressWarnings("unchecked")
				NodeCollection<Shape> shapes = (NodeCollection<Shape>)docWord.getChildNodes(NodeType.SHAPE, true);
				for(Shape shape: shapes) {
					if(shape.getText() != null && shape.getText().toLowerCase().contains("photo")) {
						BufferedImage biDest = new BufferedImage((int)shape.getWidth(), (int)shape.getHeight(), BufferedImage.TYPE_INT_ARGB);
						MyUtil.drawScaledImage2(bi, biDest);
								
						shape.removeAllChildren();
						shape.getImageData().setImage(biDest);
						//
						break;
					}
				}
			}
			//
			ByteArrayOutputStream baosDest = new ByteArrayOutputStream();
			docWord.save(baosDest, SaveFormat.DOCX);
			//
			String objName = theCase.getCaseId() + "/" + this.DOC_HEAD_APPLY_VGHTC + "_" + theCase.getPersonNameEn()  + ".docx";
			MinioUtils.uploadObject(ctxProp.getSiteId(), objName, baosDest, ctxProp.getMinioClient());
		}
	}
	
	private int calcWeeks(LocalDate dateFrom, LocalDate dateEnd) {
		int ret = 0;
		//
		GregorianCalendar cal1 = GregorianCalendar.from(dateFrom.atStartOfDay(ZoneId.systemDefault()));
		GregorianCalendar cal2 = GregorianCalendar.from(dateEnd.atStartOfDay(ZoneId.systemDefault()));
		//計算上課天數
		int cc = 1;
		while(cal1.before(cal2)) {
			cal1.add(java.util.Calendar.DATE, 1);
			if( cal1.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY
				|| cal1.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY) continue;
			//
			cc++;
		}
		//
		ret = cc / 5;
		if(cc % 5 > 0) ret += 1;
		//
		return ret;
	}
	
	//產出外籍專業人士簽稿會核單
	/*
	private void genReviewFormDialog(Set<CaseData> listCase, Session alfSession) throws Exception{
		HashMap<String, AlfrescoDocument> mapApplyForm = new HashMap<String, AlfrescoDocument>();
		//
		String namesWithoutApplyForm = "";
		String namesGened = "";
		for(CaseData theCase: listCase){
			AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
			boolean withApplyForm = false;
			for(CmisObject co: fdCase.getChildren()){
				if(!(co instanceof org.apache.chemistry.opencmis.client.api.Document)) continue;
				//
				if(Gutil.isWordFile(co.getName())){
					if(co.getName().startsWith(this.DOC_HEAD_REVIEW_FORM_PROFESSIONAL)){
						if(!namesGened.equals("")) namesGened += ",";
						namesGened += theCase.getPersonNameEn();
					}else if(co.getName().startsWith(this.DOC_HEAD_APPLY_VGHTC)){
						withApplyForm = true;
						mapApplyForm.put(theCase.getAlfDocId(), (AlfrescoDocument)co);
					}
				}
			}
			if(!withApplyForm){
				if(!namesWithoutApplyForm.equals("")) namesWithoutApplyForm += ",";
				namesWithoutApplyForm += theCase.getPersonNameEn();
			}
		}
		if(!namesWithoutApplyForm.isEmpty()) throw new MyException("勾選人員中有 " + namesWithoutApplyForm + " 尚未產出來院進修申請表。");
		//
		String msg = "確定產出外籍醫事人員簽稿會核單？";
		if(!namesGened.equals("")){
			msg = "勾選人員中；" + namesGened + " 已產出過簽稿會核單，執行時系統將重新產出，確定執行嗎？";
		}
		//
		MessageBox.createQuestion().withCaption("產出外籍醫事人員簽稿會核單")
			.withMessage(msg)
			.withYesButton(() ->{
				try {
					genReviewFormProfessionalBatch(listCase, mapApplyForm, alfSession);
					MyUtil.createInfoMessage("產出簽稿會核單", "已產出所選 " + listCase.size() + " 位外籍醫事人員之簽稿會核單。");
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			})
			.withCancelButton()
			.open();
	}
	
	
	private void genReviewFormProfessionalBatch(Set<CaseData> listCase,
			HashMap<String, AlfrescoDocument> mapApplyForm,
			Session alfSession) throws Exception{
		AlfrescoDocument alfDocTemplate = MyUtil.getWordDocFromSetting("外籍醫事", "簽稿會核", fdSetting);
		if(alfDocTemplate == null) throw new MyException("外籍醫事人員簽稿會核單樣板找不到 !");
		//
		for(CaseData theCase: listCase){
			String fileName = this.DOC_HEAD_REVIEW_FORM_PROFESSIONAL + "_" + theCase.getPersonNameEn() + ".docx";
			//
			com.aspose.words.Document docWord = new com.aspose.words.Document(alfDocTemplate.getContentStream().getStream());
			com.aspose.words.DocumentBuilder builder = new com.aspose.words.DocumentBuilder(docWord);
			com.aspose.words.Table table = docWord.getFirstSection().getBody().getTables().get(0);
			com.aspose.words.Row row = table.getRows().get(1);
			builder.moveTo(row.getCells().get(1).getFirstParagraph());
			String s = theCase.getNationality() + "籍";
			//if(mapNation.containsKey(cli.nationality)){
			//	s = mapNation.get(cli.nationality) + "籍";
			//}
			if(theCase.getRoleStatus().contains("resident")){
				s += "住院醫師";
			}else if(theCase.getRoleStatus().contains("doctor")){
				s += "主治醫師";
			}else if(theCase.getRoleStatus().contains("nurse")){
				s += "護理師";
			}else if(theCase.getRoleStatus().contains("pharmacist")){
				s += "藥劑師";
			}else if(theCase.getRoleStatus().contains("nutritionist")){
				s += "營養師";
			}else{
				s += "醫事人員";
			}
			s += theCase.getPersonNameEn();
			if(!theCase.getPersonNameCh().isEmpty()) s += "(" + theCase.getPersonNameCh() + ")";
			s += "，申請於 ";
			
			String[] ss = theCase.getListChoice().get(0).getFromDate().split("-");
			LocalDate dateFrom = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
			ss = theCase.getListChoice().get(theCase.getListChoice().size() - 1).getEndDate().split("-");
			LocalDate dateEnd = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
			
			String dateRange = MyUtil.toTWDate(dateFrom) + " 至  " + MyUtil.toTWDate(dateEnd);
			s += dateRange;
			s += " 共 " + this.calcWeeks(dateFrom, dateEnd) + " 週" + "；";
			s += "擬至 貴部科接受訓練，並且依各科部自訂之標準收取進修費$ " + "元。";
			s += "是否同意， 敬請  惠示意見，以憑報請衛生福利部審核。（外籍進修醫師來院進修，請指派本院主治醫師職級或以上之醫師指導。）";
			builder.write(s);
			//準備科別表格
			int addRows = theCase.getListChoice().size() - 1;
			for(int i=0; i < addRows; i++){
				row = table.getRows().get(4);
				com.aspose.words.Row newRow = (com.aspose.words.Row)row.deepClone(true);
				table.insertAfter(newRow, row);
			}
			//第一個部科別
			for(int i=0; i < theCase.getListChoice().size(); i++) {
				row = table.getRows().get(i + 4);
				builder.moveTo(row.getCells().get(0).getFirstParagraph());
				//
				DiviChoice choice = theCase.getListChoice().get(i);
				String depDivChName = choice.getDept().getDeptNameCh();
				if(depDivChName.isEmpty()) depDivChName = choice.getDept().getDeptNameEn();
				if(choice.getDivi() != null) {
					String divName = choice.getDivi().getDeptNameCh();
					if(divName.isEmpty()) divName = choice.getDivi().getDeptNameEn();
					//
					depDivChName += "/" + divName;
				}
				builder.write(depDivChName);
			}
			if(!theCase.getAccommType().toLowerCase().contains("no")){
				row = table.getRows().get(6 + addRows);
				builder.moveTo(row.getCells().get(1).getFirstParagraph());
				String txt = "擬申請宿舍(" + dateRange + ")，請 准允。";
				builder.write(txt);
			}
			//加入申請表
			AlfrescoDocument docApplyForm = mapApplyForm.get(theCase.getAlfDocId());
			com.aspose.words.Document doc = new com.aspose.words.Document(docApplyForm.getContentStream().getStream());
		
			Run pageBreakRun = new Run(docWord, ControlChar.PAGE_BREAK);
			docWord.getLastSection().getBody().getLastParagraph().appendChild(pageBreakRun);
			builder.moveToDocumentEnd();
			builder.insertDocument(doc, ImportFormatMode.KEEP_DIFFERENT_STYLES);
			//
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			docWord.save(baos, SaveFormat.DOCX);
			//
			AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
			MyUtil.saveToAlfresco(fileName, baos, alfDocTemplate.getContentStreamMimeType(), fdCase, alfSession);
		}
	}
	*/
	
	//產出報衛福部進修申請表
	private void genTrainApply4GovDialog(Set<CaseData> listCase) throws Exception{
		String namesGened = "";
		for(CaseData theCase: listCase){
			Item itemApplyGov = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_TRAIN_APPLY_GOV, ctxProp);
			if(itemApplyGov != null) {
				if(!namesGened.isEmpty()) namesGened += "、";
				namesGened += theCase.getPersonNameEn();
			}
		}
		//
		String msg = "確定產出" + listCase.size() + "位人員之報衛福部進修申請表？";
		if(!namesGened.equals("")){
			msg = "人員「 " + namesGened + "」已產出過報衛福部進修申請表，執行時系統將重新產出，確定執行嗎？";
		}
		MessageBox.createQuestion().withCaption("產出報衛福部進修申請表")
			.withMessage(msg)
			.withYesButton(() ->{
				try {
					genTrainApply4GovBatch(listCase);
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			})
			.withCancelButton()
			.open();
		
	}
	
	private void genTrainApply4GovBatch(Set<CaseData> listCase) throws Exception{
		Item itemApplyTempl = MyUtil.getWordItemFromSetting("衛福部", "進修申請", ctxProp);
		if(itemApplyTempl == null) throw new MyException("後台設定區找不到「報衛福部進修申請表」文件樣板檔案。");
		
		Item itemPlanTempl = MyUtil.getWordItemFromSetting("衛福部", "進修計畫書", ctxProp);
		if(itemPlanTempl == null) throw new MyException("後台設定區找不到「報衛福部進修計畫書」文件樣板。");
		//
		for(CaseData theCase: listCase){
			//AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
			//
			DiviChoice choiceFirst = theCase.getListChoice().get(0);
			DiviChoice choiceEnd = theCase.getListChoice().get(theCase.getListChoice().size() - 1);
			String depNameCh = choiceFirst.getDept().getDeptNameCh();
			//
			String[] ss = choiceFirst.getFromDate().split("-");
			LocalDate dateFrom = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
			ss = choiceEnd.getFromDate().split("-");
			LocalDate dateEnd = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
			//
			com.aspose.words.Document docWordApply = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemApplyTempl.objectName(), ctxProp.getMinioClient()));
			com.aspose.words.DocumentBuilder builderApply = new com.aspose.words.DocumentBuilder(docWordApply);
			for(Bookmark bk: docWordApply.getRange().getBookmarks()){
				if(bk.getName().contains("姓名")){
					bk.setText(theCase.getPersonNameEn());
				}else if(bk.getName().contains("國籍")){
					bk.setText(theCase.getNationality());
				}else if(bk.getName().contains("護照號碼")){
					bk.setText(theCase.getPassportNo());
				}else if(bk.getName().contains("進修起日")){
					bk.setText(MyUtil.toTWDate(dateFrom));
				}else if(bk.getName().contains("進修止日")){
					bk.setText(MyUtil.toTWDate(dateEnd));
				}else if(bk.getName().contains("職類")){
					bk.setText(getTitleFromStatus(theCase));
				}else if(bk.getName().contains("進修科別")){
					bk.setText(depNameCh);
				}
			}
			docWordApply.getRange().getBookmarks().clear();
			
			//加入身分證明文件
			List<String> listPdfType = new ArrayList<>();
			listPdfType.add(Gutil.FILE_HEADER_PASSPORT);
			listPdfType.add(Gutil.FILE_HEADER_LICENSURE);
			listPdfType.add(Gutil.FILE_HEADER_RECOMMEND);
			listPdfType.add(Gutil.FILE_HEADER_CV);
			listPdfType.add(Gutil.FILE_HEADER_HEALTHCHECK);
			for(String headerPdf: listPdfType) {
				String pdfDocName = "個人護照";
				if(headerPdf.equals(Gutil.FILE_HEADER_LICENSURE)) {
					pdfDocName = "醫事專業證書";
				}else if(headerPdf.equals(Gutil.FILE_HEADER_RECOMMEND)) {
					pdfDocName = "經歷證明（推薦函）";
				}else if(headerPdf.equals(Gutil.FILE_HEADER_CV)) {
					pdfDocName = "個人履歷";
				}else if(headerPdf.equals(Gutil.FILE_HEADER_HEALTHCHECK)) {
					pdfDocName = "健康檢查報告";
				}
				//
				Item itemPdf = MyUtil.findPdfItemByHeader(theCase.getCaseId(), headerPdf, ctxProp);
				if(itemPdf == null)
					throw new MyException("找不到申請人「" + theCase.getPersonNameEn() + "」的「" + pdfDocName + "」檔案。");
				
				PDDocument pdfDoc = PDDocument.load(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemPdf.objectName(), ctxProp.getMinioClient()));
				PDFRenderer pdfRenderer = new PDFRenderer(pdfDoc);
			    for (int i = 0; i < pdfDoc.getNumberOfPages(); i++){
			    	com.aspose.words.Document docNew = new com.aspose.words.Document();
			    	com.aspose.words.DocumentBuilder builderDocNew = new com.aspose.words.DocumentBuilder(docNew);
			    	builderDocNew.getPageSetup().setPaperSize(PaperSize.A4);
			    	//builderDocNew.getPageSetup().setTopMargin(0.79 * 72);
			    	//builderDocNew.getPageSetup().setBottomMargin(0.79 * 72);
			    	//builderDocNew.getPageSetup().setLeftMargin(1.18 * 72);
			    	//builderDocNew.getPageSetup().setRightMargin(0.59 * 72);
			    	
			    	//builderDocNew.getParagraphFormat().setLeftIndent(3.94 * 72);
			    	builderDocNew.insertParagraph();
			    	if(i == 0)
			    		builderDocNew.writeln(pdfDocName);
			    	
			    	BufferedImage bi = pdfRenderer.renderImageWithDPI(i, 300, ImageType.RGB);
			    	builderDocNew.moveToDocumentEnd();
					Shape shape = builderDocNew.insertImage(bi);
					//調整Image尺寸
					//shape.getWidth()
					if(shape.getWidth() > builderDocNew.getPageSetup().getPageWidth()) {
						
						
					}else if(shape.getHeight() > builderDocNew.getPageSetup().getPageHeight()) {
						
					}
					
					//
					com.aspose.words.Run pageBreakRun = new Run(docWordApply, ControlChar.PAGE_BREAK);
			    	docWordApply.getLastSection().getBody().getLastParagraph().appendChild(pageBreakRun);
					builderApply.moveToDocumentEnd();
					//
					builderApply.insertDocument(docNew, ImportFormatMode.KEEP_SOURCE_FORMATTING);
			    }
			}
		    
			//---------------- 計劃書 ------------------
			String divNameAll = "";
			String divNameAndTeachers = "";
			for(DiviChoice choice: theCase.getListChoice()) {
				if(!divNameAll.isEmpty()) divNameAll += "、";
				if(!choice.getDept().getDeptNameCh().trim().isEmpty()) {
					divNameAll += choice.getDept().getDeptNameCh().trim();
				}else {
					divNameAll += choice.getDept().getDeptNameEn().trim();
				}
				if(choice.getDivi() != null) {
					if(!choice.getDivi().getDivNameCh().trim().isEmpty()) {
						divNameAll += "/" + choice.getDivi().getDivNameCh().trim();
					}else {
						divNameAll += "/" + choice.getDivi().getDivNameEn().trim();
					}
				}
				//
				if(!divNameAndTeachers.isEmpty()) divNameAndTeachers += ControlChar.LINE_BREAK;
				if(!choice.getDept().getDeptNameCh().trim().isEmpty()) {
					divNameAndTeachers += choice.getDept().getDeptNameCh().trim();
				}else {
					divNameAndTeachers += choice.getDept().getDeptNameEn().trim();
				}
				if(choice.getDivi() != null) {
					if(!choice.getDivi().getDivNameCh().trim().isEmpty()) {
						divNameAndTeachers += "/" + choice.getDivi().getDivNameCh().trim();
					}else {
						divNameAndTeachers += "/" + choice.getDivi().getDivNameEn().trim();
					}
				}
				if(choice.getTeacher1() != null)
					divNameAndTeachers += " " + choice.getTeacher1().getTeacherNameCh();
				if(choice.getTeacher2() != null)
					divNameAndTeachers += " " + choice.getTeacher2().getTeacherNameCh();
				if(choice.getTeacher3() != null)
					divNameAndTeachers += " " + choice.getTeacher3().getTeacherNameCh();
			}
			//
			com.aspose.words.Document docWordPlan = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemPlanTempl.objectName(), ctxProp.getMinioClient()));
			com.aspose.words.DocumentBuilder builderPlan = new com.aspose.words.DocumentBuilder(docWordPlan);
			for(Bookmark bk: docWordPlan.getRange().getBookmarks()){
				if(bk.getName().contains("科別名稱")){
					bk.setText(divNameAll);
				}else if(bk.getName().contains("醫師名與稱謂")){
					String title = getTitleFromStatus(theCase);
					bk.setText(theCase.getPersonNameEn() + title);
				}else if(bk.getName().contains("學校或機構名稱")){
					bk.setText(theCase.getAffiliation());
				}else if(bk.getName().contains("進修目的")){
					bk.setText(theCase.getTrainGoal());
				}else if(bk.getName().contains("起訖時間")){
					bk.setText(MyUtil.toTWDate(dateFrom) + " ~ " + MyUtil.toTWDate(dateEnd));
				}else if(bk.getName().contains("進修科別")){
					bk.setText(divNameAll);
				}else if(bk.getName().contains("指導醫師")){
					bk.setText(divNameAndTeachers);
				}else if(bk.getName().contains("臨床進修項目")){
					bk.setText("");
				}
			}
			builderPlan.moveToBookmark("臨床進修項目");
			builderPlan.getFont().setBold(false);
			{
				int z = 0;
				for(DiviChoice choice: theCase.getListChoice()) {
					if(z > 0) builderPlan.writeln();
					//
					String strTrain = choice.getDept().getDeptNameCh();
					if(choice.getDivi() != null)
						strTrain += "/" + choice.getDivi().getDivNameCh();
					builderPlan.writeln(strTrain);
					z++;
				}
			}
			docWordPlan.getRange().getBookmarks().clear();			
			//合併 申請書 與 計畫書
			
			com.aspose.words.Run pageBreakRun = new Run(docWordApply, ControlChar.PAGE_BREAK);
			docWordApply.getLastSection().getBody().getLastParagraph().appendChild(pageBreakRun);
			builderApply.moveToDocumentEnd();
			builderApply.insertDocument(docWordPlan, ImportFormatMode.KEEP_DIFFERENT_STYLES);
			//
			ByteArrayOutputStream baosDest = new ByteArrayOutputStream();
			docWordApply.save(baosDest, SaveFormat.DOCX);
			//
			String fileNameApply = this.DOC_HEAD_TRAIN_APPLY_GOV + "_" + theCase.getPersonNameEn() + ".docx";
			
			//MyUtil.saveToAlfresco(fileNameApply, baosDest, alfDocApplyTempl.getContentStreamMimeType(), fdCase, alfSession);
			MinioUtils.uploadObject(ctxProp.getSiteId(), theCase.getCaseId() + "/" + fileNameApply, baosDest, ctxProp.getMinioClient());
		}
		//
		MyUtil.createInfoMessage("產出報衛福部進修申請表", "已產出所選 " + listCase.size() + " 位外籍醫事人員之報衛福部進修申請表。");
	}
	
	private String getTitleFromStatus(CaseData theCase){
		String sta = theCase.getRoleStatus().toLowerCase();
		if(sta.contains("resident")){
			return "外籍住院醫師";
		}else if(sta.contains("doctor")){
			return "外籍醫師"; 
		}else if(sta.contains("nurse")){
			return "外籍護理師";
		}else if(sta.contains("pharmacist")){
			return "外籍藥劑師";
		}else if(sta.contains("nutritionist")){
			return "外籍營養師";
		}else{
			return "外籍醫事人員";
		}
	}
	
	//產出報衛福部進修計畫書，需先產出 報衛福部進修申請表
	private void genTrainPlanGovDialog(Set<CaseData> listCase) throws Exception{
		HashMap<CaseData, Item> mapApplyForm = new HashMap<>();
		//
		String namesWithoutApplyForm = "";
		String namesGened = "";
		for(CaseData theCase: listCase){
			//AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
			Item itemApply = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_TRAIN_APPLY_GOV, ctxProp);
			if(itemApply != null) {
				mapApplyForm.put(theCase, itemApply);
			}else {
				if(!namesWithoutApplyForm.isEmpty()) namesWithoutApplyForm += "、";
				namesWithoutApplyForm += theCase.getPersonNameEn();
			}
			Item itemPlan = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_TRAIN_PLAN_GOV, ctxProp);
			if(itemPlan != null) {
				if(!namesGened.isEmpty()) namesGened += "、";
				namesGened += theCase.getPersonNameEn();
			}
		}
		//
		if(!namesWithoutApplyForm.isEmpty())
			throw new MyException("勾選案件中尚有「" + namesWithoutApplyForm + "」等人員未產出來院進修申請表。");
		//
		String msg = "確定產出" + listCase.size() + " 位人員之「報衛福部進修計畫書」？";
		if(!namesGened.equals("")){
			msg = "人員「 " + namesGened + "」已產出過報衛福部進修計畫書，執行時系統將重新產出，確定執行嗎？";
		}
		MessageBox.createQuestion().withCaption("產出報衛福部進修計畫書")
			.withMessage(msg)
			.withYesButton(() ->{
				try {
					genTrainPlan4GovBatch(mapApplyForm);
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			})
			.withCancelButton()
			.open();
		
	}
		
	private void genTrainPlan4GovBatch(HashMap<CaseData, Item> mapApplyForm) throws Exception{
		Item itemTempl = MyUtil.getWordItemFromSetting("衛福部", "進修計畫書", ctxProp);
		if(itemTempl == null) throw new MyException("後台設定區找不到「報衛福部進修計畫書」文件樣板！");
		//
		for(CaseData theCase: mapApplyForm.keySet()){
			String fileName = this.DOC_HEAD_TRAIN_PLAN_GOV + "_" + theCase.getPersonNameEn() + ".docx";
			//
			String divNameAll = "";
			String divNameAndTeachers = "";
			for(DiviChoice choice: theCase.getListChoice()) {
				if(!divNameAll.isEmpty()) divNameAll += "、";
				if(!choice.getDept().getDeptNameCh().trim().isEmpty()) {
					divNameAll += choice.getDept().getDeptNameCh().trim();
				}else {
					divNameAll += choice.getDept().getDeptNameEn().trim();
				}
				if(choice.getDivi() != null) {
					if(!choice.getDivi().getDivNameCh().trim().isEmpty()) {
						divNameAll += "/" + choice.getDivi().getDivNameCh().trim();
					}else {
						divNameAll += "/" + choice.getDivi().getDivNameEn().trim();
					}
				}
				//
				if(!divNameAndTeachers.isEmpty()) divNameAndTeachers += ControlChar.LINE_BREAK;
				if(!choice.getDept().getDeptNameCh().trim().isEmpty()) {
					divNameAndTeachers += choice.getDept().getDeptNameCh().trim();
				}else {
					divNameAndTeachers += choice.getDept().getDeptNameEn().trim();
				}
				if(choice.getDivi() != null) {
					if(!choice.getDivi().getDivNameCh().trim().isEmpty()) {
						divNameAndTeachers += "/" + choice.getDivi().getDivNameCh().trim();
					}else {
						divNameAndTeachers += "/" + choice.getDivi().getDivNameEn().trim();
					}
				}
				if(choice.getTeacher1() != null && !choice.getTeacher1().getTeacherNameCh().isEmpty())
					divNameAndTeachers += " " + choice.getTeacher1().getTeacherNameCh();
				if(choice.getTeacher2() != null && !choice.getTeacher2().getTeacherNameCh().isEmpty())
					divNameAndTeachers += " " + choice.getTeacher2().getTeacherNameCh();
				if(choice.getTeacher3() != null && !choice.getTeacher3().getTeacherNameCh().isEmpty())
					divNameAndTeachers += " " + choice.getTeacher3().getTeacherNameCh();
				
			}
			//
			String[] ss = theCase.getListChoice().get(0).getFromDate().split("-");
			LocalDate dateFrom = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
			ss = theCase.getListChoice().get(theCase.getListChoice().size() - 1).getFromDate().split("-");
			LocalDate dateEnd = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
			//
			com.aspose.words.Document docWord = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemTempl.objectName(), ctxProp.getMinioClient()));
			com.aspose.words.DocumentBuilder builder = new com.aspose.words.DocumentBuilder(docWord);
			for(Bookmark bk: docWord.getRange().getBookmarks()){
				if(bk.getName().contains("科別名稱")){
					bk.setText(divNameAll);
				}else if(bk.getName().contains("醫師名與稱謂")){
					String title = getTitleFromStatus(theCase);
					bk.setText(theCase.getPersonNameEn() + title);
				}else if(bk.getName().contains("學校或機構名稱")){
					bk.setText(theCase.getAffiliation());
				}else if(bk.getName().contains("進修目的")){
					bk.setText(theCase.getTrainGoal());
				}else if(bk.getName().contains("起訖時間")){
					bk.setText(MyUtil.toTWDate(dateFrom) + " ~ " + MyUtil.toTWDate(dateEnd));
				}else if(bk.getName().contains("進修科別")){
					bk.setText(divNameAll);
				}else if(bk.getName().contains("指導醫師")){
					bk.setText(divNameAndTeachers);
				}else if(bk.getName().contains("臨床進修項目")){
					bk.setText("");
				}
			}
			builder.moveToBookmark("臨床進修項目");
			builder.getFont().setBold(false);
			{
				int z = 0;
				for(DiviChoice choice: theCase.getListChoice()) {
					if(z > 0) builder.writeln();
					//
					String strTrain = choice.getDept().getDeptNameCh();
					if(choice.getDivi() != null)
						strTrain += "/" + choice.getDivi().getDivNameCh();
					builder.writeln(strTrain);
					z++;
				}
			}
			docWord.getRange().getBookmarks().clear();
			
			//加入申請表
			Item itemApplyForm = mapApplyForm.get(theCase);
			com.aspose.words.Document doc = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemApplyForm.objectName(), ctxProp.getMinioClient()));
		
			Run pageBreakRun = new Run(docWord, ControlChar.PAGE_BREAK);
			docWord.getLastSection().getBody().getLastParagraph().appendChild(pageBreakRun);
			builder.moveToDocumentEnd();
			builder.insertDocument(doc, ImportFormatMode.KEEP_DIFFERENT_STYLES);
			//
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			docWord.save(baos, SaveFormat.DOCX);
			//
			//AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
			//MyUtil.saveToAlfresco(fileName, baos, MyUtil.getWordMimeType(), fdCase, alfSession);
			MinioUtils.uploadObject(ctxProp.getSiteId(), theCase.getCaseId() + "/" + fileName, baos, ctxProp.getMinioClient());
		}
		//
		MyUtil.createInfoMessage("產出報衛福部進修計畫書", "已產出所選 " + mapApplyForm.size() + " 位人員之報衛福部進修計畫書。");
	}
	
	//產出邀請函
	private void genInviteLetterDialog(Set<CaseData> listCase) throws Exception{
		int cc = 0;
		for(CaseData theCase: listCase){
			Item item = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_INVITE_LETTER, ctxProp);
			if(item != null) cc++;
		}
		String msg = "確定產出所勾選 " + listCase.size() + " 位人員的邀請函？";
		if(cc > 0){
			msg = "已有 " + cc + " 位人員產出過邀請函，執行時系統將重新產出，確定執行嗎？";
		}
		MessageBox.createQuestion().withCaption("產出邀請函")
			.withMessage(msg)
			.withYesButton(() ->{
				try {
					this.genInviteLetterBatch(listCase);
					MyUtil.createInfoMessage("產出邀請函", "已產出所選 " + listCase.size() + " 位外籍醫事人員之邀請函。");
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			})
			.withCancelButton()
			.open();
	}
	
	private void genInviteLetterBatch(Set<CaseData> listCase){
		try{
			for(CaseData theCase: listCase){
				Item itemTempl = MyUtil.getWordItemFromSetting("醫事", "邀請函", ctxProp);
				if(itemTempl == null) throw new MyException("後台設定區找不到外籍醫事人員邀請函樣板檔案。");
				//
				this.genInviteLetterByProfessional(theCase, itemTempl);
			}
		}catch(Exception ex){
			Gutil.handleException(ex);
		}
	}
	
	//產出醫事人員邀請函
	private void genInviteLetterByProfessional(CaseData theCase, Item itemTempl) throws Exception{
		String fileName = this.DOC_HEAD_INVITE_LETTER + "_" + theCase.getPersonNameEn() + ".docx";
		//
		String[] ss = theCase.getListChoice().get(0).getFromDate().split("-");
		LocalDate dateFrom = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
		ss = theCase.getListChoice().get(theCase.getListChoice().size() - 1).getFromDate().split("-");
		LocalDate dateEnd = LocalDate.of(Integer.parseInt(ss[0]), Integer.parseInt(ss[1]), Integer.parseInt(ss[2]));
		//訓練科別
		com.aspose.words.Document docWord = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemTempl.objectName(), ctxProp.getMinioClient()));
		com.aspose.words.DocumentBuilder builder = new com.aspose.words.DocumentBuilder(docWord);
		
		com.aspose.words.Table table = docWord.getFirstSection().getBody().getTables().get(0);
		for(int i=0; i < theCase.getListChoice().size() - 1; i++){
			com.aspose.words.Row row = table.getRows().get(1);
			com.aspose.words.Row newRow = (com.aspose.words.Row)row.deepClone(true);
			table.insertAfter(newRow, row);
		}
		int idxRow = 0;
		for(DiviChoice choice: theCase.getListChoice()) {
			String strDepDiv = "";
			if(choice.getDivi() != null) {
				strDepDiv = choice.getDivi().getDivNameEn() + "," + choice.getDept().getDeptNameEn();
			}else {
				strDepDiv += choice.getDept().getDeptNameEn();
			}
			//
			idxRow += 1;
			com.aspose.words.Row row = table.getRows().get(idxRow);
			builder.moveTo(row.getCells().get(0).getFirstParagraph());
			builder.write(strDepDiv);
			builder.moveTo(row.getCells().get(1).getFirstParagraph());
			builder.write(choice.getFromDate() + " ~ " + choice.getEndDate());
		}
		//
		for(Bookmark bk: docWord.getRange().getBookmarks()){
			if(bk.getName().contains("抬頭")){
				String s = "Mr.";
		    	if(theCase.getGender().equalsIgnoreCase("female")) s = "Ms.";
		    	if(theCase.getRoleStatus().contains("doctor")) s = "Dr.";
		    	s += theCase.getPersonNameEn();
				bk.setText(s);	
			}else if(bk.getName().contains("星期數")){
				String s = String.valueOf(this.calcWeeks(dateFrom, dateEnd));
				s += "-week";
				bk.setText(s);
			}else if(bk.getName().contains("起始日期")){
				bk.setText(dateFrom.toString());
			}else if(bk.getName().contains("截止日期")){
				bk.setText(dateEnd.toString());
			}else if(bk.getName().contains("超連結")){
				String url = MyUtil.getContextPath() + "/gtrainapply";
				url += "?docId=" + theCase.getCaseId();
				bk.setText("");
				builder.moveToBookmark(bk.getName());
				builder.getFont().setStyleIdentifier(StyleIdentifier.HYPERLINK);
				builder.insertHyperlink(url, url, false);
			}
		}
		docWord.getRange().getBookmarks().clear();
		//
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		docWord.save(baos, SaveFormat.DOCX);
		//
		//MyUtil.saveToAlfresco(fileName, baos, alfDocTempl.getContentStreamMimeType(), fdCase, alfSession);
		MinioUtils.uploadObject(ctxProp.getSiteId(), theCase.getCaseId() + "/" + fileName, baos, ctxProp.getMinioClient());
	}
	
	//發送補件通知函電子郵件
	private void mailSupplementartLetter(Set<CaseData> listCase) throws Exception{
		if(listCase.size() > 1)
			throw new MyException("補件通知函僅限個人通知，請勿勾選多人執行。");
		//
		Item itemTempl = MyUtil.getWordItemFromSetting("補件通知", ctxProp);
		if(itemTempl == null)
			throw new MyException("後台設定區查無補件通知函郵件樣板。");
		//
		CaseData theCase = listCase.iterator().next();
		//
		com.aspose.words.Document docWord = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemTempl.objectName(), ctxProp.getMinioClient()));
		for(Bookmark bk: docWord.getRange().getBookmarks()){
			if(bk.getName().toLowerCase().contains("personalname")){
				bk.setText(theCase.getPersonNameEn());	
			}
		}
		docWord.getRange().getBookmarks().clear();
		//
		String strHtml = MyUtil.convertDocToHTML(docWord);
		
		SupplementForm form = new SupplementForm();
		form.addStyleName("halftone-whip");
		form.rtaContent.setValue(strHtml);
		//
		final Window window = new Window("發送補件通知電子郵件");
		window.setWidth(90, Unit.PERCENTAGE);
		window.setHeight(95, Unit.PERCENTAGE);
		window.setModal(true);
		window.center();
		//
		form.btnSend.addClickListener(e ->{
			MessageBox.createQuestion().withCaption("發送補件通知函")
			.withMessage("確定發送「" + theCase.getPersonNameEn() + "」之補件通知函？")
			.withYesButton(() ->{
				try {
					javax.mail.Session mailSession = Gutil.getMailSession(ctxProp);
					Message msg = Gutil.sendMailTLS(theCase.getEmail(),
										"Additional document needed by Taichung Veterans General Hospital",
										form.rtaContent.getValue(),
										mailSession,
										ctxProp.getFromAccount(),
										ctxProp.getCcAccount());
					if(msg == null)
						throw new MyException("發送郵件失敗。");
					//
					String fileName = "補件通知_" + sdfss.format(new java.util.Date()) + ".eml";
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					msg.writeTo(baos);
					//AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
					//MyUtil.saveToAlfresco(fileName, baos, "text/html; charset=utf-8", fdCase, alfSession);
					MinioUtils.uploadObject(ctxProp.getSiteId(), theCase.getCaseId() + "/" + fileName, baos, ctxProp.getMinioClient());
					//
					window.close();
					MyUtil.createInfoMessage("補件通知", "發送補件通知郵件完成。");
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
				
			})
			.withCancelButton()
			.open();
		});
		//
		window.setContent(form);
		UI.getCurrent().addWindow(window);
	}

	//發送同意函電子郵件
	private void mailInviteLetterDialog(Set<CaseData> listCase) throws Exception{
		LinkedHashMap<CaseData, Item> mapCliDoc = new LinkedHashMap<>();
		//
		int ccLost = 0;
		int ccMailed = 0;
		for(CaseData theCase: listCase){
			Item item = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_INVITE_LETTER, ctxProp);
			if(item == null) {
				ccLost++;
			}else {
				mapCliDoc.put(theCase, item);
			}
			//
			item = MyUtil.findMailItemByHeader(theCase.getCaseId(), this.DOC_HEAD_INVITE_LETTER, ctxProp);
			if(item != null) ccMailed++;
		}
		if(ccLost > 0) throw new MyException("尚有 " + ccLost + " 位申請人員未產出邀請函檔案！");
		//
		String msg = "確定發送 " + listCase.size() + " 位人員之邀請函電子郵件？";
		if(ccMailed > 0){
			msg = "已有 " + ccMailed + " 位人員發送過邀請函電子郵件，執行時系統將重新發送，確定執行嗎？";
		}
		MessageBox.createQuestion().withCaption("發送邀請函郵件")
			.withMessage(msg)
			.withYesButton(() ->{
				try {
					this.mailInviteLetterBatch(mapCliDoc);
					MyUtil.createInfoMessage("發送邀請函郵件", "已發送所選 " + listCase.size() + " 位外籍人員之邀請函電子郵件。");
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			})
			.withCancelButton()
			.open();
	}
	
	private void mailInviteLetterBatch(LinkedHashMap<CaseData, Item> mapCliDoc) throws Exception{
		javax.mail.Session mailSession = Gutil.getMailSession(ctxProp);
		for(CaseData theCase: mapCliDoc.keySet()){
			Item item = mapCliDoc.get(theCase);
			//
			com.aspose.words.Document docWord = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), item.objectName(), ctxProp.getMinioClient()));
			//發送email
			String mailSubject = "Inviting Letter from VGHTC";
			Message message = Gutil.sendMailTLS(theCase.getEmail(),
					mailSubject,
					MyUtil.convertDocToHTML(docWord),
					mailSession,
					ctxProp.getFromAccount(),
					ctxProp.getCcAccount());
			if(message != null){
				//String fileName = item.objectName().replace(".docx", ".eml");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				message.writeTo(baos);
				//AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
				//MyUtil.saveToAlfresco(fileName, baos, "text/html; charset=utf-8", fdCase, alfSession);
				MinioUtils.uploadObject(ctxProp.getSiteId(),
						item.objectName().replace(".docx", ".eml"),
						baos,
						ctxProp.getMinioClient());
			}
		}
	}
	
	//知會臨床單位承辦人
	private void mailUdTakerLetter(Set<CaseData> listCase,
			List<CaseData> listCase4Provider,			
			ListDataProvider<CaseData> ldpCase) throws Exception{
		if(listCase.size() > 1)
			throw new MyException("知會臨床單位承辦人以個別案件為單位，請勿勾選多人執行。");
		//
		Item itemTempl = MyUtil.getWordItemFromSetting("臨床", "案件通知", ctxProp);
		if(itemTempl == null)
			throw new MyException("後台設定區查無臨床單位案件通知函郵件樣板。");
		//
		CaseData theCase = listCase.iterator().next();
		//
		com.aspose.words.Document docWord = new com.aspose.words.Document(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemTempl.objectName(), ctxProp.getMinioClient()));
		com.aspose.words.DocumentBuilder builder = new com.aspose.words.DocumentBuilder(docWord);
		for(Bookmark bk: docWord.getRange().getBookmarks()){
			if(bk.getName().toLowerCase().contains("國別")){
				bk.setText(theCase.getNationality());	
			}else if(bk.getName().toLowerCase().contains("英文姓名")){
				bk.setText(theCase.getPersonNameEn());
			}else if(bk.getName().toLowerCase().contains("角色")){
				bk.setText(theCase.getRoleStatus());
			}else if(bk.getName().toLowerCase().contains("回覆網址")){
				String url = MyUtil.getContextPath() + "/gtrainman";
				url += "?clinical&docId=" + theCase.getCaseId();
				bk.setText("");
				builder.moveToBookmark(bk.getName());
				builder.getFont().setStyleIdentifier(StyleIdentifier.HYPERLINK);
				builder.insertHyperlink(url, url, false);
			}
		}
		docWord.getRange().getBookmarks().clear();
		//
		String strHtml = MyUtil.convertDocToHTML(docWord);
		
		UdTakerNoticeForm form = new UdTakerNoticeForm();
		form.addStyleName("halftone-whip");
		form.rtaContent.setValue(strHtml);
		//
		form.txtUdTakerNames.setValue(theCase.getUdTakerNames());
		form.txtUdTakerEmails.setValue(theCase.getUdTakerEmails());
		//
		final Window window = new Window("知會臨床單位承辦人電子郵件");
		window.setWidth(90, Unit.PERCENTAGE);
		window.setHeight(95, Unit.PERCENTAGE);
		window.setModal(true);
		window.center();
		//
		form.btnSend.addClickListener(e ->{
			try{
				if(form.txtUdTakerNames.getValue().trim().isEmpty())
					throw new MyException("請輸入臨床單位承辦人姓名。");
				if(form.txtUdTakerEmails.getValue().trim().isEmpty())
					throw new MyException("請輸入臨床單位承辦人電子郵件。");
				//
				String[] arrMail = form.txtUdTakerEmails.getValue().trim().split(";");
				for(String strMail: arrMail) {
					if(!Gutil.isValidEmail(strMail.trim()))
						throw new MyException(strMail.trim() + " 不是有效的郵件帳號。");
				}
				//收集檔案
				List<MailAttach> listAttach = new ArrayList<>();
				//
				//AlfrescoFolder fdCase = MyUtil.getCaseFolder(theCase.getAlfDocId(), alfSession);
				Item itemApply = MyUtil.findWordItemByHeader(theCase.getCaseId(), this.DOC_HEAD_APPLY_VGHTC, ctxProp);
				if(itemApply == null)
					throw new MyException("請先產出本案的「來院進修申請書」。");
				listAttach.add(new MailAttach(MinioUtils.getObjectPureName(itemApply),
						MinioUtils.getObjectStream(ctxProp.getSiteId(), itemApply.objectName(), ctxProp.getMinioClient())));
				
				List<String> listDocName = new ArrayList<>();
				listDocName.add(Gutil.FILE_HEADER_PHOTO);
				listDocName.add(Gutil.FILE_HEADER_PASSPORT);
				listDocName.add(Gutil.FILE_HEADER_LICENSURE);
				listDocName.add(Gutil.FILE_HEADER_RECOMMEND);
				listDocName.add(Gutil.FILE_HEADER_DIPLOMA);
				listDocName.add(Gutil.FILE_HEADER_CV);
				listDocName.add(Gutil.FILE_HEADER_HEALTHCHECK);
				listDocName.add(Gutil.FILE_HEADER_COVID);
				for(String headerName: listDocName) {
					Item itemDoc = MyUtil.findPdfItemByHeader(ctxProp.getSiteId(), headerName, ctxProp);
					if(itemDoc != null) {
						listAttach.add(new MailAttach(MinioUtils.getObjectPureName(itemDoc),
								MinioUtils.getObjectStream(ctxProp.getSiteId(), itemDoc.objectName(), ctxProp.getMinioClient())));
					}
				}
				//
				MessageBox.createQuestion().withCaption("知會臨床單位承辦人")
				.withMessage("確定發送「" + theCase.getPersonNameEn() + "」申請案件之臨床單位通知函？")
				.withYesButton(() ->{
					Connection conn = null;
					try {
						List<String> listCC = new ArrayList<>();
						if(!ctxProp.getCcAccount().isEmpty()) {
							listCC = Arrays.asList(ctxProp.getCcAccount().split(";"));
						}
						//
						javax.mail.Session mailSession = Gutil.getMailSession(ctxProp);
						Message msg = Gutil.sendMailTLS(Arrays.asList(arrMail),
											"外籍專業人士「" + theCase.getPersonNameEn() + "」來院進修申請通知",
											form.rtaContent.getValue(),
											mailSession,
											ctxProp.getFromAccount(),
											listCC,
											listAttach);
						if(msg == null)
							throw new MyException("發送郵件失敗。");
						//
						String fileName = "臨床單位知會函_" + sdfss.format(new java.util.Date()) + ".eml";
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						msg.writeTo(baos);
						//MyUtil.saveToAlfresco(fileName, baos, "text/html; charset=utf-8", fdCase, alfSession);
						MinioUtils.uploadObject(ctxProp.getSiteId(),
								theCase.getCaseId() + "/" + fileName,
								baos,
								ctxProp.getMinioClient());
						//
						theCase.setUdTakerNames(form.txtUdTakerNames.getValue().trim());
						theCase.setUdTakerEmails(form.txtUdTakerEmails.getValue().trim());
						
						Gutil.saveCaseData(ctxProp, theCase, conn);
						int idx = listCase4Provider.indexOf(theCase);
						if(idx >= 0) {
							listCase4Provider.remove(idx);
							listCase4Provider.add(idx, theCase);
						}
						ldpCase.refreshAll();
						//
						window.close();
						MyUtil.createInfoMessage("臨床單位知會函", "臨床單位知會函發送完成。");
					}catch(Exception ex) {
						Gutil.handleException(ex);
					}finally {
						Gutil.close(conn);
					}
				})
				.withCancelButton()
				.open();
			}catch(Exception ex) {
				Gutil.handleException(ex);
			}
		});
		//
		window.setContent(form);
		UI.getCurrent().addWindow(window);
	}

	//產出人員統計表
	private void genPeriodReportDialog() throws Exception{
		final Window window = new Window("產出申請人員統計表");
		window.setWidth(80, Unit.PERCENTAGE);
		window.setHeight(420, Unit.PIXELS);
		window.setModal(true);
		window.center();
		
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();
		layout.addStyleName("halftone-whip");
		{
			CheckBoxGroup<String> cbgStatus = new CheckBoxGroup<>("狀態選擇:");
			cbgStatus.setItems(Gutil.listStage);
			cbgStatus.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
			layout.addComponent(cbgStatus);
			layout.setComponentAlignment(cbgStatus, Alignment.TOP_LEFT);
			//--------- 第一列 -------------
			HorizontalLayout hr1 = new HorizontalLayout();
			hr1.setWidth(100, Unit.PERCENTAGE);
			hr1.setSpacing(true);
			
			DateField dateFrom = new DateField("進修期間(起)");
			hr1.addComponent(dateFrom);
			
			DateField dateEnd = new DateField("進修期間(迄)");
			dateEnd.setValue(LocalDate.now());
			hr1.addComponent(dateEnd);
			
			RadioButtonGroup<String> rbgArea = new RadioButtonGroup<>("區域別");
			rbgArea.setItems("任何區域", "東南亞人士", "大陸人士", "非大陸人士");
			rbgArea.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
			hr1.addComponent(rbgArea);
			hr1.setExpandRatio(rbgArea, 1);
			
			layout.addComponent(hr1);
			//------- 第二列 ---------
			HorizontalLayout hr2 = new HorizontalLayout();
			hr2.setWidth(100, Unit.PERCENTAGE);
			hr2.setSpacing(true);
			
			TextField txtNation = new TextField("國籍(英文)");
			hr2.addComponent(txtNation);
			
			TextField txtSchoolLocation = new TextField("學籍地區(英文)");
			hr2.addComponent(txtSchoolLocation);
			
			TextField txtYear = new TextField("報到年度");
			txtYear.setWidth(80, Unit.PIXELS);
			hr2.addComponent(txtYear);
			
			TextField txtDepDiv = new TextField("見習部科別中文名稱（多部科別以空白間隔）");
			txtDepDiv.setWidth(100, Unit.PERCENTAGE);
			hr2.addComponent(txtDepDiv);
			
			//TextField txtDelegate = new TextField("委訓單位");
			//hr2.addComponent(txtDelegate);
			//
			hr2.setExpandRatio(txtDepDiv, 1);
			
			layout.addComponent(hr2);
			//-------------------------
			Button btnGo = new Button("開始執行");
			btnGo.addClickListener(e -> {
				try{
					if(cbgStatus.getValue() == null || cbgStatus.getValue().size() == 0)
						throw new MyException("請先指定資料進度狀態（所在階段）。");
					//
					if( (dateFrom.getValue() != null && dateEnd.getValue() == null)
							|| (dateFrom.getValue() == null && dateEnd.getValue() != null))
						throw new MyException("請先指定完整的進修日期區間。"); 
					//
					String area = "";
					if(rbgArea.getValue() != null) area = rbgArea.getValue();
					this.genPeriodReportImpl(cbgStatus.getValue(),
										dateFrom.getValue(),
										dateEnd.getValue(),
										area,
										txtNation.getValue().trim(),
										txtSchoolLocation.getValue().trim(),
										txtYear.getValue(),
										txtDepDiv.getValue());
					//
					window.close();
				}catch(Exception ex){
					Gutil.handleException(ex);
				}
			});
			layout.addComponent(btnGo);
			layout.setComponentAlignment(btnGo, Alignment.TOP_CENTER);
		}
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}
	
	//階段會有空白結尾
	private void genPeriodReportImpl(Set<String> listStage,
										LocalDate dateFrom,
										LocalDate dateEnd,
										String area,
										String nationality,
										String schoolLocation,
										String year,
										String depDiv){
		Connection conn = null;
		try {
			Item itemTempl = MyUtil.getExcelItemFromSetting("外籍", "統計", ctxProp);
			Item itemSENation = MyUtil.getExcelItemFromSetting("東南亞國家", ctxProp);
			if(itemTempl == null) throw new MyException("設定區找不到外籍人員統計表樣板檔案。");
			//
			List<String> listSE = new ArrayList<>();
			if(area.contains("東南亞")){
				if(itemSENation == null) throw new MyException("後台設定區找不到東南亞國家關鍵字列表檔案。");
				//
				Workbook wb = WorkbookFactory.create(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemSENation.objectName(), ctxProp.getMinioClient()));
				Sheet ws = wb.getSheetAt(0);
				Iterator<Row> itRow = ws.rowIterator();
				while(itRow.hasNext()){
					Row row = itRow.next();
					if(row == null) continue;
					Cell cell = row.getCell(0);
					if(cell == null) continue;
					
					cell.setCellType(Cell.CELL_TYPE_STRING);
					if(!cell.getStringCellValue().trim().isEmpty()) {
						if(!listSE.contains(cell.getStringCellValue().trim())) listSE.add(cell.getStringCellValue().trim());
					}
				}
			}
			//
			conn = ds.getConnection();
			//
			List<CaseData> listCase = new ArrayList<>();
			//是否不分區
			boolean allStage = false;
			for(String stage: listStage) {
				if(stage.startsWith("Z")) {
					allStage = true;
				}
			}
			if(allStage) {
				List<CaseData> list = this.selectStageCasesFromDB("Z", conn);
				for(CaseData theCase: list) {
					listCase.add(theCase);
				}
			}else {
				for(String stage: listStage) {
					List<CaseData> list = this.selectStageCasesFromDB(String.valueOf(stage.charAt(0)), conn);
					if(list == null || list.isEmpty()) continue;
					//
					for(CaseData theCase: list) {
						listCase.add(theCase);
					}
				}
			}
			if(listCase.isEmpty())
				throw new MyException("所選階段內沒有任何申請人員。");
			//
			List<CaseData> listMatch = new ArrayList<>();
			//
			for(CaseData theCase: listCase){
				if(theCase.getListChoice() == null || theCase.getListChoice().isEmpty()) continue;
				//比對 日期區間
				LocalDate _dateFrom = Gutil.toLocalDate(theCase.getListChoice().get(0).getFromDate());
				LocalDate _dateEnd = Gutil.toLocalDate(theCase.getListChoice().get(theCase.getListChoice().size() - 1).getEndDate());
				
				if(dateFrom != null && dateEnd != null){
					if(_dateFrom == null || _dateEnd == null) continue;
					if(_dateFrom.isBefore(dateFrom) || _dateFrom.isAfter(dateEnd)) continue;
				}
				//比對 地區別人士
				if(area.contains("大陸")) {
					boolean keyWordExisted = false;
					if(theCase.getNationality().toLowerCase().contains("people")
							&& theCase.getNationality().toLowerCase().contains("china"))
						keyWordExisted = true;
					//
					if(area.contains("非") && keyWordExisted) continue;
					if(!area.contains("非") && !keyWordExisted) continue;
				}else if(area.contains("東南亞")){
					boolean found = false;
					for(String key: listSE){
						if(theCase.getNationality().toLowerCase().contains(key.toLowerCase())){
							found = true;
							break;
						}
					}
					if(!found) continue;
				}
				//比對國籍
				if(!nationality.isEmpty()
						&& !theCase.getNationality().toLowerCase().contains(nationality.trim().toLowerCase()))
					continue;
				//比對學籍所在地
				if(!schoolLocation.isEmpty() 
						&& !theCase.getSchoolLocation().toLowerCase().contains(schoolLocation.trim().toLowerCase()))
					continue;
				//比對 報到年度
				if(!year.trim().isEmpty()){
					if(_dateFrom == null) continue;
					//
					int intYear = Integer.parseInt(year);
					if(year.length() == 3) intYear += 1911;
					if(!_dateFrom.toString().startsWith(String.valueOf(intYear))) continue;
				}
				//比對科別
				if(!depDiv.trim().equals("")){
					String nameAll = "";
					for(DiviChoice choice: theCase.getListChoice()) {
						nameAll += choice.getDept().getDeptNameCh();
						if(choice.getDivi() != null) nameAll += choice.getDivi().getDivNameCh(); 
					}
					//
					boolean found = false;
					String[] ss = depDiv.trim().split(" ");
					for(String s: ss){
						if(nameAll.contains(s)){
							found = true;
							break;
						}
					}
					if(!found) continue;
				}
				//比對 送訓單位
				//if(!delegate.equals("") && !prof.delegateUnit.contains(delegate)) continue;
				//
				listMatch.add(theCase);
			}
			//排序
			HashMap<String, CaseData> map = new HashMap<>();
			List<String> listKey = new ArrayList<>();
			for(CaseData theCase: listMatch){
				String key = theCase.getListChoice().get(0).getEndDate() + "#" + theCase.getPersonNameEn();
				map.put(key, theCase);
				listKey.add(key);
			}
			Collections.sort(listKey);
			
			listCase = new ArrayList<>();
			for(String key: listKey){
				listCase.add(map.get(key));
			}
			//
			Workbook wb = WorkbookFactory.create(MinioUtils.getObjectStream(ctxProp.getSiteId(), itemTempl.objectName(), ctxProp.getMinioClient()));
			Sheet ws = wb.getSheetAt(0);
			Row row = ws.getRow(1);
			int ccCols = row.getLastCellNum();
			//
			List<CellStyle> listStyle = new ArrayList<>();
			for(int i=0; i < ccCols; i++){
				listStyle.add(row.getCell(i).getCellStyle());
			}
			//
			//int totTuition = 0;
			int rowIdx = 1;
			int seq = 0;
			for(CaseData theCase: listCase){
				String _dateFrom = theCase.getListChoice().get(0).getFromDate();
				String _dateEnd = theCase.getListChoice().get(theCase.getListChoice().size() - 1).getEndDate(); 
				//
				ws.shiftRows(rowIdx, ws.getLastRowNum(), 1);
				row = ws.createRow(rowIdx);
				for(int i=0; i < ccCols; i++){
					row.createCell(i).setCellStyle(listStyle.get(i));
				}
				//
				seq++;
				row.getCell(0).setCellValue(seq);
				row.getCell(1).setCellValue(_dateFrom);
				row.getCell(2).setCellValue(_dateEnd);
				row.getCell(3).setCellValue(this.calcWeeks(Gutil.toLocalDate(_dateFrom), Gutil.toLocalDate(_dateEnd)));
				row.getCell(4).setCellValue(theCase.getPersonNameEn());
				row.getCell(5).setCellValue(theCase.getPersonNameCh());
				if(theCase.getGender().toLowerCase().contains("female")){
					row.getCell(6).setCellValue("女");
				}else{
					row.getCell(6).setCellValue("男");
				}
				row.getCell(7).setCellValue(theCase.getNationality());
				row.getCell(8).setCellValue(theCase.getAffiliation());
				row.getCell(9).setCellValue(theCase.getSchoolLocation());
				row.getCell(10).setCellValue(theCase.getRoleStatus());
				//row.getCell(12).setCellValue(this.getTitleFromStatus(prof));
				//row.getCell(13).setCellValue(prof.delegateUnit); 		//委訓單位
				//row.getCell(14).setCellValue(prof.tuition);  			//收費
				//
				String dpeDivTeach = "";
				for(DiviChoice choice: theCase.getListChoice()) {
					String s = "";
					if(!choice.getDept().getDeptNameCh().isEmpty()){
						s += choice.getDept().getDeptNameCh();
					}else {
						s += choice.getDept().getDeptNameEn();
					}
					if(choice.getDivi() != null) {
						if(!choice.getDivi().getDivNameCh().isEmpty()) {
							s += "/" + choice.getDivi().getDivNameCh();
						}else {
							s += "/" + choice.getDivi().getDivNameEn();
						}
					}
					
					String teachers = "";
					if(choice.getTeacher1() != null) {
						if(!choice.getTeacher1().getTeacherNameCh().isEmpty()) {
							if(!teachers.isEmpty()) teachers += "、";
							teachers += choice.getTeacher1().getTeacherNameCh();
						}else if(!choice.getTeacher1().getTeacherNameEn().isEmpty()) {
							if(!teachers.isEmpty()) teachers += "、";
							teachers += choice.getTeacher1().getTeacherNameEn();
						}
					}
					if(choice.getTeacher2() != null) {
						if(!choice.getTeacher2().getTeacherNameCh().isEmpty()) {
							if(!teachers.isEmpty()) teachers += "、";
							teachers += choice.getTeacher2().getTeacherNameCh();
						}else if(!choice.getTeacher2().getTeacherNameEn().isEmpty()) {
							if(!teachers.isEmpty()) teachers += "、";
							teachers += choice.getTeacher2().getTeacherNameEn();
						}
					}
					if(choice.getTeacher3() != null) {
						if(!choice.getTeacher3().getTeacherNameCh().isEmpty()) {
							if(!teachers.isEmpty()) teachers += "、";
							teachers += choice.getTeacher3().getTeacherNameCh();
						}else if(!choice.getTeacher3().getTeacherNameEn().isEmpty()) {
							if(!teachers.isEmpty()) teachers += "、";
							teachers += choice.getTeacher3().getTeacherNameEn();
						}
					}
					if(!teachers.isEmpty())
						s += "(" + teachers + ")";
					//
					if(!dpeDivTeach.isEmpty()) dpeDivTeach += "、";
					dpeDivTeach += s;
				}
				row.getCell(11).setCellValue(dpeDivTeach);				//見習部科別 
				//
				//totTuition += prof.tuition;
				//
				rowIdx++;
			}
			//row = ws.getRow(rowIdx);
			//row.getCell(14).setCellValue(totTuition);
			//
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        wb.write(baos);
	        
	        String fileName = "外籍醫事申請人員統計表_" + sdfss.format(new java.util.Date())  + ".xlsx";
	        //Document doc = MyUtil.saveToAlfresco(fileName, baos, Gutil.getExcelMimeType(), fdDownload, alfSession);
			//
			MyUtil.performDownloadWithDialog(baos, fileName);
		}catch(Exception ex) {
			Gutil.handleException(ex);
		}finally {
			Gutil.close(conn);
		}
	}
	
	//加圖形驗證碼
	private void createLoginWindowWithCaptcha(CtxProperty ctxProp,
			User user,
			DeviceType deviceType,
			VerticalLayout layoutHero) throws Exception{
		final Window window = new Window(" 登入");
		window.setWidth(380, Unit.PIXELS);
		window.setHeight(440, Unit.PIXELS);
		if(deviceType == DeviceType.Pad || deviceType == DeviceType.Mobile) {
			window.setWidth(500, Unit.PIXELS);
			window.setHeight(780, Unit.PIXELS);
		}

		window.setModal(false);
		window.setClosable(false);
		window.center();
		window.setIcon(VaadinIcons.DOCTOR);
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
				final TextField txtUserNo = new TextField("帳號");
				txtUserNo.setWidth(100, Unit.PERCENTAGE);
				txtUserNo.setIcon(VaadinIcons.USER);
				vLayout.addComponent(txtUserNo);

				final TextField txtPwd = new PasswordField("密碼");
				txtPwd.setWidth(100, Unit.PERCENTAGE);
				txtPwd.setIcon(VaadinIcons.PASSWORD);
				vLayout.addComponent(txtPwd);

				Captcha cap = getCaptcha();
				//
				final TextField txtCaptcha = new TextField("請回答");
				txtCaptcha.setWidth(100, Unit.PERCENTAGE);

				HorizontalLayout hlCap = new HorizontalLayout();
				hlCap.setWidth(100, Unit.PERCENTAGE);
				//hlCap.setHeight(40, Unit.PIXELS);
				{
					Image image = new Image("Captcha", Gutil.createStreamResource(cap.biCaptcha));
					image.setHeight(40, Unit.PIXELS);
					hlCap.addComponent(image);
					//
					Button btnRefresh = new Button(VaadinIcons.REFRESH);
					btnRefresh.setStyleName(ValoTheme.BUTTON_BORDERLESS);
					btnRefresh.addClickListener(e ->{
						try {
							Captcha cap2 = getCaptcha();
							cap.ans = cap2.ans;
							cap.biCaptcha = cap2.biCaptcha;
							//
							image.setSource(Gutil.createStreamResource(cap.biCaptcha));
						}catch(Exception ex) {
							Gutil.handleException(ex);
						}
					});
					hlCap.addComponent(btnRefresh);
					hlCap.setComponentAlignment(btnRefresh, Alignment.BOTTOM_LEFT);
					//
					hlCap.addComponent(txtCaptcha);
					hlCap.setExpandRatio(txtCaptcha, 1);
				}
				vLayout.addComponent(hlCap);
				vLayout.setComponentAlignment(hlCap, Alignment.TOP_LEFT);
				//
				final Button btnLogin = new Button("登入");
				btnLogin.addClickListener(e ->{
					try {
						if(txtUserNo.getValue().trim().isEmpty()
								|| txtPwd.getValue().trim().isEmpty()) throw new MyException("請輸入您的卡號與密碼。");
						//
						if(txtCaptcha.getValue().trim().isEmpty())
								throw new MyException("請先回答圖形驗證的答案。");
						int val = 0;
						try{
							val = Integer.parseInt(txtCaptcha.getValue().trim());
						}catch(Exception ex) {
						}
						if(val != cap.ans) throw new MyException("請先回答圖形驗證的答案。");
						//
						user.userNo = txtUserNo.getValue().trim();
						//
						MinioClient client = null;
						try {
							client = MinioUtils.getMinioClient(ctxProp.getMinioHost(),
									user.userNo,
									txtPwd.getValue().trim());
						}catch(Exception _ex) {
						}
						if(client == null)
							throw new MyException("您的帳號或密碼有誤。");
						//
						ctxProp.setMinioClient(client);
						user.passwd = txtPwd.getValue().trim();
						//儲存登入資訊
						MyUtil.saveAuthCookieInSession(MyUtil.COOKIE_NAME, user);
						//
						window.close();
						//
						this.jobsAfterAuthentication(layoutHero);
					}catch(Exception ex) {
						Gutil.handleException(ex);
					}
				});
				vLayout.addComponent(btnLogin);
				vLayout.setComponentAlignment(btnLogin, Alignment.TOP_RIGHT);
				//
				txtPwd.addShortcutListener(new ShortcutListener("Shortcut Name", KeyCode.ENTER, null) {
					private static final long serialVersionUID = 1L;
					@Override
					public void handleAction(Object sender, Object target) {
						btnLogin.click();
					}
				});
			}
			layout.addComponent(vLayout);
		}
		//
		window.setContent(layout);
		UI.getCurrent().addWindow(window);
	}
	
	private Captcha getCaptcha() throws Exception{
		int rdn1 = (int) (Math.random() * 10);
		int rdn2 = (int) (Math.random() * 10);
		int rdn3 = (int)(Math.random() * 10) % 2; 
		int ans = rdn1 + rdn2;
		
		String captchaQuiz = rdn1 + " + " + rdn2;
		if(rdn3 == 1){
			captchaQuiz = rdn1 + " x " + rdn2;
			ans = rdn1 * rdn2;
		}
		String captchaLabel = captchaQuiz + " = ?";
		//
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Comic Sans MS", Font.ITALIC, 36);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(captchaLabel);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width + 10, height, BufferedImage.TYPE_INT_ARGB);
        
        g2d = img.createGraphics();
        if(rdn1 % 3 == 0){
			g2d.setBackground(java.awt.Color.GREEN);
		}else{
			g2d.setBackground(java.awt.Color.GRAY);
		}
		g2d.clearRect(0, 0, width + 10, height);
        
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        //
        for (int i = 0; i < img.getWidth(); i++) {
    		for (int j = 0; j < img.getHeight(); j++) {
				int rdn4 = (int) (Math.random() * 10);
				if(rdn4 % 15 == 0){
					img.setRGB(i, j, new java.awt.Color(255, 255, 255).getRGB());
    			}
				
    		}
		}
        //
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(java.awt.Color.BLACK);
        g2d.drawString(captchaLabel, 0, fm.getAscent());
        //
        java.util.Random random = new java.util.Random();
		for (int i = 0; i < img.getWidth(); i = i + 16) {
			int x1 = random.nextInt(img.getWidth() - 10 + 1);
			int y1 = random.nextInt(img.getHeight() - 1);
			int x2 = random.nextInt(img.getWidth() - 10 + 1);
			int y2 = random.nextInt(img.getHeight() - 1);
			g2d.drawLine(x1, y1, x2, y2);
		}
        g2d.dispose();
        //
       	Captcha cap = new Captcha();
       	cap.biCaptcha = img;
       	cap.ans = ans;
       	//
       	return cap;
	}
	
  	private void createMinioDocGrid(Grid<MinioDoc> gridFile,
  			boolean isAdminRequest) {
  		final ButtonRenderer<MinioDoc> rendererDownload = new ButtonRenderer<>(e -> {
  			try{
  				if(e.getItem() == null) throw new MyException("檔案找不到。");
  				MinioDoc doc = (MinioDoc)e.getItem();
  				//
  				InputStream is = MinioUtils.getObjectStream(ctxProp.getSiteId(), doc.objectName, ctxProp.getMinioClient());
  				//MinioUtils
  				MyUtil.performDownloadWithDialog(is, doc.pureName);
  			}catch(Exception ex) {
  				Gutil.handleException(ex);
  			}
  		});
  		rendererDownload.setHtmlContentAllowed(true);
  		//
  		gridFile.addColumn(MinioDoc::getIdx).setCaption("序").setWidth(60d);
  		gridFile.addColumn(MinioDoc::getPureName).setCaption("包含檔案").setMinimumWidth(160d).setExpandRatio(1);
  		gridFile.addColumn(f -> VaadinIcons.CLOUD_DOWNLOAD_O.getHtml(), rendererDownload).setCaption("下載").setWidth(75d).setStyleGenerator(item -> "v-align-center");
  		gridFile.addColumn(MinioDoc::getUpdatedTime).setCaption("更新時間").setWidth(180d);
  		gridFile.addColumn(MinioDoc::getVersions).setCaption("版次").setWidth(80d);
  		gridFile.addColumn(MinioDoc::getFileKb).setCaption("KB").setWidth(80d).setStyleGenerator(item -> "v-align-right");
  		if(isAdminRequest) {
  			final ButtonRenderer<MinioDoc> rendererDelete = new ButtonRenderer<>(e -> {
  				try{
  					MinioDoc itemDoc = e.getItem();
  					gridFile.select(itemDoc);
  					//
  					if(itemDoc.pureName.equalsIgnoreCase("profile.json") 
  							|| itemDoc.pureName.equalsIgnoreCase("batch.json")) return;
  					//
  					MessageBox.createQuestion().withCaption("刪除檔案")
  					.withMessage("確定刪除「" + itemDoc.pureName + "」?")
  					.withYesButton(()->{
  						MessageBox.createQuestion().withCaption("刪除檔案")
  						.withMessage("檔案刪除後即不可復原，確定刪除嗎?")
  						.withYesButton(()->{
  							try {
  								String caseId = itemDoc.getObjectName().substring(0, itemDoc.objectName.lastIndexOf("/"));
  								MinioUtils.deleteObject(ctxProp.getSiteId(), itemDoc.objectName, ctxProp.getMinioClient());
  								//
  								List<MinioDoc> listDoc = this.loadCaseFiles(caseId);
  								gridFile.setItems(listDoc);
  							}catch(Exception ex) {
  								Gutil.handleException(ex);
  							}
  						})
  						.withCancelButton()
  						.open();
  					})
  					.withCancelButton()
  					.open();
  				}catch(Exception ex) {
  					Gutil.handleException(ex);
  				}
  			});
  			rendererDelete.setHtmlContentAllowed(true);
  			//
  			gridFile.addColumn(m -> VaadinIcons.TRASH.getHtml(), rendererDelete).setCaption("刪除").setWidth(80d).setStyleGenerator(item -> "v-align-center");
  		}
  	}
  	
  	//載入焦點案件所包含檔案
  	private List<MinioDoc> loadCaseFiles(String caseId) throws Exception{
  		List<MinioDoc> list = new ArrayList<>();
  		//
  		List<Item> listItem = MinioUtils.listObjects(ctxProp.getSiteId(), true, caseId, ctxProp.getMinioClient());
  		for(Item item: listItem) {
  			if(item.isDir()) continue;
  			//
  			MinioDoc doc = new MinioDoc();
  			doc.pureName = MinioUtils.getObjectPureName(item);
  			//doc.pureName = item.objectName();
  			doc.objectName = item.objectName();
  			doc.updatedTime = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(item.lastModified());
  			int dd = (int)Math.round(item.size()/1024);
  			doc.fileKb = String.format("%,d", dd);
  			doc.versions = item.versionId();
  			//
  			list.add(doc);
  		}
		Collections.sort(list);
		//重新編號
		List<MinioDoc> listSorted = new ArrayList<>();
		int idx = 0;
		for(MinioDoc doc: list) {
			idx++;
			doc.idx = idx;
			listSorted.add(doc);
		}
		//
		return listSorted;
  	}
	
	private void loadCaseFilesIntoGrid(String caseId,
					String caseNo,
					Button btnSpace,
					Grid<MinioDoc> gridFile) throws Exception{
		//AlfrescoFolder fdCase = MyUtil.getCaseFolder(Gutil.trimAlfDocId(alfDocId), alfSession);
		gridFile.setItems(this.loadCaseFiles(caseId));
		//前往儲存區之路徑
		//String urlCurrCase = "/share/page/site/" + siteName + "/documentlibrary#filter=path%7C";
		//urlCurrCase += "/" + URLEncoder.encode(fdCase.getFolderParent().getName(), "UTF-8");
		//urlCurrCase += "/" + URLEncoder.encode(fdCase.getName(), "UTF-8");
		//
		//btnSpace.setId(urlCurrCase);
		btnSpace.setCaption("前往案號(" + caseNo + ")資料儲存區");
	}
    

	private void addGridFooter(Grid<?> grid, ListDataProvider<?> ldp ) throws Exception{
		if(grid.getFooterRowCount() > 0) {
			for(int i=0; i < grid.getFooterRowCount(); i++) {
				grid.removeFooterRow(i);
			}
		}
		FooterRow filterRow = grid.appendFooterRow();
		//
		for (Grid.Column<?, ?> column : grid.getColumns()) {
            FooterCell cellFilter = filterRow.getCell(column);
            //Add a textfield
            if(column.getCaption().equalsIgnoreCase("序")){
            	Image image = new Image(null, new ClassResource("/filter22.png"));
            	image.addStyleName("my-image-button");
            	image.addClickListener(e -> {
            		ldp.clearFilters();
            		ldp.refreshAll();
            		//
            		for(Column<?, ?> col : grid.getColumns()) {
    					FooterCell cf = filterRow.getCell(col);
    					if(!cf.getCellType().toString().equalsIgnoreCase("widget") 
    							|| !cf.getComponent().getClass().getTypeName().contains("TextField")) continue;
    					//
    					TextField tf = (TextField)cf.getComponent();
    					tf.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    					String valFilter = tf.getValue().toString().trim();
    					if(Gutil.isEmpty(valFilter)) continue;
    					//
    					if(tf.getValue().toString().trim().equalsIgnoreCase("true") 
    							|| tf.getValue().toString().trim().equalsIgnoreCase("false")){
    						ldp.addFilter(f -> {
    	            			try {
    	            				java.lang.reflect.Field currField = this.getFieldByName(f, col.getId());
    	            				if(currField == null) return false;
    	            				//
    	            				currField.setAccessible(true);
    	            				String val = currField.get(f).toString();
    	            				return val.equalsIgnoreCase(valFilter);
    	            			}catch(Exception ex) {
    	            				return false;
    	            			}
    	            		});
    					}else{
    						ldp.addFilter(f -> {
    	            			try {
    	            				if(col.getId().contains(".")) {
    	            					String[] attrs = col.getId().split("\\.");
    	            					java.lang.reflect.Field fld = this.getFieldByName(f, attrs[0]);
    	            					if(fld == null) return false;
    	            					//
    	            					Object obj = fld.get(f);
    	            					for(int i=1; i < attrs.length-1; i++) {
    	            						fld = this.getFieldByName(obj, attrs[i]);
    	            						if(fld == null) return false;
    	            						obj = fld.get(obj);
    	            					}
    	            					if(obj == null) return false;
    	            					//
    	            					fld = this.getFieldByName(obj, attrs[attrs.length - 1]);
    	            					if(fld == null) return false;
    	            					//
    	            					fld.setAccessible(true);
    	            					String val = fld.get(obj).toString();
	    	            				return val.contains(valFilter);
    	            				}else {
    	            					java.lang.reflect.Field currField = this.getFieldByName(f, col.getId());
    	            					if(currField == null) return false;
    	            					//
    	            					currField.setAccessible(true);
    	            					String val = currField.get(f).toString();
    	            					return val.contains(valFilter);
    	            				}
    	            			}catch(Exception ex) {
    	            				return false;
    	            			}
    	            		});
    					}
    				}
        		});
            	cellFilter.setComponent(image);
            }else{
            	TextField filter = new TextField();
            	filter.addStyleName(ValoTheme.TEXTFIELD_TINY);
        	    filter.setSizeFull();
            	cellFilter.setComponent(filter);
            }
        }
	}
	
	private java.lang.reflect.Field getFieldByName(Object obj, String fldName){
		java.lang.reflect.Field fld = null;
		try {
			fld = obj.getClass().getDeclaredField(fldName);
		}catch(Exception _ex) {
		}
		if(fld == null) {
			try {
				fld = obj.getClass().getField(fldName);
			}catch(Exception _ex) {
			}
		}
		return fld;
	}
	
	private void fetchStageCases(String statusNo,
			Grid<CaseData> gridCase,
			Grid<MinioDoc> gridFile,
			java.util.List<CaseData> listCaseProvider,
			ListDataProvider<CaseData> ldpCase){
		Connection conn = null;
		try{		
			conn = ds.getConnection();
			//
			gridFile.setItems(new ArrayList<MinioDoc>());
			//取消過濾器
			ldpCase.clearFilters();
			//清空篩選條件
			FooterRow filterRow = gridCase.getFooterRow(0);
			for(Grid.Column<CaseData, ?> col : gridCase.getColumns()) {
				FooterCell cf = filterRow.getCell(col);
				if(cf.getCellType().toString().equalsIgnoreCase("widget")
						&& cf.getComponent().getClass().getTypeName().contains("TextField")){
					TextField tf = (TextField)cf.getComponent();
					tf.setValue("");
				}
			}
			//
			HashMap<String, CaseData> mapSort = new HashMap<String, CaseData>();
			//
			List<CaseData> listCase = this.selectStageCasesFromDB(statusNo, conn);
			for(CaseData theCase: listCase){
				String key = theCase.getCaseNo() + "_" + theCase.getPersonNameEn();
				mapSort.put(key, theCase);
			}
			//排序
			List<String> listKey = new ArrayList<>();
			for(String key: mapSort.keySet()){
				listKey.add(key);
			}
			Collections.sort(listKey);
			//
			listCaseProvider.clear();
			//
			int idx = 0;
			for(String key: listKey){
				idx++;
				CaseData theCase = mapSort.get(key);
				theCase.setIdx(idx);
				listCaseProvider.add(theCase);
			}
		}catch(Exception ex){
			Gutil.handleException(ex);
		}finally{
			Gutil.close(conn);
		}
	}

	private List<CaseData> selectStageCasesFromDB(String stageNo, Connection conn) throws Exception{
		List<CaseData> listCase = new ArrayList<>();
		//
		String sql = "SELECT doc_id, profile_json FROM tams WHERE module_name = ? ";
		if(!stageNo.startsWith("Z")) {
			sql += " AND stage_name=?";
		}
		PreparedStatement ps = conn.prepareCall(sql);
		ps.setString(1, ctxProp.getSiteId());
		if(!stageNo.startsWith("Z")) {
			ps.setString(2, stageNo);
		}
		ResultSet rs = ps.executeQuery();
		while(rs.next()){
			CaseData theCase = gson.fromJson(rs.getString(2), CaseData.class);
			//
			theCase.setCaseId(rs.getString(1));
			theCase.setStageNo(stageNo);
			//
			listCase.add(theCase);
		}
		rs.close();
		//
		return listCase;
	}
	
	private Layout createApplyLayout(CaseData theCase, Connection conn) throws Exception{
		VerticalLayout layoutHero = new VerticalLayout();
		//
		Gutil.openApplyForm(layoutHero, ctxProp, theCase, conn, ds);
		//
		layoutHero.setWidth(100, Unit.PERCENTAGE);
		layoutHero.setMargin(true);
		//
		return layoutHero;	
	}
	
	//臨床單位回覆
	private void processClinicalResponse(CaseData theCase,
			VerticalLayout layoutHero) {
		Connection conn = null;
		try {
			conn = ds.getConnection();
			//
			ClinicalResponseForm form = new ClinicalResponseForm();
			//
			String caseInfo = "國籍：" + theCase.getNationality() + "、英文姓名：" + theCase.getPersonNameEn() + "、角色：" + theCase.getRoleStatus();
			form.txtCaseInfo.setValue(caseInfo);
			form.txtCaseInfo.setReadOnly(true);
			//
			form.rbgAgree.addValueChangeListener(e ->{
				if(e.getValue().equals("不同意")) {
					form.txtReason.setVisible(true);
					form.txtTuition.setVisible(false);
				}else {
					form.txtReason.setVisible(false);
					form.txtTuition.setVisible(true);
				}
			});
			//
			form.txtMohwFile.setReadOnly(true);
			MohwUploader uploader = new MohwUploader(theCase,
					form.txtMohwFile,
					dirUpload,
					ctxProp);
			form.uploadMohwFile.setReceiver(uploader);
			form.uploadMohwFile.addSucceededListener(uploader);
			//
			form.btnSend.addClickListener(e -> {
				try{
					if(form.rbgAgree.getValue() == null || form.rbgAgree.getValue().isEmpty())
						throw new MyException("請先勾選是否接受申請人來院進修。");
					if(form.rbgAgree.getValue().equals("不同意")
							&& form.txtReason.getValue().trim().isEmpty())
						throw new MyException("請輸入不同意理由。");
					if(!form.rbgAgree.getValue().equals("不同意")
							&& form.txtMohwFile.getValue().trim().isEmpty())
						throw new MyException("請先上傳報衛福部申請書檔案（MS Word或PDF類型檔案）。");
					//
					MessageBox.createQuestion().withCaption("案件簽核結果回覆")
					.withMessage("確定以所填資訊回覆教學部醫學教學組？")
					.withYesButton(()->{
						Connection conn2 = null;
						try {
							if(form.rbgAgree.getValue().equals("不同意")){
								theCase.setAgreeByVghTc(false);
								theCase.setRejectReason(form.txtReason.getValue().trim());
							}else {
								theCase.setAgreeByVghTc(true);
								theCase.setRejectReason("");
								if(!form.txtTuition.getValue().trim().isEmpty()) {
									int fee = 0;
									try {
										fee = Integer.parseInt(form.txtTuition.getValue().trim());
									}catch(Exception _ex) {
									}
									if(fee == 0)
										throw new MyException("進修費用無法辨識。");
									theCase.setTuition(fee);
								}
								
							}
							conn2 = ds.getConnection();
							Gutil.saveCaseData(ctxProp, theCase, conn2);
							//通知醫學教學組
							if(!ctxProp.getCcAccount().isEmpty()) {
								String msg = "臨床單位已同意外籍專業人士「" + theCase.getPersonNameEn() + "」之來院進修申請，報衛福部申請書請參考隨函附件檔案。";
								if(!theCase.isAgreeByVghTc())
									msg = "臨床單位已駁回外籍專業人士「" + theCase.getPersonNameEn() + "」之來院進修申請，不同意理由：<br/>"
											+ theCase.getRejectReason();
								//
								
								Item itemMohw = MyUtil.findWordItemByHeader(theCase.getCaseId(), Gutil.FILE_HEADER_MOHW_APPLY, ctxProp);
								if(itemMohw == null)
									itemMohw = MyUtil.findPdfItemByHeader(theCase.getCaseId(), Gutil.FILE_HEADER_MOHW_APPLY, ctxProp);
								if(itemMohw == null)
									throw new MyException("後台儲存區找不到報衛福部申請書檔案。");
								//
								List<MailAttach> listAttach = new ArrayList<>();
								listAttach.add(new MailAttach(MinioUtils.getObjectPureName(itemMohw),
										MinioUtils.getObjectStream(ctxProp.getSiteId(), itemMohw.objectName(), ctxProp.getMinioClient())));
								
								javax.mail.Session mailSession = Gutil.getMailSession(ctxProp);
								Gutil.sendMailTLS(Arrays.asList(ctxProp.getCcAccount().split(";")),
													"臨床單位回覆外籍人士「" + theCase.getPersonNameEn() + "」之來院進修申請通知",
													msg,
													mailSession,
													ctxProp.getFromAccount(),
													Arrays.asList(theCase.getUdTakerEmails().split(";")),
													listAttach);
							}							
							//到 登入畫面
							MessageBox.createQuestion().withCaption("案件簽核結果回覆")
							.withMessage("已回覆完成並電子郵件知會教學部醫學教學組承辦人。")
							.withOkButton(()->{
								try {
									MyUtil.logout(UI.getCurrent(), Gutil.COOKIE_NAME);
								}catch(Exception ex) {
									Gutil.handleException(ex);
								}
							})
							.open();
						}catch(Exception ex) {
							Gutil.handleException(ex);
						}finally {
							Gutil.close(conn2);
						}						
					})
					.withCancelButton()
					.open();
					
				}catch(Exception ex) {
					Gutil.handleException(ex);
				}
			});
			//
			layoutHero.removeAllComponents();
			layoutHero.addComponent(form);
			
		}catch(Exception ex) {
			Gutil.handleException(ex);
		}finally {
			Gutil.close(conn);
		}
	}
	
	
	@WebServlet(urlPatterns = "/*", name = "ManagerServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = ManagerUI.class, productionMode = false)
    public static class ManagerServlet extends VaadinServlet {
		private static final long serialVersionUID = 1L;
    }
}