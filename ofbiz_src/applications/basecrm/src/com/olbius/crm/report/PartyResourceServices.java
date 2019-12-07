package com.olbius.crm.report;

import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.bi.olap.grid.OlapGrid;
import com.olbius.crm.report.PartyResourceImpl.PieOlapResultQuery;
import com.olbius.crm.report.PartyResourceImpl.PieResult;

public class PartyResourceServices {

	public static Map<String, Object> getDataResourceBirthDate(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		PartyResourceImpl grid = new PartyResourceImpl(delegator);

		String dataSourceId = (String) context.get("dataSourceId");
		String geoId = (String) context.get("geoId");

		grid.putParameter("dataSourceId", dataSourceId);
		grid.putParameter("geoId", geoId);

		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		PieOlapResultQuery query = grid.new PieOlapResultQuery();
		Locale locale = (Locale) context.get("locale");
		query.setLocale(locale);
		PieResult pieResult = grid.new PieResult(grid, query);
		grid.setOlapResult(pieResult);

		Map<String, Object> result = grid.execute(context);

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	public static Map<String, Object> getDateOfBirthReportGrid(DispatchContext ctx,
			Map<String, ? extends Object> context) throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		PartyResourceImpl grid = new PartyResourceImpl(delegator);

		PartyResourceImpl.DataResource test = grid.new DataResource();

		OlapGrid olapGrid = new OlapGrid(grid, test);

		grid.setOlapResult(olapGrid);

		String dataSourceId = (String) context.get("dataSourceId");
		String geoId = (String) context.get("geoId");
		grid.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		grid.putParameter("dataSourceId", dataSourceId);
		grid.putParameter("geoId", geoId);

		Map<String, Object> result = grid.execute(context);

		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

}
