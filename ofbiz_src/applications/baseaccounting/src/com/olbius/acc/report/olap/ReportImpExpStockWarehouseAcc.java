package com.olbius.acc.report.olap;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.grid.ReturnResultGridEx;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ReportImpExpStockWarehouseAcc extends OlbiusOlapService {

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
				facilities = delegator.findList("Facility", 
						EntityCondition.makeCondition("ownerPartyId", userLogin.getString("lastOrg")), null, null, null, false);
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

	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		Long facilityDimId = getFacilityId((String) getParameter("facilityId"));
		List<Object> productDimIds = getProducts((List<?>) getParameter("product"));
		String facilityId = (String) getParameter("facilityId");
		List<Object> productIds = (List<Object>) getParameter("product");
		
		OlbiusQuery query = makeQuery();
		OlbiusQuery queryOpEx = new OlbiusQuery();
		OlbiusQuery queryOpExIm = new OlbiusQuery();
		OlbiusQuery queryIMEP = new OlbiusQuery(getSQLProcessor());
		
		OlbiusQuery queryOpening = new OlbiusQuery();//ton kho dau ki
		queryOpening.from("inventory_item_fact","itf");
		queryOpening.select("itf.product_dim_id");
		queryOpening.select("itf.facility_dim_id");
		queryOpening.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryOpening.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "openingQuantity");
		queryOpening.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
		Condition conOpeningStock = new Condition();
		conOpeningStock = Condition.make("itf.inventory_date_dim_id  < " + getSqlTime(getFromDate()));
		if (UtilValidate.isNotEmpty(facilityDimId)) {
            conOpeningStock.and(Condition.make("itf.facility_dim_id='" + facilityDimId + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
		if (UtilValidate.isNotEmpty(productDimIds)) {
            String condIn = condInFromlist(productDimIds);
            conOpeningStock.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
		}

		queryOpening.where(conOpeningStock);
		queryOpening.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");

		OlbiusQuery queryExport = new OlbiusQuery(getSQLProcessor());//Phat sinh xuat
		queryExport.from("inventory_item_fact","itf");
		queryExport.select("itf.product_dim_id");
		queryExport.select("itf.facility_dim_id");
		queryExport.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryExport.select("CASE WHEN prd.require_amount = 'Y' THEN -sum(itf.amount_on_hand_total) ELSE -sum(itf.quantity_on_hand_total) END", "exportQuantity");
		queryExport.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
		Condition conExport = new Condition();
        conExport = Condition.make("itf.inventory_type='EXPORT'");
        conExport.and(Condition.make("itf.inventory_date_dim_id BETWEEN " + getSqlTime(getFromDate()) + " AND " + getSqlTime(getThruDate())));
        if (UtilValidate.isNotEmpty(facilityDimId)) {
            conExport.and(Condition.make("itf.facility_dim_id='" + facilityDimId + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(productDimIds)) {
            String condIn = condInFromlist(productDimIds);
            conExport.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
		queryExport.where(conExport);
		queryExport.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");

		OlbiusQuery queryImport = new OlbiusQuery();//Phat sinh nhap
		queryImport.from("inventory_item_fact","itf");
		queryImport.select("itf.product_dim_id");
		queryImport.select("itf.facility_dim_id");
		queryImport.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryImport.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "importQuantity");
		queryImport.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
		Condition conImport = new Condition();
        conImport = Condition.make("itf.inventory_type='RECEIVE'");
        conImport.and(Condition.make("itf.inventory_date_dim_id BETWEEN " + getSqlTime(getFromDate()) + " AND " + getSqlTime(getThruDate())));
        if (UtilValidate.isNotEmpty(facilityDimId)) {
            conImport.and(Condition.make("itf.facility_dim_id='" + facilityDimId + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(productDimIds)) {
            String condIn = condInFromlist(productDimIds);
            conImport.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
		queryImport.where(conImport);
		queryImport.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");
		
		OlbiusQuery queryEnding = new OlbiusQuery();// ton kho cuoi ky
		queryEnding.from("inventory_item_fact","itf");
		queryEnding.select("itf.product_dim_id");
		queryEnding.select("itf.facility_dim_id");
		queryEnding.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryEnding.select("CASE WHEN prd.require_amount = 'Y' THEN sum(itf.amount_on_hand_total) ELSE sum(itf.quantity_on_hand_total) END", "endingQuantity");
		queryEnding.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = itf.product_dim_id");
		Condition condEndingStock = Condition.make("itf.inventory_date_dim_id <= " + getSqlTime(getThruDate()));
        if (UtilValidate.isNotEmpty(facilityDimId)) {
            condEndingStock.and(Condition.make("itf.facility_dim_id='" + facilityDimId + "'")); //itf.inventory_date_dim_id  <= 1515085200 AND (itf.facility_dim_id = '48')
        }
        if (UtilValidate.isNotEmpty(productDimIds)) {
            String condIn = condInFromlist(productDimIds);
            condEndingStock.and(Condition.make("itf.product_dim_id IN (" + condIn + ")"));
        }
		queryEnding.where(condEndingStock);
		queryEnding.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id, prd.require_amount");
		
		/**========== join query =============**/
		queryOpEx.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Expr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
		queryOpEx.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Expr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
		queryOpEx.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Expr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
		queryOpEx.select("Openi.openingQuantity", "openingQuantity");
		queryOpEx.select("Expr.exportQuantity", "exportQuantity");
		queryOpEx.from(queryOpening, "Openi");
		queryOpEx.join(Join.LEFT_OUTER_JOIN, queryExport, "Expr" ,"Openi.product_dim_id = Expr.product_dim_id AND Openi.quantity_uom_dim_id = Expr.quantity_uom_dim_id AND Openi.facility_dim_id = Expr.facility_dim_id");
		OlbiusQuery tempOpEx = new OlbiusQuery();
        tempOpEx.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Expr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        tempOpEx.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Expr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        tempOpEx.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Expr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        tempOpEx.select("Openi.openingQuantity", "openingQuantity");
        tempOpEx.select("Expr.exportQuantity", "exportQuantity");
        tempOpEx.from(queryOpening, "Openi");
        tempOpEx.join(Join.RIGHT_OUTER_JOIN, queryExport, "Expr" ,"Openi.product_dim_id = Expr.product_dim_id AND Openi.quantity_uom_dim_id = Expr.quantity_uom_dim_id AND Openi.facility_dim_id = Expr.facility_dim_id");
        OlbiusQuery tempOpExJoin = new OlbiusQuery();
        tempOpExJoin.select("*").from("((" + queryOpEx + ") union (" + tempOpEx + "))", "tempOpExJoin");

        queryOpExIm.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Impr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
		queryOpExIm.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Impr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
		queryOpExIm.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Impr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
		queryOpExIm.select("Openi.openingQuantity", "openingQuantity");
		queryOpExIm.select("Openi.exportQuantity", "exportQuantity");
		queryOpExIm.select("Impr.importQuantity", "importQuantity");
		queryOpExIm.from(tempOpExJoin, "Openi");
		queryOpExIm.join(Join.LEFT_OUTER_JOIN, queryImport,  "Impr" ,"Openi.product_dim_id = Impr.product_dim_id AND Openi.quantity_uom_dim_id = Impr.quantity_uom_dim_id AND Openi.facility_dim_id = Impr.facility_dim_id");
        OlbiusQuery tempOpExIm = new OlbiusQuery();
        tempOpExIm.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Impr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        tempOpExIm.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Impr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        tempOpExIm.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Impr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        tempOpExIm.select("Openi.openingQuantity", "openingQuantity");
        tempOpExIm.select("Openi.exportQuantity", "exportQuantity");
        tempOpExIm.select("Impr.importQuantity", "importQuantity");
        tempOpExIm.from(tempOpExJoin, "Openi");
        tempOpExIm.join(Join.RIGHT_OUTER_JOIN, queryImport,  "Impr" ,"Openi.product_dim_id = Impr.product_dim_id AND Openi.quantity_uom_dim_id = Impr.quantity_uom_dim_id AND Openi.facility_dim_id = Impr.facility_dim_id");
        OlbiusQuery tempOpExImJoin = new OlbiusQuery();
        tempOpExImJoin.select("*").from("((" + queryOpExIm + ") union (" + tempOpExIm + "))", "tempOpExImJoin");

        queryIMEP.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Endi.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
		queryIMEP.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Endi.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
		queryIMEP.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Endi.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
		queryIMEP.select("Openi.openingQuantity", "openingQuantity");
		queryIMEP.select("Openi.exportQuantity", "exportQuantity");
		queryIMEP.select("Openi.importQuantity", "importQuantity");
		queryIMEP.select("Endi.endingQuantity", "endingQuantity");
		queryIMEP.from(tempOpExImJoin, "Openi");
		queryIMEP.join(Join.LEFT_OUTER_JOIN, queryEnding, "Endi", "Openi.product_dim_id = Endi.product_dim_id AND Openi.quantity_uom_dim_id = Endi.quantity_uom_dim_id AND Openi.facility_dim_id = Endi.facility_dim_id");
        OlbiusQuery tempIMEP = new OlbiusQuery();
        tempIMEP.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Endi.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        tempIMEP.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Endi.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
        tempIMEP.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Endi.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        tempIMEP.select("Openi.openingQuantity", "openingQuantity");
        tempIMEP.select("Openi.exportQuantity", "exportQuantity");
        tempIMEP.select("Openi.importQuantity", "importQuantity");
        tempIMEP.select("Endi.endingQuantity", "endingQuantity");
        tempIMEP.from(tempOpExImJoin, "Openi");
        tempIMEP.join(Join.RIGHT_OUTER_JOIN, queryEnding, "Endi", "Openi.product_dim_id = Endi.product_dim_id AND Openi.quantity_uom_dim_id = Endi.quantity_uom_dim_id AND Openi.facility_dim_id = Endi.facility_dim_id");
        OlbiusQuery tempIMEPJoin = new OlbiusQuery();
        tempIMEPJoin.select("*").from("((" + queryIMEP + ") union (" + tempIMEP + "))", "tempIMEPJoin");

        OlbiusQuery queryTmp = new OlbiusQuery();
		queryTmp.select("importQuantity")
		.select("exportQuantity")
		.select("endingQuantity")
		.select("openingQuantity")
		.select("uom_dimension.uom_id", "quantity_uom_id")
		.select("facility_dimension.facility_id", "facility_id")
		.select("facility_dimension.facility_name", "facility_name")
		.select("product_dimension.product_id", "product_id")
		.select("product_dimension.product_code", "product_code")
		.select("product_dimension.product_name", "product_name");
		queryTmp.from(tempIMEPJoin, "IMEP");
		queryTmp.join(Join.INNER_JOIN, "product_dimension","IMEP.product_dim_id = product_dimension.dimension_id");
		queryTmp.join(Join.INNER_JOIN, "facility_dimension","IMEP.facility_dim_id = facility_dimension.dimension_id");
		queryTmp.join(Join.INNER_JOIN, "uom_dimension", "IMEP.quantity_uom_dim_id = uom_dimension.dimension_id");
        queryTmp.groupBy("product_dimension.dimension_id");
		
		OlbiusQuery queryAmountOpening = new OlbiusQuery();
		queryAmountOpening.select("facility_id").select("product_id").select("(sum(dr_amount) - sum(cr_amount))", "openingAmount")
			.from("inventory_product_cost_fact");
		Condition condAmountOpening = Condition.make("date_value  < " + "'" + getFromDate() + "'");
		if (UtilValidate.isNotEmpty(facilityDimId)) {
			condAmountOpening.and(Condition.makeEQ("facility_id", facilityId));
		}
		if (UtilValidate.isNotEmpty(productDimIds)) {
			condAmountOpening.and(Condition.makeIn("product_id", productIds));
		}
		queryAmountOpening.where(condAmountOpening);
		queryAmountOpening.groupBy("facility_id").groupBy("product_id");
		
		OlbiusQuery queryAmountImExEnding = new OlbiusQuery();
		queryAmountImExEnding.select("facility_id").select("product_id").select("sum(dr_amount)", "importAmount").select("sum(cr_amount)", "exportAmount")
			.from("inventory_product_cost_fact");
		Condition condAmountImExEnding = Condition.makeBetween("date_value", getFromDate(), getThruDate());
		if (UtilValidate.isNotEmpty(facilityDimId)) {
			condAmountImExEnding.and(Condition.makeEQ("facility_id", facilityId));
		}
		if (UtilValidate.isNotEmpty(productDimIds)) {
			condAmountImExEnding.and(Condition.makeIn("product_id", productIds));
		}
		queryAmountImExEnding.where(condAmountImExEnding);
		queryAmountImExEnding.groupBy("facility_id").groupBy("product_id");
		
		OlbiusQuery queryAmountEnding = new OlbiusQuery();
		queryAmountEnding.select("facility_id").select("product_id").select("(sum(dr_amount) - sum(cr_amount))", "endingAmount")
			.from("inventory_product_cost_fact");
		Condition condAmountEnding = Condition.make("date_value  <= " + "'" + getThruDate() + "'");
		if (UtilValidate.isNotEmpty(facilityDimId)) {
			condAmountEnding.and(Condition.makeEQ("facility_id", facilityId));
		}
		if (UtilValidate.isNotEmpty(productDimIds)) {
			condAmountEnding.and(Condition.makeIn("product_id", productIds));
		}
		queryAmountEnding.where(condAmountEnding);
		queryAmountEnding.groupBy("facility_id").groupBy("product_id");		
		
		
		query.select("tmp.facility_id").select("facility_name").select("tmp.product_id").select("product_code").select("product_name")
			.select("quantity_uom_id").select("openingQuantity").select("importQuantity").select("exportQuantity").select("endingQuantity")
			.select("CASE WHEN v1.openingAmount IS NULL THEN 0 ELSE v1.openingAmount END AS openingAmount")
			.select("CASE WHEN v2.importAmount IS NULL THEN 0 ELSE v2.importAmount END AS importAmount")
			.select("CASE WHEN v2.exportAmount IS NULL THEN 0 ELSE v2.exportAmount END AS exportAmount")
			.select("CASE WHEN v3.endingAmount IS NULL THEN 0 ELSE v3.endingAmount END AS endingAmount");
		query.from(queryTmp, "tmp");
		query.join(Join.LEFT_OUTER_JOIN, queryAmountOpening, "v1", "v1.product_id = tmp.product_id AND v1.facility_id = tmp.facility_id");
		query.join(Join.LEFT_OUTER_JOIN, queryAmountImExEnding, "v2", "v2.product_id = tmp.product_id AND v2.facility_id = tmp.facility_id");
		query.join(Join.LEFT_OUTER_JOIN, queryAmountEnding, "v3", "v3.product_id = tmp.product_id AND v3.facility_id = tmp.facility_id");
		
		return query;
	}

	@Override
	public void prepareResultGrid() {
		addDataField("productId", "product_id");
		addDataField("productCode", "product_code");
		addDataField("productName", "product_name");
		addDataField("facilityId", "facility_id");
		addDataField("facilityName", "facility_name");
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