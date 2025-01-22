package com.itez.vghtc.gtrainman;

import java.io.File;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.itez.vghtc.util.Dept;
import com.itez.vghtc.util.Divi;
import com.itez.vghtc.util.Gutil;
import com.itez.vghtc.util.MyException;
import com.itez.vghtc.util.Nation;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;

class SettingUploader implements Upload.Receiver, Upload.SucceededListener {
	private static final long serialVersionUID = -1L;
	private File file;
	private File dirUpload;
	private String paramName;
	private TextArea txtValue;
	private DataSource ds;
	
    public SettingUploader(String paramName,
    		TextArea txtValue,
    		File dirUpload,
    		DataSource ds) {
    	this.paramName = paramName;
    	this.txtValue = txtValue;
    	this.dirUpload = dirUpload;
    	this.ds = ds;
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
			if(paramName == null || paramName.isEmpty())
				throw new MyException("請先指定上傳的參數類別。");
			if(!Gutil.isExcelFile(file.getName())) {
				file.delete();
				throw new MyException("請以 MS Excel 檔案(副檔名 xlsx、xls)上傳。");
			}
			//
			conn = ds.getConnection();
			//
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			//
			Workbook wb = WorkbookFactory.create(file);
			Sheet ws = wb.getSheetAt(0);
			
			if(paramName.equals(Gutil.PARAM_NATION)) {
				List<Nation> listNation = this.getNations(ws);
				if(!listNation.isEmpty()) {
					String strJson = gson.toJson(listNation);
					this.addJsonToDB(paramName, strJson, conn);
					//
					txtValue.setValue(strJson);
				}
			}else if(paramName.equals(Gutil.PARAM_CLINIC_ORG)) {
				List<Divi> listDivi = this.getClinicOrgs(ws);
				if(!listDivi.isEmpty()) {
					String strJson = gson.toJson(listDivi,
							new TypeToken<List<Divi>>(){}.getType());
					this.addJsonToDB(paramName, strJson, conn);
					//
					txtValue.setValue(strJson);
				}
			}
    	}catch(Exception ex){
    		Gutil.handleException(ex);
    	}finally {
    		Gutil.close(conn);
    	}
    }

	private void addJsonToDB(final String paramName, final String strJson, Connection conn) throws Exception{
		boolean doUpdate = false;
		final String sql = "SELECT 1 FROM tams_param WHERE param_name = ?";
		PreparedStatement ps = conn.prepareCall(sql);
		ps.setString(1, paramName);
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			doUpdate = true;
		}
		rs.close();
		ps.close();
		//
		if(doUpdate) {
			final String sql2 = "UPDATE tams_param SET str_json = ? WHERE param_name = ?";
			ps = conn.prepareStatement(sql2);
			ps.setString(1, strJson);
			ps.setString(2, paramName);
			ps.execute();
			ps.close();
		}else {
			final String sql2 = "INSERT INTO tams_param (str_json, param_name) VALUES (?,?)";
			ps = conn.prepareStatement(sql2);
			ps.setString(1, strJson);
			ps.setString(2, paramName);
			ps.execute();
			ps.close();
		}
	}
	
	//讀取國別資料
	private List<Nation> getNations(Sheet ws) throws Exception{
		List<Nation> list = new ArrayList<>();
		//
		for(int i=2; i < ws.getPhysicalNumberOfRows(); i++) {
			Row row = ws.getRow(i);
			if(row == null) continue;
			//
			Nation na = new Nation();
			//
			Cell cell = row.getCell(0);
			if(cell == null) continue;
			//
			cell.setCellType(Cell.CELL_TYPE_STRING);
			na.setNationNameEn(cell.getStringCellValue().trim());
			if(na.getNationNameEn().isEmpty()) continue;
			//
			cell = row.getCell(1);
			if(cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				na.setNationNameCh(cell.getStringCellValue().trim());
			}
			//
			list.add(na);
		}
		return list;
	}
	
	//讀取部科別資料
	private List<Divi> getClinicOrgs(Sheet ws) throws Exception{
		List<Divi> listDivi = new ArrayList<>();
		//
		Iterator<Row> itRow = ws.rowIterator();
		Dept prevDept = null;
		while(itRow.hasNext()){
			Row row = itRow.next();
			if(row == null || row.getRowNum() == 0) continue;
			//
			Dept dept = new Dept(); 
			//部別
			Cell cell = row.getCell(0);
			if(cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				dept.setDeptNameEn(cell.getStringCellValue().trim());
			}
			
			cell = row.getCell(1);
			if(cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				dept.setDeptNameCh(cell.getStringCellValue().trim());
			}
			//
			if(dept.getDeptNameEn().isEmpty()) {
				if(prevDept == null) continue;
				dept = prevDept;
			}
			//
			//科別
			Divi divi = new Divi();
			
			divi.setDeptNameEn(dept.getDeptNameEn());
			divi.setDeptNameCh(dept.getDeptNameCh());
			
			cell = row.getCell(2);
			if(cell != null) {
				cell.setCellType(Cell.CELL_TYPE_STRING);
				if(!cell.getStringCellValue().trim().isEmpty()) {
					divi.setDivNameEn(cell.getStringCellValue().trim());
					//
					cell = row.getCell(3);
					if(cell != null) {
						cell.setCellType(Cell.CELL_TYPE_STRING);
						divi.setDivNameCh(cell.getStringCellValue().trim());
					}
				}
			}
			//
			boolean existed = false;
			for(Divi divi2: listDivi) {
				if(divi2.getDivNameEn().equalsIgnoreCase(divi.getDivNameEn())) {
					existed = true;
					break;
				}
			}
			if(!existed) listDivi.add(divi);	
			//
			prevDept = dept;
		}
		return listDivi;
	}
}

