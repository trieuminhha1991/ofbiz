package com.olbius.baselogistics.report.document;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.acc.excel.ExcelAccounting;
import com.olbius.bi.olap.services.OlbiusOlapService;

public class ExcelInventoryCustomer {

	@SuppressWarnings("unchecked")
	public static void exportInventoryCustomerReportExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		try {
			String dateType = request.getParameter("dateType");
			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			String product = request.getParameter("product");
			String categories = request.getParameter("categories");

			Map<String, String> datafields = new LinkedHashMap<>();
			datafields.put("dateTime", "TimeLabel");
			datafields.put("party_code", "BSCustomerId");
			datafields.put("party_name", "BSCustomerName");
			datafields.put("category_name", "BLCategoryProduct");
			datafields.put("product_code", "ProductId");
			datafields.put("product_name", "ProductName");
			datafields.put("inventoryF_%", "InventoryBeforeLabel");
			datafields.put("receive_%", "ReceiveLabel");
			datafields.put("export_%", "ExportLabel");
			datafields.put("inventoryL_%", "InventoryAfterLabel");

			ExcelAccounting.setMultiSource(UtilMisc.toSet("BaseLogisticsUiLabels", "FacilityReportUiLabels", "BaseSalesUiLabels"));
			ExcelAccounting<String, Object> excel = new ExcelAccounting<String, Object>(locale, datafields, "BSInventoryReport",
					"BSInventoryReport");

			Map<String, Object> context = UtilMisc.toMap("dateType", dateType, "fromDate",
					excel.convertDateToTimestamp(fromDate), "thruDate", excel.convertDateToTimestamp(thruDate),
					"olapType", OlbiusOlapService.GRID);

			if (UtilValidate.isNotEmpty(product)) {
				context.put("product[]", Arrays.asList(product.split(",")));
			}
			if (UtilValidate.isNotEmpty(categories)) {
				context.put("categories[]", Arrays.asList(categories.split(",")));
			}

			Map<String, Object> resultService = dispatcher.runSync("olapInventoryCustomerReport", context);

			if (ServiceUtil.isSuccess(resultService)) {
				List<Map<String, Object>> data = (List<Map<String, Object>>) resultService.get("data");
				if (UtilValidate.isNotEmpty(data)) {
					/* init excel */
					excel.setDataExcel(data);
					excel.initExcel(response, fromDate, thruDate);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
