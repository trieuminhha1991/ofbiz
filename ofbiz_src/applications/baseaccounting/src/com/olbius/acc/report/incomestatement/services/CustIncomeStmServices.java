package com.olbius.acc.report.incomestatement.services;

import com.olbius.acc.report.ReportServiceInterface;
import org.ofbiz.service.DispatchContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class CustIncomeStmServices implements ReportServiceInterface {

	protected List<Map<String, Object>> runQuery(DispatchContext dctx, Map<String, Object> context, String dimension){
		return null;
	}
	

	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		return null;
	}
	
	@Override
	public void exportToExcel(HttpServletRequest request,
			HttpServletResponse response) {
		
	}

	@Override
	public void exportToPdf(HttpServletRequest request,
			HttpServletResponse response) {
		
	}
	
}
