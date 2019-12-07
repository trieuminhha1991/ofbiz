package com.olbius.acc.report.summary;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.bi.olap.query.OlbiusQuery;
import com.olbius.bi.olap.query.condition.Condition;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
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
import com.olbius.common.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LiabilityBalServices {

    private static final String LIABILITY_RECEIVE = "131";
    private static final String LIABILITY_PAYABLE = "331";

    @SuppressWarnings("unchecked")
    public Map<String, Object> getLiabilityBalance(DispatchContext dctx, Map<String, Object> context) throws ParseException {
        Delegator delegator = dctx.getDelegator();
        List<Map<String, Object>> listGLs = new ArrayList<Map<String, Object>>();
        //Get parameters
        Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");

        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        String fromDate = null;
        if (parameters.containsKey("fromDate") && UtilValidate.isNotEmpty(parameters.get("fromDate"))) {
            fromDate = parameters.get("fromDate")[0];
        }
        String thruDate = null;
        if (parameters.containsKey("thruDate") && UtilValidate.isNotEmpty(parameters.get("thruDate"))) {
            thruDate = parameters.get("thruDate")[0];
        }
        String glAccountId = null;
        if (parameters.containsKey("glAccountId") && UtilValidate.isNotEmpty(parameters.get("glAccountId"))) {
            glAccountId = parameters.get("glAccountId")[0];
        }
        String partyId = null;
        if (parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId"))) {
            partyId = parameters.get("partyId")[0];
        }

        try {
            Map<String, Object> firstMIB = new HashMap<String, Object>();
            String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale)context.get("locale"));
            firstMIB.put("voucherDescription", openingBalMess);
            BigDecimal balCrAmount = BigDecimal.ZERO;
            BigDecimal balDrAmount = BigDecimal.ZERO;

            //date
            SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy");
            if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
                Date fdate = dt.parse(fromDate);
                Date tdate = dt.parse(thruDate);

                if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(glAccountId)) {
                    Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
                    balCrAmount = openingBal.get(BalanceWorker.CREDIT);
                    balDrAmount = openingBal.get(BalanceWorker.DEBIT);
                }

                listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
                listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
            }
            if(balCrAmount.compareTo(BigDecimal.ZERO) < 0) {
                balDrAmount = balCrAmount.negate();
                balCrAmount = BigDecimal.ZERO;
            }
            if(balDrAmount.compareTo(BigDecimal.ZERO) < 0) {
                balCrAmount = balDrAmount.negate();
                balDrAmount = BigDecimal.ZERO;
            }

            firstMIB.put("openingCreditAmount", balCrAmount);
            firstMIB.put("openingDebitAmount", balDrAmount);
            listGLs.add(firstMIB);

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

            listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
            if (UtilValidate.isNotEmpty(listAccountId)) {
                listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));
            }
            if (UtilValidate.isNotEmpty(partyId)) {
                listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
            }

            List<GenericValue> listAccTransFacts = delegator.findList("AcctgInvoiceVoucherFact", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
            for(GenericValue item: listAccTransFacts) {
                Map<String, Object> itemMap = new HashMap<String, Object>();
                itemMap.put("partyId", item.getString("partyId"));
                itemMap.put("partyCode", item.getString("partyCode"));
                itemMap.put("partyName", item.getString("partyName"));
                itemMap.put("glAccountId", item.getString("glAccountId"));
                itemMap.put("accountCode", item.getString("accountCode"));
                itemMap.put("currencyId", item.getString("currencyId"));
                itemMap.put("voucherCode", item.getString("voucherNumber"));
                itemMap.put("voucherDate", item.getDate("issuedDate"));
                itemMap.put("voucherDescription", item.getString("description"));
                itemMap.put("facilityId", item.getString("facilityId"));
                itemMap.put("facilityName", item.getString("facilityName"));

                String parentType = item.getString("documentParentType") == null ? "" : item.getString("documentParentType");
                switch (parentType) {
                    case VoucherType.PAYMENT_TYPE:
                        GenericValue paymentType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("documentType")), false);
                        itemMap.put("voucherType", paymentType.get("description", locale));
                        break;
                    case VoucherType.INVOICE_TYPE:
                        GenericValue invoiceType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", item.getString("documentType")), false);
                        itemMap.put("voucherType", invoiceType.get("description", locale));
                        break;
                    default:
                        break;
                }

                BigDecimal crAmount = item.getBigDecimal("crAmount");
                BigDecimal drAmount = item.getBigDecimal("drAmount");

                if (UtilValidate.isNotEmpty(drAmount) && UtilValidate.isNotEmpty(crAmount)
                        && crAmount.compareTo(drAmount) >= 0) {
                    BigDecimal postingCreditAmount = item.getBigDecimal("totalAmountVoucher");
                    itemMap.put("postingCreditAmount", postingCreditAmount);
                } else {
                    BigDecimal postingDebitAmount = item.getBigDecimal("totalAmountVoucher");
                    itemMap.put("postingDebitAmount", postingDebitAmount);
                }

                listGLs.add(itemMap);
            }
        } catch (GenericEntityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("listIterator", UtilMisc.toList(listGLs));
        return result;
    }
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLiabilityReceivable(DispatchContext dctx, Map<String, Object> context) throws ParseException {
		Delegator delegator = dctx.getDelegator();
		List<Map<String, Object>> listGLs = new ArrayList<Map<String, Object>>();
		//Get parameters
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");

		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String fromDate = null;
		if (parameters.containsKey("fromDate") && UtilValidate.isNotEmpty(parameters.get("fromDate"))) {
			fromDate = parameters.get("fromDate")[0];
		}
		String thruDate = null;
		if (parameters.containsKey("thruDate") && UtilValidate.isNotEmpty(parameters.get("thruDate"))) {
			thruDate = parameters.get("thruDate")[0];
		}
		String glAccountId = null;
		if (parameters.containsKey("glAccountId") && UtilValidate.isNotEmpty(parameters.get("glAccountId"))) {
			glAccountId = parameters.get("glAccountId")[0];
		}
		String partyId = null;
		if (parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId"))) {
			partyId = parameters.get("partyId")[0];
		}
		
		try {
			Map<String, Object> firstMIB = new HashMap<String, Object>();
			String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale)context.get("locale"));
			firstMIB.put("voucherDescription", openingBalMess);
			BigDecimal balCrAmount = BigDecimal.ZERO;
			BigDecimal balDrAmount = BigDecimal.ZERO;
			
			//date
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
				Date fdate = (Date) dt.parse(fromDate);
				Date tdate = (Date) dt.parse(thruDate);
				
				if (UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(partyId)) {
					Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
					balCrAmount = openingBal.get(BalanceWorker.CREDIT);
					balDrAmount = openingBal.get(BalanceWorker.DEBIT);
				}
				
				listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			}

            if(balCrAmount.compareTo(BigDecimal.ZERO) < 0) {
                balDrAmount = balCrAmount.negate();
                balCrAmount = BigDecimal.ZERO;
            }
            if(balDrAmount.compareTo(BigDecimal.ZERO) < 0) {
                balCrAmount = balDrAmount.negate();
                balDrAmount = BigDecimal.ZERO;
            }
			
			firstMIB.put("openingCreditAmount", balCrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balCrAmount : balCrAmount.negate());
			firstMIB.put("openingDebitAmount", balDrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balDrAmount : balDrAmount.negate());
			listGLs.add(firstMIB);
			
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
			
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			if (UtilValidate.isNotEmpty(listAccountId)) {
				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			}
			
			List<GenericValue> listAccTransFacts = delegator.findList("AcctgWorkShiftFact", EntityCondition.makeCondition(listAllConditions), null, listSortFields, null, false);
			for(GenericValue item: listAccTransFacts) {
				Map<String, Object> itemMap = new HashMap<String, Object>();
				itemMap.put("partyId", item.getString("partyId"));
				itemMap.put("partyCode", item.getString("partyCode"));
				itemMap.put("partyName", item.getString("partyName"));
				itemMap.put("glAccountId", item.getString("glAccountId"));
				itemMap.put("accountCode", item.getString("accountCode"));
				itemMap.put("currencyId", item.getString("currencyId"));
				itemMap.put("voucherCode", item.getString("voucherNumber"));
				itemMap.put("voucherDate", item.getDate("issuedDate"));
				itemMap.put("voucherDescription", item.getString("description"));
				itemMap.put("facilityId", item.getString("facilityId"));
				itemMap.put("facilityName", item.getString("facilityName"));
				
				String parentType = item.getString("documentParentType") == null ? "" : item.getString("documentParentType");
				switch (parentType) {
					case VoucherType.PAYMENT_TYPE:
						GenericValue paymentType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("documentType")), false);
						itemMap.put("voucherType", paymentType.get("description", locale));
						break;
					case VoucherType.INVOICE_TYPE:
						GenericValue invoiceType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", item.getString("documentType")), false);
						itemMap.put("voucherType", invoiceType.get("description", locale));
						break;
					default:
						break;
				}
				itemMap.put("postingCreditAmount", BigDecimal.ZERO);
				itemMap.put("postingDebitAmount", BigDecimal.ZERO);
				BigDecimal endingCrAmount = BigDecimal.ZERO;
				BigDecimal endingDrAmount = BigDecimal.ZERO;
                if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
                    Date fdate = (Date) dt.parse(fromDate);
                    Date tdate = (Date) dt.parse(thruDate);

                    if (UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(item.getString("partyId"))) {
                        Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(item.getString("glAccountId"), item.getString("partyId"), new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
                        balCrAmount = openingBal.get(BalanceWorker.CREDIT);
                        balDrAmount = openingBal.get(BalanceWorker.DEBIT);
                        endingCrAmount = endingCrAmount.add(balCrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balCrAmount : balCrAmount.negate());
                        endingDrAmount = endingDrAmount.add(balDrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balDrAmount : balDrAmount.negate());
                    }
                }
				
				BigDecimal crAmount = item.getBigDecimal("crAmount");
				BigDecimal drAmount = item.getBigDecimal("drAmount");
				
				if (UtilValidate.isNotEmpty(drAmount) && UtilValidate.isNotEmpty(crAmount)) {
					itemMap.put("postingCreditAmount", crAmount);
					itemMap.put("postingDebitAmount", drAmount);
					endingCrAmount = endingCrAmount.add(crAmount);
					endingDrAmount = endingDrAmount.add(drAmount);
				}
				itemMap.put("endingCreditAmount", endingCrAmount);
				itemMap.put("endingDebitAmount", endingDrAmount);
				listGLs.add(itemMap);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", UtilMisc.toList(listGLs));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLiabilitySupplier(DispatchContext dctx, Map<String, Object> context) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listResult = FastList.newInstance();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz.olap"));
		ResultSet resultSet = null;
		OlbiusQuery query = new OlbiusQuery(processor);

		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String fromDate = null;
		if (parameters.containsKey("fromDate") && UtilValidate.isNotEmpty(parameters.get("fromDate"))) {
			fromDate = parameters.get("fromDate")[0];
		}
		String thruDate = null;
		if (parameters.containsKey("thruDate") && UtilValidate.isNotEmpty(parameters.get("thruDate"))) {
			thruDate = parameters.get("thruDate")[0];
		}
		String glAccountId = null;
		if (parameters.containsKey("glAccountId") && UtilValidate.isNotEmpty(parameters.get("glAccountId"))) {
			glAccountId = parameters.get("glAccountId")[0];
		}
		
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
		
		query.select("party_id").select("party_name").select("party_code")
			.select("sum(dr_amount)", "dr_amount").select("sum(cr_amount)", "cr_amount");
		query.from("acctg_invoice_voucher_fact");
		Condition cond = new Condition();
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		cond.and(Condition.makeIn("gl_account_id", listAccountId), UtilValidate.isNotEmpty(listAccountId));
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		Date fdate = null, tdate = null;
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			fdate = (Date) dt.parse(fromDate);
			tdate = (Date) dt.parse(thruDate);
		}
		cond.and(Condition.make("voucher_created_date", ">=", getSqlFromDate(fdate)), UtilValidate.isNotEmpty(fdate));
		cond.and(Condition.make("voucher_created_date", "<=", getSqlThruDate(tdate)), UtilValidate.isNotEmpty(tdate));
		query.where(cond);
		query.groupBy("party_id").groupBy("party_name").groupBy("party_code");
		
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
					Map<String, Object> map = FastMap.newInstance();
					String partyId = resultSet.getString("party_id");
					map.put("partyId", partyId);
					map.put("partyCode", resultSet.getString("party_code"));
					map.put("partyName", resultSet.getString("party_name"));
					BigDecimal openingCreditAmount = BigDecimal.ZERO;
					BigDecimal openingDebitAmount = BigDecimal.ZERO;
					BigDecimal postingCreditAmount = resultSet.getBigDecimal("cr_amount");
					BigDecimal postingDebitAmount = resultSet.getBigDecimal("dr_amount");
					BigDecimal endingCreditAmount = BigDecimal.ZERO;
					BigDecimal endingDebitAmount = BigDecimal.ZERO;
					
					if (UtilValidate.isNotEmpty(fdate) && UtilValidate.isNotEmpty(tdate) && UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(partyId)) {
						Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()),
								new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
						openingCreditAmount = openingBal.get(BalanceWorker.CREDIT);
						openingDebitAmount = openingBal.get(BalanceWorker.DEBIT);
						if (openingDebitAmount.compareTo(BigDecimal.ZERO) < 0) {
							openingDebitAmount = openingDebitAmount.negate();
						}
					}
					
					BigDecimal endingAmount = openingCreditAmount.add(postingCreditAmount)
							.subtract(openingDebitAmount).subtract(postingDebitAmount);
					
					if (endingAmount.compareTo(BigDecimal.ZERO) >= 0) {
						endingCreditAmount = endingAmount;
					} else {
						endingDebitAmount = endingAmount.negate();
					}
					
					map.put("openingCreditAmount", openingCreditAmount);
					map.put("openingDebitAmount", openingDebitAmount);
					map.put("postingCreditAmount", postingCreditAmount);
					map.put("postingDebitAmount", postingDebitAmount);
					map.put("endingCreditAmount", endingCreditAmount);
					map.put("endingDebitAmount", endingDebitAmount);
					listResult.add(map);
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
		
		result.put("listIterator", UtilMisc.toList(listResult));
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getLiabilityReceivableTotal(DispatchContext dctx, Map<String, Object> context) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		List<Map<String, Object>> listResult = FastList.newInstance();
		Delegator delegator = dctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz.olap"));
		ResultSet resultSet = null;
		OlbiusQuery query = new OlbiusQuery(processor);

		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String fromDate = null;
		if (parameters.containsKey("fromDate") && UtilValidate.isNotEmpty(parameters.get("fromDate"))) {
			fromDate = parameters.get("fromDate")[0];
		}
		String thruDate = null;
		if (parameters.containsKey("thruDate") && UtilValidate.isNotEmpty(parameters.get("thruDate"))) {
			thruDate = parameters.get("thruDate")[0];
		}
		String glAccountId = null;
		if (parameters.containsKey("glAccountId") && UtilValidate.isNotEmpty(parameters.get("glAccountId"))) {
			glAccountId = parameters.get("glAccountId")[0];
		}
		
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
		
		query.select("party_id").select("party_name").select("party_code")
			.select("sum(dr_amount)", "dr_amount").select("sum(cr_amount)", "cr_amount");
		query.from("acctg_work_shift_fact");
		Condition cond = new Condition();
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		cond.and(Condition.makeIn("gl_account_id", listAccountId), UtilValidate.isNotEmpty(listAccountId));
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		Date fdate = null, tdate = null;
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			fdate = (Date) dt.parse(fromDate);
			tdate = (Date) dt.parse(thruDate);
		}
		cond.and(Condition.make("voucher_created_date", ">=", getSqlFromDate(fdate)), UtilValidate.isNotEmpty(fdate));
		cond.and(Condition.make("voucher_created_date", "<=", getSqlThruDate(tdate)), UtilValidate.isNotEmpty(tdate));
		query.where(cond);
		query.groupBy("party_id").groupBy("party_name").groupBy("party_code");
		
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
					Map<String, Object> map = FastMap.newInstance();
					String partyId = resultSet.getString("party_id");
					map.put("partyId", partyId);
					map.put("partyCode", resultSet.getString("party_code"));
					map.put("partyName", resultSet.getString("party_name"));
					BigDecimal openingCreditAmount = BigDecimal.ZERO;
					BigDecimal openingDebitAmount = BigDecimal.ZERO;
					BigDecimal postingCreditAmount = resultSet.getBigDecimal("cr_amount");
					BigDecimal postingDebitAmount = resultSet.getBigDecimal("dr_amount");
					BigDecimal endingCreditAmount = BigDecimal.ZERO;
					BigDecimal endingDebitAmount = BigDecimal.ZERO;
					
					if (UtilValidate.isNotEmpty(fdate) && UtilValidate.isNotEmpty(tdate) && UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(partyId)) {
						Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()),
								new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
						openingCreditAmount = openingBal.get(BalanceWorker.CREDIT);
						openingDebitAmount = openingBal.get(BalanceWorker.DEBIT);
						if (openingDebitAmount.compareTo(BigDecimal.ZERO) < 0) {
							openingDebitAmount = openingDebitAmount.negate();
						}
					}
					
					BigDecimal endingAmount = openingCreditAmount.add(postingCreditAmount)
							.subtract(openingDebitAmount).subtract(postingDebitAmount);
					
					if (endingAmount.compareTo(BigDecimal.ZERO) >= 0) {
						endingCreditAmount = endingAmount;
					} else {
						endingDebitAmount = endingAmount.negate();
					}
					
					map.put("openingCreditAmount", openingCreditAmount);
					map.put("openingDebitAmount", openingDebitAmount);
					map.put("postingCreditAmount", postingCreditAmount);
					map.put("postingDebitAmount", postingDebitAmount);
					map.put("endingCreditAmount", endingCreditAmount);
					map.put("endingDebitAmount", endingDebitAmount);
					listResult.add(map);
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
		
		result.put("listIterator", UtilMisc.toList(listResult));
		return result;
	}
	
	public static Map<String, Object> getLiabilityPreferenceInfo(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		//LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Map<String, Object> retMap = ServiceUtil.returnSuccess();
		String partyIdFrom = (String)context.get("partyIdFrom");
		String partyIdTo = (String)context.get("partyIdTo");
		String fromDateStr = (String)context.get("fromDate");
		String thruDateStr = (String)context.get("thruDate");
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			String organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			BigDecimal openingBalance;
			if(organizationId.equals(partyIdFrom))
			    openingBalance = LiabilityPref.getOpeningBalLiability(partyIdTo, organizationId, df.format(fromDate), delegator, LIABILITY_RECEIVE);
			else openingBalance = LiabilityPref.getOpeningBalLiability(partyIdFrom, organizationId, df.format(fromDate), delegator, LIABILITY_PAYABLE);
			//get amount paid
            BigDecimal amountPaid = LiabilityWorker.getTotalAmountPaid(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);
            //get amount not paid
            BigDecimal amountUnpaid = LiabilityWorker.getTotalAmountUnpaid(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);
            amountUnpaid = amountUnpaid.add(openingBalance);
            //get total amount other
            BigDecimal totalAmountOther = LiabilityWorker.getTotalAmountOther(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);
			retMap.put("openingBalance", openingBalance);
			retMap.put("amountPaid", amountPaid);
			retMap.put("amountNotPaid", amountUnpaid);
			retMap.put("amountOther", totalAmountOther);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return retMap;
	}

    public static Map<String, Object> getLiabilityPreferenceInfoNew(DispatchContext dctx, Map<String, Object> context){
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue)context.get("userLogin");
        Map<String, Object> retMap = ServiceUtil.returnSuccess();
        String partyIdFrom = (String)context.get("partyIdFrom");
        String partyIdTo = (String)context.get("partyIdTo");
        String fromDateStr = (String)context.get("fromDate");
        String thruDateStr = (String)context.get("thruDate");
        Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
        Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            String organizationId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            BigDecimal openingBalance;
            BigDecimal openingDrAmount;
            BigDecimal openingCrAmount;
            BigDecimal endingDrAmount;
            BigDecimal endingCrAmount;
            BigDecimal returnSupplierAmount = BigDecimal.ZERO;
            BigDecimal receiveableAmount = BigDecimal.ZERO;
            if(organizationId.equals(partyIdFrom)) {
                openingBalance = LiabilityPref.getOpeningBalLiability(partyIdTo, organizationId, df.format(fromDate), delegator, LIABILITY_RECEIVE);
                openingDrAmount = openingBalance;
                openingCrAmount	= BigDecimal.ZERO;
            } else {
                openingBalance = LiabilityPref.getOpeningBalLiability(partyIdFrom, organizationId, df.format(fromDate), delegator, LIABILITY_PAYABLE);
                openingDrAmount = BigDecimal.ZERO;
                openingCrAmount	= openingBalance;
            }

            // get return supplier amount
            if(organizationId.equals(partyIdFrom)) {
                returnSupplierAmount = LiabilityWorker.getReturnSupplierAmount(delegator, fromDate, thruDate, partyIdFrom, partyIdTo, "CUST_RTN_INVOICE");
            } else {
                returnSupplierAmount = LiabilityWorker.getReturnSupplierAmount(delegator, fromDate, thruDate, partyIdTo, partyIdFrom, "PURC_RTN_INVOICE");
            }

            // get amount paid
            BigDecimal amountPaid = LiabilityWorker.getTotalAmountPaid(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);

            // get total amount
            BigDecimal totalAmount = LiabilityWorker.getTotalAmount(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);

            // get amount not paid
            BigDecimal amountUnpaid = LiabilityWorker.getTotalAmountUnpaid(delegator, fromDate, thruDate, partyIdFrom, partyIdTo);
            amountUnpaid = amountUnpaid.subtract(returnSupplierAmount);
            amountUnpaid = amountUnpaid.add(openingBalance);
            if(organizationId.equals(partyIdFrom)) {
                endingDrAmount = amountUnpaid;
                endingCrAmount	= BigDecimal.ZERO;
            } else {
                endingDrAmount = BigDecimal.ZERO;
                endingCrAmount	= amountUnpaid;
            }

            retMap.put("openingDrAmount", openingDrAmount);
            retMap.put("openingCrAmount", openingCrAmount);
            retMap.put("returnSupplierAmount", returnSupplierAmount);
            retMap.put("paymentAmount", amountPaid);
            retMap.put("goodsAmount", totalAmount);
            retMap.put("receiveableAmount", receiveableAmount);
            retMap.put("endingDrAmount", endingDrAmount);
            retMap.put("endingCrAmount", endingCrAmount);
            retMap.put("amountNotPaid", amountUnpaid);
            if (amountUnpaid.compareTo(BigDecimal.ZERO) > 0) {
                retMap.put("amountNotPaidText", StringUtil.ConvertDecimalToString(amountUnpaid));
            } else {
                retMap.put("amountNotPaidText", StringUtil.ConvertDecimalToString(amountUnpaid.negate()));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        return retMap;
    }
	
	class EntitySet implements Set<Map<String, Object>>{
		
		private List<Map<String, Object>> backingList = new ArrayList<Map<String, Object>>();
		
		@Override
		public int size() {
			return backingList.size();
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return backingList.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return backingList.contains(o);
		}

		@Override
		public Iterator<Map<String, Object>> iterator() {
			// TODO Auto-generated method stub
			return backingList.iterator();
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return backingList.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean add(Map<String, Object> e) {
			// TODO Auto-generated method stub
			List<Map<String, Object>> tmpList = backingList.subList(0, this.size());
			if(tmpList.isEmpty()) {
				backingList.add(e);
				return true;
			}else {
				for(Map<String, Object> item: tmpList) {
					boolean isValid = false;
					for (String key: e.keySet()) {
						if(!e.get(key).equals(item.get(key))) {
							isValid = true;
						}
					}
					if(!isValid) {
						return false;
					}
				}
				backingList.add(e);
				return true;
			}
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return backingList.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return backingList.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Map<String, Object>> c) {
			for(Map<String, Object> elm: c) {
				this.add(elm);
			}
			return true;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return backingList.retainAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return backingList.removeAll(c);
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			backingList.clear();
		}
	}
	
	public static String getTotalLiabilityBalance(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String partyId = request.getParameter("partyId");
		
		BigDecimal totalPostingDebit = BigDecimal.ZERO;
		BigDecimal totalPostingCredit = BigDecimal.ZERO;
		BigDecimal openingAmount = BigDecimal.ZERO;
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
				
				if (UtilValidate.isNotEmpty(partyId) && UtilValidate.isNotEmpty(glAccountId)) {
					Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
					balCrAmount = openingBal.get(BalanceWorker.CREDIT);
					balDrAmount = openingBal.get(BalanceWorker.DEBIT);
				}
				
				listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			}
			
			BigDecimal openingCreditAmount = balCrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balCrAmount : balCrAmount.negate();
			BigDecimal openingDebitAmount = balDrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balDrAmount : balDrAmount.negate();
			openingAmount = openingCreditAmount.subtract(openingDebitAmount);
			
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
			
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			if (UtilValidate.isNotEmpty(listAccountId)) {
				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			}
			
			List<GenericValue> listAccTransFacts = delegator.findList("AcctgInvoiceVoucherFact", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			for (GenericValue item: listAccTransFacts) {
				BigDecimal crAmount = item.getBigDecimal("crAmount");
				BigDecimal drAmount = item.getBigDecimal("drAmount");
				
				if (UtilValidate.isNotEmpty(drAmount) && UtilValidate.isNotEmpty(crAmount)
						&& crAmount.compareTo(drAmount) >= 0) {
					BigDecimal postingCreditAmount = item.getBigDecimal("totalAmountVoucher");
					totalPostingCredit = totalPostingCredit.add(postingCreditAmount);
				} else {
					BigDecimal postingDebitAmount = item.getBigDecimal("totalAmountVoucher");
					totalPostingDebit = totalPostingDebit.add(postingDebitAmount);
				}
			}
			totalEnding = openingAmount.add(totalPostingCredit).subtract(totalPostingDebit);
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
	
	public static String getTotalLiabilitySupplier(HttpServletRequest request, HttpServletResponse response) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz.olap"));
		ResultSet resultSet = null;
		OlbiusQuery query = new OlbiusQuery(processor);

		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		
		BigDecimal totalOpeningDebit = BigDecimal.ZERO;
		BigDecimal totalOpeningCredit = BigDecimal.ZERO;
		BigDecimal totalPostingDebit = BigDecimal.ZERO;
		BigDecimal totalPostingCredit = BigDecimal.ZERO;
		BigDecimal totalEndingDebit = BigDecimal.ZERO;
		BigDecimal totalEndingCredit = BigDecimal.ZERO;
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
		
		query.select("party_id").select("sum(dr_amount)", "dr_amount").select("sum(cr_amount)", "cr_amount");
		query.from("acctg_invoice_voucher_fact");
		Condition cond = new Condition();
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		cond.and(Condition.makeIn("gl_account_id", listAccountId), UtilValidate.isNotEmpty(listAccountId));
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		Date fdate = null, tdate = null;
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			fdate = (Date) dt.parse(fromDate);
			tdate = (Date) dt.parse(thruDate);
		}
		cond.and(Condition.make("voucher_created_date", ">=", getSqlFromDate(fdate)), UtilValidate.isNotEmpty(fdate));
		cond.and(Condition.make("voucher_created_date", "<=", getSqlThruDate(tdate)), UtilValidate.isNotEmpty(tdate));
		query.where(cond);
		query.groupBy("party_id");
		
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
					BigDecimal openingCreditAmount = BigDecimal.ZERO;
					BigDecimal openingDebitAmount = BigDecimal.ZERO;
					BigDecimal postingCreditAmount = resultSet.getBigDecimal("cr_amount");
					BigDecimal postingDebitAmount = resultSet.getBigDecimal("dr_amount");
					BigDecimal endingCreditAmount = BigDecimal.ZERO;
					BigDecimal endingDebitAmount = BigDecimal.ZERO;
					String partyId = resultSet.getString("party_id");
					
					if (UtilValidate.isNotEmpty(fdate) && UtilValidate.isNotEmpty(tdate) && UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(partyId)) {
						Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()),
								new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
						openingCreditAmount = openingBal.get(BalanceWorker.CREDIT);
						openingDebitAmount = openingBal.get(BalanceWorker.DEBIT);
						if (openingDebitAmount.compareTo(BigDecimal.ZERO) < 0) {
							openingDebitAmount = openingDebitAmount.negate();
						}
					}
					
					BigDecimal endingAmount = openingCreditAmount.add(postingCreditAmount)
							.subtract(openingDebitAmount).subtract(postingDebitAmount);
					
					if (endingAmount.compareTo(BigDecimal.ZERO) >= 0) {
						endingCreditAmount = endingAmount;
					} else {
						endingDebitAmount = endingAmount.negate();
					}
					
					totalOpeningDebit = totalOpeningDebit.add(openingDebitAmount);
					totalOpeningCredit = totalOpeningCredit.add(openingCreditAmount);
					totalPostingDebit = totalPostingDebit.add(postingDebitAmount);
					totalPostingCredit = totalPostingCredit.add(postingCreditAmount);
					totalEndingDebit = totalEndingDebit.add(endingDebitAmount);
					totalEndingCredit = totalEndingCredit.add(endingCreditAmount);
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
		
		request.setAttribute("totalOpeningDebit", totalOpeningDebit);
		request.setAttribute("totalOpeningCredit", totalOpeningCredit);
		request.setAttribute("totalPostingCredit", totalPostingCredit);
		request.setAttribute("totalPostingDebit", totalPostingDebit);
		request.setAttribute("totalEndingDebit", totalEndingDebit);
		request.setAttribute("totalEndingCredit", totalEndingCredit);
		return "success";
	}
	
	public static String getTotalLiabilityReceivable(HttpServletRequest request, HttpServletResponse response) throws ParseException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		
		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		String partyId = request.getParameter("partyId");
		
		BigDecimal totalPostingDebit = BigDecimal.ZERO;
		BigDecimal totalPostingCredit = BigDecimal.ZERO;
		BigDecimal openingAmount = BigDecimal.ZERO;
		BigDecimal totalEnding = BigDecimal.ZERO;
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		
		try {
			BigDecimal balCrAmount = BigDecimal.ZERO;
			BigDecimal balDrAmount = BigDecimal.ZERO;
			
			//date
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
			if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
				Date fdate = dt.parse(fromDate);
				Date tdate = dt.parse(thruDate);
				
				if (UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(partyId)) {
					Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()), new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
					balCrAmount = openingBal.get(BalanceWorker.CREDIT);
					balDrAmount = openingBal.get(BalanceWorker.DEBIT);
				}
				
				listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.LESS_THAN_EQUAL_TO,  new java.sql.Date(tdate.getTime())));
				listAllConditions.add(EntityCondition.makeCondition("voucherCreatedDate", EntityOperator.GREATER_THAN_EQUAL_TO,  new java.sql.Date(fdate.getTime())));
			}
			
			BigDecimal openingCreditAmount = balCrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balCrAmount : balCrAmount.negate();
			BigDecimal openingDebitAmount = balDrAmount.compareTo(BigDecimal.ZERO) >= 0 ? balDrAmount : balDrAmount.negate();
			openingAmount = openingCreditAmount.subtract(openingDebitAmount);
			
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
			
			listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
			if (UtilValidate.isNotEmpty(listAccountId)) {
				listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountId));
			}
			if (UtilValidate.isNotEmpty(partyId)) {
				listAllConditions.add(EntityCondition.makeCondition("partyId", partyId));
			}
			
			List<GenericValue> listAccTransFacts = delegator.findList("AcctgWorkShiftFact", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
			for (GenericValue item: listAccTransFacts) {
				BigDecimal crAmount = item.getBigDecimal("crAmount");
				BigDecimal drAmount = item.getBigDecimal("drAmount");
				
				if (UtilValidate.isNotEmpty(drAmount) && UtilValidate.isNotEmpty(crAmount)) {
					totalPostingCredit = totalPostingCredit.add(crAmount);
					totalPostingDebit = totalPostingDebit.add(drAmount);
				}
			}
			totalEnding = openingAmount.add(totalPostingCredit).subtract(totalPostingDebit);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        request.setAttribute("totalOpeningCredit", BigDecimal.ZERO);
        request.setAttribute("totalOpeningDebit", BigDecimal.ZERO);
        request.setAttribute("totalPostingCredit", totalPostingCredit);
		request.setAttribute("totalPostingDebit", totalPostingDebit);
		request.setAttribute("totalEnding", totalEnding);
		return "success";
	}
	
	public static String getTotalLiabilityReceivableTotal(HttpServletRequest request, HttpServletResponse response) throws ParseException, NoSuchAlgorithmException, GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		SQLProcessor processor = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz.olap"));
		ResultSet resultSet = null;
		OlbiusQuery query = new OlbiusQuery(processor);

		String glAccountId = request.getParameter("glAccountId");
		String fromDate = request.getParameter("fromDate");
		String thruDate = request.getParameter("thruDate");
		
		BigDecimal totalOpeningDebit = BigDecimal.ZERO;
		BigDecimal totalOpeningCredit = BigDecimal.ZERO;
		BigDecimal totalPostingDebit = BigDecimal.ZERO;
		BigDecimal totalPostingCredit = BigDecimal.ZERO;
		BigDecimal totalEndingDebit = BigDecimal.ZERO;
		BigDecimal totalEndingCredit = BigDecimal.ZERO;
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
		
		query.select("party_id").select("sum(dr_amount)", "dr_amount").select("sum(cr_amount)", "cr_amount");
		query.from("acctg_work_shift_fact");
		Condition cond = new Condition();
		cond.and(Condition.makeEQ("organization_party_id", organizationPartyId));
		cond.and(Condition.makeIn("gl_account_id", listAccountId), UtilValidate.isNotEmpty(listAccountId));
		SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy"); 
		Date fdate = null, tdate = null;
		if (UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)) {
			fdate = (Date) dt.parse(fromDate);
			tdate = (Date) dt.parse(thruDate);
		}
		cond.and(Condition.make("voucher_created_date", ">=", getSqlFromDate(fdate)), UtilValidate.isNotEmpty(fdate));
		cond.and(Condition.make("voucher_created_date", "<=", getSqlThruDate(tdate)), UtilValidate.isNotEmpty(tdate));
		query.where(cond);
		query.groupBy("party_id");
		
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
					BigDecimal openingCreditAmount = BigDecimal.ZERO;
					BigDecimal openingDebitAmount = BigDecimal.ZERO;
					BigDecimal postingCreditAmount = resultSet.getBigDecimal("cr_amount");
					BigDecimal postingDebitAmount = resultSet.getBigDecimal("dr_amount");
					BigDecimal endingCreditAmount = BigDecimal.ZERO;
					BigDecimal endingDebitAmount = BigDecimal.ZERO;
					String partyId = resultSet.getString("party_id");
					
					if (UtilValidate.isNotEmpty(fdate) && UtilValidate.isNotEmpty(tdate) && UtilValidate.isNotEmpty(glAccountId) && UtilValidate.isNotEmpty(partyId)) {
						Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, new java.sql.Date(fdate.getTime()),
								new java.sql.Date(tdate.getTime()), organizationPartyId, delegator);
						openingCreditAmount = openingBal.get(BalanceWorker.CREDIT);
						openingDebitAmount = openingBal.get(BalanceWorker.DEBIT);
						if (openingDebitAmount.compareTo(BigDecimal.ZERO) < 0) {
							openingDebitAmount = openingDebitAmount.negate();
						}
					}
					   
					BigDecimal endingAmount = openingCreditAmount.add(postingCreditAmount)
							.subtract(openingDebitAmount).subtract(postingDebitAmount);
					
					if (endingAmount.compareTo(BigDecimal.ZERO) >= 0) {
						endingCreditAmount = endingAmount;
					} else {
						endingDebitAmount = endingAmount.negate();
					}
					
					totalOpeningDebit = totalOpeningDebit.add(openingDebitAmount);
					totalOpeningCredit = totalOpeningCredit.add(openingCreditAmount);
					totalPostingDebit = totalPostingDebit.add(postingDebitAmount);
					totalPostingCredit = totalPostingCredit.add(postingCreditAmount);
					totalEndingDebit = totalEndingDebit.add(endingDebitAmount);
					totalEndingCredit = totalEndingCredit.add(endingCreditAmount);
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
		
		request.setAttribute("totalOpeningDebit", totalOpeningDebit);
		request.setAttribute("totalOpeningCredit", totalOpeningCredit);
		request.setAttribute("totalPostingCredit", totalPostingCredit);
		request.setAttribute("totalPostingDebit", totalPostingDebit);
		request.setAttribute("totalEndingDebit", totalEndingDebit);
		request.setAttribute("totalEndingCredit", totalEndingCredit);
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