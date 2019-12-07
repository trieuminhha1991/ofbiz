package com.olbius.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
	public List<Map<String, Object>> getDataReportWarehouse(String fromDate, String thruDate, String productId,
			String facilityId) { 
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String helperName = delegator.getGroupHelperName("org.ofbiz.olap");
		GenericHelperInfo helperInfo = new GenericHelperInfo("org.ofbiz.olap", helperName);
		SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);
		List<Map<String, Object>> listProductByOrder = new ArrayList<Map<String, Object>>();
		String sqlQuery = "";
		long fromDateLong = Long.parseLong(fromDate);
		java.sql.Date dateFrom = new java.sql.Date(fromDateLong);
		long thruDateLong = Long.parseLong(thruDate);
		java.sql.Date dateThru = new java.sql.Date(thruDateLong);
		if (productId.equals("")) {
			if (facilityId.equals("")) {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'EXPORT' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				} catch (SQLException e) {
					Debug.log(e.getMessage());
				}
			} else {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'EXPORT' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND fd.facility_id = ? \n"
						+ "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(facilityId);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				} catch (SQLException e) {
					Debug.log(e.getMessage());
				}
			}
		} else {
			if (facilityId.equals("")) {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'EXPORT' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND pd.product_id = ? \n"
						+ "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(productId);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				} catch (SQLException e) {
					Debug.log(e.getMessage());
				}

			} else {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'EXPORT' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND pd.product_id = ? \n" + "AND fd.facility_id = ? \n"
						+ "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(productId);
					sqlProcessor.setValue(facilityId);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				} catch (SQLException e) {
					Debug.log(e.getMessage());
				}
			}
		}

		try {
			ResultSet result = sqlProcessor.executeQuery();
			while (result.next()) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("inventoryItemId", result.getString(1));
				item.put("productName", result.getString(2));
				item.put("facilityId", result.getString(3));
				item.put("dateValue", result.getTimestamp(4).getTime());
				item.put("quantityOnHandTotal", result.getBigDecimal(5));
				listProductByOrder.add(item);
			}
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		}
		return listProductByOrder;
	}

	@Override
	public List<Map<String, Object>> getDataInventoryReport(String fromDate, String thruDate, String productId, String facilityId) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String helperName = delegator.getGroupHelperName("org.ofbiz.olap");
		GenericHelperInfo helperInfo = new GenericHelperInfo("org.ofbiz.olap", helperName);
		SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);
		List<Map<String, Object>> listProductByInventoryItem = new ArrayList<Map<String,Object>>();
		String sqlQuery = "";
		long fromDateLong = Long.parseLong(fromDate);
		java.sql.Date dateFrom = new java.sql.Date(fromDateLong);
		long thruDateLong = Long.parseLong(thruDate);
		java.sql.Date dateThru = new java.sql.Date(thruDateLong);
		if(productId.equals("")){
			if(facilityId.equals("")){
				sqlQuery = "select pd.product_id, pd.product_name, fd.facility_id, SUM(ff.inventory_total), SUM(ff.available_to_promise_total), dd.date_value\n" + 
						  "from product_dimension as pd, facility_fact as ff, facility_dimension as fd, date_dimension as dd\n" + 
						  "where ff.product_dim_id = pd.dimension_id \n" + 
						  "AND dd.date_value >= ? \n" +
						  "AND dd.date_value <= ? \n" +
						  "AND ff.facility_dim_id = fd.dimension_id\n"+
						  "AND ff.date_dim_id = dd.dimension_id\n"+
						  "GROUP BY pd.product_id, pd.product_name, fd.facility_id, dd.date_value\n" + 
						  "ORDER BY pd.product_id, dd.date_value";
				
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
				catch (SQLException e) {
					Debug.log(e.getMessage());
				}
			}else{
				sqlQuery = "select pd.product_id, pd.product_name, fd.facility_id, SUM(ff.inventory_total), SUM(ff.available_to_promise_total), dd.date_value\n" + 
						  "from product_dimension as pd, facility_fact as ff, facility_dimension as fd, date_dimension as dd\n" + 
						  "where ff.product_dim_id = pd.dimension_id \n" + 
						  "AND dd.date_value >= ? \n" +
						  "AND dd.date_value <= ? \n" +
						  "AND fd.facility_id = ? \n" +
						  "AND ff.facility_dim_id = fd.dimension_id\n"+
						  "AND ff.date_dim_id = dd.dimension_id\n"+
						  "GROUP BY pd.product_id, pd.product_name, fd.facility_id, dd.date_value\n" + 
						  "ORDER BY pd.product_id, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(facilityId);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
				catch (SQLException e) {
					Debug.log(e.getMessage());
				}
			}
		}
		else{
			if(facilityId.equals("")){
				sqlQuery = "select pd.product_id, pd.product_name, fd.facility_id, SUM(ff.inventory_total), SUM(ff.available_to_promise_total), dd.date_value\n" + 
						  "from product_dimension as pd, facility_fact as ff, facility_dimension as fd, date_dimension as dd\n" + 
						  "where ff.product_dim_id = pd.dimension_id \n" + 
						  "AND dd.date_value >= ? \n" +
						  "AND dd.date_value <= ? \n" +
						  "AND pd.product_id = ? \n" +
						  "AND ff.facility_dim_id = fd.dimension_id\n"+
						  "AND ff.date_dim_id = dd.dimension_id\n"+
						  "GROUP BY pd.product_id, pd.product_name, fd.facility_id, dd.date_value\n" + 
						  "ORDER BY pd.product_id, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(productId);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
				catch (SQLException e) {
					Debug.log(e.getMessage());
				}
				
			}else{
				sqlQuery = "select pd.product_id, pd.product_name, fd.facility_id, SUM(ff.inventory_total), SUM(ff.available_to_promise_total), dd.date_value\n" + 
						  "from product_dimension as pd, facility_fact as ff, facility_dimension as fd, date_dimension as dd\n" + 
						  "where ff.product_dim_id = pd.dimension_id \n" + 
						  "AND dd.date_value >= ? \n" +
						  "AND dd.date_value <= ? \n" +
						  "AND pd.product_id = ? \n" +
						  "AND fd.facility_id = ? \n" +
						  "AND ff.facility_dim_id = fd.dimension_id\n"+
						  "AND ff.date_dim_id = dd.dimension_id\n"+
						  "GROUP BY pd.product_id, pd.product_name, fd.facility_id, dd.date_value\n" + 
						  "ORDER BY pd.product_id, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(productId);
					sqlProcessor.setValue(facilityId);
				} catch (GenericDataSourceException e) {
					Debug.log(e.getMessage());
				} catch (GenericEntityException e) {
					Debug.log(e.getMessage());
				}
				catch (SQLException e) {
					Debug.log(e.getMessage());
				}
			}
		}
		
		try {
			ResultSet result = sqlProcessor.executeQuery();
			while (result.next()) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("productId", result.getString(1));
				item.put("productName", result.getString(2));
				item.put("facilityId", result.getString(3));
				item.put("quantityOnHandTotal", result.getBigDecimal(4));
				item.put("avaliableToPromiseTotal", result.getBigDecimal(5));
				item.put("dateValue", result.getTimestamp(6).getTime());
				listProductByInventoryItem.add(item);
			}
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		}
		finally{
			try {
				sqlProcessor.close();
			} catch (GenericDataSourceException e) {
				Debug.log(e.getMessage());
			}
		};
		return listProductByInventoryItem;
	}

	@Override
	public List<Map<String, Object>> getDataReceiveInventoryReport(String fromDate, String thruDate, String productId,
			String facilityId) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		String helperName = delegator.getGroupHelperName("org.ofbiz.olap");
		GenericHelperInfo helperInfo = new GenericHelperInfo("org.ofbiz.olap", helperName);
		SQLProcessor sqlProcessor = new SQLProcessor(helperInfo);
		List<Map<String, Object>> listProductByOrder = new ArrayList<Map<String, Object>>();
		String sqlQuery = "";
		long fromDateLong = Long.parseLong(fromDate);
		java.sql.Date dateFrom = new java.sql.Date(fromDateLong);
		long thruDateLong = Long.parseLong(thruDate);
		java.sql.Date dateThru = new java.sql.Date(thruDateLong);
		if (productId.equals("")) {
			if (facilityId.equals("")) {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'RECEIVE' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'RECEIVE' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND fd.facility_id = ? \n"
						+ "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(facilityId);
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (facilityId.equals("")) {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'RECEIVE' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND pd.product_id = ? \n"
						+ "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(productId);
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

			} else {
				sqlQuery = "select iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value, SUM(iif.quantity_on_hand_total)\n"
						+ "from product_dimension as pd, inventory_item_fact as iif, facility_dimension as fd, date_dimension as dd\n"
						+ "where iif.inventory_type = 'RECEIVE' \n" + "AND iif.product_dim_id = pd.dimension_id \n"
						+ "AND iif.facility_dim_id = fd.dimension_id\n" + "AND dd.date_value >= ? \n"
						+ "AND dd.date_value <= ? \n" + "AND pd.product_id = ? \n" + "AND fd.facility_id = ? \n"
						+ "AND iif.inventory_date_dim_id = dd.dimension_id\n"
						+ "GROUP BY iif.inventory_item_id, pd.product_name, fd.facility_id, dd.date_value\n"
						+ "ORDER BY pd.product_name, dd.date_value";
				try {
					sqlProcessor.prepareStatement(sqlQuery);
					sqlProcessor.setValue(dateFrom);
					sqlProcessor.setValue(dateThru);
					sqlProcessor.setValue(productId);
					sqlProcessor.setValue(facilityId);
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			ResultSet result = sqlProcessor.executeQuery();
			while (result.next()) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("inventoryItemId", result.getString(1));
				item.put("productName", result.getString(2));
				item.put("facilityId", result.getString(3));
				item.put("dateValue", result.getTimestamp(4).getTime());
				item.put("quantityOnHandTotal", result.getBigDecimal(5));
				listProductByOrder.add(item);
			}
		} catch (GenericDataSourceException e) {
			Debug.log(e.getMessage());
		} catch (SQLException e) {
			Debug.log(e.getMessage());
		}
		return listProductByOrder;
	}

}
