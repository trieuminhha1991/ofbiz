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

public class ExportReportSyncTorSalesExec extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BC_TONG_HOP_DS_NHAN_VIEN_BAN_HANG";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSTurnoverSynthesisBySalesExecutive", locale));
		setRunServiceName("olapSynTorSalesExec");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSTurnoverSynthesisBySalesExecutive", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		
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
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSEmployeeId", locale), "party_id");
		addColumn(32, UtilProperties.getMessage(RESOURCE, "BSEmployeeName", locale), "name");
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSOrderVolume", locale), "order_number", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSOrderedQty", locale), "total_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSPaidQty", locale), "return_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSWeight", locale), "total_selected_amount", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSTotalDiscountAmount", locale), "discount_amount", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSFinishValueTotal", locale), "total_amount", ExportExcelStyle.STYLE_CELL_NUMBER);
	}
	
}
