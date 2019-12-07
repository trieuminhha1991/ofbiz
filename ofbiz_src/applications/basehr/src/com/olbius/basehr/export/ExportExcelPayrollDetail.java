package com.olbius.basehr.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastMap;
import org.apache.poi.ss.usermodel.Row;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import javax.rmi.CORBA.Util;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelPayrollDetail extends ExportExcelAbstract{
    private final String RESOURCE = "BaseHRUiLabels";
    private final String RESOURCE_INS = "BaseHRInsuranceUiLabels";
    private final String RESOURCE_PAYROLL = "BaseHRPayrollUiLabels";
    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        String fromDateOut = "";
        String thruDateOut = "";

        String fileName = "BANG_TIEN_LUONG";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        SimpleDateFormat formatOut = new SimpleDateFormat("dd/MM/yyyy");

        // get parameters content
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String payrollTableId = ExportExcelUtil.getParameter(parameters, "payrollTableId");

        try {
            GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
            if (UtilValidate.isNotEmpty(payrollTableRecord)){
                Timestamp fromDate = (Timestamp) payrollTableRecord.getTimestamp("fromDate");
                Timestamp thruDate = (Timestamp) payrollTableRecord.getTimestamp("thruDate");
                fromDateOut = formatOut.format(fromDate);
                thruDateOut = formatOut.format(thruDate);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        setHeaderName(UtilProperties.getMessage(RESOURCE_PAYROLL, "PayrollTableRecordTitleSimple", locale) + " "+ fromDateOut + " " + UtilProperties.getMessage(RESOURCE_PAYROLL, "PayrollTableRecordTo", locale) + " " + thruDateOut);
        setRunServiceName("JQGetListPayrollTableRecordPartyFast");
        setModuleExport("HR");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE_PAYROLL, "PayrollTableRecordPartyListTitle", locale));
        setSplitSheet(false);
        setMaxRowInSheet(300);

        // make parameters input
        Map<String, String[]> parametersCtx = FastMap.newInstance();
        parametersCtx.put("payrollTableId", new String[] {payrollTableId});
        setRunParameters(parametersCtx);
        @SuppressWarnings("unchecked")
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
        setRunListSortFields(listSortFields);

        // add subtitle rows
        String dateTimeOut = formatOut.format(nowTimestamp);
        addSubTitle(UtilProperties.getMessage(RESOURCE, "HRPDateTime", locale), dateTimeOut);

        // add all columns
        addColumn(6, UtilProperties.getMessage(RESOURCE, "HRPNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
        addColumn(20, UtilProperties.getMessage(RESOURCE, "EmployeeId", locale), "partyCode");
        addColumn(25, UtilProperties.getMessage(RESOURCE, "EmployeeName", locale), "fullName");
        addColumn(22, UtilProperties.getMessage(RESOURCE, "CommonDepartment", locale), "groupName");
        addColumn(22, UtilProperties.getMessage(RESOURCE, "HrCommonPosition", locale), "emplPositionTypeDes");
        addColumn(20, UtilProperties.getMessage(RESOURCE_PAYROLL, "SalaryBaseFlat", locale), "baseSalAmount", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(20, UtilProperties.getMessage(RESOURCE_PAYROLL, "RealWages", locale), "LUONG_CO_BAN", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRLunchAllowance", locale), "PHU_CAP_AN_TRUA", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRPositionAllowance", locale), "PHU_CAP_CHUC_VU", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRPhoneAllowance", locale), "PHU_CAP_DIEN_THOAI", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRUniformAllowance", locale), "PHU_CAP_DONG_PHUC", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRGasolineAllowance", locale), "PHU_CAP_XANG_XE", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HROtherIncome", locale), "OTHER_INCOME", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE_PAYROLL, "TotalIncome", locale), "totalIncome", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "InsuranceSalaryShort", locale), "insSalAmount", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRUnemploymentInsuranceOrg", locale), "BHTN_CTY", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRSocialInsuranceOrg", locale), "BHXH_CTY", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRHealthInsuranceOrg", locale), "BHYT_CTY", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRTotalOrgPaid", locale), "totalOrgPaid", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE_INS, "InsuranceUnemployment", locale), "BHTN", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE_INS, "InsuranceSocial", locale), "BHXH", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE_INS, "InsuranceHealth", locale), "BHYT", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRKPIPunish", locale), "PHAT_KPI", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRPersonalIncomeTax", locale), "THUE_THU_NHAP", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE, "HRTotalDeduct", locale), "totalDedution", ExportExcelStyle.STYLE_CELL_NUMBER);
        addColumn(15, UtilProperties.getMessage(RESOURCE_PAYROLL, "RealSalaryPaid", locale), "actualReceipt", ExportExcelStyle.STYLE_CELL_NUMBER);

    }

}