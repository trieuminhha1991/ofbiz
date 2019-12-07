package com.olbius.bi.services;

import com.olbius.olap.party.HROlap;
import com.olbius.olap.party.PartyOlap;
import javolution.util.FastMap;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class HRServices {
	
	public final static String module = HRServices.class.getName();
	
	public static Map<String, Object> payrollSalaryStructure(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String party_person= (String) context.get("party_person");
		String party_group= (String) context.get("party_group");		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        olap.setFromDate(fromDate);
        olap.setThruDate(thruDate);

        try {
			olap.setGroup(party_group);
            ((HROlap) olap).salaryStructure(party_person);
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
	
	public static Map<String, Object> payrollSalaryRange(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			((HROlap) olap).salaryRange();
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


	public static Map<String, Object> payrollSalaryRangeByPosition(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {

		Delegator delegator = ctx.getDelegator();

		String group = (String) context.get("group");
		String dateType = (String) context.get("dateType");
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");

		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();

		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);

		try {
			olap.setGroup(group);
			((HROlap) olap).salaryRangeByPosition(dateType);
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
}
