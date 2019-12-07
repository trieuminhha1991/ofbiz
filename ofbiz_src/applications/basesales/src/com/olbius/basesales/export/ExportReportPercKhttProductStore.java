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

public class ExportReportPercKhttProductStore extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BC_TY_LE_BILL_CO_THE_KHTT";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSPercentCustomerKhtt", locale));
		setRunServiceName("olapPercKhttProductStore");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSPercentCustomerKhtt", locale));
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
		String dateTypeStr = ExportExcelUtil.getParameter(parameters, "dateType");
		if ("DAY".equals(dateTypeStr)) setPageSizeQuery(5000);
		setRunParameters(parameters);
        
		// add subtitle rows
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSFromDate", locale), fromDateTitle);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSThruDate", locale), thruDateTitle);
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
		addColumn(14, UtilProperties.getMessage("OlapUiLabels", "olap_dateType", locale), "dateTime");
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSPSChannelId", locale), "product_store_id");
		addColumn(42, UtilProperties.getMessage(RESOURCE, "BSPSChannelName", locale), "store_name");
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSOrderVolume", locale), "num_order", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSCustomerKhttVolume", locale), "num_order_khtt", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSPercentCustomerKhtt", locale), "perc_khtt", ExportExcelStyle.STYLE_CELL_CURRENCY);
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
		BigDecimal num_order = (BigDecimal) map.get("num_order");
		BigDecimal num_order_khtt = (BigDecimal) map.get("num_order_khtt");
		Double perc_value = null;
		if (num_order != null && num_order != BigDecimal.ZERO) {
			perc_value = (double)num_order_khtt.doubleValue() / num_order.doubleValue();
		}
		createCell(row, columnKeys.size()-1, perc_value, cellStyles.get(columnKeys.size()-1));
	}
	
}
