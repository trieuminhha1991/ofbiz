package com.olbius.acc.excel;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastList;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportExcelGlAccount extends ExportExcelAbstract {
	
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";
	private final  String RESOURCE = "SGCUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "danh-sach-tai-khoan";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCListGlAccount", locale).toUpperCase());
		
		setRunServiceName("JQGetListChartOfAccountOriginationTrans");
		setModuleExport("ACC");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE, "BACCListGlAccount", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String invoiceType = ExportExcelUtil.getParameter(parameters, "organizationPartyId");
		setRunParameters(parameters);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSSTT", locale), null);
		addColumn(12, UtilProperties.getMessage("AccountingUiLabels", "AccountingAccountId", locale), "glAccountId");
		addColumn(30, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_glAccountTypeId", locale), "glAccountTypeId");
        addColumn(20, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_glAccountClassId", locale), "glAccountClassId");
//        addColumn(20, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_glAccountClassId", locale), "total", ExportExcelStyle.STYLE_CELL_CURRENCY);
        addColumn(15, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_glResourceTypeId", locale), "glResourceTypeId");
        addColumn(25, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_glTaxFormId", locale), "glTaxFormId");
        addColumn(12, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_parentGlAccountId", locale), "parentGlAccountId");
        addColumn(12, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_accountCode", locale), "accountCode");
        addColumn(40, UtilProperties.getMessage("AccountingUiLabels", "FormFieldTitle_accountName", locale), "accountName");
        addColumn(20, UtilProperties.getMessage("AccountingUiLabels", "description", locale), "description");

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
			
			if ("glTaxFormId".equals(key)) {
				String glTaxFormId = (String) map.get("glTaxFormId");
				if(glTaxFormId != null){
					GenericValue glTaxForm = null;
					try {
                        glTaxForm = delegator.findOne("Enumeration", UtilMisc.toMap("enumId", glTaxFormId), false);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (UtilValidate.isNotEmpty(glTaxForm)) {
						value = glTaxForm.get("description", locale);
					}
				}
			} else {
				value = (Object) map.get(key);
			}
			
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
	@Override
	public String run() {
		initSheet();
		
		String groupName = "";
		String companyAddress = "";
		String organizationId = PartyUtil.getRootOrganization(delegator, userLogin.getString("userLoginId"));
		GenericValue party = null;
		try {
			party = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(party)) {
			groupName = party.getString("groupName");
		}

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> dummy = null;
		try {
			dummy = delegator.findList("PartyContactMechPurpose",
					EntityCondition.makeCondition(conditions), null, null, null, true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(dummy)) {
			try {
				companyAddress = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (UtilValidate.isNotEmpty(companyAddress)) {
				companyAddress = companyAddress.replaceAll(", __", "");
			}
		}
		
		Row subTitleRow = createRow((short) 400);
		createCell(subTitleRow, 0, groupName, getCellStylesMap().get(ExportExcelStyle.STYLE_SUBTITLE_CONTENT));
		getCurrentSheet().addMergedRegion(new CellRangeAddress(getRowNumber(), getRowNumber(), 0, getColumnNumber() - 1));
		
		Row subTitleRow1 = createRow((short) 400);
		createCell(subTitleRow1, 0, companyAddress, getCellStylesMap().get(ExportExcelStyle.STYLE_SUBTITLE_CONTENT));
		getCurrentSheet().addMergedRegion(new CellRangeAddress(getRowNumber(), getRowNumber(), 0, getColumnNumber() - 1));
		
		initHeader();
		initSubTitle();
		addBlankRow();
		initColumnHeader();
		String result = initColumnContent();
		
		return result;
	}
}