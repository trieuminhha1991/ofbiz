package com.olbius.acc.report.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastMap;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelInventoryAverageCost extends ExportExcelAbstract {
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";
	private final String RESOURCE_LOG = "BaseLogisticsUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "bao-cao-ton-kho";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_LOG, "LogInventoryReport", locale).toUpperCase());
		
		setRunServiceName("getListInventoryAverageCost");
		setModuleExport("BASEACCOUNTING");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_LOG, "LogInventoryReport", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String facilityId = ExportExcelUtil.getParameter(parameters, "facilityId");
		
		// make parameters input
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.put("facilityId", new String[]{facilityId});
		setRunParameters(parametersCtx);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add subtitle rows
		if (UtilValidate.isNotEmpty(facilityId)) {
			String subTitleValue = "";
			GenericValue facility = null;
			try {
				facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(facility)) {
				subTitleValue = facility.getString("facilityName");
			}
			addSubTitle(UtilProperties.getMessage(RESOURCE_LOG, "Facility", locale) + ": ", " " + subTitleValue);
		}
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_ACC, "BACCSeqId", locale), null);
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductId", locale), "productCode");
		addColumn(35, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductName", locale), "productName");
		addColumn(13, UtilProperties.getMessage("ProductUiLabels", "ProductUnitOfMeasure", locale), "quantityUomDesc");
		addColumn(18, UtilProperties.getMessage("ProductUiLabels", "ProductAverageCost", locale), "productAverageCost", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(18, UtilProperties.getMessage(RESOURCE_ACC, "BACCTotalQuantityFacility", locale), "totalQuantityOnHand", ExportExcelStyle.STYLE_CELL_DECIMAL);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCTotalAmountFacility", locale), "totalInventoryCost", ExportExcelStyle.STYLE_CELL_CURRENCY);
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
			
			value = (Object) map.get(key);
			createCell(row, i, value, cellStyles.get(i));
		}
	}
}