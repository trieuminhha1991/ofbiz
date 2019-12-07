package com.olbius.baselogistics.requirement;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;

public class RequirementEvents {
	
	public static String findProductByOrganization(HttpServletRequest request, HttpServletResponse response){
		
		Delegator delegator = (Delegator) request.getAttribute("delegator");
    	String productToSearch = request.getParameter("productToSearch");
    	String facilityId = (String) request.getParameter("facilityId");
		
    	List<EntityCondition> listAllCondition = FastList.newInstance();
    	List<Map<String, Object>> listProducts = FastList.newInstance();
    	
    	EntityFindOptions opt = new EntityFindOptions();
    	opt.setDistinct(true);
		listAllCondition.add(EntityCondition.makeCondition(UtilMisc.toList(
				EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")),
				EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%"))
				), EntityOperator.OR));

		try {
			List<GenericValue> listPAC = delegator.findList("Product", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, opt, false);
			for(GenericValue product : listPAC){
				Map<String, Object> tmpProduct = new HashMap<String, Object>();
				if(UtilValidate.isNotEmpty(product)){
					tmpProduct.put("productName", product.get("productName"));
					tmpProduct.put("productCode", product.get("productCode"));
					tmpProduct.put("productId", product.get("productId"));
					tmpProduct.put("weightUomId", product.get("weightUomId"));
					tmpProduct.put("uomId", product.get("quantityUomId"));
					tmpProduct.put("quantityUomId", product.get("quantityUomId"));
					tmpProduct.put("requireAmount", product.get("requireAmount"));
					tmpProduct.put("comment", product.get("description"));
					
					//get QuantityOnHandTotal of product in facility if this facility has it 
					List<EntityCondition> tempCond = FastList.newInstance();
					tempCond.add(EntityCondition.makeCondition("facilityId", facilityId));
					tempCond.add(EntityCondition.makeCondition("productId", product.get("productId")));
					tempCond.add(EntityUtil.getFilterByDateExpr());
					List<GenericValue> listPOF = delegator.findList("ProductAverageCostFilterByDate", EntityCondition.makeCondition(tempCond, EntityOperator.AND), null, null, opt, false);
					
					if(UtilValidate.isNotEmpty(listPOF)) {
						GenericValue localProduct = listPOF.get(0);
						
						if(UtilValidate.isNotEmpty(localProduct.get("quantityOnHandTotal"))) {
							tmpProduct.put("quantityOnHandTotal", localProduct.get("quantityOnHandTotal"));
						}else {
							tmpProduct.put("quantityOnHandTotal", BigDecimal.valueOf(0));
						}
						
						if(UtilValidate.isNotEmpty(localProduct.get("averageCost"))) {
							tmpProduct.put("unitCost", localProduct.get("averageCost"));
						}else {
							tmpProduct.put("averageCost", BigDecimal.valueOf(0));
						}
					} else {
						tmpProduct.put("quantityOnHandTotal", BigDecimal.valueOf(0));
						tmpProduct.put("unitCost", BigDecimal.valueOf(0));
					}
					listProducts.add(tmpProduct);
				}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling event: " + e.toString();
			Debug.logError(e, errMsg, null);
			return errMsg;
		}
		
		request.setAttribute("listProducts", listProducts);
    	return "success";
	}
}
