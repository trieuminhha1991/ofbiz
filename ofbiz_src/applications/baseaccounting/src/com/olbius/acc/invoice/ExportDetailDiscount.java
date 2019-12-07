package com.olbius.acc.invoice;

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

public class ExportDetailDiscount extends ExportExcelAbstract {
	
	private final String RESOURCE_ACC = "BaseAccountingUiLabels";

	@Override
	protected void prepareParameters(DispatchContext dctx, Map<String, Object> context) {
		Locale locale = getLocale();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		
		String fileName = "chi-tiet-chiet-khau";
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-YYYY");
		String dateTime = format.format(nowTimestamp);
		fileName += "-" + dateTime;
		setFileName(fileName);
		setHeaderName(UtilProperties.getMessage(RESOURCE_ACC, "BACCViewDetailDiscount", locale).toUpperCase());
		
		setRunServiceName("jqxGetDetailDiscountForParty");
		setModuleExport("ACC");
		setDescriptionExport(UtilProperties.getMessage(RESOURCE_ACC, "BACCViewDetailDiscount", locale));
		setSplitSheet(false);
		setMaxRowInSheet(100);
		
		// get parameters content
		@SuppressWarnings({ "unchecked" })
		Map<String, String[]> parameters = FastMap.newInstance();
        parameters.putAll((Map<String, String[]>) context.get("parameters"));
        parameters.put("isFullData", new String[]{"Y"});
		setRunParameters(parameters);
		
		@SuppressWarnings("unchecked")
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		
        @SuppressWarnings("unchecked")
		List<String> listSortFields = (List<String>) context.get("listSortFields");
        
        setRunListAllConditions(listAllConditions);
		setRunListSortFields(listSortFields);
        
		// add all columns
		addColumn(8, UtilProperties.getMessage(RESOURCE_ACC, "BACCSeqId", locale), null);
		addColumn(12, UtilProperties.getMessage(RESOURCE_ACC, "BACCInvoiceId", locale), "invoiceId");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductId", locale), "productCode");
        addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCProductName", locale), "productName");
		addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCQuantity", locale), "quantity", ExportExcelStyle.STYLE_CELL_CURRENCY);
        addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCUnitPrice", locale), "amount", ExportExcelStyle.STYLE_CELL_CURRENCY);
        addColumn(20, UtilProperties.getMessage(RESOURCE_ACC, "BACCTotal", locale), "totalAmount", ExportExcelStyle.STYLE_CELL_CURRENCY);
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
			value = map.get(key);
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
		return initColumnContent();
	}
}