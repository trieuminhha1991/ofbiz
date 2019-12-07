package com.olbius.acc.report.olap;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.webapp.event.ExportExcelEvents;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelImpExpStockWarehouse extends ExportExcelAbstract{
	private final String RESOURCE = "BaseAccountingUiLabels"; 

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BAO_CAO_XUAT_NHAP_TON";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage("BaseLogisticsUiLabels", "InventoryReportTotal", locale));
		setRunServiceName("olbiusReportImpExpStockWarehouse");
		setModuleExport("ACC");
		setDescriptionExport(UtilProperties.getMessage("BaseLogisticsUiLabels", "InventoryReportTotal", locale));
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
		addColumn(25, UtilProperties.getMessage("BaseLogisticsUiLabels", "FacilityName", locale), "facilityName");
		addColumn(12, UtilProperties.getMessage("ProductUiLabels", "ProductUnitOfMeasure", locale), "quantityUomId");
		
		addColumn(15, UtilProperties.getMessage("AccountingUiLabels", "AccountingQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCOpeningStock", locale), "openingQuantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCOpeningStock", locale), "openingAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(15, UtilProperties.getMessage("AccountingUiLabels", "AccountingQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCImportStock", locale), "importQuantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCImportStock", locale), "importAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(15, UtilProperties.getMessage("AccountingUiLabels", "AccountingQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCExportStock", locale), "exportQuantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCExportStock", locale), "exportAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(15, UtilProperties.getMessage("AccountingUiLabels", "AccountingQuantity", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCEndingStock", locale), "endingQuantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCAmount", locale) + " " + UtilProperties.getMessage(RESOURCE, "BACCEndingStock", locale), "endingAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
	}
}