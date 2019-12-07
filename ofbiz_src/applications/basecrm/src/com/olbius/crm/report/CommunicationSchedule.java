package com.olbius.crm.report;

import java.sql.ResultSet;
import java.sql.Timestamp;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import com.olbius.bi.olap.TypeOlap;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.join.Join;

public class CommunicationSchedule extends TypeOlap {
	class Communication {
		private OlbiusQuery query;
		private SQLProcessor processor;
		private String partyId;
		private Timestamp currentDate;

		public Communication(SQLProcessor processor, String partyId, Timestamp currentDate) {
			this.processor = processor;
			this.partyId = partyId;
			this.currentDate = currentDate;
		}

		private OlbiusQuery init() {
			query = (OlbiusQuery) new OlbiusQuery(processor);
			Condition condition = new Condition();
			condition.and(Condition.makeEQ("pcr.party_id_from", partyId));
			condition.and(Condition.makeEQ("pcr.role_type_id_from", "CALLCENTER_EMPL"));
			condition.and(Condition.makeEQ("pcr.status_id", "CONTACT_ASSIGNED"));
			condition.and(
					Condition.make("(pcr.thru_date IS NULL OR pcr.thru_date > '" + getSqlFromDate(currentDate) + "')"));
			condition.and(Condition
					.make("(pcr.from_date IS NULL OR pcr.from_date <= '" + getSqlThruDate(currentDate) + "')"));
			condition.and(Condition.makeEQ("mc.is_active", "Y"));
			query.select("count(*)", "_count").from("party_campaign_relationship", "pcr").join(Join.INNER_JOIN,
					"marketing_campaign", "mc", "mc.marketing_campaign_id = pcr.marketing_campaign_id")
					.where(condition);
			return query;
		}

		public Object getCommunicationTotal() {
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

	class NextSchedule {
		private OlbiusQuery query;
		private SQLProcessor processor;
		private String partyId;
		private Timestamp fromDate;
		private Timestamp thruDate;

		public NextSchedule(SQLProcessor processor, String partyId, Timestamp fromDate, Timestamp thruDate) {
			this.processor = processor;
			this.partyId = partyId;
			this.fromDate = fromDate;
			this.thruDate = thruDate;
		}

		private OlbiusQuery init() {
			query = (OlbiusQuery) new OlbiusQuery(processor);
			Condition condition = new Condition();
			condition.and(Condition.makeBetween("ce.entry_date", fromDate, thruDate));
			condition.and(Condition.makeEQ("ce.communication_event_type_id", "PHONE_COMMUNICATION"));
			condition.and(Condition.makeEQ("ce.party_id_from", partyId));
			condition.and(Condition.makeEQ("ce.status_id", "COM_SCHEDULED"));
			condition.and(Condition.makeEQ("ce.subject_enum_id", "COM_SCHEDULE_NEXT"));
			query.select("count(*)", "_count").from("communication_event", "ce").where(condition);
			return query;
		}

		public Object getNextScheduleTotal() {
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
