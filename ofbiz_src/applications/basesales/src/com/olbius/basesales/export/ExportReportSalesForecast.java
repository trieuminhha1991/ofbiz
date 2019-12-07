package com.olbius.basesales.export;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;

public class ExportReportSalesForecast extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BC_SALES_FORECAST";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSReportSalesForecast", locale));
		setRunServiceName("olapSalesForecast");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSReportSalesForecast", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		setPageSizeQuery(5000);
		
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
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSSalesForecastId", locale), "sales_forecast_id");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomTimePeriodId", locale), "custom_time_period_id");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSNumDay", locale), "num_day");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSProductCode", locale), "product_code");
		addColumn(36, UtilProperties.getMessage(RESOURCE, "BSProductName", locale), "product_name");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BSTarget", locale), "quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BSActual", locale), "report_quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSPercentComplete", locale), "perc_khtt", ExportExcelStyle.STYLE_CELL_CURRENCY);
		//addColumn(18, UtilProperties.getMessage(RESOURCE, "BSAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BSTarget", locale), "quantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
		//addColumn(18, UtilProperties.getMessage(RESOURCE, "BSAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BSActual", locale), "report_quantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
	}

	@Override
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		int columnIndex = 0;
		
		if (hasColumnIndex) {
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for (int i = columnIndex; i < columnKeys.size(); i++) {
			String key = columnKeys.get(i);
			if (map.containsKey(key)) {
				Object value = (Object) map.get(key);
				createCell(row, i, value, cellStyles.get(i));
			}
		}
		
		// extend row
		BigDecimal quantity = (BigDecimal) map.get("quantity");
		BigDecimal report_quantity = (BigDecimal) map.get("report_quantity");
		Double perc_value = null;
		if (report_quantity == null) report_quantity = BigDecimal.ZERO;
		if (quantity != null && quantity != BigDecimal.ZERO) {
			perc_value = (double)report_quantity.doubleValue() / quantity.doubleValue();
		}
		createCell(row, columnKeys.size()-1, perc_value, cellStyles.get(columnKeys.size()-1));
	}
	
}
