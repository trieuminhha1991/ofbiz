package com.olbius.basehr.report.absent.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.report.absent.query.AbsentOlapImpl;
import com.olbius.basehr.report.absent.query.AbsentOlapImplChart;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.grid.OlapGrid;

public class AbsentService {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> absentEmplDetail(DispatchContext ctx, Map<String, ? extends Object> context)
			throws Exception {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		
		List<String> rootList = PartyUtil.getListOrgManagedByParty(delegator, userLoginId);
		
		AbsentOlapImpl absentOlapImpl = new AbsentOlapImpl();
		AbsentOlapImpl.getData getData = absentOlapImpl.new getData();
		OlapGrid olapGrid = new OlapGrid(absentOlapImpl, getData);
		absentOlapImpl.setOlapResult(olapGrid);

		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		List<String> employeeId = (List<String>) context.get("employeeId[]");
		
		absentOlapImpl.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		absentOlapImpl.setFromDate(fromDate);
		absentOlapImpl.setThruDate(thruDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.EMPLOYEEID, employeeId);
		absentOlapImpl.putParameter(AbsentOlapImpl.FROMDATE, fromDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.THRUDATE, thruDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.ORG, rootList);

		Map<String,Object> result = absentOlapImpl.execute((Map<String, Object>) context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> absentTimeChart(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		
		List<String> rootList = PartyUtil.getListOrgManagedByParty(delegator, userLoginId);
		
		AbsentOlapImplChart absentOlapImpl = new AbsentOlapImplChart();
		AbsentOlapImplChart.absentTimeColumn absentTimeCol = absentOlapImpl.new absentTimeColumn();
		AbsentOlapImplChart.absentTimeColumnOut absentTimeColOut = absentOlapImpl.new absentTimeColumnOut(absentOlapImpl, absentTimeCol);
		
		absentOlapImpl.setOlapResult(absentTimeColOut);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		absentOlapImpl.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		absentOlapImpl.setFromDate(fromDate);
		absentOlapImpl.setThruDate(thruDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.FROMDATE, fromDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.THRUDATE, thruDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.ORG, rootList);
		
		Map<String, Object> result = absentOlapImpl.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> absentTimePieChart(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		
		List<String> rootList = PartyUtil.getListOrgManagedByParty(delegator, userLoginId);
		AbsentOlapImplChart absentOlapImpl = new AbsentOlapImplChart();
		AbsentOlapImplChart.absentTimePie absentOlapImplPie = absentOlapImpl.new absentTimePie();
		AbsentOlapImplChart.absentTimePieOut absentOlapImplPieOut = absentOlapImpl.new absentTimePieOut(absentOlapImpl, absentOlapImplPie);
		
		absentOlapImpl.setOlapResult(absentOlapImplPieOut);
		
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		absentOlapImpl.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		absentOlapImpl.setFromDate(fromDate);
		absentOlapImpl.setThruDate(thruDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.FROMDATE, fromDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.THRUDATE, thruDate);
		absentOlapImpl.putParameter(AbsentOlapImpl.ORG, rootList);
		
		Map<String, Object> result = absentOlapImpl.execute();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
