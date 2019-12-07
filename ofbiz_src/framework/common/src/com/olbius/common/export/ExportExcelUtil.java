package com.olbius.common.export;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilValidate;

public class ExportExcelUtil {
	
	public static String getParameter(Map<String, String[]> parameters, String key) {
		String resultValue = null;
		if (UtilValidate.isEmpty(key) || UtilValidate.isEmpty(parameters)) return resultValue;
		if (parameters.containsKey(key) && parameters.get(key).length > 0) {
			resultValue = parameters.get(key)[0]; 
		}
		return resultValue;
	}
	
	public static Cell createCell(Row row, int columnIndex, Object value, CellStyle cellStyle) {
		if (row == null) return null;
		
		Cell cell = row.createCell(columnIndex);
		if (value != null) {
			if (value instanceof String) cell.setCellValue((String) value);
			if (value instanceof Integer) cell.setCellValue((Integer) value);
			if (value instanceof Double) cell.setCellValue((Double) value);
			if (value instanceof Long) cell.setCellValue((Long) value);
		}
		if (cellStyle != null) cell.setCellStyle(cellStyle);
		return cell;
	}
	
	public static String getStringParameterValues(Map<String, String[]> parameters) {
		StringBuilder resultValue = new StringBuilder();
		if (UtilValidate.isEmpty(parameters)) return resultValue.toString();
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			resultValue.append(entry.getKey());
			resultValue.append("=");
			resultValue.append(entry.getValue());
			resultValue.append("@");
		}
		return resultValue.toString();
	}
}
