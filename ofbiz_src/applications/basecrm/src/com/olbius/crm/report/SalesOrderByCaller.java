package com.olbius.crm.report;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.cache.dimension.PartyDimension;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;

public class SalesOrderByCaller extends TypeOlap {
	class Turnover {
		private OlbiusQuery query;
		private SQLProcessor processor;
		private Delegator delegator;
		private String organizationId;
		private String partyId;
		private Timestamp fromDate;
		private Timestamp thruDate;

		public Turnover(SQLProcessor processor, Delegator delegator, String organizationId, String partyId,
				Timestamp fromDate, Timestamp thruDate) {
			this.processor = processor;
			this.delegator = delegator;
			this.organizationId = organizationId;
			this.partyId = partyId;
			this.fromDate = fromDate;
			this.thruDate = thruDate;
		}

		private Object getParty(String partyId) {
			if (partyId != null) {
				return PartyDimension.D.getId(delegator, partyId);
			}
			return null;
		}

		private OlbiusQuery init() {
			query = (OlbiusQuery) new OlbiusQuery(processor);
			Condition condition = new Condition();
			condition.and(Condition.makeEQ("sof.party_from_dim_id", getParty(organizationId)));
			condition.and(Condition.makeEQ("sof.call_center_party_dim_id", getParty(partyId)));
			condition.and("sof.order_item_status <> 'ITEM_CANCELLED'");
			condition.and(Condition.makeBetween("sof.order_date", fromDate, thruDate));
			query.select("case when SUM(sof.total) <> 0 then SUM(sof.total) else 0 end", "_total")
					.from("sales_order_fact", "sof").where(condition);
			return query;
		}

		public Object getSOTurnover() {
			Object value = null;
			try {
				if (query == null) {
					query = init();
				}
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
					value = resultSet.getObject("_total");
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

	class Total {
		private OlbiusQuery query;
		private SQLProcessor processor;
		private Delegator delegator;
		private String organizationId;
		private String partyId;
		private Timestamp fromDate;
		private Timestamp thruDate;

		public Total(SQLProcessor processor, Delegator delegator, String organizationId, String partyId,
				Timestamp fromDate, Timestamp thruDate) {
			this.processor = processor;
			this.delegator = delegator;
			this.organizationId = organizationId;
			this.partyId = partyId;
			this.fromDate = fromDate;
			this.thruDate = thruDate;
		}

		private Object getParty(String partyId) {
			if (partyId != null) {
				return PartyDimension.D.getId(delegator, partyId);
			}
			return null;
		}

		private OlbiusQuery init() {
			query = (OlbiusQuery) new OlbiusQuery(processor);
			Condition condition = new Condition();
			condition.and(Condition.makeEQ("sof.party_from_dim_id", getParty(organizationId)));
			condition.and(Condition.makeEQ("sof.call_center_party_dim_id", getParty(partyId)));
			condition.and("sof.order_item_status <> 'ITEM_CANCELLED'");
			condition.and(Condition.makeBetween("sof.order_date", fromDate, thruDate));
			query.select("count(*)", "_count").from("sales_order_fact", "sof").where(condition);
			return query;
		}

		public Object getSOTotal() {
			Object value = null;
			try {
				if (query == null) {
					query = init();
				}
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
					value = resultSet.getObject("_count");
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
}
