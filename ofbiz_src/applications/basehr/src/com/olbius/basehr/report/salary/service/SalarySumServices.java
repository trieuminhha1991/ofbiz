package com.olbius.basehr.report.salary.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.report.salary.query.CodeSalarySumOlapImpl;
import com.olbius.basehr.report.salary.query.OrgSalarySumOlapImpl;
import com.olbius.basehr.report.salary.query.SalarySumOlapImpl;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.grid.OlapGrid;

public class SalarySumServices {
	
	public static Map<String, Object> getPayrollTableReport(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		//Get locale and delegator
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String customTime = (String)context.get("customTime");
		//Get OlapImpl
		SalarySumOlapImpl grid = new SalarySumOlapImpl(delegator,locale);
		
		//Set parameters for OlapImpl
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter("customTime", customTime);
		
		grid.setOlapResultType(OlapGrid.class);
		Map<String, Object> result = grid.execute(context);
		return result;
	}
	
	public static Map<String, Object> getPayrollTableReportByOrg(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String customTime = (String)context.get("customTime");
		String parentId = (String)context.get("parentId");
		String organization = (String)context.get("organization");
		if(organization == null && parentId == null){
			organization = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		}
		//Get OlapImpl
		OrgSalarySumOlapImpl grid = new OrgSalarySumOlapImpl(delegator);
		
		//Set parameters for OlapImpl
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter("organization", organization);
		grid.putParameter("customTime", customTime);
		grid.putParameter("parentId", parentId);
		
		grid.setOlapResultType(OlapGrid.class);
		Map<String, Object> result = grid.execute(context);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getPayrollTableReportByCode(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		//Get locale and delegator
		Delegator delegator = ctx.getDelegator();
		
		//Get parameters
		Timestamp fromDate = (Timestamp)context.get("fromDate");
		Timestamp thruDate = (Timestamp)context.get("thruDate");
		String customTime = (String)context.get("customTime");
		List<String> codeList = (List<String>)context.get("codeList[]");
		//Get OlapImpl
		CodeSalarySumOlapImpl grid = new CodeSalarySumOlapImpl(delegator);
		
		//Set parameters for OlapImpl
		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter("codeList[]", codeList);
		grid.putParameter("customTime", customTime);
		
		grid.setOlapResultType(OlapGrid.class);
		Map<String, Object> result = grid.execute(context);
		return result;
	}
}
