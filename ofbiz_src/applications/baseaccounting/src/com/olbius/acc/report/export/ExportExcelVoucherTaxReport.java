package com.olbius.acc.report.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelVoucherTaxReport extends ExportExcelAbstract {
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		String fileName = "BAO_CAO_THUE";
		String invoiceType = parameters.get("invoiceType")[0];
		if (UtilValidate.isNotEmpty(invoiceType)) {
			fileName += invoiceType.equals("SALES_INVOICE") ? "_BAN_RA" : "_MUA_VAO";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		if (invoiceType.equals("SALES_INVOICE"))
			setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherTaxSalesReport", locale));
		else 
			setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherTaxPurchaseReport", locale));
		setRunServiceName("JQVoucherTaxReport");
		setModuleExport("BASEACCOUNTING");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherTaxReport", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_ACTIVITY);
		setSplitSheet(false);
		setMaxRowInSheet(100);

		// get parameters content
		
		setRunParameters(parameters);

		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		@SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);

		// add all columns	
		addColumn(8, UtilProperties.getMessage("BaseSalesUiLabels", "BSSTT", locale), null);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCFormInv", locale), "voucherForm");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvCode", locale), "voucherSerial");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvSerialNumber", locale), "voucherNumber");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCIssueDate", locale), "issuedDate", ExportExcelStyle.STYLE_CELL_DATE);
		if (invoiceType.equals("SALES_INVOICE"))
			addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BuyerName", locale), "partyName");
		else
			addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "SellerName", locale), "partyName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCTaxCode", locale), "taxCode");
		if (invoiceType.equals("SALES_INVOICE"))
			addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCSalesValueBeforeTax", locale), "amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		else
			addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCPurchaseValueBeforeTax", locale), "amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCVAT", locale), "taxAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "TaxRate", locale) + " (%)", "taxPercentage", ExportExcelStyle.STYLE_CELL_PERCENT);
		addColumn(35, UtilProperties.getMessage(RESOURCE_ACC, "BACCDescription", locale), "description");
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
			
			String invoiceTypeId = (String) map.get("invoiceTypeId");
			
			if ("taxPercentage".equals(key)) {
				BigDecimal taxPercentage = (BigDecimal) map.get("taxPercentage");
				if (taxPercentage != null) {
					value = taxPercentage.divide(new BigDecimal(100));
				}
			} else if ("amount".equals(key)) {
				BigDecimal amount = (BigDecimal) map.get("amount");
				if ("PURC_RTN_INVOICE".equals(invoiceTypeId)) {
					value = amount.negate();
				} else {
					value = amount;
				}
			} else if ("taxAmount".equals(key)) {
				BigDecimal taxAmount = (BigDecimal) map.get("taxAmount");
				if ("PURC_RTN_INVOICE".equals(invoiceTypeId)) {
					value = taxAmount.negate();
				} else {
					value = taxAmount;
				}
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
}