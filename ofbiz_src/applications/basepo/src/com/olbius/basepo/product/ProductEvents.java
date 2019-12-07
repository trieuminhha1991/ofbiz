package com.olbius.basepo.product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.product.ProductUtils;
import com.olbius.common.util.VNCharacterUtils;

import javolution.util.FastList;

public class ProductEvents {
	public static final String module = ProductEvents.class.getName();
	
	public static String findProductsAddToQuotPO(HttpServletRequest request, HttpServletResponse response){
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		List<GenericValue> productList = new ArrayList<GenericValue>();
		
		try {
			String productToSearch = request.getParameter("productToSearch");
			
			if (UtilValidate.isNotEmpty(productToSearch)) {
				List<EntityCondition> orConds = FastList.newInstance();
				//orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition("productNameSimple", EntityOperator.LIKE, "%" + VNCharacterUtils.removeAccent(productToSearch).toUpperCase() +"%"));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				orConds.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("idSKU"), EntityOperator.LIKE, EntityFunction.UPPER("%" + productToSearch +"%")));
				
				String idPLU = null;
				if (productToSearch.length() == 13) {
					idPLU = ProductUtils.getPluCodeInEanId(productToSearch);
					if (idPLU != null) { // EAN13 code
						orConds.add(EntityCondition.makeCondition("idPLU", idPLU));
					}
				}
				
				EntityCondition orCond = EntityCondition.makeCondition(orConds, EntityOperator.OR);
				
				List<EntityCondition> mainConds = FastList.newInstance();
				mainConds.add(orCond);
				mainConds.add(EntityCondition.makeCondition("isVirtual", "N"));
				mainConds.add(EntityUtil.getFilterByDateExpr());
				mainConds.add(EntityCondition.makeCondition(EntityCondition.makeCondition("salesDiscontinuationDate", null), 
						EntityOperator.OR, EntityCondition.makeCondition("salesDiscontinuationDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())));
				
				EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
				findOptions.setMaxRows(20);
				EntityCondition mainCond = EntityCondition.makeCondition(mainConds, EntityOperator.AND);
					
				Set<String> listSelectFields = new HashSet<String>();
				listSelectFields.add("productId");
				listSelectFields.add("productCode");
				listSelectFields.add("productName");
				listSelectFields.add("quantityUomId");
				listSelectFields.add("salesUomId");
				listSelectFields.add("uomId");
				listSelectFields.add("isVirtual");
				listSelectFields.add("isVariant");
				//listSelectFields.add("idSKU");
				//listSelectFields.add("idPLU");
				productList = delegator.findList("ProductAndUomAndGoodIdsAndCatalog", mainCond, listSelectFields, UtilMisc.toList("productCode"), findOptions, false);
				if (UtilValidate.isEmpty(productList)) {
					productList = new ArrayList<GenericValue>();
				}
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling findProductsAddToQuotPO event: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		request.setAttribute("productsList", productList);
		return "success";
	}
}
