package com.olbius.basehr.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelPayrollTable extends ExportExcelAbstract{
    private final String RESOURCE = "BaseHRPayrollUiLabels";
    private String currencyUomId = null;
    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        String fileName = "DANH_SACH_BANG_LUONG";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        setHeaderName(UtilProperties.getMessage(RESOURCE, "PayrollTableRecordTitle", locale));
        setRunServiceName("JQGetListPayrollTableRecord");
        setModuleExport("HR");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "PayrollTableRecordTitle", locale));
        setSplitSheet(false);
        setMaxRowInSheet(100);

        // get parameters content
        @SuppressWarnings({ "unchecked" })
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        currencyUomId = ExportExcelUtil.getParameter(parameters, "currencyUomId");
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
        addSubTitle(UtilProperties.getMessage(RESOURCE, "HRPDateTime", locale), dateTimeOut);

        // add all columns
        addColumn(6, UtilProperties.getMessage(RESOURCE, "HRPNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
        addColumn(25, UtilProperties.getMessage(RESOURCE, "PayrollTableName", locale), "payrollTableName");
        addColumn(20, UtilProperties.getMessage(RESOURCE, "HRPCommonStatus", locale), "statusId");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "HRPCommonFromDate", locale), "fromDate", ExportExcelStyle.STYLE_CELL_DATETIME);
        addColumn(16, UtilProperties.getMessage(RESOURCE, "HRPCommonThruDate", locale), "thruDate", ExportExcelStyle.STYLE_CELL_DATETIME);
        addColumn(18, UtilProperties.getMessage(RESOURCE, "TotalOrgPaidInsurance", locale), "totalOrgPaid", ExportExcelStyle.STYLE_CELL_CURRENCY);
        addColumn(18, UtilProperties.getMessage(RESOURCE, "TotalRealSalaryPaid", locale), "totalAcutalReceipt", ExportExcelStyle.STYLE_CELL_CURRENCY);

    }

    @Override
    protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
        int columnIndex = 0;

        if (hasColumnIndex) {
            ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
            columnIndex++;
        }

        for (int i = columnIndex; i < columnKeys.size(); i++) {
            Object value = null;
            String key = columnKeys.get(i);
            if ("statusId".equals(key)) {
                String statusId = (String) map.get("statusId");
                if (statusId != null) {
                    try {
                        GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
                        if (status != null) {
                            value = status.get("description", locale);
                        }
                    } catch (GenericEntityException e) {
                        Debug.logWarning("Error when get status", module);
                    }

                }
            } else if("totalOrgPaid".equals(key) && UtilValidate.isNotEmpty(currencyUomId)) {
                BigDecimal totalOrgPaid = (BigDecimal) map.get("totalOrgPaid");
                value = UtilFormatOut.formatCurrency(totalOrgPaid, currencyUomId, locale);
            } else if("totalAcutalReceipt".equals(key) && UtilValidate.isNotEmpty(currencyUomId)){
                BigDecimal totalAcutalReceipt = (BigDecimal) map.get("totalAcutalReceipt");
                value = UtilFormatOut.formatCurrency(totalAcutalReceipt, currencyUomId, locale);
            } else {
                value = (Object) map.get(key);
            }
            createCell(row, i, value, cellStyles.get(i));
        }
    }

}
