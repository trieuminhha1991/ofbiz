package com.olbius.salesmtl.report;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;

import com.olbius.basesales.report.ResultProductStore;
import com.olbius.bi.olap.OlbiusBuilder;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class SynthesisRevenueImpl extends OlbiusBuilder{
	public static final String ORG = "ORG";
	public static final String LEVEL = "LEVEL";
	public static final String FLAG_CHILD = "FLAG_CHILD";
	public static final String FLAG_PARENT = "FLAG_PARENT";
	public static final String DEP_ID = "DEP_ID";
	public static final String MONTHH = "MONTHH";
	public static final String YEARR = "YEARR";
	public static final String LIMIT_SALESMAN = "LIMIT_SALESMAN";
	public static final String FLAG_TYPE_PERSON = "FLAG_TYPE_PERSON";
	
	public SynthesisRevenueImpl(Delegator delegator) {
		super(delegator);
	}
	
	private List<Map<String,String>> type;
	
	private List<Map<String,String>> getType() {
		if(type == null) {
			ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
			type = enumType.getListResultStore();
		}
		return type;
	}
	
	private OlbiusQuery query;
	private OlbiusQuery fromQuery;
	
	@Override
	public void prepareResultGrid() {
//		Boolean flagChild = (Boolean) getParameter(FLAG_CHILD);
		addDataField("depName", "name1");
		addDataField("depId", "party_id");
//		addDataField("depCode", "party_code");
		List<Map<String,String>> type = getType();
		for(int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");			
			addDataField(productId+"_q", productId+"_q", new ReturnResultCallback<Object>() { 
				@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
					return (BigDecimal) object;}else{return "-";}}
			});
			addDataField(productId+"_t", productId+"_t", new ReturnResultCallback<Object>() { 
				@Override public Object get(Object object) {if(UtilValidate.isNotEmpty(object) && !object.equals(new BigDecimal(0))){
					return (BigDecimal) object;}else{return "-";}}
			});
		}
	}
	
	@Override
	protected OlapQuery getQuery() {
		if(query == null) {
			initQuery();
		}
		return query;
	}
	
	private void initQuery() {
		Condition condition = new Condition();
		query = new OlbiusQuery(getSQLProcessor());
		fromQuery = new OlbiusQuery(getSQLProcessor());
		String depId = (String) getParameter(DEP_ID);
		Boolean flagChild = (Boolean) getParameter(FLAG_CHILD);
		String flagParent = (String) getParameter(FLAG_PARENT);
		
		fromQuery.select("pd.party_id", flagChild == false).select("pd.name", "name1", flagChild == false).select("pd.party_code", flagChild == false).select("prd.product_id")
		.select("sum(sof.quantity)", "volume").select("sum(sof.total)", "valuei")
		.select("pd2.party_id", flagChild == true).select("case when pd2.name is null then pd2.description else pd2.name end as name1", flagChild == true).select("pd2.party_code", flagChild == true)
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "level_relationship", "lr", "lr.salesman_id = sof.sale_executive_party_dim_id")
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.dsa_dep", flagChild == false)
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.dsa_dep", flagChild == true && "DSAFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = lr.csm_dep", flagChild == true && "DSAFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.csm_dep", flagChild == true && "CSMFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = lr.rsm_dep", flagChild == true && "CSMFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.rsm_dep", flagChild == true && "RSMFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = lr.asm_dep", flagChild == true && "RSMFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.asm_dep", flagChild == true && "ASMFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = lr.sup_dep", flagChild == true && "ASMFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd", "pd.dimension_id = lr.sup_dep", flagChild == true && "SUPFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "party_dimension", "pd2", "pd2.dimension_id = lr.salesman_id", flagChild == true && "SUPFlag".equals(flagParent))
		.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = sof.product_dim_id")
		.join(Join.INNER_JOIN, "product_promo_dimension", "ppd", "ppd.dimension_id = sof.discount_dim_id")
		.where(condition)
		.groupBy("pd.party_id", flagChild == false).groupBy("pd.party_code", flagChild == false).groupBy("prd.product_id").groupBy("pd.name", flagChild == false)
		.groupBy("pd2.party_id", flagChild == true).groupBy("pd2.party_code", flagChild == true).groupBy("pd2.description", flagChild == true).groupBy("pd2.name", flagChild == true);
		
		condition.and("sof.order_item_status <> 'ITEM_CANCELLED'").and("ppd.product_promo_id IS NULL").andEQ("pd.party_id", depId, UtilValidate.isNotEmpty(depId));
		
		query.select("tmp.party_id").select("tmp.party_code").select("tmp.name1")
		.from(fromQuery, "tmp")
		.groupBy("tmp.party_code").groupBy("tmp.party_id").groupBy("tmp.name1");
		
		List<Map<String,String>> type = getType();
		
		for(int i = 0; i < type.size(); i++) {
			String productId = (String) type.get(i).get("product_id");
			query.select("sum(case when TMP.product_id = '"+productId+"' then TMP.volume else 0 end)", '"' + productId +"_q"+ '"');
			query.select("sum(case when TMP.product_id = '"+productId+"' then TMP.valuei else 0 end)", '"' + productId +"_t"+ '"');
		}
		
	}
}
