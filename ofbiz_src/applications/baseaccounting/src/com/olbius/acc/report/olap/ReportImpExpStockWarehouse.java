package com.olbius.acc.report.olap;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportImpExpStockWarehouse extends OlbiusOlapService {

    private OlbiusQuery query;
    private ReturnResultGrid result = new ReturnResultGrid();

    @Override
    public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        setFromDate((Date) context.get("fromDate"));
        setThruDate((Date) context.get("thruDate"));
        String facilityId = (String) context.get("facilityId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        if (facilityId == null || facilityId.equals("null")) {
            List<GenericValue> facilities = null;
            try {
                facilities = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", userLogin.getString("lastOrg")), null, null, null, false);
            } catch (GenericEntityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (UtilValidate.isNotEmpty(facilities)) {
                facilityId = facilities.get(0).getString("facilityId");
            }
        }
        putParameter("facilityId", facilityId);
        putParameter("product", context.get("product[]"));
    }

    @Override
    protected OlapQuery getQuery() {
        if (query == null) {
            query = init();
        }
        return query;
    }

    private Long getFacilityId(String facilityId) {
        Long facility = null;
        if (facilityId != null) {
            facility = FacilityDimension.D.getId(delegator, (String) facilityId);
        }
        return facility;
    }

    private List<Object> getProducts(List<?> list) {
        List<Object> tmp = new ArrayList<Object>();
        if (list != null) {
            for (Object x : list) {
                Long y = ProductDimension.D.getId(delegator, (String) x);
                if (y != -1) {
                    tmp.add(y);
                }
            }
        }
        return tmp;
    }

    private String condInFromlist(List<Object> x) {
        String condIn = "";
        for(Object a : x) {
            condIn += "'" + a.toString() + "',";
        }
        return condIn.substring(0, condIn.length() - 1);
    }
    private OlbiusQuery init() {
        Long facility = getFacilityId((String) getParameter("facilityId"));
        List<Object> products = getProducts((List<?>) getParameter("product"));

        OlbiusQuery query = makeQuery();
        OlbiusQuery queryOpEx = new OlbiusQuery();
        OlbiusQuery queryOpExIm = new OlbiusQuery();
        OlbiusQuery queryIMEP = new OlbiusQuery(getSQLProcessor());

        OlbiusQuery queryOpening = new OlbiusQuery();//ton kho dau ki
        queryOpening.from("inventory_item_fact", "itf");
        queryOpening.select("itf.product_dim_id");
        queryOpening.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
        queryOpening.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "openingQuantity");
        queryOpening.select("CASE WHEN prd.require_amount = 'Y' THEN sum(pacf.average_cost * itf.amount_on_hand_total) ELSE sum(pacf.average_cost * itf.quantity_on_hand_total) END", "openingAmount");
        queryOpening.select("itf.facility_dim_id");
        queryOpening.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
        queryOpening.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf", "itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and pacf.thru_date_dim = -1");
        Condition conOpeningStock = new Condition();
        conOpeningStock = Condition.make("itf.inventory_date_dim_id  <= " + getSqlTime(getFromDate()));
        if (UtilValidate.isNotEmpty(facility)) {
            conOpeningStock.and(Condition.make("itf.facility_dim_id='" + facility + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(products)) {
            String condIn = condInFromlist(products);
            conOpeningStock.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
        queryOpening.where(conOpeningStock);
        queryOpening.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");

        OlbiusQuery queryExport = new OlbiusQuery(getSQLProcessor());//Phat sinh xuat
        queryExport.from("inventory_item_fact", "itf");
        queryExport.select("itf.product_dim_id");
        queryExport.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
        queryExport.select("CASE WHEN prd.require_amount = 'Y' THEN -sum(itf.amount_on_hand_total) ELSE -sum(itf.quantity_on_hand_total) END", "exportQuantity");
        queryExport.select("CASE WHEN prd.require_amount = 'Y' THEN -sum(pacf.average_cost * itf.amount_on_hand_total) ELSE -sum(pacf.average_cost * itf.quantity_on_hand_total) END", "exportAmount");
        queryExport.select("itf.facility_dim_id");
        queryExport.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
        queryExport.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf", "itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and pacf.thru_date_dim = -1");
        Condition conExport = new Condition();
        conExport = Condition.make("itf.inventory_type='EXPORT'");
        conExport.and(Condition.make("itf.inventory_date_dim_id BETWEEN " + getSqlTime(getFromDate()) + " AND " + getSqlTime(getThruDate())));
        if (UtilValidate.isNotEmpty(facility)) {
            conExport.and(Condition.make("itf.facility_dim_id='" + facility + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(products)) {
            String condIn = condInFromlist(products);
            conExport.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
        queryExport.where(conExport); //itf.inventory_type = 'EXPORT' AND ((itf.inventory_date_dim_id BETWEEN '2018-01-05' AND '2018-01-05')) AND (itf.facility_dim_id = '48')
        queryExport.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");

        OlbiusQuery queryImport = new OlbiusQuery();//Phat sinh nhap
        queryImport.from("inventory_item_fact", "itf");
        queryImport.select("itf.product_dim_id");
        queryImport.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
        queryImport.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "importQuantity");
        queryImport.select("CASE WHEN prd.require_amount = 'Y' THEN sum(pacf.average_cost * itf.amount_on_hand_total) ELSE sum(pacf.average_cost * itf.quantity_on_hand_total) END", "importAmount");
        queryImport.select("itf.facility_dim_id");
        queryImport.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
        queryImport.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf", "itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and pacf.thru_date_dim = -1");
        Condition conImport = new Condition();
        conImport = Condition.make("itf.inventory_type='RECEIVE'");
        conImport.and(Condition.make("itf.inventory_date_dim_id BETWEEN " + getSqlTime(getFromDate()) + " AND " + getSqlTime(getThruDate())));
        if (UtilValidate.isNotEmpty(facility)) {
            conImport.and(Condition.make("itf.facility_dim_id='" + facility + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(products)) {
            String condIn = condInFromlist(products);
            conImport.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
        queryImport.where(conImport); //itf.inventory_type = 'RECEIVE' AND ((itf.inventory_date_dim_id BETWEEN '2018-01-05' AND '2018-01-05')) AND (itf.facility_dim_id = '48')
        queryImport.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");

        OlbiusQuery queryEnding = new OlbiusQuery();// ton kho cuoi ky
        queryEnding.from("inventory_item_fact", "itf");
        queryEnding.select("itf.product_dim_id");
        queryEnding.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
        queryEnding.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "endingQuantity");
        queryEnding.select("CASE WHEN prd.require_amount = 'Y' THEN sum(pacf.average_cost * itf.amount_on_hand_total) ELSE sum(pacf.average_cost * itf.quantity_on_hand_total) END", "endingAmount");
        queryEnding.select("itf.facility_dim_id");
        queryEnding.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
        queryEnding.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf", "itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and pacf.thru_date_dim = -1");
        Condition condEndingStock = Condition.make("itf.inventory_date_dim_id <= " + getSqlTime(getThruDate()));
        if (UtilValidate.isNotEmpty(facility)) {
            condEndingStock.and(Condition.make("itf.facility_dim_id='" + facility + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(products)) {
            String condIn = condInFromlist(products);
            condEndingStock.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
        queryEnding.where(condEndingStock); //itf.inventory_date_dim_id <= 1515171599 AND (itf.facility_dim_id = '48')
        queryEnding.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");

        /**========== join query =============**/
        queryOpEx.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Expr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        queryOpEx.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Expr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        queryOpEx.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Expr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        queryOpEx.select("Openi.openingQuantity", "openingQuantity");
        queryOpEx.select("Openi.openingAmount", "openingAmount");
        queryOpEx.select("Expr.exportQuantity", "exportQuantity");
        queryOpEx.select("Expr.exportAmount", "exportAmount");
        queryOpEx.from(queryOpening, "Openi");
        queryOpEx.join(Join.LEFT_OUTER_JOIN, queryExport, "Expr", "Openi.product_dim_id = Expr.product_dim_id AND Openi.quantity_uom_dim_id = Expr.quantity_uom_dim_id AND Openi.facility_dim_id = Expr.facility_dim_id");

        OlbiusQuery temp1 = new OlbiusQuery();
        temp1.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Expr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        temp1.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Expr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        temp1.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Expr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        temp1.select("Openi.openingQuantity", "openingQuantity");
        temp1.select("Openi.openingAmount", "openingAmount");
        temp1.select("Expr.exportQuantity", "exportQuantity");
        temp1.select("Expr.exportAmount", "exportAmount");
        temp1.from(queryOpening, "Openi");
        temp1.join(Join.RIGHT_OUTER_JOIN, queryExport, "Expr", "Openi.product_dim_id = Expr.product_dim_id AND Openi.quantity_uom_dim_id = Expr.quantity_uom_dim_id AND Openi.facility_dim_id = Expr.facility_dim_id");
        OlbiusQuery temp = new OlbiusQuery();
        temp.select("*").from("((" + queryOpEx + ") union all (" + temp1 + "))", "temp");

        queryOpExIm.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Impr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        queryOpExIm.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Impr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        queryOpExIm.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Impr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        queryOpExIm.select("Openi.openingQuantity", "openingQuantity");
        queryOpExIm.select("Openi.openingAmount", "openingAmount");
        queryOpExIm.select("Openi.exportQuantity", "exportQuantity");
        queryOpExIm.select("Openi.exportAmount", "exportAmount");
        queryOpExIm.select("Impr.importQuantity", "importQuantity");
        queryOpExIm.select("Impr.importAmount", "importAmount");
        queryOpExIm.from(temp, "Openi");
        queryOpExIm.join(Join.LEFT_OUTER_JOIN, queryImport, "Impr", "Openi.product_dim_id = Impr.product_dim_id AND Openi.quantity_uom_dim_id = Impr.quantity_uom_dim_id AND Openi.facility_dim_id = Impr.facility_dim_id");

        OlbiusQuery temp2 = new OlbiusQuery();
        temp2.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Impr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        temp2.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Impr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        temp2.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Impr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        temp2.select("Openi.openingQuantity", "openingQuantity");
        temp2.select("Openi.openingAmount", "openingAmount");
        temp2.select("Openi.exportQuantity", "exportQuantity");
        temp2.select("Openi.exportAmount", "exportAmount");
        temp2.select("Impr.importQuantity", "importQuantity");
        temp2.select("Impr.importAmount", "importAmount");
        temp2.from(temp, "Openi");
        temp2.join(Join.RIGHT_OUTER_JOIN, queryImport, "Impr", "Openi.product_dim_id = Impr.product_dim_id AND Openi.quantity_uom_dim_id = Impr.quantity_uom_dim_id AND Openi.facility_dim_id = Impr.facility_dim_id");
        OlbiusQuery temp3 = new OlbiusQuery();
        temp3.select("*").from("((" + queryOpExIm + ") union all (" + temp2 + "))", "temp3");

        queryIMEP.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Endi.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        queryIMEP.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Endi.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        queryIMEP.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Endi.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        queryIMEP.select("Openi.openingQuantity", "openingQuantity");
        queryIMEP.select("Openi.openingAmount", "openingAmount");
        queryIMEP.select("Openi.exportQuantity", "exportQuantity");
        queryIMEP.select("Openi.exportAmount", "exportAmount");
        queryIMEP.select("Openi.importQuantity", "importQuantity");
        queryIMEP.select("Openi.importAmount", "importAmount");
        queryIMEP.select("Endi.endingQuantity", "endingQuantity");
        queryIMEP.select("Endi.endingAmount", "endingAmount");
        queryIMEP.from(temp3, "Openi");
        queryIMEP.join(Join.LEFT_OUTER_JOIN, queryEnding, "Endi", "Openi.product_dim_id = Endi.product_dim_id AND Openi.quantity_uom_dim_id = Endi.quantity_uom_dim_id AND Openi.facility_dim_id = Endi.facility_dim_id");

        OlbiusQuery temp4 = new OlbiusQuery();
        temp4.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Endi.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        temp4.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Endi.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        temp4.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Endi.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        temp4.select("Openi.openingQuantity", "openingQuantity");
        temp4.select("Openi.openingAmount", "openingAmount");
        temp4.select("Openi.exportQuantity", "exportQuantity");
        temp4.select("Openi.exportAmount", "exportAmount");
        temp4.select("Openi.importQuantity", "importQuantity");
        temp4.select("Openi.importAmount", "importAmount");
        temp4.select("Endi.endingQuantity", "endingQuantity");
        temp4.select("Endi.endingAmount", "endingAmount");
        temp4.from(temp3, "Openi");
        temp4.join(Join.RIGHT_OUTER_JOIN, queryEnding, "Endi", "Openi.product_dim_id = Endi.product_dim_id AND Openi.quantity_uom_dim_id = Endi.quantity_uom_dim_id AND Openi.facility_dim_id = Endi.facility_dim_id");
        OlbiusQuery temp5 = new OlbiusQuery();
        temp5.select("*").from("((" + queryIMEP + ") union all (" + temp4 + "))", "temp5");

        query.select("importAmount").select("importQuantity").select("exportAmount").select("exportQuantity").select("endingQuantity").select("endingAmount").select("openingAmount").select("openingQuantity").select("uom_dimension.uom_id", "quantity_uom_id").select("facility_dimension.facility_id", "facility_id")
        .select("facility_dimension.facility_name", "facility_name").select("facility_dimension.facility_code", "facility_code").select("product_dimension.product_id", "product_id").select("product_dimension.product_code", "product_code").select("product_dimension.product_name", "product_name");
        query.from(temp5, "IMEP");
        query.join(Join.INNER_JOIN, "product_dimension", "IMEP.product_dim_id = product_dimension.dimension_id");
        query.join(Join.INNER_JOIN, "facility_dimension", "IMEP.facility_dim_id = facility_dimension.dimension_id");
        query.join(Join.INNER_JOIN, "uom_dimension", "IMEP.quantity_uom_dim_id = uom_dimension.dimension_id");
        query.groupBy("product_dimension.dimension_id");
        return query;
    }

    @Override
    public void prepareResultGrid() {
        addDataField("productId", "product_id");
        addDataField("productCode", "product_code");
        addDataField("productName", "product_name");
        addDataField("facilityId", "facility_id");
        addDataField("facilityName", "facility_name");
        addDataField("facilityCode", "facility_code");
        addDataField("quantityUomId", "quantity_uom_id");
        addDataField("openingQuantity", "openingQuantity");
        addDataField("openingAmount", "openingAmount");
        addDataField("importQuantity", "importQuantity");
        addDataField("importAmount", "importAmount");
        addDataField("exportQuantity", "exportQuantity");
        addDataField("exportAmount", "exportAmount");
        addDataField("endingQuantity", "endingQuantity");
        addDataField("endingAmount", "endingAmount");
    }

    @Override
    protected OlapResultQueryInterface returnResultGrid() {
        return result;
    }

    private class ReturnResultGrid extends ReturnResultGridEx {
        @Override
        protected Map<String, Object> getObject(ResultSet result) {
            Map<String, Object> map = super.getObject(result);
            try {
                map.put("facility_id", result.getString("facility_id"));
                map.put("facility_name", result.getString("facility_name"));
                map.put("facility_code", result.getString("facility_code"));
                map.put("product_id", result.getString("product_id"));
                map.put("product_code", result.getString("product_code"));
                map.put("product_name", result.getString("product_name"));
                map.put("quantity_uom_id", result.getString("quantity_uom_id"));
                if (UtilValidate.isNotEmpty(result.getBigDecimal("openingQuantity"))) {
                    map.put("openingQuantity", result.getBigDecimal("openingQuantity"));
                } else {
                    map.put("openingQuantity", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("openingAmount"))) {
                    map.put("openingAmount", result.getBigDecimal("openingAmount"));
                } else {
                    map.put("openingAmount", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("importQuantity"))) {
                    map.put("importQuantity", result.getBigDecimal("importQuantity"));
                } else {
                    map.put("importQuantity", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("importAmount"))) {
                    map.put("importAmount", result.getBigDecimal("importAmount"));
                } else {
                    map.put("importAmount", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("exportQuantity"))) {
                    map.put("exportQuantity", result.getBigDecimal("exportQuantity"));
                } else {
                    map.put("exportQuantity", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("exportAmount"))) {
                    map.put("exportAmount", result.getBigDecimal("exportAmount"));
                } else {
                    map.put("exportAmount", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("endingQuantity"))) {
                    map.put("endingQuantity", result.getBigDecimal("endingQuantity"));
                } else {
                    map.put("endingQuantity", BigDecimal.ZERO);
                }
                if (UtilValidate.isNotEmpty(result.getBigDecimal("endingAmount"))) {
                    map.put("endingAmount", result.getBigDecimal("endingAmount"));
                } else {
                    map.put("endingAmount", BigDecimal.ZERO);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return map;
        }
    }
}