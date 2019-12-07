package com.olbius.bi.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.bi.olap.chart.OlapColumnChart;
import com.olbius.bi.olap.chart.OlapLineChart;
import com.olbius.olap.accounting.AccountingOlap;
import com.olbius.olap.accounting.AccountingOlapImpl;
import com.olbius.olap.accounting.AccountingOlapImplv2;

public class AccountingServices {
	
	public final static String module = AccountingServices.class.getName();
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateAcc(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		Boolean productFlag = (Boolean) context.get("productFlag");
		if(productFlag == null) {
			productFlag = false;
        }
		Boolean categoryFlag = (Boolean) context.get("categoryFlag");
		if(categoryFlag == null) {
			categoryFlag = false;
        }
		Boolean groupFlag = (Boolean) context.get("groupFlag");
		if(groupFlag == null) {
			productFlag = true;
        }
		String product = (String) context.get("product");
		String code = (String) context.get("code");
		String currency = (String) context.get("currency");		
		String debitCreditFlag = (String) context.get("debitCreditFlag");		
		Boolean orig = (Boolean) context.get("orig");		
        if(orig == null) {
        	orig = false;
        }
        Boolean level = (Boolean) context.get("level");		
        if(level == null) {
        	level = false;
        }
        Boolean distrib = (Boolean) context.get("distrib");		
        if(distrib == null) {
        	distrib = false;
        }
        Boolean dateFlag = (Boolean) context.get("dateFlag");		
        if(dateFlag == null) {
        	dateFlag = true;
        }
        String dateType= (String) context.get("dateType");
        if(dateType == null) {
        	dateType = "DAY";
        }
        String group= (String) context.get("group");
        List<String> groups = (List<String>) context.get("group[]");
        
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        Integer limit = (Integer) context.get("limit");
        if(limit == null) {
        	limit = 0;
        }
        Boolean sort = (Boolean) context.get("sort");		
        if(sort == null) {
        	sort = true;
        }
		
		AccountingOlap olap = OlapServiceFactory.ACCOUNTING.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        olap.setGroup(group);
		olap.setGroup(groups);
		((AccountingOlapImpl) olap).setLimit(limit);
        
        try {
            olap.evaluateAcc(dateFlag, productFlag, categoryFlag, groupFlag, product, code, currency, debitCreditFlag, dateType, orig, level, distrib, sort);
		} catch (GenericDataSourceException e) {
			throw new GenericServiceException(e);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		} catch (SQLException e) {
			throw new GenericServiceException(e);
		} finally {
			try {
				olap.close();
			} catch (GenericDataSourceException e) {
				throw new GenericServiceException(e);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> evaluateAccV2(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");

		Boolean productFlag = (Boolean) context.get("productFlag");
		if(productFlag == null) {
			productFlag = false;
        }
		Boolean categoryFlag = (Boolean) context.get("categoryFlag");
		if(categoryFlag == null) {
			categoryFlag = false;
        }
		Boolean groupFlag = (Boolean) context.get("groupFlag");
		if(groupFlag == null) {
			productFlag = true;
        }
		String product = (String) context.get("product");
		String code = (String) context.get("code");
		String currency = (String) context.get("currency");		
		String debitCreditFlag = (String) context.get("debitCreditFlag");		
		Boolean orig = (Boolean) context.get("orig");		
        if(orig == null) {
        	orig = false;
        }
        Boolean level = (Boolean) context.get("level");		
        if(level == null) {
        	level = false;
        }
        Boolean distrib = (Boolean) context.get("distrib");		
        if(distrib == null) {
        	distrib = false;
        }
        Boolean dateFlag = (Boolean) context.get("dateFlag");		
        if(dateFlag == null) {
        	dateFlag = true;
        }
        String dateType= (String) context.get("dateType");
        if(dateType == null) {
        	dateType = "DAY";
        }
        String group= (String) context.get("group");
        List<String> groups = (List<String>) context.get("group[]");
        
		Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        Long limit = (Long) context.get("limit");
        long ZeroValue = 0;
        if(UtilValidate.isEmpty(limit) || limit == null) {
        	limit = ZeroValue;
        }
        Boolean sort = (Boolean) context.get("sort");		
        if(sort == null) {
        	sort = true;
        }		
		
        AccountingOlapImplv2 olap = new AccountingOlapImplv2(delegator);
		if (dateFlag)
			olap.setOlapResultType(OlapLineChart.class);
		else olap.setOlapResultType(OlapColumnChart.class);
		
        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);
        olap.putParameter(AccountingOlapImplv2.CATEGORY_FLAG, categoryFlag);
        olap.putParameter(AccountingOlapImplv2.CODE, code);
        olap.putParameter(AccountingOlapImplv2.CURRENCY, currency);
        olap.putParameter(AccountingOlapImplv2.DATE_FLAG, dateFlag);
        olap.putParameter(AccountingOlapImplv2.DATE_TYPE, dateType);
        olap.putParameter(AccountingOlapImplv2.DEBIT_CREDIT_FLAG, debitCreditFlag);
        olap.putParameter(AccountingOlapImplv2.DISTRIB, distrib);
        olap.putParameter(AccountingOlapImplv2.GROUP, group);
        olap.putParameter(AccountingOlapImplv2.GROUP_FLAG, groupFlag);
        olap.putParameter(AccountingOlapImplv2.LEVEL, level);
        olap.putParameter(AccountingOlapImplv2.LIMIT, limit);
        olap.putParameter(AccountingOlapImplv2.ORIG, orig);
        olap.putParameter(AccountingOlapImplv2.PRODUCT, product);
        olap.putParameter(AccountingOlapImplv2.PRODUCT_FLAG, productFlag);
        olap.putParameter(AccountingOlapImplv2.SORT, sort);
        olap.putParameter(AccountingOlapImplv2.GROUPS, groups);
        
        
		Map<String, Object> result = olap.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
