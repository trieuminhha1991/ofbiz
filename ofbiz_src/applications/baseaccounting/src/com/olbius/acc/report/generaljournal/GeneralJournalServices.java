package com.olbius.acc.report.generaljournal;

import com.olbius.acc.report.ReportServiceInterface;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class GeneralJournalServices implements ReportServiceInterface {
	
	public final static String module = GeneralJournalServices.class.getName();
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getData(DispatchContext dctx, Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listJournal = new ArrayList<Map<String, Object>>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	int total_size = 0;
    	EntityListIterator acctgTranIterator = null;
    	try {
			List<String> listSortFields = new ArrayList<String>();
			listSortFields.add("-transactionDate");
			listSortFields.add("-acctgTransId");
    		if (organizationPartyId!= null) {
        		EntityCondition organizationPartyCon = EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId);
        		listAllConditions.add(organizationPartyCon);
        	} 	    	
			acctgTranIterator = delegator.find("AcctgTransFactRecip", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null,listSortFields, opts);
			int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
			int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
			int start = pagenum*pagesize;
			total_size = acctgTranIterator.getResultsTotalSize();
			if(total_size - start < 0) {
				start = total_size;
			}
			List<GenericValue> listAcctgTrans = acctgTranIterator.getPartialList(start, pagesize);
			for(GenericValue item: listAcctgTrans) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("transactionDate", item.getString("transactionDate"));
				data.put("glAccountCode", item.getString("glAccountCode"));
				data.put("recipGlAccountCode", item.getString("recipGlAccountCode"));
				if(item.getString("debitCreditFlag").equals("D")) {
					data.put("crAmount", BigDecimal.ZERO);
					data.put("drAmount", item.getBigDecimal("amount").abs());
				} else {
					data.put("crAmount", item.getBigDecimal("amount").abs());
					data.put("drAmount", BigDecimal.ZERO);
				}
				listJournal.add(data);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage(), module);
			return ServiceUtil.returnError(e.getMessage());
		} finally {
			try {
				if(acctgTranIterator != null) {
					acctgTranIterator.close();
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", listJournal);
		result.put("TotalRows", total_size + "");
		return result;
	}

	
	@Override
	public void exportToExcel(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
	}

	@Override
	public void exportToPdf(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
	}
	
	@SuppressWarnings("unchecked")
	public static String getTotalGeneralJournal(HttpServletRequest request, HttpServletResponse response) throws GeneralServiceException, GenericServiceException {
		Locale locale = UtilHttp.getLocale(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> context = FastMap.newInstance();
		Map<String, String[]> parameters = request.getParameterMap();
		
		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			String key = entry.getKey();
			Object value = null;
			String[] valueMap = entry.getValue();
			if (valueMap != null && valueMap.length > 0) {
				if (valueMap.length == 1) {
					value = valueMap[0];
				} else {
					key += "[]";
					value = valueMap;
				}
			}
			context.put(key, value);
		}
		String fromDateStr = ExportExcelUtil.getParameter(parameters, "fromDate");
		String thruDateStr = ExportExcelUtil.getParameter(parameters, "thruDate");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		if (fromDateStr != null) {
			fromDateStr += " 00:00:00.000";
			fromDate = Timestamp.valueOf(fromDateStr);
		}
		if (thruDateStr != null) {
			thruDateStr += " 23:59:59.999";
			thruDate = Timestamp.valueOf(thruDateStr);
		}
		context.put("fromDate", fromDate);
		context.put("thruDate", thruDate);
		context.put("olapType", "GRID");
		context.put("userLogin", userLogin);
		context.put("locale", locale);
		
		Map<String, Object> contextCtx = ServiceUtil.setServiceFields(dispatcher, "getGeneralJournalNoShort", context, userLogin, null, locale);
		Map<String, Object> resultService = dispatcher.runSync("getGeneralJournalNoShort", contextCtx);
		if (ServiceUtil.isError(resultService)) {
			Debug.logError(ServiceUtil.getErrorMessage(resultService), module);
			return ExportExcelEvents.RESULT_ERROR;
		}
		
		List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");

		BigDecimal totalCr = BigDecimal.ZERO;
		BigDecimal totalDr = BigDecimal.ZERO;
		
		for (Map<String, Object> map : listData) {
			totalCr = totalCr.add((BigDecimal) map.get("crAmount"));
			totalDr = totalDr.add((BigDecimal) map.get("drAmount"));
		}
		
		request.setAttribute("totalCr", totalCr);
		request.setAttribute("totalDr", totalDr);
		return "success";
	}
	
	protected static Timestamp getSqlFromDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}

	protected static Timestamp getSqlThruDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}
}