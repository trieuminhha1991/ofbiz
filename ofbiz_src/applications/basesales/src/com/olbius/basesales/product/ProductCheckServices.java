package com.olbius.basesales.product;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.datasource.GenericHelperInfo;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ProductCheckServices {
	public static final String module = ProductCheckServices.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";

    public static int getResultsTotalSize(GenericHelperInfo helperInfo, String query) throws GenericEntityException {
        Integer resultSize = 0;
        
		SQLProcessor sqlP = new SQLProcessor(helperInfo);
    	StringBuilder sqlBuffer = new StringBuilder("SELECT COUNT(1) FROM (");
    	sqlBuffer.append(query);
    	sqlBuffer.append(") AS TMP_TABLE");
		sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, 1, 1);
        try {
            sqlP.executeQuery();
            long count = 0;
            ResultSet resultSet = sqlP.getResultSet();
            if (resultSet.next()) {
                count = resultSet.getLong(1);
            }
            resultSize = (int) count;
        } catch (SQLException e) {
            throw new GenericDataSourceException("Error getting count value", e);
        } finally {
            sqlP.close();
        }
		
        return resultSize;
    }
    
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqCheckProductError(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listIterator = new ArrayList<Map<String, Object>>();
		//List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		//List<String> listSortFields = (List<String>) context.get("listSortFields");
		//EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		try {
			String viewIndexStr = (String) parameters.get("pagenum")[0];
	    	String viewSizeStr = (String) parameters.get("pagesize")[0];
	    	int viewIndex = viewIndexStr == null ? 0 : new Integer(viewIndexStr);
	    	int viewSize = viewSizeStr == null ? 0 : new Integer(viewSizeStr);
			
			GenericHelperInfo helperInfo = ((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz");
			SQLProcessor sqlP = new SQLProcessor(helperInfo);
	    	try {
				StringBuilder sqlBuffer = new StringBuilder("");
				
				String limitSql = null;
				boolean flagSearch = true;
				String filterType = SalesUtil.getParameter(parameters, "filterType");
				if ("MISS_TAX_CATEGORY".equals(filterType)) {
					// co trong danh muc ban, ko co trong danh muc thue
					sqlBuffer.append("SELECT * ");
					sqlBuffer.append("FROM (SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) > 0) as TMP ");
					sqlBuffer.append("WHERE TMP.product_id IN (SELECT PROD.product_id ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) < ?) ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("CATALOG_CATEGORY");
					sqlP.setValue("TAX_CATEGORY");
					sqlP.setValue(1);
				} else if ("MISS_REF_CATEGORY".equals(filterType)) {
					// co trong danh muc ban, ko co trong danh muc ref
					sqlBuffer.append("SELECT * ");
					sqlBuffer.append("FROM (SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) > 0) as TMP ");
					sqlBuffer.append("WHERE TMP.product_id IN (SELECT PROD.product_id ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) < ?) ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("CATALOG_CATEGORY");
					sqlP.setValue("MMS_CATAGORY_REF");
					sqlP.setValue(1);
				} else if ("MISS_UPC".equals(filterType)) {
					// co trong danh muc ban, ko co UPC
					sqlBuffer.append("SELECT * ");
					sqlBuffer.append("FROM (SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) > 0) as TMP ");
					sqlBuffer.append("WHERE TMP.product_id IN (SELECT PROD.product_id ");
					sqlBuffer.append("FROM product as PROD LEFT JOIN good_identification as GI ON PROD.product_id = GI.product_id ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(GI.id_value) < ?) ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("CATALOG_CATEGORY");
					sqlP.setValue(1);
				} else if ("MISS_UPC_PRIMARY".equals(filterType)) {
					// co trong danh muc ban, ko co UPC chinh
					sqlBuffer.append("SELECT * ");
					sqlBuffer.append("FROM (SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) > 0) as TMP ");
					sqlBuffer.append("WHERE TMP.product_id IN (SELECT product_id ");
					sqlBuffer.append("FROM good_identification group by product_id having count(iupprm) = ?) ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("CATALOG_CATEGORY");
					sqlP.setValue(0);
				} else if ("MISS_SALES_PRICE".equals(filterType)) {
					// co trong danh muc ban, ko co gia ban
					sqlBuffer.append("SELECT * ");
					sqlBuffer.append("FROM (SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) > 0) as TMP ");
					sqlBuffer.append("WHERE TMP.product_id IN (SELECT PROD.product_id ");
					sqlBuffer.append("FROM product as PROD LEFT JOIN product_price as PR ON PROD.product_id = PR.product_id ");
					sqlBuffer.append("where PR.thru_date is null ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PR.price) < ?) ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("CATALOG_CATEGORY");
					sqlP.setValue(1);
				} else if ("MISS_PURCHASE_PRICE".equals(filterType)) {
					// co trong danh muc ban, ko co gia mua
					sqlBuffer.append("SELECT * ");
					sqlBuffer.append("FROM (SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) > 0) as TMP ");
					sqlBuffer.append("WHERE TMP.product_id IN (SELECT PROD.product_id ");
					sqlBuffer.append("FROM product as PROD LEFT JOIN supplier_product as SP ON PROD.product_id = SP.product_id ");
					sqlBuffer.append("where SP.available_thru_date is null ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(SP.last_price) < ?) ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("CATALOG_CATEGORY");
					sqlP.setValue(1);
				} else if ("ALL_MISS_TAX_CATEGORY".equals(filterType)) {
					// trong tat ca san pham, ko co trong danh muc thue
					sqlBuffer.append("SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) < ? ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("TAX_CATEGORY");
					sqlP.setValue(1);
				} else if ("ALL_MISS_REF_CATEGORY".equals(filterType)) {
					// trong tat ca san pham, ko co trong danh muc ref
					sqlBuffer.append("SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD ");
					sqlBuffer.append("LEFT JOIN product_category_member as PCM ON PROD.product_id = PCM.product_id and PCM.thru_date is null ");
					sqlBuffer.append("LEFT JOIN product_category as PC ON PCM.product_category_id = PC.product_category_id AND PC.product_category_type_id = ? ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PC.product_category_id) < ? ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue("MMS_CATAGORY_REF");
					sqlP.setValue(1);
				} else if ("ALL_MISS_UPC".equals(filterType)) {
					// trong tat ca san pham, ko co UPC
					sqlBuffer.append("SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD LEFT JOIN good_identification as GI ON PROD.product_id = GI.product_id ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(GI.id_value) < ? ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue(1);
				} else if ("ALL_MISS_UPC_PRIMARY".equals(filterType)) {
					// trong tat ca san pham, ko co UPC chinh
					sqlBuffer.append("SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM good_identification as GI ");
					sqlBuffer.append("LEFT JOIN product as PROD ON GI.product_id = PROD.product_id ");
					sqlBuffer.append("GROUP BY PROD.product_id HAVING COUNT(GI.iupprm) = ? ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue(0);
				} else if ("ALL_MISS_SALES_PRICE".equals(filterType)) {
					// trong tat ca san pham, ko co gia ban
					sqlBuffer.append("SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD LEFT JOIN product_price as PR ON PROD.product_id = PR.product_id ");
					sqlBuffer.append("where PR.thru_date is null ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(PR.price) < ? ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue(1);
				} else if ("ALL_MISS_PURCHASE_PRICE".equals(filterType)) {
					// trong tat ca san pham, ko co gia mua
					sqlBuffer.append("SELECT PROD.product_id, PROD.product_code, PROD.product_name ");
					sqlBuffer.append("FROM product as PROD LEFT JOIN supplier_product as SP ON PROD.product_id = SP.product_id ");
					sqlBuffer.append("where SP.available_thru_date is null ");
					sqlBuffer.append("GROUP BY PROD.product_id ");
					sqlBuffer.append("HAVING COUNT(SP.last_price) < ? ");
					
					limitSql = "LIMIT " + viewSize + " OFFSET " + viewIndex;
					sqlBuffer.append(limitSql);
					
					sqlP.prepareStatement(sqlBuffer.toString()); //sqlP.prepareStatement(sqlBuffer.toString(), true, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					sqlP.setValue(1);
				} else {
					flagSearch = false;
				}
				
				if (flagSearch) {
					sqlP.executeQuery();
					
					ResultSet resultSet = sqlP.getResultSet();
					while (resultSet.next()) {
						Map<String, Object> itemMap = FastMap.newInstance();
						itemMap.put("productId", resultSet.getString("product_id"));
						itemMap.put("productCode", resultSet.getString("product_code"));
						itemMap.put("productName", resultSet.getString("product_name"));
						listIterator.add(itemMap);
					}
					
					String query = sqlP.getPreparedStatement().toString();
					if (limitSql != null) query = query.replace(limitSql, "");
					int totalRows = getResultsTotalSize(helperInfo, query);
					successResult.put("TotalRows", String.valueOf(totalRows));
				} else {
					successResult.put("TotalRows", String.valueOf(0));
				}
			} finally {
	            sqlP.close();
	        }
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqCheckProductError service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	
	public static Map<String, Object> checkProductErrorDetail(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Map<String, Object> results = FastMap.newInstance();
		try {
			String successMsg = UtilProperties.getMessage(resource, "BSEnoughData", locale);
			String errorMsg = UtilProperties.getMessage(resource, "BSMissData", locale);
			String bsEnough = UtilProperties.getMessage(resource, "BSEnough", locale);
			String bsMiss = UtilProperties.getMessage(resource, "BSMiss", locale);
			String productId = (String) context.get("productId");
			if (UtilValidate.isNotEmpty(productId)) {
				productId = productId.trim();
			}
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if (product == null) {
				GenericValue goodIdentification = EntityUtil.getFirst(delegator.findByAnd("GoodIdentification", UtilMisc.toMap("idValue", productId), null, false));
				if (goodIdentification != null) {
					productId = goodIdentification.getString("productId");
					product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					if (product == null) {
						results.put("stateId", "MISS_DATA");
						results.put("stateMsg", UtilProperties.getMessage(resource_error, "BSProductNotFound", locale));
						successResult.put("results", results);
						return successResult;
					}
				} else {
					results.put("stateId", "MISS_DATA");
					results.put("stateMsg", UtilProperties.getMessage(resource_error, "BSProductNotFound", locale));
					successResult.put("results", results);
					return successResult;
				}
			}
			
			List<EntityCondition> conds = FastList.newInstance();
			boolean enoughData = true;
			
			// BSMissPrimaryCategory
			String primaryCategoryState = null;
			String primaryCategoryId = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productCategoryId", product.get("primaryProductCategoryId")));
			conds.add(EntityCondition.makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<String> listPrimaryCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
			if (UtilValidate.isEmpty(listPrimaryCategoryIds)) {
				enoughData = false;
				primaryCategoryState = bsMiss;
			} else {
				primaryCategoryState = bsEnough;
				primaryCategoryId = listPrimaryCategoryIds.toString();
			}
			results.put("primaryCategoryState", primaryCategoryState);
			results.put("primaryCategoryId", primaryCategoryId);
			
			// BSMissTaxCategory
			String taxCategoryState = null;
			String taxCategoryIds = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productCategoryTypeId", "TAX_CATEGORY"));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<String> listTaxCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
			if (UtilValidate.isEmpty(listTaxCategoryIds)) {
				enoughData = false;
				taxCategoryState = bsMiss;
			} else {
				taxCategoryState = bsEnough;
				taxCategoryIds = listTaxCategoryIds.toString();
			}
			results.put("taxCategoryState", taxCategoryState);
			results.put("taxCategoryIds", taxCategoryIds);
			
			// BSMissMMSCategory
			String refCategoryState = null;
			String refCategoryIds = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("productCategoryTypeId", "MMS_CATAGORY_REF"));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<String> listRefCategoryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductCategoryAndMember", EntityCondition.makeCondition(conds), UtilMisc.toSet("productCategoryId"), null, null, false), "productCategoryId", true);
			if (UtilValidate.isEmpty(listRefCategoryIds)) {
				enoughData = false;
				refCategoryState = bsMiss;
			} else {
				refCategoryState = bsEnough;
				refCategoryIds = listRefCategoryIds.toString();
			}
			results.put("refCategoryState", refCategoryState);
			results.put("refCategoryIds", refCategoryIds);
			
			// BSMissUPC
			String upcState = null;
			String upcIds = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
			List<String> listUpcIds = EntityUtil.getFieldListFromEntityList(delegator.findList("GoodIdentification", EntityCondition.makeCondition(conds), UtilMisc.toSet("idValue"), null, null, false), "idValue", true);
			if (UtilValidate.isEmpty(listUpcIds)) {
				enoughData = false;
				upcState = bsMiss;
			} else {
				upcState = bsEnough;
				upcIds = listUpcIds.toString();
			}
			results.put("upcState", upcState);
			results.put("upcIds", upcIds);
			
			// BSMissUPCPrimary
			String upcPrimaryState = null;
			String upcPrimaryIds = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("goodIdentificationTypeId", "SKU"));
			conds.add(EntityCondition.makeCondition("iupprm", 1L));
			List<String> listUpcPrimaryIds = EntityUtil.getFieldListFromEntityList(delegator.findList("GoodIdentification", EntityCondition.makeCondition(conds), UtilMisc.toSet("idValue"), null, null, false), "idValue", true);
			if (UtilValidate.isEmpty(listUpcPrimaryIds)) {
				enoughData = false;
				upcPrimaryState = bsMiss;
			} else {
				upcPrimaryState = bsEnough;
				upcPrimaryIds = listUpcPrimaryIds.toString();
			}
			results.put("upcPrimaryState", upcPrimaryState);
			results.put("upcPrimaryIds", upcPrimaryIds);
			
			// BSMissSalesPrice
			String salesPriceState = null;
			String salesPriceIds = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("termUomId", product.get("quantityUomId")));
			conds.add(EntityUtil.getFilterByDateExpr());
			List<String> listSalesPriceIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductPrice", EntityCondition.makeCondition(conds), UtilMisc.toSet("price"), null, null, false), "price", true);
			if (UtilValidate.isEmpty(listSalesPriceIds)) {
				enoughData = false;
				salesPriceState = bsMiss;
			} else {
				salesPriceState = bsEnough;
				salesPriceIds = listSalesPriceIds.toString();
			}
			results.put("salesPriceState", salesPriceState);
			results.put("salesPriceIds", salesPriceIds);
			
			// BSMissPurchasePrice
			String purchPriceState = null;
			String purchPriceIds = null;
			conds.clear();
			conds.add(EntityCondition.makeCondition("productId", productId));
			conds.add(EntityCondition.makeCondition("quantityUomId", product.get("quantityUomId")));
			conds.add(EntityUtil.getFilterByDateExpr("availableFromDate", "availableThruDate"));
			List<String> listPurchPriceIds = EntityUtil.getFieldListFromEntityList(delegator.findList("SupplierProduct", EntityCondition.makeCondition(conds), UtilMisc.toSet("lastPrice"), null, null, false), "lastPrice", true);
			if (UtilValidate.isEmpty(listPurchPriceIds)) {
				enoughData = false;
				purchPriceState = bsMiss;
			} else {
				purchPriceState = bsEnough;
				purchPriceIds = listPurchPriceIds.toString();
			}
			results.put("purchPriceState", purchPriceState);
			results.put("purchPriceIds", purchPriceIds);
			
			// finish
			results.put("productId", product.get("productId"));
			results.put("productCode", product.get("productCode"));
			results.put("productName", product.get("productName"));
			if (enoughData) {
				results.put("stateId", "ENOUGH_DATA");
				results.put("stateMsg", successMsg);
			} else {
				results.put("stateId", "MISS_DATA");
				results.put("stateMsg", errorMsg);
			}
			
			/*
			results.put("stateId", "MISS_DATA"); // ENOUGH_DATA, MISS_DATA
			results.put("stateMsg", errorMsg);
			results.put("taxCategoryState", taxCategoryState);
			results.put("taxCategoryIds", taxCategoryIds);
			results.put("refCategoryState", refCategoryState);
			results.put("refCategoryIds", refCategoryIds);
			results.put("upcState", upcState);
			results.put("upcIds", upcIds);
			results.put("upcPrimaryState", upcPrimaryState);
			results.put("upcPrimaryIds", upcPrimaryIds);
			results.put("salesPriceState", salesPriceState);
			results.put("salesPriceIds", salesPriceIds);
			results.put("purchPriceState", purchPriceState);
			results.put("purchPriceIds", purchPriceIds);
			results.put("productId", product.get("productId"));
			results.put("productCode", product.get("productCode"));
			results.put("productName", product.get("productName"));
			 */
		} catch (Exception e) {
			String errMsg = "Fatal error calling checkProductErrorDetail service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("results", results);
		return successResult;
	}
	
}
