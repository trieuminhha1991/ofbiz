package com.olbius.acc.excel;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
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

public class ExportExcelGeneralLedger extends ExportExcelAbstract {
	private final String RESOURCE_SALES = "BaseSalesUiLabels";
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "so-cai-tai-khoan";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCGeneralLedger", locale).toUpperCase());
		
		setRunServiceName("JqxGetGeneralLedger");
		setModuleExport("BASEACCOUTING");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCGeneralLedger", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String glAccountId = ExportExcelUtil.getParameter(parameters, "glAccountId");
		String fromDate = ExportExcelUtil.getParameter(parameters, "fromDate");
		String thruDate = ExportExcelUtil.getParameter(parameters, "thruDate");
		
		// make parameters input
		Map<String, String[]> parametersCtx = FastMap.newInstance();
		parametersCtx.put("glAccountId", new String[] { glAccountId });
		parametersCtx.put("fromDate", new String[] { fromDate });
		parametersCtx.put("thruDate", new String[] { thruDate });
		setRunParameters(parametersCtx);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add subtitle rows
		addSubTitle(UtilProperties.getMessage(RESOURCE_ACC, "ExcelFromDate", locale) + ": ", " " + fromDate);
		addSubTitle(UtilProperties.getMessage(RESOURCE_ACC, "ExcelThruDate", locale) + ": ", " " + thruDate);
		GenericValue glAccount = null;
		try {
			glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), true);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(glAccount)) {
			addSubTitle(UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountId", locale) + ": ", " " + glAccountId + " - " + glAccount.getString("accountName"));
		}
		
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_SALES, "BSSTT", locale), null);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCTransDate", locale), "transDate");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCAcctgTransId", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCGLVoucher", locale), "acctgTransId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherId", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCGLVoucher", locale), "documentId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherNumber", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCGLVoucher", locale), "voucherCode");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherNumberSystem", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCGLVoucher", locale), "documentNumber");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCVoucherDate", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCGLVoucher", locale), "voucherDate");
		addColumn(30, UtilProperties.getMessage(RESOURCE_ACC, "BACCDescription", locale), "voucherDescription");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCRecipGlAccountId", locale), "recipGlAccountCode");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCGLAmount", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCDebitAmount", locale), "debitAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCGLAmount", locale) + " " +
				UtilProperties.getMessage(RESOURCE_ACC, "BACCCreditAmount", locale), "creditAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(30, UtilProperties.getMessage(RESOURCE_ACC, "BACCNote", locale), "note");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCGlAccountId", locale), "glAccountCode");
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