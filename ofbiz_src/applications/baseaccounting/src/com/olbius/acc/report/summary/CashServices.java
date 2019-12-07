package com.olbius.acc.report.summary;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDataSourceException;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashServices {
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getCashDetailBook(DispatchContext dctx, Map<String, Object> context) throws ParseException {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listGLs = new ArrayList<Map<String, Object>>();
		List<Object> listAccountId = FastList.newInstance();
		//Get parameters
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("transactionDate");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String	fromDate = (String) parameters.get("fromDate")[0];
		String	thruDate = (String) parameters.get("thruDate")[0];
		
		String	glAccountId = (String) parameters.get("glAccountId")[0];
		try {
			Map<String, Object> firstMIB = new HashMap<String, Object>();
			String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale)context.get("locale"));
			firstMIB.put("voucherDescription", openingBalMess);
			
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			Date fdate = (Date) dt.parse(fromDate);
			Date tdate = (Date) dt.parse(thruDate);
			
			Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
			BigDecimal balAmount = openingBal.get(BalanceWorker.DEBIT);
			firstMIB.put("balAmount", balAmount);
			listGLs.add(firstMIB);
			
			//Get Cash Detail Book
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			//Get GlAccount
			Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
			List<Account> listAccount = acc.getListChild();
			for(Account item : listAccount) {
				AccountEntity accEntity = item.getAcc();
				listAccountId.add(accEntity.getGlAccountId());
			}
			listAccountId.add(glAccountId);
			listConds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			listConds.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));
			
			listConds.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
			listConds.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			List<GenericValue> listAccTransFacts = delegator.findList("AcctgDocumentListFact", EntityCondition.makeCondition(listConds), null, listSortFields, null, false);
			for(GenericValue item: listAccTransFacts) {
				Map<String, Object> moneyInBank = new HashMap<String, Object>();
				moneyInBank.put("transDate", item.getDate("transactionDate"));
				moneyInBank.put("glAccountCode", item.getString("accountCode"));
				moneyInBank.put("recipGlAccountCode", item.getString("accountRecipCode"));
				moneyInBank.put("currencyId", item.getString("currencyId"));
				moneyInBank.put("voucherDate", item.getTimestamp("documentDate"));

				String voucherId = item.getString("documentId") == null ? item.getString("documentNumber") : (item.getString("documentId") + item.getString("documentNumber"));
				moneyInBank.put("voucherID", item.getString("voucherCode") + " ( " +  voucherId + " ) ");
				moneyInBank.put("voucherDescription", item.getString("description"));
				
				
				if (item.getBigDecimal("drAmount").compareTo(BigDecimal.ZERO) > 0)
				moneyInBank.put("receiptVoucherID" , item.getString("voucherCode") + " ( " +  voucherId + " ) ");
				else if (item.getBigDecimal("crAmount").compareTo(BigDecimal.ZERO) > 0)
				moneyInBank.put("payVoucherID", item.getString("voucherCode") + " ( " +  voucherId + " ) ");
				String parentType = item.getString("documentParentType") == null ? "" : item.getString("documentParentType");
				
				switch (parentType) {
					case VoucherType.PAYMENT_TYPE:
						GenericValue voucherType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("documentType")), false);
						moneyInBank.put("voucherType", voucherType.get("description",locale));
						break;
					default:
						break;
				}
				
				moneyInBank.put("creditAmount", item.getBigDecimal("crAmount"));
				moneyInBank.put("debitAmount", item.getBigDecimal("drAmount"));
				
				balAmount = balAmount.subtract(item.getBigDecimal("crAmount"));
				balAmount = balAmount.add(item.getBigDecimal("drAmount"));
				moneyInBank.put("balAmount", balAmount);
				//FIXME 
				//Description, voucherID, voucherDate
				listGLs.add(moneyInBank);
			}
		} catch (GenericEntityException | NoSuchAlgorithmException e) {
			ErrorUtils.processException(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", listGLs);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getMoneyInBank(DispatchContext dctx, Map<String, Object> context) throws ParseException {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listGLs = new ArrayList<Map<String, Object>>();
		List<Object> listAccountId = FastList.newInstance();
		//Get parameters
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String	glAccountId = (String) parameters.get("glAccountId")[0];
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		listSortFields.add("transactionDate");		
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String	fromDate = (String) parameters.get("fromDate")[0];
		String	thruDate = (String) parameters.get("thruDate")[0];
		try {
			Map<String, Object> firstMIB = new HashMap<String, Object>();
			String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale)context.get("locale"));
			firstMIB.put("voucherDescription", openingBalMess);
			
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			Date fdate = (Date) dt.parse(fromDate);
			Date tdate = (Date) dt.parse(thruDate);
			
			Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
			BigDecimal balAmount = openingBal.get(BalanceWorker.DEBIT);
			firstMIB.put("balAmount", balAmount);
			listGLs.add(firstMIB);
			
			//Get Cash Detail Book
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			//Get GlAccount
			Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
			List<Account> listAccount = acc.getListChild();
			for(Account item : listAccount) {
				AccountEntity accEntity = item.getAcc();
				listAccountId.add(accEntity.getGlAccountId());
			}
			listAccountId.add(glAccountId);
			listConds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			listConds.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));
			
			listConds.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
			listConds.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			List<GenericValue> listAccTransFacts = delegator.findList("AcctgDocumentListFact", EntityCondition.makeCondition(listConds), null, listSortFields, null, false);
			for(GenericValue item: listAccTransFacts) {
				Map<String, Object> moneyInBank = new HashMap<String, Object>();
				moneyInBank.put("transDate", item.getDate("transactionDate"));
				moneyInBank.put("glAccountCode", item.getString("accountCode"));
				moneyInBank.put("recipGlAccountCode", item.getString("accountRecipCode"));
				moneyInBank.put("currencyId", item.getString("currencyId"));
				moneyInBank.put("voucherDate", item.getTimestamp("documentDate"));
				String voucherId = item.getString("documentId") == null ? item.getString("documentNumber") : (item.getString("documentId") + item.getString("documentNumber"));
				moneyInBank.put("voucherID", item.getString("voucherCode") + " ( " +  voucherId + " ) ");
				moneyInBank.put("voucherDescription", item.getString("description"));
				String parentType = item.getString("documentParentType") == null ? "" : item.getString("documentParentType");
				
				switch (parentType) {
					case VoucherType.PAYMENT_TYPE:
						GenericValue voucherType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("documentType")), false);
						moneyInBank.put("voucherType", voucherType.get("description",locale));
						break;
					default:
						break;
				}
				
				moneyInBank.put("creditAmount", item.getBigDecimal("crAmount"));
				moneyInBank.put("debitAmount", item.getBigDecimal("drAmount"));
				
				balAmount = balAmount.subtract(item.getBigDecimal("crAmount"));
				balAmount = balAmount.add(item.getBigDecimal("drAmount"));
				moneyInBank.put("balAmount", balAmount);
				//FIXME 
				//Description, voucherID, voucherDate
				listGLs.add(moneyInBank);
			}
		} catch (GenericEntityException | NoSuchAlgorithmException e) {
			ErrorUtils.processException(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", listGLs);
		return result;
	}
	
	public static String getEndingAmountMoneyInBank(HttpServletRequest request, HttpServletResponse response) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz.olap"));
		ResultSet resultSet = null;
		OlbiusQuery query = new OlbiusQuery(processor);

		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		
		BigDecimal endingAmount = BigDecimal.ZERO;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		//Get GlAccount
		List<Object> listAccountId = FastList.newInstance();
		if (UtilValidate.isNotEmpty(glAccountId)) {
			Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
			List<Account> listAccount = acc.getListChild();
			for(Account item : listAccount) {
				AccountEntity accEntity = item.getAcc();
				listAccountId.add(accEntity.getGlAccountId());
			}
			listAccountId.add(glAccountId);
		}
		
		query.select("dr_amount").select("cr_amount");
		query.from("acctg_document_list_fact");
		Condition cond = new Condition();
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		cond.and(Condition.makeIn("gl_account_id", listAccountId), UtilValidate.isNotEmpty(listAccountId));
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		Date fdate = null, tdate = null;
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			fdate = (Date) dt.parse(fromDate);
			tdate = (Date) dt.parse(thruDate);
		}
		cond.and(Condition.make("transaction_date", ">=", getSqlFromDate(fdate)), UtilValidate.isNotEmpty(fdate));
		cond.and(Condition.make("transaction_date", "<=", getSqlThruDate(tdate)), UtilValidate.isNotEmpty(tdate));
		query.where(cond);
		
		if (UtilValidate.isNotEmpty(fdate) && UtilValidate.isNotEmpty(tdate) && UtilValidate.isNotEmpty(glAccountId)) {
			Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()),
					new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
			endingAmount = openingBal.get(BalanceWorker.DEBIT);
		}
		
		try {
			try {
				resultSet = query.getResultSet();
			} catch (GenericDataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while (resultSet.next()) {
					BigDecimal crAmount = resultSet.getBigDecimal("cr_amount");
					BigDecimal drAmount = resultSet.getBigDecimal("dr_amount");
					
					endingAmount = endingAmount.subtract(crAmount);
					endingAmount = endingAmount.add(drAmount);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		request.setAttribute("endingAmount", endingAmount);
		return "success";
	}
	
	public static String getEndingAmountCashDetail(HttpServletRequest request, HttpServletResponse response) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz.olap"));
		ResultSet resultSet = null;
		OlbiusQuery query = new OlbiusQuery(processor);
		
		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		
		BigDecimal endingAmount = BigDecimal.ZERO;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		//Get GlAccount
		List<Object> listAccountId = FastList.newInstance();
		if (UtilValidate.isNotEmpty(glAccountId)) {
			Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
			List<Account> listAccount = acc.getListChild();
			for(Account item : listAccount) {
				AccountEntity accEntity = item.getAcc();
				listAccountId.add(accEntity.getGlAccountId());
			}
			listAccountId.add(glAccountId);
		}
		
		query.select("dr_amount").select("cr_amount");
		query.from("acctg_document_list_fact");
		Condition cond = new Condition();
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		cond.and(Condition.makeIn("gl_account_id", listAccountId), UtilValidate.isNotEmpty(listAccountId));
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		Date fdate = null, tdate = null;
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			fdate = (Date) dt.parse(fromDate);
			tdate = (Date) dt.parse(thruDate);
		}
		cond.and(Condition.make("transaction_date", ">=", getSqlFromDate(fdate)), UtilValidate.isNotEmpty(fdate));
		cond.and(Condition.make("transaction_date", "<=", getSqlThruDate(tdate)), UtilValidate.isNotEmpty(tdate));
		query.where(cond);
		
		if (UtilValidate.isNotEmpty(fdate) && UtilValidate.isNotEmpty(tdate) && UtilValidate.isNotEmpty(glAccountId)) {
			Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()),
					new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
			endingAmount = openingBal.get(BalanceWorker.DEBIT);
		}
		
		try {
			try {
				resultSet = query.getResultSet();
			} catch (GenericDataSourceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				while (resultSet.next()) {
					BigDecimal crAmount = resultSet.getBigDecimal("cr_amount");
					BigDecimal drAmount = resultSet.getBigDecimal("dr_amount");
					
					endingAmount = endingAmount.subtract(crAmount);
					endingAmount = endingAmount.add(drAmount);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		request.setAttribute("endingAmount", endingAmount);
		return "success";
	}
	
	protected static Timestamp getSqlFromDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}

	protected static Timestamp getSqlThruDate(Date date) {
		if (date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Timestamp sqlDate = new Timestamp(calendar.getTimeInMillis());
		return sqlDate;
	}
}