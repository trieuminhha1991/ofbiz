package com.olbius.acc.report.liability.services;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

import com.olbius.acc.report.liability.query.LiabilityDetailGlOlapGrid;
import com.olbius.acc.report.liability.query.LiabilityOlapImpl;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.OlapInterface;
import com.olbius.bi.olap.grid.OlapGrid;

public class LiabilityServices{
	public static Map<String, Object> reportLiabilityGl(DispatchContext dctx, Map<String, ? extends Object> context){
		Delegator delegator = dctx.getDelegator();
		LiabilityOlapImpl grid = new LiabilityOlapImpl();
		OlapGrid gridResult = new OlapGrid(grid, grid.new LiabilityGrid());
		grid.setOlapResult(gridResult);
		
		Boolean init = (Boolean) context.get("init");		
        Date fromDate = (Date) context.get("fromDate");
        Date thruDate = (Date) context.get("thruDate");
        
        List<?> type = (List<?>) context.get("type[]");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));           
        Map<String, String> map = new HashMap<String, String>();
        
        for(Object s : type) {
        	String tmp = (String) s;
        	tmp = (String) context.get(tmp);
        	map.put((String) s, tmp);
        }        
        map.put("ORGANIZATION", organizationPartyId);
        int limit = 0;
        
        int offset = 0;
        
        grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

        grid.setFromDate(fromDate);
        grid.setThruDate(thruDate);
        
        if(init != null && (Boolean)init) {
        	 grid.putParameter(OlapInterface.INIT, init);
        }
        
        grid.putParameter(LiabilityOlapImpl.TYPE, map);
        
		Map<String, Object> result = grid.execute(context);
		
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
	public static Map<String, Object> reportLiabilityDetailGl(DispatchContext dctx, Map<String, ? extends Object> context){
		LiabilityDetailGlOlapGrid grid = new LiabilityDetailGlOlapGrid();
		Delegator delegator = dctx.getDelegator();
		String SUPPLIER = (String) context.get("SUPPLIER");
		String GL_ACCOUNT = (String) context.get("GL_ACCOUNT");
		String ORGANIZATION = (String) context.get("ORGANIZATION");
		Map<String, String> map = new HashMap<String, String>();
		map.put("SUPPLIER", SUPPLIER);
		map.put("GL_ACCOUNT", GL_ACCOUNT);
		map.put("ORGANIZATION", ORGANIZATION);
		
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		
		try {
			grid.reportLiabilityDetail(map);
		} catch (GenericDataSourceException e) {
			e.printStackTrace();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				grid.close();
			} catch (GenericDataSourceException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("data", grid.getData());
		result.put("datafields", grid.getDataFields());
		result.put("id", grid.getId());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
	
}
