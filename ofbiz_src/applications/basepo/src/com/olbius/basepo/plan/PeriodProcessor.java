package com.olbius.basepo.plan;

import java.util.List;
import java.util.Map;

public interface PeriodProcessor {
	public Map<String, Object> getDataInventoryReport(long fromDate, long thruDate, List<String> listFacility);

	public Map<String, Object> getDataSalesReport(long fromDate, long thruDate, String org, String orderStatusId);
}
