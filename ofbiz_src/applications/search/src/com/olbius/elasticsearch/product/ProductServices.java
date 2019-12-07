package com.olbius.elasticsearch.product;

import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.elasticsearch.ElasticIndex;
import com.olbius.elasticsearch.ElasticIndexFactory;
import com.olbius.elasticsearch.SearchClientFactory;

import io.searchbox.client.JestClient;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;

public class ProductServices {
	public static Map<String, Object> indexProducts(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		ElasticIndex index = ElasticIndexFactory.getInstance();
		EntityListIterator iterator = null;
		JestClient jestClient = SearchClientFactory.getInstance().getJestClient();
		try {
			jestClient.execute(new CreateIndex.Builder("product").build());
			
			PutMapping putMapping = new PutMapping.Builder(
			        "product",
			        "venues",
			        "{ \"venues\" : { \"properties\" :"
			        		+ "{ \"location\" : {\"type\" : \"geo_point\"},"
			        		+ "\"brandName\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"},"
			        		+ "\"orderDate\" : {\"type\" : \"long\", \"index\" : \"not_analyzed\"},"
			        		+ "\"partyId\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"},"
			        		+ "\"primaryProductCategoryId\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"},"
			        		+ "\"productCode\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"},"
			        		+ "\"productId\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"},"
			        		+ "\"productName\" : {\"type\" : \"string\", \"index\" : \"not_analyzed\"}"
			        + "} } }"
			).build();
			jestClient.execute(putMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			iterator = delegator.find("PartyOutletOrdered",
					EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "ITEM_COMPLETED"), null, null,
					null, null);
			index.setIndex(delegator, "product");
			index.setType("venues");
			GenericValue value = null;
			
			while ((value = iterator.next()) != null) {
				ProductIndex pIndex = new ProductIndex();
				pIndex.setId(value.getString("productId") + value.getString("partyId"));
				pIndex.setProductId(value.getString("productId"));
				pIndex.setProductCode(value.getString("productCode"));
				pIndex.setProductName(value.getString("productName"));
				pIndex.setBrandName(value.getString("brandName"));
				pIndex.setPrimaryProductCategoryId(value.getString("primaryProductCategoryId"));
				pIndex.setPartyId(value.getString("partyId"));
				pIndex.setOrderDate(value.getTimestamp("orderDate").getTime());
				pIndex.getLocation().put("lat", value.getString("latitude"));
				pIndex.getLocation().put("lon", value.getString("longitude"));
				index.indexDatas(pIndex);
//				jestClient.execute(new Delete.Builder(value.getString("productId") + value.getString("partyId"))
//						.index("product").type("venues").build());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		} finally {
			if (iterator != null) {
				iterator.close();
			}
		}
		index.commit();
		return result;
	}
}
