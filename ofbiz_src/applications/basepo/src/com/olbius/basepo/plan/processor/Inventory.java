package com.olbius.basepo.plan.processor;

import java.sql.Date;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.cache.dimension.ProductDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;

public class Inventory {
	private OlbiusQuery query;
	private SQLProcessor processor;
	private Delegator delegator;
	private Date inventory_date;
	private List<Object> facilities;
	private Object product;

	private List<Object> getFacilities(List<?> list) {
		List<Object> tmp = new ArrayList<Object>();
		if (list != null) {
			for (Object x : list) {
				Long y = FacilityDimension.D.getId(delegator, (String) x);
				if (y != -1) {
					tmp.add(y);
				}
			}
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

	public Inventory(SQLProcessor processor, Delegator delegator, Date inventory_date, List<Object> facilities,
			Object product) {
		super();
		this.processor = processor;
		this.delegator = delegator;
		this.inventory_date = inventory_date;
		this.facilities = facilities;
		this.product = product;
	}

	private OlbiusQuery init() {
		query = (OlbiusQuery) new OlbiusQuery(processor);
		Condition condition = new Condition();
		condition.and(Condition.makeEQ("dd.date_value", inventory_date));
		condition.and(Condition.makeIn("ff.facility_dim_id", getFacilities(facilities)));
		condition.and(Condition.makeEQ("ff.product_dim_id", getProduct(product)));
		query.select(new Sum("ff.inventory_total"), "inventory_total").from("facility_fact", "ff")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "dd.dimension_id = ff.date_dim_id").where(condition);
		return query;
	}

	public Object getInventoryTotal() {
		Object value = null;
		try {
			if (query == null) {
				query = init();
			}
			ResultSet resultSet = query.getResultSet();
			while (resultSet.next()) {
				value = resultSet.getObject("inventory_total");
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
