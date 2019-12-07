/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 */

package org.ofbiz.common.uom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;

import com.ibm.icu.util.Calendar;

/**
 * UomWorker
 */
public class UomWorker {
    public static final String module = UomWorker.class.getName();
    
    private UomWorker () {}

    public static int[] uomTimeToCalTime(String uomId) {
        if ("TF_ms".equals(uomId)) {
            return new int[] { Calendar.MILLISECOND, 1 };
        } else if ("TF_s".equals(uomId)) {
            return new int[] { Calendar.SECOND, 1 };
        } else if ("TF_min".equals(uomId)) {
            return new int[] { Calendar.MINUTE, 1 };
        } else if ("TF_hr".equals(uomId)) {
            return new int[] { Calendar.HOUR, 1 };
        } else if ("TF_day".equals(uomId)) {
            return new int[] { Calendar.DAY_OF_YEAR, 1 };
        } else if ("TF_wk".equals(uomId)) {
            return new int[] { Calendar.WEEK_OF_YEAR, 1 };
        } else if ("TF_mon".equals(uomId)) {
            return new int[] { Calendar.MONTH, 1 };
        } else if ("TF_yr".equals(uomId)) {
            return new int[] { Calendar.YEAR, 1 };
        } else if ("TF_decade".equals(uomId)) {
            return new int[] { Calendar.YEAR, 10 };
        } else if ("TF_score".equals(uomId)) {
            return new int[] { Calendar.YEAR, 20 };
        } else if ("TF_century".equals(uomId)) {
            return new int[] { Calendar.YEAR, 100 };
        } else if ("TF_millenium".equals(uomId)) {
            return new int[] { Calendar.YEAR, 1000 };
        }

        return null;
    }

    public static Calendar addUomTime(Calendar cal, Timestamp startTime, String uomId, int value) {
        if (cal == null) {
            cal = Calendar.getInstance();
        }
        if (startTime != null) {
            cal.setTimeInMillis(startTime.getTime());
        }
        int[] conv = uomTimeToCalTime(uomId);

        // conversion multiplier * value by type
        cal.add(conv[0], (value * conv[1]));
        return cal;
    }

    public static Calendar addUomTime(Calendar cal, String uomId, int value) {
        return addUomTime(cal, null, uomId, value);
    }

    public static Calendar addUomTime(Timestamp startTime, String uomId, int value) {
        return addUomTime(null, startTime, uomId, value);
    }

