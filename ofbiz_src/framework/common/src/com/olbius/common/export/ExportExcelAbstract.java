package com.olbius.common.export;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.webapp.event.ExportExcelEvents;

import javolution.util.FastList;
import javolution.util.FastMap;

public abstract class ExportExcelAbstract implements ExportExcelInteface{
	public final String module = ExportExcelAbstract.class.getName();
	private final int ROW_ACCESS_WINDOWN_SIZE = 100;
	private final int MAX_ROW_IN_SHEET = 60000;
	private final int PAGE_SIZE_QUERY_SYNC = 200; // page size query when 10s ahead
	private final int PAGE_SIZE_QUERY = 500;
	
	protected Delegator delegator;
	protected LocalDispatcher dispatcher;
	protected Locale locale;
	protected GenericValue userLogin;
	private Boolean isCheckTimeout;
	private Boolean isForceTimeout;
	protected String exportType;
	
	private int pageSizeQuery;
	private String moduleExport;
	private String descriptionExport;
	private String fileName;
	private String headerName;
	private SXSSFWorkbook wb;
	private Map<String, CellStyle> cellStylesMap;
	private boolean isSplitSheet;
	private int maxRowInSheet;
	private String prefixSheetName;
	private int sheetNumber;
	private int columnNumber;
	private int rowNumber;
	private SXSSFSheet currentSheet;
	private List<Integer> columnLens;
	private List<String> columnTitles;
	protected List<String> columnKeys;
	private List<String> columnStyles;
	private List<Map<String, Object>> listSubtitle;
	private String runServiceName;
	private Map<String, String[]> runParameters;
	private List<EntityCondition> runListAllConditions;
	private List<String> runListSortFields;
	protected boolean hasColumnIndex;
	protected List<CellStyle> cellStyles;
	private int styleNumber;
	
	public ExportExcelAbstract() {}
	
	public ExportExcelAbstract(DispatchContext dctx, Map<String, Object> context) {
		init(dctx, context);
	}
	
	public void init(DispatchContext dctx, Map<String, Object> context){
		this.delegator = dctx.getDelegator();
		this.dispatcher = dctx.getDispatcher();
		this.locale = (Locale) context.get("locale");
		this.userLogin = (GenericValue) context.get("userLogin");
		
		columnLens = new ArrayList<Integer>();
		columnTitles = new ArrayList<String>();
		columnKeys = new ArrayList<String>();
		columnStyles = new ArrayList<String>();
		cellStyles = new ArrayList<CellStyle>();
		listSubtitle = new ArrayList<Map<String, Object>>();
		this.isCheckTimeout = context.get("isCheckTimeout") != null ? (Boolean) context.get("isCheckTimeout") : true;
		this.isForceTimeout = false;
		this.isSplitSheet = false;
		this.maxRowInSheet = MAX_ROW_IN_SHEET;
		this.sheetNumber = 1;
		this.columnNumber = 0;
		this.hasColumnIndex = false;
		this.exportType = ExportExcelEvents.EXPORT_TYPE_ACTIVITY;
		this.styleNumber = 0;
		this.pageSizeQuery = PAGE_SIZE_QUERY;
		
		this.wb = new SXSSFWorkbook(ROW_ACCESS_WINDOWN_SIZE); 
        this.wb.setCompressTempFiles(true);
		this.cellStylesMap = ExportExcelStyle.createStyles(this.wb);
		
		prepareParameters(dctx, context);
		
		if (this.prefixSheetName == null) this.prefixSheetName = "Sheet";
	}
	
	protected abstract void prepareParameters(DispatchContext dctx, Map<String, Object> context);
	
	public String run() {
		
		initSheet();
		
		initHeader();
		
		initSubTitle();
		
		addBlankRow(false);
		
		initColumnHeader();
		
		String result = initColumnContent();
		
		return result;
	}
	
	protected void initSheet() {
		this.currentSheet = (SXSSFSheet) wb.createSheet(prefixSheetName + sheetNumber);
		this.currentSheet.setRandomAccessWindowSize(ROW_ACCESS_WINDOWN_SIZE);// keep ROW_ACCESS_WINDOWN_SIZE rows in memory, exceeding rows will be flushed to disk
		
		// turn on grid lines
		this.currentSheet.setDisplayGridlines(true);
		this.currentSheet.setPrintGridlines(true);
		this.currentSheet.setFitToPage(true);
		this.currentSheet.setHorizontallyCenter(true);
		this.currentSheet.setAutobreaks(true);
		
		this.rowNumber = -1;
		
		setColumnSize();
	}
	
