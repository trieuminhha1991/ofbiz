package com.olbius.basehr.importExport;

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
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class ImportExportExcel implements ImportExportFile {
	
	@Override
	public void importDataFromFile(Delegator delegator, GenericValue userLogin, Object importConfig) throws GenericEntityException {
		if(importConfig instanceof ImportExcelConfig){
			ImportExportWorker.importExcelData(delegator, userLogin, (ImportExcelConfig)importConfig);
		}
	}
	
	public static boolean isExcelFile(String contentType) {
		if(contentType!= null && (contentType.contains("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                ||contentType.contains("application/wps-office.xlsx"))){
			return true;
		}
		return false;
	}

	@Override
	public void exportDataToFile(HttpServletRequest request,HttpServletResponse response, Object config, String url){		
		if(config instanceof ExportExcelConfig){
			try {
				ExportExcelConfig data = (ExportExcelConfig)config;
				HSSFWorkbook template;		
				template = new HSSFWorkbook(new URL(url).openStream());
				HSSFWorkbook excelExprot = new HSSFWorkbook();
				HSSFSheet sheet = template.getSheetAt(0);
				HSSFSheet newSheet = excelExprot.createSheet();
				ImportExportWorker.copySheets(newSheet, sheet, true);
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
}
