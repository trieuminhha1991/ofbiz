package com.olbius.basesales.export;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;

public class ExportReportSyncTorProductProdStore extends ExportExcelAbstract{
	private final String RESOURCE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BC_TONG_HOP_DS_SP_THEO_CUA_HANG";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE, "BSPSTurnoverProductSynthesisByChannel", locale));
		setRunServiceName("olapSynTorProductProdStore");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSPSTurnoverProductSynthesisByChannel", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Timestamp fromDate = null;
		Timestamp thruDate = null;
		String fromDateTitle = null;
		String thruDateTitle = null;
		String fromDateStr = ExportExcelUtil.getParameter(parameters, "fromDate");
		String thruDateStr = ExportExcelUtil.getParameter(parameters, "thruDate");
		SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		if (fromDateStr != null) {
			fromDateStr += " 00:00:00.000";
			fromDate = Timestamp.valueOf(fromDateStr);
			fromDateTitle = formatOut.format(fromDate);
		}
		if (thruDateStr != null) {
			thruDateStr += " 23:59:59.999";
			thruDate = Timestamp.valueOf(thruDateStr);
			thruDateTitle = formatOut.format(thruDate);
		}
		setRunParameters(parameters);
        
		// add subtitle rows
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSFromDate", locale), fromDateTitle);
		addSubTitle(UtilProperties.getMessage(RESOURCE, "BSThruDate", locale), thruDateTitle);
		
		List<GenericValue> listProductStore = getProductStore();
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
		addColumn(14, UtilProperties.getMessage(RESOURCE, "BSProductId", locale), "product_code");
		addColumn(32, UtilProperties.getMessage(RESOURCE, "BSProductName", locale), "product_name");
		if (UtilValidate.isNotEmpty(listProductStore)) {
			for (GenericValue prodStore : listProductStore) {
				addColumn(14, prodStore.getString("productStoreId") + " - " + prodStore.getString("storeName"), prodStore.getString("productStoreId"), ExportExcelStyle.STYLE_CELL_CURRENCY);
			}
		}
	}
	
	private List<GenericValue> getProductStore(){
		List<GenericValue> prodStoreDimension = new ArrayList<GenericValue>();
		try {
			prodStoreDimension = this.delegator.findByAnd("ProductStoreDimension", null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError("Error when query ", module);
		}
		return prodStoreDimension;
	};
}