	protected void addColumn(int numCharacter, String columnTitle, String columnKey) {
		addColumn(numCharacter, columnTitle, columnKey, (String) null);
	}
	protected void addColumn(int numCharacter, String columnTitle, String columnKey, String columnStyle) {
		columnLens.add(numCharacter);
		columnTitles.add(columnTitle);
		columnKeys.add(columnKey);
		
		if (columnStyle == null) columnStyle = ExportExcelStyle.STYLE_CELL_CONTENT;
		columnStyles.add(columnStyle);
		
		columnNumber++;
	}
	protected void addColumn(int numCharacter, String columnTitle, String columnKey, CellStyle columnStylePoi) {
		columnLens.add(numCharacter);
		columnTitles.add(columnTitle);
		columnKeys.add(columnKey);
		
		String columnStyle = null;
		if (columnStylePoi != null) {
			columnStyle = "new_cell_style_" + styleNumber;
			cellStylesMap.put(columnStyle, columnStylePoi);
			styleNumber++;
		}
		if (columnStyle == null) columnStyle = ExportExcelStyle.STYLE_CELL_CONTENT;
		columnStyles.add(columnStyle);
		
		columnNumber++;
	}
	
	protected void setColumnSize() {
		for (int i = 0; i < columnLens.size(); i++) {
			int numCharacter = columnLens.get(i);
			this.currentSheet.setColumnWidth(i, numCharacter * 256);
		}
	}
	
	protected void addSubTitle(String cellLabel, String cellValue) {
		Map<String, Object> subtitleItem = new HashMap<String, Object>();
		subtitleItem.put("label", cellLabel);
		subtitleItem.put("value", cellValue);
		listSubtitle.add(subtitleItem);
	}
	
	protected void addBlankRow() {
		addBlankRow(true);
	}
	
