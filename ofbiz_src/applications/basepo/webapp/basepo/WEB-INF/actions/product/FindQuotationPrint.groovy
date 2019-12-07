import java.util.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.service.ServiceUtil;
import java.util.Map;
import java.util.Calendar;
import javolution.util.FastList;
import javolution.util.FastMap;


String salesMethodChannelEnumId = parameters.salesMethodChannelEnumId;
String productStoreId = parameters.productStoreId;
String partyId = parameters.partyId;

	List<EntityCondition> listAllConditions = FastList.newInstance();
	List<String> listSortFields = FastList.newInstance();
	EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
	Map<String, String[]> parametersLocal = FastMap.newInstance();
	if (salesMethodChannelEnumId != null) {
		String[] salesMethodChannelEnumIds = [salesMethodChannelEnumId];
		parametersLocal.put("salesMethodChannelEnumId", salesMethodChannelEnumIds);
	}
	if (productStoreId != null) {
		String[] productStoreIds = [productStoreId];
		parametersLocal.put("productStoreId", productStoreIds);
	}
	if (partyId != null) {
		String[] partyIds = [partyId];
		parametersLocal.put("partyId", partyIds);
	}
	String[] pagenum = ["0"];
	String[] pagesizes = ["0"];
	parametersLocal.put("pagenum", pagenum);
	parametersLocal.put("pagesize", pagesizes);
	
	Map<String, Object> findCtx = FastMap.newInstance();
	findCtx.put("listAllConditions", listAllConditions);
	findCtx.put("listSortFields", listSortFields);
	findCtx.put("opts", opts);
	findCtx.put("parameters", parametersLocal);
	findCtx.put("userLogin", userLogin);
	findCtx.put("locale", locale);
	
	Map<String, Object> resultService =  dispatcher.runSync("JQFindProductPriceQuotes", findCtx);
	List<Map<String, Object>> listData = (List<Map<String, Object>>) resultService.get("listIterator");
	List<GenericValue> uomList = delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"), null, false);

	String productStoreName = productStoreId;
	String currencyUomId = null;
	Calendar cal = Calendar.getInstance();
	List<Map<String, Object>> listQuotationData = FastList.newInstance();
	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
	if (listData) {
		GenericValue productStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId", productStoreId), false);
		if (productStore != null) {
			productStoreName = productStore.getString("storeName");
			currencyUomId = productStore.getString("defaultCurrencyUomId");
		}
		
		for (Map<String, Object> mapItem in listData) {
			Map<String, Object> itemData = mapItem;
			
			String quantityUomId = (String) mapItem.get("uomId");
			String quantityUomDesc = quantityUomId;
			GenericValue quantityUom = EntityUtil.getFirst(EntityUtil.filterByAnd(uomList, UtilMisc.toMap("uomId", quantityUomId)));
			if (quantityUom != null) quantityUomDesc = (String) quantityUom.get("description", locale);
			itemData.put("quantityUomDesc", quantityUomDesc);
			
			listQuotationData.add(itemData);
		}
		cal.setTimeInMillis(nowTimestamp.getTime());
	}
	
context.customerId = partyId;
context.productStoreName = productStoreName;
context.currencyUomId = currencyUomId;
context.listQuotationData = listQuotationData;
context.fromDateDateTime = cal;
