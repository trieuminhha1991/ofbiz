package com.olbius.acc.report;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SheetConfig {
	private List<ColumnConfig> columnConfig;
	private List<Map<String, Object>> dataConfig;
	private Map<String, Object> groupConfigs;
	private String sheetName;
	private String title = "NO TITLE";
	private String resource;
	private boolean isGroup = false;
	private short headerHeight = 500;
	private Locale locale;

	public List<ColumnConfig> getColumnConfig() {
		return columnConfig;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public void setResource(String rs) {
		this.resource = rs;
	}

	public String getResource() {
		return this.resource;
	}

	public void setColumnConfig(List<ColumnConfig> columnConfig) {
		this.columnConfig = columnConfig;
	}

	public List<Map<String, Object>> getDataConfig() {
		return dataConfig;
	}

	public void setDataConfig(List<Map<String, Object>> dataConfig) {
		this.dataConfig = dataConfig;
	}

	public void setGroupConfigs(Map<String, Object> groupConfigs) {
		this.groupConfigs = groupConfigs;
	}

	public Map<String, Object> getGroupConfigs() {
		return this.groupConfigs;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public short getHeaderHeight() {
		return headerHeight;
	}

	public void setHeaderHeight(short headerHeight) {
		this.headerHeight = headerHeight;
	}

	/**
	 * if excel use group column mode need invoke this method to register
	 * 
	 * @author Namdn
	 */
	public void setGroup(boolean group) {
		this.isGroup = group;
	}

	/**
	 * return true if excels use group column,otherwise return false
	 * 
	 * @author Namdn
	 */
	public boolean isGroup() {
		return this.isGroup;
	}

}
