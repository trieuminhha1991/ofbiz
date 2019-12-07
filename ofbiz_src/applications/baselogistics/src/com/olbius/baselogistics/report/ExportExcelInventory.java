package com.olbius.baselogistics.report;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExportExcelInventory extends ExportExcelAbstract{
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_PO = "BasePOUiLabels";
	private final String RESOURCE_LOG = "BaseLogisticsUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "danh-sach-hang-ton-kho";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_LOG, "ListInventory", locale).toUpperCase());
		
		setRunServiceName("getInventory");
		setModuleExport("LOG");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_LOG, "ListInventory", locale));
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
//		Map<String, String[]> parametersCtx = FastMap.newInstance();
		setRunParameters(parameters);
		
//		@SuppressWarnings("unchecked")
//		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
//		if (UtilValidate.isNotEmpty(fs)) {
//			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN, fs));
//		}
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        if (UtilValidate.isEmpty(listSortFields)) {
        	listSortFields = UtilMisc.toList("productId", "facilityName");
		}
//        setRunListAllConditions(listAllConditions);
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
		addColumn(30, UtilProperties.getMessage(RESOURCE_LOG, "Facility", locale), "facilityName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "ProductId", locale), "productCode");
		addColumn(35, UtilProperties.getMessage(RESOURCE_LOG, "ProductName", locale), "productName");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "BLCategoryProduct", locale), "primaryProductCategoryId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_SALES, "BSUPC", locale), "idSKU");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "SupplierId", locale), "supplierId");
		addColumn(15, UtilProperties.getMessage(RESOURCE_LOG, "BLDonViTonKho", locale), "uomId");
		addColumn(13, UtilProperties.getMessage(RESOURCE_LOG, "BLPackingForm", locale), "quantityConvert", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(13, UtilProperties.getMessage(RESOURCE_LOG, "QOH", locale), "quantityOnHandTotal", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_PO, "BSAverageCost", locale), "unitCost", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_SALES, "BSValue", locale), "total", ExportExcelStyle.STYLE_CELL_CURRENCY);
	}
	
	@Override
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		Map<String, Object> result = FastMap.newInstance();
		int columnIndex = 0;
		
		if (hasColumnIndex) {
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for (int i = columnIndex; i < columnKeys.size(); i++) {
			Object value = null;
			String key = columnKeys.get(i);
			
			BigDecimal quantityOnHandTotal = (BigDecimal) map.get("quantityOnHandTotal");
			if ("Y".equals(map.get("requireAmount")) && "WEIGHT_MEASURE".equals(map.get("amountUomTypeId"))) {
				quantityOnHandTotal = (BigDecimal) map.get("amountOnHandTotal");
			}
			
			BigDecimal unitCost = null;
			try {
				result = dispatcher.runSync("getProductAverageCostBaseSimple",
						UtilMisc.toMap("ownerPartyId", map.get("ownerPartyId"), "facilityId", map.get("facilityId"),
								"productId", map.get("productId"), "userLogin", userLogin));
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (ServiceUtil.isSuccess(result)) {
				unitCost = (BigDecimal) result.get("unitCost");
			}
			
			BigDecimal total = null;
			if (UtilValidate.isNotEmpty(quantityOnHandTotal) && UtilValidate.isNotEmpty(unitCost)) {
				total = quantityOnHandTotal.multiply(unitCost);
			}
			
			if ("uomId".equals(key)) {
				String uomId = (String) ("Y".equals(map.get("requireAmount")) ? map.get("weightUomId") : map.get("quantityUomId"));
				if (uomId != null) {
					value = getDescriptionUom(delegator, uomId);
				}
			} else if ("quantityConvert".equals(key)) {
				value = getQuantityConvert(delegator, map);
			} else if ("quantityOnHandTotal".equals(key)) {
				value = quantityOnHandTotal;
			} else if ("unitCost".equals(key)) {
				value = unitCost;
			} else if ("total".equals(key)) {
				value = total;
			} else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
	private static String getDescriptionUom(Delegator delegator, String uomId) {
		String description = uomId;
		GenericValue uom = null;
		try {
			uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", uomId), false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(uom)) {
			description = uom.getString("abbreviation");
		}
		return description;
	}
	
	private static BigDecimal getQuantityConvert(Delegator delegator, Map<String, Object> x) {
		BigDecimal quantityConvert = BigDecimal.ONE;
		
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", x.get("productId"), "largest", "Y")));
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			List<GenericValue> configPackings = delegator.findList("ConfigPacking",	EntityCondition.makeCondition(conditions), null, null, null, false);
			if (UtilValidate.isNotEmpty(configPackings)){
				quantityConvert = configPackings.get(0).getBigDecimal("quantityConvert");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return quantityConvert;
	}
}
