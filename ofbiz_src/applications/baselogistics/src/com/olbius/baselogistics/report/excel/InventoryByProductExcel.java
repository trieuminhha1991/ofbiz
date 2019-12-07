package com.olbius.baselogistics.report.excel;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class InventoryByProductExcel extends ExportExcelAbstract{
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_LOG = "BaseLogisticsUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "BAO_CAO_SO_NGAY_LUU_KHO_";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_LOG, "BLReportInventoryByDate", locale).toUpperCase());
		
		setRunServiceName("JQListInventoryByProductReport");
		setModuleExport("LOG");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_LOG, "BLReportInventoryByDate", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String listFacilities = ExportExcelUtil.getParameter(parameters, "listFacilities");
		List<String> fs = FastList.newInstance();
		if (UtilValidate.isNotEmpty(listFacilities)) {
			JSONArray x = JSONArray.fromObject(listFacilities);
			for (Object o : x) {
				JSONObject f = JSONObject.fromObject(o);
				if (UtilValidate.isNotEmpty(f)) {
					fs.add(f.getString("facilityId"));
				}
			}
		}
		
		// make parameters input
		setRunParameters(parameters);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add subtitle rows
		if (UtilValidate.isNotEmpty(fs)) {
			String subTitleValue = "";
			for (String it : fs) {
				subTitleValue += it;
				if (fs.indexOf(it) != fs.size() - 1) {
					subTitleValue += ", ";
				}
			}
			addSubTitle(UtilProperties.getMessage(RESOURCE_LOG, "Facility", locale), subTitleValue);
		}
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSSTT", locale), null);
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "FacilityId", locale), "facilityId");
		addColumn(30, UtilProperties.getMessage(RESOURCE_LOG, "BLCategoryProduct", locale), "categoryId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "BLSKUCode", locale), "productCode");
		addColumn(30, UtilProperties.getMessage(RESOURCE_LOG, "ProductName", locale), "productName");
		addColumn(40, UtilProperties.getMessage(RESOURCE_SALES, "BSUPC", locale), "idSKU");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "BLQuantityEATotal", locale), "qohEA");
		addColumn(13, UtilProperties.getMessage(RESOURCE_LOG, "BLPackingForm", locale), "quantityConvert", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(13, UtilProperties.getMessage(RESOURCE_LOG, "BLQuantityByQCUom", locale), "qohQC", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(13, UtilProperties.getMessage(RESOURCE_LOG, "NumberDayInInventory", locale), "numberDayInv", ExportExcelStyle.STYLE_CELL_NUMBER);
	}
	
	@Override
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		int columnIndex = 0;
		
		if (hasColumnIndex) {
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for (int i = columnIndex; i < columnKeys.size(); i++) {
			String key = columnKeys.get(i);
			Object value = (Object) map.get(key);
			if ("quantityConvert".equals(key) && UtilValidate.isEmpty(value)) {
				value = BigDecimal.ONE;
			} 
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
}
