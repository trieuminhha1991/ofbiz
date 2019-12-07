package com.olbius.acc.excel;

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

public class ExportExcelOrderReceiptNote extends ExportExcelAbstract {
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";
	private final String RESOURCE_POS = "BasePosUiLabels";
	private final String RESOURCE_SALES = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		// make parameters input
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String isShowAll = ExportExcelUtil.getParameter(parameters, "isShowAll");
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.put("isShowAll", new String[] { isShowAll });
		setRunParameters(parametersCtx);
		
		String fileName = "";
		if (UtilValidate.isEmpty(isShowAll)) {
			fileName = "nhan-tien-tu-ngan-hang";
			setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCTakeMoneyFromBank", locale).toUpperCase());
			setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCTakeMoneyFromBank", locale));
		} else {
			fileName = "danh-sach-don-hang-pos";
			setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCListOrderPOS", locale).toUpperCase());
			setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCListOrderPOS", locale));
		}
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		
		setRunServiceName("JQGetListOrderReceiptNote");
		setModuleExport("BASEACCOUTING");
		
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSSTT", locale), null);
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCOrderId", locale), "orderId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_POS, "BPOSReceiptId", locale), "receiptId");
		if (UtilValidate.isNotEmpty(isShowAll)) {
			addColumn(20, UtilProperties.getMessage("CommonUiLabels", "CommonStatus", locale), "statusId");
		} 
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCBankId", locale), "bankId");
		addColumn(40, UtilProperties.getMessage(RESOURCE_ACC, "BACCBankName", locale), "bankName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_SALES, "BSOrderDate", locale), "orderDate", ExportExcelStyle.STYLE_CELL_DATETIME);
		addColumn(15, UtilProperties.getMessage(RESOURCE_SALES, "BSProductStoreId", locale), "productStoreId");
		addColumn(30, UtilProperties.getMessage(RESOURCE_SALES, "BSStoreName", locale), "storeName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_POS, "BPOSWorkShiftId", locale), "posTerminalStateId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_POS, "BPOSEmployee", locale), "employeeId");
		addColumn(25, UtilProperties.getMessage("BaseLogisticsUiLabels", "BLEmployeeName", locale), "employeeName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvoiceId", locale), "invoiceId");
		addColumn(22, UtilProperties.getMessage(RESOURCE_ACC, "BACCPaymentId", locale), "paymentId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCAmount", locale), "amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCPaymentAmount", locale), "amountApplied", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCPaymentAmountNotApply", locale), "amountNotApply", ExportExcelStyle.STYLE_CELL_CURRENCY);
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
			
			if ("statusId".equals(key)) {
				String statusId = (String) map.get("statusId");
				if (statusId != null) {
					try {
						GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
						if (UtilValidate.isNotEmpty(status)) {
							value = status.get("description", locale);
						}
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
}