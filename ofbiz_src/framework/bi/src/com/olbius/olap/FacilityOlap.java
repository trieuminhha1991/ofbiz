package com.olbius.olap;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

public class FacilityOlap {
	
	 public static final String module = FacilityOlap.class.getName();
	
	public static Map<String, Object> loadInventoryReceive(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		
		Delegator delegator = ctx.getDelegator();
		
		String tenant = delegator.getDelegatorTenantId();
		
		if(tenant==null) {
			tenant = Constant.getTenantDefault();
		}
		
		GenericValue inventory = null;
		
		try {
			inventory = delegator.findOne("InventoryItemFact", false, UtilMisc.toMap("inventoryItemId", context.get("inventoryItemId"), "tenantId", tenant));
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		
		if(inventory != null) {
			return result;
		}
		
		inventory = delegator.makeValue("InventoryItemFact");
		inventory.set("tenantId", tenant);
		inventory.set("inventoryItemId", context.get("inventoryItemId"));
		inventory.set("facilityId", context.get("facilityId"));
		
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		Map<String, Object> naturalKeyFields = new HashMap<String, Object>();
		
		Object dateId = null;
		Object productId = null;
		Object facilityId = context.get("facilityId");
		
		Timestamp timestamp = (Timestamp) context.get("createdStamp");
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
		Date date = new Date(calendar.getTimeInMillis());
		
		try {
			naturalKeyFields.put("dateValue", date);
			Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "DateDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
			dateId = tmp.get("dimensionId");
			if(dateId == null) {
				tmp = dispatcher.runSync("createDateDimension", UtilMisc.toMap("dateValue", timestamp, "userLogin" ,context.get("userLogin")));
				dateId = tmp.get("dimensionId");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		try {
			naturalKeyFields.clear();
			naturalKeyFields.put("productId", context.get("productId"));
			Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "ProductDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
			productId = tmp.get("dimensionId");
			if(productId == null) {
				dispatcher.runSync("loadProductInProductDimension", UtilMisc.toMap("productId", context.get("productId"), "updateMode", "TYPE2", "userLogin" ,context.get("userLogin")));
				tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "ProductDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
				productId = tmp.get("dimensionId");
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		
		if(dateId !=null && productId != null && facilityId != null) {
			GenericValue facility = null;
			
			try {
				facility = delegator.findOne("FacilityFact", false, UtilMisc.toMap("dateDimId", dateId, "productDimId", productId, "facilityId", facilityId, "tenantId", tenant));
			} catch (GenericEntityException e) {
			}
			
			if(facility != null) {
				BigDecimal receiveTotal = (BigDecimal) facility.get("receiveTotal");
				if(context.get("quantityOnHandTotal")!=null) {
					
					facility.set("receiveTotal", receiveTotal.add((BigDecimal)context.get("quantityOnHandTotal")));
					try {
						facility.store();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
				
			} else {
				facility = delegator.makeValue("FacilityFact");
				facility.set("tenantId", tenant);
				facility.set("facilityId", facilityId);
				facility.set("productDimId", productId);
				facility.set("dateDimId", dateId);
				BigDecimal decimal = (BigDecimal)context.get("quantityOnHandTotal");
				if(decimal == null) {
					decimal = new BigDecimal(0);
				}
				facility.set("receiveTotal", decimal);
				try {
					facility.create();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
			
			inventory.set("productDimId", productId);
			
			inventory.set("inventoryDateDimId", dateId);
			
			inventory.set("quantityOnHandTotal", context.get("quantityOnHandTotal"));
			
			inventory.set("unitCost", context.get("unitCost"));
			
			Object currencyId = null;
			
			try {
				naturalKeyFields.clear();
				naturalKeyFields.put("currencyId", context.get("currencyUomId"));
				Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "CurrencyDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
				currencyId = tmp.get("dimensionId");
				/*if(currencyId == null) {
					tmp = dispatcher.runSync("createDateDimension", UtilMisc.toMap("dateValue", timestamp, "userLogin" ,context.get("userLogin")));
					dateId = tmp.get("dimensionId");
				}*/
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
			
			inventory.set("origCurrencyDimId", currencyId);
			
			try {
				inventory.create();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static Map<String, Object> loadInventoryTotal(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		Delegator delegator = ctx.getDelegator();
		
		String tenant = delegator.getDelegatorTenantId();
		
		if(tenant==null) {
			tenant = Constant.getTenantDefault();
		}
		
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		Object dateId = null;
		Object productId = null;
		Object facilityId = context.get("facilityId");
		
		java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
		
        GenericValue dateValue = null;
        try {
            dateValue = EntityUtil.getFirst(delegator.findByAnd("DateDimension", UtilMisc.toMap("dateValue", currentDate), null, false));
        } catch (GenericEntityException e) {
        	e.printStackTrace();
        }
        
        if(dateValue!=null) {
        	dateId = dateValue.get("dimensionId");
        }
        
        if(dateId != null) {
        	DynamicViewEntity viewEntity = new DynamicViewEntity();
			viewEntity.addMemberEntity("IVV", "InventoryItem");
			viewEntity.addAlias("IVV", "productId", null, null, true, true, null);
			viewEntity.addAlias("IVV", "facilityId", null, null, true, true, null);
			viewEntity.addAlias("IVV", "sum1", "quantityOnHandTotal", null, false, false, "sum");
			viewEntity.addAlias("IVV", "sum2", "availableToPromiseTotal", null, false, false, "sum");
			
			List<String> oderBy = new ArrayList<String>();
			oderBy.add("facilityId");
			oderBy.add("productId");
			EntityListIterator pli = null;
			try {
				pli = delegator.findListIteratorByCondition(viewEntity, null, null, null, oderBy, null);
				
				GenericValue inventory = null;
				Map<String, Object> naturalKeyFields = new HashMap<String, Object>();
				
				while((inventory = pli.next())!=null) {
					productId = inventory.get("productId");
					facilityId = inventory.get("facilityId");
					
					if(productId!= null) {
						try {
							naturalKeyFields.clear();
							naturalKeyFields.put("productId", productId);
							Map<String, Object> tmp = dispatcher.runSync("getDimensionIdFromNaturalKey", UtilMisc.toMap("dimensionEntityName", "ProductDimension", "naturalKeyFields", naturalKeyFields, "userLogin" ,context.get("userLogin")));
							productId = tmp.get("dimensionId");
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
					
					if(productId!= null && facilityId!=null) {
						GenericValue facility = null;
						try {
							facility = delegator.findOne("FacilityFact", UtilMisc.toMap("dateDimId", dateId, "productDimId", productId, "facilityId", facilityId, "tenantId", tenant), false);
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
						
						if(facility != null) {
							BigDecimal quantityOnHandTotal = inventory.getBigDecimal("sum1");
							BigDecimal availableToPromiseTotal = inventory.getBigDecimal("sum2");
							facility.set("inventoryTotal", quantityOnHandTotal);
							facility.set("availableToPromiseTotal", availableToPromiseTotal);
							facility.store();
						} else {
							facility = delegator.makeValue("FacilityFact");
							facility.set("tenantId", tenant);
							facility.set("facilityId", facilityId);
							facility.set("productDimId", productId);
							facility.set("dateDimId", dateId);
							BigDecimal decimal = new BigDecimal(0);
							facility.set("receiveTotal", decimal);
							BigDecimal quantityOnHandTotal = inventory.getBigDecimal("sum1");
							BigDecimal availableToPromiseTotal = inventory.getBigDecimal("sum2");
							facility.set("inventoryTotal", quantityOnHandTotal);
							facility.set("availableToPromiseTotal", availableToPromiseTotal);
							facility.create();
						}
					}
				}
				
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} finally {
				if(pli != null) {
					try {
						pli.close();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
        }
        
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
