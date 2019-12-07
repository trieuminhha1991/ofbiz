package com.olbius.acc.report.olap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.webapp.event.ExportExcelEvents;

public class ExportExcelStockWarehouseAcc extends ExportExcelAbstract{
	private final String RESOURCE = "BaseAccountingUiLabels"; 

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BAO_CAO_TON_CUOI_KI";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BACCStockWarehouseAccReport", locale));
		setRunServiceName("olbiusReportStockWarehouseAcc");
		setModuleExport("ACC");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCStockWarehouseAccReport", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		setRunParameters(parameters);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add all columns
		addColumn(8, UtilProperties.getMessage("BaseSalesUiLabels", "BSNo2", locale), null);
		addColumn(15, UtilProperties.getMessage(RESOURCE, "BACCProductId", locale), "productCode");
		addColumn(30, UtilProperties.getMessage(RESOURCE, "BACCProductName", locale), "productName");
		addColumn(15, UtilProperties.getMessage("BaseLogisticsUiLabels", "FacilityId", locale), "facilityId");
		addColumn(30, UtilProperties.getMessage("BaseLogisticsUiLabels", "FacilityName", locale), "facilityName");
		addColumn(12, UtilProperties.getMessage("ProductUiLabels", "ProductUnitOfMeasure", locale), "quantityUomId");
		
		addColumn(15, UtilProperties.getMessage("AccountingUiLabels", "AccountingQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCEndingStock", locale), "endingQuantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCEndingStock", locale), "endingAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
	}
}