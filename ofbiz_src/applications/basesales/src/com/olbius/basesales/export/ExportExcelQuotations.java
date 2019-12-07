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

public class ExportExcelQuotations extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "DANH_SACH_SP";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSListProductQuotation", locale));
		setRunServiceName("JQListProductQuotation");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSListProductQuotation", locale));
		setSplitSheet(false);
		
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
        
		// add subtitle rows
		SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSQuotationId", locale), "productQuotationId");
		addColumn(24, UtilProperties.getMessage(RESOURCE, "BSQuotationName", locale), "quotationName");
		addColumn(24, UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale), "storeNames");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCurrencyUomId", locale), "currencyUomId");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSCreateDate", locale), "createDate");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSFromDate", locale), "fromDate");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BSThruDate", locale), "thruDate");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSStatus", locale), "statusId");
	}
	
}
