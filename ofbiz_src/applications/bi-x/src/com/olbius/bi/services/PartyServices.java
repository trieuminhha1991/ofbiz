package com.olbius.bi.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.olap.party.HROlap;
import com.olbius.olap.party.HROlapImpl;
import com.olbius.olap.party.PartyOlap;

public class PartyServices {
	
	public final static String module = PartyServices.class.getName();
	
	public static Map<String, Object> personBirth(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Boolean gender = (Boolean) context.get("gender");
		
		if(gender == null) {
			gender = false;
		}
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		((HROlapImpl) olap).setLocale((Locale) context.get("locale"));
		
		try {
			olap.setGroup(group);
			olap.personBirth(gender);
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
	
	public static Map<String, Object> gender(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		((HROlapImpl) olap).setLocale((Locale) context.get("locale"));
		
		try {
			olap.setGroup(group);
			olap.gender();
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
	
	public static Map<String, Object> member(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		List<?> groups = (List<?>) context.get("group[]");
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String dateType = (String) context.get("dateType");
		
		Boolean child = (Boolean) context.get("child");
		
		Boolean cur = (Boolean) context.get("cur");
		
		if(child == null) {
			child = false;
		}
		
		if(cur == null) {
			cur = false;
		}
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			olap.member(dateType, cur, groups);
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
	
	public static Map<String, Object> personOlap(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String dateType = (String) context.get("dateType");
		
		Boolean ft = (Boolean) context.get("ft");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			olap.personOlap(dateType, ft);
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
	
	public static Map<String, Object> school(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		String type = (String) context.get("type");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		((HROlapImpl) olap).setLocale((Locale) context.get("locale"));
		
		try {
			olap.setGroup(group);
			olap.school(type);
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
	
	public static Map<String, Object> position(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		String type = (String) context.get("type");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		try {
			olap.setGroup(group);
			olap.position(type);;
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
	
	public static Map<String, Object> getAllPartyParent(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		List<GenericValue> values = null;
		
		Set<String> _set = new TreeSet<String>();
		
		Set<String> __set = new TreeSet<String>();
		
		try {
			values = delegator.findList("PartyRelationship", EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"), null, UtilMisc.toList("partyIdTo"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		for(GenericValue value : values) {
			_set.add(value.getString("partyIdFrom"));
			__set.add(value.getString("partyIdTo"));
		}
		
		Set<String> _pr = new TreeSet<String>();
		
		for(String s : _set) {
			if(!__set.contains(s)) {
				_pr.add(s);
			}
		}
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> parent = new ArrayList<String>();
		parent.addAll(_pr);
		result.put("parent", parent);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;

	}
	
	public static Map<String, Object> getChildParty(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		List<GenericValue> values = null;
		
		String parent = (String) context.get("parent");
		
		Set<String> _set = new TreeSet<String>();
		
		List<EntityCondition> conditions = new ArrayList<EntityCondition>();
		
		conditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
		conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, parent));
		
		try {
			values = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("partyIdTo"), null, false);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e);
		}
		
		for(GenericValue value : values) {
			_set.add(value.getString("partyIdTo"));
		}
		
		Map<String, Object> result = FastMap.newInstance();
		List<String> child = new ArrayList<String>();
		child.addAll(_set);
		result.put("child", child);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;

	}
	
	public static Map<String, Object> timeTracker(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String timeId = (String) context.get("timeId");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			((HROlap) olap).timeTracker(timeId);
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
	
	public static Map<String, Object> onTime(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		
		Delegator delegator = ctx.getDelegator();
		
		String group = (String) context.get("group");
		
		Date fromDate = (Date) context.get("fromDate");
		
		Date thruDate = (Date) context.get("thruDate");
		
		String timeId = (String) context.get("timeId");
		
		PartyOlap olap = OlapServiceFactory.PARTY.newInstance();
		
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		olap.setFromDate(fromDate);
		
		olap.setThruDate(thruDate);
		
		try {
			olap.setGroup(group);
			((HROlap) olap).onTime(timeId);
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
