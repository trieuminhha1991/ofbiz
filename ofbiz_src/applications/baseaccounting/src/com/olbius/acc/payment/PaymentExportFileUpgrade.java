package com.olbius.acc.payment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

import com.olbius.acc.utils.ExcelUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.common.export.ExportExcelAbstract;
import com.olbius.common.export.ExportExcelStyle;
import com.olbius.common.export.ExportExcelUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class PaymentExportFileUpgrade extends ExportExcelAbstract {
	private final String RESOURCE_ACCOUTING = "BaseAccountingUiLabels";
	private final String RESOURCE_SALE = "BaseSalesUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		// TODO Auto-generated method stub
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

		String fileName = "danh_sach_thanh_toan";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String dateTime = format.format(nowTimestamp);
		fileName += "_" + dateTime;
		setFileName(fileName);

		setHeaderName(UtilProperties.getMessage(RESOURCE_ACCOUTING, "BACCPaymentList", locale).toUpperCase());
		setRunServiceName("JqxGetListPaymentsNew");
		setModuleExport("BASEACCOUTING");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACCOUTING, "BACCPaymentList", locale));

		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String paymentType = parameters.get("paymentType") != null? ((String[])parameters.get("paymentType"))[0] : null;
		setRunParameters(parameters);

		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		@SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
		
		
		addColumn(10, UtilProperties.getMessage(RESOURCE_SALE, "BSSTT", locale), null);
		addColumn(20, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentId", locale), "paymentCode");
		addColumn(25, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentFromParty", locale), "fullNameFrom");
		if("AP".equals(paymentType)){
			addColumn(15, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentToParty", locale), "fullNameTo");
			addColumn(25, UtilProperties.getMessage("BaseSalesUiLabels", "BSOrganizationId", locale), "partyCodeTo");
		}else{
			addColumn(25, UtilProperties.getMessage("BaseSalesUiLabels", "BSOrganizationId", locale), "partyCodeFrom");
			addColumn(15, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentToParty", locale), "fullNameTo");

		}
		addColumn(32, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentMethodType", locale), "paymentMethodId");
		addColumn(22, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentTypeId", locale),"paymentTypeId" );
		addColumn(20, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCStatusId", locale), "statusId");
		addColumn(19, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCEffectiveDate", locale), "effectiveDate");
		addColumn(22, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAmount", locale), "amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
		addColumn(22, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCAppliedPayments", locale), "paymentApplied", ExportExcelStyle.STYLE_CELL_CURRENCY );
		addColumn(22, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpenPayments", locale), "openPayment", ExportExcelStyle.STYLE_CELL_CURRENCY );
		addColumn(70, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCComment", locale) , "comments" );
		addColumn(15, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPaymentId", locale) , "paymentId" );
	}

	@Override
	protected void initCells(Map<String, Object> map, int rowIndex, Row row) {
		int columnIndex = 0 ;
		
		if(hasColumnIndex){
			ExportExcelUtil.createCell(row, columnIndex, rowIndex, cellStyles.get(columnIndex)); // 0. STT
			columnIndex++;
		}
		
		for(int i= columnIndex; i < columnKeys.size(); i++){
			Object value =null;
			String key = columnKeys.get(i);
			if("statusId".equals(key)){
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
			}else if("paymentMethodId".equals(key)){
				String paymentMethodId = (String) map.get("paymentMethodId");
				try{
					GenericValue paymentMethod = delegator.findOne("PaymentMethod", UtilMisc.toMap("paymentMethodId", paymentMethodId), false);
					if(paymentMethod != null){
						value = paymentMethod.get("description", locale);
					}
				} catch (GenericEntityException e) {
					Debug.logWarning("Error when get paymentMethodId", module);
				}
			}else if("paymentTypeId".equals(key)){
				String paymentTypeId = (String)map.get("paymentTypeId");
				if(paymentTypeId!=null){
					try{
						GenericValue paymentType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", paymentTypeId), false);
						if(UtilValidate.isNotEmpty(paymentTypeId)){
							value = paymentType.get("description", locale);
						}
					}catch (GenericEntityException e) {
						Debug.logWarning("Error when get paymentTypeId", module);
					}
				}
				
			}else if("openPayment".equals(key)){
				BigDecimal amount = map.get("amount") != null ? (BigDecimal) map.get("amount") : BigDecimal.ZERO;
				BigDecimal paymentApplied = map.get("paymentApplied") != null ? (BigDecimal) map.get("paymentApplied") : BigDecimal.ZERO;
				value = amount.subtract(paymentApplied);
			}else {
				value = (Object) map.get(key);
			}
			createCell(row, i, value, cellStyles.get(i));
		}
	}
	
	@Override
	public String run() {
		initSheet();
		String result= "";
		// Dong mo rong 1
		try {
			Map<String, Object> infor = getAddressInfo(delegator, userLogin, locale);
			if(UtilValidate.isNotEmpty(infor)){
				Map<String, CellStyle> styles = ExcelUtil.createStyles(getWb());
				Row subTitleRow = createRow((short) 400);
				createCell(subTitleRow, 0, infor.get("groupName").toString().toUpperCase(), styles.get("cell_bold_normal_Left_8"));
				getCurrentSheet().addMergedRegion(new CellRangeAddress(getRowNumber(),
				getRowNumber(), 0, 3));
				
				Row subTitleRow2 = createRow((short) 400);
				createCell(subTitleRow2, 0, (String)infor.get("companyAddress"), styles.get("cell_bold_normal_Left_8"));
				getCurrentSheet().addMergedRegion(new CellRangeAddress(getRowNumber(),getRowNumber(), 0, 3));
				initHeader();
				initSubTitle();
				addBlankRow();
				initColumnHeader();
				result = initColumnContent();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static Map<String, Object> getAddressInfo(Delegator delegator, GenericValue userLogin, Locale locale) throws Exception{
		Map<String, Object> info = FastMap.newInstance();

		String organizationId = PartyUtil.getRootOrganization(delegator, userLogin.getString("userLoginId"));
		info.putAll(delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", organizationId), true));

		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(
				UtilMisc.toMap("partyId", organizationId, "contactMechPurposeTypeId", "PRIMARY_LOCATION")));
		List<GenericValue> dummy = delegator.findList("PartyContactMechPurpose",
				EntityCondition.makeCondition(conditions), null, null, null, true);
		if (UtilValidate.isNotEmpty(dummy)) {
			String fullName = delegator.findOne("PostalAddressDetail", UtilMisc.toMap("contactMechId", EntityUtil.getFirst(dummy).get("contactMechId")), false).getString("fullName");
			if (UtilValidate.isNotEmpty(fullName)) {
				fullName = fullName.replaceAll(", __", "");
			}
			info.put("companyAddress", fullName);
		}
		return info;
	}
}
