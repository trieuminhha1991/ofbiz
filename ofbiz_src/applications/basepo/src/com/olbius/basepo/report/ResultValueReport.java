package com.olbius.basepo.report;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class ResultValueReport extends TypeOlap {
	private SQLProcessor processor;
	private OlbiusQuery queryTopCategoryPurchase;
	private OlbiusQuery queryTopLeastCategoryPurchase;

	public ResultValueReport(SQLProcessor processor, String organization) {
		this.processor = processor;
		Date curDate = new Date(System.currentTimeMillis());
		Timestamp curTime = new Timestamp(curDate.getTime());
		Timestamp startMonth = UtilDateTime.getMonthStart(curTime);
		Condition condition = new Condition();

		OlbiusQuery query2 = (OlbiusQuery) new OlbiusQuery(processor);
		query2.select("quantity", "party_to_dim_id", "order_date_dim_id", "product_dim_id", "status_dim_id")
				.from("purchase_order_fact")
				.groupBy("quantity", "party_to_dim_id", "order_date_dim_id", "product_dim_id", "status_dim_id");

		queryTopCategoryPurchase = (OlbiusQuery) new OlbiusQuery(processor);
		queryTopCategoryPurchase.from(query2, "purchase_order_fact")
				.select("sum(purchase_order_fact.quantity)", "totalQuantity").select("category_dimension.category_name")
				.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_category_relationship", null,
						"product_category_relationship.product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "category_dimension", null,
						"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization",
						"purchase_order_fact.party_to_dim_id = party_organization.dimension_id AND party_organization.party_id = "
								+ "'" + organization + "'");
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
		condition.and(Condition.make("category_dimension.category_id is not null"));
		queryTopCategoryPurchase.where(condition).groupBy("category_dimension.category_name");
		queryTopCategoryPurchase.orderBy("totalQuantity", OlbiusQuery.DESC, true);
		queryTopCategoryPurchase.limit(1);

		queryTopLeastCategoryPurchase = (OlbiusQuery) new OlbiusQuery(processor);
		queryTopLeastCategoryPurchase.from(query2, "purchase_order_fact")
				.select("sum(purchase_order_fact.quantity)", "totalQuantity").select("category_dimension.category_name")
				.join(Join.INNER_JOIN, "date_dimension", null, "order_date_dim_id = date_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_dimension", null, "product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "product_category_relationship", null,
						"product_category_relationship.product_dim_id = product_dimension.dimension_id")
				.join(Join.INNER_JOIN, "category_dimension", null,
						"product_category_relationship.category_dim_id = category_dimension.dimension_id AND category_dimension.category_type = 'CATALOG_CATEGORY'")
				.join(Join.INNER_JOIN, "party_dimension", "party_organization",
						"purchase_order_fact.party_to_dim_id = party_organization.dimension_id AND party_organization.party_id = "
								+ "'" + organization + "'");
		condition.and(Condition.makeBetween("date_dimension.date_value", getSqlDate(startMonth), getSqlDate(curTime)));
		condition.and(Condition.make("category_dimension.category_id is not null"));
		queryTopLeastCategoryPurchase.where(condition).groupBy("category_dimension.category_name");
		queryTopLeastCategoryPurchase.orderBy("totalQuantity", OlbiusQuery.ASC, true);
		queryTopLeastCategoryPurchase.limit(1);

	}

	public List<String> getTopCategoryPurchase() {
		List<String> list = new ArrayList<String>();

		try {
			ResultSet resultSet = queryTopCategoryPurchase.getResultSet();
			while (resultSet.next()) {
				list.add(resultSet.getString("totalQuantity"));
				list.add(resultSet.getString("category_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (processor != null) {
				try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public List<String> getTopLeastCategoryPurchase() {
		List<String> list = new ArrayList<String>();

		try {
			ResultSet resultSet = queryTopLeastCategoryPurchase.getResultSet();
			while (resultSet.next()) {
				list.add(resultSet.getString("totalQuantity"));
				list.add(resultSet.getString("category_name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (processor != null) {
				try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public static Map<String, Object> getMostCategoryPurchaseOrderReportOlap(DispatchContext dctx,
			Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		ResultValueReport type = new ResultValueReport(
				new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getTopCategoryPurchase());
		return result;
	}

	public static Map<String, Object> getLeastCategoryPurchaseOrderReportOlap(DispatchContext dctx,
			Map<String, Object> context) {
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		ResultValueReport type = new ResultValueReport(
				new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")), organization);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listValue", type.getTopLeastCategoryPurchase());
		return result;
	}
}
