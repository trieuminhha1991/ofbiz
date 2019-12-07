package com.olbius.baselogistics.report;

import java.sql.SQLException;
import java.util.List;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.olap.OlapInterface;

public interface FacilityOlapLOG extends OlapInterface {
	
	public static String TYPE_RECEIVE = "RECEIVE";
	public static String TYPE_EXPORT = "EXPORT";
	public static String TYPE_INVENTORY = "INVENTORY";
	public static String TYPE_BOOK = "BOOK";
	public static String TYPE_AVAILABLE = "AVAILABLE";
	
	void productReceiveQOH(List<String> facilityId, List<String> productId, String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productExportQOH(List<String> facilityId, List<String> productId, String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productInventoryQOH(List<String> facilityId, List<String> productId,  String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productBookATP(List<String> facilityId, List<String> productId, String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException;

	void productInventoryATP(List<String> facilityId, List<String> productId,  String dateType, List<String> geoId, List<String> geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
}
