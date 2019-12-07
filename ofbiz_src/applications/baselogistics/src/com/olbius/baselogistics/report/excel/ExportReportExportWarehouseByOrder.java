package com.olbius.baselogistics.report.excel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

public class ExportReportExportWarehouseByOrder extends ExportExcelAbstract{
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_LOG = "BaseLogisticsUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "bao-cao-xuat-kho-du-kien";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_LOG, "WarehousingExportReportExpectedUnderSaleOrder", locale).toUpperCase());
		
		setRunServiceName("JQGetReportExportWarehouseByOrder");
		setModuleExport("LOG");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_LOG, "WarehousingExportReportExpectedUnderSaleOrder", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        if (UtilValidate.isEmpty(listSortFields)) {
			listSortFields = UtilMisc.toList("productCode");
		}
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add all columns
		addColumn(10, UtilProperties.getMessage(RESOURCE_SALES, "BSSTT", locale), null);
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "ProductId", locale), "productCode");
		addColumn(35, UtilProperties.getMessage(RESOURCE_LOG, "ProductName", locale), "productName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "ExportQuantityExpected", locale), "quantity", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "Unit", locale), "quantityUomId");
		addColumn(30, UtilProperties.getMessage(RESOURCE_LOG, "DateExpectedWarehousing", locale), "estimatedDeliveryDate");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "OrderId", locale), "orderId");
		addColumn(30, UtilProperties.getMessage(RESOURCE_LOG, "Facility", locale), "originFacilityId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_SALES, "BSSalesChannel", locale), "productStoreId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "Priority", locale), "priority");
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
			
			BigDecimal quantity = (BigDecimal) map.get("quantity");
			if ("Y".equals(map.get("requireAmount")) && "WEIGHT_MEASURE".equals(map.get("amountUomTypeId"))) {
				quantity = ((BigDecimal) map.get("selectedAmount")).multiply(quantity);
			}
			
			if ("quantity".equals(key)) {
				value = quantity;
			} else if ("quantityUomId".equals(key)) {
				String uomId = (String) ("Y".equals(map.get("requireAmount")) ? map.get("weightUomId") : map.get("quantityUomId"));
				if (uomId != null) {
					value = getDescriptionUom(delegator, uomId);
				}
			} else if ("estimatedDeliveryDate".equals(key)) {
				Timestamp shipAfterDate = (Timestamp) map.get("shipAfterDate");
				Timestamp shipBeforeDate = (Timestamp) map.get("shipBeforeDate");
				if (UtilValidate.isNotEmpty(shipAfterDate) && UtilValidate.isNotEmpty(shipBeforeDate)) {
					String s1 = new SimpleDateFormat("dd/MM/yyyy").format(shipAfterDate);
					String s2 = new SimpleDateFormat("dd/MM/yyyy").format(shipBeforeDate);
					value = s1 + " - " + s2;
				} else {
					value = "";
				}
			} else if ("originFacilityId".equals(key)) {
				value = getFacilityName(delegator, (String) map.get("originFacilityId"));
			} else if ("productStoreId".equals(key)) {
				value = getStoreName(delegator, (String) map.get("productStoreId"));
			} else if ("priority".equals(key)){
				value = getPriorityName(delegator, (String) map.get("priority"));
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
	private static String getDescriptionUom(Delegator delegator, String uomId) {
		String description = uomId;
		GenericValue uom = null;
		try {
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(uom)) {
			description = uom.getString("abbreviation");
		}
		return description;
	}
	
	private static String getFacilityName(Delegator delegator, String facilityId) {
		String facilityName = facilityId;
		GenericValue facility = null;
		try {
			facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(facility)) {
			facilityName = facility.getString("facilityName");
		}
		return facilityName;
	}
	
	private static String getStoreName(Delegator delegator, String productStoreId) {
		String storeName = productStoreId;
		GenericValue store = null;
		try {
			store = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(store)) {
			storeName = store.getString("storeName");
		}
		return storeName;
	}
	
	private static String getPriorityName(Delegator delegator, String enumId) {
		String description = enumId;
		GenericValue priority = null;
		try {
			priority = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", enumId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(priority)) {
			description = priority.getString("description");
		}
		return description;
	}
	
}