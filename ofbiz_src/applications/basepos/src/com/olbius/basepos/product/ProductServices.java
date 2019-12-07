package com.olbius.basepos.product;



import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basepos.util.VNCharacterUtils;


public class ProductServices {
	public static final String module = ProductJQServices.class.getName();
    
	public static Map<String, Object> updateProductNameSimple(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	String productId = (String) context.get("productId");
    	String productName = (String) context.get("productName");
    	try {
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String oldProductName = product.getString("productNameSimple");
			// Upper case by default
	    	String convertedProductName = null;
	    	if(productName != null && !productName.isEmpty()){
	    		convertedProductName = VNCharacterUtils.removeAccent(productName).toUpperCase();
	    		if(convertedProductName.equals(oldProductName)){
		    		return ServiceUtil.returnSuccess();
		    	}
	    	}
			// Remove Vietnamese characters from productname
			product.set("productNameSimple", convertedProductName);
			product.store();
		} catch (GenericEntityException e1) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e1.toString();
			Debug.logError(e1, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
    	return ServiceUtil.returnSuccess();
    }
}