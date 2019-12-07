package com.olbius.report;

import java.util.List;
import java.util.Map;

public interface PeriodProcessor {
	public List<Map<String, Object>> getDataReportWarehouse(String fromDate, String thruDate, String productId, String facilityId);
	public List<Map<String, Object>> getDataInventoryReport(String fromDate, String thruDate, String productId, String facilityId);
	public List<Map<String, Object>> getDataReceiveInventoryReport(String fromDate, String thruDate, String productId, String facilityId);
}