    /*
     * Convenience method to call the convertUom service
     */
    public static BigDecimal convertUom(BigDecimal originalValue, String uomId, String uomIdTo, LocalDispatcher dispatcher) {
        if (originalValue == null || uomId == null || uomIdTo == null) return null;
        if (uomId.equals(uomIdTo)) return originalValue;

        Map<String, Object> svcInMap = FastMap.newInstance();
        svcInMap.put("originalValue", originalValue);
        svcInMap.put("uomId", uomId);
        svcInMap.put("uomIdTo", uomIdTo);

        Map<String, Object> svcOutMap = FastMap.newInstance();
        try {
            svcOutMap = dispatcher.runSync("convertUom", svcInMap);
        } catch (GenericServiceException ex) {
            Debug.logError(ex, module);
            return null;
        }

        if (svcOutMap.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS) && svcOutMap.get("convertedValue") != null) {
            return (BigDecimal) svcOutMap.get("convertedValue");
        }
        Debug.logError("Failed to perform conversion for value [" + originalValue.toPlainString() + "] from Uom [" + uomId + "] to Uom [" + uomIdTo + "]",module);
        return null;
    }
    
    public static BigDecimal customConvertUom(String productId, String uomId, String uomIdTo, BigDecimal originalValue, String convert,Delegator delegator){
   	 	if (originalValue == null || uomId == null || uomIdTo == null) return null;
        if (uomId.equals(uomIdTo)) return originalValue;
        BigDecimal quantityReturn = BigDecimal.ONE;
        GenericValue convertUom;
		try {
			List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomFromId", uomId, "uomToId", uomIdTo)), null, null, null, false);
			listConfigs = EntityUtil.filterByDate(listConfigs);
			if (!listConfigs.isEmpty()){
				convertUom = listConfigs.get(0);
			} else {
				return new BigDecimal(-1);
			}
			if(UtilValidate.isNotEmpty(convertUom)){
	        	 BigDecimal quantityConvert = convertUom.getBigDecimal("quantityConvert");
	        	 if(quantityConvert != null && quantityConvert.compareTo(BigDecimal.ZERO) != 0){
	        		 //check convert or revert
	        		 if(convert != null && convert.equals("convert")){
	        			 float quantity = originalValue.floatValue() * quantityConvert.floatValue();
	        			 quantityReturn = BigDecimal.valueOf(quantity);
	        			
	        		 }else if (convert != null && convert.equals("revert")){
	        			 
	        			 quantityReturn = originalValue.divide(quantityConvert, 2, RoundingMode.HALF_UP);
	        		 }
	        		 
	        	 }else{
	        		 return originalValue; 
	        	 }
	         }
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return quantityReturn;
    }
    
    public static BigDecimal customConvertUom(String productId, String uomId, String uomIdTo, BigDecimal originalValue, Delegator delegator){
   	 	if (originalValue == null || UtilValidate.isEmpty(uomId) || UtilValidate.isEmpty(uomIdTo)) return null;
        if (uomId.equals(uomIdTo)) return originalValue;
        
        BigDecimal quantityReturn = null;
        GenericValue convertUom;
		try {
			List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
			listAllConditions.add(EntityCondition.makeCondition("productId", productId));
			listAllConditions.add(EntityCondition.makeCondition("uomFromId", uomId));
			listAllConditions.add(EntityCondition.makeCondition("uomToId", uomIdTo));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			convertUom = EntityUtil.getFirst(delegator.findList("ConfigPacking", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false));
			if (convertUom == null){
				listAllConditions.clear();
				listAllConditions.add(EntityCondition.makeCondition("productId", productId));
				listAllConditions.add(EntityCondition.makeCondition("uomFromId", uomIdTo));
				listAllConditions.add(EntityCondition.makeCondition("uomToId", uomId));
				listAllConditions.add(EntityUtil.getFilterByDateExpr());
				convertUom = EntityUtil.getFirst(delegator.findList("ConfigPacking", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false));
				if (convertUom == null) {
					return null;
				} else {
					BigDecimal quantityConvert = convertUom.getBigDecimal("quantityConvert");
					if (quantityConvert != null){
						int compare = BigDecimal.ZERO.compareTo(quantityConvert);
						if (compare < 0) {
							// quantityConvert greater than 0 (uomIdFrom less than uomIdTo)
							quantityReturn = originalValue.divide(quantityConvert, 2, RoundingMode.HALF_UP);
						} else if (compare > 0) {
							// quantityConvert less than 0 (uomIdFrom greater than uomIdTo)
							quantityConvert = quantityConvert.abs();
							quantityReturn = originalValue.multiply(quantityConvert);
						} else {
							return BigDecimal.ZERO;
						}
					}
				}
			} else {
				BigDecimal quantityConvert = convertUom.getBigDecimal("quantityConvert");
				if (quantityConvert != null){
					int compare = BigDecimal.ZERO.compareTo(quantityConvert);
					if (compare < 0) {
						// quantityConvert greater than 0 (uomIdFrom greater than uomIdTo)
						quantityReturn = originalValue.multiply(quantityConvert);
					} else if (compare > 0) {
						// quantityConvert less than 0 (uomIdFrom less than uomIdTo)
						quantityConvert = quantityConvert.abs();
						quantityReturn = originalValue.divide(quantityConvert, 2, RoundingMode.HALF_UP);
					} else {
						return BigDecimal.ZERO;
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError("Error when customConvertUom: productId = " + productId + ", uomIdFrom = " + uomId + ", uomIdTo = " + uomIdTo + ", originalValue = " + originalValue, module);
		}
		return quantityReturn;
    }
    
    public static BigDecimal getValueConvertUom(String productId, String uomId, String uomIdTo, Delegator delegator) throws GenericEntityException{
    	BigDecimal convertValueReturn = null;
   	 	if (UtilValidate.isEmpty(uomId) || UtilValidate.isEmpty(uomIdTo)) return null;
        if (uomId.equals(uomIdTo)) return convertValueReturn;
        
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		listAllConditions.add(EntityCondition.makeCondition("productId", productId));
		listAllConditions.add(EntityCondition.makeCondition("uomFromId", uomId));
		listAllConditions.add(EntityCondition.makeCondition("uomToId", uomIdTo));
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		GenericValue convertUom = EntityUtil.getFirst(delegator.findList("ConfigPacking", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false));
		if (convertUom == null){
			listAllConditions.clear();
			listAllConditions.add(EntityCondition.makeCondition("productId", productId));
			listAllConditions.add(EntityCondition.makeCondition("uomFromId", uomIdTo));
			listAllConditions.add(EntityCondition.makeCondition("uomToId", uomId));
			listAllConditions.add(EntityUtil.getFilterByDateExpr());
			convertUom = EntityUtil.getFirst(delegator.findList("ConfigPacking", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false));
			if (convertUom != null) {
				BigDecimal quantityConvert = convertUom.getBigDecimal("quantityConvert");
				if (quantityConvert != null){
					int compare = BigDecimal.ZERO.compareTo(quantityConvert);
					if (compare != 0) {
						// quantityConvert greater than 0 (uomIdFrom less than uomIdTo)
						convertValueReturn = quantityConvert.divide(quantityConvert, 2, RoundingMode.HALF_UP);
					}
				}
			}
		} else {
			convertValueReturn = convertUom.getBigDecimal("quantityConvert");
		}
		return convertValueReturn;
    }
}
