package com.olbius.basepo.plan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;

public class DailyProcessor implements PeriodProcessor {

	@Override
	public Map<String, Object> getDataInventoryReport(long fromDate, long thruDate, List<String> listFacility) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String helperName = delegator.getGroupHelperName("org.ofbiz.olap");
		GenericHelperInfo helperInfo = new GenericHelperInfo("org.ofbiz.olap", helperName);
		SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);

		String facilitySql = "";
		for (int i = 0; i < listFacility.size(); i++) {
			if (i == (listFacility.size() - 1)) {
				facilitySql += "?";
			} else {
				facilitySql += "?,";
			}
		}
		Map<String, Object> productByInventoryItemMap = FastMap.newInstance();
		String sqlQuery = "";
		java.sql.Date dateFrom = new java.sql.Date(fromDate);
		java.sql.Date dateThru = new java.sql.Date(thruDate);
		sqlQuery = "select pd.product_id, pd.product_name, SUM(ff.inventory_total), SUM(ff.available_to_promise_total), dd.date_value\n"
				+ "from product_dimension as pd, facility_fact as ff, facility_dimension as fd, date_dimension as dd\n"
				+ "where ff.product_dim_id = pd.dimension_id \n" + "AND dd.date_value >= ? \n"
				+ "AND dd.date_value <= ? \n" + "AND fd.facility_id IN (" + facilitySql + ")"
				+ "AND ff.facility_dim_id = fd.dimension_id\n" + "AND ff.date_dim_id = dd.dimension_id\n"
				+ "GROUP BY pd.product_id, pd.product_name, dd.date_value\n" + "ORDER BY pd.product_id, dd.date_value";
		try {
			sqlProcessor.prepareStatement(sqlQuery);
			sqlProcessor.setValue(dateFrom);
			sqlProcessor.setValue(dateThru);
			for (int i = 0; i < listFacility.size(); i++) {
				sqlProcessor.setValue(listFacility.get(i));
			}
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		}
		try {
			ResultSet result = sqlProcessor.executeQuery();
			while (result.next()) {
				productByInventoryItemMap.put(result.getString(1), result.getBigDecimal(4));
			}
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		} finally {
			try {
				sqlProcessor.close();
			} catch (GenericDataSourceException e) {
				Debug.log(e.getMessage());
			}
		}
		return productByInventoryItemMap;
	}

	@Override
	public Map<String, Object> getDataSalesReport(long fromDate, long thruDate, String org, String orderStatusId) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String helperName = delegator.getGroupHelperName("org.ofbiz.olap");
		GenericHelperInfo helperInfo = new GenericHelperInfo("org.ofbiz.olap", helperName);
		SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);

		Map<String, Object> productBySalesItemMap = FastMap.newInstance();
		String sqlQuery = "";
		java.sql.Date dateFrom = new java.sql.Date(fromDate);
		java.sql.Date dateThru = new java.sql.Date(thruDate);
		sqlQuery = "select pd.product_code, pd.product_id, SUM(sof.quantity)\n"
				+ "from product_dimension as pd, sales_order_fact as sof, party_dimension as pgd, date_dimension as dd, product_promo_dimension as ppd\n"
				+ "where sof.product_dim_id = pd.dimension_id \n" + "AND sof.party_from_dim_id = pgd.dimension_id \n"
				+ "AND sof.order_date_dim_id = dd.dimension_id\n" + "AND ppd.dimension_id = sof.promo_dim_id\n"
				+ "AND dd.date_value >= ? \n" + "AND dd.date_value <= ? \n" + "AND pgd.party_id = ? \n"
				+ "AND sof.order_status = ? \n" + "AND sof.order_item_status <> ? \n"
				+ "AND ppd.product_promo_id is null \n" + "GROUP BY pd.product_id, pd.product_code \n";
		try {
			sqlProcessor.prepareStatement(sqlQuery);
			sqlProcessor.setValue(dateFrom);
			sqlProcessor.setValue(dateThru);
			sqlProcessor.setValue(org);
			sqlProcessor.setValue(orderStatusId);
			sqlProcessor.setValue("ITEM_CANCELLED");
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		}
		try {
			ResultSet result = sqlProcessor.executeQuery();
			while (result.next()) {
				productBySalesItemMap.put(result.getString(2), result.getBigDecimal(3));
			}
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		} finally {
			try {
				sqlProcessor.close();
			} catch (GenericDataSourceException e) {
				Debug.log(e.getMessage());
			}
		}
		return productBySalesItemMap;
	}

}
