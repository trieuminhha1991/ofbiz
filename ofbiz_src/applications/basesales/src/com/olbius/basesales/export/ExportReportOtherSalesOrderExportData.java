package com.olbius.basesales.export;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;

public class ExportReportOtherSalesOrderExportData extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "TRICH_XUAT_SO_LIEU_BAN_HANG";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSReportSalesOrderExportData", locale));
		setRunServiceName("olapOtherSalesOrderExportData");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSReportSalesOrderExportData", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		setPageSizeQuery(20000);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		String fromDateTitle = null;
		String thruDateTitle = null;
		String fromDateStr = ExportExcelUtil.getParameter(parameters, "fromDate");
		String thruDateStr = ExportExcelUtil.getParameter(parameters, "thruDate");
		SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (fromDateStr != null) {
			fromDateStr += " 00:00:00.000";
			fromDate = Timestamp.valueOf(fromDateStr);
			fromDateTitle = formatOut.format(fromDate);
		}
		if (thruDateStr != null) {
			thruDateStr += " 23:59:59.999";
			thruDate = Timestamp.valueOf(thruDateStr);
			thruDateTitle = formatOut.format(thruDate);
		}
		setRunParameters(parameters);
        
		// add subtitle rows
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSFromDate", locale), fromDateTitle);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSThruDate", locale), thruDateTitle);
		
		// add all columns
		addColumn(8, "no", null);
		addColumn(16, "order_id", "order_id");
		addColumn(16, "order_item_seq_id", "order_item_seq_id");
		addColumn(16, "order_name", "order_name");
		addColumn(16, "order_date", "order_date", ExportExcelStyle.STYLE_CELL_DATE);
		addColumn(16, "currency_uom", "currency_uom");
		addColumn(16, "product_store_id", "product_store_id");
		addColumn(16, "product_store_name", "product_store_name");
		addColumn(16, "tax_amount", "tax_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "discount_amount", "discount_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "sub_total_amount", "sub_total_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "total_amount", "total_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "product_id", "product_id");
		addColumn(16, "product_name", "product_name");
		addColumn(16, "primary_product_category_id", "primary_product_category_id");
		addColumn(16, "quantity", "quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(16, "cancel_quantity", "cancel_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(16, "return_quantity", "return_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(16, "total_quantity", "total_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(16, "total_selected_amount", "total_selected_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "unit_price", "unit_price", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "quantity_uom_id", "quantity_uom_id");
		addColumn(16, "return_id", "return_id");
		addColumn(16, "return_price", "return_price", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "product_avg_cost", "product_avg_cost", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(16, "sales_method_channel_enum_id", "sales_method_channel_enum_id");
		addColumn(16, "sales_channel_enum_id", "sales_channel_enum_id");
		addColumn(16, "order_status_id", "order_status_id");
		addColumn(16, "order_item_status_id", "order_item_status_id");
		addColumn(16, "creator_id", "creator_id");
		addColumn(16, "sgc_customer_id", "sgc_customer_id");
	}
	
}
