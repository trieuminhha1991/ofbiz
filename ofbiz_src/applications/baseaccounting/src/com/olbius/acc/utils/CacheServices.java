package com.olbius.acc.utils;

import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.util.Map;

/**
 * Created by user on 11/21/18.
 */
public class CacheServices {
    public static Map<String, Object> clearAccountingCache(DispatchContext ctx, Map<String, ? extends Object> context) {
        Delegator delegator = ctx.getDelegator();
        CacheUtils.clearCache(delegator);
        return ServiceUtil.returnSuccess();
    }
}
