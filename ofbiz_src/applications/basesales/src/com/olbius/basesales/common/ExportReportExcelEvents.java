package com.olbius.basesales.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.ExportExcelUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ExportReportExcelEvents {
	public final static String RESOURCE = "BaseSalesUiLabels";
	public final static String RESOURCE_ERROR = "BaseSalesErrorUiLabels";
	public static String module = ExportExcelEvents.class.getName();
	
	@SuppressWarnings({ "unchecked" })
	public static void exportReportStatisticSalesDataExcel(HttpServletRequest request, HttpServletResponse response) {
		//GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String fileNameMiddle = "THONG_KE_DU_LIEU_BACH_HOA";
			String headerName = UtilProperties.getMessage(RESOURCE, "BSReportStatisticSalesData", locale);
			
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = fileNameMiddle;
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			String dateTime = format.format(nowTimestamp);
			String dateTimeStr = formatDate.format(nowTimestamp);
			String dateTimeOut = formatOut.format(nowTimestamp);
			fileName += "_" + dateTime;
			
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			String fromDateTitle = null;
			String thruDateTitle = null;
			String orderStatusId = (String) request.getParameter("orderStatusId");
			String viewPartner = (String) request.getParameter("viewPartner");
			String fromDateStr = (String) request.getParameter("fromDate");
			String thruDateStr = (String) request.getParameter("thruDate");
			//String dateTypeStr = (String) request.getParameter("dateType");
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
			sheet.setColumnWidth(1, 16*256); 	// 1. BSOrderId orderId 120
			sheet.setColumnWidth(2, 16*256); 	// 2. BSCreatorId creatorId 100
			sheet.setColumnWidth(3, 22*256); 	// 3. BSCreatorName creatorName 160
			sheet.setColumnWidth(4, 10*256); 	// 4. BSPSSalesChannelId productStoreId 100 
			sheet.setColumnWidth(5, 42*256); 	// 5. BSPSSalesChannel store_name 160
			sheet.setColumnWidth(6, 16*256); 	// 6. BSDayMonthYearSlash order_date 100
			sheet.setColumnWidth(7, 16*256); 	// 7. BSHourMinuteSecondSlash 90
			sheet.setColumnWidth(8, 16*256); 	// 8. BSAmount total_amount 110
			sheet.setColumnWidth(9, 12*256); 	// 9. BSNumberItem num_item 100
			
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
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 6));
			ExportExcelUtil.createCell(khoangCachRow, 0, " ", styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			// header name
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", headerName, locale) + " " + dateTimeStr;
			Row titleRow = sheet.createRow(rownum);
			titleRow.setHeight((short) 400);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
			ExportExcelUtil.createCell(titleRow, 0, reportTitle.toUpperCase(), styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			
			Row subTitleRow = sheet.createRow(rownum);
			subTitleRow.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow, 0, UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow, 2, dateTimeOut, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 9));
			rownum++;
			
			Row subTitleRow1 = sheet.createRow(rownum);
			subTitleRow1.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow1, 0, UtilProperties.getMessage(RESOURCE, "BSFromDate", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow1, 2, fromDateTitle, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 9));
			rownum++;
			
			Row subTitleRow2 = sheet.createRow(rownum);
			subTitleRow2.setHeight((short) 375);
			ExportExcelUtil.createCell(subTitleRow2, 0, UtilProperties.getMessage(RESOURCE, "BSThruDate", locale), styles.get("cell_normal_cell_subtitle_right"));
			ExportExcelUtil.createCell(subTitleRow2, 2, thruDateTitle, styles.get("cell_normal_cell_subtitle"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 1));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 9));
			rownum++;
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 9));
			rownum++;
			
			// title name
			List<String> titles = new FastList<String>();
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNo2", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSOrderId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSCreatorId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSCreatorName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPSSalesChannelId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPSSalesChannel", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSDayMonthYearSlash", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSHourMinuteSecondSlash", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSAmount", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNumberItem", locale));
			
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
			
			int totalRows = 0;
			int lastNum = 0;
			boolean isContinue = true;
			long limit = 200;
			long offset = 0;
			boolean init = true;
			
			while (isContinue) {
				Map<String, Object> context = FastMap.newInstance();
				context.put("olapType", "GRID");
				context.put("orderStatusId", orderStatusId);
				context.put("viewPartner", viewPartner);
				context.put("fromDate", fromDate);
				context.put("thruDate", thruDate);
				//context.put("dateType", dateTypeStr);
				context.put("limit", limit);
				context.put("offset", offset);
				context.put("init", init);
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				
				Map<String, Object> resultService =  dispatcher.runSync("olapOtherStatisticSalesData", context, 500, true);
				if (ServiceUtil.isError(resultService)) {
					isContinue = false;
					continue;
				}
				List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("data");
				if (init) {
					Integer totalRowsStr = (Integer) resultService.get("totalsize");
					if (UtilValidate.isNotEmpty(totalRowsStr)) {
						totalRows = totalRowsStr.intValue();
						init = false;
					}
				}
				
				if (ServiceUtil.isSuccess(resultService) && UtilValidate.isNotEmpty(listData) && totalRows > 0) {
					offset += limit;
					lastNum = lastNum + listData.size();
					if (lastNum >= totalRows) {
						isContinue = false;
					}
					
					/*
					sheet.setColumnWidth(0, 8*256); 	// 0. STT
					sheet.setColumnWidth(1, 12*256); 	// 1. BSOrderId orderId 120
					sheet.setColumnWidth(2, 10*256); 	// 2. BSCreatorId creatorId 100
					sheet.setColumnWidth(3, 16*256); 	// 3. BSCreatorName creatorName 160
					sheet.setColumnWidth(4, 10*256); 	// 4. BSPSSalesChannelId productStoreId 100 
					sheet.setColumnWidth(5, 16*256); 	// 5. BSPSSalesChannel store_name 160
					sheet.setColumnWidth(6, 10*256); 	// 6. BSDayMonthYearSlash order_date 100
					sheet.setColumnWidth(7, 9*256); 	// 7. BSHourMinuteSecondSlash 90
					sheet.setColumnWidth(8, 11*256); 	// 8. BSAmount total_amount 110
					sheet.setColumnWidth(9, 10*256); 	// 9. BSNumberItem num_item 100
					 */
					
					for (Map<String, Object> map : listData) {
						Row row = sheet.createRow(rownum);
						row.setHeight(rowHeight);
						
						// 0. STT
						ExportExcelUtil.createCell(row, 0, index, normalCellStyle);
						
						// 1. orderId
						String orderId = (String) map.get("order_id");
						ExportExcelUtil.createCell(row, 1, orderId, normalCellStyle);
						
						// 2. creatorId
						String creatorId = (String) map.get("creator_id");
						ExportExcelUtil.createCell(row, 2, creatorId, normalCellStyle);
						
						// 3. creatorName
						String creatorName = (String) map.get("creator_name");
						ExportExcelUtil.createCell(row, 3, creatorName, normalCellStyle);
						
						// 4. productStoreId
						String productStoreId = (String) map.get("product_store_id");
						ExportExcelUtil.createCell(row, 4, productStoreId, normalCellStyle);
						
						// 5. storeName
						String storeName = (String) map.get("store_name");
						ExportExcelUtil.createCell(row, 5, storeName, normalCellStyle);
						
						// 6. orderDate
						Timestamp orderDate = (Timestamp) map.get("order_date");
						String orderDateStr = null;
						if (orderDate != null) orderDateStr = formatDate.format(orderDate);
						ExportExcelUtil.createCell(row, 6, orderDateStr, normalCellStyle);
						
						// 7. orderTime
						String orderTimeStr = null;
						if (orderDate != null) orderTimeStr = formatTime.format(orderDate);
						ExportExcelUtil.createCell(row, 7, orderTimeStr, normalCellStyle);
						
						// 8. totalAmount
						BigDecimal totalAmount = (BigDecimal) map.get("total_amount");
						Double totalAmountDb = null;
						if (totalAmount != null) totalAmountDb = totalAmount.doubleValue();
						ExportExcelUtil.createCell(row, 8, totalAmountDb, normalCellStyleCurrency);
						
						// 9. numItem
						Long numItem = (Long) map.get("num_item");
						ExportExcelUtil.createCell(row, 9, numItem, normalCellStyleNumber);
						
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
}
