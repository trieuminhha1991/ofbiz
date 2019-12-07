package com.olbius.acc.report.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;
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

public class ExportExcelVoucherDeclaration extends ExportExcelAbstract {
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BANG_KE_KHAI_CHUNG_TU";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherDeclaration", locale));
		setRunServiceName("getGeneralJournal");
		setModuleExport("BASEACCOUNTING");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherDeclaration", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
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
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCTimeLabel", locale), "dateTime");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherDate2", locale), "documentDate");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherId", locale), "documentId");
		addColumn(18, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherNumber", locale), "voucherCode");
		addColumn(18, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherNumberSystem", locale), "documentNumber");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerId", locale), "partyId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerName", locale), "partyName");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCDescription", locale), "description");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCAcctgTransTypeId", locale), "acctgTransTypeId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountCode", locale), "accountCode");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountName", locale), "accountName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCRecipGlAccountCode", locale), "accountRecipCode");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCRecipGlAccountName", locale), "accountRecipName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCDebitAmount", locale), "drAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCreditAmount", locale), "crAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(15, UtilProperties.getMessage("BaseSalesUiLabels", "BSOrderId", locale), "orderId");
		addColumn(15, UtilProperties.getMessage("BaseSalesUiLabels", "BSReturnId", locale), "returnId");
		addColumn(18, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductId", locale), "productCode");
		addColumn(30, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductName", locale), "productName");
		addColumn(20, UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesChannel", locale), "salesMethodChannelName");
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
			
			if ("acctgTransTypeId".equals(key)) {
				GenericValue type = null;
				try {
					type = delegator.findOne("AcctgTransType", UtilMisc.toMap("acctgTransTypeId", map.get("acctgTransTypeId")), false);
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (UtilValidate.isNotEmpty(type)) {
					value = type.get("description", locale);
				}
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
}