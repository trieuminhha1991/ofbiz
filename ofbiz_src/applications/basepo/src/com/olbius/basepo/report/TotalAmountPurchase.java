package com.olbius.basepo.report;

import java.sql.ResultSet;
import java.util.Date;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.cache.dimension.PartyDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;

public class TotalAmountPurchase extends TypeOlap {
	private OlbiusQuery query;
	private SQLProcessor processor;
	private Delegator delegator;
	private String organization;
	private Date fromDate;

	private Object getParty(String partyId) {
		if (partyId != null) {
			return PartyDimension.D.getId(delegator, partyId);
		}
		return null;
	}

	public TotalAmountPurchase(SQLProcessor processor, Delegator delegator, String organization, Date fromDate) {
		this.processor = processor;
		this.delegator = delegator;
		this.organization = organization;
		this.fromDate = fromDate;
	}

	private OlbiusQuery init() {
		query = (OlbiusQuery) new OlbiusQuery(processor);
		Condition condition = new Condition();
		condition.and(Condition.makeBetween("pof.order_date", getSqlDate(fromDate),
				getSqlDate(new Date(System.currentTimeMillis()))));
		condition.and(Condition.makeEQ("pof.party_to_dim_id", getParty(organization)));
		condition.and(Condition.makeEQ("sd.status_id", "ITEM_COMPLETED"));
		query.select(new Sum("pof.price"), "value_total").from("purchase_order_fact", "pof")
				.join(Join.INNER_JOIN, "status_dimension", "sd", "sd.dimension_id = pof.status_dim_id")
				.where(condition);
		return query;
	}

	public Object getValueTotal() {
		Object value = null;
		try {
			if (query == null) {
				query = init();
			}
			ResultSet resultSet = query.getResultSet();
			while (resultSet.next()) {
				value = resultSet.getObject("value_total");
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
