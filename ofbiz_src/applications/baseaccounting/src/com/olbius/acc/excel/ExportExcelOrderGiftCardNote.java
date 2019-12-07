package com.olbius.acc.excel;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelOrderGiftCardNote extends ExportExcelAbstract {
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
		setRunParameters(parameters);
		
		String fileName = "danh-sach-don-hang-phieu-mua-hang";
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCListOrderGiftCard", locale).toUpperCase());
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCListOrderGiftCard", locale));
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		
		setRunServiceName("JQGetListOrderGiftCardNote");
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
		addColumn(15, UtilProperties.getMessage(RESOURCE_POS, "BPOSGiftCardId", locale), "giftCardId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_SALES, "BSOrderDate", locale), "orderDate", ExportExcelStyle.STYLE_CELL_DATETIME);
		addColumn(15, UtilProperties.getMessage(RESOURCE_SALES, "BSProductStoreId", locale), "productStoreId");
		addColumn(30, UtilProperties.getMessage(RESOURCE_SALES, "BSStoreName", locale), "storeName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_POS, "BPOSWorkShiftId", locale), "posTerminalStateId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_POS, "BPOSEmployee", locale), "employeeId");
		addColumn(25, UtilProperties.getMessage("BaseLogisticsUiLabels", "BLEmployeeName", locale), "employeeName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCAmount", locale), "amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
	}
}