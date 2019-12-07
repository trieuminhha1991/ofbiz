package com.olbius.acc.report.summary;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

public class VDServices {

	public static final String MODULE = VDServices.class.getName();

	// @SuppressWarnings("unchecked")
	// public Map<String, Object> getVoucherDeclaration(DispatchContext dctx,
	// Map<String, Object> context) {
	// Delegator delegator = dctx.getDelegator();
	// List<Map<String, Object>> listVDs = new ArrayList<Map<String, Object>>();
	// //Get parameters
	// GenericValue userLogin = (GenericValue)context.get("userLogin");
	// EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	// List<String> listSortFields = (List<String>)
	// context.get("listSortFields");
	// listSortFields.add("-transactionDate");
	// listSortFields.add("-voucherId");
	// String organizationPartyId =
	// MultiOrganizationUtil.getCurrentOrganization(delegator,
	// userLogin.getString("userLoginId"));
	// List<EntityCondition> listAllConditions = (List<EntityCondition>)
	// context.get("listAllConditions");
	// /*opts.setFetchSize(15);*/
	// int size = 0;
	// EntityListIterator listAccTransFacts = null;
	// Map<String,String[]> parameters = (Map<String, String[]>)
	// context.get("parameters");
	//
	// if (UtilValidate.isEmpty(parameters.get("filterListFields")))
	// {
	// DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	// Calendar thruDate = Calendar.getInstance();
	// java.sql.Timestamp sqlThruDate = new java.sql.Timestamp
	// (thruDate.getTime().getTime());
	// Calendar fromDate = thruDate;
	// fromDate.set(Calendar.DAY_OF_MONTH,
	// fromDate.getActualMinimum(Calendar.DAY_OF_MONTH));
	// java.sql.Timestamp sqlFromDate = new java.sql.Timestamp
	// (fromDate.getTime().getTime());
	// EntityCondition fdateCondition =
	// EntityCondition.makeCondition("voucherDate",
	// EntityOperator.GREATER_THAN_EQUAL_TO, sqlFromDate);
	// EntityCondition tdateCondition =
	// EntityCondition.makeCondition("voucherDate",
	// EntityOperator.LESS_THAN_EQUAL_TO, sqlThruDate);
	// listAllConditions.add(fdateCondition);
	// listAllConditions.add(tdateCondition);
	// }
	// try {
	// String ps = parameters.containsKey("pagesize") ? (String)
	// parameters.get("pagesize")[0] : null;
	// String pn = parameters.containsKey("pagenum") ? (String)
	// parameters.get("pagenum")[0] : null;
	// //Get GlAccount
	// EntityCondition voucherCond =
	// EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("paymentId",
	// EntityJoinOperator.NOT_EQUAL, null),
	// EntityCondition.makeCondition("invoiceId", EntityJoinOperator.NOT_EQUAL,
	// null)), EntityJoinOperator.OR);
	// listAllConditions.add(EntityCondition.makeCondition("organizationPartyId",
	// organizationPartyId));
	// listAllConditions.add(voucherCond);
	// listAccTransFacts = delegator.find("AcctgTransFactRecip",
	// EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND),
	// null, null, listSortFields, opts);
	// List<GenericValue> listAccTransSub = FastList.newInstance();
	// if(ps != null && pn != null)
	// {
	// listAccTransSub =
	// listAccTransFacts.getPartialList(Integer.parseInt(ps)*Integer.parseInt(pn)
	// + 1, Integer.parseInt(ps));
	// }else listAccTransSub = listAccTransFacts.getCompleteList();
	//
	// /*while((item = listAccTransFacts.next()) != null) {*/
	// for(GenericValue item : listAccTransSub)
	// {
	// Map<String, Object> voucherDecl = new HashMap<String, Object>();
	// voucherDecl.put("transactionDate", item.getString("transactionDate"));
	// voucherDecl.put("glAccountCode", item.getString("glAccountCode"));
	// voucherDecl.put("recipGlAccountCode",
	// item.getString("recipGlAccountCode"));
	// voucherDecl.put("currencyId", item.getString("currencyId"));
	// String partyId = "";
	// if (UtilValidate.isNotEmpty(item.getString("partyId")))
	// partyId = item.getString("partyId");
	// else partyId = item.getString("partyGrId");
	// String customerName = PartyUtil.getPartyName(delegator, partyId);
	// voucherDecl.put("partyId",partyId);
	// voucherDecl.put("customerName",customerName);
	// //FIXME add description
	// /*voucherDecl.put("description", item.getString("description"));*/
	// voucherDecl.put("acctgTransEntryTypeId",
	// item.getString("acctgTransEntryTypeId"));
	// if (item.getString("paymentId") != null) {
	// voucherDecl.put("voucherNumber", item.getString("paymentId"));
	// }
	// if (item.getString("invoiceId") != null) {
	// GenericValue partyAccPref = delegator.findOne("PartyAcctgPreference",
	// UtilMisc.toMap("partyId", organizationPartyId), false);
	// String invoiceIdPrefix = partyAccPref.getString("invoiceIdPrefix");
	// String invoiceId = item.getString("invoiceId");
	// voucherDecl.put("voucherId", invoiceIdPrefix);
	// voucherDecl.put("voucherNumber",
	// invoiceId.substring(invoiceIdPrefix.length(), invoiceId.length()));
	// }
	// if(item.getString("debitCreditFlag").equals("C")) {
	// voucherDecl.put("creditAmount", item.getBigDecimal("amount").abs());
	// voucherDecl.put("debitAmount", BigDecimal.ZERO);
	// }else {
	// voucherDecl.put("debitAmount", item.getBigDecimal("amount").abs());
	// voucherDecl.put("creditAmount", BigDecimal.ZERO);
	// }
	// //FIXME
	// //Description, voucherID, voucherDate
	// voucherDecl.put("voucherDate", item.getString("voucherDate"));
	// voucherDecl.put("voucherDescription",
	// item.getString("voucherDescription"));
	// listVDs.add(voucherDecl);
	// }
	// size = listAccTransFacts.getResultsTotalSize();
	// } catch (GenericEntityException e) {
	// ErrorUtils.processException(e, e.getMessage());
	// return ServiceUtil.returnError(e.getMessage());
	// }finally {
	// if(listAccTransFacts != null) {
	// try {
	// listAccTransFacts.close();
	// } catch (GenericEntityException e) {
	// ErrorUtils.processException(e, MODULE);
	// }
	// }
	// }
	//
	// Map<String, Object> result = ServiceUtil.returnSuccess();
	// result.put("listIterator", listVDs);
	// result.put("TotalRows", size + "");
	// return result;
	// }
	//

	/*@SuppressWarnings("unchecked")
	public static Map<String, Object> getVoucherDeclaration(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("-documentDate");
		listSortFields.add("acctgTransId");
		listSortFields.add("acctgTransEntrySeqId");
		listSortFields.add("acctgTransEntrySeqId");
		listSortFields.add("reciprocalSeqId");

		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		opts.setResultSetType(ResultSet.HOLD_CURSORS_OVER_COMMIT);
		String oraganizationPartyId = ((GenericValue) context.get("userLogin")).getString("lastOrg");

		Map<String, String> mapCondition = new HashMap<String, String>();
		if (oraganizationPartyId != null) {
			mapCondition.put("organizationPartyId", oraganizationPartyId);
		}
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		try {
			tmpConditon = EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND);
			listIterator = delegator.find("AcctgDocumentListFact", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling getListInvoice service: " + e.toString();
			Debug.logError(e, errMsg, MODULE);
		}

		successResult.put("listIterator", listIterator);
		return successResult;
	}*/

}
