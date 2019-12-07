package com.olbius.acc.invoice;

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

public class ExportExcelListVoucherInvoice extends ExportExcelAbstract {
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "danh_sach_chung_tu";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "ListVouchers", locale).toUpperCase());
		
		setRunServiceName("JQGetListVoucher");
		setModuleExport("ACC");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "ListVouchers", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// make parameters input
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		setRunParameters(parametersCtx);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSSTT", locale), null);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "VoucherForm", locale), "voucherForm");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "VoucherSerial", locale), "voucherSerial");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "VoucherNumber", locale), "voucherNumber");
		addColumn(22, UtilProperties.getMessage(RESOURCE_ACC, "BACCIssueDate", locale), "issuedDate");
		addColumn(22, UtilProperties.getMessage(RESOURCE_ACC, "PublicationReceivingDate", locale), "voucherCreatedDate");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvoiceId", locale), "invoiceId");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvoiceTypeId", locale), "invoiceTypeId");
		addColumn(20, UtilProperties.getMessage("CommonUiLabels", "CommonStatus", locale), "newStatusId");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvoiceFromParty", locale), "fullNameFrom");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvoiceToParty", locale), "fullNameTo");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductStoreId", locale), "productStoreId");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductStoreDemension", locale), "storeName");
		addColumn(30, UtilProperties.getMessage(RESOURCE_ACC, "BACCDescription", locale), "description");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "AmountNotIncludeTax", locale), "amount", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(25, UtilProperties.getMessage("CommonUiLabels", "CommonTax", locale), "taxAmount", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(25, UtilProperties.getMessage("CommonUiLabels", "CommonTotal", locale), "totalAmount", ExportExcelStyle.STYLE_CELL_NUMBER);
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
			
			if ("newStatusId".equals(key)) {
				String statusId = (String) map.get("newStatusId");
				if(statusId != null){
					GenericValue statusItem = null;
					try {
						statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (UtilValidate.isNotEmpty(statusItem)) {
						value = statusItem.get("description", locale);
					}
				}
			} else if ("invoiceTypeId".equals(key)) {
				String invoiceTypeId = (String) map.get("invoiceTypeId");
				if(invoiceTypeId != null){
					GenericValue invoiceType = null;
					try {
						invoiceType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", invoiceTypeId), false);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (UtilValidate.isNotEmpty(invoiceType)) {
						value = invoiceType.get("description", locale);
					}
				}
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
}