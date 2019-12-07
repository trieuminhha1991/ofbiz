package com.olbius.salesmtl.document;

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

public class ExcelFacility {

	@SuppressWarnings("unchecked")
	public static void exportInventoryToExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);

		try {
			String dateType = request.getParameter("dateType");
			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			String group = request.getParameter("group");
			String facility = request.getParameter("facility");
			String product = request.getParameter("product");

			Map<String, String> datafields = new LinkedHashMap<>();
			datafields.put("dateTime", "TimeLabel");
			datafields.put("facility", "FacilityLabel");
			datafields.put("party", "DistributorLabel");
			datafields.put("product", "ProductLabel");
			datafields.put("uom", "olap_uom");
			datafields.put("inventoryP_%", "InventoryBeforeLabel");
			datafields.put("receive_%", "ReceiveLabel");
			datafields.put("export_%", "ExportLabel");
			datafields.put("inventory_%", "InventoryAfterLabel");
			ExcelAccounting.setMultiSource(UtilMisc.toSet("BaseLogisticsUiLabels", "FacilityReportUiLabels", "BaseSalesUiLabels"));
			ExcelAccounting<String, Object> excel = new ExcelAccounting<String, Object>(locale, datafields,
					"LogInventoryReport", "LogInventoryReport");

			Map<String, Object> context = UtilMisc.toMap("dateType", dateType, "fromDate",
					excel.convertDateToTimestamp(fromDate), "thruDate", excel.convertDateToTimestamp(thruDate),
					"olapType", OlbiusOlapService.GRID);

			if (UtilValidate.isNotEmpty(group)) {
				context.put("group[]", Arrays.asList(group.split(",")));
			}
			if (UtilValidate.isNotEmpty(facility)) {
				context.put("facility[]", Arrays.asList(facility.split(",")));
			}
			if (UtilValidate.isNotEmpty(product)) {
				context.put("product[]", Arrays.asList(product.split(",")));
			}

			Map<String, Object> resultService = dispatcher.runSync("olbiusReportFacility", context);

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
