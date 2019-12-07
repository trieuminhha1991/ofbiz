package com.olbius.acc.report.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import com.olbius.webapp.event.ExportExcelEvents;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

public class ExportReportIncomeStatement extends ExportExcelAbstract {
	private final String RESOURCE = "BaseAccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String reportType = ExportExcelUtil.getParameter(parameters, "reportType");
		String fileName = "";
		if (reportType.equals("general")) {
			fileName = "BAO_CAO_DOANH_THU_TONG_HOP_";
			setHeaderName(UtilProperties.getMessage(RESOURCE, "BACCIncomeStatement", locale));
			setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCIncomeStatement", locale));
		} else if (reportType.equals("product")) {
			fileName = "BAO_CAO_DOANH_THU_THEO_SP_";
			setHeaderName(UtilProperties.getMessage(RESOURCE, "BACCProductIncomeStatement", locale));
			setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCProductIncomeStatement", locale));
		} else if (reportType.equals("party")) {
			fileName = "BAO_CAO_DOANH_THU_THEO_KH_";
			setHeaderName(UtilProperties.getMessage(RESOURCE, "BACCCustomerIncomeStatement", locale));
			setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCCustomerIncomeStatement", locale));
		} else if (reportType.equals("category")) {
			fileName = "BAO_CAO_DOANH_THU_THEO_DM_";
			setHeaderName(UtilProperties.getMessage(RESOURCE, "BACCCategoryIncomeStatement", locale));
			setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCCategoryIncomeStatement", locale));
		} else if (reportType.equals("productStore")) {
			fileName = "BAO_CAO_DOANH_THU_THEO_CH_";
			setHeaderName(UtilProperties.getMessage(RESOURCE, "BACCProductStoreIncomeStatement", locale));
			setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCProductStoreIncomeStatement", locale));
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);
		setRunServiceName("olbiusReportIncomeStatement");
		setModuleExport("LOG");
		setExportType(ExportExcelEvents.EXPORT_TYPE_OLAP);
		setSplitSheet(true);
		
		setRunParameters(parameters);
        
		// add all columns
		addColumn(8, UtilProperties.getMessage("BaseSalesUiLabels", "BSNo2", locale), null);
		if (reportType.equals("general")) {
			addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCProductId", locale), "productCode");
			addColumn(30, UtilProperties.getMessage(RESOURCE, "BACCProductName", locale), "productName");
			addColumn(22, UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryName", locale), "categoryId");
			addColumn(15, UtilProperties.getMessage(RESOURCE, "BACCCustomerId", locale), "partyCode");
			addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCCustomerName", locale), "fullName");
			addColumn(15, UtilProperties.getMessage(RESOURCE, "BACCProductStoreId", locale), "productStoreId");
			addColumn(30, UtilProperties.getMessage(RESOURCE, "BACCProductStoreDemension", locale), "productStoreName");

		} else if (reportType.equals("product")) {
			addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCProductId", locale), "productCode");
			addColumn(30, UtilProperties.getMessage(RESOURCE, "BACCProductName", locale), "productName");
		} else if (reportType.equals("party")) {
			addColumn(15, UtilProperties.getMessage(RESOURCE, "BACCCustomerId", locale), "partyCode");
			addColumn(20, UtilProperties.getMessage(RESOURCE, "BACCCustomerName", locale), "fullName");
		} else if (reportType.equals("category")) {
			addColumn(22, UtilProperties.getMessage("BaseSalesUiLabels", "BSCategoryName", locale), "categoryName");
		} else if (reportType.equals("productStore")) {
			addColumn(15, UtilProperties.getMessage(RESOURCE, "BACCProductStoreId", locale), "productStoreId");
			addColumn(30, UtilProperties.getMessage(RESOURCE, "BACCProductStoreDemension", locale), "productStoreName");
		}
		
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCTransactionTime", locale), "transTime");
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCSaleIncome", locale), "saleIncome", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCSaleDiscount", locale), "saleDiscount", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCPromotion", locale), "promotion", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCSaleReturn", locale), "saleReturn", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCNetRevenue", locale), "netRevenue", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCCOGS", locale), "cogs", ExportExcelStyle.STYLE_CELL_NUMBER);
		addColumn(18, UtilProperties.getMessage(RESOURCE, "BACCGrossProfit", locale), "grossProfit", ExportExcelStyle.STYLE_CELL_NUMBER);
	}
}