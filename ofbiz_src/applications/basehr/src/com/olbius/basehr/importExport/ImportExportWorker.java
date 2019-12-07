package com.olbius.basehr.importExport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;

import com.olbius.basehr.timekeeping.utils.TimekeepingUtils;
import com.olbius.basehr.util.PartyUtil;

public class ImportExportWorker {
	public static String patternDate = "dd/MM/yyyy";
	public static String patternTime = "HH:mm:ss";

	public static void importExcelData(Delegator delegator,
			GenericValue userLogin, ImportExcelConfig config) throws GenericEntityException {
		Workbook wb = config.getExcelData();
		String entityName = config.getEntityName();
		java.sql.Date fromDate = config.getFromDate();
		java.sql.Date thruDate = config.getThruDate();
		ModelEntity modelEntity = delegator.getModelEntity(entityName);
		Map<Integer, String> map = config.getFieldColumnExcelCorr();
		String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		Sheet sheet = wb.getSheetAt(0);
		int rows; // No of rows
		rows = sheet.getLastRowNum();
		int cols = 0; // No of columns
		int startLine = config.getStartLine();
		Map<String, List<String>> fieldBoundaryMap = config.getFieldInListMap();
		Locale locale = config.getLocale();
		if (locale == null) {
			locale = Locale.getDefault();
		}
		DataFormatter dft = new DataFormatter(locale);
		String dateTimePattern = config.getDateTimePattern();
		if (dateTimePattern == null) {
			dateTimePattern = patternDate;
		}
		String overrideDataWay = config.getOverrideDataWay();
		Set<String> partySetExistsInTracker = FastSet.newInstance();
		for (int r = startLine; r <= rows; r++) {
			Row row = sheet.getRow(r);
			if (row != null) {
				GenericValue entityGv = delegator.makeValue(config.getEntityName());
				entityGv.set("orgId", orgId);
				boolean createRecord = true;
				cols = row.getLastCellNum();
				for (int c = 0; c < cols; c++) {
					if (map.get(c) != null) {
						Cell cell = row.getCell(c);
						if (cell != null) {
							String cellStr = dft.formatCellValue(cell);
							String fieldInDB = map.get(c);
							ModelField field = modelEntity.getField(fieldInDB);
							String fieldType = field.getType();
							Object fieldValue = convertExcelColumnTypeToFieldType(cellStr, fieldType, dateTimePattern);
							if("partyId".equals(fieldInDB)){
								List<GenericValue> party = delegator.findByAnd("Party", UtilMisc.toMap("partyCode", fieldValue), null, false);
								if(UtilValidate.isEmpty(party)){
									break;
								}
								fieldValue = party.get(0).getString("partyId");
								if(UtilValidate.isNotEmpty(fieldValue) && "deleteAllData".equals(overrideDataWay) && !partySetExistsInTracker.contains(fieldValue)){
									partySetExistsInTracker.add((String)fieldValue);
									TimekeepingUtils.deleteEmplAttendanceTrackerExists(delegator, (String)fieldValue, fromDate, thruDate, orgId);
								}
							}
							if(UtilValidate.isNotEmpty(fieldValue) && (fieldValue instanceof java.sql.Date) && fromDate != null && thruDate != null){
								Date tempDate = (Date)fieldValue;
								if(tempDate.compareTo(fromDate) < 0 || tempDate.compareTo(thruDate) > 0){
									break;
								}
							}
							List<String> listBoundField = fieldBoundaryMap.get(fieldInDB);
							if (UtilValidate.isEmpty(fieldValue)|| (listBoundField != null && !listBoundField.contains(fieldValue))) {
								break;
							} else {
								entityGv.set(fieldInDB, fieldValue);
							}
						}
					}
				}
				for(Map.Entry<Integer, String> entry: map.entrySet()){
					String value = entry.getValue();
					if(entityGv.get(value) == null){
						createRecord = false;
						break;
					}
				}
				if (createRecord) {
					try {
						delegator.createOrStore(entityGv);
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Object convertExcelColumnTypeToFieldType(String cellValueStr,
			String fieldType, String dateTimePattern) {
		SimpleDateFormat formatDate = new SimpleDateFormat(dateTimePattern);
		SimpleDateFormat formatTime = new SimpleDateFormat(patternTime);
		if ("date".equals(fieldType)) {
			try {
				Date date = formatDate.parse(String.valueOf(cellValueStr));
				return new java.sql.Date(date.getTime());
			} catch (ParseException e) {
				return null;
			}
		} else if ("time".equals(fieldType)) {
			try {
				Date date = formatTime.parse(String.valueOf(cellValueStr));
				return new java.sql.Time(date.getTime());
			} catch (ParseException e) {
				return null;
			}
		} else {
			return cellValueStr;
		}
	}

	public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet) {
		copySheets(newSheet, sheet, true);
	}

	public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet,
			boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>()
				: null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow srcRow = sheet.getRow(i);
			if (srcRow != null && !isEmptyRow(srcRow)) {
				HSSFRow destRow = newSheet.createRow(i);
				copyRow(sheet, newSheet, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for (int i = 0; i <= maxColumnNum; i++) {
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
	}

	public static boolean isEmptyRow(Row row) {
		boolean isEmptyRow = true;
		for (int cellNum = row.getFirstCellNum(); cellNum < row
				.getLastCellNum(); cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
					&& StringUtils.isNotBlank(cell.toString())) {
				isEmptyRow = false;
				break;
			}
		}
		return isEmptyRow;
	}

	public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet,
			HSSFRow srcRow, HSSFRow destRow,
			Map<Integer, HSSFCellStyle> styleMap) {
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
		destRow.setHeight(srcRow.getHeight());
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			HSSFCell oldCell = srcRow.getCell(j);
			HSSFCell newCell = destRow.getCell(j);
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				copyCell(oldCell, newCell, styleMap);
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet,
						srcRow.getRowNum(), (short) oldCell.getColumnIndex());
				if (mergedRegion != null) {
					CellRangeAddress newMergedRegion = new CellRangeAddress(
							mergedRegion.getFirstRow(),
							mergedRegion.getLastRow(),
							mergedRegion.getFirstColumn(),
							mergedRegion.getLastColumn());
					CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(
							newMergedRegion);
					if (isNewMergedRegion(wrapper, mergedRegions)) {
						mergedRegions.add(wrapper);
						destSheet.addMergedRegion(wrapper.range);
					}
				}
			}
		}

	}

	public static void copyCell(HSSFCell oldCell, HSSFCell newCell,
			Map<Integer, HSSFCellStyle> styleMap) {
		if (styleMap != null) {
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet()
					.getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {
				int stHashCode = oldCell.getCellStyle().hashCode();
				HSSFCellStyle newCellStyle = styleMap.get(stHashCode);
				if (newCellStyle == null) {
					newCellStyle = newCell.getSheet().getWorkbook()
							.createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		switch (oldCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}

	}

	public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum,
			short cellNum) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum)) {
				return merged;
			}
		}
		return null;
	}

	private static boolean isNewMergedRegion(
			CellRangeAddressWrapper newMergedRegion,
			Set<CellRangeAddressWrapper> mergedRegions) {
		return !mergedRegions.contains(newMergedRegion);
	}

	public static CellStyle getCellStyle(Workbook wb, Font font, Short alignment, Short verticalAlignment, Short border, Short borderColor) {
		CellStyle style = wb.createCellStyle();
		style.setFont(font);
		style.setAlignment(alignment);
		if (verticalAlignment != null) {
			style.setVerticalAlignment(verticalAlignment);
		}
		style.setWrapText(true);
		
		if (border != null) {
			style.setBorderRight(border);
			style.setRightBorderColor(borderColor);
			style.setBorderBottom(border);
			style.setBottomBorderColor(borderColor);
			style.setBorderLeft(border);
			style.setLeftBorderColor(borderColor);
			style.setBorderTop(border);
			style.setTopBorderColor(borderColor);
		}
		return style;
	}
	public static void createEmptyCell(Row row, int fromCol, int toCol,
			CellStyle style) {
		for(int i = fromCol; i <= toCol; i++){
			Cell tempCell = row.createCell(i);
			tempCell.setCellStyle(style);
		}
	}

	public static void writeDataToRowExcel(Row row, Map<Integer, ? extends Object> data, Map<Integer, CellStyle> mapStyle, CellStyle defaultStyle) {
		for(Entry<Integer, ? extends Object> entry: data.entrySet()){
			Integer column = entry.getKey();
			Object cellValue = entry.getValue();
			CellStyle style = null;
			if(mapStyle != null){
				style = mapStyle.get(column);
			}
			if(style == null){
				style = defaultStyle;
			}
			Cell cell = row.createCell(column);
			cell.setCellStyle(style);
			if(cellValue != null){
				if(cellValue instanceof Double){
					cell.setCellValue((Double)cellValue);
				}else if(cellValue instanceof String){
					cell.setCellValue((String)cellValue);
				}
			}
		}
	}

	public static Map<Integer, Object> readColumnMapFromJson(String columnMapParam) {
		JSONArray columnMapJsonList = JSONArray.fromObject(columnMapParam);
		Map<Integer, Object> retMap = FastMap.newInstance();
		String prefix = "datafield_";
		for(int i = 0; i < columnMapJsonList.size(); i++){
			JSONObject columnMapJson = columnMapJsonList.getJSONObject(i);
			String fieldValueInSys = columnMapJson.getString("fieldValueInSys");
			String fieldType = columnMapJson.getString("fileType");
			if(columnMapJson.has("fieldValueInExcel") && columnMapJson.getString("fieldValueInExcel").length() > 0){
				String fieldValueInExcel = columnMapJson.getString("fieldValueInExcel");
				fieldValueInExcel = fieldValueInExcel.trim();
				String columnIdStr = fieldValueInExcel.substring(prefix.length());
				int columnId = Integer.parseInt(columnIdStr);
				switch (fieldType) {
					case "string":
						retMap.put(columnId, fieldValueInSys);
						break;
					case "date":
						java.sql.Date date = new java.sql.Date(Long.parseLong(fieldValueInSys));
						retMap.put(columnId, date);
						break;
					default:
						break;
				}
			}
		}
		return retMap;
	}

	public static Object getCellValue(Cell cell) {
		if(cell == null){
			return null;
		}
		int cellType = cell.getCellType();
		switch (cellType) {
			case HSSFCell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
			case HSSFCell.CELL_TYPE_NUMERIC:
				return cell.getNumericCellValue();
			case HSSFCell.CELL_TYPE_BLANK:
				return null;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue();
			case HSSFCell.CELL_TYPE_ERROR:
				return cell.getErrorCellValue();
			case HSSFCell.CELL_TYPE_FORMULA:
				return cell.getCellFormula();
			default:
				break;
		}
		return null;
	}
}
