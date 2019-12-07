package org.ofbiz.mobileservices;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.DateUtil;
import com.olbius.basehr.util.PartyUtil;

public class SupMobileServices{
	/**
	 * This services of Sales Sup use for mobile apps
	 *
	 * */
	public static final String module = SupMobileServices.class.getName();
	
	public static final String resource = "BaseSalesUiLabels";
	
	/**
	 * This method allow get route list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListRouteBySup(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dc.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opt = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		int pagenum = Integer.parseInt( context.containsKey("pagenum") ? (String) context.get("pagenum") : "0");
		int pagesize = Integer.parseInt(context.containsKey("pagesize") ? (String) context.get("pagesize") : "20");
		String filter = context.containsKey("filter") ? (String) context.get("filter") : null;
		String supId = (String) context.get("supId");
		String options = context.containsKey("options") ? (String) context.get("options") : null;
		Map<String,Object> result = FastMap.newInstance();
		
		try {
			parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
			parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
			
			if(filter != null)
			{
/*				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("groupName",EntityJoinOperator.LIKE,"%" + filter + "%"),
						EntityCondition.makeCondition("partyCode",EntityJoinOperator.LIKE,"%" + filter + "%")),EntityJoinOperator.OR));*/
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("routeName",EntityJoinOperator.LIKE,"%" + filter + "%"),
						EntityCondition.makeCondition("routeCode",EntityJoinOperator.LIKE,"%" + filter + "%")),EntityJoinOperator.OR));

			}
			
			
			if(options != null && !options.equals("undefined")){
				
					opt.setDistinct(true);
					
					opt.setResultSetType(ResultSet.TYPE_SCROLL_SENSITIVE);
					
					List<String> listFilter = FastList.newInstance();
					
					JSONArray js = null;
					
					try {
						 js = JSONArray.fromObject(options);
					} catch (Exception e) {
						Debug.log("error when parse object to jsonArray");
					}
					if(js != null)
						for(Object j : js)
							listFilter.add(new SupMobileServices().new miniUtils().getDayRoute(Integer.parseInt(j.toString())));
					
					listAllConditions = new SupMobileServices().new miniUtils().buildConditionFindRoute(delegator, listAllConditions, opt);
					/*listAllConditions.add(EntityCondition.makeCondition("createdByUserLogin", userLogin.get("userLoginId")));
					listAllConditions.add(EntityCondition.makeCondition("statusId", "PARTY_ENABLED"));
					listAllConditions.add(EntityCondition.makeCondition("partyTypeId", "SALES_ROUTE"));*/
					listAllConditions.add(EntityCondition.makeCondition("managerId", userLogin.getString("partyId")));
					listAllConditions.add(EntityCondition.makeCondition("statusId", "ROUTE_ENABLED"));

					if(!listFilter.isEmpty())
						listAllConditions.add(EntityCondition.makeCondition("scheduleRoute",EntityOperator.IN,listFilter));
					
					EntityCondition cond = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
					/*EntityListIterator iterator = delegator.find("RouteInformationByDay", cond,
																null, null, UtilMisc.toList("partyId"), opt);
					if(iterator != null)
					{
						List<GenericValue> iteratorList = iterator.getCompleteList();
						List<Map<String,Object>> rsList = FastList.newInstance();*/
				try {
					List<GenericValue> iteratorList = delegator.findList("RouteInformationByDay", cond,
							null, UtilMisc.toList("routeId"), opt, false);
					if (UtilValidate.isNotEmpty(iteratorList)) {
						List<Map<String, Object>> rsList = FastList.newInstance();
						for (GenericValue i : iteratorList) {
							Map<String, Object> o = FastMap.newInstance();
							o.putAll(i);
							List<GenericValue> employee = delegator.findList("PartyRelationShipAndPerson",
									EntityCondition.makeCondition(UtilMisc.toList(
											EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"),
											EntityCondition.makeCondition("roleTypeIdTo", "ROUTE"),
											/*EntityCondition.makeCondition("partyIdTo", i.getString("partyId")),*/
											EntityCondition.makeCondition("partyIdTo", i.getString("routeId")),
											EntityUtil.getFilterByDateExpr())),
									UtilMisc.toSet("partyCode", "fromDate"), UtilMisc.toList("-fromDate"), opt, false);
							o.put("employeeId", employee);
							rsList.add(o);
						}

						result.put("listIterator", rsList.isEmpty() ? FastList.newInstance() : rsList);
						result.put("TotalRows", String.valueOf(rsList.size()));
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}else{
				
				Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
						"JQGetListRoute",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
				inputServices.put("userLogin", userLogin);
				 result = dc.getDispatcher().runSync("JQGetListRoute", inputServices);
				 
				 if(ServiceUtil.isError(result))
					 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
							 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));
			}
			
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	/**
	 * This method allow get saler list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListSalerBySup(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opt = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		int pagenum = Integer.parseInt( context.containsKey("pagenum") ? (String) context.get("pagenum") : "0");
		int pagesize = Integer.parseInt(context.containsKey("pagesize") ? (String) context.get("pagesize") : "20");
		String filter = context.containsKey("filter") ? (String) context.get("filter") : null;
		Map<String,Object> result = FastMap.newInstance();
		String supId = (String) context.get("supId");
		
		try {
			parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
			parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
			
			if(filter != null)
			{
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("fullName",EntityJoinOperator.LIKE,"%" + filter + "%"),
						EntityCondition.makeCondition("partyCode",EntityJoinOperator.LIKE,"%" + filter + "%")),EntityJoinOperator.OR));
			}
			
			Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
					"JQGetListSalesman",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
			inputServices.put("userLogin", userLogin);
			result = dc.getDispatcher().runSync("JQGetListSalesman", inputServices);
			
			 if(ServiceUtil.isError(result))
				 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
						 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));
			 else{
				 List<?> listEmpl = null;
				 try {
					 
					listEmpl = (List<Map<String,Object>>) result.get("listIterator");
				} catch (ClassCastException e) {
					try {
						listEmpl = (List<GenericValue>) result.get("listIterator");
					} catch (ClassCastException e2) {
						Debug.log("---------CANNOT CAST LIST ITERATOR-------");
					}
				}
				 
				 if(listEmpl != null)
				 {
					 List<String> partyIds = FastList.newInstance();
					 for(Object o  : listEmpl)
					 {
						 if(o instanceof java.util.Map)
						 {
							 Map<String,Object> m = (Map<String,Object>) o;
							 String id = (String) (m.containsKey("partyId") ? m.get("partyId") : m.get("partyCode"));
							 if(UtilValidate.isEmpty(id))
								 continue;
							 
							 partyIds.add(id);
							 
						 }else if(o instanceof org.ofbiz.entity.GenericValue)
						 {
							 
						 }
					 }
					 
					 
					 if(!partyIds.isEmpty())
					 {
						 listAllConditions.clear();
						 parameters.clear();
						 
						 List<GenericValue> routeList = FastList.newInstance();
						 for(String p : partyIds)
						 {
							 parameters.clear();
							 listAllConditions.clear();
							 parameters.put("distinct", new String[]{"Y"});
							 parameters.put("partyId", new String[]{p});
							 inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
										"JQGetListRoutes",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
								inputServices.put("userLogin", userLogin);
								Map<String,Object> mapTpx = dc.getDispatcher().runSync("JQGetListRoutes", inputServices);
								if(ServiceUtil.isSuccess(mapTpx) && mapTpx.containsKey("listIterator"))
								{
									EntityListIterator ei = (EntityListIterator) mapTpx.get("listIterator");
									if(ei.getResultsTotalSize() > 10)
										routeList.addAll(ei.getPartialList(0, 10));
									else
										routeList.addAll(ei.getCompleteList());
									
									try {
										if(ei != null)
											ei.close();
									} catch (Exception e) {
										Debug.logError(e, module);
									}
								}
						 }
						 if(!routeList.isEmpty())
							 result.put("routeList", routeList);
					 }
				 }
			 }
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * This method allow get exhibiton/accumulate list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListExhAcc(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opt = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		int pagenum = Integer.parseInt( context.containsKey("pagenum") ? (String) context.get("pagenum") : "0");
		int pagesize = Integer.parseInt(context.containsKey("pagesize") ? (String) context.get("pagesize") : "20");
		Map<String,Object> result = FastMap.newInstance();
		String supId = (String) context.get("supId");
		List<GenericValue> listIterator = FastList.newInstance();
		EntityListIterator iteratorList = null;
		try {
			parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
			parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
			
			Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
					"JQListProductPromoExt",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
			inputServices.put("userLogin", userLogin);
			 result = dc.getDispatcher().runSync("JQListProductPromoExt", inputServices);
			 
			 if(ServiceUtil.isSuccess(result))
			 {
				  iteratorList = (EntityListIterator) result.get("listIterator");
				  
				  int size = iteratorList.getResultsTotalSize();
				  
				  if(size > 0)
				  {
					  listIterator.addAll(iteratorList.getCompleteList());
					  result.put("listIterator", listIterator);
					  result.put("TotalRows", String.valueOf(size));
				  }
			 }else
			 {
				 result.put("listIterator", listIterator);
				 result.put("TotalRows", String.valueOf(listIterator.size()));
			 }
			 
			 if(ServiceUtil.isError(result))
				 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
						 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}finally{
			try {
				if(iteratorList != null)
				{
					iteratorList.close();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * This method allow getListCheckInventoryAgent list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getListCheckInventoryAgent(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opt = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		int pagenum = Integer.parseInt( context.containsKey("pagenum") ? (String) context.get("pagenum") : "0");
		int pagesize = Integer.parseInt(context.containsKey("pagesize") ? (String) context.get("pagesize") : "20");
		Map<String,Object> result = FastMap.newInstance();
		String supId = (String) context.get("supId");
		List<GenericValue> listIterator = FastList.newInstance();
		EntityListIterator iteratorList = null;
		try {
			parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
			parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
			
			Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
					"JQGetListCheckInventoryAgents",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
			inputServices.put("userLogin", userLogin);
			
			 result = dc.getDispatcher().runSync("JQGetListCheckInventoryAgents", inputServices);
			 
			 if(ServiceUtil.isSuccess(result))
			 {
				  iteratorList = (EntityListIterator) result.get("listIterator");
				  
				  int size = iteratorList.getResultsTotalSize();
				  
				  if(size > 0)
				  {
					  listIterator.addAll(iteratorList.getCompleteList());
					  result.put("listIterator", listIterator);
					  result.put("TotalRows", String.valueOf(size));
				  }
			 }else
			 {
				 result.put("listIterator", listIterator);
				 result.put("TotalRows", String.valueOf(listIterator.size()));
			 }
			 
			 if(ServiceUtil.isError(result))
				 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
						 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}finally{
			try {
				if(iteratorList != null)
				{
					iteratorList.close();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * This method allow JQGetListInventoryOfAgents list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getInventoryList(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opt = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		int pagenum = Integer.parseInt( context.containsKey("pagenum") ? (String) context.get("pagenum") : "0");
		int pagesize = Integer.parseInt(context.containsKey("pagesize") ? (String) context.get("pagesize") : "20");
		Map<String,Object> result = FastMap.newInstance();
		String partyId = (String) context.get("storeid");
		List<GenericValue> listIterator = FastList.newInstance();
		EntityListIterator iteratorList = null;
		try {
			parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
			parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
			
			if(partyId != null)
				parameters.put("partyId", new String[]{String.valueOf(partyId)});
			
			Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
					"JQGetListInventoryOfAgents",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
			inputServices.put("userLogin", userLogin);
			
			 result = dc.getDispatcher().runSync("JQGetListInventoryOfAgents", inputServices);
			 
			 if(ServiceUtil.isSuccess(result))
			 {
				  iteratorList = (EntityListIterator) result.get("listIterator");
				  
				  int size = iteratorList.getResultsTotalSize();
				  
				  if(size > 0)
				  {
					  listIterator.addAll(iteratorList.getCompleteList());
					  result.put("listIterator", listIterator);
					  result.put("TotalRows", String.valueOf(size));
				  }
			 }else
			 {
				 result.put("listIterator", listIterator);
				 result.put("TotalRows", String.valueOf(listIterator.size()));
			 }
			 
			 if(ServiceUtil.isError(result))
				 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
						 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}finally{
			try {
				if(iteratorList != null)
				{
					iteratorList.close();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * This method allow get store list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getStoresList(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opts = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		String pagenum = context.containsKey("pagenum") ? (String) context.get("pagenum") : "0";
		String pagesize =  context.containsKey("pagesize") ? (String) context.get("pagesize") : "20";
		String filter =  context.containsKey("filter") ? (String) context.get("filter") : null;
		
		opts = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, false);
		
		Map<String,Object> result = FastMap.newInstance();
		String partyId = (String) context.get("partyId");
		List<GenericValue> listIterator = FastList.newInstance();
		EntityListIterator iteratorList = null;
		try {
			
			parameters.put("pagesize", new String[]{pagesize});
			parameters.put("pagenum", new String[]{pagenum});
			
			String serviceName = partyId != null ? "JQGetListStores" : "JQGetListAgents";
			if(partyId != null)
				parameters.put("partyId",new String[]{partyId});
			
			if(filter != null)
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("groupName",EntityJoinOperator.LIKE,"%" + filter + "%"),
						EntityCondition.makeCondition("partyCode",EntityJoinOperator.LIKE,"%" + filter + "%")),EntityJoinOperator.OR));
			
			Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
					serviceName,UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opts,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
			
			inputServices.put("userLogin", userLogin);
			
			result = dc.getDispatcher().runSync(serviceName, inputServices);
			
				 
			 if(ServiceUtil.isSuccess(result))
			 {
				  iteratorList = (EntityListIterator) result.get("listIterator");
				  
				  int size = iteratorList.getResultsTotalSize();
				  
				  if(size > 0)
				  {
					  if(pagesize != null && pagenum != null && filter == null)
						  listIterator.addAll(iteratorList.getPartialList(Integer.parseInt(pagesize)*Integer.parseInt(pagenum) + 1, Integer.parseInt(pagesize)));
					  else
						  listIterator.addAll(iteratorList.getCompleteList());
					  
					  result.put("listIterator", listIterator);
					  result.put("TotalRows", String.valueOf(size));
				  }
			 }
			 
			 if(ServiceUtil.isError(result))
				 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
						 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}finally{
				try {
					if(iteratorList != null)
					{
						iteratorList.close();
					}
					
					if(!result.containsKey("listIterator"))
						result.put("listIterator", listIterator);
					
					if(!result.containsKey("TotalRows"))
						result.put("TotalRows", "0");
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
		}
		
		return result;
	}
	
	
	/**
	 * This method allow get detail Exhibition/Accumulate list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	public static Map<String,Object> getDetailProgramExhAcc(DispatchContext dc,Map<String, ? extends Object> context){
		Delegator delegator = dc.getDelegator();
		Map<String,Object> content = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String productPromoId = (String) context.get("promoId");
		
		try {
			
			if (UtilValidate.isNotEmpty(productPromoId)) {
				content.put("productPromoId", productPromoId);
				GenericValue productPromo = delegator.findOne("ProductPromoExt", UtilMisc.toMap("productPromoId", productPromoId), false);
				if (UtilValidate.isNotEmpty(productPromo)) {
					 content.put("productPromo", productPromo);
				}
				
				List<EntityCondition> listCond = new ArrayList<EntityCondition>();
				listCond.add(EntityCondition.makeCondition("productPromoId", productPromoId));
				List<EntityCondition> listCondOr = new ArrayList<EntityCondition>();
				listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp()));
				listCondOr.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
				listCond.add(EntityCondition.makeCondition(listCondOr, EntityOperator.OR));
				
				List<GenericValue> productStorePromoAppl = delegator.findList("ProductStorePromoExtAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("sequenceNum", "productPromoId"), null, false);
				List<GenericValue> promoRoleTypeApply = delegator.findList("ProductPromoExtRoleTypeAppl", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
				
				List<GenericValue> productPromoRules = delegator.findByAnd("ProductPromoExtRule", UtilMisc.toMap("productPromoId",productPromoId), UtilMisc.toList("ruleName"), false);
				List<GenericValue> promoProductPromoCategories = delegator.findByAnd("ProductPromoExtCategory",
						UtilMisc.toMap("productPromoId", productPromoId,"productPromoRuleId","_NA_","productPromoActionSeqId","_NA_","productPromoCondSeqId","_NA_"), null, false);
					
				List<GenericValue> promoProductPromoProducts = delegator.findByAnd("ProductPromoExtProduct",
							UtilMisc.toMap("productPromoId",productPromoId,"productPromoRuleId","_NA_","productPromoActionSeqId","_NA","productPromoCondSeqId","_NA_"), null, false);
					
				List<GenericValue> productStores = delegator.findByAnd("ProductStore", null, null, false);
				List<GenericValue> roleTypes = delegator.findByAnd("RoleType", null, null, false);
				
				if (UtilValidate.isNotEmpty(productStorePromoAppl)) {
					 content.put("productStorePromoAppl", productStorePromoAppl);
				}
				
				if (UtilValidate.isNotEmpty(promoRoleTypeApply)) {
					 content.put("promoRoleTypeApply", promoRoleTypeApply);
				}

				if (UtilValidate.isNotEmpty(productPromoRules)) {
					 content.put("productPromoRules", productPromoRules);
					 
					 List<Map<String,Object>> other = FastList.newInstance();
					 Map<String,Object> mapsRuleDetail = FastMap.newInstance();
					 for(GenericValue rule : productPromoRules)
					 {
						 List<GenericValue> productPromoConds = FastList.newInstance();
						 List<GenericValue> productPromoActions = FastList.newInstance();
						 productPromoConds = rule.getRelated("ProductPromoExtCond", null,UtilMisc.toList("productPromoCondSeqId"), false);
						 productPromoActions = rule.getRelated("ProductPromoExtAction", null, UtilMisc.toList("productPromoActionSeqId"), false);
						 int condSize = productPromoConds.size();
						 int actSize = productPromoActions.size();
						 int _size = condSize > actSize ?  condSize : actSize;
						 int j = 0;
						 for(int i = 0 ;i < _size;i++)
						 {
							 if(i <= condSize)
							 {
								 GenericValue productPromoCond = productPromoConds.get(i);
								 //get categories related
								 List<GenericValue> condProductPromoCategories =  productPromoCond.getRelated("ProductPromoExtCategory", null, null, false);
								 if(UtilValidate.isNotEmpty(condProductPromoCategories))
								 {
									 Map<String,Object> _temp = FastMap.newInstance();
									 for(GenericValue condProductPromoCategory : condProductPromoCategories){
										 GenericValue condProductCategory = condProductPromoCategory.getRelatedOne("ProductCategory", true);
										 GenericValue condApplEnumeration = condProductPromoCategory.getRelatedOne("ApplEnumeration", true);
										 _temp.put(String.valueOf(j++), UtilMisc.toMap("productCateConds" + String.valueOf(j), condProductCategory,"applEnumConds" + String.valueOf(j),condApplEnumeration));
									 }
									 other.add(_temp);
								 }
								 
								 //get products related promo
								   List<GenericValue> condProductPromoProducts = productPromoCond.getRelated("ProductPromoExtProduct", null, null, false);
								   if(UtilValidate.isNotEmpty(condProductPromoProducts))
								   {
									   String productsCondList = "";
									   for(GenericValue condProductPromoProduct : condProductPromoProducts){
										   GenericValue condProduct =  condProductPromoProduct.getRelatedOne("Product", true);
										   productsCondList += condProduct.getString("internalName") != null ? condProduct.getString("internalName") : "";
										   productsCondList += "\n";
										   productsCondList += condProduct.getString("productCode") != null ? condProduct.getString("productCode") : condProduct.getString("productId");
									   }
									   mapsRuleDetail.put("productsCondsList_" + rule.getString("productPromoRuleId"), productsCondList);
								   }
                                 
								   
								//get conditions name 
								 String inputParamEnumId = productPromoCond.getString("inputParamEnumId");
								 String operatorEnumId = productPromoCond.getString("operatorEnumId");
								 String condValue = productPromoCond.getString("condValue");
								 condValue = condValue != null ? condValue : "";
								 String firstConds = "",middleConds = "";
								 if(inputParamEnumId != null)
								 {
									 GenericValue inputParam = productPromoCond.getRelatedOne("InputParamEnumeration", true);
									 firstConds = (String) (UtilValidate.isNotEmpty(inputParam) ? inputParam.get("description", locale) : inputParamEnumId);
								 }
								 
								 if(operatorEnumId != null)
								 {
									 GenericValue operatorEnum = productPromoCond.getRelatedOne("OperatorEnumeration", true);
									 middleConds =  (String) (UtilValidate.isNotEmpty(operatorEnum) ? operatorEnum.get("description", locale) : operatorEnumId);
								 }
								 
								 mapsRuleDetail.put("condsName_" +  rule.getString("productPromoRuleId"), (firstConds + " " + middleConds + " " + condValue));
								 
								 
							 }
							
							 
							 if(i <= actSize)
							 {
								 //get actions name and detail
								 GenericValue productPromoAct = productPromoActions.get(i);
								 List<GenericValue> actProductPromoCategories =  productPromoAct.getRelated("ProductPromoExtCategory", null, null, false);
								 if(UtilValidate.isNotEmpty(actProductPromoCategories))
								 {
									 Map<String,Object> _temp = FastMap.newInstance();
									 for(GenericValue actProductPromoCategory : actProductPromoCategories){
										 GenericValue actProductCategory = actProductPromoCategory.getRelatedOne("ProductCategory", true);
										 GenericValue actApplEnumeration = actProductPromoCategory.getRelatedOne("ApplEnumeration", true);
										 _temp.put(String.valueOf(j++), UtilMisc.toMap("productCateAct" + String.valueOf(j), actProductCategory,"ApplEnumAct" + String.valueOf(j),actApplEnumeration));
									 }
									 other.add(_temp);
								 }
								 
								   //get products actions related promotions
								   List<GenericValue> actionProductPromoProducts = productPromoAct.getRelated("ProductPromoExtProduct", null, null, false);
								   if(UtilValidate.isNotEmpty(actionProductPromoProducts))
									 {
									   String productsActList = "";
									   for(GenericValue actionProductPromoProduct : actionProductPromoProducts){
										   GenericValue condProduct =  actionProductPromoProduct.getRelatedOne("Product", true);
										   productsActList += condProduct.getString("internalName") != null ? condProduct.getString("internalName") : "";
										   productsActList += "\n";
										   productsActList += condProduct.getString("productCode") != null ? condProduct.getString("productCode") : condProduct.getString("productId");
									   }
									   mapsRuleDetail.put("productActsList_" + rule.getString("productPromoRuleId"), productsActList);
									 }
								 
								//get conditions name 
								 String inputParamEnumId = productPromoAct.getString("productPromoActionEnumId");
								 String firstConds = "";
								 if(inputParamEnumId != null)
								 {
									 GenericValue inputParam = productPromoAct.getRelatedOne("ActionEnumeration", true);
									 firstConds = (String) (UtilValidate.isNotEmpty(inputParam) ? inputParam.get("description", locale) : inputParamEnumId);
								 }
								 
								 if(productPromoAct.getString("quantity") != null)
									 firstConds += " \n " + UtilProperties.getMessage(resource, "Quantity", locale) + productPromoAct.getString("quantity");
								 if(productPromoAct.getString("amount") != null)
									 firstConds += " \n " + UtilProperties.getMessage(resource, "BSAmountOrPercent", locale) + " " + productPromoAct.getString("amount");
								 if(productPromoAct.getString("productId") != null)
									 firstConds += " \n " + UtilProperties.getMessage(resource, "BSProductId", locale) + " " +  productPromoAct.getString("productId");
								 if(productPromoAct.getString("partyId") != null)
									 firstConds += " \n " + UtilProperties.getMessage(resource, "BSPartyId", locale) + " " + productPromoAct.getString("partyId");
								 
								 if(productPromoAct.getString("operatorEnumId") != null)
								 {
									 GenericValue actionOperEnum = productPromoAct.getRelatedOne("OperatorEnumeration", true);
									 firstConds += " \n " + actionOperEnum.get("description",locale);
								 }
								 
								 if(productPromoAct.getString("isCheckInv") != null)
								 {
									 firstConds += " \n " + productPromoAct.getString("isCheckInv");
								 }
								 
								 mapsRuleDetail.put("actionsName_" +  rule.getString("productPromoRuleId"), firstConds);
								 
							 }
							
						 }
						 
						 mapsRuleDetail.put("conds_" + rule.getString("productPromoRuleId"), productPromoConds);
						 mapsRuleDetail.put("actions_" + rule.getString("productPromoRuleId"), productPromoActions);
						 mapsRuleDetail.put("other_" + rule.getString("productPromoRuleId"), other);
						 
						 
					 }
					 
					 if(UtilValidate.isNotEmpty(mapsRuleDetail))
						 content.put("mapRuleDetail", mapsRuleDetail);
				}
				
				if (UtilValidate.isNotEmpty(promoProductPromoCategories)) {
					 content.put("promoProductPromoCategories", promoProductPromoCategories);
				}
				
				if (UtilValidate.isNotEmpty(promoProductPromoProducts)) {
					 content.put("promoProductPromoProducts", promoProductPromoProducts);
				}
				

				if (UtilValidate.isNotEmpty(productStores)) {
					 content.put("productStores", productStores);
				}
				
				if (UtilValidate.isNotEmpty(roleTypes)) {
					 content.put("roleTypes", roleTypes);
				}
				
				
			}
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}
		
		return UtilMisc.<String,Object>toMap("content",content);
	}
	
	/**
	 * get list address by route id
	 * @param list information route need gets address
	 * @return List Map<?.?> all element adddress of route 
	 * 
	 * */
	public static Map<String,Object> getAddressRoute(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		String listroute = (String) (context.containsKey("routeId[]") ? context.get("routeId[]") : null);
		Map<String,Object> result = FastMap.newInstance();
		List<Map<String,List<Map<String,Object>>>> listAddressDetail = FastList.newInstance();
		try {
			if(listroute == null) 
				return ServiceUtil.returnError("---List route cant be missing!---");
			
			JSONArray jsonArr = null;
			
			List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
			List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
			EntityFindOptions opt = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
			Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
			
			try {
				jsonArr = JSONArray.fromObject(listroute);
			} catch (Exception e) {
				throw new IllegalAccessException("------- Cannot be parse string :" + listroute + "to JSON Array ---------");
			}
			
			if(jsonArr != null)
			{
				for(int i = 0;i < jsonArr.size();i++)
				{
					JSONObject job = (JSONObject) jsonArr.get(i);
					String objName = "route_" + i;
					parameters.clear();
					if(job.containsKey(objName))
						parameters.put("partyId", new String[]{job.getString(objName)});
					
					Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
							"JQGetRouteAddresses",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,
									"opts",opt,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
					
					inputServices.put("userLogin", userLogin);
					Map<String,Object> resultTpx = dc.getDispatcher().runSync("JQGetRouteAddresses", inputServices);
					
					if(ServiceUtil.isSuccess(resultTpx))
					{
						List<Map<String,Object>> listAddress = (List<Map<String, Object>>) (resultTpx.containsKey("listIterator") ? resultTpx.get("listIterator") : null);
					
						if(listAddress != null)
							listAddressDetail.add(UtilMisc.<String,List<Map<String,Object>>>toMap(objName, listAddress));
					}
					
				}
				result.put("listIterator", listAddressDetail);
				result.put("TotalRows", String.valueOf(listAddressDetail.size()));
			}
			
		} catch (Exception e) {
			String msg = "-----------Error in services cause : " + e.getMessage() + "---------------";
			Debug.logError(msg, module);
			return ServiceUtil.returnError(msg);
		}
		
		return result;
	}
	
	public static Map<String,Object> getInfoProfile(DispatchContext dpct,Map<String,Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> mapInfo = null;
		try {
			mapInfo = new SupMobileServices().new miniUtils().getInfoProfile(dpct.getDispatcher(), dpct.getDelegator(), locale, userLogin);
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		
		return UtilMisc.<String,Object>toMap("mapInfo", mapInfo != null ? mapInfo : FastMap.newInstance());
	}
	
	public static Map<String,Object> getListOrgManagedByParty(DispatchContext dpct,Map<String,Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dpct.getDelegator();
		List<String> result = null;
		try {
			
			result = PartyUtil.getListOrgManagedByParty(delegator, userLogin.getString("userLoginId"));
			
		} catch (Exception e) {
			Debug.logError(e, module);
		}
		
		return UtilMisc.<String,Object>toMap("resultList", result != null ? result : FastMap.newInstance());
	}
	
	
	/**
	 * This method allow get employee leave list belong to sup
	 * @param Map<?,?> context and DispatchContext dc
	 * 
	 * */
	@SuppressWarnings("unchecked")
	public static Map<String,Object> getEmplLeaveList(DispatchContext dc,Map<String, ? extends Object> context){
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dc.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) (context.containsKey("listAllConditions") ? (List<EntityCondition>) context.get("listAllConditions") : FastList.newInstance());
		List<String> listSortFields = (List<String>) (context.containsKey("listSortFields") ?(List<String>) context.get("listSortFields") : FastList.newInstance());
		EntityFindOptions opts = (EntityFindOptions) (context.containsKey("opts") ? (EntityFindOptions) context.get("opts") : new EntityFindOptions());
		Map<String,String[]> parameters = (Map<String,String[]>)(context.containsKey("parameters") ? (Map<String,String[]>) context.get("parameters") : new HashMap<String,String[]>());
		String pagenum = context.containsKey("pagenum") ? (String) context.get("pagenum") : "0";
		String pagesize =  context.containsKey("pagesize") ? (String) context.get("pagesize") : "20";
		String filter = context.containsKey("filter") ? (String) context.get("filter") : null;
		
		opts = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, false);
		
		Map<String,Object> result = FastMap.newInstance();
		String partyId = (String) context.get("partyId");
		String year = (String) context.get("year");
		List<Map<String,Object>> listIterator = FastList.newInstance();
		try {
			
			parameters.put("pagesize", new String[]{pagesize});
			parameters.put("pagenum", new String[]{pagenum});
			parameters.put("hasrequest", new String[]{"Y"});
			parameters.put("year", new String[]{year});
			
			if(partyId != null)
				parameters.put("partyId",new String[]{partyId});
			
			if(filter != null)
				listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("fullName",EntityJoinOperator.LIKE,"%" + filter + "%"),
						EntityCondition.makeCondition("partyCode",EntityJoinOperator.LIKE,"%" + filter + "%")),EntityJoinOperator.OR));
		
			Map<String,Object> inputServices = ServiceUtil.setServiceFields(dc.getDispatcher(),
					"JQgetListEmplLeave",UtilMisc.toMap("listAllConditions", listAllConditions,"listSortFields",listSortFields,"opts",opts,"parameters",parameters), userLogin, (TimeZone) context.get("timeZone"), locale);
			
			inputServices.put("timeZone", (TimeZone) context.get("timeZone"));
			try {
				result = dc.getDispatcher().runSync("JQgetListEmplLeave", inputServices);
			} catch (Exception e) {
				result.put("listIterator", listIterator);
				result.put("TotalRows", "0");
			}
			
			
				 
			 if(ServiceUtil.isSuccess(result))
			 {
				 
				  listIterator = (List<Map<String, Object>>) result.get("listIterator");
				  for(Map<String, Object> i : listIterator)
				  {
					  GenericValue reason = delegator.findOne("EmplLeaveReasonTypeAndSign", false, UtilMisc.toMap("emplLeaveReasonTypeId", i.get("emplLeaveReasonTypeId")));	
					  if(reason != null)
					  {
						  i.put("reason", reason.getString("description"));
						  if(reason.getBigDecimal("rateBenefit") != null)
							  i.put("rateBenefit", reason.getBigDecimal("rateBenefit").multiply(new BigDecimal(100)));
						  
					  }
					  GenericValue status = delegator.findOne("StatusItem", false, UtilMisc.toMap("statusId", i.get("statusId")));
					  if(status != null)
						  	i.put("statusDes", status.getString("description"));
					  
					  GenericValue leaveFirstHalf = delegator.findOne("EmplLeaveType", false, UtilMisc.toMap("leaveTypeId", "FIRST_HALF_DAY"));
					  if(leaveFirstHalf != null)
						  	i.put("leaveFirstHalf",leaveFirstHalf);
					  
					  GenericValue leaveSecondHalf = delegator.findOne("EmplLeaveType", false, UtilMisc.toMap("leaveTypeId", "SECOND_HALF_DAY"));
					  if(leaveSecondHalf != null)
						  	i.put("leaveSecondHalf",leaveSecondHalf);
				  }
				  
				  result.put("listIterator", listIterator);
				  result.put("TotalRows", String.valueOf(listIterator.size()));
			 }
			 
			/* if(ServiceUtil.isError(result))
				 return ServiceUtil.returnError(ModelService.ERROR_MESSAGE
						 ,UtilMisc.toList(result.containsKey(ModelService.RESPOND_ERROR) ? result.get(ModelService.RESPOND_ERROR ) : ""));*/
			 
		} catch (Exception e) {
			Debug.logError(e, module);
			e.printStackTrace();
		}finally{
					
					if(!result.containsKey("listIterator"))
						result.put("listIterator", listIterator);
					
					if(!result.containsKey("TotalRows"))
						result.put("TotalRows", "0");
		}
		
		return result;
	}
	
	
	/**
	 * The mini utils useful for supMobile Services
	 * 
	 * */
	class miniUtils{
		public String getDayRoute(int number){
			if(number == 0)
				return null;
			
			switch(number)
			{
			case 2: 
				return "MONDAY";
			case 3:
				return "TUESDAY";
			case 4:
				return "WEDNESDAY";
			case 5:
				return "THURSDAY";
			case 6:
				return "FRIDAY";
			case 7:
				return "SATURDAY";
			}
			
			return null;
		}
		
		public List<EntityCondition> buildConditionFindRoute(Delegator delegator, List<EntityCondition> listAllConditions, EntityFindOptions opts) throws GenericEntityException{
			List<EntityCondition> condi = FastList.newInstance();
			for(EntityCondition e : listAllConditions){
				String tmp = e.toString();
				if(tmp.contains("employeeId")){
					String[] tmpL = tmp.split(" LIKE|= ");
					if(UtilValidate.isNotEmpty(tmpL) && tmpL.length == 2){
						List<GenericValue> employee = delegator.findList("PartyRelationShipAndPerson",
								EntityCondition.makeCondition(UtilMisc.toList(
										EntityCondition.makeCondition("roleTypeIdFrom", "SALES_EXECUTIVE"),
										EntityCondition.makeCondition("roleTypeIdTo", "ROUTE"),
										EntityCondition.makeCondition("partyCode", EntityOperator.LIKE,
												new StringBuilder()
														.append("%")
														.append(tmpL[1]
														.replace("\"",  "")
														.replace("%",  "")
														.replace("'",  "")
														.replace(" ",  "")).append("%").toString()),
										EntityUtil.getFilterByDateExpr())),
								UtilMisc.toSet("partyIdFrom", "partyIdTo", "fromDate"), UtilMisc.toList("-fromDate"), opts, false);
						List<String> tmem = FastList.newInstance();
						for(GenericValue e1 : employee){
							tmem.add(e1.getString("partyIdTo"));
						}
						if(UtilValidate.isNotEmpty(tmem)){
							condi.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, tmem));
						}else{
							condi.add(EntityCondition.makeCondition("partyId", null));
						}
					}else{
						condi.add(EntityCondition.makeCondition("partyId", null));
					}
					break;
				}else if(tmp.contains("scheduleRoute")){
					List<GenericValue> routes = delegator.findList("RouteSchedule", e, UtilMisc.toSet("routeId"), null, opts, false);
					List<String> routesCond = FastList.newInstance();
					for(GenericValue y : routes){
						routesCond.add(y.getString("routeId"));
					}
					condi.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, routesCond));
				}else{
					condi.add(e);
				}
			}
			return condi;
		}
		
		public Map<String,Object> getInfoProfile(LocalDispatcher local,Delegator delegator,Locale locale,GenericValue userLogin){
			Map<String,Object> profile = FastMap.newInstance();
			String partyId = null;
			if(userLogin == null)
				return FastMap.newInstance();
			else 
				partyId = userLogin.getString("partyId");
			
			if(partyId == null)
				return FastMap.newInstance();
			
			try {
				GenericValue lookupPerson = delegator.findOne("PartyAndPerson", false, UtilMisc.toMap("partyId", partyId));
				
				if(lookupPerson == null)
					return  FastMap.newInstance();
				
				StringBuilder name = new StringBuilder().append(
						(lookupPerson.get("lastName") != null ? lookupPerson.get("lastName") + " " : " ")
					+ 	(lookupPerson.get("middleName") != null ? lookupPerson.get("middleName") + " " : " ")
					+ 	(lookupPerson.get("firstName") != null ? lookupPerson.get("firstName") + " " : " ")
						);
				
				profile.put("name", name != null ? name.toString() : "");
				profile.put("uId", userLogin.get("userLoginId"));
				profile.put("pId", partyId);
				
				if(lookupPerson.containsKey("gender"))
				{
					GenericValue gender =  delegator.findOne("Gender", UtilMisc.toMap("genderId", lookupPerson.getString("gender")), false);
					if(gender != null)
						profile.put("gender", gender.get("description", locale));
				}
				
				if(lookupPerson.containsKey("birthDate"))
				{
					String birthDate = DateUtil.convertDate(lookupPerson.getDate("birthDate"));
					if(birthDate != null)
						profile.put("birthDate", birthDate);
				}
				
				profile.put("idNumber", lookupPerson.getString("idNumber"));
				
				if(lookupPerson.containsKey("idIssuePlace"))
				{
					GenericValue idIssuePlace =  delegator.findOne("Geo",UtilMisc.toMap("geoId",lookupPerson.getString("idIssuePlace")), false);
					if(idIssuePlace != null)
						profile.put("geoName", idIssuePlace.getString("geoName"));
				}
				
				if(lookupPerson.containsKey("idIssueDate"))
				{
					String idIssueDate = DateUtil.convertDate(lookupPerson.getDate("idIssueDate"));
					if(idIssueDate != null)
						profile.put("idIssueDate", idIssueDate);
				}
				
				if(lookupPerson.containsKey("nativeLand"))
					profile.put("nativeLand", lookupPerson.getString("nativeLand"));
				
				if(lookupPerson.containsKey("ethnicOrigin"))
				{
					GenericValue eth =  delegator.findOne("EthnicOrigin",UtilMisc.toMap("ethnicOriginId",lookupPerson.getString("ethnicOrigin")), false);
					if(eth != null)
						profile.put("eth", eth.get("description",locale));
				}
				
				if(lookupPerson.containsKey("religion"))
				{
					GenericValue religion =  delegator.findOne("Religion",UtilMisc.toMap("religionId",lookupPerson.getString("religion")), false);
					if(religion != null)
						profile.put("religion", religion.get("description",locale));
				}
				
				if(lookupPerson.containsKey("nationality"))
				{
					GenericValue nation =  delegator.findOne("Nationality",UtilMisc.toMap("nationalityId",lookupPerson.getString("nationality")), false);
					if(nation != null)
						profile.put("nation", nation.get("description",locale));
				}
				
				if(lookupPerson.containsKey("maritalStatusId"))
				{
					GenericValue maritalStatus =  delegator.findOne("StatusItem",UtilMisc.toMap("statusId",lookupPerson.getString("maritalStatusId")), false);
					if(maritalStatus != null)
						profile.put("maritalStatus", maritalStatus.get("description",locale));
				}
				
				
				Map<String,Object> employmentData = local.runSync("getCurrentPartyEmploymentData", UtilMisc.toMap("userLogin",userLogin,"partyId",userLogin.getString("partyId")));
				
				if(ServiceUtil.isSuccess(employmentData))
				{
					String _p = (String) (employmentData.containsKey("emplPosition") ? ((Map<String,Object>) employmentData.get("emplPosition")).get("partyId") : null);
					GenericValue department = null;
					if(_p != null)
						department = delegator.findOne("PartyGroup", false, UtilMisc.toMap("partyId",_p));
					if(department != null)
						profile.put("groupName", department.getString("groupName"));
				
					if(employmentData.containsKey("emplPositionType"))	
						profile.put("emplPositionType",((Map<String,Object>) employmentData.get("emplPositionType")).get("description"));
				}
				
			profile.put("permanentInfo", generateInfo(local, delegator, userLogin, "PERMANENT_RESIDENCE"));	
			
			profile.put("residenceInfo", generateInfo(local, delegator, userLogin, "CURRENT_RESIDENCE"));
			
			
			Map<String,Object> partyEmail = local.runSync("getPartyEmail",
						UtilMisc.toMap("userLogin",userLogin,"partyId",userLogin.getString("partyId"),"contactMechPurposeTypeId","PRIMARY_EMAIL"));
			
			if(ServiceUtil.isSuccess(partyEmail))
				profile.put("emailAddress",partyEmail.get("emailAddress"));
			
			Map<String,Object> mobileNumber = local.runSync("getPartyTelephone",
					UtilMisc.toMap("userLogin",userLogin,"partyId",userLogin.getString("partyId"),"contactMechPurposeTypeId","PHONE_MOBILE"));
		
			if(ServiceUtil.isSuccess(mobileNumber))
			{
				String mobileNum = "";	
				mobileNum += mobileNumber.get("countryCode") != null ? mobileNumber.get("countryCode") + " " : "";
				mobileNum += mobileNumber.get("areaCode") != null ? mobileNumber.get("areaCode") + " " : "";
				mobileNum += mobileNumber.get("contactNumber") != null ? mobileNumber.get("contactNumber") + " " : "";
				profile.put("mobileNumber",mobileNum);
			}
			
			Map<String,Object> phoneNumber = local.runSync("getPartyTelephone",
					UtilMisc.toMap("userLogin",userLogin,"partyId",userLogin.getString("partyId"),"contactMechPurposeTypeId","PHONE_HOME"));
		
			if(ServiceUtil.isSuccess(phoneNumber))
			{
				String mobileNum = "";	
				mobileNum += mobileNumber.get("countryCode") != null ? mobileNumber.get("countryCode") + " " : "";
				mobileNum += mobileNumber.get("areaCode") != null ? mobileNumber.get("areaCode") + " " : "";
				mobileNum += mobileNumber.get("contactNumber") != null ? mobileNumber.get("contactNumber") + " " : "";
				profile.put("phoneNumber",mobileNum);
			}
			
			} catch (Exception e) {
				Debug.logError(e.getMessage(), module);
			}
			
			return profile;
		}
		
		
		public String generateInfo(LocalDispatcher local,Delegator delegator,GenericValue userLogin,String type) throws GenericServiceException, GenericEntityException{
			Map<String,Object> currentResidence = local.runSync("getPartyPostalAddress", UtilMisc.toMap("contactMechPurposeTypeId",type,"userLogin",userLogin,"partyId",userLogin.getString("partyId")));
			String residenceInfo = "";
			if(ServiceUtil.isSuccess(currentResidence))
			{
				if(currentResidence.containsKey("contactMechId"))
				{
					GenericValue permanent = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", currentResidence.get("contactMechId")), false);
					residenceInfo += permanent.getString("address1");
					if(permanent.containsKey("wardGeoId"))
					{
						GenericValue ward = delegator.findOne("Geo", UtilMisc.toMap("geoId", permanent.get("wardGeoId")), false);
						if(ward != null && !ward.getString("geoId").equals("_NA_"))
							residenceInfo +=  ward.getString("geoName") != null ? "," + ward.getString("geoName") : "";
					}

					if(permanent.containsKey("countyGeoId"))
					{
						GenericValue district = delegator.findOne("Geo", UtilMisc.toMap("geoId", permanent.get("countyGeoId")), false);
						if(district != null && !district.getString("geoId").equals("_NA_"))
							residenceInfo +=  district.getString("geoName") != null ? "," + district.getString("geoName") : "";
					}


					if(permanent.containsKey("stateProvinceGeoId"))
					{
						GenericValue stateProvince = delegator.findOne("Geo", UtilMisc.toMap("geoId", permanent.get("stateProvinceGeoId")), false);
						if(stateProvince != null && !stateProvince.getString("geoId").equals("_NA_"))
							residenceInfo +=  stateProvince.getString("geoName") != null ? "," + stateProvince.getString("geoName") : "";
					}
					
				}
				
			}
			
			return residenceInfo;
		}
		
	}
}