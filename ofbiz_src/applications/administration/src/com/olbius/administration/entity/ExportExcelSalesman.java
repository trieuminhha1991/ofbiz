package com.olbius.administration.entity;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelSalesman extends ExportExcelAbstract {
    private final String RESOURCE = "BaseSalesUiLabels";
    private final String RESOURCE_ = "BaseHRDirectoryUiLabels";

    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        String fileName = "DANH_SACH_SALESMAN";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        setHeaderName(UtilProperties.getMessage(RESOURCE, "BSListSalesman", locale));
        setRunServiceName("JQGetListSalesmanSample");
        setModuleExport("SALES");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSListSalesman", locale));
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
        addColumn(6, UtilProperties.getMessage(RESOURCE, "BSNo2", locale), null, ExportExcelStyle.STYLE_CELL_CONTENT_CENTER);
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSEmployeeId", locale) + " (*)", "partyCode");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSLastName", locale) + " (*)", "lastName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSMiddleName", locale), "middleName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSFirstName", locale)  + " (*)", "firstName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSRepresentativeGender", locale), "gender");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSRepresentativeBirthday", locale) + " (dd/mm/yyyy)", "birthday");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "IDNumber", locale), "idNumber");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "Religion", locale), "religion");
        addColumn(36, UtilProperties.getMessage(RESOURCE_, "HrolbiusidIssueDate", locale) + " (dd/mm/yyyy)", "idIssueDate");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "HrolbiusidIssuePlace", locale), "idIssuePlace");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "MaritalStatus", locale), "maritalStatusId");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "EthnicOrigin", locale), "ethnicOrigin");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "Nationality", locale), "nationality");
        addColumn(16, UtilProperties.getMessage(RESOURCE_, "NativeLand", locale), "nativeLand");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSAddress1", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "PermanentResidence", locale) + ")", "address1Per");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSWard", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "PermanentResidence", locale) + ")", "wardGeoNamePer");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCounty", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "PermanentResidence", locale) + ")", "districtGeoNamePer");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSStateProvince", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "PermanentResidence", locale) + ")", "stateProvinceGeoNamePer");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCountry", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "PermanentResidence", locale) + ")", "countryGeoNamePer");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSAddress1", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "CurrentResidence", locale) + ")", "address1Curr");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSWard", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "CurrentResidence", locale) + ")", "wardGeoNameCurr");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCounty", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "CurrentResidence", locale) + ")", "districtGeoNameCurr");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSStateProvince", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "CurrentResidence", locale) + ")", "stateProvinceGeoNameCurr");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCountry", locale) + "(" +
                UtilProperties.getMessage("BaseHRUiLabels", "CurrentResidence", locale) + ")", "countryGeoNameCurr");
        addColumn(24, UtilProperties.getMessage(RESOURCE_, "PartyIdWork", locale) + " (*)", "partyIdFrom");
        addColumn(24, UtilProperties.getMessage(RESOURCE_, "JobPosition", locale) + " (*)", "emplPositionTypeName");
        addColumn(36, UtilProperties.getMessage(RESOURCE_, "DateJoinCompany", locale) + " (*, dd/mm/yyyy)", "dateJoinCompany");
        addColumn(36, UtilProperties.getMessage(RESOURCE_, "SalaryBaseFlat", locale) + " (*)" + " " +
                UtilProperties.getMessage("BaseSalesMtlUiLabels", "BSDecimalPoint", locale), "amount");
        addColumn(24, UtilProperties.getMessage(RESOURCE_, "PeriodTypePayroll", locale) + " (*)", "periodTypeId");
        addColumn(24, UtilProperties.getMessage(RESOURCE, "UserLoginID", locale) + " (*)", "userLoginId");
    }
}