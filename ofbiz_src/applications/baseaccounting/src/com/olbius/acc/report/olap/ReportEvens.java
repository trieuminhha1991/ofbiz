package com.olbius.acc.report.olap;

import com.olbius.acc.report.financialstm.FinancialReportBuilder;
import com.olbius.acc.report.financialstm.PeriodUtils;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.grid.OlapGrid;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

public class ReportEvens {
	public Map<String, Object> jqGetListImpExpStockWarehouseReportOlap(DispatchContext dctx,
			Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		Boolean init = (Boolean) context.get("init");
		String productId = (String) context.get("productId");
		String facilityId = (String) context.get("facilityId");

		ImpExpStockWarehouseOlapImp grid = new ImpExpStockWarehouseOlapImp(delegator);
		grid.setOlapResultType(OlapGrid.class);

		grid.setFromDate(fromDate);
		grid.setThruDate(thruDate);
		grid.putParameter("PRODUCT_ID", productId);
		grid.putParameter("FACILITY_ID", facilityId);

		if (init != null && (Boolean) init) {
			grid.putParameter(OlapInterface.INIT, init);
		}

		Map<String, Object> result = grid.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static String getDataFinancialStatement(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		String organizationPartyId = request.getParameter("organizationPartyId");
		String reportTypeId = request.getParameter("reportTypeId");
		String flag = request.getParameter("flag");
		Map<String, Object> parameters = FastMap.newInstance();
		parameters.put("customTimePeriodId", customTimePeriodId);
		parameters.put("organizationPartyId", organizationPartyId);
		parameters.put("reportTypeId", reportTypeId);
		parameters.put("flag", flag);
		
		FinancialReportBuilder builder = new FinancialReportBuilder();
		JSONArray jsonArray = builder.convertToJsonArray(parameters, delegator);
		String periodName = PeriodUtils.getPeriodName(customTimePeriodId, delegator);
		String previousPeriodName = PeriodUtils.getPreviousPeriodName(customTimePeriodId, delegator);
		String jsonData = jsonArray.toString();

		request.setAttribute("listData", jsonData);
		request.setAttribute("periodName", periodName);
		request.setAttribute("previousPeriodName", previousPeriodName);

		return "success";
	}
	
	public static String getStateCustomTimePeriod(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		String customTimePeriodId = request.getParameter("customTimePeriodId");
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		String isClosed = "N";
		if (UtilValidate.isNotEmpty(customTimePeriod)) {
			isClosed = customTimePeriod.getString("isClosed");
		}

		request.setAttribute("isClosed", isClosed);

		return "success";
	}
}