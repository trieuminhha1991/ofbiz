package com.olbius.baselogistics.report.excel;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;

import javolution.util.FastMap;

public class PercentageCompletedOrderExcel extends ExportExcelAbstract{
	private final String RESOURCE = "BaseLogisticsUiLabels";
	private final String RESOURCE_SALES = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "[LOG]_BC_TI_LE_DON_HANG_HOAN_THANH_";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BLPercentageCompletedOrder", locale));
		setRunServiceName("olapReportPercentageCompletedOrder");
		setModuleExport("LOG");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BLPercentageCompletedOrder", locale));
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
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.putAll(parameters);
		String[] group = getArrayValueParams(parameters, "group");
		if (group != null) parametersCtx.put("group", group);
		
		setRunParameters(parametersCtx);
        
		// add subtitle rows
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE_SALES, "BSDateTime", locale), dateTimeOut);
		addSubTitle(UtilProperties.getMessage(RESOURCE_SALES, "BSFromDate", locale), fromDateTitle);
		addSubTitle(UtilProperties.getMessage(RESOURCE_SALES, "BSThruDate", locale), thruDateTitle);
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BLTime", locale), "dateTime");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BLTotalOrderCompleted", locale), "order_num", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(24, UtilProperties.getMessage(RESOURCE, "BLTotalOrder", locale), "order_num_total", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BLPercentageCompleted", locale), "percentage_order", ExportExcelStyle.STYLE_CELL_DECIMAL);
	}
	
	private String[] getArrayValueParams(Map<String, String[]> parameters, String key){
		if (parameters.containsKey(key) && parameters.get(key).length > 0) {
			String[] paramValue = parameters.get(key);
			if (UtilValidate.isNotEmpty(paramValue) && paramValue.length == 1 && !"null".equals(paramValue[0])) {
				String[] paramValueArr = paramValue[0].split(",");
				if (paramValueArr.length == 1) {
					String[] paramValueNew = new String[2];
					paramValueNew[0] = paramValueArr[0];
					paramValueNew[1] = "";
					return paramValueNew;
				} else {
					return paramValueArr;
				}
			}
		}
		return null;
	}
}
