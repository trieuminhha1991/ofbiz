package com.olbius.basepo.report.facility;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ModelService;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basepo.report.PurchaseOrderReportImp;
import com.olbius.basepo.report.facility.ImportedFacilityReportImpl.ResultOutPOReport;
import com.olbius.bi.olap.grid.OlapGrid;

public class ImportedFacilityReportServices {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListImportedGoods(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericServiceException {
		Delegator delegator = ctx.getDelegator();
		String dateType = (String) context.get("dateType");
		String filterSaleOrder = (String) context.get("filterSaleOrder");
		List<Object> productId = (List<Object>) context.get("productId[]");
		List<Object> statusId = (List<Object>) context.get("statusId[]");
		List<Object> categoryId = (List<Object>) context.get("categoryId[]");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String partyIdByFacility = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
		Locale locale = (Locale) context.get("locale");
		ImportedFacilityReportImpl poReport = new ImportedFacilityReportImpl();
		ResultOutPOReport resultOutPOReport = poReport.new ResultOutPOReport();
		poReport.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));
		OlapGrid gird = new OlapGrid(poReport, resultOutPOReport);
		poReport.setOlapResult(gird);

		if (dateType == null) {
			dateType = "DAY";
		}
		Date fromDate = (Date) context.get("fromDate");
		Date thruDate = (Date) context.get("thruDate");

		poReport.SQLProcessor(new SQLProcessor(((GenericDelegator) delegator).getGroupHelperInfo("org.ofbiz.olap")));

		poReport.setFromDate(fromDate);
		poReport.setThruDate(thruDate);

		poReport.putParameter(PurchaseOrderReportImp.DATE_TYPE, dateType);
		poReport.putParameter(PurchaseOrderReportImp.PRODUCT_ID, productId);
		poReport.putParameter(PurchaseOrderReportImp.STATUS_ID, statusId);
		poReport.putParameter(PurchaseOrderReportImp.CATEGORY_ID, categoryId);
		poReport.putParameter(PurchaseOrderReportImp.USER_LOGIN_ID, partyIdByFacility);
		poReport.putParameter(PurchaseOrderReportImp.FILTER_SALE_ORDER, filterSaleOrder);
		poReport.putParameter(PurchaseOrderReportImp.LOCALE, locale);
		Map<String, Object> result = poReport.execute(context);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}
}
