package com.olbius.basesales.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExportInventoryCustomerExcelGT extends ExportExcelAbstract {
    private final String RESOURCE = "BaseSalesUiLabels";
    private final String RESOURCE_SALES_MTL = "BaseSalesMtlUiLabels";
    private final String RESOURCE_HR = "BaseHRDirectoryUiLabels";
    private final String RESOURCE_LOG = "BaseLogisticsUiLabels";

    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        List<GenericValue> listProduct = FastList.newInstance();
        GenericValue salesStatement = null;

        setHeaderName(UtilProperties.getMessage(RESOURCE, "BSViewInventoryCustomerGT", locale));
        setRunServiceName("JQGetListInventoryCusAndProdGT");
        setModuleExport("SALES");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSViewInventoryCustomerGT", locale));
        setSplitSheet(false);
        setMaxRowInSheet(100);
        setPrefixSheetName(UtilProperties.getMessage(RESOURCE, "BSInventoryCusInfoSheet", locale));

        // get parameters content
        @SuppressWarnings({"unchecked"})
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String url = ExportExcelUtil.getParameter(parameters, "url");
        String[] lstParam = url.split("&");
        String listProductStr = null;
        String changeDateTypeIdStr = null;
        String agentChainMTIdStr = null;
        String fromDateStr = null;
        String thruDateStr = null;
        for (String aParam : lstParam) {
            if (aParam.contains("productIds=")){
                listProductStr = aParam.replace("productIds=", "");
            } else if (aParam.contains("changeDateTypeId=")) {
                changeDateTypeIdStr = aParam.replace("changeDateTypeId=", "");
            } else if (aParam.contains("agentChainMTId=")) {
                agentChainMTIdStr = aParam.replace("agentChainMTId=", "");
            } else if (aParam.contains("fromDate=")) {
                fromDateStr = aParam.replace("fromDate=", "");
            } else if (aParam.contains("thruDate=")) {
                thruDateStr = aParam.replace("thruDate=", "");
            }
        }
        Map<String, String[]> parametersCtx = FastMap.newInstance();
        parametersCtx.put("productIds", new String[]{listProductStr});
        parametersCtx.put("agentChainMTId", new String[]{agentChainMTIdStr});
        parametersCtx.put("changeDateTypeId", new String[]{changeDateTypeIdStr});
        parametersCtx.put("fromDate", new String[]{fromDateStr});
        parametersCtx.put("thruDate", new String[]{thruDateStr});
        parametersCtx.put("pagesize", new String[]{"10"});
        setRunParameters(parametersCtx);

        @SuppressWarnings("unchecked")
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
        setRunListSortFields(listSortFields);

        String fileName = "CHOT_TON_THI_TRUONG";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        try {
            //parse data
            String regex = "[^\\[\\]\"\\s,]+"; //cut and take String not equals [ ] " , space
            Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(listProductStr);
            List<String> productIds = FastList.newInstance();
            while (matcher.find()) {
                productIds.add(matcher.group(0));
            }
            listProduct = delegator.findList("ProductAndUom",
                    EntityCondition.makeCondition(EntityCondition.makeCondition("isVirtual", "N"),
                            EntityOperator.AND, EntityCondition.makeCondition("productId", EntityOperator.IN, productIds)), null, null, null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // add subtitle rows
        SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateTimeOut = formatOut.format(nowTimestamp);
        addSubTitle(UtilProperties.getMessage(RESOURCE, "BSDateTime", locale), dateTimeOut);

        // add all columns
        addColumn(6, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
        addColumn(16, UtilProperties.getMessage(RESOURCE_SALES_MTL, "BSCustomerId", locale), "partyCode", ExportExcelStyle.STYLE_COLUMN_LABEL);
        addColumn(22, UtilProperties.getMessage(RESOURCE, "BSCustomerName", locale), "fullName", ExportExcelStyle.STYLE_COLUMN_LABEL);
        addColumn(25, UtilProperties.getMessage(RESOURCE, "BSSalesExecutive", locale), "createdBy", ExportExcelStyle.STYLE_COLUMN_LABEL);
        addColumn(20, UtilProperties.getMessage(RESOURCE_LOG, "InventoryDate", locale), "fromDate", ExportExcelStyle.STYLE_CELL_DATE);
        for (GenericValue item : listProduct) {
            addColumn(10, item.getString("productCode") + "(" + item.get("quantityUomDescription") + ")", "prodCode_" + item.getString("productId"));
        }
    }
}