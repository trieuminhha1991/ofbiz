package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.bi.olap.AbstractOlap;
import com.olbius.bi.olap.OlapResultQueryInterface;
import com.olbius.bi.olap.cache.dimension.FacilityDimension;
import com.olbius.bi.olap.query.OlapQuery;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import com.olbius.bi.olap.query.function.Sum;
import com.olbius.bi.olap.query.join.Join;

import javolution.util.FastList;

public class ChartPieProportionPurchaseReturnReason extends AbstractOlap {
	private OlbiusQuery query;
	GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
	
	private void initQuery() {
		
		String ownerPartyId = (String) getParameter("ownerPartyId");
		
		List<Object> facilities = getFacilities((List<?>) getParameter("facility"));
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		List<String> listFacilityIds = FastList.newInstance();

		List<GenericValue> listFas = FastList.newInstance();
		
		try {
			listFas = delegator.findList("Facility",
					EntityCondition.makeCondition(UtilMisc.toMap("primaryFacilityGroupId", "FACILITY_INTERNAL", "ownerPartyId", ownerPartyId)), null,
					null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e.toString(), module);
		}
		if (!listFas.isEmpty()) {
			listFacilityIds = EntityUtil.getFieldListFromEntityList(listFas, "facilityId", true);
			facilities = getFacilities(listFacilityIds);
		}
		query = new OlbiusQuery(getSQLProcessor());
		query.select("iif.return_reason_dim_id");
		query.select("CASE WHEN rr.description is not null THEN rr.description ELSE 'NO_REASON' END", "reason");
		query.select(
				new Sum("CASE WHEN pr.require_amount = 'Y' THEN ABS(iif.amount_on_hand_total) ELSE ABS(iif.quantity_on_hand_total) END"),
				"quantity");
		query.from("inventory_item_fact", "iif")
				.join(Join.INNER_JOIN, "product_dimension", "pr", "iif.product_dim_id = pr.dimension_id")
				.join(Join.INNER_JOIN, "date_dimension", "dd", "iif.inventory_date_dim_id = dd.dimension_id")
				.join(Join.INNER_JOIN, "return_reason_dimension", "rr", "iif.return_reason_dim_id = rr.dimension_id")
				.groupBy("iif.return_reason_dim_id")
				.groupBy("rr.description")
				.where(Condition.makeBetween("dd.date_value", getSqlDate(fromDate), getSqlDate(thruDate))
						.andIn("iif.facility_dim_id", facilities).andEQ("pr.product_type", "FINISHED_GOOD")
						.andEQ("iif.inventory_change_type", "VENDOR_RETURN")
						.andEQ("iif.inventory_type", "EXPORT")
						);
	}

	@Override
	protected OlbiusQuery getQuery() {
		if (query == null) {
			initQuery();
		}
		return query;
	}

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

	public class ResultOutReport implements OlapResultQueryInterface {
		@Override
		public Object resultQuery(OlapQuery query) {
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				ResultSet resultSet = query.getResultSet();
				while (resultSet.next()) {
					String inventory_change_type = resultSet.getString("reason");
					if (UtilValidate.isNotEmpty(inventory_change_type)) {
						BigDecimal totalQuantity = resultSet.getBigDecimal("quantity");
						map.put(inventory_change_type, totalQuantity);
					}
				}
			} catch (GenericDataSourceException e) {
				e.printStackTrace();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return map;
		}

	}
}