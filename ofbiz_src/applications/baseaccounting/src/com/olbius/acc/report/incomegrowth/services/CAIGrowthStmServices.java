package com.olbius.acc.report.incomegrowth.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.acc.report.incomegrowth.DataChartAdapter;
import com.olbius.acc.report.incomegrowth.ProductChartAdapter;
import com.olbius.acc.report.incomegrowth.entity.CategoryIncomeGrowth;
import com.olbius.acc.report.incomegrowth.entity.IncomeGrowth;
import com.olbius.acc.utils.EntityUtils;

public class CAIGrowthStmServices implements ReportServiceInterface{
	
	public static final String module = CAIGrowthStmServices.class.getName();
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getData(DispatchContext dctx,Map<String, Object> context) {
		//Get parameters
		LocalDispatcher dispatcher = dctx.getDispatcher();
		
		List<IncomeGrowth> listIncomeGrowth = FastList.newInstance();
		List<String> dataFields = new ArrayList<String>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		
		try {
			Map<String, Object> getIncomeStmCtx = FastMap.newInstance();
			getIncomeStmCtx.put("fromDate", context.get("fromDate1"));
			getIncomeStmCtx.put("thruDate", context.get("thruDate1"));
			getIncomeStmCtx.put("userLogin", context.get("userLogin"));
			getIncomeStmCtx.put("dateType", context.get("dateType"));
			getIncomeStmCtx.put("categoryId", context.get("categoryId"));
			getIncomeStmCtx.put("limit", context.get("limit"));
			getIncomeStmCtx.put("offset", context.get("offset"));
			getIncomeStmCtx.put("init", context.get("init"));
			getIncomeStmCtx.put("hasTransTime", "N");
			Map<String, Object> getCatIncomeStm1Rs = dispatcher.runSync("getCategoryIncomeStm", getIncomeStmCtx);
			List<Map<String, Object>> data1 = (List<Map<String, Object>>)getCatIncomeStm1Rs.get("data");
			
			getIncomeStmCtx.clear();
			getIncomeStmCtx.put("fromDate", context.get("fromDate2"));
			getIncomeStmCtx.put("thruDate", context.get("thruDate2"));
			getIncomeStmCtx.put("userLogin", context.get("userLogin"));
			getIncomeStmCtx.put("dateType", context.get("dateType"));
			getIncomeStmCtx.put("categoryId", context.get("categoryId"));
			getIncomeStmCtx.put("limit", context.get("limit"));
			getIncomeStmCtx.put("offset", context.get("offset"));
			getIncomeStmCtx.put("init", context.get("init"));
			getIncomeStmCtx.put("hasTransTime", "N");
			Map<String, Object> getCatIncomeStm2Rs = dispatcher.runSync("getCategoryIncomeStm", getIncomeStmCtx);
			List<Map<String, Object>> data2 = (List<Map<String, Object>>)getCatIncomeStm2Rs.get("data");
			
			for(Map<String, Object> item1 : data1) {
				for(Map<String, Object> item2 : data2) {
					if(((String)item1.get("categoryId")).equals(((String)item2.get("categoryId")))) {
						CategoryIncomeGrowth income = new CategoryIncomeGrowth();
						income.setGrossProfit1((BigDecimal)item1.get("grossProfit"));
						income.setSaleIncome1((BigDecimal)item1.get("saleIncome"));
						income.setGrossProfit2((BigDecimal)item2.get("grossProfit"));
						income.setSaleIncome2((BigDecimal)item2.get("saleIncome"));
						income.setCategoryId((String)item1.get("categoryId"));
						listIncomeGrowth.add(income);
					}
				}
			}
			
			for(IncomeGrowth item: listIncomeGrowth) {
				data.add(EntityUtils.convertEntityToMap(item));
			}
		} catch (Exception e) {
			e.printStackTrace();
			org.ofbiz.base.util.Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("data", data);
		result.put("totalsize", data.size());
		dataFields.add("categoryId");
		dataFields.add("saleIncome2");
		dataFields.add("grossProfit2");
		dataFields.add("saleIncome1");
		dataFields.add("grossProfit1");
		dataFields.add("grossProfitRate");
		dataFields.add("saleIncomeRate");
		result.put("datafields", dataFields);
        return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> evaluateCAIGColumnChart(DispatchContext dctx, Map<String, Object> context) {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		
		//Variables
		List<String> xAxis = new ArrayList<String>();
		Map<String, Object> yAxis = new HashMap<String, Object>();
		//Get parameters
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		TimeZone timeZone = (TimeZone) context.get("timeZone");
		Map<String, Object> mapCtx = new HashMap<String, Object>();
		Map<String, Object> mapRs = new HashMap<String, Object>();
		try {
			mapCtx = ServiceUtil.setServiceFields(dispatcher, "getCatIncomeGrowthStm", context, userLogin, timeZone, locale);
			mapRs = dispatcher.runSync("getCatIncomeGrowthStm", mapCtx);
		} catch (GeneralServiceException | GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Map<String, Object>> data = (List<Map<String,Object>>)mapRs.get("data");
		DataChartAdapter adapter = new ProductChartAdapter();
		adapter.convertData(yAxis, xAxis, UtilMisc.toList("grossProfitRate", "saleIncomeRate"), "categoryId", data);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("xAxis", xAxis);
		result.put("yAxis", yAxis);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	@Override
	public void exportToExcel(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportToPdf(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}
	
}
