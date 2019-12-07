package com.olbius.baselogistics.inventory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.product.util.InventoryUtil;
import com.olbius.product.util.ProductUtil;
import com.olbius.security.util.SecurityUtil;
import com.olbius.util.FacilityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class StockingServices {
	
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resource_error = "BaseSalesErrorUiLabels";
	
	public static final String module = StockingServices.class.getName();
    public static final String resourceError = "BaseLogisticsErrorUiLabels";
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStockEvents(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		
		if (parameters.containsKey("facilityId") && UtilValidate.isNotEmpty(parameters.get("facilityId")) && parameters.get("facilityId").length > 0) {
			String facilityId = parameters.get("facilityId")[0];
			try {
				if (FacilityUtil.checkRoleWithFacility(delegator, facilityId, userLogin.getString("partyId"), "MANAGER")){
					listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.EQUALS, facilityId));
				}
			} catch (GenericEntityException e) {
				Debug.log(e.getStackTrace().toString(), module);
				return ServiceUtil.returnError("Service listStockEvents:" + e.toString());
			}
		}
		EntityListIterator listIterator = null;
		List<String> listFacilityIds = FastList.newInstance();
		
		Security security = ctx.getSecurity();
		String company = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "PARTY_DISTRIBUTOR")) {
			// list stock event of distributor manages
			List<String> listDistributorIds = FastList.newInstance();
			if (SalesPartyUtil.isDistributor(delegator, userLogin.getString("partyId"))){
				listDistributorIds.add(userLogin.getString("partyId"));
			} else {
				listDistributorIds = PartyUtil.getDistributorManages(delegator, userLogin.getString("partyId"), company);
			}
			if (!listDistributorIds.isEmpty()){
				try {
					// facility distributor
					List<GenericValue> listFacility = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", EntityOperator.IN, listDistributorIds), null, null, null, false);
					if (!listFacility.isEmpty()) listFacilityIds = EntityUtil.getFieldListFromEntityList(listFacility, "facilityId", true);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
					Debug.logError(e, errMsg);
					return ServiceUtil.returnError("Service listStockEvents:" + e.toString());
				}
				// facility consign
				List<EntityCondition> conds = FastList.newInstance();
				conds.add(EntityCondition.makeCondition("roleTypeId", "FACILITY_ADMIN"));
				conds.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, listDistributorIds));
				conds.add(EntityUtil.getFilterByDateExpr());
				List<GenericValue> listFaPa = FastList.newInstance();
				try {
					listFaPa = delegator.findList("FacilityParty", EntityCondition.makeCondition(conds), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList FacilityParty: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listFaPa.isEmpty()){
					List<String> listConsignIds = FastList.newInstance();
					listConsignIds = EntityUtil.getFieldListFromEntityList(listFaPa, "facilityId", true);
					conds = FastList.newInstance();
					conds.add(EntityCondition.makeCondition("facilityId", EntityOperator.IN, listConsignIds));
					conds.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company));
					List<GenericValue> listFaConsignInCompany = FastList.newInstance();
					try {
						listFaConsignInCompany = delegator.findList("Facility", EntityCondition.makeCondition(conds), null, null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (!listFaConsignInCompany.isEmpty()){
						List<String> listFas = EntityUtil.getFieldListFromEntityList(listFaConsignInCompany, "facilityId", true);
						listFacilityIds.addAll(listFas);
					}
				}
			}
			
		} else if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "VIEW", "MODULE", "LOGISTICS")){
			if (SecurityUtil.getOlbiusSecurity(security).olbiusHasPermission(userLogin, "ADMIN", "MODULE", "LOGISTICS")){
				List<GenericValue> listFaCompany = FastList.newInstance();
				try {
					listFaCompany = delegator.findList("Facility", EntityCondition.makeCondition("ownerPartyId", company), null, null, null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList Facility: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				if (!listFaCompany.isEmpty()) listFacilityIds = EntityUtil.getFieldListFromEntityList(listFaCompany, "facilityId", true);
			} else {
				listFacilityIds = FacilityUtil.getFacilityManages(delegator, userLogin);
				listAllConditions.add(EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS, company));
			}
		}
		
		if (!listFacilityIds.isEmpty()){
			listAllConditions.add(EntityCondition.makeCondition("facilityId", EntityJoinOperator.IN, listFacilityIds));
		} else {
			result.put("listIterator", listIterator);
			return result;
		}
		
		listSortFields.add("-fromDate");
		
		try {
			listIterator = delegator.find("StockEventDetail", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			Debug.log(e.getStackTrace().toString(), module);
			return ServiceUtil.returnError("Service listStockEvents:" + e.toString());
		}
		result.put("listIterator", listIterator);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStockEventItemTempData(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("eventId") && UtilValidate.isNotEmpty(parameters.get("eventId"))
					&& parameters.containsKey("partyInput") && UtilValidate.isNotEmpty(parameters.get("partyInput"))
					&& parameters.containsKey("partyCount") && UtilValidate.isNotEmpty(parameters.get("partyCount"))
					&& parameters.containsKey("partyScan") && UtilValidate.isNotEmpty(parameters.get("partyScan"))
					&& parameters.containsKey("partyCheck") && UtilValidate.isNotEmpty(parameters.get("partyCheck"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS,
						parameters.get("eventId")[0]));
				listAllConditions.add(EntityCondition.makeCondition("partyInput", EntityJoinOperator.EQUALS,
						parameters.get("partyInput")[0]));
				listAllConditions.add(EntityCondition.makeCondition("partyCount", EntityJoinOperator.EQUALS,
						parameters.get("partyCount")[0]));
				listAllConditions.add(EntityCondition.makeCondition("partyScan", EntityJoinOperator.EQUALS,
						parameters.get("partyScan")[0]));
				listAllConditions.add(EntityCondition.makeCondition("partyCheck", EntityJoinOperator.EQUALS,
						parameters.get("partyCheck")[0]));
				listSortFields.add("eventItemSeqId");
				EntityListIterator listIterator = delegator.find("StockEventItemAndRoleTempData",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStockEventItem(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("eventId") && UtilValidate.isNotEmpty(parameters.get("eventId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS,
						parameters.get("eventId")[0]));
				if (parameters.containsKey("location") && UtilValidate.isNotEmpty(parameters.get("location"))) {
					listAllConditions.add(EntityCondition.makeCondition("location", EntityJoinOperator.EQUALS,
							parameters.get("location")[0]));
				}
				listSortFields.add("toHighlight");
				listSortFields.add("-editable");
				listSortFields.add("eventItemSeqId");
				EntityListIterator listIterator = delegator.find("StockEventAggregated2",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStockEventAggregated(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("eventId") && UtilValidate.isNotEmpty(parameters.get("eventId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS,
						parameters.get("eventId")[0]));
				listSortFields.add("quantityDifference");
				listSortFields.add("productId");
				EntityListIterator listIterator = delegator.find("StockEventAggregated",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStockEventVariance(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("eventId") && UtilValidate.isNotEmpty(parameters.get("eventId"))
					&& parameters.containsKey("productId") && UtilValidate.isNotEmpty(parameters.get("productId"))) {
				List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
				List<String> listSortFields = (List<String>) context.get("listSortFields");
				EntityFindOptions opts = (EntityFindOptions) context.get("opts");
				listAllConditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS,
						parameters.get("eventId")[0]));
				listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS,
						parameters.get("productId")[0]));
				listSortFields.add("-eventVarianceSeqId");
				EntityListIterator listIterator = delegator.find("StockEventVariance",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listGoodIdentificationAndProduct(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listSortFields.add("productName");
			EntityListIterator listIterator = delegator.find("GoodIdentificationAndProduct",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static Map<String, Object> uploadStockingExcelFiles(DispatchContext ctx, Map<String, Object> context) throws IOException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		InputStream stream = null;
		String eventId = null;
		if (UtilValidate.isNotEmpty(context.get("eventId"))) {
			eventId = (String) context.get("eventId");
		}
		String fileName = null;
		if (UtilValidate.isNotEmpty(context.get("fileName"))) {
			fileName = (String) context.get("fileName");
		}
		String location = null;
		if (UtilValidate.isNotEmpty(fileName)) {
			fileName = fileName.replaceAll("\\\\", "\\\\\\\\");
			fileName = fileName.split("\\\\")[4];
			location = fileName.split("\\.")[0];
		}
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		try {
			ByteBuffer uploadedFile = (ByteBuffer) context.get("uploadedFile");
			stream = new ByteArrayInputStream(uploadedFile.array());
			Workbook workbook = getWorkbook(stream, (String)context.get("fileName"));
			Sheet firstSheet = workbook.getSheetAt(0);
	        Iterator<Row> iterator = firstSheet.iterator();
	        while (iterator.hasNext()) {
	        	Row nextRow = iterator.next();
	        	switch (nextRow.getRowNum()) {
				case 0:
					checkTitleLv1(nextRow);
					break;
				default:
					List<Map<String, Object>> listInvTmp = new ArrayList<Map<String, Object>>();
					listInvTmp = analyzeContent(nextRow, delegator);
					listItems.addAll(listInvTmp);
					break;
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseCRMUiLabels", "WrongFormat", locale) + e.getMessage());
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		if (!listItems.isEmpty()){
			for (Map<String, Object> prd : listItems) {
				String line = "";
				if (UtilValidate.isNotEmpty(prd.get("productId"))) {
					String productId = (String)prd.get("productId");
					GenericValue objProduct = null;
					try {
						objProduct = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findOne Product: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (UtilValidate.isEmpty(objProduct)) {
						List<GenericValue> listProduct = FastList.newInstance();
						try {
							listProduct = delegator.findList("Product", EntityCondition.makeCondition("productCode", productId), null,
									null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList Product: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!listProduct.isEmpty()){
							objProduct = listProduct.get(0);
						}
					}
					if (UtilValidate.isEmpty(objProduct)) {
						String errMsg = UtilProperties.getMessage(resourceError, "BLProductNotExists", locale) + " " + productId;
						Debug.logError(errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					List<GenericValue> listGoodIdentification = FastList.newInstance();
					try {
						listGoodIdentification = delegator.findList("GoodIdentification", EntityCondition.makeCondition("productId", productId), null,
								null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList GoodIdentification: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					String a = "";
					if (!listGoodIdentification.isEmpty()){
						a = listGoodIdentification.get(0).getString("idValue");
					} else {
						a = objProduct.getString("productCode");
					}
					line = a;
				}
				if (UtilValidate.isNotEmpty(prd.get("quantity"))) {
					line = line + "," + (String)prd.get("quantity");
				}
				if (UtilValidate.isNotEmpty(prd.get("expireDate"))) {
					line = line + "," + (String)prd.get("expireDate");
				}
				if (UtilValidate.isNotEmpty(prd.get("datetimeManufactured"))) {
					line = line + "," + (String)prd.get("datetimeManufactured");
				}
				if (UtilValidate.isNotEmpty(prd.get("lotId"))) {
					if (prd.get("lotId") instanceof  Double){
						NumberFormat formatter = new DecimalFormat("0");
					    String temp = formatter.format(prd.get("lotId"));
					    line = line + "," + String.valueOf(temp);
					}
					else {
						line = line + "," + (String)prd.get("lotId");
					}
				}
				try {
					createStockEventItemTempData(delegator, eventId, line, location);
				} catch (Exception e) {
					e.printStackTrace();
					return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "BLFileFormatError", locale) + e.getMessage());
				}
			}
			LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyInput = null;
			if (UtilValidate.isNotEmpty(context.get("partyInput"))) {
				partyInput = (String) context.get("partyInput");
			}
			String partyCount = null;
			if (UtilValidate.isNotEmpty(context.get("partyCount"))) {
				partyCount = (String) context.get("partyCount");
			}
			String partyScan = null;
			if (UtilValidate.isNotEmpty(context.get("partyScan"))) {
				partyScan = (String) context.get("partyScan");
			}
			String partyCheck = null;
			if (UtilValidate.isNotEmpty(context.get("partyCheck"))) {
				partyCheck = (String) context.get("partyCheck");
			}
			// createStockEventItemRoleTempData
			try {
				dispatcher.runSync("createStockEventItemRoleTempData",
						UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyInput,
								"roleTypeId", "STOCKING_INPUT", "userLogin", userLogin));
				dispatcher.runSync("createStockEventItemRoleTempData",
						UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyCount,
								"roleTypeId", "STOCKING_COUNT", "userLogin", userLogin));
				dispatcher.runSync("createStockEventItemRoleTempData",
						UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyScan,
								"roleTypeId", "STOCKING_SCAN", "userLogin", userLogin));
				dispatcher.runSync("createStockEventItemRoleTempData",
						UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyCheck,
								"roleTypeId", "STOCKING_CHECK", "userLogin", userLogin));
			} catch (GenericServiceException e) {
				e.printStackTrace();
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, "HasErrorWhenProcessing", locale) + e.getMessage());
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static String uploadStockingFiles(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		InputStream stream = null;
		BufferedReader bReader = null;
		boolean beganTx = TransactionUtil.begin(7200);
		Locale locale = UtilHttp.getLocale(request);
		try {
			String eventId = null;
			String partyInput = null;
			String partyCount = null;
			String partyScan = null;
			String partyCheck = null;
			List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			for (FileItem item : items) {
				if (item.isFormField()) {
					if ("eventId".equals(item.getFieldName())) {
						eventId = IOUtils.toString(item.getInputStream(), "UTF-8");
					}
					if ("partyInput".equals(item.getFieldName())) {
						partyInput = IOUtils.toString(item.getInputStream(), "UTF-8");
					}
					if ("partyCount".equals(item.getFieldName())) {
						partyCount = IOUtils.toString(item.getInputStream(), "UTF-8");
					}
					if ("partyScan".equals(item.getFieldName())) {
						partyScan = IOUtils.toString(item.getInputStream(), "UTF-8");
					}
					if ("partyCheck".equals(item.getFieldName())) {
						partyCheck = IOUtils.toString(item.getInputStream(), "UTF-8");
					}
				}
			}
			for (FileItem item : items) {
				if (!item.isFormField()) {
					String fileName = FilenameUtils.getName(item.getName());
					if (UtilValidate.isNotEmpty(fileName)) {
						String location = fileName.split("\\.")[0];
						String eof = fileName.split("\\.")[1];
						InputStream fileContent = item.getInputStream();
						ByteBuffer uploadedFile = ByteBuffer.wrap(IOUtils.toByteArray(fileContent));
						
						if (UtilValidate.isNotEmpty(eof) && "TXT".equals(eof.toUpperCase())){
							// file text .txt
							stream = new ByteArrayInputStream(uploadedFile.array());
							bReader = new BufferedReader(new InputStreamReader(stream));
							String line;
							while ((line = bReader.readLine()) != null) {
								createStockEventItemTempData(delegator, eventId, line, location);
							}
						}  
						// createStockEventItemRoleTempData
						dispatcher.runSync("createStockEventItemRoleTempData",
								UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyInput,
										"roleTypeId", "STOCKING_INPUT", "userLogin", userLogin));
						dispatcher.runSync("createStockEventItemRoleTempData",
								UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyCount,
										"roleTypeId", "STOCKING_COUNT", "userLogin", userLogin));
						dispatcher.runSync("createStockEventItemRoleTempData",
								UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyScan,
										"roleTypeId", "STOCKING_SCAN", "userLogin", userLogin));
						dispatcher.runSync("createStockEventItemRoleTempData",
								UtilMisc.toMap("eventId", eventId, "location", location, "partyId", partyCheck,
										"roleTypeId", "STOCKING_CHECK", "userLogin", userLogin));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resourceError, "BLFileFormatError", locale));
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return "error";
		} finally {
			if (stream != null) {
				stream.close();
			}
			if (bReader != null) {
				bReader.close();
			}
		}
		TransactionUtil.commit(beganTx);
		return "success";
	}

	private static List<Map<String, Object>> analyzeContent(Row nextRow, Delegator delegator) throws GenericEntityException {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = FastMap.newInstance();
		BigDecimal quantity = BigDecimal.ZERO;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				if (UtilValidate.isNotEmpty(value)) {
					if (value instanceof  Double){
						NumberFormat formatter = new DecimalFormat("0");
					    String temp = formatter.format(value);
						value = String.valueOf(temp);
					}
					List<GenericValue> products = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("productCode", value)), null, null, null, false);
					if (!products.isEmpty()){
						map.put("productId", products.get(0).getString("productId"));
						map.put("productCode", value);
						map.put("isProductId", true);
					} else {
						map.put("isProductId", false);
						map.put("productId", value);
						map.put("productCode", value);
					}
				}
				break;
			case 1:
				if (UtilValidate.isNotEmpty(value)) {
					map.put("quantity", value.toString());
					quantity = new BigDecimal(value.toString());
				}
				break;
			case 2:
				if (UtilValidate.isNotEmpty(value)) {
					map.put("datetimeManufactured", value);
				}
				break;
			case 3:
				if (UtilValidate.isNotEmpty(value)) {
					map.put("expireDate", value);
				}
				break;
			case 4:
				if (UtilValidate.isNotEmpty(value)) {
					map.put("lotId", value);
				}
				break;
			default:
				break;
			}
		}
		if (quantity.compareTo(BigDecimal.ZERO) > 0){
			listItems.add(map);
		}
		return listItems;
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
	
	private static void checkTitleLv1(Row nextRow) throws Exception {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				if (!"Mã sản phẩm".equals(value)) {
					throw new Exception("Mã sản phẩm");
				}
				break;
			case 1:
				if (!"Số lượng".equals(value)) {
					throw new Exception("Số lượng");
				}
				break;
			case 2:
				if (!"Ngày sản xuất".equals(value)) {
					throw new Exception("Ngày sản xuất");
				}
				break;
			case 3:
				if (!"Hạn sử dụng".equals(value)) {
					throw new Exception("Hạn sử dụng");
				}
				break;
			case 4:
				if (!"Lô sản xuất".equals(value)) {
					throw new Exception("Lô sản xuất");
				}
				break;
			default:
				break;
			}
		}
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
	
	private static void createStockEventItemTempData(Delegator delegator, Object eventId, String line, String location)
			throws Exception {
		String[] dummy = line.split(",");
		if (dummy.length > 1) {
			String idValue = dummy[0];
			if (UtilValidate.isNotEmpty(idValue)) {
				
				List<GenericValue> listGoodIdentification = FastList.newInstance();
				try {
					listGoodIdentification = delegator.findList("GoodIdentification", EntityCondition.makeCondition("idValue", idValue), null, null,
							null, false);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when findList GoodIdentification: " + e.toString();
					Debug.logError(e, errMsg, module);
				}
				
				BigDecimal quantity = new BigDecimal(dummy[1]);
				String exp = dummy[2];
				String mnf = dummy[3];
				String lot = dummy[4];
				GenericValue value = delegator.makeValidValue("StockEventItemTempData", UtilMisc.toMap("eventId",
						eventId, "quantity", quantity, "location", location));
				
				if (!listGoodIdentification.isEmpty()){
					value.put("idValue", idValue);
					GenericValue goodIdentification = delegator.findOne("GoodIdentification",
							UtilMisc.toMap("goodIdentificationTypeId", "SKU", "idValue", idValue), false);
					if (UtilValidate.isNotEmpty(goodIdentification)) {
						Object productId = goodIdentification.get("productId");
						GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
						if (UtilValidate.isNotEmpty(product)) {
							value.set("productId", productId);
							value.set("productCode", product.get("productCode"));
							value.set("productName", product.get("productName"));
						}
					}
				} else {
					List<GenericValue> listProduct = FastList.newInstance();
					try {
						listProduct = delegator.findList("Product", EntityCondition.makeCondition("productCode", idValue), null,
								null, null, false);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when findList Product: " + e.toString();
						Debug.logError(e, errMsg, module);
					}
					if (!listProduct.isEmpty()){
						GenericValue product = listProduct.get(0);
						value.put("productId", product.get("productId"));
						value.put("productCode", product.get("productCode"));
						value.put("productName", product.get("productName"));
						listGoodIdentification = FastList.newInstance();
						try {
							listGoodIdentification = delegator.findList("GoodIdentification", EntityCondition.makeCondition("productId", product.get("productId")), null,
									null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList GoodIdentification: " + e.toString();
							Debug.logError(e, errMsg, module);
						}
						if (!listGoodIdentification.isEmpty()){
							value.put("idValue", listGoodIdentification.get(0).get("idValue"));
						}
					}
				}
				
				delegator.setNextSubSeqId(value, "eventItemSeqId", 5, 1);
				if (UtilValidate.isNotEmpty(mnf)) {
					String[] mnfArr = mnf.split("/");
					String day = mnfArr[0];
					String month = mnfArr[1];
					String year = mnfArr[2];
					if (day.length() == 1){
						day = "0"+day;
					}
					if (month.length() == 1){
						month = "0"+month;
					}
					String fullDate = year+"-"+month+"-"+day;
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp manufactureDate = new Timestamp ((dateFormat.parse(fullDate)).getTime());
					value.set("manufactureDate", manufactureDate);
				}
				if (UtilValidate.isNotEmpty(exp)) {
					String[] expArr = exp.split("/");
					String day = expArr[0];
					String month = expArr[1];
					String year = expArr[2];
					if (day.length() == 1){
						day = "0"+day;
					}
					if (month.length() == 1){
						month = "0"+month;
					}
					String fullDate = year+"-"+month+"-"+day;
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Timestamp expireDate = new Timestamp ((dateFormat.parse(fullDate)).getTime());
					value.set("expireDate", expireDate);
				}
				if (UtilValidate.isNotEmpty(lot)) {
					value.set("lot", lot);
				}
				value.create();
			}
		}
	}

	private static void deleteStockEventItemTempData(Delegator delegator, Object eventId, Object partyInput,
			Object partyCount, Object partyScan, Object partyCheck) throws Exception {
		EntityListIterator iterator = delegator
				.find("StockEventItemAndRoleTempData",
						EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "partyInput", partyInput,
								"partyCount", partyCount, "partyScan", partyScan, "partyCheck", partyCheck)),
						null, null, null, null);
		GenericValue value = null;
		List<EntityCondition> conditions = FastList.newInstance();
		while ((value = iterator.next()) != null) {
			conditions.clear();
			conditions.add(EntityCondition.makeCondition(
					UtilMisc.toMap("eventId", value.get("eventId"), "eventItemSeqId", value.get("eventItemSeqId"))));
			delegator.removeByCondition("StockEventItemTempData", EntityCondition.makeCondition(conditions));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", value.get("eventId"), "location",
					value.get("location"), "partyId", value.get("partyInput"), "roleTypeId", "STOCKING_INPUT")));
			delegator.removeByCondition("StockEventItemRoleTempData", EntityCondition.makeCondition(conditions));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", value.get("eventId"), "location",
					value.get("location"), "partyId", value.get("partyCount"), "roleTypeId", "STOCKING_COUNT")));
			delegator.removeByCondition("StockEventItemRoleTempData", EntityCondition.makeCondition(conditions));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", value.get("eventId"), "location",
					value.get("location"), "partyId", value.get("partyScan"), "roleTypeId", "STOCKING_SCAN")));
			delegator.removeByCondition("StockEventItemRoleTempData", EntityCondition.makeCondition(conditions));

			conditions.clear();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", value.get("eventId"), "location",
					value.get("location"), "partyId", value.get("partyCheck"), "roleTypeId", "STOCKING_CHECK")));
			delegator.removeByCondition("StockEventItemRoleTempData", EntityCondition.makeCondition(conditions));
		}
		if (iterator != null) {
			iterator.close();
		}
	}

	public static Map<String, Object> deleteAllStockEventItemTempData(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			Object eventId = context.get("eventId");
			Object partyInput = context.get("partyInput");
			Object partyCount = context.get("partyCount");
			Object partyScan = context.get("partyScan");
			Object partyCheck = context.get("partyCheck");
			deleteStockEventItemTempData(delegator, eventId, partyInput, partyCount, partyScan, partyCheck);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> transferToStockEventItem(DispatchContext ctx, Map<String, Object> context)
			throws Exception {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		EntityListIterator iterator = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Object eventId = context.get("eventId");
			Object partyInput = context.get("partyInput");
			Object partyCount = context.get("partyCount");
			Object partyScan = context.get("partyScan");
			Object partyCheck = context.get("partyCheck");

			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "partyInput", partyInput,
					"partyCount", partyCount, "partyScan", partyScan, "partyCheck", partyCheck)));
			iterator = delegator.find("StockEventItemAndRoleTempData", EntityCondition.makeCondition(conditions), null,
					null, null, null);
			GenericValue value = null;
			Set<String> locations = new HashSet<>();
			while ((value = iterator.next()) != null) {
				String editable = "N";
				if (UtilValidate.isEmpty(value.get("productId"))) {
					editable = "Y";
				}
				String productId = null;
				if (value.containsKey("productId")){
					productId = (String)value.get("productId");
				}
				String idValue = null;
				if (value.containsKey("idValue")){
					idValue = (String)value.get("idValue");
				}
				if (UtilValidate.isEmpty(productId)) {
					if (UtilValidate.isNotEmpty(idValue)) {
						List<GenericValue> list = FastList.newInstance();
						try {
							list = delegator.findList("GoodIdentification", EntityCondition.makeCondition(UtilMisc.toMap("idValue", idValue)), null, null, null, false);
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findList GoodIdentification: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (!list.isEmpty()){
							productId = list.get(0).getString("productId");
						}
					}
				}
				if (UtilValidate.isNotEmpty(productId)) {
					dispatcher.runSync("createStockEventItem",
							UtilMisc.toMap("eventId", eventId, "idValue", idValue, "productId", productId, "quantity",
									value.get("quantity"), "location", value.get("location"), "editable", editable, "expireDate", value.get("expireDate"), "manufactureDate", value.get("manufactureDate"), "lot", value.get("lot"),
									"userLogin", userLogin));
					locations.add(value.getString("location"));
				}
			}
			for (String location : locations) {
				dispatcher.runSync("createStockEventItemStatus", UtilMisc.toMap("eventId", eventId, "location",
						location, "statusId", "STOCKING_CREATED", "userLogin", userLogin));
				// createStockEventItemRole
				dispatcher.runSync("createStockEventItemRole", UtilMisc.toMap("eventId", eventId, "location", location,
						"partyId", partyInput, "roleTypeId", "STOCKING_INPUT", "userLogin", userLogin));
				dispatcher.runSync("createStockEventItemRole", UtilMisc.toMap("eventId", eventId, "location", location,
						"partyId", partyCount, "roleTypeId", "STOCKING_COUNT", "userLogin", userLogin));
				dispatcher.runSync("createStockEventItemRole", UtilMisc.toMap("eventId", eventId, "location", location,
						"partyId", partyScan, "roleTypeId", "STOCKING_SCAN", "userLogin", userLogin));
				dispatcher.runSync("createStockEventItemRole", UtilMisc.toMap("eventId", eventId, "location", location,
						"partyId", partyCheck, "roleTypeId", "STOCKING_CHECK", "userLogin", userLogin));
			}
			deleteStockEventItemTempData(delegator, eventId, partyInput, partyCount, partyScan, partyCheck);
			result.put("eventId", eventId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}

	public static Object getEventId(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin,
			Object facilityId) throws Exception {
		Object eventId = null;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "isClosed", "N")));
		List<GenericValue> stockEvents = delegator.findList("StockEvent", EntityCondition.makeCondition(conditions),
				null, null, null, false);
		if (UtilValidate.isEmpty(stockEvents)) {
			Map<String, Object> result = dispatcher.runSync("createStockEvent",
					UtilMisc.toMap("facilityId", facilityId, "userLogin", userLogin));
			eventId = result.get("eventId");
		} else {
			eventId = EntityUtil.getFirst(stockEvents).get("eventId");
		}
		return eventId;
	}

	public static Map<String, Object> createStockEvent(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			Object eventId = UtilValidate.isEmpty(context.get("eventId")) ? delegator.getNextSeqId("StockEvent")
					: context.get("eventId");
			GenericValue stockEvent = delegator.makeValidValue("StockEvent", context);
			stockEvent.set("eventId", eventId);
			if (UtilValidate.isEmpty(context.get("fromDate"))) {
				stockEvent.set("fromDate", UtilDateTime.nowTimestamp());
			}
			stockEvent.create();
			GenericValue system = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId", "system"));
			dispatcher.runAsync("freezeInventory", UtilMisc.toMap("eventId", eventId, "userLogin", system));
			result.put("eventId", eventId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateStockEvent(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEvent = delegator.makeValidValue("StockEvent", context);
			stockEvent.store();
			result.put("eventId", stockEvent.get("eventId"));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteStockEvent(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		try {
			Object eventId = context.get("eventId");
			GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", eventId), false);
			stockEvent.set("thruDate", UtilDateTime.nowTimestamp());
			stockEvent.set("isClosed", "Y");
			stockEvent.set("description", UtilProperties.getMessage(resource, "Canceled", locale));
			stockEvent.store();
			result.put("eventId", eventId);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> finishStockEvent(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", context.get("eventId")),
					false);
			stockEvent.set("thruDate", UtilDateTime.nowTimestamp());
			stockEvent.store();
			autoFillVariance(ctx, context);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	private static void autoFillVariance(DispatchContext ctx, Map<String, Object> context) throws Exception {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Object eventId = context.get("eventId");

		EntityListIterator iterator = delegator.find("StockEventAggregated",
				EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId), null, null, null, null);
		GenericValue value = null;
		while ((value = iterator.next()) != null) {
			Object productId = value.get("productId");
			result = dispatcher.runSync("getQuantityNonVariance",
					UtilMisc.toMap("eventId", eventId, "productId", productId, "userLogin", userLogin));
			BigDecimal quantity = (BigDecimal) result.get("quantity");
			if (quantity.compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal quantityDifference = value.getBigDecimal("quantityDifference");
				String varianceReasonId = "VAR_LOST";
				if (quantityDifference.compareTo(BigDecimal.ZERO) > 0) {
					varianceReasonId = "VAR_FOUND";
				}
				dispatcher.runSync("createStockEventVariance",
						UtilMisc.toMap("eventId", eventId, "productId", productId, "statusId",
								"STOCKING_VARIANCE_CREATED", "varianceReasonId", varianceReasonId, "quantity", quantity,
								"userLogin", userLogin));
			}
		}
	}

	public static Map<String, Object> createStockEventItem(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventItem = delegator.makeValidValue("StockEventItem", context);
			delegator.setNextSubSeqId(stockEventItem, "eventItemSeqId", 5, 1);
			stockEventItem.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateStockEventItem(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			BigDecimal quantityRecheck = (BigDecimal) context.get("quantityRecheck");
			Long expireDate = null;
			if (UtilValidate.isNotEmpty(context.get("expireDate"))) {
				expireDate = new Long ((String) context.get("expireDate"));
			}
			Long manufactureDate = null;
			if (UtilValidate.isNotEmpty(context.get("manufactureDate"))) {
				manufactureDate = new Long ((String) context.get("manufactureDate"));
			}
			String lot = null;
			if (UtilValidate.isNotEmpty(context.get("lot"))) {
				lot = (String) context.get("lot");
			}
			GenericValue stockEventItem = delegator.findOne("StockEventItem",
					UtilMisc.toMap("eventId", context.get("eventId"), "eventItemSeqId", context.get("eventItemSeqId")),
					false);
			if (UtilValidate.isNotEmpty(stockEventItem)) {
				stockEventItem.set("quantityRecheck", quantityRecheck);
				BigDecimal toHighlight = quantityRecheck.subtract(stockEventItem.getBigDecimal("quantity"));
				if (toHighlight.compareTo(BigDecimal.ZERO) != 0) {
					stockEventItem.set("toHighlight", toHighlight.abs());
				}
				if (UtilValidate.isNotEmpty(expireDate)) {
					Timestamp exp = new Timestamp(expireDate);
					stockEventItem.set("expireDate", exp);
				}
				if (UtilValidate.isNotEmpty(manufactureDate)) {
					Timestamp mnf = new Timestamp(manufactureDate);
					stockEventItem.set("manufactureDate", mnf);
				}
				if (UtilValidate.isNotEmpty(lot)) {
					stockEventItem.set("lot", lot);
				}
				Timestamp manufactureDateRecheck = null;
				if (UtilValidate.isNotEmpty(context.get("manufactureDateRecheck"))) {
					manufactureDateRecheck = (Timestamp) context.get("manufactureDateRecheck");
					stockEventItem.set("manufactureDateRecheck", manufactureDateRecheck);
				}
				Timestamp expireDateRecheck = null;
				if (UtilValidate.isNotEmpty(context.get("expireDateRecheck"))) {
					expireDateRecheck = (Timestamp) context.get("expireDateRecheck");
					stockEventItem.set("expireDateRecheck", expireDateRecheck);
				}
				String lotRecheck = null;
				if (UtilValidate.isNotEmpty(context.get("lotRecheck"))) {
					lotRecheck = (String) context.get("lotRecheck");
					stockEventItem.set("lotRecheck", lotRecheck);
				}
				stockEventItem.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteStockEventItem(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummy = null;
		try {
			Object eventId = context.get("eventId");
			GenericValue stockEventItem = delegator.findOne("StockEventItem",
					UtilMisc.toMap("eventId", eventId, "eventItemSeqId", context.get("eventItemSeqId")), false);
			Object location = stockEventItem.get("location");
			stockEventItem.remove();

			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			dummy = delegator.find("StockEventItem",
					EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "location", location)), null, null,
					null, findOptions);
			if (dummy.getResultsTotalSize() == 0) {
				delegator.removeByCondition("StockEventItemStatus",
						EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "location", location)));
				delegator.removeByCondition("StockEventItemRole",
						EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "location", location)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (dummy != null) {
				try {
					dummy.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static Map<String, Object> updateQuantityRecheck(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		try {
			Object eventId = context.get("eventId");
			Object location = context.get("location");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			iterator = delegator.find("StockEventItem",
					EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "location", location)), null, null,
					null, null);
			GenericValue value = null;
			while ((value = iterator.next()) != null) {
				value.set("quantityRecheck", value.get("quantity"));
				value.set("expireDateRecheck", value.get("expireDate"));
				value.set("manufactureDateRecheck", value.get("manufactureDate"));
				value.set("lotRecheck", value.get("lot"));
				value.store();
			}
			dispatcher.runSync("updateStockEventItemStatus", UtilMisc.toMap("eventId", eventId, "location", location,
					"statusId", "STOCKING_GUARANTEED", "userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}

	public static Map<String, Object> createStockEventItemStatus(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventItemStatus = delegator.findOne("StockEventItemStatus",
					UtilMisc.toMap("eventId", context.get("eventId"), "location", context.get("location")), false);
			if (UtilValidate.isEmpty(stockEventItemStatus)) {
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				stockEventItemStatus = delegator.makeValidValue("StockEventItemStatus", context);
				stockEventItemStatus.set("statusUserLogin", userLogin.get("userLoginId"));
				delegator.create(stockEventItemStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateStockEventItemStatus(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			GenericValue stockEventItemStatus = delegator.findOne("StockEventItemStatus",
					UtilMisc.toMap("eventId", context.get("eventId"), "location", context.get("location")), false);
			stockEventItemStatus.set("statusId", context.get("statusId"));
			stockEventItemStatus.set("statusUserLogin", userLogin.get("userLoginId"));
			stockEventItemStatus.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateAllStockEventItemStatus(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			List<GenericValue> stockEventItemStatus = delegator.findList("StockEventItemStatus",
					EntityCondition.makeCondition(UtilMisc.toMap("eventId", context.get("eventId"))), null, null, null,
					false);
			for (GenericValue x : stockEventItemStatus) {
				x.set("statusId", context.get("statusId"));
				x.set("statusUserLogin", userLogin.get("userLoginId"));
				x.store();
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createStockEventRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventRole = delegator.makeValidValue("StockEventRole", context);
			GenericValue partyRole = delegator.makeValidValue("PartyRole", context);
			delegator.createOrStore(partyRole);
			delegator.createOrStore(stockEventRole);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteStockEventRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventRole = delegator.makeValidValue("StockEventRole", context);
			stockEventRole.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createStockEventItemRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventItemRole = delegator.makeValidValue("StockEventItemRole", context);
			delegator.createOrStore(stockEventItemRole);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteStockEventItemRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventItemRole = delegator.makeValidValue("StockEventItemRole", context);
			stockEventItemRole.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createStockEventItemRoleTempData(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventItemRole = delegator.makeValidValue("StockEventItemRoleTempData", context);
			delegator.createOrStore(stockEventItemRole);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteStockEventItemRoleTempData(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventItemRole = delegator.makeValidValue("StockEventItemRoleTempData", context);
			stockEventItemRole.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> createStockEventVariance(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventVariance = delegator.makeValidValue("StockEventVariance", context);
			delegator.setNextSubSeqId(stockEventVariance, "eventVarianceSeqId", 5, 1);
			delegator.create(stockEventVariance);
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateStockEventVariance(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventVariance = delegator.makeValidValue("StockEventVariance", context);
			stockEventVariance.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteStockEventVariance(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue stockEventVariance = delegator.makeValidValue("StockEventVariance", context);
			stockEventVariance.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> submitQuantityAggregated2(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		EntityListIterator iterator = null;
		try {
			Object eventId = context.get("eventId");
			GenericValue userLogin = (GenericValue) context.get("userLogin");

			iterator = delegator.find("StockEventVariance",
					EntityCondition
							.makeCondition(UtilMisc.toMap("eventId", eventId, "statusId", "STOCKING_VARIANCE_CREATED")),
					null, null, null, null);
			GenericValue value = null;
			while ((value = iterator.next()) != null) {
//				String productId = value.getString("productId");
//				String varianceReasonId = value.getString("varianceReasonId");
//				BigDecimal quantity = value.getBigDecimal("quantity");
//				String comments = value.getString("comments");
				// viettb's place

				// end viettb's place
				dispatcher.runSync("updateAllStockEventItemStatus",
						UtilMisc.toMap("eventId", eventId, "statusId", "STOCKING_COMPLETED", "userLogin", userLogin));
			}
			delegator.storeByCondition("StockEvent", UtilMisc.toMap("isClosed", "Y"),
					EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId)));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}

	public static Map<String, Object> submitQuantityAggregatedV1(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue system = delegator.findOne("UserLogin", true, UtilMisc.toMap("userLoginId", "system"));
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator,
				userLogin.getString("userLoginId"));
		try {
			Object eventId = context.get("eventId");
			iterator = delegator.find("StockEventAggregated",
					EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId), null, null, null,
					null);
			GenericValue value = null;
			while ((value = iterator.next()) != null) {
				GenericValue product = delegator.findOne("Product", false,
						UtilMisc.toMap("productId", value.getString("productId")));
				String requireAmount = product.getString("requireAmount");
				Boolean reqAmount = false;
				if (UtilValidate.isNotEmpty(requireAmount) && "Y".equals(requireAmount)) {
					reqAmount = true;
				}

				if (value.getBigDecimal("quantityDifference").compareTo(BigDecimal.ZERO) > 0) {
					Map<String, Object> inventoryItem = FastMap.newInstance();
					Timestamp datetimeReceived = new Timestamp(new Date().getTime());
					inventoryItem.put("productId", value.getString("productId"));

					GenericValue party = delegator.findOne("PartyAcctgPreference", false,
							UtilMisc.toMap("partyId", ownerPartyId));
					inventoryItem.put("currencyUomId", party.get("baseCurrencyUomId"));

					BigDecimal quantityReceive = value.getBigDecimal("quantityDifference");
					// inventoryItem.put("quantityOnHandTotal",
					// quantityReceive);
					// inventoryItem.put("availableToPromiseTotal",
					// quantityReceive);
					// inventoryItem.put("accountingQuantityTotal",
					// quantityReceive);
					//
					// if (reqAmount) {
					// inventoryItem.put("quantityOnHandTotal", BigDecimal.ONE);
					// inventoryItem.put("amountOnHandTotal", quantityReceive);
					// inventoryItem.put("availableToPromiseTotal",
					// BigDecimal.ONE);
					// }
					//
					GenericValue productAverageCost = null;
					Map<String, String> productAvgFindMap = UtilMisc.toMap("productId", value.getString("productId"),
							"facilityId", value.getString("facilityId"), "organizationPartyId", ownerPartyId,
							"productAverageCostTypeId", "SIMPLE_AVG_COST");
					List<GenericValue> productAverageCostList = delegator.findByAnd("ProductAverageCost",
							productAvgFindMap, UtilMisc.toList("-fromDate"), false);
					productAverageCostList = EntityUtil.filterByDate(productAverageCostList, true);
					productAverageCost = (productAverageCostList != null && productAverageCostList.size() > 0)
							? productAverageCostList.get(0) : null;
					if (productAverageCost != null) {
						inventoryItem.put("unitCost", productAverageCost.getBigDecimal("averageCost"));
						inventoryItem.put("purCost", productAverageCost.getBigDecimal("averagePurCost"));
					} else {
						inventoryItem.put("unitCost", BigDecimal.ZERO);
						inventoryItem.put("purCost", BigDecimal.ZERO);
					}
					inventoryItem.put("datetimeReceived", datetimeReceived);
					inventoryItem.put("facilityId", value.getString("facilityId"));
					inventoryItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
					inventoryItem.put("ownerPartyId", ownerPartyId);
					inventoryItem.put("comments", "NOACC");
					inventoryItem.put("userLogin", system);
					String inventoryItemId = null;
					try {
						Map<String, Object> mapTmp = dispatcher.runSync("createInventoryItem", inventoryItem);
						inventoryItemId = (String) mapTmp.get("inventoryItemId");
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItem error!");
					}

					Map<String, Object> inventoryItemDetail = FastMap.newInstance();
					inventoryItemDetail.put("inventoryItemId", inventoryItemId);
					inventoryItemDetail.put("quantityOnHandDiff", quantityReceive);
					inventoryItemDetail.put("availableToPromiseDiff", quantityReceive);
					inventoryItemDetail.put("accountingQuantityDiff", quantityReceive);
					if (reqAmount) {
						inventoryItem.put("quantityOnHandDiff", BigDecimal.ONE);
						inventoryItem.put("amountOnHandDiff", quantityReceive);
						inventoryItem.put("availableToPromiseDiff", BigDecimal.ONE);
					}
					inventoryItemDetail.put("description", "NOACC");
					inventoryItemDetail.put("userLogin", system);
					try {
						dispatcher.runSync("createInventoryItemDetail", inventoryItemDetail);
					} catch (GenericServiceException e) {
						return ServiceUtil.returnError("OLBIUS: runsync service createInventoryItemDetail error!");
					}
				} else if (value.getBigDecimal("quantityDifference").compareTo(BigDecimal.ZERO) < 0) {
					BigDecimal quantityDifference = value.getBigDecimal("quantityDifference").abs();
					// Map<String, String> inventoryItemFindMap =
					// UtilMisc.toMap("productId", value.getString("productId"),
					// "facilityId", value.getString("facilityId"),
					// "organizationPartyId", ownerPartyId);
					EntityCondition Cond1 = EntityCondition.makeCondition("productId", EntityOperator.EQUALS,
							value.getString("productId"));
					EntityCondition Cond2 = EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,
							value.getString("facilityId"));
					EntityCondition Cond3 = EntityCondition.makeCondition("ownerPartyId", EntityOperator.EQUALS,
							ownerPartyId);
					EntityCondition Cond4 = EntityCondition.makeCondition("quantityOnHandTotal",
							EntityOperator.GREATER_THAN, BigDecimal.ZERO);
					List<EntityCondition> listConds = UtilMisc.toList(Cond1, Cond2, Cond3, Cond4);
					EntityCondition allConds = EntityCondition.makeCondition(listConds, EntityOperator.AND);

					List<GenericValue> inventoryItemList = delegator.findList("InventoryItem", allConds, null,
							UtilMisc.toList("datetimeReceived"), null, false);

					for (int i = 0; i < inventoryItemList.size(); i++) {
						GenericValue inventoryItem = inventoryItemList.get(i);
						if (quantityDifference.compareTo(BigDecimal.ZERO) > 0) {
							Map<String, Object> inventoryItemDetail = FastMap.newInstance();
							inventoryItemDetail.put("inventoryItemId", inventoryItem.getString("inventoryItemId"));

							if (reqAmount) {
								if (quantityDifference
										.compareTo(inventoryItem.getBigDecimal("amountOnHandTotal")) > 0) {
									inventoryItemDetail.put("quantityOnHandDiff", new BigDecimal(-1));
									inventoryItemDetail.put("amountOnHandDiff", quantityDifference
											.subtract(inventoryItem.getBigDecimal("amountOnHandTotal")).negate());
									inventoryItemDetail.put("availableToPromiseDiff", new BigDecimal(-1));
									quantityDifference = quantityDifference
											.subtract(inventoryItem.getBigDecimal("amountOnHandTotal"));
								} else {
									inventoryItemDetail.put("quantityOnHandDiff", new BigDecimal(-1));
									inventoryItemDetail.put("amountOnHandDiff", quantityDifference.negate());
									inventoryItemDetail.put("availableToPromiseDiff", new BigDecimal(-1));
									quantityDifference = BigDecimal.ZERO;
								}
							} else {
								if (quantityDifference
										.compareTo(inventoryItem.getBigDecimal("quantityOnHandTotal")) > 0) {
									inventoryItemDetail.put("quantityOnHandDiff", quantityDifference
											.subtract(inventoryItem.getBigDecimal("quantityOnHandTotal")).negate());
									inventoryItemDetail.put("availableToPromiseDiff", quantityDifference
											.subtract(inventoryItem.getBigDecimal("quantityOnHandTotal")).negate());
									quantityDifference = quantityDifference
											.subtract(inventoryItem.getBigDecimal("quantityOnHandTotal"));
								} else {
									inventoryItemDetail.put("quantityOnHandDiff", quantityDifference.negate());
									inventoryItemDetail.put("availableToPromiseDiff", quantityDifference.negate());
									quantityDifference = BigDecimal.ZERO;
								}
							}
							inventoryItemDetail.put("userLogin", system);
							inventoryItemDetail.put("description", "NOACC");
							try {
								dispatcher.runSync("createInventoryItemDetail", inventoryItemDetail);
							} catch (GenericServiceException e) {
								return ServiceUtil
										.returnError("OLBIUS: runsync service createInventoryItemDetail error!");
							}
						}
					}
				}
			}
			dispatcher.runSync("updateAllStockEventItemStatus",
					UtilMisc.toMap("eventId", eventId, "statusId", "STOCKING_COMPLETED", "userLogin", userLogin));
			delegator.storeByCondition("StockEvent", UtilMisc.toMap("isClosed", "Y"),
					EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId)));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}

	public static Map<String, Object> submitQuantityAggregated(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		// String ownerPartyId =
		// MultiOrganizationUtil.getCurrentOrganization(delegator,
		// userLogin.getString("userLoginId"));
		try {
			Object eventId = context.get("eventId");

			GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", eventId), false);
			String facilityId = stockEvent.getString("facilityId");

			GenericValue facility = delegator.findOne("Facility", UtilMisc.toMap("facilityId", facilityId), false);
			String ownerPartyId = facility.getString("ownerPartyId");

			iterator = delegator.find("StockEventVariance",
					EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId), null, null, null,
					null);
			GenericValue value = null;
			List<Map<String, Object>> listProducts = new ArrayList<Map<String, Object>>();
			while ((value = iterator.next()) != null) {
				if (value.getString("productId") != null && value.getBigDecimal("quantity") != null
						&& value.getBigDecimal("quantity").compareTo(BigDecimal.ZERO) > 0) {
					GenericValue product = delegator.findOne("Product",
							UtilMisc.toMap("productId", value.getString("productId")), false);
					Map<String, Object> productMap = FastMap.newInstance();
					productMap.put("productId", value.getString("productId"));
					productMap.put("facilityId", facilityId);
					productMap.put("ownerPartyId", ownerPartyId);
					productMap.put("quantityOnHandVar", value.getBigDecimal("quantity").toString());
					productMap.put("quantityUomId", product.getString("quantityUomId"));
					productMap.put("varianceReasonId", value.getString("varianceReasonId"));
					listProducts.add(productMap);
				}
			}
			
			if (!listProducts.isEmpty()){
				dispatcher.runSync("createPhysicalInventoryAndMultiVarianceNew",
						UtilMisc.toMap("listProducts", listProducts, "facilityId", facilityId, "eventId", eventId,
								"partyId", ownerPartyId, "physicalInventoryDate", System.currentTimeMillis(), "userLogin",
								userLogin));
			}

			dispatcher.runSync("updateAllStockEventItemStatus",
					UtilMisc.toMap("eventId", eventId, "statusId", "STOCKING_COMPLETED", "userLogin", userLogin));
			
			Map<String, Object> map = FastMap.newInstance();
			map.put("eventId", eventId);
			map.put("userLogin", userLogin);
			try {
				Map<String, Object> rs = dispatcher.runSync("updateInventoryDateByStockEvent", map);
				if (ServiceUtil.isError(rs)) {
					return ServiceUtil.returnError(ServiceUtil.getErrorMessage(rs));
				}
			} catch (GenericServiceException e) {
				String errMsg = "OLBIUS: Fatal error when run service updateInventoryDateByStockEvent: " + e.toString();
				Debug.logError(e, errMsg, module);
				return ServiceUtil.returnError(errMsg);
			}
			
			delegator.storeByCondition("StockEvent", UtilMisc.toMap("isClosed", "Y"),
					EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId)));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		return result;
	}

	public static Map<String, Object> checkLocationExistsInStockEvent(DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummy = null;
		String check = "false";
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			dummy = delegator.find("StockEventItem",
					EntityCondition.makeCondition(
							UtilMisc.toMap("eventId", context.get("eventId"), "location", context.get("location"))),
					null, null, null, findOptions);
			if (dummy.getResultsTotalSize() == 0) {
				dummy.close();
				dummy = delegator.find("StockEventItemTempData",
						EntityCondition.makeCondition(
								UtilMisc.toMap("eventId", context.get("eventId"), "location", context.get("location"))),
						null, null, null, findOptions);
				if (dummy.getResultsTotalSize() == 0) {
					check = "true";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummy != null) {
				try {
					dummy.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("check", check);
		return result;
	}

	public static Map<String, Object> loadLocationInEvent(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<Map<String, Object>> locations = FastList.newInstance();
		try {
			List<GenericValue> stockEventItemStatus = delegator.findList("StockEventItemStatus",
					EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, context.get("eventId")), null,
					UtilMisc.toList("location"), null, false);
			for (GenericValue x : stockEventItemStatus) {
				Map<String, Object> item = FastMap.newInstance();
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", x.get("statusId")),
						true);
				item.put("text", x.get("location") + " [" + statusItem.get("description", locale) + "]");
				item.put("value", x.get("location"));
				item.put("statusId", x.get("statusId"));
				locations.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("locations", locations);
		return result;
	}

	public static Map<String, Object> getEmployeeByPositionType(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> empls = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("emplPositionTypeId", EntityJoinOperator.EQUALS,
					context.get("emplPositionTypeId")));
			List<GenericValue> emplPositionActiveDetail = delegator.findList("EmplPositionDetail",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("partyCode"), null, false);
			for (GenericValue x : emplPositionActiveDetail) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("partyName", x.get("partyCode") + " - " + x.get("partyName"));
				item.put("partyId", x.get("partyId"));
				empls.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("empls", empls);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createStockEventAndRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Object facilityId = context.get("facilityId");
			List<String> stockInputIds = (List<String>) context.get("stockInputIds[]");
			List<String> stockCountIds = (List<String>) context.get("stockCountIds[]");
			List<String> stockScanIds = (List<String>) context.get("stockScanIds[]");
			List<String> stockCheckIds = (List<String>) context.get("stockCheckIds[]");

			result = dispatcher.runSync("createStockEvent", UtilMisc.toMap("facilityId", facilityId, "eventName",
					context.get("eventName"), "userLogin", userLogin));

			Object eventId = result.get("eventId");
			for (String s : stockInputIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_INPUT", "userLogin", userLogin));
			}
			for (String s : stockCountIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_COUNT", "userLogin", userLogin));
			}
			for (String s : stockScanIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_SCAN", "userLogin", userLogin));
			}
			for (String s : stockCheckIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_CHECK", "userLogin", userLogin));
			}
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateStockEventAndRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			Object eventId = context.get("eventId");
			List<String> stockInputIds = (List<String>) context.get("stockInputIds[]");
			List<String> stockCountIds = (List<String>) context.get("stockCountIds[]");
			List<String> stockScanIds = (List<String>) context.get("stockScanIds[]");
			List<String> stockCheckIds = (List<String>) context.get("stockCheckIds[]");

			dispatcher.runSync("updateStockEvent",
					UtilMisc.toMap("eventId", eventId, "eventName", context.get("eventName"), "userLogin", userLogin));

			removeStockEventRole(delegator, eventId, stockInputIds, "STOCKING_INPUT");
			for (String s : stockInputIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_INPUT", "userLogin", userLogin));
			}
			removeStockEventRole(delegator, eventId, stockCountIds, "STOCKING_COUNT");
			for (String s : stockCountIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_COUNT", "userLogin", userLogin));
			}
			removeStockEventRole(delegator, eventId, stockScanIds, "STOCKING_SCAN");
			for (String s : stockScanIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_SCAN", "userLogin", userLogin));
			}
			removeStockEventRole(delegator, eventId, stockCheckIds, "STOCKING_CHECK");
			for (String s : stockCheckIds) {
				dispatcher.runSync("createStockEventRole", UtilMisc.toMap("eventId", eventId, "partyId", s,
						"roleTypeId", "STOCKING_CHECK", "userLogin", userLogin));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	private static void removeStockEventRole(Delegator delegator, Object eventId, List<String> parties,
			Object roleTypeId) throws GenericEntityException {
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_IN, parties));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", eventId, "roleTypeId", roleTypeId)));
		delegator.removeByCondition("StockEventRole", EntityCondition.makeCondition(conditions));
	}

	public static Map<String, Object> loadPartiesInEventByRole(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> parties = FastList.newInstance();
		try {
			List<GenericValue> stockEventRoleDetail = delegator.findList("StockEventRoleDetail",
					EntityCondition.makeCondition(
							UtilMisc.toMap("eventId", context.get("eventId"), "roleTypeId", context.get("roleTypeId"))),
					null, null, null, false);
			for (GenericValue x : stockEventRoleDetail) {
				Map<String, Object> item = FastMap.newInstance();
				item.put("partyName", x.get("partyCode") + " - " + x.get("partyName"));
				item.put("partyId", x.get("partyId"));
				parties.add(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("parties", parties);
		return result;
	}

	public static Map<String, Object> checkStockEventFinishable(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator dummy = null;
		String check = "false";
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			dummy = delegator.find("StockEventItemStatus",
					EntityCondition.makeCondition(
							UtilMisc.toMap("eventId", context.get("eventId"), "statusId", "STOCKING_CREATED")),
					null, null, null, findOptions);
			if (dummy.getResultsTotalSize() == 0) {
				dummy.close();

				List<EntityCondition> conditions2 = FastList.newInstance();
				conditions2.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, null));
				conditions2.add(EntityCondition.makeCondition("quantityRecheck", EntityJoinOperator.EQUALS, null));

				List<EntityCondition> conditions = FastList.newInstance();
				conditions.add(EntityCondition.makeCondition(conditions2, EntityOperator.OR));
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("eventId", context.get("eventId"))));

				dummy = delegator.find("StockEventItemDetail", EntityCondition.makeCondition(conditions), null, null,
						null, findOptions);
				if (dummy.getResultsTotalSize() == 0) {
					check = "true";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummy != null) {
				try {
					dummy.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("check", check);
		return result;
	}

	public static Map<String, Object> checkStockEventSubmitable(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		String check = "true";
		try {
			Object eventId = context.get("eventId");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId));
			iterator = delegator.find("StockEventAggregated", EntityCondition.makeCondition(conditions), null, null,
					null, null);
			GenericValue value = null;
			while ((value = iterator.next()) != null) {
				BigDecimal aggregated = value.getBigDecimal("quantityDifference").abs();
				Object productId = value.get("productId");
				conditions.clear();
				conditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId));
				conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));

				BigDecimal varianced = BigDecimal.ZERO;
				List<GenericValue> stockEventVarianced = delegator.findList("StockEventVarianceComparison",
						EntityCondition.makeCondition(conditions), null, null, null, false);
				for (GenericValue x : stockEventVarianced) {
					if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
						varianced = varianced.add((x.getBigDecimal("quantity").abs()));
					}
				}
				if (aggregated.compareTo(varianced) != 0) {
					check = "false";
					result.put("productId", productId);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (iterator != null) {
				try {
					iterator.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("check", check);
		return result;
	}

	public static Map<String, Object> freezeInventory(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		try {
			Object eventId = context.get("eventId");
			GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", eventId), false);
			Object facilityId = stockEvent.get("facilityId");
			List<EntityCondition> conds = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD");
			conds.add(cond1);
			iterator = delegator.find("Product", EntityCondition.makeCondition(conds), null, null, null, null);
			GenericValue product = null;
			while ((product = iterator.next()) != null) {
				Object productId = product.get("productId");
				BigDecimal quantity = BigDecimal.ZERO;
				Object unitCost = null;
				Object purCost = null;
	
				List<GenericValue> inventory = delegator.findList("ProductFacilityAndAverageCost",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "facilityId", facilityId)),
						null, null, null, false);
				if (UtilValidate.isNotEmpty(inventory)) {
					for (GenericValue x : inventory) {
						if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
							quantity = x.getBigDecimal("quantity");
							unitCost = x.getBigDecimal("unitCost");
							purCost = x.getBigDecimal("purCost");
						}
					}
				}
				delegator.create("InventoryFreezed", UtilMisc.toMap("eventId", eventId, "productId", productId,
							"quantity", quantity, "unitPrice", unitCost, "purCost", purCost));
			}
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				try {
					iterator.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static Map<String, Object> getQuantityNonVariance(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		BigDecimal quantity = BigDecimal.ZERO;
		try {
			Object eventId = context.get("eventId");
			Object productId = context.get("productId");
			List<EntityCondition> conditions = FastList.newInstance();

			BigDecimal aggregated = BigDecimal.ZERO;

			conditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId));

			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			List<GenericValue> stockEventAggregated = delegator.findList("StockEventAggregated",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : stockEventAggregated) {
				aggregated = aggregated.add((x.getBigDecimal("quantityDifference").abs()));
			}

			conditions.clear();
			conditions.add(EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId));
			conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.EQUALS, productId));
			BigDecimal varianced = BigDecimal.ZERO;
			List<GenericValue> stockEventVarianced = delegator.findList("StockEventVarianceComparison",
					EntityCondition.makeCondition(conditions), null, null, null, false);
			for (GenericValue x : stockEventVarianced) {
				if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
					varianced = varianced.add((x.getBigDecimal("quantity").abs()));
				}
			}
			quantity = aggregated.subtract(varianced);
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.put("quantity", quantity);
		return result;
	}

	public static Map<String, Object> fixInventoryFreezed(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> stockEvents = delegator.findList("StockEvent", null, null, null, null, false);
			List<EntityCondition> conds = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD");
			conds.add(cond1);
			
			for (GenericValue stockEvent : stockEvents) {
				EntityListIterator iterator = null;
				try {
					Object eventId = stockEvent.get("eventId");
					iterator = delegator.find("Product", EntityCondition.makeCondition(conds), null, null, null, null);
					GenericValue product = null;
					while ((product = iterator.next()) != null) {
						Object productId = product.get("productId");
						GenericValue inventoryFreezed = delegator.findOne("InventoryFreezed",
								UtilMisc.toMap("eventId", eventId, "productId", productId), false);
						if (UtilValidate.isEmpty(inventoryFreezed)) {
							delegator.create("InventoryFreezed", UtilMisc.toMap("eventId", eventId, "productId",
									productId, "quantity", BigDecimal.ZERO));
						}
					}
					result.clear();
				} catch (Exception e) {
					throw e;
				} finally {
					if (iterator != null) {
						try {
							iterator.close();
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> reFreezeInventory(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator iterator = null;
		try {
			Object eventId = context.get("eventId");
			GenericValue stockEvent = delegator.findOne("StockEvent", UtilMisc.toMap("eventId", eventId), false);
			Object facilityId = stockEvent.get("facilityId");
			delegator.removeByCondition("InventoryFreezed",
					EntityCondition.makeCondition("eventId", EntityJoinOperator.EQUALS, eventId));
			
			List<EntityCondition> conds = FastList.newInstance();
			EntityCondition cond1 = EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "FINISHED_GOOD");
			conds.add(cond1);
			
			iterator = delegator.find("Product", EntityCondition.makeCondition(conds), null, null, null, null);
			GenericValue product = null;
			while ((product = iterator.next()) != null) {
				Object productId = product.get("productId");
				BigDecimal quantity = BigDecimal.ZERO;
				Object unitCost = null;
				Object purCost = null;
	
				List<GenericValue> inventory = delegator.findList("ProductFacilityAndAverageCost",
						EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "facilityId", facilityId)),
						null, null, null, false);
				if (UtilValidate.isNotEmpty(inventory)) {
					for (GenericValue x : inventory) {
						if (UtilValidate.isNotEmpty(x.getBigDecimal("quantity"))) {
							quantity = x.getBigDecimal("quantity");
							unitCost = x.getBigDecimal("unitCost");
							purCost = x.getBigDecimal("purCost");
						}
					}
				}
				delegator.create("InventoryFreezed", UtilMisc.toMap("eventId", eventId, "productId", productId,
							"quantity", quantity, "unitPrice", unitCost, "purCost", purCost));
			}
			result.clear();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				try {
					iterator.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
	
	public static Map<String, Object> updateInventoryDateByStockEvent(DispatchContext ctx, Map<String, Object> context) {
		// update expireDate, manufactureDate, lot
		String eventId = null;
		if (UtilValidate.isNotEmpty(context.get("eventId"))) {
			eventId = (String) context.get("eventId");
		}
		Delegator delegator = ctx.getDelegator();
		GenericValue objStockEvent = null;
		try {
			objStockEvent = delegator.findOne("StockEvent", false, UtilMisc.toMap("eventId", eventId));
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findOne StockEvent: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (UtilValidate.isEmpty(objStockEvent)) {
			String errMsg = "OLBIUS: Fatal error when updateInventoryDateByStockEvent StockEvent not found!";
			Debug.logError(errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		String facilityId = objStockEvent.getString("facilityId");
		
		List<EntityCondition> conds = FastList.newInstance();
		conds.add(EntityCondition.makeCondition("eventId", eventId));
		List<GenericValue> listStockEventItem = FastList.newInstance();
		try {
			listStockEventItem = delegator.findList("StockEventItemGroupByDate", EntityCondition.makeCondition(conds), null, null, null,
					false);
		} catch (GenericEntityException e) {
			String errMsg = "OLBIUS: Fatal error when findList StockEventItemGroupByDate: " + e.toString();
			Debug.logError(e, errMsg, module);
			return ServiceUtil.returnError(errMsg);
		}
		if (!listStockEventItem.isEmpty()){
			List<Map<String, Object>> listEventItemRemains = FastList.newInstance();
			List<String> inventoryItemIds = FastList.newInstance();
			for (GenericValue item : listStockEventItem) {
				String productId = item.getString("productId");
				// check inven du dieu kien => khong thay doi
				String lot = item.getString("lotRecheck");
				Timestamp expireDate = item.getTimestamp("expireDateRecheck");
				Timestamp manufactureDate = item.getTimestamp("manufactureDateRecheck");
				BigDecimal quantityRecheck = item.getBigDecimal("quantityRecheck");
				Map<String, Object> attributes = FastMap.newInstance();
				attributes.put("facilityId", facilityId);
				attributes.put("productId", productId);
				if (UtilValidate.isNotEmpty(expireDate)) {
					attributes.put("expireDate", expireDate);
				}
				if (UtilValidate.isNotEmpty(manufactureDate)) {
					attributes.put("datetimeManufactured", manufactureDate);
				}
				if (UtilValidate.isNotEmpty(lot)) {
					attributes.put("lotId", lot);
				}
				List<Map<String, Object>> listInvs = FastList.newInstance();
				try {
					listInvs = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantityRecheck);
				} catch (GenericEntityException e) {
					String errMsg = "OLBIUS: Fatal error when InventoryUtil getInventoryItemsForQuantity: " + e.toString();
					Debug.logError(e, errMsg, module);
					return ServiceUtil.returnError(errMsg);
				}
				BigDecimal qoh = BigDecimal.ZERO;
				if (!listInvs.isEmpty()){
					for (Map<String, Object> inv : listInvs) {
						String inventoryItemId = (String)inv.get("inventoryItemId");
						BigDecimal quantity = (BigDecimal)inv.get("quantity");
						qoh = qoh.add(quantity);
						
						GenericValue objInventoryItem = null;
						try {
							objInventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne InventoryItem: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						BigDecimal qoh2 = objInventoryItem.getBigDecimal("quantityOnHandTotal");
						if (ProductUtil.isWeightProduct(delegator, productId)){
							qoh2 = objInventoryItem.getBigDecimal("amountOnHandTotal");
						}
						if (qoh2.compareTo(quantity) <= 0){
							inventoryItemIds.add(inventoryItemId);
						} else {
							// tach inventory thanh inventory moi
							try {
								String newInvId = com.olbius.baselogistics.util.InventoryUtil.splitInventoryItemByQuantity(delegator, inventoryItemId, quantity);
								if (UtilValidate.isNotEmpty(newInvId)) {
									inventoryItemIds.add(newInvId);
								}
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findOne InventoryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
						}
					}
				}
				if (quantityRecheck.compareTo(qoh) > 0){
					// khong du inven phu hop 
					// lay inven khac de bu vao
					BigDecimal remain = quantityRecheck.subtract(qoh);
					Map<String, Object> map = FastMap.newInstance();
					map.put("expireDate", expireDate);
					map.put("manufactureDate", manufactureDate);
					map.put("lot", lot);
					map.put("productId", productId);
					map.put("quantityRecheck", remain);
					listEventItemRemains.add(map);
				}
			}
			if (!listEventItemRemains.isEmpty()){
				for (Map<String,Object> map : listEventItemRemains) {
					String productId = (String)map.get("productId");
					String lot = (String)map.get("lot");
					Timestamp expireDate = (Timestamp)map.get("expireDate");
					Timestamp manufactureDate = (Timestamp)map.get("manufactureDate");
					BigDecimal quantityRecheck = (BigDecimal)map.get("quantityRecheck");
					
					List<Map<String, Object>> listInvs = FastList.newInstance();
					Map<String, Object> attributes = FastMap.newInstance();
					attributes.put("facilityId", facilityId);
					attributes.put("productId", productId);
					try {
						listInvs = InventoryUtil.getInventoryItemsForQuantity(delegator, attributes, quantityRecheck);
					} catch (GenericEntityException e) {
						String errMsg = "OLBIUS: Fatal error when InventoryUtil getInventoryItemsForQuantity: " + e.toString();
						Debug.logError(e, errMsg, module);
						return ServiceUtil.returnError(errMsg);
					}
					if (UtilValidate.isNotEmpty(lot)) {
						GenericValue objLot = null;
						try {
							objLot = delegator.findOne("Lot", false,
									UtilMisc.toMap("lotId", lot));
						} catch (GenericEntityException e) {
							String errMsg = "OLBIUS: Fatal error when findOne Lot: " + e.toString();
							Debug.logError(e, errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
						if (UtilValidate.isEmpty(objLot)) {
							GenericValue lotNew = delegator.makeValue("Lot");
							lotNew.put("lotId", lot);
							lotNew.put("creationDate", UtilDateTime.nowTimestamp());
							try {
								delegator.create(lotNew);
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when Create Lot: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							} 
						}
					}
					if (!listInvs.isEmpty()){
						BigDecimal qoh = BigDecimal.ZERO;
						for (Map<String, Object> inv : listInvs) {
							String inventoryItemId = (String)inv.get("inventoryItemId");
							BigDecimal quantity = (BigDecimal)inv.get("quantity");
							qoh = qoh.add(quantity);
							
							GenericValue objInventoryItem = null;
							try {
								objInventoryItem = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", inventoryItemId));
							} catch (GenericEntityException e) {
								String errMsg = "OLBIUS: Fatal error when findOne InventoryItem: " + e.toString();
								Debug.logError(e, errMsg, module);
								return ServiceUtil.returnError(errMsg);
							}
							BigDecimal qoh2 = objInventoryItem.getBigDecimal("quantityOnHandTotal");
							if (ProductUtil.isWeightProduct(delegator, productId)){
								qoh2 = objInventoryItem.getBigDecimal("amountOnHandTotal");
							}
							if (qoh2.compareTo(quantity) <= 0){
								// update
								objInventoryItem.set("expireDate", expireDate);
								objInventoryItem.set("datetimeManufactured", manufactureDate);
								objInventoryItem.set("lotId", lot);
								try {
									delegator.store(objInventoryItem);
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when store InventoryItem: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
								inventoryItemIds.add(inventoryItemId);
							} else {
								// tach inventory thanh inventory moi
								try {
									String newInvId = com.olbius.baselogistics.util.InventoryUtil.splitInventoryItemByQuantity(delegator, inventoryItemId, quantity);
									if (UtilValidate.isNotEmpty(newInvId)) {
										
										GenericValue objInventoryItemNew = null;
										try {
											objInventoryItemNew = delegator.findOne("InventoryItem", false, UtilMisc.toMap("inventoryItemId", newInvId));
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when findOne InventoryItem: " + e.toString();
											Debug.logError(e, errMsg, module);
											return ServiceUtil.returnError(errMsg);
										}
										objInventoryItemNew.set("expireDate", expireDate);
										objInventoryItemNew.set("datetimeManufactured", manufactureDate);
										objInventoryItemNew.set("lotId", lot);
										try {
											delegator.store(objInventoryItemNew);
										} catch (GenericEntityException e) {
											String errMsg = "OLBIUS: Fatal error when store InventoryItem: " + e.toString();
											Debug.logError(e, errMsg, module);
											return ServiceUtil.returnError(errMsg);
										}
										
										inventoryItemIds.add(newInvId);
									}
								} catch (GenericEntityException e) {
									String errMsg = "OLBIUS: Fatal error when findOne InventoryItem: " + e.toString();
									Debug.logError(e, errMsg, module);
									return ServiceUtil.returnError(errMsg);
								}
							}
						}
						if (quantityRecheck.compareTo(qoh) > 0){
							Locale locale = (Locale) context.get("locale");
							String errMsg = UtilProperties.getMessage(resourceError, "BLInventoryNotEnough", locale);
							Debug.logError(errMsg, module);
							return ServiceUtil.returnError(errMsg);
						}
					}
				}
			}
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("eventId", eventId);
		return result;
	}
}