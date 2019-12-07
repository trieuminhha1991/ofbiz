package com.olbius.acc.report.summary;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.report.financialstm.Fomular;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.MultiOrganizationUtil;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GeneralLedgerServices {
	public static final String module = GeneralLedgerServices.class.getName();
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getGeneralLedger(DispatchContext dctx, Map<String, Object> context) throws ParseException {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listGLs = new ArrayList<Map<String, Object>>();
		
		//Get parameters
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String fromDate = (String) parameters.get("fromDate")[0];
		String thruDate = (String) parameters.get("thruDate")[0];
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		try {
			//Set condition
			String	glAccountId = null;
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			Date fdate = (Date) dt.parse(fromDate);
			Date tdate = (Date) dt.parse(thruDate);
			
			Map<String, Object> firstMIB = new HashMap<String, Object>();
			String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale)context.get("locale"));
			firstMIB.put("voucherDescription", openingBalMess);
			
			if(parameters.get("glAccountId") != null && 
					!((String)parameters.get("glAccountId")[0]).equals("undefined") ||
					parameters.get("glAccountId") != null &&
					!((String)parameters.get("glAccountId")[0]).equals("undefined")) {
				
				listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
								
				//Set glAccountCode
				glAccountId = (String) parameters.get("glAccountId")[0];
				
				Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
				BigDecimal drAmount = openingBal.get(BalanceWorker.DEBIT);
				BigDecimal crAmount = openingBal.get(BalanceWorker.CREDIT);
				if(crAmount.compareTo(BigDecimal.ZERO) < 0) {
				    drAmount = crAmount.negate();
				    crAmount = BigDecimal.ZERO;
                }
                if (drAmount.compareTo(BigDecimal.ZERO) < 0) {
				    crAmount = drAmount.negate();
				    drAmount = BigDecimal.ZERO;
                }
				firstMIB.put("debitAmount", drAmount);
				firstMIB.put("creditAmount", crAmount);
				listGLs.add(firstMIB);

                List<Object> listAccountId = FastList.newInstance();
                Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
                List<Account> listAccount = acc.getListChild();
                for(Account item : listAccount) {
                    AccountEntity accEntity = item.getAcc();
                    listAccountId.add(accEntity.getGlAccountId());
                }
                listAccountId.add(glAccountId);
                listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));

                //Get General Ledger
				listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
