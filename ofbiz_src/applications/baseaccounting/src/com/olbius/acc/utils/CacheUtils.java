package com.olbius.acc.utils;

import org.ofbiz.entity.Delegator;
import org.omg.CORBA.portable.Delegate;

/**
 * Created by user on 11/15/18.
 */
public class CacheUtils {
    public static final String symbol = ";";
    public static final String FORMULA_OPENING_CACHE = "formulaOpeningCache";
    public static final String FORMULA_POSTED_CACHE = "formulaPostedCache";
    public static final String FORMULA_ENDING_CACHE = "formulaEndingCache";
    public static final String FORMULA_TARGET_CACHE = "formulaTargetCache";
    public static final String BAL_OPENING_CACHE = "balOpeningCache";
    public static final String BAL_POSTED_CACHE = "balPostedCache";
    public static final String BAL_ENDING_CACHE = "balEndingCache";

    public static void clearCache(Delegator delegator) {
        CacheSingleton.getCacheByName(BAL_OPENING_CACHE).clean(delegator);
        CacheSingleton.getCacheByName(BAL_POSTED_CACHE).clean(delegator);
        CacheSingleton.getCacheByName(BAL_ENDING_CACHE).clean(delegator);
        CacheSingleton.getCacheByName(FORMULA_TARGET_CACHE).clean(delegator);

        CacheSingleton.getFormulaCache(FORMULA_OPENING_CACHE).clean(delegator);
        CacheSingleton.getFormulaCache(FORMULA_POSTED_CACHE).clean(delegator);
        CacheSingleton.getFormulaCache(FORMULA_ENDING_CACHE).clean(delegator);
    }

}
