package com.olbius.acc.report;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.service.DispatchContext;

public interface ReportServiceInterface {
	public final static String resource = "BaseAccountingUiLabels";
	
	//Get data for grid
	public Map<String, Object> getData (DispatchContext dctx, Map<String, Object> context);
	
	//Export grid to excel
	public void exportToExcel(HttpServletRequest request, HttpServletResponse response);
	
	//Export grid to pdf
	public void exportToPdf(HttpServletRequest request, HttpServletResponse response);
}
