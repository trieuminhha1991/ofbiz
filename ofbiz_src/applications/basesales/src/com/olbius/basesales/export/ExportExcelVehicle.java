package com.olbius.basesales.export;

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

public class ExportExcelVehicle extends ExportExcelAbstract {
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "DANH SACH XE TAI";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BLVehicle", locale));
		setRunServiceName("JQGetVehicle"); //getAllVehicles
		setModuleExport("VEHICLES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BLVehicleDescription", locale));
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
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSOrderId", locale), "vehicleId");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSSalesChannel", locale), "driverId");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomer", locale), "description");
		addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomerName", locale), "plate");
	}
}
