package com.olbius.crm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ExcelUtil {
	public static Map<String, Object> getGeoIdByGeoName(Delegator delegator, Object geoName, Object geoTypeId,
			Object geoId) throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(geoName)) {
			List<EntityCondition> conditions = FastList.newInstance();

			if (UtilValidate.isNotEmpty(geoId)) {
				List<GenericValue> geoAssoc = delegator.findList("GeoAssoc",
						EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId, "geoAssocTypeId", "REGIONS")),
						UtilMisc.toSet("geoIdTo"), null, null, false);
				conditions.add(EntityCondition.makeCondition("geoId", EntityJoinOperator.IN,
						EntityUtil.getFieldListFromEntityList(geoAssoc, "geoIdTo", true)));
			}
			conditions.add(EntityCondition.makeCondition("geoTypeId", EntityJoinOperator.EQUALS, geoTypeId));
			conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("geoName"),
					EntityJoinOperator.EQUALS, correctValue(geoName.toString().trim()).toUpperCase()));
			List<GenericValue> geos = delegator.findList("Geo", EntityCondition.makeCondition(conditions),
					UtilMisc.toSet("geoId", "geoName"), null, null, false);
			if (UtilValidate.isNotEmpty(geos)) {
				GenericValue geo = EntityUtil.getFirst(geos);
				result.put("geoId", geo.get("geoId"));
				result.put("geoName", geo.get("geoName"));
			}
		}
		return result;
	}

	public static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
		Workbook workbook = null;
		if (excelFilePath.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (excelFilePath.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}
		return workbook;
	}

	public static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue().trim();
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().getTime();
			} else {
				return cell.getNumericCellValue();
			}
		}
		return null;
	}

	private static String correctValue(String value) {
		Map<String, String> mapCorrectValue = FastMap.newInstance();
		mapCorrectValue.put("Hà Nội", "Hà Nội");
		if (mapCorrectValue.containsKey(value)) {
			value = mapCorrectValue.get(value);
		}
		return value;
	}
}
