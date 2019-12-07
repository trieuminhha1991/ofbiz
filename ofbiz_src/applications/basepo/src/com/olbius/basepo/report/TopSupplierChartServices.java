package com.olbius.basepo.report;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.utils.ErrorUtils;

import javolution.util.FastMap;

public class TopSupplierChartServices {

	public static final String MODULE = TopSupplierChartServices.class.getName();

	public static Map<String, Object> evaluateTop5SupColumnChart(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		// Get parameters
		Delegator delegator = ctx.getDelegator();
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");
		String topType = (String) context.get("topType");
		String productId = (String) context.get("productId");
		if (topType == null || topType.isEmpty()) {
			topType = TopSupplierChart._TOP_TYPE;
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String ownerPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

		// Get chart
		TopSupplierChart olap = new TopSupplierChart(delegator);
		olap.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		olap.setFromDate(fromDate);
		olap.setThruDate(thruDate);
		try {
			olap.top5SupplierChart(ownerPartyId, productId, topType);

		} catch (GenericDataSourceException e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		} catch (SQLException e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}

		Map<String, Object> result = FastMap.newInstance();
		result.put("xAxis", olap.getXAxis());
		result.put("yAxis", olap.getYAxis());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
