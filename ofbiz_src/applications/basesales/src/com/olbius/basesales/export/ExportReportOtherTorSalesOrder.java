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

public class ExportReportOtherTorSalesOrder extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BC_THONG_KE_DON_HANG_BAN";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSReportSalesOrder", locale));
		setRunServiceName("olapOtherTorSalesOrder");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSReportSalesOrder", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(true);
		
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
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSOrderId", locale), "order_id");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSCreateDate", locale), "order_date");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale), "store_name");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSCustomerId", locale), "customer_id");
		addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCustomerName", locale), "customer_name");
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSOrderedQty", locale), "total_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSPaidQty", locale), "return_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSWeight", locale), "total_selected_amount", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSTotalDiscountAmount", locale), "discount_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSFinishValueTotal", locale), "total_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
	}
	
}
