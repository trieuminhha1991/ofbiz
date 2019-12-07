package com.olbius.olap.facility;

import java.sql.SQLException;

import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;

import com.olbius.olap.OlapInterface;

public interface FacilityOlap extends OlapInterface {
	
	public static String TYPE_RECEIVE = "RECEIVE";
	public static String TYPE_EXPORT = "EXPORT";
	public static String TYPE_INVENTORY = "INVENTORY";
	public static String TYPE_BOOK = "BOOK";
	public static String TYPE_AVAILABLE = "AVAILABLE";
	
	void productReceiveQOH(String facilityId, String productId, String dateType, String geoId, String geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productExportQOH(String facilityId, String productId, String dateType, String geoId, String geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productInventoryQOH(String facilityId, String productId,  String dateType, String geoId, String geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productBookATP(String facilityId, String productId, String dateType, String geoId, String geoType) throws GenericDataSourceException, GenericEntityException, SQLException;

	void productInventoryATP(String facilityId, String productId,  String dateType, String geoId, String geoType) throws GenericDataSourceException, GenericEntityException, SQLException;
	
	void productDelivery(String productId, String geoId, String geoType, boolean facilityFlag) throws GenericDataSourceException, GenericEntityException, SQLException;
}
