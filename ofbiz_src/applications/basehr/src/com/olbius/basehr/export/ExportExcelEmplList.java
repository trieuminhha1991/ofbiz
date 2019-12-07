package com.olbius.basehr.export;

import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastMap;
import org.apache.poi.ss.usermodel.Row;
import org.apache.xmlrpc.webserver.HttpServletRequestImpl;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelEmplList extends ExportExcelAbstract{
    private final String RESOURCE = "BaseHRUiLabels";
    private final String RESOURCE_PTY = "PartyUiLabels";
    @Override
    protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
        Locale locale = getLocale();
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        String fileName = "DANH_SACH_NHAN_VIEN";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String dateTime = format.format(nowTimestamp);
        fileName += "_" + dateTime;
        setFileName(fileName);
        setHeaderName(UtilProperties.getMessage(RESOURCE, "HREmplList", locale));
        setRunServiceName("JQListEmployeeDetailInfo");
        setModuleExport("HR");
        setDescriptionExport(UtilProperties.getMessage(RESOURCE, "HREmplList", locale));
        setSplitSheet(false);
        setMaxRowInSheet(300);

        // get parameters content
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String partyGroupId = ExportExcelUtil.getParameter(parameters, "partyGroupId");
        String fromDate = ExportExcelUtil.getParameter(parameters, "fromDate");
        String thruDate = ExportExcelUtil.getParameter(parameters, "thruDate");
        String hasrequest = ExportExcelUtil.getParameter(parameters, "hasrequest");

        // make parameters input

        Map<String, String[]> parametersCtx = FastMap.newInstance();
        parametersCtx.put("partyGroupId", new String[] { partyGroupId });
        parametersCtx.put("fromDate", new String[] { fromDate });
        parametersCtx.put("thruDate", new String[] { thruDate });
        parametersCtx.put("hasrequest", new String[] { hasrequest });
        setRunParameters(parametersCtx);



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
        addColumn(20, UtilProperties.getMessage(RESOURCE, "EmployeeId", locale), "partyCode");
        addColumn(25, UtilProperties.getMessage(RESOURCE, "EmployeeName", locale), "fullName");
        addColumn(12, UtilProperties.getMessage(RESOURCE_PTY, "PartyGender", locale), "gender");
        addColumn(25, UtilProperties.getMessage(RESOURCE, "HrCommonPosition", locale), "emplPositionType");
        addColumn(20, UtilProperties.getMessage(RESOURCE, "PartyIdWork", locale), "department");
        addColumn(18, UtilProperties.getMessage(RESOURCE, "HRCommonCurrStatus", locale), "workingStatusId");
        addColumn(18, UtilProperties.getMessage(RESOURCE_PTY, "PartyBirthDate", locale), "birthDate", ExportExcelStyle.STYLE_CELL_DATETIME);
        addColumn(18, UtilProperties.getMessage(RESOURCE, "DateJoinCompany", locale), "dateJoinCompany", ExportExcelStyle.STYLE_CELL_DATETIME);
        addColumn(18, UtilProperties.getMessage(RESOURCE, "AgreementDate", locale), "agreementDate", ExportExcelStyle.STYLE_CELL_DATETIME);
        addColumn(18, UtilProperties.getMessage(RESOURCE, "HREmplResignDate", locale), "dateResign", ExportExcelStyle.STYLE_CELL_DATETIME);
        addColumn(18, UtilProperties.getMessage(RESOURCE, "HREmplReasonResign", locale), "terminationReasonId");
        addColumn(18, UtilProperties.getMessage(RESOURCE, "agreementTypeId", locale), "agreementTypeId");
        addColumn(18, UtilProperties.getMessage(RESOURCE, "ProbationDuration", locale), "probationaryDeadline");

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
            if("workingStatusId".equals(key)){
                String workingStatusId = (String) map.get("workingStatusId");
                if(UtilValidate.isNotEmpty(workingStatusId)){
                    try{
                        GenericValue workingStatus = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", workingStatusId), false);
                        if(UtilValidate.isNotEmpty(workingStatusId)){
                            value = workingStatus.get("description", locale);
                        }
                    }catch (GenericEntityException e){
                        Debug.logWarning("Error when get working status", module);
                    }
                }
            }else if ("gender".equals(key)){
                String genderId = (String) map.get("gender");
                if(UtilValidate.isNotEmpty(genderId)){
                    try{
                        GenericValue gender = delegator.findOne("Gender", UtilMisc.toMap("genderId", genderId), false);
                        if(UtilValidate.isNotEmpty(genderId)){
                            value = gender.get("description", locale);
                        }
                    }catch (GenericEntityException e){
                        Debug.logWarning("Error when get gender", module);
                    }
                }
            }else if("terminationReasonId".equals(key)){
                String terminationReasonId = (String) map.get("terminationReasonId");
                if(UtilValidate.isNotEmpty(terminationReasonId)){
                    try{
                        GenericValue terminationReason = delegator.findOne("TerminationReason", UtilMisc.toMap("terminationReasonId", terminationReasonId), false);
                        if(UtilValidate.isNotEmpty(terminationReason)){
                            value = terminationReason.get("description", locale);
                        }
                    }catch (GenericEntityException e){
                        Debug.logWarning("Error when get terminationReason", module);
                    }
                }
            }else {
                value = (Object) map.get(key);
            }
            createCell(row, i, value, cellStyles.get(i));
        }
    }

}
