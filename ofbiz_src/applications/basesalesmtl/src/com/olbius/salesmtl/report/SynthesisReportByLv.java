package com.olbius.salesmtl.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.report.ResultProductStore;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import com.olbius.entity.cache.OlbiusCache;

import javolution.util.FastList;

public class SynthesisReportByLv extends OlbiusOlapService {

	private OlbiusQuery query;

	@Override
	public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String dsaId = userLogin.getString("partyId");
		List<String> dep = FastList.newInstance();
		try {
			dep = PartyUtil.getDepartmentOfEmployee(delegator, dsaId, UtilDateTime.nowTimestamp());
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String flag2 = null;
		String depp = dep.get(0);
		String levelId = (String) context.get("levelId");
		Boolean flag = true;
		
		if(UtilValidate.isNotEmpty(levelId)){
			if(levelId.contains("CSM")) {
				flag2 = "CSMFlag";
			} else if(levelId.contains("RSM")) {
				flag2 = "RSMFlag";
			}else if(levelId.contains("ASM")) {
				flag2 = "ASMFlag";
			} else if(levelId.contains("SUP")) {
				flag2 = "SUPFlag";
			} else if(levelId.contains("DSA")) {
				flag2 = "DSAFlag";
			}
		} else {
			if(dsaId.contains("CSM")) {
				flag2 = "CSMFlag";
			} else if(dsaId.contains("RSM")) {
				flag2 = "RSMFlag";
			}else if(dsaId.contains("ASM")) {
				flag2 = "ASMFlag";
			} else if(dsaId.contains("SUP")) {
				flag2 = "SUPFlag";
			} else if(dsaId.contains("DSA")) {
				flag2 = "DSAFlag";
			}
		}
		
		if(UtilValidate.isNotEmpty(levelId)){
			putParameter("depId", levelId);
			flag = true;
		} else {
			if(depp.contains("DSA")){
				putParameter("depId", depp);
				flag = false;
			} else {
				putParameter("depId", levelId);
				flag = true;
			}
		}
		putParameter("flagChild", flag);
		putParameter("flagParent", flag2);
		setFromDate((Date) context.get("fromDate"));
		setThruDate((Date) context.get("thruDate"));
		
		putParameter("dateType", context.get("dateType"));
		putParameter("product", context.get("product[]"));
		putParameter("salesChannel", context.get("salesChannel[]"));
		putParameter("channelType", context.get("channelType[]"));
//		putParameter("product2", context.get("product[]"));
//		putParameter("salesChannel2", context.get("salesChannel[]"));
//		putParameter("channelType2", context.get("channelType[]"));
		putParameter("levelId", levelId);
	}

	@Override
	protected OlapQuery getQuery() {
		if (query == null) {
			query = init();
		}
		return query;
	}
	
	private List<Map<String,String>> type;
	
	private List<Map<String,String>> getType() {
		if(type == null) {
			ResultProductStore enumType = new ResultProductStore(getSQLProcessor());
			type = enumType.getListResultStore();
		}
		return type;
	}

