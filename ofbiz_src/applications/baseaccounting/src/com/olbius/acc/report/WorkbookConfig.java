package com.olbius.acc.report;

import java.util.List;

public class WorkbookConfig {
	private String name;
	private List<SheetConfig> sheetConfigs;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<SheetConfig> getSheetConfigs() {
		return sheetConfigs;
	}
	public void setSheetConfigs(List<SheetConfig> sheetConfigs) {
		this.sheetConfigs = sheetConfigs;
	}
}
