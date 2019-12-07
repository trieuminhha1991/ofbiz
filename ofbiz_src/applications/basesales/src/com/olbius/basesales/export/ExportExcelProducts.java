package com.olbius.basesales.export;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

public class ExportExcelProducts extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";
	private final String RESOURCEPO = "BasePOUiLabels";
	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "DANH_SACH_SP";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSListProduct", locale));
		setRunServiceName("JQGetPOListProducts");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSListProduct", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		//String productStoreId = ExportExcelUtil.getParameter(parameters, "productStoreId");
		// make parameters input
		//Map<String, String[]> parametersCtx = FastMap.newInstance();
		//parametersCtx.put("productStoreId", new String[]{productStoreId});
		setRunParameters(parameters);
		
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
		//addSubTitle(UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale), productStoreId);
		//addSubTitle(UtilProperties.getMessage(RESOURCE, "BSNote", locale), UtilProperties.getMessage(RESOURCE, "BSThePriceInCludedTax", locale));
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSProductId", locale), "productCode");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSUPC", locale), "idSKU");
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNumChild", locale), "numChild");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSPrimaryCategory", locale), "primaryProductCategoryId");
		addColumn(36, UtilProperties.getMessage(RESOURCE, "BSProductName", locale), "productName");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSProductPackingUomId", locale), "quantityUomId");
		addColumn(36, UtilProperties.getMessage(RESOURCE, "BSState", locale), "productState");
		addColumn(36, UtilProperties.getMessage(RESOURCE, "BSPurchaseDiscontinuationDate", locale), "purchaseDiscontinuationDate", ExportExcelStyle.STYLE_CELL_DATETIME);
		addColumn(36, UtilProperties.getMessage(RESOURCE, "BSSalesDiscontinuationDate", locale), "salesDiscontinuationDate", ExportExcelStyle.STYLE_CELL_DATETIME);
	}
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		int columnIndex = 0;
		Locale locale = getLocale();
		if (hasColumnIndex) {
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for (int i = columnIndex; i < columnKeys.size(); i++) {
			Object value = null;
			String key = columnKeys.get(i);
			if ("quantityUomId".equals(key)) {
				String quantityUomId = (String) map.get("quantityUomId");
				if (quantityUomId != null) {
					try {
						GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
						if (uom != null) {
							value = uom.get("description", locale);
						}
					} catch (GenericEntityException e) {
						Debug.logWarning("Error when get uom", module);
					}
					
				}
			} else if ("productState".equals(key)){
				Timestamp purchaseDiscontinuationDate = (Timestamp) map.get("purchaseDiscontinuationDate");
				Timestamp salesDiscontinuationDate = (Timestamp) map.get("salesDiscontinuationDate");
				Timestamp nowTimestamp = UtilDateTime.nowTimestamp() ;
				String state ="";
				if (UtilValidate.isNotEmpty(salesDiscontinuationDate) && nowTimestamp.after(salesDiscontinuationDate) ) {
					state += UtilProperties.getMessage(RESOURCEPO, "BSDiscountinueSales", locale);
				}
				if (UtilValidate.isNotEmpty(purchaseDiscontinuationDate) && nowTimestamp.after(purchaseDiscontinuationDate) ) {
					if (state != "") state += ", ";
					state += UtilProperties.getMessage(RESOURCEPO, "BSDiscountinuePurchase", locale);;
				}
				value = state;
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
}
