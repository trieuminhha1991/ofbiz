package com.olbius.basesales.export;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.olbius.basesales.forecast.SalesForecastWorker;
import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

import javolution.util.FastMap;

public class ExportExcelSalesForeCastDetail extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
//		String fileName = "CHI_TIET_SALES_FORECAST";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
//		fileName += "_" + dateTime;
//		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSSalesForecastDetailTable", locale));
		setRunServiceName("JQGetSalesForecastContent");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSSalesForecastDetailTable", locale));
		setSplitSheet(false);
		setPrefixSheetName(UtilProperties.getMessage(RESOURCE, "BSheetSalesForecast", locale));
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String salesForecastId = ExportExcelUtil.getParameter(parameters, "salesForecastId");
		String internalPartyId = ExportExcelUtil.getParameter(parameters, "internalPartyId");
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.put("salesForecastId", new String[]{salesForecastId});
		parametersCtx.put("internalPartyId", new String[]{internalPartyId});
		setRunParameters(parameters);
		
		//find customTimePeriodName
		String periodName = "";
		List<GenericValue> listPeriodThisAndChildren = new ArrayList<GenericValue>();
		try {
			if(UtilValidate.isNotEmpty(salesForecastId) && UtilValidate.isNotEmpty(internalPartyId)){
				GenericValue salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
				if (salesForecast != null) {
					String customTimePeriodId = salesForecast.getString("customTimePeriodId");
					if(UtilValidate.isNotEmpty(customTimePeriodId)){
						GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
						if(customTimePeriod != null){
							periodName = customTimePeriod.getString("periodName");
						}
						listPeriodThisAndChildren.addAll(SalesForecastWorker.getPeriodChildrenFull(customTimePeriodId, delegator));
						listPeriodThisAndChildren.add(customTimePeriod);
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		//set File Name To Year_set_sales_forecast
		String fileName = "CHI_TIET_SALES_FORECAST" + "_" + periodName;
		fileName += "_" + dateTime;
		setFileName(fileName);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add subtitle rows
		SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSSalesForecastYear", locale), periodName);
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSProductId", locale), "productCode");
		addColumn(24, UtilProperties.getMessage(RESOURCE, "BSProductName", locale), "productName", ExportExcelStyle.STYLE_COLUMN_LABEL);
		addColumn(15, UtilProperties.getMessage(RESOURCE, "BSFeature", locale), "features", ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
		for(GenericValue item:listPeriodThisAndChildren){
			addColumn(10, item.getString("periodName"), item.getString("customTimePeriodId"));
		}
		
	}
	
	@Override
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		int columnIndex = 0;
		
		if (hasColumnIndex) {
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for (int i = columnIndex; i < columnKeys.size(); i++) {
			Object value = null;
			String key = columnKeys.get(i);
			if ("features".equals(key)) {
				String features = (String) map.get("features");
				if (features != null && !features.equals("")) {
					value = (Object) map.get(key);
				} else {
					value = (Object)"-";
				}
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
}
