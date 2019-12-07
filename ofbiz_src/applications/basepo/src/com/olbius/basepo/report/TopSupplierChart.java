package com.olbius.basepo.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.olap.AbstractOlap;
import com.olbius.olap.OlapInterface;

public class TopSupplierChart extends AbstractOlap implements OlapInterface {

	protected static final String _TOP_TYPE = "TOPMAX";
	private Delegator delegator;

	public TopSupplierChart(Delegator delegator) {
		super();
		this.delegator = delegator;
	}

	private Object getProduct(String productId) {
		if (productId != null) {
			return ProductDimension.D.getId(delegator, productId);
		}
		return null;
	}

	public void top5SupplierChart(String ownerPartyId, String productId, String topType)
			throws GenericDataSourceException, GenericEntityException, SQLException {
		OlbiusQuery query2 = new OlbiusQuery(getSQLProcessor());
		Object product_id = getProduct(productId);
		query2.select("pof.party_to_dim_id", "pof.party_from_dim_id", "pof.order_date_dim_id", "pof.product_dim_id", "pof.status_dim_id")
				.select("CASE WHEN prd.require_amount = 'Y' THEN (pof.quantity * pof.selected_amount) ELSE pof.quantity END AS quantity")
				.from("purchase_order_fact", "pof")
				.join(Join.INNER_JOIN, "product_dimension", "prd", "prd.dimension_id = pof.product_dim_id")
				.groupBy("quantity", "party_to_dim_id", "party_from_dim_id", "order_date_dim_id", "product_dim_id",
						"status_dim_id, prd.require_amount, pof.selected_amount");

		OlbiusQuery query = new OlbiusQuery(getSQLProcessor());
		Condition condition = new Condition();
		query.from(query2, "pof").select("supplier.party_id").select("sum(pof.quantity)", "quantity");
		query.join(Join.INNER_JOIN, "date_dimension", "dd", "order_date_dim_id = dd.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "supplier", "pof.party_from_dim_id = supplier.dimension_id");
		query.join(Join.INNER_JOIN, "party_dimension", "buyer",
				"pof.party_to_dim_id = buyer.dimension_id AND buyer.party_id = " + "'" + ownerPartyId + "'");
		condition.and(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate)))
				.andEQ("pof.product_dim_id", product_id, product_id != null);

		query.where(condition);
		query.groupBy("supplier.party_id");
		if (topType.equals(_TOP_TYPE)) {
			query.orderBy("quantity", OlbiusQuery.DESC, true);
		} else {
			query.orderBy("quantity", OlbiusQuery.ASC, true);
		}

		query.limit(5);

		ResultSet resultSet = query.getResultSet();

		while (resultSet.next()) {
			String _key = resultSet.getString("party_id");
			if (yAxis.get("amount") == null) {
				yAxis.put("amount", new ArrayList<Object>());
			}
			xAxis.add(_key);
			BigDecimal quantity = resultSet.getBigDecimal("quantity");
			yAxis.get("amount").add(quantity);
		}
	}
}
