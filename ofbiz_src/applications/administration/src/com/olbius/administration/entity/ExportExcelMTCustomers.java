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

public class ExportExcelMTCustomers extends ExportExcelAbstract {
    private final String RESOURCE = "BaseSalesUiLabels";

    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        String fileName = "DANH_SACH_KH_MT";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        setHeaderName(UtilProperties.getMessage(RESOURCE, "BSListCustomerMT", locale));
        setRunServiceName("JQGetListMTCustomerSample");
        setModuleExport("SALES");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BSListCustomerMT", locale));
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
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomerId", locale), "partyCode");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomerName", locale) + "(*)", "fullName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSOfficeSiteName", locale), "fullName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "PartyTaxAuthInfos", locale), "taxCode");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCurrencyUomId", locale), "preferredCurrencyUomId");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSDescription", locale), "comments");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSSupervisor", locale) + "(*)", "supervisorName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSSalesman", locale), "salesmanName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSRoute", locale), "routeName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSPSProductStore", locale) + "(*)", "productStores");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCustomerType", locale) + "(*)", "partyTypeName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCountry", locale) + "(*)", "countryGeoName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSStateProvince", locale) + "(*)", "stateProvinceGeoName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSCounty", locale), "districtGeoName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSWard", locale), "wardGeoName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSAddress1", locale) + "(*)", "address1");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSPhoneNumber", locale) + "(*)", "contactNumber");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSEmailAddress", locale), "emailName");
        addColumn(16, UtilProperties.getMessage(RESOURCE, "BSRepresentativeName", locale), "representative.partyFullName");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSRepresentativeGender", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "representative.gender");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSRepresentativeBirthday", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ", dd/mm/yyyy)", "representative.birthDate");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCountry", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "countryGeoName");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSStateProvince", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "stateProvinceGeoName");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSCounty", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "districtGeoName");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSWard", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "wardGeoName");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSAddress1", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "address1");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSPhoneNumber", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "representative.contactNumber");
        addColumn(36, UtilProperties.getMessage(RESOURCE, "BSEmailAddress", locale) + "("
                + UtilProperties.getMessage(RESOURCE, "BSRepresentative", locale) + ")", "representative.infoString");
    }
}