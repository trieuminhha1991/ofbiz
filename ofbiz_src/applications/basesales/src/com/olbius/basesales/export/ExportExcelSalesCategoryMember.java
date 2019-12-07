package com.olbius.basesales.export;

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
import com.olbius.common.export.ExportExcelUtil;

import javolution.util.FastMap;

public class ExportExcelSalesCategoryMember extends ExportExcelAbstract{
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_PO = "BasePOUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "DANH_MUC_COOPSMILE";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_SALES, "BSProductInSalesCategory", locale));
		setRunServiceName("JQExportProductInSalesCategory");
		setModuleExport("PO");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_SALES, "BSProductInSalesCategory", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String productStoreId = ExportExcelUtil.getParameter(parameters, "productStoreId");
		// make parameters input
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.put("productStoreId", new String[]{productStoreId});
		setRunParameters(parametersCtx);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add subtitle rows
		SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE_SALES, "BSDateTime", locale), dateTimeOut);
		addSubTitle(UtilProperties.getMessage(RESOURCE_SALES, "BSPSSalesChannel", locale), productStoreId);
		//addSubTitle(UtilProperties.getMessage(RESOURCE_SALES, "BSNote", locale), UtilProperties.getMessage(RESOURCE_SALES, "BSThePriceInCludedTax", locale));
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSNo2", locale), null);
		addColumn(16, "UPC", "idSKU");
		addColumn(8, "IUPPRM", "iupprm");
		addColumn(12, UtilProperties.getMessage(RESOURCE_SALES, "BSProductId", locale), "productCode");
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSNHCategoryId", locale), "primaryProductCategoryId");
		addColumn(35, UtilProperties.getMessage(RESOURCE_SALES, "BSProductName", locale), "productName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_SALES, "BSTaxProductCategory", locale), "taxCategoryId");
		addColumn(10, UtilProperties.getMessage(RESOURCE_SALES, "BSAbbSupplierId", locale), "supplierCode");
		addColumn(11, UtilProperties.getMessage(RESOURCE_PO, "BSPurchaseUomId", locale), "purchaseUomId");
		addColumn(13, UtilProperties.getMessage(RESOURCE_SALES, "BSPurchasePrice", locale) + " (" + UtilProperties.getMessage(RESOURCE_SALES, "BSBeforeVAT", locale) + ")", "purchasePrice", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(13, UtilProperties.getMessage(RESOURCE_SALES, "BSPurchasePrice", locale) + " (" + UtilProperties.getMessage(RESOURCE_SALES, "BSAfterVAT", locale) + ")", "purchasePriceVAT", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(10, UtilProperties.getMessage(RESOURCE_SALES, "BSQtyConvert", locale), "quantityConvert", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(11, UtilProperties.getMessage(RESOURCE_PO, "BSSalesUomId", locale), "salesUomId");
		addColumn(13, UtilProperties.getMessage(RESOURCE_SALES, "BSSalesPrice", locale) + " (" + UtilProperties.getMessage(RESOURCE_SALES, "BSBeforeVAT", locale) + ")", "salesPrice", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(13, UtilProperties.getMessage(RESOURCE_SALES, "BSSalesPrice", locale) + " (" + UtilProperties.getMessage(RESOURCE_SALES, "BSAfterVAT", locale) + ")", "salesPriceVAT", ExportExcelStyle.STYLE_CELL_CURRENCY);
		//addColumn(20, UtilProperties.getMessage(RESOURCE_SALES, "BSQuotationId", locale), "");
		addColumn(20, UtilProperties.getMessage(RESOURCE_PO, "BSPurchaseDiscontinuationDate", locale), "purchaseDiscontinuationDate");
		addColumn(20, UtilProperties.getMessage(RESOURCE_PO, "BSSalesDiscontinuationDate", locale), "salesDiscontinuationDate");
	}
	
}
