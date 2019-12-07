package com.olbius.salesmtl.report_new;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.ReturnResultCallback;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.bi.olap.services.OlbiusOlapService;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesMtlGridServices extends OlbiusOlapService {

    private OlbiusQuery query;
    private Boolean isViewRepOrg = true;
    private Boolean isViewRepParner = false;

    @Override
    public void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        setFromDate((Date) context.get("fromDate"));
        setThruDate((Date) context.get("thruDate"));
        String all = UtilProperties.getMessage("BaseSalesUiLabels", "BSAllObject", (Locale)context.get("locale"));
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userId = (String) userLogin.get("partyId");
        String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        putParameter("dateType", context.get("dateType"));
        putParameter("agency", context.get("agency[]"));
        putParameter("productStore", context.get("productStore[]"));
        putParameter("orderStatus", context.get("orderStatus[]"));
        putParameter("category", context.get("category[]"));
        putParameter("userId", userId);
        putParameter("organization", organization);
        putParameter("ALL", all);
        putParameter("fileName", "evaluateTurnoverDistributor"); // cache the specific file

        String viewPartner = (String) context.get("viewPartner");
        putParameter("viewPartner", viewPartner);
    }

    @Override
    protected OlapQuery getQuery() {
        if (query == null) {
            query = init();
        }
        return query;
    }

    private OlbiusQuery init() {
        OlbiusQuery query = makeQuery();
        List<Object> agencyId = (List<Object>) getParameter("agency");
        List<Object> categoryId = (List<Object>) getParameter("category");
        List<Object> status = (List<Object>) getParameter("orderStatus");
        String userId = (String) getParameter("userId");

        Object status1 = "ORDER_COMPLETED";
        if(UtilValidate.isEmpty(status)){
            status = FastList.newInstance();
            status.add(status1);
        }

        OlbiusQuery innerQuery = makeQuery();
        Condition conditions = new Condition();
        innerQuery.select("product_dimension.product_code")
                .select("product_dimension.internal_name")
                .select("product_store_dimension.product_store_id")
                .select("product_store_dimension.store_name")
                .select("sum(quantity)", "Quantity")
                .select("sum(total)", "Total")
                .select("pd.party_code", "agency_id", UtilValidate.isNotEmpty(agencyId))
                .select("pd.name", "agency_name", UtilValidate.isNotEmpty(agencyId))
                .select("pd.party_id", "party_agency_id")
                .select("'all_object' as agency_id", UtilValidate.isEmpty(agencyId))
                .select("'all_object2' as agency_name", UtilValidate.isEmpty(agencyId))
                .select("product_store_dimension.product_store_id || '-' || product_dimension.product_code", "description")
                .select("category_dimension.category_name")
                .select("sales_order_fact.quantity_uom", "unit")
                .from("product_dimension")
                .join(Join.INNER_JOIN, "sales_order_fact", "sales_order_fact.product_dim_id = product_dimension.dimension_id")
                .join(Join.INNER_JOIN, "party_dimension", "pd", "sales_order_fact.party_to_dim_id = pd.dimension_id")
                .join(Join.INNER_JOIN,"date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
                .join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.discount_dim_id")
                .join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
                .join(Join.INNER_JOIN, "party_dimension", "pd1", "sales_order_fact.party_from_dim_id = pd1.dimension_id")
                .join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
                .join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id")
                .where(conditions)
                .groupBy("product_dimension.product_code")
                .groupBy("pd.party_code", UtilValidate.isNotEmpty(agencyId))
                .groupBy("pd.description", UtilValidate.isNotEmpty(agencyId))
                .groupBy("product_store_dimension.product_store_id")
                .groupBy("product_store_dimension.store_name").groupBy("category_dimension.category_name")
                .groupBy("product_dimension.internal_name").groupBy("sales_order_fact.quantity_uom")
                .orderBy("product_dimension.product_code").orderBy("product_dimension.internal_name");
        ;
        System.out.println(""+getSqlDate(fromDate));
        System.out.println(""+getSqlDate(thruDate));
        conditions.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
                .andEQ("sales_order_fact.order_status", status)
                .and("category_dimension.category_id not like '%TAX%'")
                .and("product_category_relationship.thru_dim_date = -1")
                .andIn("sales_order_fact.order_status", status)
                .and("product_promo_dimension.product_promo_id IS NULL")
                .andIn("pd.party_id", agencyId, UtilValidate.isNotEmpty(agencyId)))
                .andEQ("pd1.party_id", userId);

        if(UtilValidate.isNotEmpty(categoryId) && categoryId.toArray().length == 1 && "all".equals(categoryId.get(0))){

        } else {
            conditions.and(Condition.makeIn("category_dimension.category_id", categoryId, !"all".equals(categoryId)));
        }
        if("ORDER_CANCELLED".equals(status)){
            conditions.andEQ("sales_order_fact.order_status", status, status != null);
            conditions.and("sales_order_fact.order_item_status = 'ITEM_CANCELLED'");
        } else {
            conditions.and("sales_order_fact.order_item_status <> 'ITEM_CANCELLED'");
        }
        return innerQuery;

    }

    @Override
    public void prepareResultGrid() {
        addDataField("stt");
        addDataField("agencyId", "agency_id", new ReturnResultCallback<String>() {
            @Override
            public String get(Object object) {
                if(!"all_object".equals(object)){
                    return (String) object;
                } else{
                    String all = (String) getParameter("ALL");
                    return all;
                }
            }
        });
        addDataField("agencyName", "agency_name", new ReturnResultCallback<String>() {
            @Override
            public String get(Object object) {
                if(!"all_object2".equals(object)){
                    return (String) object;
                } else{
                    String all = (String) getParameter("ALL");
                    return all;
                }
            }
        });
        addDataField("category", "category_name");
        addDataField("percent");
        addDataField("productId", "product_code");
        addDataField("productName", "internal_name");
        addDataField("quantity1", "Quantity", new ReturnResultCallback<BigDecimal>() {
            @Override
            public BigDecimal get(Object object) {
                if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
            }
        });
        addDataField("total1", "Total", new ReturnResultCallback<BigDecimal>() {
            @Override
            public BigDecimal get(Object object) {
                if(UtilValidate.isNotEmpty(object)){ return (BigDecimal) object; }else{ return new BigDecimal(0); }
            }
        });
        addDataField("unit", "unit");
    }


}
