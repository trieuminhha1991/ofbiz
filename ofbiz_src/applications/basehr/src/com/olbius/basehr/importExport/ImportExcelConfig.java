package com.olbius.basehr.importExport;

import java.sql.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

public class ImportExcelConfig {
	private Workbook excelData;
	private String entityName;
	private Integer startLine;
	private Locale locale;
	private String dateTimePattern;
	private Date fromDate;
	private Date thruDate;
	private String overrideDataWay;
	
	//map between column in excel file with field in database
	private Map<Integer, String> fieldColumnExcelCorr;
	private Map<String, List<String>> fieldInListMap;
	public ImportExcelConfig(Workbook excelData, String entityName) {
		this(excelData, entityName, null);
	}

	public ImportExcelConfig(Workbook excelData, String entityName,
			Map<Integer, String> fieldColumnExcelCorr) {
		super();
		this.excelData = excelData;
		this.entityName = entityName;
		this.fieldColumnExcelCorr = fieldColumnExcelCorr;
	}
	
	public Map<String, List<String>> getFieldInListMap() {
		return fieldInListMap;
	}

	public void setFieldInListMap(Map<String, List<String>> fieldInListMap) {
		this.fieldInListMap = fieldInListMap;
	}

	public Workbook getExcelData() {
		return excelData;
	}
	
	public Integer getStartLine() {
		return startLine;
	}

	public void setStartLine(Integer startLine) {
		this.startLine = startLine;
	}

	public void setExcelData(Workbook excelData) {
		this.excelData = excelData;
	}
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public Map<Integer, String> getFieldColumnExcelCorr(){
		return fieldColumnExcelCorr;
	}
	public void setFieldColumnExcelCorr(Map<Integer, String> fieldColumnExcelCorr) {
		this.fieldColumnExcelCorr = fieldColumnExcelCorr;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getDateTimePattern() {
		return dateTimePattern;
	}

	public void setDateTimePattern(String dateTimePattern) {
		this.dateTimePattern = dateTimePattern;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getThruDate() {
		return thruDate;
	}

	public void setThruDate(Date thruDate) {
		this.thruDate = thruDate;
	}

	public String getOverrideDataWay() {
		return overrideDataWay;
	}

	public void setOverrideDataWay(String overrideDataWay) {
		this.overrideDataWay = overrideDataWay;
	}


}
