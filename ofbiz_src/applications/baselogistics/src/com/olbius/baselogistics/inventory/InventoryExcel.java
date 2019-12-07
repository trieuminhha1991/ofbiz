package com.olbius.baselogistics.inventory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.baselogistics.inventory.InventoryServices;
import com.olbius.baselogistics.util.LogisticsFacilityUtil;
import com.olbius.baselogistics.util.ExcelUtil;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class InventoryExcel {
	public final static String RESOURCE = "BaseLogisticsUiLabels";
	public static SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

	@SuppressWarnings("unchecked")
	public static void Download(HttpServletRequest request, HttpServletResponse response)
			throws GenericEntityException {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		EntityListIterator iterator = null;
		boolean beganTx = TransactionUtil.begin(7200);
		try {
			String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
					userLogin.getString("userLoginId"));
			// start renderExcel
			Workbook wb = new HSSFWorkbook();
			Map<String, CellStyle> styles = ExcelUtil.createStylesNormal(wb);

			Sheet sheet = sheetSetting(wb, "TONKHO");
			int rownum = ExcelUtil.insertLogo(wb, sheet);

			Row row = sheet.createRow(rownum);
			Cell cell = row.createCell(0);
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "Facility", locale));
			cell.setCellStyle(styles.get("cell_normal_cell_subtitle"));
			Cell cellTKH = row.createCell(1);
			cellTKH.setCellStyle(styles.get("cell_normal_cell_subtitle"));
			rownum++;
			rownum++;

			row = sheet.createRow(rownum);
			row.setHeight((short) 400);
			cell = row.createCell(2);
			sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 2, 4));
			cell.setCellValue(UtilProperties.getMessage(RESOURCE, "ListInventory", locale).toUpperCase());
			cell.setCellStyle(styles.get("cell_bold_centered_no_border_16"));
			rownum++;
			rownum++;
			rownum++;

			List<String> titles = UtilMisc.toList(UtilProperties.getMessage("BaseSalesUiLabels", "BSSTT", locale),
					UtilProperties.getMessage(RESOURCE, "Facility", locale),
					UtilProperties.getMessage(RESOURCE, "ProductId", locale),
					UtilProperties.getMessage(RESOURCE, "ProductName", locale),
					UtilProperties.getMessage(RESOURCE, "BLCategoryProduct", locale),
					UtilProperties.getMessage("BaseSalesUiLabels", "BSUPC", locale));

			titles.addAll(UtilMisc.toList(UtilProperties.getMessage(RESOURCE, "BLDonViTonKho", locale),
					UtilProperties.getMessage(RESOURCE, "BLPackingForm", locale),
					UtilProperties.getMessage(RESOURCE, "QOH", locale),
					UtilProperties.getMessage("BasePOUiLabels", "BSAverageCost", locale),
					UtilProperties.getMessage("BaseSalesUiLabels", "BSValue", locale)));

			row = sheet.createRow(rownum);
			row.setHeight((short) 900);
			for (String t : titles) {
				cell = row.createCell(titles.indexOf(t));
				cell.setCellValue(t);
				cell.setCellStyle(styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
			}
			rownum++;

			LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
			Map<String, String[]> params = request.getParameterMap();
			Map<String, String[]> paramsExtend = new HashMap<String, String[]>(params);
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("parameters", paramsExtend);
			context.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("getJQGridConditions", context);
			List<String> listSortFields = null;
			List<EntityCondition> listAllConditions = null;
			if (ServiceUtil.isSuccess(resultService)) {
				listSortFields = (List<String>) resultService.get("listSortFields");
				listAllConditions = (List<EntityCondition>) resultService.get("listAllConditions");
			}

			List<EntityCondition> conditions = FastList.newInstance();
			Boolean poEmpl = SecurityUtil.hasRole("PO_EMPLOYEE", userLogin.getString("partyId"), delegator);
			if (!poEmpl) {
				List<GenericValue> listFacilitys = LogisticsFacilityUtil.getFacilityWithRole(delegator,
						userLogin.getString("partyId"),
						UtilProperties.getPropertyValue(InventoryServices.LOGISTICS_PROPERTIES, "roleType.manager"));
				conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN,
						EntityUtil.getFieldListFromEntityList(listFacilitys, "facilityId", true)));
			}
			conditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, ownerPartyId));
			if (UtilValidate.isNotEmpty(listAllConditions)) {
				conditions.addAll(listAllConditions);
			}

			String listFacilities = paramsExtend.get("listFacilities") != null
					? ((String[]) paramsExtend.get("listFacilities"))[0] : null;
			if (UtilValidate.isNotEmpty(listFacilities)) {
				JSONArray x = JSONArray.fromObject(listFacilities);
				List<String> fs = FastList.newInstance();
				for (Object o : x) {
					JSONObject f = JSONObject.fromObject(o);
					if (UtilValidate.isNotEmpty(f)) {
						fs.add(f.getString("facilityId"));
					}
				}
				conditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN, fs));
			}
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields = UtilMisc.toList("primaryProductCategoryId", "productCode");
			}

			Map<String, Object> result = FastMap.newInstance();
			String facilityName = "";
			boolean _continue = true;
			EntityFindOptions opts = null;
			int size = 10000;
			int index = 0;

			int count = 0;
			int count_row = 0;
			int count_sheet = 0;
			while (_continue) {
				opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
						EntityFindOptions.CONCUR_READ_ONLY, false);
				opts.setLimit(size);
				opts.setOffset(index);

				try {
					iterator = delegator.find("InventoryItemTotalDetail", EntityCondition.makeCondition(conditions),
							null, null, listSortFields, opts);
					int _size = iterator.getResultsTotalSize();

					if (_size > 0) {
						if (_size == size) {
							index += size - 1;
						} else {
							_continue = false;
						}
						GenericValue x = null;
						while ((x = iterator.next()) != null) {
							if (count_row == 30000) {
								rownum = 0;
								count_row = 0;
								count_sheet++;
								sheet = sheetSetting(wb, "TONKHO (" + count_sheet + ")");

								row = sheet.createRow(rownum);
								row.setHeight((short) 900);
								for (String t : titles) {
									cell = row.createCell(titles.indexOf(t));
									cell.setCellValue(t);
									if (titles.indexOf(t) == 4) {
										cell.setCellStyle(
												styles.get("cell_yellow_bold_centered_wrap_text_bordered_10"));
									} else {
										cell.setCellStyle(styles.get("cell_bold_centered_wrap_text_bordered_10"));
									}
								}
								rownum += 1;
							}

							count_row++;
							count++;

							row = sheet.createRow(rownum);

							int _count = 0;
							// STT
							cell = row.createCell(_count);
							cell.setCellValue(count);
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							cell = row.createCell(_count);
							cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
							cell.setCellValue(x.getString("facilityName"));
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							cell = row.createCell(_count);
							cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
							cell.setCellValue(x.getString("productCode"));
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							cell = row.createCell(_count);
							cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
							cell.setCellValue(x.getString("productName"));
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							cell = row.createCell(_count);
							cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
							cell.setCellValue(x.getString("primaryProductCategoryId"));
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							cell = row.createCell(_count);
							cell.setCellType(ExcelUtil.CELL_TYPE_STRING);
							cell.setCellValue(x.getString("idSKU"));
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							cell = row.createCell(_count);
							String uomId = "Y".equals(x.get("requireAmount")) ? x.getString("weightUomId") : x.getString("quantityUomId");
							String description = getDescriptionUom(delegator, uomId);
							cell.setCellValue(description);
							cell.setCellStyle(styles.get("cell_left_wrap_text_bordered_10"));
							_count++;

							BigDecimal quantityConvert = getQuantityConvert(delegator, x);
							cell = row.createCell(_count);
							if (UtilValidate.isNotEmpty(quantityConvert)) {
								cell.setCellValue(quantityConvert.intValue());
							} else {
								cell.setCellValue(1);
							}
							cell.setCellStyle(styles.get("cell_normal_auto_border_full_10_number"));
							_count++;

							BigDecimal quantityOnHandTotal = x.getBigDecimal("quantityOnHandTotal");
							if ("Y".equals(x.get("requireAmount")) && "WEIGHT_MEASURE".equals(x.get("amountUomTypeId"))) {
								quantityOnHandTotal = x.getBigDecimal("amountOnHandTotal");
							}
							cell = row.createCell(_count);
							if (UtilValidate.isNotEmpty(quantityOnHandTotal)) {
								cell.setCellValue(quantityOnHandTotal.intValue());
							} else {
								cell.setCellValue(0);
							}
							cell.setCellStyle(styles.get("cell_normal_auto_border_full_10_number"));
							_count++;

							BigDecimal unitCost = null;
							result = dispatcher.runSync("getProductAverageCostBaseSimple",
									UtilMisc.toMap("ownerPartyId", ownerPartyId, "facilityId", x.get("facilityId"),
											"productId", x.get("productId"), "userLogin", userLogin));
							if (ServiceUtil.isSuccess(result)) {
								unitCost = (BigDecimal) result.get("unitCost");
								cell = row.createCell(_count);
								if (UtilValidate.isNotEmpty(unitCost)) {
									cell.setCellValue(unitCost.doubleValue());
								} else {
									cell.setCellValue(0);
								}
								cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
								_count++;
							}

							if (UtilValidate.isNotEmpty(quantityOnHandTotal) && UtilValidate.isNotEmpty(unitCost)) {
								cell = row.createCell(_count);
								cell.setCellValue(quantityOnHandTotal.multiply(unitCost).doubleValue());
								cell.setCellStyle(styles.get("cell_right_wrap_text_bordered_10"));
								_count++;
							}

							if (!facilityName.contains(x.getString("facilityName"))) {
								facilityName += x.getString("facilityName") + ", ";
							}

							rownum++;
						}
					} else {
						_continue = false;
					}
				} catch (Exception e) {
					throw e;
				} finally {
					if (iterator != null) {
						iterator.close();
					}
				}
			}
			if (UtilValidate.isNotEmpty(facilityName)) {
				facilityName = facilityName.substring(0, facilityName.length() - 2);
				cellTKH.setCellValue(facilityName);
			}
			ExcelUtil.responseWrite(response, wb, "danh-sach-hang-ton-kho-");
		} catch (Exception e) {
			e.printStackTrace();
		}
		TransactionUtil.commit(beganTx);
	}

	private static BigDecimal getQuantityConvert(Delegator delegator, GenericValue x) {
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
		System.out.println("dmm: "+quantityConvert);
		return quantityConvert;
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

	private static Sheet sheetSetting(Workbook wb, String sheetName) {
		Sheet sheet = wb.createSheet(sheetName);
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

		sheet.setColumnWidth(0, 15 * 256);
		sheet.setColumnWidth(1, 30 * 256);
		sheet.setColumnWidth(2, 20 * 256);
		sheet.setColumnWidth(3, 40 * 256);
		for (int i = 4; i < 20; i++) {
			sheet.setColumnWidth(i, 20 * 256);
		}
		return sheet;
	}
}
