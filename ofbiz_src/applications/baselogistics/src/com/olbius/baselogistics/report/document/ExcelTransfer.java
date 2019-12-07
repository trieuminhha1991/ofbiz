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

public class ExcelTransfer {

	@SuppressWarnings("unchecked")
	public static void exportTransferOlapExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		try {
			String dateType = request.getParameter("dateType");
			String fromDate = request.getParameter("fromDate");
			String thruDate = request.getParameter("thruDate");
			String origin_facility = request.getParameter("origin_facility");
			String dest_facility = request.getParameter("dest_facility");
			String status_transfer_id = request.getParameter("status_transfer_id");
			String product = request.getParameter("product");
			String categories = request.getParameter("categories");

			Map<String, String> datafields = new LinkedHashMap<>();
			datafields.put("origin_facility", "FacilityFrom");
			datafields.put("dest_facility", "FacilityTo");
			datafields.put("transfer_type_id", "TransferType");
			datafields.put("transfer_id", "TransferId");
			datafields.put("product_code", "ProductCode");
			datafields.put("product_name", "ProductName");
			datafields.put("lot_id", "Batch");
			datafields.put("datetime_manufactured", "ProductManufactureDate");
			datafields.put("expire_date", "ExpireDate");
			datafields.put("quantity_%", "RequiredNumber");
			datafields.put("actual_exported_quantity_%", "ActualExportedQuantity");
			datafields.put("quantity_uom", "QuantityUomId");
			datafields.put("status_id", "LogShipmentStatus");
			datafields.put("delivery_id", "DeliveryTransferId");
			datafields.put("delivery_status_id", "StatusDelivery");
			datafields.put("transfer_date", "TransferDate");
			ExcelAccounting.setMultiSource(UtilMisc.toSet("BaseLogisticsUiLabels", "FacilityReportUiLabels", "BaseSalesUiLabels"));
			ExcelAccounting<String, Object> excel = new ExcelAccounting<String, Object>(locale, datafields,
					"ReportTransfer", "ReportTransfer");

			Map<String, Object> context = UtilMisc.toMap("dateType", dateType, "fromDate",
					excel.convertDateToTimestamp(fromDate), "thruDate", excel.convertDateToTimestamp(thruDate),
					"olapType", OlbiusOlapService.GRID);

			if (UtilValidate.isNotEmpty(origin_facility)) {
				context.put("origin_facility[]", Arrays.asList(origin_facility.split(",")));
			}
			if (UtilValidate.isNotEmpty(dest_facility)) {
				context.put("dest_facility[]", Arrays.asList(dest_facility.split(",")));
			}
			if (UtilValidate.isNotEmpty(status_transfer_id)) {
				context.put("status_transfer_id[]", Arrays.asList(status_transfer_id.split(",")));
			}
			if (UtilValidate.isNotEmpty(product)) {
				context.put("product[]", Arrays.asList(product.split(",")));
			}
			if (UtilValidate.isNotEmpty(categories)) {
				context.put("categories[]", Arrays.asList(categories.split(",")));
			}

			Map<String, Object> resultService = dispatcher.runSync("olapTransferReport", context);

			if (ServiceUtil.isSuccess(resultService)) {
				List<Map<String, Object>> data = (List<Map<String, Object>>) resultService.get("data");
				if (UtilValidate.isNotEmpty(data)) {
					/* init excel */
					excel.setDataExcel(data);
					excel.setFieldDescribe(UtilMisc.<String, Object> toMap("transfer_type_id",
							UtilMisc.toList("TransferType", "description"), "status_id",
							UtilMisc.toList("StatusItem", "description"), "delivery_status_id",
							UtilMisc.toList("StatusItem", "description")));
					excel.initExcel(response, fromDate, thruDate);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
