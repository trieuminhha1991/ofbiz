package com.olbius.acc.report.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class ExportExcelGeneralJournal extends ExportExcelAbstract {
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";
	BigDecimal totalCr = BigDecimal.ZERO;
	BigDecimal totalDr = BigDecimal.ZERO;

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
		setRunServiceName("getGeneralJournalNoShort");
		setModuleExport("BASEACCOUNTING");
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
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCTimeLabel", locale), "dateTime");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherDate2", locale), "documentDate");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCAcctgTransId", locale), "acctgTransId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherId", locale), "documentId");
		addColumn(18, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherNumber", locale), "voucherCode");
		addColumn(18, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherNumberSystem", locale), "documentNumber");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerIdShortSys", locale), "partyId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerId", locale), "partyCode");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCustomerName", locale), "partyName");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCDescription", locale), "description");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCAcctgTransTypeId", locale), "acctgTransTypeId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountCode", locale), "accountCode");
		addColumn(25, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountName", locale), "accountName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductStoreId", locale), "productStoreId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductStoreDemension", locale), "productStoreName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCDebitAmount", locale), "drAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCCreditAmount", locale), "crAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
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
			} else if ("drAmount".equals(key)) {
				BigDecimal drAmount = (BigDecimal) map.get("drAmount");
				totalDr = totalDr.add(drAmount);
				value = drAmount;
			} else if ("crAmount".equals(key)) {
				BigDecimal crAmount = (BigDecimal) map.get("crAmount");
				totalCr = totalCr.add(crAmount);
				value = crAmount;
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
	@Override
	public String run() {
		initSheet();
		initHeader();
		initSubTitle();
		addBlankRow();
		initColumnHeader();
		String result = initColumnContent();
		Row totalRow = createRow((short) 350);
		createCell(totalRow, 0, UtilProperties.getMessage(RESOURCE_ACC, "BACCAmountTotal", locale),
				getCellStylesMap().get(ExportExcelStyle.STYLE_CELL_CONTENT_CENTER));
		createCell(totalRow, getColumnNumber() - 2, totalDr, getCellStylesMap().get(ExportExcelStyle.STYLE_CELL_CURRENCY));
		createCell(totalRow, getColumnNumber() - 1, totalCr, getCellStylesMap().get(ExportExcelStyle.STYLE_CELL_CURRENCY));
		getCurrentSheet().addMergedRegion(new CellRangeAddress(getRowNumber(), getRowNumber(), 0, getColumnNumber() - 3));
		return result;
	}
}