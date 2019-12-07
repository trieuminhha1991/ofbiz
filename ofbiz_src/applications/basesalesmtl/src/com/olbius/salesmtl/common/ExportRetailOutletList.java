package com.olbius.salesmtl.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

import javolution.util.FastMap;

public class ExportRetailOutletList extends ExportExcelAbstract{
	private final String RESOURCE_BSMTL = "BaseSalesMtlUiLabels";
	private final String RESOURCE_BS = "BaseSalesUiLabels";
	private final String RESOURCE_DMS = "DmsUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "DANH_SACH_DAI_LY";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_BSMTL, "BSListAgents", locale));
		setRunServiceName("JQGetListAgents");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_BSMTL, "BSListAgents", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyIdFrom = ExportExcelUtil.getParameter(parameters, "partyIdFrom");
		String routeId = ExportExcelUtil.getParameter(parameters, "routeId");
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
		addSubTitle(UtilProperties.getMessage(RESOURCE_BS, "BSDateTime", locale), dateTimeOut);
        if (UtilValidate.isNotEmpty(partyIdFrom)){
            addSubTitle(UtilProperties.getMessage(RESOURCE_BSMTL, "BSDistributorId", locale), partyIdFrom);
        }
        if (UtilValidate.isNotEmpty(routeId)){
            addSubTitle(UtilProperties.getMessage(RESOURCE_BSMTL, "BsRouteId", locale), routeId);
        }
		//addSubTitle(UtilProperties.getMessage(RESOURCE, "BSNote", locale), UtilProperties.getMessage(RESOURCE, "BSThePriceInCludedTax", locale));
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_DMS, "DmsSequenceId", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER );
		addColumn(14, UtilProperties.getMessage(RESOURCE_BSMTL, "BSAgentId", locale), "partyCode", ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
		addColumn(16, UtilProperties.getMessage(RESOURCE_BSMTL, "BSAgentName", locale), "fullName");
		addColumn(14, UtilProperties.getMessage(RESOURCE_DMS, "DmsTelecom", locale), "contactNumber", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(20, UtilProperties.getMessage(RESOURCE_DMS, "DmsAddress", locale), "address1", ExportExcelStyle.STYLE_COLUMN_LABEL);
		addColumn(14, UtilProperties.getMessage(RESOURCE_BSMTL, "BSLongitude", locale), "longitude");
		addColumn(14, UtilProperties.getMessage(RESOURCE_BSMTL, "BSLatitude", locale), "latitude");
		addColumn(14, UtilProperties.getMessage(RESOURCE_DMS, "DmsEmail", locale), "emailAddress");
		addColumn(16, UtilProperties.getMessage(RESOURCE_BSMTL, "BSSalesman", locale), "salesmanName");
		addColumn(14, UtilProperties.getMessage(RESOURCE_DMS, "DmsStatus", locale), "statusId");
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