	private static final OlbiusCache<Long> SALES_CHANNEL = new OlbiusCache<Long>() {
		
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("ProductStoreDimension", UtilMisc.toMap("productStoreId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}
		
	};
	
	private static final OlbiusCache<Long> CHANNEL_TYPE = new OlbiusCache<Long>() {
		
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("EnumerationDimension", UtilMisc.toMap("enumId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}
		
	};

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
	
	private List<Object> getSalesChannel(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = SALES_CHANNEL.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	private List<Object> getChannelType(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = CHANNEL_TYPE.get(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
		}
		return tmp;
	}
	
	@SuppressWarnings("unchecked")
	private OlbiusQuery init() {
		OlbiusQuery query0 = makeQuery();
		OlbiusQuery fromQuery = makeQuery();

		List<Object> products = getProducts((List<?>) getParameter("product"));
		Boolean productFlag = false;
		if(!products.isEmpty()){
			productFlag = true;
		}
		
		List<Object> salesChannel = getSalesChannel((List<?>) getParameter("salesChannel"));
		Boolean salesChannelFlag = false;
		if(!salesChannel.isEmpty()){
			salesChannelFlag = true;
		}
		List<Object> channelType = getChannelType((List<?>) getParameter("channelType"));
		Boolean channelTypeFlag = false;
		if(!channelType.isEmpty()){
			channelTypeFlag = true;
		}
		
		List<String> products2 = (List<String>) getParameter("product");
		Condition condition = new Condition();
		String depId = (String) getParameter("depId");
		Boolean flagChild = (Boolean) getParameter("flagChild");
		String flagParent = (String) getParameter("flagParent");
		
		fromQuery.select("pd.party_id", flagChild == false).select("pd.name", "name1", flagChild == false).select("pd.party_code", flagChild == false).select("prd.product_id")
		.select("sum(sof.quantity)", "volume").select("sum(sof.total)", "valuei")
		.select("pd2.party_id", flagChild == true).select("case when pd2.name is null then pd2.description else pd2.name end as name1", flagChild == true).select("pd2.party_code", flagChild == true)
		.from("sales_order_fact", "sof")
		.join(Join.INNER_JOIN, "level_relationship", "lr", "lr.salesman_id = sof.sale_executive_party_dim_id")
		.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
		.join(Join.INNER_JOIN, "product_store_dimension", "psd", "psd.dimension_id = sof.product_store_dim_id", salesChannelFlag)
		.join(Join.INNER_JOIN, "enumeration_dimension", "ed", "ed.dimension_id = sof.sales_method_channel_enum_dim_id", channelTypeFlag)
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
		
		condition.and("sof.order_item_status <> 'ITEM_CANCELLED'")
		.and("ppd.product_promo_id IS NULL")
		.andEQ("pd.party_id", depId, UtilValidate.isNotEmpty(depId))
		.andBetween("dd.dimension_id", getSqlTime(getFromDate()), getSqlTime(getThruDate()))
		.andIn("prd.dimension_id", products, productFlag)
		.andIn("psd.dimension_id", salesChannel, salesChannelFlag)
		.andIn("ed.dimension_id", channelType, channelTypeFlag);
		
		query0.select("tmp.party_id").select("tmp.party_code").select("tmp.name1")
		.from(fromQuery, "tmp")
		.groupBy("tmp.party_code").groupBy("tmp.party_id").groupBy("tmp.name1");
		
		if(!productFlag){
			List<Map<String,String>> type = getType();
			for(int i = 0; i < type.size(); i++) {
				String productId = (String) type.get(i).get("product_id");
				query0.select("sum(case when TMP.product_id = '"+ productId + "' then TMP.volume else 0 end)", '"' + productId +"_q"+ '"');
				query0.select("sum(case when TMP.product_id = '"+ productId + "' then TMP.valuei else 0 end)", '"' + productId +"_t"+ '"');
			}
		} else { 
			for(int i = 0; i < products2.size(); i++) {
				String productId = (String) products2.get(i);
				query0.select("sum(case when TMP.product_id = '"+ productId + "' then TMP.volume else 0 end)", '"' + productId +"_q"+ '"');
				query0.select("sum(case when TMP.product_id = '"+ productId + "' then TMP.valuei else 0 end)", '"' + productId +"_t"+ '"');
			}
		}
		
		return query0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void prepareResultGrid() {
		List<Object> products = getProducts((List<?>) getParameter("product"));
		List<String> products2 = (List<String>) getParameter("product");
		Boolean productFlag = false;
		if(!products.isEmpty()){
			productFlag = true;
		}
		addDataField("depName", "name1");
		addDataField("depId", "party_id");
		List<Map<String,String>> type = getType();
		if(!productFlag){
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
		} else {
			for(int i = 0; i < products2.size(); i++) {
				String productId = (String) products2.get(i);
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
	}
}
