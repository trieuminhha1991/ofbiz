package com.olbius.basesales.export;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;

public class ExhibitionPromotionReportExcel extends ExportExcelAbstract{
	private final String RESOURCE_BSMTL = "BaseSalesMtlUiLabels";
	private final String RESOURCE_BS = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String fileName = "DANH_SACH_DANG_KY_TBTL";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_BS, "BSSpecialPromoReport", locale));
		setRunServiceName("olapExhibitionPromotionReport");
		setModuleExport("SALES");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_BS, "BSSpecialPromoReport", locale));
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String yearr = ExportExcelUtil.getParameter(parameters, "yearr");
		String monthh = ExportExcelUtil.getParameter(parameters, "monthh");
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.putAll(parameters);
		parametersCtx.put("yearr", new String[]{yearr});
		parametersCtx.put("monthh", new String[]{monthh});
		setRunParameters(parametersCtx);
        
		// add subtitle rows
		SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String dateTimeOut = formatOut.format(nowTimestamp);
		addSubTitle(UtilProperties.getMessage(RESOURCE_BS, "BSDateTime", locale), dateTimeOut);
        if (UtilValidate.isNotEmpty(userLogin)){
            addSubTitle(UtilProperties.getMessage(RESOURCE_BSMTL, "BSDistributorId", locale), (String)userLogin.get("userLoginId"));
        }
		//addSubTitle(UtilProperties.getMessage(RESOURCE, "BSNote", locale), UtilProperties.getMessage(RESOURCE, "BSThePriceInCludedTax", locale));
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_BS, "BSNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER );
		addColumn(14, UtilProperties.getMessage(RESOURCE_BS, "BSProductPromo", locale), "special_promo_id");
		addColumn(16, UtilProperties.getMessage(RESOURCE_BS, "BSPromoName", locale), "special_promo_name");
		addColumn(14, UtilProperties.getMessage(RESOURCE_BS, "BSCustomerId", locale), "customer_code");
		addColumn(20, UtilProperties.getMessage(RESOURCE_BS, "BSCustomerName", locale), "customer_name");
	}
	
	
}
