package com.olbius.basesales.export;

import com.olbius.basesales.product.ProductWorker;
import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExportExcelSalesStatementDetail extends ExportExcelAbstract {
    private final String RESOURCE = "BaseSalesUiLabels";
    private final String RESOURCE_SALES_MTL = "BaseSalesMtlUiLabels";
    private final String RESOURCE_HR = "BaseHRDirectoryUiLabels";

    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        List<GenericValue> listProduct = FastList.newInstance();
        GenericValue salesStatement = null;

        setHeaderName(UtilProperties.getMessage(RESOURCE, "BSSalesStatementDetailTable", locale));
        setRunServiceName("JQListOrganizationUnitManager");
        setModuleExport("SALES");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSSalesStatementDetailTable", locale));
        setSplitSheet(false);
        setPrefixSheetName(UtilProperties.getMessage(RESOURCE, "BSheetSalesStatement", locale));

        // get parameters content
        @SuppressWarnings({"unchecked"})
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String salesStatementId = ExportExcelUtil.getParameter(parameters, "salesStatementId");
        String currentListProductIds = ExportExcelUtil.getParameter(parameters, "productIds");
        Map<String, String[]> parametersCtx = FastMap.newInstance();
        parametersCtx.put("salesStatementId", new String[]{salesStatementId});
        parametersCtx.put("productIds", new String[]{currentListProductIds});
        parametersCtx.put("pagesize", new String[]{"0"});
        setRunParameters(parametersCtx);

        @SuppressWarnings("unchecked")
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
        setRunListSortFields(listSortFields);

        String fileName = "CHI_TIET_SALES_STATEMENT" + "_" + salesStatementId;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);

        try {
            //parse data
            String regex = "[^\\[\\]\"\\s,]+"; //cut and take String not equals [ ] " , space
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(currentListProductIds);
            List<String> productIds = FastList.newInstance();
            while (matcher.find()) {
                productIds.add(matcher.group(0));
            }
            listProduct = delegator.findList("ProductAndUom",
                    EntityCondition.makeCondition(EntityCondition.makeCondition("isVirtual", "N"),
                            EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.IN, productIds)), null, UtilMisc.toList("productCode"), null, false);
            salesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", salesStatementId), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // add subtitle rows
        SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateTimeOut = formatOut.format(nowTimestamp);
        addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);
        addSubTitle(UtilProperties.getMessage(RESOURCE, "BSSalesStatementId", locale), salesStatementId);

        // add all columns
        addColumn(6, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
        addColumn(18, UtilProperties.getMessage(RESOURCE_HR, "OrgUnitName", locale), "partyIdFrom", ExportExcelStyle.STYLE_COLUMN_LABEL);
        if ("SALES_IN".equals(salesStatement.getString("salesStatementTypeId"))) {
            addColumn(13, UtilProperties.getMessage(RESOURCE_SALES_MTL, "BSDistributorId", locale), "partyCode", ExportExcelStyle.STYLE_COLUMN_LABEL);
        }else if ("SALES_OUT".equals(salesStatement.getString("salesStatementTypeId"))){
            addColumn(13, UtilProperties.getMessage(RESOURCE, "BSEmployeeId", locale), "partyCode", ExportExcelStyle.STYLE_COLUMN_LABEL);
        }
        addColumn(20, UtilProperties.getMessage(RESOURCE, "BSFullName", locale), "partyName", ExportExcelStyle.STYLE_COLUMN_LABEL);
        for (GenericValue item : listProduct) {
            addColumn(10, item.getString("productCode") + "(" + item.get("quantityUomDescription") + ")", "prodCode_" + item.getString("productId"));
        }

    }
}
