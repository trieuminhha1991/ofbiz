package com.olbius.acc.report.olap;

import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import java.math.BigDecimal;

public class ImpExpStockWarehouseOlapImp extends OlbiusBuilder{
	public static final String PRODUCT_ID = "PRODUCT_ID";
	public static final String FACILITY_ID = "FACILITY_ID";
	private OlbiusQuery query;
	private Delegator delegator;
	
	public ImpExpStockWarehouseOlapImp(Delegator delegator) {
		super(delegator);
		this.delegator = delegator;
	}	
	
	public Delegator getDelegator() {
		return delegator;
	}

	@Override
	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}
	
	private Object getFacilities(String object) {
		Long tmp = null;
		if (object != null) {
				tmp = FacilityDimension.D.getId(delegator, object);
		}
		return tmp;
	}
	
	private Object getProducts(Object object) {
		Long tmp = null;
		if (object != null) {
				tmp = ProductDimension.D.getId(delegator, (String) object);
		}
		return tmp;
	}
	
	private void initQuery(){
		Object productId = getProducts( getParameter(PRODUCT_ID));
		Object facilityId = getFacilities((String) getParameter(FACILITY_ID));

		query = new OlbiusQuery(getSQLProcessor());
		
		OlbiusQuery queryOpEx = new OlbiusQuery();
		OlbiusQuery queryOpExIm = new OlbiusQuery();
		OlbiusQuery queryIMEP = new OlbiusQuery(getSQLProcessor());
		
		OlbiusQuery queryOpening = new OlbiusQuery();//ton kho dau ki
		queryOpening.from("inventory_item_fact","itf");
		queryOpening.select("itf.product_dim_id");
		queryOpening.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryOpening.select("sum(itf.quantity_on_hand_total)","openingQuantity");
		queryOpening.select("(SUM(pacf.average_cost * itf.quantity_on_hand_total) + SUM(pacf.average_pur_cost*itf.quantity_on_hand_total))","openingAmount");
		queryOpening.select("itf.facility_dim_id");
		queryOpening.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf","itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and ((itf.inventory_date_dim_id  between pacf.from_date_dim and pacf.thru_date_dim) or (itf.inventory_date_dim_id  > pacf.from_date_dim and pacf.thru_date_dim = -1))");
		Condition conOpeningStock = new Condition();
		conOpeningStock = Condition.make("itf.inventory_date_dim_id  <= " + getSqlTime(getFromDate()));
		if (facilityId != null){
			conOpeningStock.and(Condition.make("itf.facility_dim_id='" + facilityId + "'"));
		}
		if (productId != null){
			conOpeningStock.and(Condition.make("itf.product_dim_id='" + productId + "'"));
		}
		queryOpening.where(conOpeningStock);
		queryOpening.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id");

		OlbiusQuery queryExport = new OlbiusQuery(getSQLProcessor());//Phat sinh xuat
		queryExport.from("inventory_item_fact","itf");
		queryExport.select("itf.product_dim_id");
		queryExport.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryExport.select("-sum(itf.quantity_on_hand_total)", "exportQuantity");
		queryExport.select("-(SUM(pacf.average_cost*itf.quantity_on_hand_total) + SUM(pacf.average_pur_cost*itf.quantity_on_hand_total))", "exportAmount");
		queryExport.select("itf.facility_dim_id");
		queryExport.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf","itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and ((itf.inventory_date_dim_id between pacf.from_date_dim and pacf.thru_date_dim) or (itf.inventory_date_dim_id > pacf.from_date_dim and pacf.thru_date_dim = -1))");
		Condition conExport = new Condition();
		conExport = Condition.make("itf.inventory_type='EXPORT'");
        conExport.and(Condition.make("itf.inventory_date_dim_id BETWEEN " + getSqlTime(getFromDate()) + " AND " + getSqlTime(getThruDate())));
        if (facilityId != null){
            conExport.and(Condition.make("itf.facility_dim_id='" + facilityId + "'"));
        }
        if (productId != null){
            conExport.and(Condition.make("itf.product_dim_id='" + productId + "'"));
        }
		queryExport.where(conExport);
		queryExport.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id");

		OlbiusQuery queryImport = new OlbiusQuery();//Phat sinh nhap
		queryImport.from("inventory_item_fact","itf");
		queryImport.select("itf.product_dim_id");
		queryImport.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryImport.select("sum(quantity_on_hand_total)","importQuantity");
		queryImport.select("(SUM(pacf.average_cost*itf.quantity_on_hand_total) + SUM(pacf.average_pur_cost*itf.quantity_on_hand_total))","importAmount");
		queryImport.select("itf.facility_dim_id");
		queryImport.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf","itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and ((itf.inventory_date_dim_id between pacf.from_date_dim and pacf.thru_date_dim) or (itf.inventory_date_dim_id > pacf.from_date_dim and pacf.thru_date_dim = -1))");
		Condition conImport = new Condition();
        conImport = Condition.make("itf.inventory_type='RECEIVE'");
        conImport.and(Condition.make("itf.inventory_date_dim_id BETWEEN " + getSqlTime(getFromDate()) + " AND " + getSqlTime(getThruDate())));
        if (facilityId != null){
            conImport.and(Condition.make("itf.facility_dim_id='" + facilityId + "'"));
        }
        if (productId != null){
            conImport.and(Condition.make("itf.product_dim_id='" + productId + "'"));
        }
		queryImport.where(conImport);
		queryImport.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id");
		
		OlbiusQuery queryEnding = new OlbiusQuery();// ton kho cuoi ky
		queryEnding.from("inventory_item_fact","itf");
		queryEnding.select("itf.product_dim_id");
		queryEnding.select("itf.quantity_uom_dim_id", "quantity_uom_dim_id");
		queryEnding.select("sum(itf.quantity_on_hand_total)","endingQuantity");
		queryEnding.select("(SUM(pacf.average_cost * itf.quantity_on_hand_total) + SUM(pacf.average_pur_cost * itf.quantity_on_hand_total))","endingAmount");
		queryEnding.select("itf.facility_dim_id");
		queryEnding.join(Join.LEFT_OUTER_JOIN, "product_average_cost_fact as pacf", "itf.facility_dim_id = pacf.facility_dim_id and itf.product_dim_id = pacf.product_dim_id and ((itf.inventory_date_dim_id between pacf.from_date_dim and pacf.thru_date_dim) or (itf.inventory_date_dim_id > pacf.from_date_dim and pacf.thru_date_dim = -1))");
		Condition condEndingStock = Condition.make("itf.inventory_date_dim_id <= " + getSqlTime(getThruDate()));
        if (facilityId != null){
            condEndingStock.and(Condition.make("itf.facility_dim_id='" + facilityId + "'"));
        }
        if (productId != null){
            condEndingStock.and(Condition.make("itf.product_dim_id='" + productId + "'"));
        }
		queryEnding.where(condEndingStock);
		queryEnding.groupBy("itf.product_dim_id, itf.facility_dim_id, itf.quantity_uom_dim_id");
		
		/**========== join query =============**/
		queryOpEx.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Expr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
		queryOpEx.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Expr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
//		queryOpEx.select("CASE WHEN Openi.currency_id IS NULL THEN  Expr.currency_id ELSE Openi.currency_id END AS currency_id");
		queryOpEx.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Expr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
		queryOpEx.select("Openi.openingQuantity", "openingQuantity");
		queryOpEx.select("Openi.openingAmount", "openingAmount");
		queryOpEx.select("Expr.exportQuantity", "exportQuantity");
		queryOpEx.select("Expr.exportAmount", "exportAmount");
		queryOpEx.from(queryOpening, "Openi");
		queryOpEx.join(Join.LEFT_OUTER_JOIN, queryExport, "Expr" ,"Openi.product_dim_id = Expr.product_dim_id AND Openi.quantity_uom_dim_id = Expr.quantity_uom_dim_id AND Openi.facility_dim_id = Expr.facility_dim_id");
		OlbiusQuery temp = new OlbiusQuery();
        temp.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Expr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        temp.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Expr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
//		temp.select("CASE WHEN Openi.currency_id IS NULL THEN  Expr.currency_id ELSE Openi.currency_id END AS currency_id");
        temp.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Expr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        temp.select("Openi.openingQuantity", "openingQuantity");
        temp.select("Openi.openingAmount", "openingAmount");
        temp.select("Expr.exportQuantity", "exportQuantity");
        temp.select("Expr.exportAmount", "exportAmount");
        temp.from(queryOpening, "Openi");
        temp.join(Join.RIGHT_OUTER_JOIN, queryExport, "Expr" ,"Openi.product_dim_id = Expr.product_dim_id AND Openi.quantity_uom_dim_id = Expr.quantity_uom_dim_id AND Openi.facility_dim_id = Expr.facility_dim_id");
        OlbiusQuery tempOpEx = new OlbiusQuery();
        tempOpEx.select("*").from("((" + queryOpEx + ") union all (" + temp + "))", "tempOpEx");

        queryOpExIm.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Impr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
		queryOpExIm.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Impr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
//		queryOpExIm.select("CASE WHEN Openi.currency_id IS NULL THEN  Impr.currency_id ELSE Openi.currency_id END AS currency_id");
		queryOpExIm.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Impr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
		queryOpExIm.select("Openi.openingQuantity", "openingQuantity");
		queryOpExIm.select("Openi.openingAmount", "openingAmount");
		queryOpExIm.select("Openi.exportQuantity", "exportQuantity");
		queryOpExIm.select("Openi.exportAmount", "exportAmount");
		queryOpExIm.select("Impr.importQuantity", "importQuantity");
		queryOpExIm.select("Impr.importAmount", "importAmount");
		queryOpExIm.from(tempOpEx, "Openi");
		queryOpExIm.join(Join.LEFT_OUTER_JOIN, queryImport,  "Impr" ,"Openi.product_dim_id = Impr.product_dim_id AND Openi.quantity_uom_dim_id = Impr.quantity_uom_dim_id AND Openi.facility_dim_id = Impr.facility_dim_id");
		OlbiusQuery temp1 = new OlbiusQuery();
        temp1.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Impr.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        temp1.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Impr.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
//		temp1.select("CASE WHEN Openi.currency_id IS NULL THEN  Impr.currency_id ELSE Openi.currency_id END AS currency_id");
        temp1.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Impr.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        temp1.select("Openi.openingQuantity", "openingQuantity");
        temp1.select("Openi.openingAmount", "openingAmount");
        temp1.select("Openi.exportQuantity", "exportQuantity");
        temp1.select("Openi.exportAmount", "exportAmount");
        temp1.select("Impr.importQuantity", "importQuantity");
        temp1.select("Impr.importAmount", "importAmount");
        temp1.from(tempOpEx, "Openi");
        temp1.join(Join.RIGHT_OUTER_JOIN, queryImport,  "Impr" ,"Openi.product_dim_id = Impr.product_dim_id AND Openi.quantity_uom_dim_id = Impr.quantity_uom_dim_id AND Openi.facility_dim_id = Impr.facility_dim_id");
        OlbiusQuery tempOpExIm = new OlbiusQuery();
        tempOpExIm.select("*").from("((" + queryOpExIm + ") union all (" + temp1 + "))", "tempOpExIm");

        queryIMEP.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Endi.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
		queryIMEP.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Endi.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
//		query.select("CASE WHEN Openi.currency_id IS NULL THEN  Endi.currency_id ELSE Openi.currency_id END AS currency_id");
		queryIMEP.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Endi.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
		queryIMEP.select("Openi.openingQuantity", "openingQuantity");
		queryIMEP.select("Openi.openingAmount", "openingAmount");
		queryIMEP.select("Openi.exportQuantity", "exportQuantity");
		queryIMEP.select("Openi.exportAmount", "exportAmount");
		queryIMEP.select("Openi.importQuantity", "importQuantity");
		queryIMEP.select("Openi.importAmount", "importAmount");
		queryIMEP.select("Endi.endingQuantity", "endingQuantity");
		queryIMEP.select("Endi.endingAmount", "endingAmount");		
		queryIMEP.from(tempOpExIm, "Openi");
		queryIMEP.join(Join.LEFT_OUTER_JOIN, queryEnding, "Endi", "Openi.product_dim_id = Endi.product_dim_id AND Openi.quantity_uom_dim_id = Endi.quantity_uom_dim_id AND Openi.facility_dim_id = Endi.facility_dim_id");

        OlbiusQuery temp2 = new OlbiusQuery();
        temp2.select("CASE WHEN Openi.facility_dim_id IS NULL THEN  Endi.facility_dim_id ELSE Openi.facility_dim_id END AS facility_dim_id");
        temp2.select("CASE WHEN Openi.quantity_uom_dim_id IS NULL THEN  Endi.quantity_uom_dim_id ELSE Openi.quantity_uom_dim_id END AS quantity_uom_dim_id");
//		temp2.select("CASE WHEN Openi.currency_id IS NULL THEN  Endi.currency_id ELSE Openi.currency_id END AS currency_id");
        temp2.select("CASE WHEN Openi.product_dim_id IS NULL THEN  Endi.product_dim_id ELSE Openi.product_dim_id END AS product_dim_id");
        temp2.select("Openi.openingQuantity", "openingQuantity");
        temp2.select("Openi.openingAmount", "openingAmount");
        temp2.select("Openi.exportQuantity", "exportQuantity");
        temp2.select("Openi.exportAmount", "exportAmount");
        temp2.select("Openi.importQuantity", "importQuantity");
        temp2.select("Openi.importAmount", "importAmount");
        temp2.select("Endi.endingQuantity", "endingQuantity");
        temp2.select("Endi.endingAmount", "endingAmount");
        temp2.from(tempOpExIm, "Openi");
        temp2.join(Join.RIGHT_OUTER_JOIN, queryEnding, "Endi", "Openi.product_dim_id = Endi.product_dim_id AND Openi.quantity_uom_dim_id = Endi.quantity_uom_dim_id AND Openi.facility_dim_id = Endi.facility_dim_id");
        OlbiusQuery tempIMEP = new OlbiusQuery();
        tempIMEP.select("*").from("((" + queryIMEP + ") union all (" + temp2 + "))", "tempIMEP");

        query.select("importAmount")
		.select("importQuantity")
		.select("exportAmount")
		.select("exportQuantity")
		.select("endingQuantity")
		.select("endingAmount")
		.select("openingAmount")
		.select("openingQuantity")
		.select("uom_dimension.uom_id", "quantity_uom_id")
		.select("facility_dimension.facility_id", "facility_id")
		.select("facility_dimension.facility_name", "facility_name")
		.select("product_dimension.product_id", "product_id")
		.select("product_dimension.product_code", "product_code")
		.select("product_dimension.product_name", "product_name");
		query.from(tempIMEP, "IMEP");
		query.join(Join.INNER_JOIN, "product_dimension","IMEP.product_dim_id = product_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "facility_dimension","IMEP.facility_dim_id = facility_dimension.dimension_id");
		query.join(Join.INNER_JOIN, "uom_dimension", "IMEP.quantity_uom_dim_id = uom_dimension.dimension_id");
		query.groupBy("product_dimension.dimension_id");
	}
	
	@Override
	protected OlbiusQuery getQuery() {
		if(query == null) {
			initQuery();
		}
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

		addDataField("openingQuantity", "openingQuantity", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});	
		addDataField("openingAmount", "openingAmount", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("importQuantity", "importQuantity", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});		
		addDataField("importAmount", "importAmount", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("exportQuantity", "exportQuantity", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("exportAmount", "exportAmount", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("endingQuantity", "endingQuantity", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
		addDataField("endingAmount", "endingAmount", new ReturnResultCallback<BigDecimal>() {
			@Override
			public BigDecimal get(Object object) {
				if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
			}
		});
	}	
}