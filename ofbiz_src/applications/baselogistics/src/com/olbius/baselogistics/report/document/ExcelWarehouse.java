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

public class ExcelWarehouse {

	@SuppressWarnings("unchecked")
	public static void exportWarehouseReportExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);

		try {
			String dateType = request.getParameter("dateType");
			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			String facility = request.getParameter("facility");
			String product = request.getParameter("product");
			String categories = request.getParameter("categories");
			String inventoryType = request.getParameter("inventoryType");

			Map<String, String> datafields = new LinkedHashMap<>();
			datafields.put("dateTime", "TimeLabel");
			datafields.put("facility_name", "LogFacilityName");
			datafields.put("product_code", "ProductId");
			datafields.put("product_name", "ProductName");
			datafields.put("category_name", "BLCategoryProduct");
			datafields.put("inventory_date", "ReceivedDate");
			datafields.put("manufactured_date", "ProductManufactureDate");
			datafields.put("expire_date", "ExpireDate");
			datafields.put("quantity_on_hand_total_%", "Quantity");
			datafields.put("uom_id", "QuantityUomId");

			String excelTitle = "LogReportExportWarehouse";
			if (!"EXPORT".equals(inventoryType)) {
				excelTitle = "LogReportWareHourse";
			}
			ExcelAccounting.setMultiSource(UtilMisc.toSet("BaseLogisticsUiLabels", "FacilityReportUiLabels", "BaseSalesUiLabels"));
			ExcelAccounting<String, Object> excel = new ExcelAccounting<String, Object>(locale, datafields, excelTitle,
					excelTitle);
			Map<String, Object> context = UtilMisc.toMap("dateType", dateType, "fromDate",
					excel.convertDateToTimestamp(fromDate), "thruDate", excel.convertDateToTimestamp(thruDate),
					"olapType", OlbiusOlapService.GRID, "inventoryType", inventoryType);

			if (UtilValidate.isNotEmpty(facility)) {
				context.put("facility[]", Arrays.asList(facility.split(",")));
			}
			if (UtilValidate.isNotEmpty(product)) {
				context.put("product[]", Arrays.asList(product.split(",")));
			}
			if (UtilValidate.isNotEmpty(categories)) {
				context.put("categories[]", Arrays.asList(categories.split(",")));
			}

			Map<String, Object> resultService = dispatcher.runSync("olapWarehouseReport", context);

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
