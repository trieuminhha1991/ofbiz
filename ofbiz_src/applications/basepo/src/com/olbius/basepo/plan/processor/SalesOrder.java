package com.olbius.basepo.plan.processor;

import java.sql.Date;
import java.sql.ResultSet;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;
import com.olbius.entity.cache.OlbiusCache;

public class SalesOrder {

	private OlbiusQuery query;
	private SQLProcessor processor;
	private Delegator delegator;
	private Date fromDate;
	private Date thruDate;
	private Object product;
	private Object organizationId;

	private static final OlbiusCache<Long> PARTY = new OlbiusCache<Long>() {
		@Override
		public Long loadCache(Delegator delegator, String key) throws Exception {
			GenericValue value = EntityUtil
					.getFirst(delegator.findByAnd("PartyDimension", UtilMisc.toMap("partyId", key), null, false));
			return value != null ? value.getLong("dimensionId") : -1;
		}
	};

	private Object getParty(Object o) {
		Object tmp = null;
		if (o != null) {
			tmp = PARTY.get(delegator, (String) o);
		}
		return tmp;
	}

	private Object getProduct(Object p) {
		Object tmp = null;
		if (p != null) {
			tmp = ProductDimension.D.getId(delegator, (String) p);
		}
		return tmp;
	}

	public SalesOrder(SQLProcessor processor, Delegator delegator, Date fromDate, Date thruDate, Object product,
			Object organizationId) {
		super();
		this.processor = processor;
		this.delegator = delegator;
		this.fromDate = fromDate;
		this.thruDate = thruDate;
		this.product = product;
		this.organizationId = organizationId;
	}

	private OlbiusQuery init() {
		query = (OlbiusQuery) new OlbiusQuery(processor);
		Condition condition = new Condition();
		condition.and(Condition.makeBetween("dd.date_value", fromDate, thruDate));
		condition.and(Condition.makeEQ("sof.party_from_dim_id", getParty(organizationId)));
		condition.and(Condition.makeEQ("sof.product_dim_id", getProduct(product)));
		condition.and(Condition.make("sof.order_item_status", Condition.NOT_EQ, "ITEM_CANCELLED"));
		condition.and(Condition.make("sof.order_item_status", Condition.NOT_EQ, "ITEM_REJECTED"));
		query.select(new Sum("sof.quantity"), "quantity").from("sales_order_fact", "sof")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = sof.order_date_dim_id")
				.where(condition);
		return query;
	}

	public Object getQuantityTotal() {
		Object value = null;
		try {
			if (query == null) {
				query = init();
			}
			ResultSet resultSet = query.getResultSet();
			while (resultSet.next()) {
				value = resultSet.getObject("quantity");
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
		return value;
	}
}