	protected void addBlankRow(boolean hasMerge) {
		Row blankRow = createRow((short) 350);
		if (hasMerge) {
			createCell(blankRow, 0, "", null);
			currentSheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, columnNumber - 1));
		}
	}
	
	protected void initHeader() {
		Row spaceRow = createRow((short) 400);
		currentSheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, columnNumber - 1));
		createCell(spaceRow, 0, "", cellStylesMap.get(ExportExcelStyle.STYLE_HEADER_NAME));
		
		// header name
		String headerNameValue = getHeaderName();
		Row rowHeader = createRow((short) 400);
		currentSheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, columnNumber - 1));
		createCell(rowHeader, 0, headerNameValue.toUpperCase(), cellStylesMap.get(ExportExcelStyle.STYLE_HEADER_NAME));
	}
	
	protected void initSubTitle() {
		for (Map<String, Object> item : listSubtitle) {
			Row subTitleRow = createRow((short) 375);
			createCell(subTitleRow, 0, item.get("label"), cellStylesMap.get(ExportExcelStyle.STYLE_SUBTITLE_LABEL));
			createCell(subTitleRow, 2, item.get("value"), cellStylesMap.get(ExportExcelStyle.STYLE_SUBTITLE_CONTENT));
			currentSheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, 1));
			currentSheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 2, columnNumber - 1));
		}
	}
	
	protected void initColumnHeader() {
		Row row = createRow((short) 600);
		CellStyle style = cellStylesMap.get(ExportExcelStyle.STYLE_COLUMN_LABEL);
		for (int i = 0; i < columnTitles.size(); i++) {
			createCell(row, i, columnTitles.get(i), style);
		}
	}
	
	/**
	 * 
	 * @param parameters List parameters send to service get data
	 * @return
	 * @throws GenericServiceException
	 */
	@SuppressWarnings("unchecked")
	protected String initColumnContent() {
		int rowIndex = 1;
		int pagenum = 0;
		int pagesize = isCheckTimeout ? PAGE_SIZE_QUERY_SYNC : pageSizeQuery;
		//int totalRows = 0;
		//int lastRowNum = 0;
		boolean isContinue = true;
		boolean isActivityExport = true;
		if (ExportExcelEvents.EXPORT_TYPE_OLAP.equals(exportType)) isActivityExport = false;
		boolean isFirstRun = false;
		long offset = 0;
		
		List<EntityCondition> listAllConditions = getRunListAllConditions();
		List<String> listSortFields = getRunListSortFields();
		if (listAllConditions == null) listAllConditions = FastList.newInstance();
		if (listSortFields == null) listSortFields = FastList.newInstance();
		EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
		
		String serviceName = getRunServiceName();
		Map<String, String[]> parameters = getRunParameters();
		if (parameters == null) parameters = FastMap.newInstance();
		
		// prepare get cell style
		for (String item : columnStyles) {
			cellStyles.add(cellStylesMap.get(item));
		}
		
		if (columnKeys.get(0) == null) hasColumnIndex = true;
		
		try {
			long startTime = System.currentTimeMillis();
			Map<String, String[]> parametersCtx = FastMap.newInstance();
			Map<String, Object> context = FastMap.newInstance();
			while (isContinue) {
				parametersCtx.clear();
				parametersCtx.putAll(parameters);
				parametersCtx.put("pagenum", new String[]{String.valueOf(pagenum)});
				parametersCtx.put("pagesize", new String[]{String.valueOf(pagesize)});
				
				context.clear();
				if (isActivityExport) {
					parametersCtx.put("isInit", new String[]{String.valueOf(isFirstRun)});
					context.put("listAllConditions", listAllConditions);
					context.put("listSortFields", listSortFields);
					context.put("opts", opts);
					context.put("parameters", parametersCtx);
					context.put("userLogin", userLogin);
					context.put("locale", locale);
				} else {
					for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
						String key = entry.getKey();
						Object value = null;
						String[] valueMap = entry.getValue();
						if (valueMap != null && valueMap.length > 0) {
							if (valueMap.length == 1) {
								value = valueMap[0];
							} else {
								key += "[]";
								value = valueMap;
							}
						}
						context.put(key, value);
					}
					String fromDateStr = ExportExcelUtil.getParameter(parameters, "fromDate");
					String thruDateStr = ExportExcelUtil.getParameter(parameters, "thruDate");
					Timestamp fromDate = null;
					Timestamp thruDate = null;
					if (fromDateStr != null) {
						fromDateStr += " 00:00:00.000";
						fromDate = Timestamp.valueOf(fromDateStr);
					}
					if (thruDateStr != null) {
						thruDateStr += " 23:59:59.999";
						thruDate = Timestamp.valueOf(thruDateStr);
					}
					context.put("fromDate", fromDate);
					context.put("thruDate", thruDate);
					context.put("olapType", "GRID");
					//context.put("dateType", dateTypeStr);
					context.put("limit", pagesize);
					context.put("offset", offset);
					context.put("init", isFirstRun);
					context.put("userLogin", userLogin);
					context.put("locale", locale);
				}
				
				Map<String, Object> contextCtx = ServiceUtil.setServiceFields(dispatcher, serviceName, context, userLogin, null, locale);
				Map<String, Object> resultService = dispatcher.runSync(serviceName, contextCtx);
				if (ServiceUtil.isError(resultService)) {
					Debug.logError(ServiceUtil.getErrorMessage(resultService), module);
					return ExportExcelEvents.RESULT_ERROR;
				}
				
				List<Map<String, Object>> listData = null;
				if (isActivityExport) {
					Map<String, Object> resultData = processDataFromResult(resultService, pagenum, pagesize, locale);
					listData = (List<Map<String, Object>>) resultData.get("listIterator");
					/*if (isFirstRun) {
						String totalRowsStr = (String) resultData.get("TotalRows");
						if (UtilValidate.isNotEmpty(totalRowsStr)) {
							totalRows = Integer.parseInt(totalRowsStr);
						}
					}*/
				} else {
					listData = (List<Map<String, Object>>) resultService.get("data");
					/*if (isFirstRun) {
						Integer totalRowsStr = (Integer) resultService.get("totalsize");
						if (UtilValidate.isNotEmpty(totalRowsStr)) {
							totalRows = totalRowsStr.intValue();
							isFirstRun = false;
						}
					}*/
					offset += pagesize;
				}
				if (ServiceUtil.isSuccess(resultService) && UtilValidate.isNotEmpty(listData)) { // && totalRows > 0
					if (!isCheckTimeout) {
						try {
							Thread.sleep(3000L);
						} catch (InterruptedException e) {
							Debug.logError("Error when sleep when process Export excel", module);
						}
					}
					
					pagenum++;
					/*lastRowNum = lastRowNum + listData.size();
					if (lastRowNum >= totalRows) {
						isContinue = false;
					}*/
					if (pagesize > listData.size()) {
						isContinue = false;
					}
					
					for (Map<String, Object> map : listData) {
						Row row = createRow(null);
						
						initCells(map, rowIndex, row);
						
						rowIndex++;
					}
				} else {
					isContinue = false;
				}
				
				if (isCheckTimeout) {
					long countTime = System.currentTimeMillis();
					if (countTime - startTime > ExportExcelEvents.MAX_TIME_TIME_OUT) {
						return ExportExcelEvents.RESULT_TIMEOUT;
					}
				}
			}
		} catch (GenericServiceException | GeneralServiceException e) {
			if (!isForceTimeout) Debug.logError(e, module);
			return ExportExcelEvents.RESULT_ERROR;
		}
		return ExportExcelEvents.RESULT_SUCCESS;
	}
	
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		int columnIndex = 0;
		
		if (hasColumnIndex) {
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for (int i = columnIndex; i < columnKeys.size(); i++) {
			Object value = (Object) map.get(columnKeys.get(i));
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
	protected Row createRow(Short rowHeight) {
		//if (rowHeight == null) rowHeight = 375;
		if (isSplitSheet && rowNumber >= maxRowInSheet) {
			sheetNumber++;
			initSheet();
			initColumnHeader();
		}
		rowNumber++;
		Row row = currentSheet.createRow(rowNumber);
		if (rowHeight != null) row.setHeight(rowHeight);
		
		return row;
	}
	
	protected Cell createCell(Row row, int columnIndex, Object value, CellStyle cellStyle) {
		if (row == null) return null;
		
		Cell cell = row.createCell(columnIndex);
		if (value != null) {
			if (value instanceof String) cell.setCellValue((String) value);
			else if (value instanceof Integer) cell.setCellValue((Integer) value);
			else if (value instanceof Double) cell.setCellValue((Double) value);
			else if (value instanceof Long) cell.setCellValue((Long) value);
			else if (value instanceof Timestamp) {
				if (cellStyle.getDataFormat() == 0) cellStyle = cellStylesMap.get(ExportExcelStyle.STYLE_CELL_DATETIME);
				Date cellValue = new Date(((Timestamp) value).getTime());
				cell.setCellValue(cellValue);
			} else if (value instanceof Date) {
				if (cellStyle.getDataFormat() == 0) cellStyle = cellStylesMap.get(ExportExcelStyle.STYLE_CELL_DATETIME);
				cell.setCellValue((Date) value);
			} else if (value instanceof BigDecimal) {
				if (value != null) {
					double cellValue = ((BigDecimal) value).doubleValue();
					cell.setCellValue(cellValue);
				}
			} else {
				if (value != null) cell.setCellValue(String.valueOf(value));
			}
		}
		if (cellStyle != null) cell.setCellStyle(cellStyle);
		return cell;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> processDataFromResult(Map<String, Object> resultService, int iIndex, int iSize, Locale locale) {
		Map<String, Object> successResult = new HashMap<String, Object>();
		List<Map<String, Object>> resultValue = new ArrayList<Map<String, Object>>();
		String totalRows = (String) resultService.get("TotalRows");
		if (totalRows == null) totalRows = "0";
		successResult.put("listIterator", resultValue);
		successResult.put("TotalRows", totalRows);
		
		if (ServiceUtil.isError(resultService)) {
			return successResult;
		}
		Object resultList = (Object) resultService.get("listIterator");
		if (resultList == null) {
			return successResult;
		}
		
		if (resultList instanceof EntityListIterator) {
			List<GenericValue> listGenericValue = null;
			EntityListIterator tmpList = null;
			try {
				tmpList = (EntityListIterator) resultList;
				if (UtilValidate.isEmpty(resultService.get("TotalRows"))) {
					if (iSize != 0) {
						if (iIndex == 0) {
							listGenericValue = tmpList.getPartialList(0, iSize);
						} else {
							listGenericValue = tmpList.getPartialList(iIndex * iSize + 1, iSize);
						}
					} else {
						listGenericValue = tmpList.getCompleteList();
					}
					totalRows = String.valueOf(tmpList.getResultsSizeAfterPartialList());
				} else {
					listGenericValue = tmpList.getCompleteList();
				}
			} catch (Exception e) {
				Debug.logError(e, module);
			} finally {
				if (tmpList != null)
					try {
						tmpList.close();
					} catch (GenericEntityException e) {
						Debug.logError(e, module);
					}
			}
			if (listGenericValue != null && !listGenericValue.isEmpty()) {
				for (GenericValue item : listGenericValue) {
					Map<String, Object> mapItem = item.getAllFields();
					resultValue.add(mapItem);
				}
			}
		} else if (resultList instanceof List){
			List<Object> list = (List<Object>) resultList;
			if (iSize != 0 && (list.size() > iSize)) {
				if (iIndex == 0) {
					list = list.subList(0, iSize);
				} else {
					int toIndex = iIndex * iSize + iSize;
					if (list.size() > toIndex) {
						list = list.subList(iIndex * iSize, iIndex * iSize + iSize);
					} else {
						list = list.subList(iIndex * iSize, list.size());
					}
				}
			}
			if (list.size() > 0) {
				Object firstItem = list.get(0);
				if (firstItem instanceof Map) {
					resultValue = (List<Map<String, Object>>) resultList;
				} else if (firstItem instanceof GenericValue){
					for (Object item : list) {
						GenericValue itemGv = (GenericValue) item;
						Map<String, Object> mapItem = itemGv.getAllFields();
						resultValue.add(mapItem);
					}
				} else {
					Class<?> clazz = firstItem.getClass();
					Field[] fields = clazz.getDeclaredFields();
					if (UtilValidate.isNotEmpty(fields)) {
						for (Object item : list) {
							Map<String, Object> mapItem = new HashMap<String, Object>();
							for (Field field : fields) {
					            String key = field.getName();
					            Class<?> keyType = field.getType();
					            String methodName = null;
					            if (keyType.equals(boolean.class)) {
					            	methodName = "is" + key.substring(0, 1).toUpperCase() + key.substring(1);
					            } else {
					            	methodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
					            }
					            try {
						            Method method = clazz.getMethod(methodName);
						            if (method != null) {
						            	Object mapValue = method.invoke(item);
						            	mapItem.put(key, mapValue);
						            }
					            } catch (Exception e1) {
					            	// do nothing, continue
									//Debug.logWarning("Missing when invoke method: " + methodName, module);
								}
							}
							resultValue.add(mapItem);
						}
					}
				}
			}
		}
		successResult.put("listIterator", resultValue);
		successResult.put("TotalRows", totalRows);
		return successResult;
	}
	
	public Sheet getCurrentSheet() {
		return currentSheet;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public Map<String, CellStyle> getCellStylesMap() {
		return cellStylesMap;
	}

	public void setPrefixSheetName(String prefixSheetName) {
		this.prefixSheetName = prefixSheetName;
	}

	
	public int getPageSizeQuery() {
		return pageSizeQuery;
	}
	public void setPageSizeQuery(int pageSizeQuery) {
		this.pageSizeQuery = pageSizeQuery;
	}
	public String getModuleExport() {
		return moduleExport;
	}
	public void setModuleExport(String moduleExport) {
		this.moduleExport = moduleExport;
	}
	public String getDescriptionExport() {
		return descriptionExport;
	}
	public void setDescriptionExport(String descriptionExport) {
		this.descriptionExport = descriptionExport;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getHeaderName() {
		return headerName;
	}
	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public int getSizeRowInSheet() {
		return maxRowInSheet;
	}

	public void setSizeRowInSheet(int sizeRowInSheet) {
		this.maxRowInSheet = sizeRowInSheet;
	}

	public boolean isSplitSheet() {
		return isSplitSheet;
	}

	public void setSplitSheet(boolean isSplitSheet) {
		this.isSplitSheet = isSplitSheet;
	}

	public int getMaxRowInSheet() {
		return maxRowInSheet;
	}

	public void setMaxRowInSheet(int maxRowInSheet) {
		this.maxRowInSheet = maxRowInSheet;
	}

	public SXSSFWorkbook getWb() {
		return wb;
	}

	public Delegator getDelegator() {
		return delegator;
	}

	public void setDelegator(Delegator delegator) {
		this.delegator = delegator;
	}

	public LocalDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(LocalDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Boolean getIsCheckTimeout() {
		return isCheckTimeout;
	}

	public void setIsCheckTimeout(Boolean isCheckTimeout) {
		this.isCheckTimeout = isCheckTimeout;
	}

	public Boolean getIsForceTimeout() {
		return isForceTimeout;
	}

	public void setIsForceTimeout(Boolean isForceTimeout) {
		this.isForceTimeout = isForceTimeout;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getRunServiceName() {
		return runServiceName;
	}

	public void setRunServiceName(String runServiceName) {
		this.runServiceName = runServiceName;
	}

	public Map<String, String[]> getRunParameters() {
		return runParameters;
	}

	public void setRunParameters(Map<String, String[]> runParameters) {
		this.runParameters = runParameters;
	}

	public List<EntityCondition> getRunListAllConditions() {
		return runListAllConditions;
	}

	public void setRunListAllConditions(List<EntityCondition> runListAllConditions) {
		this.runListAllConditions = runListAllConditions;
	}

	public List<String> getRunListSortFields() {
		return runListSortFields;
	}

	public void setRunListSortFields(List<String> runListSortFields) {
		this.runListSortFields = runListSortFields;
	}
}
