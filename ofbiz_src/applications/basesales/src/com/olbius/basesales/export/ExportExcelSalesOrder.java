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
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

public class ExportExcelSalesOrder extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "DANH_SACH_SO";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSListOrder", locale));
		setRunServiceName("JQListSalesOrder");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSListSalesOrder", locale));
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
		
		// add all columns
		addColumn(6, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSOrderId", locale), "orderId");
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSSalesChannel", locale), "productStoreId");
		addColumn(16, UtilProperties.getMessage("CommonUiLabels", "CommonStatus", locale), "statusId");
		addColumn(19, UtilProperties.getMessage(RESOURCE, "BSCreateDate", locale), "orderDate", ExportExcelStyle.STYLE_CELL_DATETIME);
		addColumn(19, UtilProperties.getMessage(RESOURCE, "BSDesiredDeliveryDate", locale), "fullDeliveryDate", ExportExcelStyle.STYLE_CELL_DATETIME);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomer", locale), "customerCode");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomerName", locale), "customerFullName");
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSPhoneNumber", locale), "fullContactNumber");
		addColumn(16, UtilProperties.getMessage("CommonUiLabels", "CommonAmount", locale), "grandTotal", ExportExcelStyle.STYLE_CELL_DECIMAL_ZERO);
		addColumn(12, UtilProperties.getMessage(RESOURCE, "BSAgreementCode", locale), "agreementCode");
		addColumn(13, UtilProperties.getMessage(RESOURCE, "BSPriority", locale), "priority");
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
						if (status != null) {
							value = status.get("description", locale);
						}
					} catch (GenericEntityException e) {
						Debug.logWarning("Error when get status", module);
					}
					
				}
			} else if ("priority".equals(key)) {
				String priority = (String) map.get("priority");
				if (priority != null) {
					try {
						GenericValue priorityGv = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", priority), false);
						if (priorityGv != null) {
							value = priorityGv.get("description", locale);
						}
					} catch (GenericEntityException e) {
						Debug.logWarning("Error when get priority", module);
					}
				}
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
}
