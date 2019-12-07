package com.olbius.export;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
public class ExportDocument {
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}
	
	public static void exportDataToExcel(HttpServletRequest request, HttpServletResponse response, ExportDataInfo data, String url){
		try {
			HSSFWorkbook template;		
			template = new HSSFWorkbook(new URL(url).openStream());
			HSSFWorkbook excelExprot = new HSSFWorkbook();
			HSSFSheet sheet = template.getSheetAt(0);
			HSSFSheet newSheet = excelExprot.createSheet();
			ExportUtil.copySheets(newSheet, sheet, true);
			int rowNbr = newSheet.getLastRowNum();			
			String fileName = data.getFileName();
			List<Map<Integer, String>> listData = data.getData();
			for(Map<Integer, String> temp: listData){
				rowNbr++;
				Row newRow = newSheet.createRow(rowNbr);
				for(Map.Entry<Integer, String> entry: temp.entrySet()){
					Cell newCell = newRow.createCell(entry.getKey());
					if(entry.getValue() != null){
						newCell.setCellValue(entry.getValue());
					}
				}
			}
			response.setHeader("content-disposition", "attachment;filename=" + fileName);
			response.setContentType("application/vnd.xls");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();	
			excelExprot.write(bos);
			byte[] bytes = bos.toByteArray();
			response.getOutputStream().write(bytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
