package com.olbius.basesales.forecast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.olbius.basehr.importExport.ImportExportWorker;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class SalesForecastWorker {
	public static final String module = SalesForecastWorker.class.getName();
	
	public static List<GenericValue> getDescendantSalesForecast(Delegator delegator, String parentSalesForecastId) throws GenericEntityException {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		GenericValue sf = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", parentSalesForecastId), false);
		if (sf != null) {
			returnValue.add(sf);
			List<GenericValue> sfChilds = delegator.findByAnd("SalesForecast", UtilMisc.toMap("parentSalesForecastId", parentSalesForecastId), null, false);
			if (UtilValidate.isNotEmpty(sfChilds)) {
				for (GenericValue item : sfChilds) {
					returnValue.addAll(getDescendantSalesForecast(delegator, item.getString("salesForecastId")));
				}
			}
		}
		return returnValue;
	}
	
	/*
	 * List<EntityCondition> listAllCondition = FastList.newInstance();
	 * listAllCondition.add(SALES_MONTH);
	 */
	public static List<GenericValue> getPeriodChildrenFull(String customTimePeriodId, Delegator delegator) throws GenericEntityException {
		List<GenericValue> result = new ArrayList<GenericValue>();
		EntityFindOptions periodFindOptions = new EntityFindOptions();
		periodFindOptions.setDistinct(true);
		List<GenericValue> periodChildren = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("parentPeriodId", customTimePeriodId), null, UtilMisc.toList("periodName"), periodFindOptions, false);
		for (GenericValue child : periodChildren) {
			if (!"SALES_MONTH".equals(child.getString("periodTypeId"))) {
				List<GenericValue> ppItems = getPeriodChildrenFull(child.getString("customTimePeriodId"), delegator);
				if (ppItems != null) result.addAll(ppItems);
			} else {
				result.add(child);
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> processRowData(Map<String, Object> productItem, Map<String, Object> rowDetailCommon) {
		Map<String, Object> rowDetailFinal = FastMap.newInstance();
		rowDetailFinal.put("productId", productItem.get("productId"));
		rowDetailFinal.put("productCode", productItem.get("productCode"));
		rowDetailFinal.put("internalName", productItem.get("internalName"));
		rowDetailFinal.put("productName", productItem.get("productName"));
		rowDetailFinal.put("quantityUomId", productItem.get("quantityUomId"));
		rowDetailFinal.put("parentProductId", productItem.get("parentProductId"));
		rowDetailFinal.put("isVirtual", productItem.get("isVirtual"));
		rowDetailFinal.put("isVariant", productItem.get("isVariant"));
		rowDetailFinal.put("features", productItem.get("features"));
		for (Map.Entry<String, Object> entry : rowDetailCommon.entrySet()) {
			Map<String, Object> cellValue = (Map<String, Object>) entry.getValue();
			String m_customTimePeriodId = entry.getKey();
			String m_salesForecastId = (String) cellValue.get("salesForecastId");
			String m_salesForecastDetailId = (String) cellValue.get("salesForecastDetailId");
			if (UtilValidate.isNotEmpty(m_salesForecastId)) rowDetailFinal.put(m_customTimePeriodId + "_sf", m_salesForecastId);
			if (UtilValidate.isNotEmpty(m_salesForecastDetailId)) rowDetailFinal.put(m_customTimePeriodId + "_sfi", m_salesForecastDetailId);
			rowDetailFinal.put(m_customTimePeriodId, cellValue.get("quantity"));
			rowDetailFinal.put(m_customTimePeriodId + "_old", cellValue.get("quantity"));
		}
		return rowDetailFinal;
	}
	
	public static Map<String, Object> getForecastHeaderVertical(String partyId, List<GenericValue> listCustomTimePeriod, List<GenericValue> listSalesForeCast, Map<String, Object> product, Delegator delegator) throws GenericEntityException {
		String productId = (String) product.get("productId");
		Map<String, Object> returnValue = FastMap.newInstance(); // [month1 = value, month2 = value]
		
		List<EntityCondition> filterParty = FastList.newInstance();
		if ("Other" == partyId) {
			filterParty.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.EQUALS, null));
		} else {
			filterParty.add(EntityCondition.makeCondition("internalPartyId", partyId));
		}
		EntityCondition filterAll = EntityCondition.makeCondition(filterParty, EntityOperator.AND);
		List<GenericValue> getListForecastFiltered = EntityUtil.filterByCondition(listSalesForeCast, filterAll);
		
		if (getListForecastFiltered != null) {
			if ((getListForecastFiltered != null && getListForecastFiltered.size() > 0)) {
				for (GenericValue periodItem : listCustomTimePeriod) {
					String periodId = periodItem.getString("customTimePeriodId");
					BigDecimal quantityTmp = BigDecimal.ZERO;
					Map<String, Object> valueMap = FastMap.newInstance();
					
					List<GenericValue> getListForecast2 = FastList.newInstance();
					if ("SALES_MONTH".equals(periodItem.getString("periodTypeId"))) {
						EntityCondition filterByPeriod = EntityCondition.makeCondition("customTimePeriodId", periodId);
						getListForecast2 = EntityUtil.filterByCondition(getListForecastFiltered, filterByPeriod);
					} else {
						List<String> m_customTimePeriodIds = EntityUtil.getFieldListFromEntityList(getPeriodChildrenFull(periodId, delegator), "customTimePeriodId", true);
						if (UtilValidate.isNotEmpty(m_customTimePeriodIds)) {
							EntityCondition filterByPeriod = EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, m_customTimePeriodIds);
							getListForecast2 = EntityUtil.filterByCondition(getListForecastFiltered, filterByPeriod);
						}
					}
					if ("SALES_MONTH".equals(periodItem.getString("periodTypeId"))) {
						if (UtilValidate.isNotEmpty(getListForecast2)) {
							for (GenericValue forecast : getListForecast2) {
								List<GenericValue> forecastDetailItems = delegator.findByAnd("SalesForecastDetail", UtilMisc.toMap("salesForecastId", forecast.getString("salesForecastId")), null, false);
								List<String> productListFinded = EntityUtil.getFieldListFromEntityList(forecastDetailItems, "productId", true);
								if (productListFinded != null && productListFinded.size() > 0) {
									if (productListFinded.contains(productId)) {
										boolean isFinded = false;
										for (GenericValue forecastItem : forecastDetailItems) {
											String productIdTmp = forecastItem.getString("productId");
											if (UtilValidate.isNotEmpty(productIdTmp)) {
												if (productId.equals(productIdTmp)) {
													quantityTmp = forecastItem.getBigDecimal("quantity");
													valueMap.put("salesForecastId", forecast.getString("salesForecastId"));
													valueMap.put("salesForecastDetailId", forecastItem.getString("salesForecastDetailId"));
													isFinded = true;
													break;
												}
											}
										}
										if (isFinded) break;
									}
								}
							}
						}
					} else {
						if (UtilValidate.isNotEmpty(getListForecast2)) {
							for (GenericValue forecast : getListForecast2) {
								List<GenericValue> forecastDetailItems = delegator.findByAnd("SalesForecastDetail", UtilMisc.toMap("salesForecastId", forecast.getString("salesForecastId")), null, false);
								List<String> productListFinded = EntityUtil.getFieldListFromEntityList(forecastDetailItems, "productId", true);
								if (productListFinded != null && productListFinded.size() > 0) {
									if (productListFinded.contains(productId)) {
										for (GenericValue forecastItem : forecastDetailItems) {
											String productIdTmp = forecastItem.getString("productId");
											if (UtilValidate.isNotEmpty(productIdTmp)) {
												if (productId.equals(productIdTmp)) {
													quantityTmp = quantityTmp.add(forecastItem.getBigDecimal("quantity"));
												}
											}
										}
									}
								}
							}
						}
					}
					valueMap.put("customTimePeriod", periodItem);
					valueMap.put("quantity", quantityTmp);
					returnValue.put(periodId, valueMap);
				}
			} else {
				// skip QUY, NAM
			}
		}
		return returnValue;
	}
	public static List<Map<String,Object>> getSalesFCSheetDetail(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Locale locale,
												   ByteBuffer uploadedFile, Map<Integer, Object> columnExcelMap, Integer sheetIndex, int startLine) throws IOException {
		List<Map<String, Object>> result = FastList.newInstance();
		Map<String, Object> productRow = FastMap.newInstance();
		ByteArrayInputStream bais = new ByteArrayInputStream(uploadedFile.array());
		Workbook wb = new XSSFWorkbook(bais);
		Sheet sheetImport = wb.getSheetAt(sheetIndex);
		int rows = sheetImport.getLastRowNum();
		int cols = 0; // No of columns
		for(int i = startLine; i <= rows; i++){
			productRow = FastMap.newInstance();
			Row row = sheetImport.getRow(i);
			if(!ImportExportWorker.isEmptyRow(row)){
				cols = row.getLastCellNum();
				for (int c = 0; c < cols; c++) {
					if(columnExcelMap.get(c) != null){
						Object fieldValue = columnExcelMap.get(c);
						Cell cell = row.getCell(c);
						Object cellValue = ImportExportWorker.getCellValue(cell);
						if(cell != null && cellValue != null){
							if("productCode".equals(fieldValue) && cellValue instanceof String){
								productRow.put("productCode", cellValue);
							}else if(cellValue instanceof Double){
								productRow.put( (String)fieldValue, cellValue);
							}
						}
					}
				}
				result.add(productRow);
			}
		}
		return result;
	}
}
