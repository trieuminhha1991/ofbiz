package com.olbius.salesmtl.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.util.ExportExcelUtil;
import com.olbius.salesmtl.SupervisorServices;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ExportExcelEvents {
	public final static String RESOURCE = "BaseSalesUiLabels";
	public final static String RESOURCE_ERROR = "BaseSalesErrorUiLabels";
	public static String module = ExportExcelEvents.class.getName();
	
	public static void exportRetailOutletListExcel(HttpServletRequest request, HttpServletResponse response) {
		//GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		//LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
			String fileName = "DAI_LY_BAN_LE";
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
			
			sheet.setColumnWidth(0, 8*256); // column 1 contain 21 characters
			sheet.setColumnWidth(1, 18*256); // column 1 contain 21 characters
			sheet.setColumnWidth(2, 18*256); // column 2 contain 21 characters
			sheet.setColumnWidth(3, 12*256);
			sheet.setColumnWidth(4, 48*256);
			sheet.setColumnWidth(5, 22*256);
			sheet.setColumnWidth(6, 22*256);
			sheet.setColumnWidth(7, 22*256);
			sheet.setColumnWidth(8, 15*256);
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
			String reportTitle = UtilProperties.getMessage("BaseSalesUiLabels", "BSRetailOutletList", locale) + " " + dateTimeStr;
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
			
			// blank row
			Row blankRow2 = sheet.createRow(rownum+1);
			blankRow2.setHeight((short) 350);
			//blankRow2.setRowStyle(styles.get("cell_normal_centered_wrap_text_border_top_10"));
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 0, 8));
			rownum++;
			
			// title columns name
			List<String> titles = new FastList<String>();
			titles.add(UtilProperties.getMessage(RESOURCE, "BSNo2", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSOutletId", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSOutletName", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSPhone", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSAddress", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSEmail", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSDistributor", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSSalesExecutive", locale));
			titles.add(UtilProperties.getMessage(RESOURCE, "BSStatus", locale));
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
			short rowHeight = 375;
			int index = 1;
			
			List<GenericValue> partyStatuses = delegator.findByAnd("StatusItem", UtilMisc.toMap("statusTypeId", "PARTY_STATUS"), null, false);
			
			EntityListIterator listIterator = null;
			try {
				String partyIdFrom = (String) request.getParameter("partyIdFrom");
				
				List<EntityCondition> listAllConditions = FastList.newInstance();
				List<String> listSortFields = FastList.newInstance();
				EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
				Map<String, String[]> parameters = FastMap.newInstance();
				parameters.put("partyIdFrom", new String[]{partyIdFrom});
				
				Map<String, Object> context = FastMap.newInstance();
				context.put("listAllConditions", listAllConditions);
				context.put("listSortFields", listSortFields);
				context.put("opts", opts);
				context.put("parameters", parameters);
				context.put("userLogin", userLogin);
				context.put("locale", locale);
				
				listIterator =  SupervisorServices.getListAgentsInner(delegator, userLogin, parameters, listAllConditions, listSortFields, opts);
				if (listIterator != null) {
					//listData = listIterator.getCompleteList();
					int iIndex = 0;
					int iSize = 100;
					int totalSize = listIterator.getResultsTotalSize();
					List<GenericValue> listData = null;
					while (iIndex * iSize < totalSize) {
						if (iIndex == 0) {
							listData = listIterator.getPartialList(0, iSize);
						} else {
							listData = listIterator.getPartialList(iIndex * iSize + 1, iSize);
						}
						if (listData != null) {
							for (Map<String, Object> map : listData) {
								Row row = sheet.createRow(rownum);
								row.setHeight(rowHeight);
								
								ExportExcelUtil.createCell(row, 0, index, normalCellStyle);
								
								String partyCode = (String) map.get("partyCode");
								ExportExcelUtil.createCell(row, 1, partyCode, normalCellStyle);
								
								String groupName = (String) map.get("groupName");
								ExportExcelUtil.createCell(row, 2, groupName, normalCellStyle);
								
								String contactNumber = (String) map.get("telecomNumber");
								ExportExcelUtil.createCell(row, 3, contactNumber, normalCellStyle);
								
								String address1 = (String) map.get("postalAddressName");
								ExportExcelUtil.createCell(row, 4, address1, normalCellStyle);
								
								String emailAddress = (String) map.get("emailAddress");
								ExportExcelUtil.createCell(row, 5, emailAddress, normalCellStyle);
								
								String distributor = (String) map.get("distributorName");
								ExportExcelUtil.createCell(row, 6, distributor, normalCellStyle);
								
								String salesman = (String) map.get("salesmanName");
								ExportExcelUtil.createCell(row, 7, salesman, normalCellStyle);
								
								String statusId = (String) map.get("statusId");
								GenericValue partyStatus = EntityUtil.getFirst(EntityUtil.filterByAnd(partyStatuses, UtilMisc.toMap("statusId", statusId)));
								if (partyStatus != null) statusId = (String) partyStatus.get("description", locale);
								ExportExcelUtil.createCell(row, 8, statusId, normalCellStyle);
								
								index++;
								rownum++;
							}
						}
						iIndex++;
					}
				}
			} catch (Exception e) {
				Debug.logWarning(e, module);
			} finally {
				if (listIterator != null) listIterator.close();
			}
			
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + fileName + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (SocketException e) {
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
}
