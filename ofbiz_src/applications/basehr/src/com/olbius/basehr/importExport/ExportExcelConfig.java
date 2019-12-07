package com.olbius.basehr.importExport;

import java.util.List;
import java.util.Map;

public class ExportExcelConfig {
	private String fileName;
	private List<Map<Integer, String>> data;
	
	public ExportExcelConfig() {
		super();
	}
	public ExportExcelConfig(String fileName, List<Map<Integer, String>> data) {
		super();
		this.fileName = fileName;
		this.data = data;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Map<Integer, String>> getData() {
		return data;
	}
	public void setData(List<Map<Integer, String>> data) {
		this.data = data;
	}
}
