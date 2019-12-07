package com.olbius.acc.utils;

import com.olbius.acc.report.financialstm.*;
import com.olbius.entity.cache.OlbiusCache;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

import java.util.HashMap;
import java.util.Map;

public class CacheSingleton {

    private static FormulaCache postedCache;
    private static FormulaCache openingCache;
    private static FormulaCache endingCache;
    private static TargetFormulaCache targetCache;

    private static OpeningCache balOpeningCache;
    private static EndingCache balEndingCache;
    private static PostedCache balPostedCache;

    private static Map<String, OlbiusCache> map = FastMap.newInstance();
    private static Map<String, FormulaCache> formulaMap = FastMap.newInstance();
    private static Map<String, Long> config;
    static {
        config = new HashMap<>();
        config.put("maximumSize", 5000L);
        config.put("timeCache", 60L);
    }

    public static OlbiusCache getCacheByName(String name) {
        if (UtilValidate.isNotEmpty(map.get(name))) return map.get(name);
        if (CacheUtils.BAL_OPENING_CACHE.equals(name)) {
            if(balOpeningCache == null) {
                balOpeningCache = new OpeningCache(config);
            }
            if (balEndingCache == null) {
                balEndingCache = new EndingCache(config);
            }
            balOpeningCache.setEndingCache(balEndingCache);
            map.put(name, balOpeningCache);
        }
        else if(CacheUtils.BAL_POSTED_CACHE.equals(name)) {
            if(balPostedCache == null) balPostedCache = new PostedCache(config);
            map.put(name, balPostedCache);
        }
        else if(CacheUtils.BAL_ENDING_CACHE.equals(name)) {
            if(balEndingCache == null) balEndingCache = new EndingCache(config);
            if(balOpeningCache == null) balOpeningCache = new OpeningCache(config);
            if(balPostedCache == null) balPostedCache = new PostedCache(config);
            balEndingCache.setOpeningCache(balOpeningCache);
            balEndingCache.setPostedCache(balPostedCache);
            map.put(name, balEndingCache);
        }
        else if(CacheUtils.FORMULA_TARGET_CACHE.equals(name)) {
            if(targetCache == null) targetCache = new TargetFormulaCache(config);
            map.put(name, targetCache);
        }
        return map.get(name);
    }

    public static FormulaCache getFormulaCache(String name) {
        if (UtilValidate.isNotEmpty(formulaMap.get(name))) return formulaMap.get(name);
        if(CacheUtils.FORMULA_OPENING_CACHE.equals(name)) {
            if(openingCache == null) openingCache = new OpeningFormulaCache(config);
            if(endingCache == null) endingCache = new EndingFormulaCache(config);
            ((OpeningFormulaCache)openingCache).setEndingCache(endingCache);
            formulaMap.put(name, openingCache);
        }
        else if(CacheUtils.FORMULA_POSTED_CACHE.equals(name)) {
            if(postedCache == null) postedCache = new PostedFormulaCache(config);
            formulaMap.put(name, postedCache);
        }
        else if(CacheUtils.FORMULA_ENDING_CACHE.equals(name)) {
            if(openingCache == null) openingCache = new OpeningFormulaCache(config);
            if(postedCache == null) postedCache = new PostedFormulaCache(config);
            if(endingCache == null) endingCache = new EndingFormulaCache(config);
            ((EndingFormulaCache) endingCache).setOpeningCache(openingCache);
            ((EndingFormulaCache) endingCache).setPostedCache(postedCache);
            formulaMap.put(name, endingCache);
        }
        return formulaMap.get(name);
    }

    private static boolean isUpdated(Cache cache) throws GenericEntityException {
        boolean isUpdated = false;

        //Get delegator
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");

        GenericValue pentahoService = delegator.findOne("PentahoServices", UtilMisc.toMap("service", "acctgTransTotal"), false);
        if (pentahoService != null && pentahoService.getTimestamp("lastUpdated") != null) {
            isUpdated = !pentahoService.getTimestamp("lastUpdated").equals(cache.lastUpdate);
        }
        return isUpdated;
    }
}
