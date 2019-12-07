package com.olbius.basesales.report;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ResultProductStore2{
	private final OlbiusQuery query;
	
	private SQLProcessor processor;
	
	public ResultProductStore2(SQLProcessor processor) {
		this.processor = processor;
		query = (OlbiusQuery) new OlbiusQuery(processor).select("DISTINCT(product_store_id), store_name")
				.from("product_store_dimension").where(Condition.make("product_store_id is NOT NULL")).orderBy("product_store_id");
	}
	
	public List<Map<String, String>> getListResultStore2() {
//		List<String> list = new ArrayList<String>();
		List<Map<String, String>> list2 = FastList.newInstance();
		
		try {
			ResultSet resultSet = query.getResultSet();
			while(resultSet.next()) {
				 Map<String, String> tmp = FastMap.newInstance();
				 tmp.put("product_store_id", resultSet.getString("product_store_id"));
				 tmp.put("store_name", resultSet.getString("store_name"));
				 list2.add(tmp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(processor != null) {
				 try {
					processor.close();
				} catch (GenericDataSourceException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list2;
	}
	
	public static Map<String, Object> getListResultStore2(DispatchContext dctx, Map<String, Object> context){	
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String organization = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		ResultProductStore2 type = new ResultProductStore2(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listResultStore", type.getListResultStore2());
		return result;
	}

}
