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

public class ExportReportOtherStatisticSalesData extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BC_THONG_KE_DU_LIEU_BACH_HOA";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSReportStatisticSalesData", locale));
		setRunServiceName("olapOtherStatisticSalesData");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSReportStatisticSalesData", locale));
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
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCreatorId", locale), "creator_id");
		addColumn(22, UtilProperties.getMessage(RESOURCE, "BSCreatorName", locale), "creator_name");
		addColumn(10, UtilProperties.getMessage(RESOURCE, "BSPSSalesChannelId", locale), "product_store_id");
		addColumn(42, UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale), "store_name");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSDayMonthYearSlash", locale), "order_date", ExportExcelStyle.STYLE_CELL_DATE);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSHourMinuteSecondSlash", locale), "order_date", ExportExcelStyle.createStyleColumn(getWb(), "hh/mm/ss"));
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSAmount", locale), "total_amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSNumberItem", locale), "num_item", ExportExcelStyle.STYLE_CELL_NUMBER);
	}
	
}
