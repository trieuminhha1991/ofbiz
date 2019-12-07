package com.olbius.salesmtl.report_new;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.chart.OlapPieChart;
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

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesMtlChartPieServices extends OlbiusOlapService {

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
                .select("product_store_dimension.product_store_id")
                .select("product_store_dimension.store_name")
                .select("product_dimension.internal_name")
                .select("sum(quantity)", "Quantity")
                .select("sum(total)", "Total")
                .select("pd.party_code", "agency_id")
                .select("pd.party_id", "party_agency_id")
                .select("pd.name", "agency_name")
                .select("product_store_dimension.product_store_id || '-' || product_dimension.product_code", "description")
                .select("category_dimension.category_name")
                .select("sales_order_fact.quantity_uom", "unit")
                .from("product_dimension")
                .join(Join.INNER_JOIN, "sales_order_fact", "sales_order_fact.product_dim_id = product_dimension.dimension_id")
                .join(Join.INNER_JOIN, "party_dimension", "pd", "sales_order_fact.party_to_dim_id = pd.dimension_id")
                .join(Join.INNER_JOIN,"date_dimension","sales_order_fact.order_date_dim_id = date_dimension.dimension_id")
                .join(Join.INNER_JOIN, "product_promo_dimension", "product_promo_dimension.dimension_id = sales_order_fact.discount_dim_id")
                .join(Join.INNER_JOIN, "product_store_dimension", "sales_order_fact.product_store_dim_id = product_store_dimension.dimension_id")
                .join(Join.INNER_JOIN, "party_dimension", null, "sales_order_fact.party_from_dim_id = party_dimension.dimension_id")
                .join(Join.INNER_JOIN, "product_category_relationship", null, "product_category_relationship.product_dim_id = product_dimension.dimension_id")
                .join(Join.INNER_JOIN, "category_dimension", null, "product_category_relationship.category_dim_id = category_dimension.dimension_id")
                .where(conditions)
                .groupBy("pd.party_code")
                .groupBy("pd.description")
                .orderBy("pd.party_code");
        conditions.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
                .andEQ("sales_order_fact.order_status", status)
                .and("category_dimension.category_id not like '%TAX%'")
                .and("product_category_relationship.thru_dim_date = -1")
                .andIn("sales_order_fact.order_status", status)
                .and("product_promo_dimension.product_promo_id IS NULL")
                .andIn("pd.party_id", agencyId, UtilValidate.isNotEmpty(agencyId)))
                .andEQ("party_dimension.party_id", userId);

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
    public void prepareResultChart() {
        if(getOlapResult() instanceof OlapPieChart) {
            addXAxis("agency_name");
            addYAxis("Total");
        }
    }
}
