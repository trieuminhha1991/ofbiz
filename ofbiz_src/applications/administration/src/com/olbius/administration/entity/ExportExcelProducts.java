package com.olbius.administration.entity;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelProducts extends ExportExcelAbstract {
    private final String RESOURCE = "BaseSalesUiLabels";
    private final String RESOURCE_ = "BaseSalesMtlUiLabels";

    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        String fileName = "DANH_SACH_SP";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        setHeaderName(UtilProperties.getMessage(RESOURCE, "BSListProduct", locale));
        setRunServiceName("JQGetPOListProductSample");
        setModuleExport("SALES");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSListProduct", locale));
        setSplitSheet(false);
        setMaxRowInSheet(100);

        // get parameters content
        @SuppressWarnings({ "unchecked" })
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        //String productStoreId = ExportExcelUtil.getParameter(parameters, "productStoreId");
        // make parameters input
        //Map<String, String[]> parametersCtx = FastMap.newInstance();
        //parametersCtx.put("productStoreId", new String[]{productStoreId});
        setRunParameters(parameters);

        @SuppressWarnings("unchecked")
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
        setRunListSortFields(listSortFields);

        // add subtitle rows
        SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateTimeOut = formatOut.format(nowTimestamp);
        addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);

        // add all columns
        addColumn(8, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null);
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSProductId", locale), "productCode");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSProductName", locale) + " (*)", "productName");
        addColumn(24, UtilProperties.getMessage(RESOURCE, "BSProductPackingUomId", locale) + " (*)", "descriptionUom");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSListPrice", locale) + " (*)" + " " +
                UtilProperties.getMessage(RESOURCE_, "BSDecimalPoint", locale), "productListPrice");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSDefaultPrice", locale) + " (*)" + " " +
                UtilProperties.getMessage(RESOURCE_, "BSDecimalPoint", locale), "productDefaultPrice");
        addColumn(24, UtilProperties.getMessage(RESOURCE, "BSCurrencyUomId", locale) + " (*, ID)", "currencyUomId");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSAbbProductWeight", locale) + " " +
                UtilProperties.getMessage(RESOURCE_, "BSDecimalPoint", locale), "productWeight");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSAbbWeightAfterPacked", locale) + " " +
                UtilProperties.getMessage(RESOURCE_, "BSDecimalPoint", locale), "weight");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSAbbWeightUom", locale), "weightUomName");
    }
}
