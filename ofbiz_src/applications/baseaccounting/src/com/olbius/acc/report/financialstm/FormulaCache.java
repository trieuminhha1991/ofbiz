package com.olbius.acc.report.financialstm;

import com.olbius.entity.cache.OlbiusCache;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by user on 11/16/18.
 */
public abstract class FormulaCache extends OlbiusCache<Map<String, BigDecimal>> {
    static final String symbol = ";";

    FormulaCache(Map<String, Long> config) {
        super(config);
    }
}
