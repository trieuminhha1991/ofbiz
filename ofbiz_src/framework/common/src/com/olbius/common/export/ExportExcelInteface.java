package com.olbius.common.export;

import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.ofbiz.service.DispatchContext;

public interface ExportExcelInteface {
	public void init(DispatchContext dctx, Map<String, Object> context);
	public String run();
	public SXSSFWorkbook getWb();
	public String getModuleExport();
	public String getDescriptionExport();
	public String getFileName();
	public String getExportType();
	public void setIsCheckTimeout(Boolean isCheckTimeout);
	public void setIsForceTimeout(Boolean isForceTimeout);
}