//				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
				
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
				
				List<GenericValue> listAccTransFacts = delegator.findList("AcctgDocumentListFact", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
				
				if (UtilValidate.isNotEmpty(listAccTransFacts)) {
					for (GenericValue item : listAccTransFacts) {
						Map<String, Object> genLedger = new HashMap<String, Object>();
						genLedger.put("transDate", item.getDate("transactionDate"));					
						genLedger.put("glAccountCode", item.getString("accountCode"));
						genLedger.put("recipGlAccountCode", item.getString("accountRecipCode"));
						genLedger.put("currencyId", item.getString("currencyId"));
						genLedger.put("voucherDate", item.getTimestamp("documentDate"));
						genLedger.put("voucherID", item.getString("documentId") == null ? item.getString("documentNumber") : (item.getString("documentId") + item.getString("documentNumber")));
						String parentType = item.getString("documentParentType") == null ? "" : item.getString("documentParentType");
						String description = item.getString("description");
						String voucherDescription = "";
						if (UtilValidate.isNotEmpty(description)) {
							voucherDescription += description;
						}
						switch (parentType) {
							case VoucherType.PAYMENT_TYPE:
								GenericValue voucherType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("documentType")), false);
								if (UtilValidate.isNotEmpty(voucherType.getString("description"))) {
									voucherDescription += " [" + voucherType.get("description", locale) + "]";
								}
								genLedger.put("voucherDescription", voucherDescription);
								break;
							case VoucherType.INVOICE_TYPE:
								voucherType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", item.getString("documentType")), false);
								if (UtilValidate.isNotEmpty(voucherType.getString("description"))) {
									voucherDescription += " [" + voucherType.get("description", locale) + "]";
								}
								genLedger.put("voucherDescription", voucherDescription);
								break;
							case VoucherType.DELIVERY_TYPE:
								voucherType = delegator.findOne("DeliveryType", UtilMisc.toMap("deliveryTypeId", item.getString("documentType")), false);
								if (UtilValidate.isNotEmpty(voucherType.getString("description"))) {
									voucherDescription += " [" + voucherType.get("description", locale) + "]";
								}
								genLedger.put("voucherDescription", voucherDescription);
								break;
							default:
								break;
						}
						genLedger.put("creditAmount", item.getBigDecimal("crAmount"));
						genLedger.put("debitAmount", item.getBigDecimal("drAmount"));
						//FIXME 
						//Description, voucherID, voucherDate
						listGLs.add(genLedger);
					}
				}
			}
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result.put("listIterator", listGLs);
		result.put("TotalRows", String.valueOf(listGLs.size()));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAnalysisReport(DispatchContext dctx, Map<String, Object> context) throws ParseException {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listARs = new ArrayList<Map<String, Object>>();
		
		//Get parameters
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String	fromDate = (String) parameters.get("fromDate")[0];
		String	thruDate = (String) parameters.get("thruDate")[0];
		Locale locale = (Locale) context.get("locale");
		try {
			if (UtilValidate.isEmpty(listSortFields)) {
				listSortFields.add("-transactionDate");
			}
			//Set condition
			String	glAccountId = null;
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			Date fdate = (Date) dt.parse(fromDate);
			Date tdate = (Date) dt.parse(thruDate);
			
			if(parameters.get("glAccountId") != null && 
					!((String)parameters.get("glAccountId")[0]).equals("undefined") ||
					parameters.get("glAccountId") != null &&
					!((String)parameters.get("glAccountId")[0]).equals("undefined")) {
				
				glAccountId = (String) parameters.get("glAccountId")[0];
				//FIXME Replace glAccountId by glAccountCode
				
				listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
				
				// consider all children gl_account
				List<String> allGlAccountChildren = getAllGlAccountChildren(glAccountId, delegator);
				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, allGlAccountChildren));
				
				// consider only the selected gl_account
				//listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.EQUALS, glAccountId));
				
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));								
				//Get Analysis Report
				List<GenericValue> listAccTransFacts = delegator.findList("AcctgDocumentListFact", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
				BigDecimal creditBalAmount = BigDecimal.ZERO;
				BigDecimal debitBalAmount = BigDecimal.ZERO;
				Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
				Map<String, Object> firstPDB = new HashMap<String, Object>();
				String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale)context.get("locale"));
				firstPDB.put("voucherDescription", openingBalMess);
				firstPDB.put("creditBalAmount", openingBal.get(BalanceWorker.CREDIT));
				firstPDB.put("debitBalAmount", openingBal.get(BalanceWorker.DEBIT));
				listARs.add(firstPDB);
				
				if (BigDecimal.ZERO.compareTo(openingBal.get(BalanceWorker.DEBIT)) != 0) {
					debitBalAmount = openingBal.get(BalanceWorker.DEBIT);
				} else if (BigDecimal.ZERO.compareTo(openingBal.get(BalanceWorker.CREDIT)) != 0) {
					creditBalAmount = openingBal.get(BalanceWorker.CREDIT);
				}
				
				if (UtilValidate.isNotEmpty(listAccTransFacts)) {
					for (GenericValue item : listAccTransFacts) {
						Map<String, Object> analysisReport = new HashMap<String, Object>();
						analysisReport.put("acctgTransId", item.getString("acctgTransId"));
						analysisReport.put("documentId", item.getString("documentId"));
						analysisReport.put("documentNumber", item.getString("documentNumber"));
						analysisReport.put("voucherCode", item.getString("voucherCode"));
						analysisReport.put("transDate", item.getDate("transactionDate"));					
						analysisReport.put("glAccountCode", item.getString("accountCode"));
						analysisReport.put("recipGlAccountCode", item.getString("accountRecipCode"));
						analysisReport.put("currencyId", item.getString("currencyId"));
						analysisReport.put("voucherDate", item.getTimestamp("documentDate"));
						analysisReport.put("voucherID", item.getString("documentId") == null ? item.getString("documentNumber") : (item.getString("documentId") + item.getString("documentNumber")));
						String parentType = item.getString("documentParentType") == null ? "" : item.getString("documentParentType");
						String description = item.getString("description");
						String voucherDescription = "";
						if (UtilValidate.isNotEmpty(description)) {
							voucherDescription += description;
						}
						switch (parentType) {
							case VoucherType.PAYMENT_TYPE:
								GenericValue voucherType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("documentType")), false);
								if (UtilValidate.isNotEmpty(voucherType.getString("description"))) {
									voucherDescription += " [" + voucherType.get("description", locale) + "]";
								}
								analysisReport.put("voucherDescription", voucherDescription);
								break;
							case VoucherType.INVOICE_TYPE:
								voucherType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", item.getString("documentType")), false);
								if (UtilValidate.isNotEmpty(voucherType.getString("description"))) {
									voucherDescription += " [" + voucherType.get("description", locale) + "]";
								}
								analysisReport.put("voucherDescription", voucherDescription);
								break;
							case VoucherType.DELIVERY_TYPE:
								voucherType = delegator.findOne("DeliveryType", UtilMisc.toMap("deliveryTypeId", item.getString("documentType")), false);
								if (UtilValidate.isNotEmpty(voucherType.getString("description"))) {
									voucherDescription += " [" + voucherType.get("description", locale) + "]";
								}
								analysisReport.put("voucherDescription", voucherDescription);
								break;
							default:
								break;
						}
						//FIXME
						
						analysisReport.put("creditAmount", item.getBigDecimal("crAmount"));
						analysisReport.put("debitAmount", item.getBigDecimal("drAmount"));
						
						if (item.getBigDecimal("crAmount").compareTo(BigDecimal.ZERO) > 0)
						creditBalAmount = creditBalAmount.add(item.getBigDecimal("crAmount"));
						debitBalAmount = debitBalAmount.add(item.getBigDecimal("drAmount"));
						
						if (creditBalAmount.compareTo(debitBalAmount) > 0) {
							analysisReport.put("debitBalAmount", BigDecimal.ZERO);
							analysisReport.put("creditBalAmount", creditBalAmount.subtract(debitBalAmount));
						} else {
							analysisReport.put("debitBalAmount", debitBalAmount.subtract(creditBalAmount));
							analysisReport.put("creditBalAmount", BigDecimal.ZERO);
						}
						listARs.add(analysisReport);
					}
				}
			}
		} catch (GenericEntityException | NoSuchAlgorithmException e) {
			ErrorUtils.processException(e, e.getMessage());
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", listARs);
		result.put("TotalRows", String.valueOf(listARs.size()));
		return result;
	}
	
	public String getTotalGenLedger(HttpServletRequest request, HttpServletResponse response) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		
		BigDecimal totalPostingDebit = BigDecimal.ZERO;
		BigDecimal totalPostingCredit = BigDecimal.ZERO;
		BigDecimal totalEnding = BigDecimal.ZERO;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			BigDecimal balCrAmount = BigDecimal.ZERO;
			BigDecimal balDrAmount = BigDecimal.ZERO;
			
			//date
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
				Date fdate = (Date) dt.parse(fromDate);
				Date tdate = (Date) dt.parse(thruDate);
				
				Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
				balCrAmount = openingBal.get(BalanceWorker.CREDIT);
				balDrAmount = openingBal.get(BalanceWorker.DEBIT);
				if(balCrAmount.compareTo(BigDecimal.ZERO) < 0) {
				    balDrAmount = balCrAmount.negate();
				    balCrAmount = BigDecimal.ZERO;
                }
                if(balDrAmount.compareTo(BigDecimal.ZERO) < 0) {
				    balCrAmount = balDrAmount.negate();
				    balDrAmount = BigDecimal.ZERO;
                }
				
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			}
			
			BigDecimal openingCreditAmount = balCrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balCrAmount : balCrAmount.negate();
			BigDecimal openingDebitAmount = balDrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balDrAmount : balDrAmount.negate();
			totalPostingDebit = totalPostingDebit.add(openingDebitAmount);
			totalPostingCredit = totalPostingCredit.add(openingCreditAmount);
			
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			if (UtilValidate.isNotEmpty(glAccountId)) {
                List<String> listChildGlAcc = Fomular.getAllGlAccountIdChildren(glAccountId, delegator);
                listChildGlAcc.add(glAccountId);
				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listChildGlAcc));
			}

			List<GenericValue> listAccTransFacts = delegator.findList("AcctgDocumentListFact", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			for (GenericValue item: listAccTransFacts) {
				BigDecimal crAmount = item.getBigDecimal("crAmount");
				BigDecimal drAmount = item.getBigDecimal("drAmount");
				totalPostingDebit = totalPostingDebit.add(drAmount);
				totalPostingCredit = totalPostingCredit.add(crAmount);
			}
			totalEnding = totalPostingCredit.subtract(totalPostingDebit);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("totalPostingCredit", totalPostingCredit);
		request.setAttribute("totalPostingDebit", totalPostingDebit);
		request.setAttribute("totalEnding", totalEnding);
		return "success";
	}
	// get all children of all levels
		public static List<String> getAllGlAccountChildren(String strGlAccountId,
				Delegator delegator) throws GenericEntityException {
			List<String> listData = new ArrayList<String>();
			List<GenericValue> listTmp = delegator.findList("GlAccount",
					EntityCondition.makeCondition("parentGlAccountId",
							EntityOperator.EQUALS, strGlAccountId), UtilMisc
							.toSet("glAccountId"), null, null, false);
			if (listTmp != null) {
				for (GenericValue genericValue : listTmp) {
					listData.add((String) genericValue.get("glAccountId"));
					listData.addAll(getAllGlAccountChildren(
							(String) genericValue.get("glAccountId"), delegator));
				}
			}
			return listData;
		}
	public String getTotalAnalysisReport(HttpServletRequest request, HttpServletResponse response) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		
		BigDecimal totalPostingDebit = BigDecimal.ZERO;
		BigDecimal totalPostingCredit = BigDecimal.ZERO;
		BigDecimal totalEnding = BigDecimal.ZERO;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		Debug.log(module + "::getTotalAnalysisReport, gl_account " + glAccountId + ", organization = " + organizationPartyId);
		
		
		try {
			BigDecimal creditBalAmount = BigDecimal.ZERO;
			BigDecimal debitBalAmount = BigDecimal.ZERO;
			
			//date
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
				Date fdate = (Date) dt.parse(fromDate);
				Date tdate = (Date) dt.parse(thruDate);
				
				
				Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningFromTo(glAccountId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
				
				if (BigDecimal.ZERO.compareTo(openingBal.get(BalanceWorker.DEBIT)) != 0) {
					debitBalAmount = openingBal.get(BalanceWorker.DEBIT);
				} else if (BigDecimal.ZERO.compareTo(openingBal.get(BalanceWorker.CREDIT)) != 0) {
					creditBalAmount = openingBal.get(BalanceWorker.CREDIT);
				}
				
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			}
			
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			if (UtilValidate.isNotEmpty(glAccountId)) {
			
				//consider all gl_account including children
				List<String> allGlAccountChildren = getAllGlAccountChildren(glAccountId, delegator);
				allGlAccountChildren.add(glAccountId);
				for(String gl: allGlAccountChildren){
					Debug.log(module + "::getTotalAnalysisReport, PQD, child gl_account " + gl);
				}
				
				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, allGlAccountChildren));
				
				// consider only the selected gl_account
				//listAllConditions.add(EntityCondition.makeCondition("glAccountId", glAccountId));
				
			
			}
			
			List<GenericValue> listAccTransFacts = delegator.findList("AcctgDocumentListFact", 
					EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			
			Debug.log(module + "::getTotalAnalysisReport, PQD, total list = " + listAccTransFacts.size());
					
			for (GenericValue item: listAccTransFacts) {
				BigDecimal crAmount = item.getBigDecimal("crAmount");
				BigDecimal drAmount = item.getBigDecimal("drAmount");
				totalPostingDebit = totalPostingDebit.add(drAmount);
				totalPostingCredit = totalPostingCredit.add(crAmount);
				
				creditBalAmount = creditBalAmount.add(item.getBigDecimal("crAmount"));
				debitBalAmount = debitBalAmount.add(item.getBigDecimal("drAmount"));
			}
			totalEnding = creditBalAmount.subtract(debitBalAmount);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		request.setAttribute("totalPostingCredit", totalPostingCredit);
		request.setAttribute("totalPostingDebit", totalPostingDebit);
		request.setAttribute("totalEnding", totalEnding);
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