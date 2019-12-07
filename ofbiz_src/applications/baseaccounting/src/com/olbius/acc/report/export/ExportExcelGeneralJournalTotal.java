package com.olbius.acc.report.export;

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

public class ExportExcelGeneralJournalTotal extends ExportExcelAbstract {
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";
	private final String RESOURCE_ACC_OTHER = "AccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "SO_NHAT_KY_CHUNG";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCGeneralJournal", locale));
		setRunServiceName("getGeneralJournalTotalV2");
		setModuleExport("ACC");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCGeneralJournal", locale));
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
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC_OTHER, "FormFieldTitle_transactionDate", locale), "transactionDate");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerId", locale), "partyId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerName", locale), "partyName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCDescription", locale), "description");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCAcctgTransTypeId", locale), "acctgTransTypeId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountCode", locale), "accountCode");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountName", locale), "accountName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCRecipGlAccountCode", locale), "accountRecipCode");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCRecipGlAccountName", locale), "accountRecipName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCDebitAmount", locale), "drAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCreditAmount", locale), "crAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCDeliveryId", locale), "deliveryId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductStoreDemension", locale), "facilityId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductStoreId", locale), "facilityName");
	}
	
}
