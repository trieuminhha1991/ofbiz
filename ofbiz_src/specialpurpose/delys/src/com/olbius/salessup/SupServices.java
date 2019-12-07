package com.olbius.salessup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;


public class SupServices{
	public static final String module  = SupServices.class.getName();
	public static final String resource = "DelysAdminUiLabels";
	/*description  : get list route for salesSup
	 * @param : DispatchContext
	 * @param : context
	 * @return 
	 * 
	 * */
	public static Map<String,Object> JQgetListRoute(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		List<Map<String,Object>> listRoute = FastList.newInstance();
		try {
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			List<String> ScheduleInUTF = new ArrayList<String>();
			try {
				List<GenericValue> listRouteDetail  = delegator.findList("RouteDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
				if(listRouteDetail.size() > 0){
					for(int i = 0 ; i < listRouteDetail.size();i++){
						if(listRoute.size() > 0 ){
							boolean flag = false;
							for(int j = 0 ; j < listRoute.size();j++){
								if(listRouteDetail.get(i).getString("routeId").equals(listRoute.get(j).get("routeId"))){
									flag = true;
									String schedule = (String) listRoute.get(j).get("scheduleRoute");
									if(schedule.equals(listRouteDetail.get(i).getString("scheduleRoute"))){
										continue;
									}else{
										schedule += "," + listRouteDetail.get(i).getString("scheduleRoute") + ",";
										listRoute.get(j).put("scheduleRoute", schedule);
									}
								}
							}
							if(!flag){
								Map<String,Object> mapTpx = FastMap.newInstance();
								mapTpx.put("routeId", listRouteDetail.get(i).getString("routeId"));
								mapTpx.put("description", listRouteDetail.get(i).getString("description"));
								mapTpx.put("scheduleRoute", listRouteDetail.get(i).getString("scheduleRoute"));
								listRoute.add(mapTpx);
								}
						}else{
							Map<String,Object> mapTemp = FastMap.newInstance();
							mapTemp.put("routeId", listRouteDetail.get(i).getString("routeId"));
							mapTemp.put("description", listRouteDetail.get(i).getString("description"));
							mapTemp.put("scheduleRoute", listRouteDetail.get(i).getString("scheduleRoute"));
							listRoute.add(mapTemp);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();return ServiceUtil.returnError("Error get list routedetail");
				// TODO: handle exception
			}
//			listRoute = delegator.find("RouteDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opt);
				result.put("listIterator", listRoute);	
				result.put("TotalRows", String.valueOf(listRoute.size()));
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal get list Route cause : " + e.getMessage());
			// TODO: handle exception
		}
		
		return result;
	}
	
	
	/*
	 * delete route 
	 * @param DispatchContext
	 * */
	public static Map<String,Object> deleteRoute(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = dpct.getDelegator();
		String partyId = (String) context.get("routeId");
		String roleType = "DELYS_ROUTE";
		try {
			try {
				GenericValue party = delegator.findOne("Party", false, UtilMisc.toMap("partyId", partyId));
				if(UtilValidate.isNotEmpty(party)){
					party.set("statusId", "PARTY_DISABLED");
					party.store();
				}
				Map<String,Object> mapTmp  = FastMap.newInstance();
				GenericValue userLogin = delegator.findOne("UserLogin", false, UtilMisc.toMap("userLoginId","system"));
				List<EntityCondition> listcond = FastList.newInstance();
				listcond.add(EntityCondition.makeCondition("partyIdFrom",partyId));
				listcond.add(EntityCondition.makeCondition("partyIdTo",partyId));
				List<GenericValue> listPt = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listcond,EntityJoinOperator.OR), null, null, null, false);
				if(UtilValidate.isNotEmpty(listPt)){
					for(GenericValue pt : listPt){
						pt.remove();
					}
				}
				if(UtilValidate.isNotEmpty(userLogin)){
					mapTmp.put("userLogin",userLogin);
				}
				mapTmp.put("roleTypeId", roleType);
				mapTmp.put("partyId", partyId);
				dpct.getDispatcher().runSync("deletePartyRole", mapTmp);
			} catch (Exception e) {
				e.printStackTrace();
				return ServiceUtil.returnError("Fatal delete role and disabled party cause : " + e.getMessage());
				// TODO: handle exception
			}
			try {
				GenericValue routeInfo  = delegator.findOne("RouteInformation", false, UtilMisc.toMap("routeId",partyId));
				List<GenericValue> listRouteSchedule = delegator.findList("RouteSchedule", EntityCondition.makeCondition(UtilMisc.toMap("routeId",partyId)), null, null, null, false);
				for(GenericValue routeSchedule : listRouteSchedule){
					routeSchedule.remove();
				}
				if(UtilValidate.isNotEmpty(routeInfo)){
					routeInfo.remove();
				}
			} catch (Exception e) {
				e.printStackTrace();
				return ServiceUtil.returnError("Fatal error when delete route infomartion and route schedule cause : " + e.getMessage());
				// TODO: handle exception
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal delete route cause : " + e.getMessage());
			// TODO: handle exception
		}
		
		return ServiceUtil.returnSuccess();
	}
	/*
	 * update Route 
	 * @param DispatchContext
	 * @param context
	 * @return
	 * 
	 * */
	public static Map<String,Object> updateRoute(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
 		String routeId = (String) context.get("routeId");
		String routeName = (String) context.get("description");
		List<String> Schedule = (List<String>) context.get("scheduleRoute");
		Locale locale = (Locale) context.get("locale");
		try {
			if(UtilValidate.isNotEmpty(routeId) && UtilValidate.isNotEmpty(routeName)){
				GenericValue routeInfo  = delegator.findOne("RouteInformation", false, UtilMisc.toMap("routeId", routeId));
				routeInfo.set("description", routeName);
				routeInfo.store();
			}
			List<GenericValue> listRouteSchedule = delegator.findList("RouteSchedule", EntityCondition.makeCondition(UtilMisc.toMap("routeId", routeId)), null, null, null, false);
			for(GenericValue rt : listRouteSchedule){
				rt.remove();
			}
			for(String sc : Schedule){
				String strTmp = "";
				List<String> ListstrTmp = new ArrayList<String>();
				if(sc.equals("T2") || sc.equals("MONDAY")){
					strTmp = "MONDAY";
				}else if(sc.equals("T3") || sc.equals("TUESDAY")){
					strTmp = "TUESDAY";
				}else if(sc.equals("T4") || sc.equals("WEDNESDAY")){
					strTmp = "WEDNESDAY";
				}else if(sc.equals("T5") || sc.equals("THURSDAY")){
					strTmp = "THURSDAY";
				}else if(sc.equals("T6") || sc.equals("FRIDAY")){
					strTmp = "FRIDAY";
				}else if(sc.equals("T7") || sc.equals("SATURDAY")){
					strTmp = "SATURDAY";
				};
				GenericValue rtschedule = delegator.makeValue("RouteSchedule");
				rtschedule.set("routeId", routeId);
				rtschedule.set("scheduleRoute", strTmp);
				rtschedule.create();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "updateRouteError", locale));
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "updateRouteSuccess", locale));
	}
	/*
	 *update customer in route  
	 * 
	 * */
	public static Map<String,Object> removeCustomerOutRoute(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator  delegator = (Delegator) dpct.getDelegator();
		String cusId = (String) context.get("cusId");
		Locale locale = (Locale) context.get("locale");
		try {
			if(UtilValidate.isNotEmpty(cusId)){
				List<GenericValue> ptRelation = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", cusId,"roleTypeIdTo","DELYS_CUSTOMER_GT")),null,null,null,false);
				if(UtilValidate.isNotEmpty(ptRelation)){
					Map<String,Object> maptpx = FastMap.newInstance();
					maptpx.put("userLogin",(GenericValue) context.get("userLogin"));
					maptpx.put("partyIdFrom", ptRelation.get(0).getString("partyIdFrom"));
					maptpx.put("partyIdTo", ptRelation.get(0).getString("partyIdTo"));
					maptpx.put("roleTypeIdTo", ptRelation.get(0).getString("roleTypeIdTo"));
					maptpx.put("roleTypeIdFrom", ptRelation.get(0).getString("roleTypeIdFrom"));
					maptpx.put("fromDate", ptRelation.get(0).getString("fromDate"));
					maptpx.put("thruDate", UtilDateTime.nowTimestamp());
					maptpx.put("partyRelationshipTypeId", "GROUP_ROLLUP");
					try {
						dpct.getDispatcher().runSync("updatePartyRelationship", maptpx);
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "removeerror", locale));
					}
				}
			}
		} catch (Exception e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "removeerror", locale));
			// TODO: handle exception
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "removesuccess", locale));
	}
	
	/*
	 *update customer in route  
	 * 
	 * */
	public static Map<String,Object> removeSMOutRoute(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator  delegator = (Delegator) dpct.getDelegator();
		String cusId = (String) context.get("cusId");
		Locale locale = (Locale) context.get("locale");
		try {
			if(UtilValidate.isNotEmpty(cusId)){
				List<GenericValue> ptRelation = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", cusId,"roleTypeIdFrom","DELYS_SALESMAN_GT","thruDate",null)),null,null,null,false);
				if(UtilValidate.isNotEmpty(ptRelation)){
					Map<String,Object> maptpx = FastMap.newInstance();
					maptpx.put("userLogin",(GenericValue) context.get("userLogin"));
					maptpx.put("partyIdFrom", ptRelation.get(0).getString("partyIdFrom"));
					maptpx.put("partyIdTo", ptRelation.get(0).getString("partyIdTo"));
					maptpx.put("roleTypeIdTo", ptRelation.get(0).getString("roleTypeIdTo"));
					maptpx.put("roleTypeIdFrom", ptRelation.get(0).getString("roleTypeIdFrom"));
					maptpx.put("fromDate", ptRelation.get(0).getString("fromDate"));
					maptpx.put("thruDate", UtilDateTime.nowTimestamp());
					maptpx.put("partyRelationshipTypeId", "GROUP_ROLLUP");
					try {
						dpct.getDispatcher().runSync("updatePartyRelationship", maptpx);
					} catch (Exception e) {
						e.printStackTrace();
						return ServiceUtil.returnError(UtilProperties.getMessage(resource, "removeerror", locale));
					}
				}
			}
		} catch (Exception e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "removeerror", locale));
			// TODO: handle exception
		}
		
		return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "removesuccess", locale));
	}
	public static Map<String,Object> getListCustomerDelys(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String,Object> result = FastMap.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		try {
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
		    int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
		    int start = pagesize * pageNum;
		    int end = start + pagesize;
			listAllConditions.add(EntityCondition.makeCondition("roleTypeId","DELYS_CUSTOMER_GT"));
			listIterator = delegator.find("PartyRoleAndPartyDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
//			List<GenericValue> listCust = listIterator.getPartialList(start, end);
			result.put("listIterator",listIterator);
//			result.put("TotalRows",String.valueOf(listIterator.getCompleteList().size()));
//			listIterator.close();
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal get list customer cause : " + e.getMessage());
			// TODO: handle exception
		}
		int a = 0;
		return result;
	}
	
	/*
	 * get list Exhibited Register for Sup
	 * @DispatchContext
	 * @Context
	 * @return
	 * 
	 * 
	 * */
	public static Map<String,Object> JQgetListExhibitedRegister(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> listPromotions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		try {
			Map<String,Object> tmpResult = dpct.getDispatcher().runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin")));
			if(ServiceUtil.isError(tmpResult)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "errorgetListStore", locale));
			}
			List<GenericValue> listStores = FastList.newInstance();
			listStores = (List<GenericValue>) tmpResult.get("listProductStore");
			if(UtilValidate.isNotEmpty(listStores)){
				for(GenericValue store : listStores){
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", store.getString("productStoreId"),"productPromoTypeId","EXHIBITED")));
					listCond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(UtilDateTime.nowTimestamp())));
						List<GenericValue> listTmp = delegator.findList("ProductStorePromoApplFilterLoose", EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, null, opt, false);
						if(UtilValidate.isNotEmpty(listTmp)){
							for(GenericValue tmp : listTmp){
								listPromotions.add(tmp);
							}	
						}
				}
			}
			result.put("listIterator", listPromotions);
			result.put("TotalRows", String.valueOf(listPromotions.size()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	/*
	 * get list accumulate Register for Sup
	 * @DispatchContext
	 * @Context
	 * @return
	 * 
	 * 
	 * */
	public static Map<String,Object> JQgetListAccumulateRegister(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<GenericValue> listPromotions = FastList.newInstance();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		try {
			Map<String,Object> tmpResult = dpct.getDispatcher().runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin")));
			if(ServiceUtil.isError(tmpResult)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "errorgetListStore", locale));
			}
			List<GenericValue> listStores = FastList.newInstance();
			listStores = (List<GenericValue>) tmpResult.get("listProductStore");
			if(UtilValidate.isNotEmpty(listStores)){
				for(GenericValue store : listStores){
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", store.getString("productStoreId"),"productPromoTypeId","ACCUMULATE")));
					listCond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(UtilDateTime.nowTimestamp())));
						List<GenericValue> listTmp = delegator.findList("ProductStorePromoApplFilterLoose", EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, null, opt, false);
						if(UtilValidate.isNotEmpty(listTmp)){
							for(GenericValue tmp : listTmp){
								listPromotions.add(tmp);
							}	
						}
				}
			}
			result.put("listIterator", listPromotions);
			result.put("TotalRows", String.valueOf(listPromotions.size()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	
	public static Map<String,Object> getListExhibited(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String,Object> result = FastMap.newInstance();
		List<GenericValue> listPromotions = FastList.newInstance();
		try {
			Map<String,Object> tmpResult = dpct.getDispatcher().runSync("getListStoreCompanyViewedByUserLogin", UtilMisc.toMap("userLogin", (GenericValue) context.get("userLogin")));
			if(ServiceUtil.isError(tmpResult)){
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "errorgetListStore", locale));
			}
			List<GenericValue> listStores = FastList.newInstance();
			listStores = (List<GenericValue>) tmpResult.get("listProductStore");
			if(UtilValidate.isNotEmpty(listStores)){
				for(GenericValue store : listStores){
					List<EntityCondition> listCond = FastList.newInstance();
					listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", store.getString("productStoreId"),"productPromoTypeId","EXHIBITED")));
					listCond.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr(UtilDateTime.nowTimestamp())));
						List<GenericValue> listTmp = delegator.findList("ProductStorePromoApplFilterLoose", EntityCondition.makeCondition(listCond,EntityJoinOperator.AND), null, null, null, false);
						if(UtilValidate.isNotEmpty(listTmp)){
							for(GenericValue tmp : listTmp){
								listPromotions.add(tmp);
							}	
						}
				}
			}
			result.put("listIterator", listPromotions);
			result.put("TotalRows", String.valueOf(listPromotions.size()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	 public static Map<String,Object> getLevelExhibited(DispatchContext dpct,Map<String,?extends Object> context){
		 Delegator delegator = (Delegator) dpct.getDelegator();
		 String exhibitedId = (String) context.get("exhibitedId");
		 Map<String,Object> mapLevel = FastMap.newInstance();
		 try {
			if(UtilValidate.isNotEmpty(exhibitedId)){
					List<GenericValue> listLevel = delegator.findList("ProductPromoRule", EntityCondition.makeCondition(UtilMisc.toMap("productPromoId", exhibitedId)), null,UtilMisc.toList("+productPromoRuleId"), null, false);
					mapLevel.put("listLevel", listLevel);	
			}
		} catch (Exception e) {
			return ServiceUtil.returnError("Fatal error when get list level cause : " + e.getMessage());
		}
		 return mapLevel;
	 }
	 
	public static Map<String,Object> getListCustomerRegister(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		String promoId = (String) context.get("promoId");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		 List<String> listSortFields = (List<String>) context.get("listSortFields");
		 EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> result = FastMap.newInstance();
		try{
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition( UtilMisc.toMap("productPromoId", parameters.get("promoId")[0])));
			List<GenericValue> listCustomer = delegator.findList("ProductPromoRegisterAndCustomerDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields,opt ,false );
			result.put("listIterator", listCustomer);
			result.put("TotalRows", String.valueOf(listCustomer.size()));
		} catch (Exception e) {
			return ServiceUtil.returnError("Fatal error when get list Customer Register cause : " + e.getMessage());
			// TODO: handle exception
		}
		return result;
	}
	
	public static Map<String,Object> JQgetListExhibitedMarking(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> results = FastMap.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("registerStatus", "REG_PROMO_ACCEPTED","promoMarkValue",null)));
			List<GenericValue> listPromos = delegator.findList("ProductPromoRegisterAndCustomerDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opt, false);
			List<Map<String,Object>> listCustomer = FastList.newInstance();
			
			if(UtilValidate.isNotEmpty(listPromos)){
				List<GenericValue> listpromosTmp = FastList.newInstance();
				for(GenericValue po : listPromos){
					List<EntityCondition> listAll = FastList.newInstance();
					listAll.add(EntityCondition.makeCondition("productPromoId", po.getString("productPromoId")));
					listAll.add(EntityCondition.makeCondition("productPromoTypeId","EXHIBITED"));
					listAll.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
					List<GenericValue> tmp = delegator.findList("ProductPromo",EntityCondition.makeCondition(listAll,EntityJoinOperator.AND),null,null,null,false );
					if(UtilValidate.isNotEmpty(tmp)){
						listpromosTmp.add(po);
					}
				}	
				for(GenericValue promos : listpromosTmp){
						if(!listCustomer.isEmpty()){
							boolean checkIn = false;
							for(Map<String,Object> cus :listCustomer){
								if(cus.get("partyId").equals(promos.getString("partyId"))){
									checkIn = true;break;
								}else continue;	
							}
							if(!checkIn) {
								Map<String,Object> tmp = FastMap.newInstance();
								tmp.put("partyId", promos.getString("partyId"));
								tmp.put("groupName", promos.getString("groupName"));
								listCustomer.add(tmp);
							}
						}else {
							Map<String,Object> tmp = FastMap.newInstance();
							tmp.put("partyId", promos.getString("partyId"));
							tmp.put("groupName", promos.getString("groupName"));
							listCustomer.add(tmp);
						}
				}
				results.put("listIterator", listCustomer);
				results.put("TotalRows", String.valueOf(listCustomer.size()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal get list customer register approved cause : " + e.getMessage());
			// TODO: handle exception
		}
		return results;
	}
	
	public static Map<String,Object> JQgetListAccumulateMarking(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> results = FastMap.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("registerStatus", "REG_PROMO_ACCEPTED","promoMarkValue",null)));
			List<GenericValue> listPromos = delegator.findList("ProductPromoRegisterAndCustomerDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opt, false);
			List<Map<String,Object>> listCustomer = FastList.newInstance();
			
			if(UtilValidate.isNotEmpty(listPromos)){
				List<GenericValue> listpromosTmp = FastList.newInstance();
				for(GenericValue po : listPromos){
					List<EntityCondition> listAll = FastList.newInstance();
					listAll.add(EntityCondition.makeCondition("productPromoId", po.getString("productPromoId")));
					listAll.add(EntityCondition.makeCondition("productPromoTypeId","ACCUMULATE"));
					listAll.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
					List<GenericValue> tmp = delegator.findList("ProductPromo",EntityCondition.makeCondition(listAll,EntityJoinOperator.AND),null,null,null,false );
					if(UtilValidate.isNotEmpty(tmp)){
						listpromosTmp.add(po);
					}
				}	
				for(GenericValue promos : listpromosTmp){
						if(!listCustomer.isEmpty()){
							boolean checkIn = false;
							for(Map<String,Object> cus :listCustomer){
								if(cus.get("partyId").equals(promos.getString("partyId"))){
									checkIn = true;break;
								}else continue;	
							}
							if(!checkIn) {
								Map<String,Object> tmp = FastMap.newInstance();
								tmp.put("partyId", promos.getString("partyId"));
								tmp.put("groupName", promos.getString("groupName"));
								listCustomer.add(tmp);
							}
						}else {
							Map<String,Object> tmp = FastMap.newInstance();
							tmp.put("partyId", promos.getString("partyId"));
							tmp.put("groupName", promos.getString("groupName"));
							listCustomer.add(tmp);
						}
				}
				results.put("listIterator", listCustomer);
				results.put("TotalRows", String.valueOf(listCustomer.size()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError("Fatal get list customer register approved cause : " + e.getMessage());
			// TODO: handle exception
		}
		return results;
	}
	
	public static Map<String,Object> JQgetListExhibitedForMark(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> results = FastMap.newInstance();
		List<Map<String,Object>> listFilterEx  = FastList.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("partyId",(String) parameters.get("customerId")[0]));
			List<GenericValue> listEx = delegator.findList("ExhibitedsOfStores", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opt, false);
			List<GenericValue> listPPM = delegator.findList("ProductPromoMarking", null	, null, null, null, false);
			if(UtilValidate.isNotEmpty(listPPM)){
				for(GenericValue ex : listEx){
					boolean flag = false;
					for(GenericValue ppm : listPPM){
						if(ex.getString("productPromoRegisterId").equals(ppm.getString("productPromoRegisterId"))){
								if(!ppm.getString("result").isEmpty()){
									flag = true;
									break;
								}else continue;
						}
					}
					if(!flag) {
							Map<String,Object> mapTmp = FastMap.newInstance();
							mapTmp.put("productPromoRegisterId", ex.getString("productPromoRegisterId"));
							mapTmp.put("partyId",  ex.getString("partyId"));
							mapTmp.put("groupName",  ex.getString("groupName"));
							mapTmp.put("promoName",  ex.getString("promoName"));
							mapTmp.put("productPromoRuleId",  ex.getString("productPromoRuleId"));
							listFilterEx.add(mapTmp);
					};
				}
				results.put("listIterator",listFilterEx);
				results.put("TotalRows", String.valueOf(listFilterEx.size()));
			}else{
				results.put("listIterator",listEx);
				results.put("TotalRows", String.valueOf(listEx.size()));	
			}
			
			
			
		} catch (Exception e) {
			return ServiceUtil.returnError("Fatal error when get list Exhibited Marking cause : " + e.getMessage());
			// TODO: handle exception
		}
		return results;
	}
	
	public static Map<String,Object> JQgetListAccumulateForMark(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> results = FastMap.newInstance();
		List<Map<String,Object>> listFilterEx  = FastList.newInstance();
		try {
			opt.setDistinct(true);
			listAllConditions.add(EntityCondition.makeCondition("partyId",(String) parameters.get("customerId")[0]));
			listAllConditions.add(EntityUtil.getFilterByDateExpr("fromDate", "thruDate"));
			List<GenericValue> listEx = delegator.findList("AccumulateOfStores", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opt, false);
			List<GenericValue> listPPM = delegator.findList("ProductPromoMarking", null	, null, null, null, false);
			if(UtilValidate.isNotEmpty(listPPM)){
				for(GenericValue ex : listEx){
					boolean flag = false;
					for(GenericValue ppm : listPPM){
						if(ex.getString("productPromoRegisterId").equals(ppm.getString("productPromoRegisterId"))){
								if(!ppm.getString("result").isEmpty()){
									flag = true;
									break;
								}else continue;
						}
					}
					if(!flag) {
							Map<String,Object> mapTmp = FastMap.newInstance();
							mapTmp.put("productPromoRegisterId", ex.getString("productPromoRegisterId"));
							mapTmp.put("partyId",  ex.getString("partyId"));
							mapTmp.put("groupName",  ex.getString("groupName"));
							mapTmp.put("promoName",  ex.getString("promoName"));
							mapTmp.put("productPromoRuleId",  ex.getString("productPromoRuleId"));
							listFilterEx.add(mapTmp);
					};
				}
				results.put("listIterator",listFilterEx);
				results.put("TotalRows", String.valueOf(listFilterEx.size()));
			}else{
				results.put("listIterator",listEx);
				results.put("TotalRows", String.valueOf(listEx.size()));	
			}
			
			
			
		} catch (Exception e) {
			return ServiceUtil.returnError("Fatal error when get list Exhibited Marking cause : " + e.getMessage());
			// TODO: handle exception
		}
		return results;
	}
	
	public static Map<String,Object> ResultMarking(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		String listEx = (String) context.get("listEx");
		Locale locale = (Locale) context.get("locale");
		
		try {
			JSONArray arr = new JSONArray();
			if(!listEx.isEmpty()){
				 arr = JSONArray.fromObject(listEx);
				if(UtilValidate.isNotEmpty(arr)){
					 for(int i = 0 ; i < arr.size();i++){
							JSONObject obj = arr.getJSONObject(i);
							String registerId = obj.getString("productPromoRegisterId");
							String result = obj.getString("result");
							GenericValue regis = delegator.makeValue("ProductPromoMarking");
							regis.set("productPromoRegisterId", registerId);
							regis.set("result", result);
							regis.set("createdBy", (String) ((GenericValue) context.get("userLogin")).getString("partyId"));
							regis.set("createdDate", UtilDateTime.nowTimestamp());
							regis.create();
					 }
				}
				
			}
			
		} catch (Exception e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "markingerror", locale));
			// TODO: handle exception
		}
	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resource, "markingsuccess", locale));	
	}
	/*
	 * exhibited Register
	 * @param DispatchContext dpct,context
	 * */
	public static Map<String,Object> exhibitedRegisterSUP(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		String createdBy = ((GenericValue) context.get("userLogin")).getString("partyId");
		String customerId = (String) context.get("customerId");
		String productPromoId = (String) context.get("productPromoId");
		String ruleId = (String) context.get("ruleId");
		String registerStatus = "REG_PROMO_CREATED";
		Timestamp createdDate = (Timestamp) context.get("createdDate");
		Timestamp nowtimestamp = UtilDateTime.nowTimestamp();
		Map<String,Object> result  = FastMap.newInstance();
		Map<String,Object> tmp = FastMap.newInstance();
		try {
			List<GenericValue> listRegistered = delegator.findList("ProductPromoRegister", null, null, null, null, false);
			if(UtilValidate.isNotEmpty(listRegistered)){
				boolean checkIn = false;
				for(GenericValue regis : listRegistered){
						if(regis.getString("productPromoId").equals(productPromoId) && regis.getString("partyId").equals(customerId)){
							tmp.put("duplicate", "duplicate");
							result.put("result", tmp);
							checkIn = true;
							return result;
						}else {
							continue;
						}
					}
				if(!checkIn){
					tmp.put("duplicate", "");
					GenericValue ex = delegator.makeValue("ProductPromoRegister");
					String productPromoRegisterId = delegator.getNextSeqId("ProductPromoRegister");
					ex.set("productPromoRegisterId", productPromoRegisterId);
					ex.set("createdBy", createdBy);
					ex.set("partyId", customerId);
					ex.set("productPromoId", productPromoId);
					ex.set("productPromoRuleId", ruleId);
					ex.set("registerStatus", registerStatus);
					if(createdDate == null){
						ex.set("createdDate", nowtimestamp);
					}else ex.set("createdDate", createdDate); 
					ex.create();
					tmp.put("productPromoId", productPromoId);
					tmp.put("customerId", customerId);
					tmp.put("registerStatus", registerStatus);
				}
			}else{
				GenericValue ex = delegator.makeValue("ProductPromoRegister");
				String productPromoRegisterId = delegator.getNextSeqId("ProductPromoRegister");
				ex.set("productPromoRegisterId", productPromoRegisterId);
				ex.set("createdBy", createdBy);
				ex.set("partyId", customerId);
				ex.set("productPromoId", productPromoId);
				ex.set("productPromoRuleId", ruleId);
				ex.set("registerStatus", registerStatus);
				if(createdDate == null){
					ex.set("createdDate", nowtimestamp);
				}else ex.set("createdDate", createdDate); 
				ex.create();
				tmp.put("productPromoId", productPromoId);
				tmp.put("customerId", customerId);
				tmp.put("registerStatus", registerStatus);
			}
		} catch (Exception e) {
			Debug.logError("Can't create register for exhibited " + e.getMessage(), module);
			e.printStackTrace();
		}
		result.put("result", tmp);
		return result;
	}
	/*
	 * reject exhibited 
	 * 
	 * */
	public static Map<String,Object> rejectExhibited(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		String partyId = (String) context.get("partyId");
		String productPromoId =  (String) context.get("productPromoId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		try {
			if(UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(productPromoId) && UtilValidate.isNotEmpty(productPromoRuleId)){
				List<GenericValue> ppm = delegator.findList("ProductPromoRegister", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId,"productPromoId",productPromoId,"productPromoRuleId",productPromoRuleId)),null,null,null, false);
				ppm.get(0).set("registerStatus", "REG_PROMO_CANCELED");
				ppm.get(0).store();
			}	
		} catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess();
	} 
	/*
	 * update exhibited 
	 * 
	 * */
	public static Map<String,Object> updateExhibited(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		String productPromoRegisterId = (String) context.get("productPromoRegisterId");
		String productPromoRuleId = (String) context.get("productPromoRuleId");
		try {
			if(UtilValidate.isNotEmpty(productPromoRegisterId) && UtilValidate.isNotEmpty(productPromoRuleId) ){
					GenericValue ppm = delegator.findOne("ProductPromoRegister", false, UtilMisc.toMap("productPromoRegisterId", productPromoRegisterId));
					if(UtilValidate.isNotEmpty(ppm)){
						ppm.set("productPromoRuleId",productPromoRuleId);
						ppm.store();
					}
			}	
		} catch (Exception e) {
			return ServiceUtil.returnError(e.getMessage());
			// TODO: handle exception
		}
		return ServiceUtil.returnSuccess();
	} 
	
	public static Map<String,Object> JQgetListPPM(DispatchContext dpct,Map<String,?extends Object> context){
		Delegator delegator = (Delegator) dpct.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opt = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String,String[]>) context.get("parameters");
		Map<String,Object> results = FastMap.newInstance();
		try {
			listAllConditions.add(EntityCondition.makeCondition("partyId",parameters.get("customerId")[0]));
			opt.setDistinct(true);
			if(!parameters.get("customerId")[0].isEmpty()){
				List<GenericValue> listPPM = delegator.findList("ProductPromoMarkingAndRegister", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, opt,false);
				results.put("listIterator", listPPM);
				results.put("TotalRows", String.valueOf(listPPM.size()));
			}
		} catch (Exception e) {
			return ServiceUtil.returnError("Fatal error when get list ppm cause : " +e.getMessage());
		}
			return results;
	}
	
}
