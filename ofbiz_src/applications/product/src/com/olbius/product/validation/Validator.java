package com.olbius.product.validation;

import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;

import com.olbius.product.exception.DuplicateProductIdException;

public class Validator {
	public static Map<String, Object> duplicateProduct(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericServiceException{
		Map<String, Object> result = FastMap.newInstance();
		String productId = (String)context.get("productId");
		Delegator dlgt = ctx.getDelegator();
		GenericValue product = null;
		try {
			product = dlgt.findOne("Product", UtilMisc.toMap("productId",productId), true);
		} catch (GenericEntityException e) {
			throw new GenericServiceException(e.getMessage());
		}
		if (product != null){
			throw new DuplicateProductIdException("Product Id existed");
		}
		
		return result;
	}

}
