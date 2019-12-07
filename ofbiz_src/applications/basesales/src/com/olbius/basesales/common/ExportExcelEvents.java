package com.olbius.basesales.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.product.ProductPriceServices;
import com.olbius.basesales.product.ProductWorker;
import com.olbius.basesales.util.ExportExcelUtil;
import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;

public class ExportExcelEvents {
	public final static String RESOURCE = "BaseSalesUiLabels";
	public final static String RESOURCE_ERROR = "BaseSalesErrorUiLabels";
	public static String module = ExportExcelEvents.class.getName();
	
	@SuppressWarnings({ "unchecked", "unused" })
	public static void exportSales4cExcel(HttpServletRequest request, HttpServletResponse response) throws IOException, GenericEntityException, ParseException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		String mainTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSByStore", locale);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = "BAO_CAO_BAN_HANG";
			String salesStatementId = (String) request.getParameter("salesStatementId");
			String productIdsStr = (String) request.getParameter("productIds");
			
			String statementTypeIdStr = "";
			GenericValue salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
			if (salesStatement != null) {
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
				String dateTime = format.format(nowTimestamp);
				fileName = salesStatement.getString("salesStatementTypeId") + "_" + dateTime;
				if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
					statementTypeIdStr = UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesIn", locale);
				} else {
					statementTypeIdStr = UtilProperties.getMessage("BaseSalesUiLabels", "BSSalesOut", locale);
				}
			}
			
			List<String> productIds = new ArrayList<String>();
			if (productIdsStr != null) {
				JSONArray jsonArray = new JSONArray();
				if (UtilValidate.isNotEmpty(productIdsStr)) {
					jsonArray = JSONArray.fromObject(productIdsStr);
				}
				if (jsonArray != null && jsonArray.size() > 0) {
					for (int i = 0; i < jsonArray.size(); i++) {
						productIds.add(jsonArray.getString(i));
					}
				}
			}
			List<GenericValue> productList = ProductWorker.getListProduct(delegator, productIds);
			if (productList == null) productList = new ArrayList<GenericValue>();
			
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			Map<String, String[]> parameters = FastMap.newInstance();
			parameters.put("salesStatementId", new String[]{salesStatementId});
			parameters.put("productIds", new String[]{productIdsStr});
			parameters.put("pagesize", new String[]{"0"});
			
			Map<String, Object> context = FastMap.newInstance();
			context.put("listAllConditions", listAllConditions);
			context.put("listSortFields", listSortFields);
			context.put("opts", opts);
			context.put("parameters", parameters);
			context.put("userLogin", userLogin);
			context.put("locale", locale);
			
			Map<String, Object> resultService =  dispatcher.runSync("JQListOrganizationUnitManagerReport", context);
			List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("listIterator");
			
			
			//start renderExcel
			Workbook wb = new HSSFWorkbook();
			//Map<String, CellStyle> styles = ExportExcelUtil.createStyles(wb);
			Map<String, CellStyle> styles = ExportExcelUtil.createStylesNormal(wb);
			Map<String, CellStyle> stylesLevel = ExportExcelUtil.createStylesLevel(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			
			// turn on gridLines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 21*256);
			sheet.setColumnWidth(1, 40*256);
			sheet.setColumnWidth(2, 10*256);
			for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 12*256);
			}
			
			Row imgHead = sheet.createRow(0);
			Cell imgCell = imgHead.createCell(0);
			int rownum = 0;
			FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("basesales", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				Debug.logWarning("Error when set image logo", module);
			} finally {
				if (is != null) is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell khoangCachCell = khoangCachRow.createCell(0);
			khoangCachCell.setCellValue(" ");
			khoangCachCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			// header name
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSReport", locale) + " " + statementTypeIdStr;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum,rownum,0,8));
			Cell titleCell = titleRow.createCell(0);
			titleCell.setCellValue(reportTitle.toUpperCase());
			titleCell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum += 1;
			
			// sub title row
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			String dateTimeOut = null;
			if (salesStatement != null) {
				GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", salesStatement.get("customTimePeriodId")), false);
				if (customTimePeriod != null) {
					Date fromDate = customTimePeriod.getDate("fromDate");
					Date thruDate = customTimePeriod.getDate("thruDate");
					String fromDateStr = formatDate.format(fromDate);
					String thruDateStr = formatDate.format(thruDate);
					dateTimeOut = customTimePeriod.getString("periodName") + " (" + fromDateStr + " - " + thruDateStr + ")";
				}
			}
			Row subTitleRow = sheet.createRow(rownum);
			subTitleRow.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow, 0, UtilProperties.getMessage(RESOURCE, "BSSalesCustomTimePeriod", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow, 2, dateTimeOut, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
			rownum++;
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			rownum += 1;
			
			// title columns
			String orgUnitNameTitle = UtilProperties.getMessage("BaseHRDirectoryUiLabels", "OrgUnitName", locale);
			String orgUnitIdTitle = UtilProperties.getMessage("BaseHRDirectoryUiLabels", "OrgUnitId", locale);
			String totalEmplTitle = "";
			if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
				totalEmplTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSNumDistributor", locale);
			} else {
				totalEmplTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSNumEmployee", locale);
			}
			List<String> titles = new FastList<String>();
			titles.add(orgUnitNameTitle);
			titles.add(orgUnitIdTitle);
			titles.add(totalEmplTitle);
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			Row headerBreakdownAmountRow2 = sheet.createRow(rownum+1);
			headerBreakdownAmountRow2.setHeight((short) 350);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			int index = titles.size();
			
			for (int i = 0; i < titles.size(); i++) {
				Cell headerBreakdownAmountCell = headerBreakdownAmountRow.createCell(i);
				headerBreakdownAmountCell.setCellStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
				headerBreakdownAmountCell.setCellValue(titles.get(i));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum+1, i, i));
			}
			
			String targetUiLabel = "Target";
			String actualUiLabel = "Actual";
			String percentUiLabel = "Percent";
			for (GenericValue item : productList) {
				String internalName = item.getString("productName") + " [" + item.getString("productCode") + "]";
				
				Cell targetCell = headerBreakdownAmountRow.createCell(index);
				targetCell.setCellValue(internalName);
				targetCell.setCellStyle(styles.get("cell_normal_centered_vtop_wrap_text_border_top_10"));
				
				Cell actualCell = headerBreakdownAmountRow.createCell(index + 1);
				//actualCell.setCellValue(internalName);
				actualCell.setCellStyle(styles.get("cell_normal_centered_vtop_wrap_text_border_top_10"));
				
				Cell percentCell = headerBreakdownAmountRow.createCell(index + 2);
				//percentCell.setCellValue(internalName);
				percentCell.setCellStyle(styles.get("cell_normal_centered_vtop_wrap_text_border_top_10"));
				
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, index, index+2));
				
				// create row 2
				Cell targetCell2 = headerBreakdownAmountRow2.createCell(index);
				targetCell2.setCellValue(targetUiLabel);
				targetCell2.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				Cell actualCell2 = headerBreakdownAmountRow2.createCell(index + 1);
				actualCell2.setCellValue(actualUiLabel);
				actualCell2.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				Cell percentCell2 = headerBreakdownAmountRow2.createCell(index + 2);
				percentCell2.setCellValue(percentUiLabel);
				percentCell2.setCellStyle(styles.get("cell_normal_centered_border_full_10"));
				
				index += 3;
			}
			rownum += 2;
			
			sheet.createFreezePane(2, 5);
			
			// row data list
			for (Map<String, Object> map : listData) {
				Row orderDetailRow = sheet.createRow(rownum);
				
				Integer levelValue = (Integer) map.get("levelTree");
				if (levelValue == null) levelValue = -1;
				
				if (levelValue <= 0) {
					orderDetailRow.setHeight((short) 375);
				} else {
					orderDetailRow.setHeight((short) 345);
				}
				
				//String partyId = (String) map.get("partyId");
				String partyCode = (String) map.get("partyCode");
				Cell partyIdCell = orderDetailRow.createCell(0);
				partyIdCell.setCellValue(partyCode);
				partyIdCell.setCellStyle(styles.get("cell_normal_auto_border_full_10"));
				
				String partyName = (String) map.get("partyName");
				Cell partyNameCell = orderDetailRow.createCell(1);
				partyNameCell.setCellValue(partyName);
				partyNameCell.setCellStyle(styles.get("cell_normal_auto_border_full_10"));
				
				Integer totalEmployee = (Integer) map.get("totalEmployee");
				Cell partyIdFromCell = orderDetailRow.createCell(2);
				if (totalEmployee != null) partyIdFromCell.setCellValue(totalEmployee);
				partyIdFromCell.setCellStyle(styles.get("cell_normal_auto_border_full_10"));
				
				if (levelValue == -1) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_levelne1"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_levelne1"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_levelne1"));
				} else if (levelValue == 0) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level0"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level0"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level0"));
				} else if (levelValue == 1) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level1"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level1"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level1"));
				} else if (levelValue == 2) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level2"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level2"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level2"));
				} else if (levelValue == 3) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level3"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level3"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level3"));
				} else if (levelValue == 4) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level4"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level4"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level4"));
				} else if (levelValue == 5) {
					partyIdCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level5"));
					partyNameCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level5"));
					partyIdFromCell.setCellStyle(stylesLevel.get("cell_normal_left_border_full_10_level5"));
				}
				
				int indexI = 3;
				for (GenericValue productItem : productList) {
					String column1 = "prodCode_" + productItem.getString("productId");
					String column2 = "actual_" + productItem.getString("productId");
					String column3 = "percent_" + productItem.getString("productId");
					
					BigDecimal column1Value = (BigDecimal) map.get(column1);
					Cell objCell1 = orderDetailRow.createCell(indexI);
					objCell1.setCellValue(column1Value != null ? column1Value.doubleValue() : 0);
					indexI++;
					
					BigDecimal column2Value = (BigDecimal) map.get(column2);
					Cell objCell2 = orderDetailRow.createCell(indexI);
					objCell2.setCellValue(column2Value != null ? column2Value.doubleValue() : 0);
					indexI++;
					
					BigDecimal column3Value = (BigDecimal) map.get(column3);
					Cell objCell3 = orderDetailRow.createCell(indexI);
					if (column3Value != null) {
						column3Value = column3Value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
						objCell3.setCellValue(column3Value.doubleValue());
					} else {
						objCell3.setCellValue(0);
					}
					indexI++;
					
					if (levelValue == -1) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_levelne1"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_levelne1"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_levelne1"));
					} else if (levelValue == 0) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level0"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level0"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_level0"));
					} else if (levelValue == 1) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level1"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level1"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_level1"));
					} else if (levelValue == 2) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level2"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level2"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_level2"));
					} else if (levelValue == 3) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level3"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level3"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_level3"));
					} else if (levelValue == 4) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level4"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level4"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_level4"));
					} else if (levelValue == 5) {
						objCell1.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level5"));
						objCell2.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_level5"));
						objCell3.setCellStyle(stylesLevel.get("cell_normal_auto_border_full_10_percent_level5"));
					}
				}
				rownum += 1;
			}
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		} finally {
			if(baos != null)baos.close();
		}
	}
	
	
	@SuppressWarnings({ "unchecked" })
	public static void exportGoodQuotaExcel(HttpServletRequest request, HttpServletResponse response) {
		//GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = "BAO_GIA";
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dateTime = format.format(nowTimestamp);
			String dateTimeStr = formatDate.format(nowTimestamp);
			String dateTimeOut = formatOut.format(nowTimestamp);
			fileName += "_" + dateTime;
			
			String salesMethodChannelEnumId = (String) request.getParameter("salesMethodChannelEnumId");
			String productStoreId = (String) request.getParameter("productStoreId");
			String partyId = (String) request.getParameter("partyId");
			
			
			//start renderExcel
			Workbook wb = new HSSFWorkbook();
			//Map<String, CellStyle> styles = ExportExcelUtil.createStyles(wb);
			Map<String, CellStyle> styles = ExportExcelUtil.createStylesNormal(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			
			// turn on gridLines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 8*256); // column 1 contain 21 characters
			sheet.setColumnWidth(1, 18*256); // column 1 contain 21 characters
			sheet.setColumnWidth(2, 18*256); // column 2 contain 21 characters
			//sheet.setColumnWidth(3, 12*256); // feature
			sheet.setColumnWidth(3, 48*256); // product name
			sheet.setColumnWidth(4, 12*256); // tax
			sheet.setColumnWidth(5, 12*256); // uom
			sheet.setColumnWidth(6, 15*256);
			sheet.setColumnWidth(7, 15*256);
			sheet.setColumnWidth(8, 10*256); // BSChange
			sheet.setColumnWidth(9, 10*256); // BSPriceIsSale
			sheet.setColumnWidth(10, 18*256); //BSSalesDiscontinuationDate
			sheet.setColumnWidth(11, 18*256); // BSPurchaseDiscontinuationDate
			/*for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 12*256);
			}*/
			
			int rownum = 0;
			FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("basesales.properties", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				Debug.logWarning("Error when set image logo", module);
			} finally {
				if (is != null) is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			ExportExcelUtil.createCell(khoangCachRow, 0, " ", styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			// header name
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSQuotation", locale) + " " + dateTimeStr;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			ExportExcelUtil.createCell(titleRow, 0, reportTitle.toUpperCase(), styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			Row subTitleRow = sheet.createRow(rownum);
			subTitleRow.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow, 0, UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow, 2, dateTimeOut, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
			rownum++;
			
			if (UtilValidate.isNotEmpty(productStoreId)) {
				String productStoreName = productStoreId;
				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
				if (productStore != null) productStoreName = productStore.getString("storeName");
				
				Row subTitleRow1 = sheet.createRow(rownum);
				subTitleRow1.setHeight((short) 375);
				ExportExcelUtil.createCell(subTitleRow1, 0, UtilProperties.getMessage(RESOURCE, "BSSalesChannel", locale), styles.get("cell_normal_cell_subtitle_right"));
				ExportExcelUtil.createCell(subTitleRow1, 2, productStoreName, styles.get("cell_normal_cell_subtitle"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
				rownum++;
			}
			
			if (UtilValidate.isNotEmpty(partyId)) {
				Row subTitleRow2 = sheet.createRow(rownum);
				subTitleRow2.setHeight((short) 375);
				ExportExcelUtil.createCell(subTitleRow2, 0, UtilProperties.getMessage(RESOURCE, "BSCustomerId", locale), styles.get("cell_normal_cell_subtitle_right"));
				ExportExcelUtil.createCell(subTitleRow2, 2, partyId, styles.get("cell_normal_cell_subtitle"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
				rownum++;
			}
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			rownum++;
			
			// title name
			List<String> titles = new FastList<String>();
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNo2", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSBarcode", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductId", locale));
			/*titles.add(UtilProperties.getMessage(RESOURCE, "BSParentProduct", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSFeature", locale));*/
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSTax", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductPackingUomId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSListPrice", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSSalesPrice", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSChange", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPriceIsSale", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSSalesDiscontinuationDate", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPurchaseDiscontinuationDate", locale));
			// title row
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			//headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			// blank row
			/*Row headerBreakdownAmountRow2 = sheet.createRow(rownum+1);
			headerBreakdownAmountRow2.setHeight((short) 350);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));*/
			
			// title content in row
			CellStyle titleCellStyle = styles.get("cell_normal_centered_wrap_text_border_top_10");
			for (int i = 0; i < titles.size(); i++) {
				ExportExcelUtil.createCell(headerBreakdownAmountRow, i, titles.get(i), titleCellStyle);
				//sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, i, i));
			}
			rownum++;
			
			//sheet.createFreezePane(2, 5);
			
			// row data list
			CellStyle normalCellStyle = styles.get("cell_normal_auto_border_full_10");
			CellStyle normalCellStyleDate = styles.get("cell_normal_auto_border_full_10_date");
			CellStyle normalCellStyleCenter = styles.get("cell_normal_centered_border_full_10");
			CellStyle normalCellStylePercent = styles.get("cell_normal_auto_border_full_10_percent");
			CellStyle normalCellStyleCurrency = styles.get("cell_normal_auto_border_full_10_currency");
			CellStyle normalCellStyleCurrencyStrikeout = styles.get("cell_normal_auto_border_full_10_currency_strikeout");
			short rowHeight = 375;
			int index = 1;
			
			List<GenericValue> uomList = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"), null, false);
			int pagenum = 0;
			int pagesize = 100;
			int totalRows = 0;
			int lastNum = 0;
			boolean isContinue = true;
			
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			
			while (isContinue) {
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("salesMethodChannelEnumId", new String[]{salesMethodChannelEnumId});
				parameters.put("productStoreId", new String[]{productStoreId});
				parameters.put("partyId", new String[]{partyId});
				parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
				parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
				
				Map<String, Object> context = FastMap.newInstance();
				context.put("listAllConditions", listAllConditions);
				context.put("listSortFields", listSortFields);
				context.put("opts", opts);
				context.put("parameters", parameters);
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				
				Map<String, Object> resultService =  dispatcher.runSync("JQFindProductPriceQuotes", context);
				List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("listIterator");
				String totalRowsStr = (String) resultService.get("TotalRows");
				if (UtilValidate.isNotEmpty(totalRowsStr)) {
					totalRows = Integer.parseInt(totalRowsStr);
				}
				if (ServiceUtil.isSuccess(resultService) && UtilValidate.isNotEmpty(listData) && totalRows > 0) {
					pagenum++;
					lastNum = lastNum + listData.size();
					if (lastNum >= totalRows) {
						isContinue = false;
					}
					
					for (Map<String, Object> map : listData) {
						Row row = sheet.createRow(rownum);
						row.setHeight(rowHeight);
						
						ExportExcelUtil.createCell(row, 0, index, normalCellStyle);
						
						String barcode = (String) map.get("barcode");
						ExportExcelUtil.createCell(row, 1, barcode, normalCellStyle);
						
						String productCode = (String) map.get("productCode");
						ExportExcelUtil.createCell(row, 2, productCode, normalCellStyle);
						
						/*String parentProductCode = (String) map.get("parentProductCode");
						ExportExcelUtil.createCell(row, 2, parentProductCode, normalCellStyle);
						
						String features = (String) map.get("features");
						ExportExcelUtil.createCell(row, 3, features, normalCellStyle);*/
						
						String productName = (String) map.get("productName");
						ExportExcelUtil.createCell(row, 3, productName, normalCellStyle);
						
						BigDecimal taxPercentage = (BigDecimal) map.get("taxPercentage");
						Double taxPercentageDouble = null;
						if (taxPercentage != null) {
							taxPercentage = taxPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
							taxPercentageDouble = taxPercentage.doubleValue();
						}
						ExportExcelUtil.createCell(row, 4, taxPercentageDouble, normalCellStylePercent);
						
						String quantityUomId = (String) map.get("uomId");
						GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(uomList, UtilMisc.toMap("uomId", quantityUomId)));
						if (quantityUom != null) quantityUomId = (String) quantityUom.get("description", locale);
						ExportExcelUtil.createCell(row, 5, quantityUomId, normalCellStyleCenter);
						
						BigDecimal unitListPriceVAT = (BigDecimal) map.get("unitListPriceVAT");
						BigDecimal priceVAT = (BigDecimal) map.get("priceVAT");
						
						Double priceDouble = null;
						if (unitListPriceVAT != null) priceDouble = unitListPriceVAT.doubleValue();
						if (unitListPriceVAT != null && priceVAT != null && unitListPriceVAT.compareTo(priceVAT) != 0) {
							ExportExcelUtil.createCell(row, 6, priceDouble, normalCellStyleCurrencyStrikeout);
						} else {
							ExportExcelUtil.createCell(row, 6, priceDouble, normalCellStyleCurrency);
						}
						
						Double priceVATDouble = null;
						if (priceVAT != null) priceVATDouble = priceVAT.doubleValue();
						ExportExcelUtil.createCell(row, 7, priceVATDouble, normalCellStyleCurrency);
						
						if (unitListPriceVAT != null && priceVAT != null && unitListPriceVAT.compareTo(priceVAT) != 0) {
							ExportExcelUtil.createCell(row, 8, "Y", normalCellStyleCenter);
						} else {
							ExportExcelUtil.createCell(row, 8, "", normalCellStyleCenter);
						}
						
						if (unitListPriceVAT != null && priceVAT != null && unitListPriceVAT.compareTo(priceVAT) > 0) {
							ExportExcelUtil.createCell(row, 9, "Y", normalCellStyleCenter);
						} else {
							ExportExcelUtil.createCell(row, 9, "", normalCellStyleCenter);
						}
						
						Timestamp salesDate = null;
						String salesDateStr = null;
						if (UtilValidate.isNotEmpty(map.get("salesDiscontinuationDate"))){
							salesDate = (Timestamp) map.get("salesDiscontinuationDate");
							salesDateStr = formatOut.format(salesDate);
						}
						if (salesDateStr != null){
							ExportExcelUtil.createCell(row, 10, salesDateStr, normalCellStyle);
						} else {
							ExportExcelUtil.createCell(row, 10, "", normalCellStyle);
						}
						
						Timestamp purchaseDate = null;
						String purchaseDateStr = null;
						if (UtilValidate.isNotEmpty(map.get("purchaseDiscontinuationDate"))){
							purchaseDate = (Timestamp) map.get("purchaseDiscontinuationDate");
							purchaseDateStr = formatOut.format(purchaseDate);
						}
						if (purchaseDateStr != null){
							ExportExcelUtil.createCell(row, 11, purchaseDateStr, normalCellStyle);
						} else {
							ExportExcelUtil.createCell(row, 11, "", normalCellStyle);
						}
						index++;
						rownum++;
					}
				} else {
					isContinue = false;
				}
			}
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			Debug.logError(e, module);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		} catch (IOException e) {
			Debug.logError(e, module);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Debug.logError(e, module);
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportProductErrorExcel(HttpServletRequest request, HttpServletResponse response) {
		//GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		//Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String filterType = (String) request.getParameter("filterType");
			/* filterType
			 * MISS_TAX_CATEGORY
			 * MISS_REF_CATEGORY
			 * MISS_UPC
			 * MISS_UPC_PRIMARY
			 * MISS_SALES_PRICE
			 * MISS_PURCHASE_PRICE
			 */
			String fileNameMiddle = "";
			String findInName = "";
			String headerName = "";
			if ("MISS_TAX_CATEGORY".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissTaxCategory", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInSalesCategory", locale);
				fileNameMiddle = "THIEU_DANH_MUC_THUE";
			} else if ("MISS_REF_CATEGORY".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissMMSCategory", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInSalesCategory", locale);
				fileNameMiddle = "THIEU_DANH_MUC_MMS";
			} else if ("MISS_UPC".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissUPC", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInSalesCategory", locale);
				fileNameMiddle = "THIEU_UPC";
			} else if ("MISS_UPC_PRIMARY".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissUPCPrimary", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInSalesCategory", locale);
				fileNameMiddle = "THIEU_UPC_CHINH";
			} else if ("MISS_SALES_PRICE".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissSalesPrice", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInSalesCategory", locale);
				fileNameMiddle = "THIEU_GIA_BAN";
			} else if ("MISS_PURCHASE_PRICE".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissPurchasePrice", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInSalesCategory", locale);
				fileNameMiddle = "THIEU_GIA_MUA";
			} else if ("ALL_MISS_TAX_CATEGORY".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissTaxCategory", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInAll", locale);
				fileNameMiddle = "TC_THIEU_DANH_MUC_THUE";
			} else if ("ALL_MISS_REF_CATEGORY".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissMMSCategory", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInAll", locale);
				fileNameMiddle = "TC_THIEU_DANH_MUC_MMS";
			} else if ("ALL_MISS_UPC".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissUPC", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInAll", locale);
				fileNameMiddle = "TC_THIEU_UPC";
			} else if ("ALL_MISS_UPC_PRIMARY".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissUPCPrimary", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInAll", locale);
				fileNameMiddle = "TC_THIEU_UPC_CHINH";
			} else if ("ALL_MISS_SALES_PRICE".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissSalesPrice", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInAll", locale);
				fileNameMiddle = "TC_THIEU_GIA_BAN";
			} else if ("ALL_MISS_PURCHASE_PRICE".equals(filterType)) {
				headerName = UtilProperties.getMessage(RESOURCE, "BSMissPurchasePrice", locale);
				findInName = UtilProperties.getMessage(RESOURCE, "BSInAll", locale);
				fileNameMiddle = "TC_THIEU_GIA_MUA";
			}
			
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = "SP_" + fileNameMiddle;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dateTime = format.format(nowTimestamp);
			String dateTimeStr = formatDate.format(nowTimestamp);
			String dateTimeOut = formatOut.format(nowTimestamp);
			fileName += "_" + dateTime;
			
			
			//start renderExcel
			Workbook wb = new HSSFWorkbook();
			//Map<String, CellStyle> styles = ExportExcelUtil.createStyles(wb);
			Map<String, CellStyle> styles = ExportExcelUtil.createStylesNormal(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			
			// turn on gridLines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 10*256); // column 1 contain 21 characters
			sheet.setColumnWidth(1, 20*256); // column 2 contain 21 characters
			sheet.setColumnWidth(2, 68*256); // product name
			/*for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 12*256);
			}*/
			
			int rownum = 0;
			FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("basesales.properties", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				Debug.logWarning("Error when set image logo", module);
			} finally {
				if (is != null) is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
			ExportExcelUtil.createCell(khoangCachRow, 0, " ", styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			// header name
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", headerName, locale) + " " + dateTimeStr;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
			ExportExcelUtil.createCell(titleRow, 0, reportTitle.toUpperCase(), styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			Row subTitleRow = sheet.createRow(rownum);
			subTitleRow.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow, 0, UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow, 2, dateTimeOut, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 6));
			rownum++;
			
			Row subTitleRow1 = sheet.createRow(rownum);
			subTitleRow1.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow1, 0, UtilProperties.getMessage(RESOURCE, "BSFindIn", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow1, 2, findInName, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 6));
			rownum++;
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
			rownum++;
			
			// title name
			List<String> titles = new FastList<String>();
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNo2", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductName", locale));
			// title row
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			//headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			// blank row
			/*Row headerBreakdownAmountRow2 = sheet.createRow(rownum+1);
			headerBreakdownAmountRow2.setHeight((short) 350);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));*/
			
			// title content in row
			CellStyle titleCellStyle = styles.get("cell_normal_centered_wrap_text_border_top_10");
			for (int i = 0; i < titles.size(); i++) {
				ExportExcelUtil.createCell(headerBreakdownAmountRow, i, titles.get(i), titleCellStyle);
				//sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, i, i));
			}
			rownum++;
			
			//sheet.createFreezePane(2, 5);
			
			// row data list
			CellStyle normalCellStyle = styles.get("cell_normal_auto_border_full_10");
			//CellStyle normalCellStyleCenter = styles.get("cell_normal_centered_border_full_10");
			//CellStyle normalCellStylePercent = styles.get("cell_normal_auto_border_full_10_percent");
			//CellStyle normalCellStyleCurrency = styles.get("cell_normal_auto_border_full_10_currency");
			//CellStyle normalCellStyleCurrencyStrikeout = styles.get("cell_normal_auto_border_full_10_currency_strikeout");
			short rowHeight = 375;
			int index = 1;
			
			int pagenum = 0;
			int pagesize = 100;
			int totalRows = 0;
			int lastNum = 0;
			boolean isContinue = true;
			
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			
			while (isContinue) {
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("filterType", new String[]{filterType});
				parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
				parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
				
				Map<String, Object> context = FastMap.newInstance();
				context.put("listAllConditions", listAllConditions);
				context.put("listSortFields", listSortFields);
				context.put("opts", opts);
				context.put("parameters", parameters);
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				
				Map<String, Object> resultService =  dispatcher.runSync("JQCheckProductError", context);
				List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("listIterator");
				String totalRowsStr = (String) resultService.get("TotalRows");
				if (UtilValidate.isNotEmpty(totalRowsStr)) {
					totalRows = Integer.parseInt(totalRowsStr);
				}
				if (ServiceUtil.isSuccess(resultService) && UtilValidate.isNotEmpty(listData) && totalRows > 0) {
					pagenum++;
					lastNum = lastNum + listData.size();
					if (lastNum >= totalRows) {
						isContinue = false;
					}
					
					for (Map<String, Object> map : listData) {
						Row row = sheet.createRow(rownum);
						row.setHeight(rowHeight);
						
						ExportExcelUtil.createCell(row, 0, index, normalCellStyle);
						
						String productCode = (String) map.get("productCode");
						ExportExcelUtil.createCell(row, 1, productCode, normalCellStyle);
						
						String productName = (String) map.get("productName");
						ExportExcelUtil.createCell(row, 2, productName, normalCellStyle);
						
						index++;
						rownum++;
					}
				} else {
					isContinue = false;
				}
			}
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			Debug.logError(e, module);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		} catch (IOException e) {
			Debug.logError(e, module);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Debug.logError(e, module);
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static void exportProdSalesPriceChangeExcel(HttpServletRequest request, HttpServletResponse response) {
		//GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = "DS_GIA_THAY_DOI";
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			//SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dateTime = format.format(nowTimestamp);
			//String dateTimeStr = formatDate.format(nowTimestamp);
			String dateTimeOut = formatOut.format(nowTimestamp);
			fileName += "_" + dateTime;
			
			String changeDateTypeId = (String) request.getParameter("changeDateTypeId");
			String productStoreId = (String) request.getParameter("productStoreId");
			String fromDateStr = (String) request.getParameter("fromDate");
			String thruDateStr = (String) request.getParameter("thruDate");
			
			//start renderExcel
			Workbook wb = new HSSFWorkbook();
			//Map<String, CellStyle> styles = ExportExcelUtil.createStyles(wb);
			Map<String, CellStyle> styles = ExportExcelUtil.createStylesNormal(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			
			// turn on gridLines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 8*256); // column 1 contain 21 characters
			sheet.setColumnWidth(1, 18*256); // column 1 contain 21 characters
			sheet.setColumnWidth(2, 18*256); // column 2 contain 21 characters
			//sheet.setColumnWidth(3, 12*256); // feature
			sheet.setColumnWidth(3, 48*256); // product name
			sheet.setColumnWidth(4, 12*256); // tax
			sheet.setColumnWidth(5, 12*256); // uom
			sheet.setColumnWidth(6, 15*256);
			sheet.setColumnWidth(7, 15*256);
			sheet.setColumnWidth(8, 10*256); // BSChange
			sheet.setColumnWidth(9, 10*256); // BSPriceIsSale
			/*for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 12*256);
			}*/
			
			int rownum = 0;
			FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("basesales.properties", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				Debug.logWarning("Error when set image logo", module);
			} finally {
				if (is != null) is.close();
			}
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			ExportExcelUtil.createCell(khoangCachRow, 0, " ", styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			
			Map<String, String[]> parameters1 = FastMap.newInstance();
			parameters1.put("changeDateTypeId", new String[]{changeDateTypeId});
			parameters1.put("productStoreId", new String[]{productStoreId});
			parameters1.put("fromDate", new String[]{fromDateStr});
			parameters1.put("thruDate", new String[]{thruDateStr});
			Map<String, Object> processDateResult = ProductPriceServices.processFromDateThruDate(parameters1, nowTimestamp, locale);
			if (ServiceUtil.isError(processDateResult)) {
				Debug.logError(ServiceUtil.getErrorMessage(processDateResult), module);
			}
			fromDate = (Timestamp) processDateResult.get("fromDate");
			thruDate = (Timestamp) processDateResult.get("thruDate");
			SimpleDateFormat formatOut1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String fromTimeOut1 = fromDate != null ? formatOut1.format(fromDate) : "";
			String thruTimeOut1 = thruDate != null ? formatOut1.format(thruDate) : "";
			String rangeDateStr = fromTimeOut1 + " - " + thruTimeOut1;
			
			// header name
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSAbbListSalesPriceChange", locale);
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			ExportExcelUtil.createCell(titleRow, 0, reportTitle.toUpperCase(), styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			Row subTitleRow = sheet.createRow(rownum);
			subTitleRow.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow, 0, UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow, 2, dateTimeOut, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
			rownum++;
			
			if (UtilValidate.isEmpty(productStoreId)) {
				String organizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
				GenericValue productStore = EntityUtil.getFirst(delegator.findByAnd("ProductStore", UtilMisc.toMap("payToPartyId", organizationId), null, false));
				if (productStore != null) productStoreId = productStore.getString("productStoreId");
			}
			if (UtilValidate.isNotEmpty(productStoreId)) {
				String productStoreName = productStoreId;
				GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
				if (productStore != null) productStoreName = productStore.getString("storeName");
				
				Row subTitleRow1 = sheet.createRow(rownum);
				subTitleRow1.setHeight((short) 375);
				ExportExcelUtil.createCell(subTitleRow1, 0, UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale), styles.get("cell_normal_cell_subtitle_right"));
				ExportExcelUtil.createCell(subTitleRow1, 2, productStoreName, styles.get("cell_normal_cell_subtitle"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
				rownum++;
			}
			
			if (UtilValidate.isNotEmpty(rangeDateStr)) {
				Row subTitleRow2 = sheet.createRow(rownum);
				subTitleRow2.setHeight((short) 375);
				ExportExcelUtil.createCell(subTitleRow2, 0, UtilProperties.getMessage(RESOURCE, "BSRangeDate", locale), styles.get("cell_normal_cell_subtitle_right"));
				ExportExcelUtil.createCell(subTitleRow2, 2, rangeDateStr, styles.get("cell_normal_cell_subtitle"));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
				sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
				rownum++;
			}
			
			Row subTitleRow2 = sheet.createRow(rownum);
			subTitleRow2.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow2, 0, UtilProperties.getMessage(RESOURCE, "BSNoteY", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow2, 2, UtilProperties.getMessage(RESOURCE, "BSCurrentPriceWhichInTwoColumnTwoIsCalculatedAtNowTimestamp", locale), styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 8));
			rownum++;
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			rownum++;
			
			// title name
			List<String> titles = new FastList<String>();
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNo2", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSBarcode", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductId", locale));
			/*titles.add(UtilProperties.getMessage(RESOURCE, "BSParentProduct", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSFeature", locale));*/
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSTax", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductPackingUomId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSListPrice", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSSalesPrice", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSChange", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPriceIsSale", locale));
			// title row
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			//headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			// blank row
			/*Row headerBreakdownAmountRow2 = sheet.createRow(rownum+1);
			headerBreakdownAmountRow2.setHeight((short) 350);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));*/
			
			// title content in row
			CellStyle titleCellStyle = styles.get("cell_normal_centered_wrap_text_border_top_10");
			for (int i = 0; i < titles.size(); i++) {
				ExportExcelUtil.createCell(headerBreakdownAmountRow, i, titles.get(i), titleCellStyle);
				//sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, i, i));
			}
			rownum++;
			
			//sheet.createFreezePane(2, 5);
			
			// row data list
			CellStyle normalCellStyle = styles.get("cell_normal_auto_border_full_10");
			CellStyle normalCellStyleCenter = styles.get("cell_normal_centered_border_full_10");
			CellStyle normalCellStylePercent = styles.get("cell_normal_auto_border_full_10_percent");
			CellStyle normalCellStyleCurrency = styles.get("cell_normal_auto_border_full_10_currency");
			CellStyle normalCellStyleCurrencyStrikeout = styles.get("cell_normal_auto_border_full_10_currency_strikeout");
			short rowHeight = 375;
			int index = 1;
			
			List<GenericValue> uomList = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"), null, false);
			int pagenum = 0;
			int pagesize = 100;
			int totalRows = 0;
			int lastNum = 0;
			boolean isContinue = true;
			
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			
			while (isContinue) {
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("changeDateTypeId", new String[]{changeDateTypeId});
				parameters.put("productStoreId", new String[]{productStoreId});
				parameters.put("fromDate", new String[]{fromDateStr});
				parameters.put("thruDate", new String[]{thruDateStr});
				parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
				parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
				
				Map<String, Object> context = FastMap.newInstance();
				context.put("listAllConditions", listAllConditions);
				context.put("listSortFields", listSortFields);
				context.put("opts", opts);
				context.put("parameters", parameters);
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				
				Map<String, Object> resultService =  dispatcher.runSync("JQGetListProductSalesPriceChange", context);
				List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("listIterator");
				String totalRowsStr = (String) resultService.get("TotalRows");
				if (UtilValidate.isNotEmpty(totalRowsStr)) {
					totalRows = Integer.parseInt(totalRowsStr);
				}
				if (ServiceUtil.isSuccess(resultService) && UtilValidate.isNotEmpty(listData) && totalRows > 0) {
					pagenum++;
					lastNum = lastNum + listData.size();
					if (lastNum >= totalRows) {
						isContinue = false;
					}
					
					for (Map<String, Object> map : listData) {
						Row row = sheet.createRow(rownum);
						row.setHeight(rowHeight);
						
						ExportExcelUtil.createCell(row, 0, index, normalCellStyle);
						
						String barcode = (String) map.get("barcode");
						ExportExcelUtil.createCell(row, 1, barcode, normalCellStyle);
						
						String productCode = (String) map.get("productCode");
						ExportExcelUtil.createCell(row, 2, productCode, normalCellStyle);
						
						/*String parentProductCode = (String) map.get("parentProductCode");
						ExportExcelUtil.createCell(row, 2, parentProductCode, normalCellStyle);
						
						String features = (String) map.get("features");
						ExportExcelUtil.createCell(row, 3, features, normalCellStyle);*/
						
						String productName = (String) map.get("productName");
						ExportExcelUtil.createCell(row, 3, productName, normalCellStyle);
						
						BigDecimal taxPercentage = (BigDecimal) map.get("taxPercentage");
						Double taxPercentageDouble = null;
						if (taxPercentage != null) {
							taxPercentage = taxPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
							taxPercentageDouble = taxPercentage.doubleValue();
						}
						ExportExcelUtil.createCell(row, 4, taxPercentageDouble, normalCellStylePercent);
						
						String quantityUomId = (String) map.get("uomId");
						GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(uomList, UtilMisc.toMap("uomId", quantityUomId)));
						if (quantityUom != null) quantityUomId = (String) quantityUom.get("description", locale);
						ExportExcelUtil.createCell(row, 5, quantityUomId, normalCellStyleCenter);
						
						BigDecimal unitListPriceVAT = (BigDecimal) map.get("unitListPriceVAT");
						BigDecimal priceVAT = (BigDecimal) map.get("priceVAT");
						
						Double priceDouble = null;
						if (unitListPriceVAT != null) priceDouble = unitListPriceVAT.doubleValue();
						if (unitListPriceVAT != null && priceVAT != null && unitListPriceVAT.compareTo(priceVAT) != 0) {
							ExportExcelUtil.createCell(row, 6, priceDouble, normalCellStyleCurrencyStrikeout);
						} else {
							ExportExcelUtil.createCell(row, 6, priceDouble, normalCellStyleCurrency);
						}
						
						Double priceVATDouble = null;
						if (priceVAT != null) priceVATDouble = priceVAT.doubleValue();
						ExportExcelUtil.createCell(row, 7, priceVATDouble, normalCellStyleCurrency);
						
						if (unitListPriceVAT != null && priceVAT != null && unitListPriceVAT.compareTo(priceVAT) != 0) {
							ExportExcelUtil.createCell(row, 8, "Y", normalCellStyleCenter);
						} else {
							ExportExcelUtil.createCell(row, 8, "", normalCellStyleCenter);
						}
						
						if (unitListPriceVAT != null && priceVAT != null && unitListPriceVAT.compareTo(priceVAT) > 0) {
							ExportExcelUtil.createCell(row, 9, "Y", normalCellStyleCenter);
						} else {
							ExportExcelUtil.createCell(row, 9, "", normalCellStyleCenter);
						}
						
						index++;
						rownum++;
					}
				} else {
					isContinue = false;
				}
			}
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			Debug.logError(e, module);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		} catch (IOException e) {
			Debug.logError(e, module);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Debug.logError(e, module);
				}
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public static void exportProductInSalesCategoryExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		List<Object> errMsgList = FastList.newInstance();
		boolean beganTx = false;
		try {
		// begin the transaction
     	beganTx = TransactionUtil.begin(7200);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String productStoreId = (String) request.getParameter("productStoreId");
			String fileNameMiddle = "DANH_MUC_COOPSMILE";
			String headerName = UtilProperties.getMessage(RESOURCE, "BSProductInSalesCategory", locale);
			
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = fileNameMiddle;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dateTime = format.format(nowTimestamp);
			String dateTimeStr = formatDate.format(nowTimestamp);
			String dateTimeOut = formatOut.format(nowTimestamp);
			fileName += "_" + dateTime;
			
			//start renderExcel
			Workbook wb = new HSSFWorkbook();
			//Map<String, CellStyle> styles = ExportExcelUtil.createStyles(wb);
			Map<String, CellStyle> styles = ExportExcelUtil.createStylesNormal(wb);
			Sheet sheet = wb.createSheet("Sheet1");
			CellStyle csWrapText = wb.createCellStyle();
			csWrapText.setWrapText(true);
			
			// turn on gridLines
			sheet.setDisplayGridlines(true);
			sheet.setPrintGridlines(true);
			sheet.setFitToPage(true);
			sheet.setHorizontallyCenter(true);
			sheet.setAutobreaks(true);
			
			PrintSetup printSetup = sheet.getPrintSetup();
			printSetup.setLandscape(true);
			printSetup.setFitHeight((short) 1);
			printSetup.setFitWidth((short) 1);
			
			sheet.setColumnWidth(0, 8*256); 	// 0. STT
			sheet.setColumnWidth(1, 16*256); 	// 1. UPC 130
			sheet.setColumnWidth(2, 8*256); 	// 2. IUPPRM 60
			sheet.setColumnWidth(3, 12*256); 	// 3. Ma sp 100
			sheet.setColumnWidth(4, 8*256); 	// 4. Ma NH: NH_1 70 
			sheet.setColumnWidth(5, 35*256); 	// 5. Ten sp 160
			sheet.setColumnWidth(6, 11*256); 	// 6. Don vi co ban 90
			sheet.setColumnWidth(7, 10*256); 	// 7. Quy doi 60
			sheet.setColumnWidth(8, 15*256); 	// 8. Loai thue 120
			sheet.setColumnWidth(9, 10*256); 	// 9. Ma NCC 80
			sheet.setColumnWidth(10, 13*256); 	// 10. Gia mua sau thue 110
			sheet.setColumnWidth(11, 13*256); 	// 11. Gia ban sau thue 110
			sheet.setColumnWidth(12, 15*256); 	// 12. Chinh sach gia da ap dung 110
			sheet.setColumnWidth(13, 20*256); 	// 13. Ngay ngung mua
			sheet.setColumnWidth(14, 20*256); 	// 14. Ngay ngung ban
			
			/*for (int i = 3; i < 100; i++) {
				sheet.setColumnWidth(i, 12*256);
			}*/
			
			int rownum = 0;
			/*FileInputStream is = null;
			try {
				String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("basesales.properties", "image.management.logoPath"), null);
				File file = new File(imageServerPath);
				is = new FileInputStream(file);
				byte[] bytesImg = IOUtils.toByteArray(is);
				int pictureIdx = wb.addPicture(bytesImg, Workbook.PICTURE_TYPE_PNG);
				CreationHelper helper = wb.getCreationHelper();
				Drawing drawing = sheet.createDrawingPatriarch();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setCol2(1);
				anchor.setRow1(0);
				anchor.setRow2(5);
				Picture pict = drawing.createPicture(anchor, pictureIdx);
				pict.getPictureData();
				rownum = 5;
			} catch (Exception e) {
				Debug.logWarning("Error when set image logo", module);
			} finally {
				if (is != null) is.close();
			}*/
			
			Row khoangCachRow = sheet.createRow(rownum);
			khoangCachRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 12));
			ExportExcelUtil.createCell(khoangCachRow, 0, " ", styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			// header name
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", headerName, locale) + " " + dateTimeStr;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 12));
			ExportExcelUtil.createCell(titleRow, 0, reportTitle.toUpperCase(), styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			Row subTitleRow = sheet.createRow(rownum);
			subTitleRow.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow, 0, UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow, 2, dateTimeOut, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 12));
			rownum++;
			
			Row subTitleRow1 = sheet.createRow(rownum);
			subTitleRow1.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow1, 0, UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow1, 2, productStoreId, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 12));
			rownum++;
			
			Row subTitleRow2 = sheet.createRow(rownum);
			subTitleRow2.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow2, 0, UtilProperties.getMessage(RESOURCE, "BSNote", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow2, 2, UtilProperties.getMessage(RESOURCE, "BSThePriceInCludedTax", locale), styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 12));
			rownum++;
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 12));
			rownum++;
			
			// title name
			List<String> titles = new FastList<String>();
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNo2", locale));
			titles.add("UPC");
			titles.add("IUPPRM");
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNHCategoryId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSProductName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSUnitUom", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSQtyConvert", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSTaxProductCategory", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSAbbSupplierId", locale));
			titles.add(UtilProperties.getMessage("BasePOUiLabels", "BSPurchaseUomId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPurchasePrice", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSQtyConvert", locale));
			titles.add(UtilProperties.getMessage("BasePOUiLabels", "BSSalesUomId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSSalesPrice", locale));
			//titles.add(UtilProperties.getMessage(RESOURCE, "BSQuotationId", locale));
			titles.add(UtilProperties.getMessage("BasePOUiLabels", "BSPurchaseDiscontinuationDate", locale));
			titles.add(UtilProperties.getMessage("BasePOUiLabels", "BSSalesDiscontinuationDate", locale));
			
			// title row
			Row headerBreakdownAmountRow = sheet.createRow(rownum);
			headerBreakdownAmountRow.setHeight((short) 600);
			//headerBreakdownAmountRow.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			
			// blank row
			/*Row headerBreakdownAmountRow2 = sheet.createRow(rownum+1);
			headerBreakdownAmountRow2.setHeight((short) 350);
			headerBreakdownAmountRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));*/
			
			// title content in row
			CellStyle titleCellStyle = styles.get("cell_normal_centered_wrap_text_border_top_10");
			for (int i = 0; i < titles.size(); i++) {
				ExportExcelUtil.createCell(headerBreakdownAmountRow, i, titles.get(i), titleCellStyle);
				//sheet.addMergedRegion(new CellRangeAddress(rownum, rownum + 1, i, i));
			}
			rownum++;
			
			//sheet.createFreezePane(2, 5);
			
			// row data list
			CellStyle normalCellStyle = styles.get("cell_normal_auto_border_full_10");
			CellStyle normalCellStyleNumber = styles.get("cell_normal_auto_border_full_10_number");
			CellStyle normalCellStyleCurrency = styles.get("cell_normal_auto_border_full_10_currency");
			//CellStyle normalCellStyleCenter = styles.get("cell_normal_centered_border_full_10");
			//CellStyle normalCellStylePercent = styles.get("cell_normal_auto_border_full_10_percent");
			//CellStyle normalCellStyleCurrency = styles.get("cell_normal_auto_border_full_10_currency");
			//CellStyle normalCellStyleCurrencyStrikeout = styles.get("cell_normal_auto_border_full_10_currency_strikeout");
			short rowHeight = 375;
			int index = 1;
			
			int pagenum = 0;
			int pagesize = 100;
			int totalRows = 0;
			int lastNum = 0;
			boolean isContinue = true;
			
			List<EntityCondition> listAllConditions = FastList.newInstance();
			List<String> listSortFields = FastList.newInstance();
			EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
			
			while (isContinue) {
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("productStoreId", new String[]{productStoreId});
				parameters.put("pagenum", new String[]{String.valueOf(pagenum)});
				parameters.put("pagesize", new String[]{String.valueOf(pagesize)});
				
				Map<String, Object> context = FastMap.newInstance();
				context.put("listAllConditions", listAllConditions);
				context.put("listSortFields", listSortFields);
				context.put("opts", opts);
				context.put("parameters", parameters);
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				
				Map<String, Object> resultService =  dispatcher.runSync("JQExportProductInSalesCategory", context);
				List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("listIterator");
				String totalRowsStr = (String) resultService.get("TotalRows");
				if (UtilValidate.isNotEmpty(totalRowsStr)) {
					totalRows = Integer.parseInt(totalRowsStr);
				}
				if (ServiceUtil.isSuccess(resultService) && UtilValidate.isNotEmpty(listData) && totalRows > 0) {
					pagenum++;
					lastNum = lastNum + listData.size();
					if (lastNum >= totalRows) {
						isContinue = false;
					}
					
					for (Map<String, Object> map : listData) {
						Row row = sheet.createRow(rownum);
						row.setHeight(rowHeight);
						
						// 0. STT
						ExportExcelUtil.createCell(row, 0, index, normalCellStyle);
						
						// 1. idSKU
						String idSKU = (String) map.get("idSKU");
						ExportExcelUtil.createCell(row, 1, idSKU, normalCellStyle);
						
						// 2. iupprm
						Long iupprm = (Long) map.get("iupprm");
						ExportExcelUtil.createCell(row, 2, iupprm, normalCellStyle);
						
						// 3. productCode
						String productCode = (String) map.get("productCode");
						ExportExcelUtil.createCell(row, 3, productCode, normalCellStyle);
						
						// 4. primaryProductCategoryId
						String primaryProductCategoryId = (String) map.get("primaryProductCategoryId");
						ExportExcelUtil.createCell(row, 4, primaryProductCategoryId, normalCellStyle);
						
						// 5. productName
						String productName = (String) map.get("productName");
						ExportExcelUtil.createCell(row, 5, productName, normalCellStyle);
						
						// 6. taxCategoryId
						String taxCategoryId = (String) map.get("taxCategoryId");
						ExportExcelUtil.createCell(row, 6, taxCategoryId, normalCellStyle);
						
						// 7. supplierCode
						String supplierCode = (String) map.get("supplierCode");
						ExportExcelUtil.createCell(row, 7, supplierCode, normalCellStyle);
						
						// 8. purchaseUomId
						String purchaseUomId = (String) map.get("purchaseUomId");
						ExportExcelUtil.createCell(row, 8, purchaseUomId, normalCellStyle);
						
						// 9. purchasePriceVAT
						BigDecimal purchasePriceVAT = (BigDecimal) map.get("purchasePriceVAT");
						Double purchasePriceVATDb = null;
						if (purchasePriceVAT != null) purchasePriceVATDb = purchasePriceVAT.doubleValue();
						ExportExcelUtil.createCell(row, 9, purchasePriceVATDb, normalCellStyleCurrency);
						
						// 10. quantityConvert
						BigDecimal quantityConvert = (BigDecimal) map.get("quantityConvert");
						Double quantityConvertDb = null;
						if (quantityConvert != null) quantityConvertDb = quantityConvert.doubleValue();
						ExportExcelUtil.createCell(row, 10, quantityConvertDb, normalCellStyleNumber);
						
						// 11. salesUomId
						String salesUomId = (String) map.get("salesUomId");
						ExportExcelUtil.createCell(row, 11, salesUomId, normalCellStyle);
						
						// 12. salesPriceVAT
						BigDecimal salesPriceVAT = (BigDecimal) map.get("salesPriceVAT");
						Double salesPriceVATDb = null;
						if (salesPriceVAT != null) salesPriceVATDb = salesPriceVAT.doubleValue();
						ExportExcelUtil.createCell(row, 12, salesPriceVATDb, normalCellStyleCurrency);
						
						// 13. purchaseDiscontinuationDate
						Timestamp purchaseDiscontinuationDate = (Timestamp) map.get("purchaseDiscontinuationDate");
						String purchaseDiscontinuationDateStr = null;
						if (purchaseDiscontinuationDate != null) purchaseDiscontinuationDateStr = formatOut.format(purchaseDiscontinuationDate);
						ExportExcelUtil.createCell(row, 13, purchaseDiscontinuationDateStr, normalCellStyle);
						
						// 14. salesDiscontinuationDate
						Timestamp salesDiscontinuationDate = (Timestamp) map.get("salesDiscontinuationDate");
						String salesDiscontinuationDateStr = null;
						if (salesDiscontinuationDate != null) salesDiscontinuationDateStr = formatOut.format(salesDiscontinuationDate);
						ExportExcelUtil.createCell(row, 14, salesDiscontinuationDateStr, normalCellStyle);
						
						index++;
						rownum++;
					}
				} else {
					isContinue = false;
				}
			}
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
			Debug.logError(e, module);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
		} catch (IOException e) {
			Debug.logError(e, module);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException e) {
					Debug.logError(e, module);
				}
			}
		}
		
		} catch (Exception e) {
			Debug.logError(e, module);
			try {
			    TransactionUtil.rollback(beganTx, e.getMessage(), e);
			} catch (Exception e1) {
			    Debug.logError(e1, module);
			}
			errMsgList.add(UtilProperties.getMessage(RESOURCE_ERROR, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
		} catch (Throwable t) {
			Debug.logError(t, module);
			request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
			try {
			    TransactionUtil.rollback(beganTx, t.getMessage(), t);
			} catch (Exception e2) {
			    Debug.logError(e2, module);
			}
			errMsgList.add(UtilProperties.getMessage(RESOURCE_ERROR, "BSErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
		} finally {
			if (UtilValidate.isNotEmpty(errMsgList)) {
				try {
			        TransactionUtil.rollback(beganTx, "Have error when process", null);
			    } catch (Exception e2) {
			        Debug.logError(e2, module);
			    }
				//request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				//return "error";
			} else {
				// commit the transaction
			    try {
			        TransactionUtil.commit(beganTx);
			    } catch (Exception e) {
			        Debug.logError(e, module);
			    }
			}
		}
	}
}
