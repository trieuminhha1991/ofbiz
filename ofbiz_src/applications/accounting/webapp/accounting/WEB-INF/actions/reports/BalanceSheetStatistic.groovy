/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;

import java.sql.Date;
import java.sql.Timestamp;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import javolution.util.FastList;

int iIndex = balance.toInteger();
if(iIndex >= listKey.size()){
	return;
}else{
	balance = listKey.get(iIndex);
}
uiLabelMap = UtilProperties.getResourceBundleMap("AccountingUiLabels", locale);

thruDate = UtilDateTime.toTimestamp(listCTP.get(iIndex).get("thruDate"));;
if (!glFiscalTypeId) {
    return;
}
balance = "" + balance;
// Setup the divisions for which the report is executed
List partyIds = PartyWorker.getAssociatedPartyIdsByRelationshipType(delegator, organizationPartyId, 'GROUP_ROLLUP');
partyIds.add(organizationPartyId);

// Get the group of account classes that will be used to position accounts in the proper section of the financial statement
GenericValue assetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "ASSET"), true);
List assetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(assetGlAccountClass);
context.assetAccountClassIds = assetAccountClassIds;
GenericValue contraAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CONTRA_ASSET"), true);
List contraAssetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(contraAssetGlAccountClass);
context.contraAssetAccountClassIds = contraAssetAccountClassIds;
GenericValue liabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "LIABILITY"), true);
List liabilityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(liabilityGlAccountClass);
context.liabilityAccountClassIds = liabilityAccountClassIds;
GenericValue equityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "EQUITY"), true);
List equityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(equityGlAccountClass);
context.equityAccountClassIds = equityAccountClassIds;
GenericValue currentAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CURRENT_ASSET"), true);
List currentAssetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(currentAssetGlAccountClass);
context.currentAssetAccountClassIds = currentAssetAccountClassIds;
GenericValue longtermAssetGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "LONGTERM_ASSET"), true);
List longtermAssetAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(longtermAssetGlAccountClass);
context.longtermAssetAccountClassIds = longtermAssetAccountClassIds;
GenericValue currentLiabilityGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "CURRENT_LIABILITY"), true);
List currentLiabilityAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(currentLiabilityGlAccountClass);
context.currentLiabilityAccountClassIds = currentLiabilityAccountClassIds;
GenericValue accumDepreciationGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "ACCUM_DEPRECIATION"), true);
List accumDepreciationAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(accumDepreciationGlAccountClass);
context.accumDepreciationAccountClassIds = accumDepreciationAccountClassIds;
GenericValue accumAmortizationGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "ACCUM_AMORTIZATION"), true);
List accumAmortizationAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(accumAmortizationGlAccountClass);
context.accumAmortizationAccountClassIds = accumAmortizationAccountClassIds;

Timestamp fromDate = UtilDateTime.toTimestamp(listCTP.get(iIndex).get("fromDate"));
if (!fromDate) {
    return;
}
println("iIndex" + iIndex);
GenericValue lastClosedTimePeriod = listCTP.get(iIndex);
// Get the opening balances of all the accounts
Map assetOpeningBalances = [:];
Map contraAssetOpeningBalances = [:];
Map currentAssetOpeningBalances = [:];
Map longtermAssetOpeningBalances = [:];
Map liabilityOpeningBalances = [:];
Map currentLiabilityOpeningBalances = [:];
Map equityOpeningBalances = [:];
if (lastClosedTimePeriod) {
    List timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, assetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    List lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        assetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, contraAssetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        contraAssetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, liabilityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        liabilityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, equityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        equityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentAssetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        currentAssetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, longtermAssetAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        longtermAssetOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
    timePeriodAndExprs = FastList.newInstance();
    timePeriodAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds));
    timePeriodAndExprs.add(EntityCondition.makeCondition("endingBalance", EntityOperator.NOT_EQUAL, BigDecimal.ZERO));
    timePeriodAndExprs.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.EQUALS, lastClosedTimePeriod.customTimePeriodId));
    lastTimePeriodHistories = delegator.findList("GlAccountAndHistory", EntityCondition.makeCondition(timePeriodAndExprs, EntityOperator.AND), null, null, null, false);
    lastTimePeriodHistories.each { lastTimePeriodHistory ->
        Map accountMap = UtilMisc.toMap("glAccountId", lastTimePeriodHistory.glAccountId, "accountCode", lastTimePeriodHistory.accountCode, "accountName", lastTimePeriodHistory.accountName, "balance", lastTimePeriodHistory.getBigDecimal("endingBalance"), "D", lastTimePeriodHistory.getBigDecimal("postedDebits"), "C", lastTimePeriodHistory.getBigDecimal("postedCredits"));
        currentLiabilityOpeningBalances.put(lastTimePeriodHistory.glAccountId, accountMap);
    }
}

List balanceTotalList = [];

List mainAndExprs = FastList.newInstance();
mainAndExprs.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.IN, partyIds));
mainAndExprs.add(EntityCondition.makeCondition("isPosted", EntityOperator.EQUALS, "Y"));
mainAndExprs.add(EntityCondition.makeCondition("glFiscalTypeId", EntityOperator.EQUALS, glFiscalTypeId));
mainAndExprs.add(EntityCondition.makeCondition("acctgTransTypeId", EntityOperator.NOT_EQUAL, "PERIOD_CLOSING"));
mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
mainAndExprs.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, thruDate));
List listTree;
if(globalListTree == null){
	// 1. get glAccount tree
	listTree = FastList.newInstance();
	int tmpCount = 1;
	// 1.1 Cache LONGTERM_ASSET
	listTree.add(getChildrenTreeForClass("ASSET", tmpCount++));
	listTree.add(getChildrenTreeForClass("LIABILITY", tmpCount++));
	listTree.add(getChildrenTreeForClass("EQUITY", tmpCount++));
	
}else{
	listTree = globalListTree;
}

// ASSETS
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List assetAndExprs = FastList.newInstance(mainAndExprs);
assetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, assetAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(assetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(assetOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // assets are accounts of class DEBIT: the balance is given by debits minus credits
    BigDecimal balance = debitAmount.subtract(creditAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.assetAccountBalanceList = accountBalanceList;
context.assetAccountBalanceList.add(UtilMisc.toMap("accountName", uiLabelMap.AccountingTotalAssets, "balance", balanceTotal));
context.assetBalanceTotal = balanceTotal;

// CURRENT ASSETS
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List currentAssetAndExprs = FastList.newInstance(mainAndExprs);
currentAssetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentAssetAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(currentAssetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(currentAssetOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // assets are accounts of class DEBIT: the balance is given by debits minus credits
    BigDecimal balance = debitAmount.subtract(creditAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.currentAssetBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingCurrentAssets", "balance", balanceTotal));

// LONGTERM ASSETS
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List longtermAssetAndExprs = FastList.newInstance(mainAndExprs);
longtermAssetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, longtermAssetAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(longtermAssetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(longtermAssetOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // assets are accounts of class DEBIT: the balance is given by debits minus credits
    BigDecimal balance = debitAmount.subtract(creditAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.longtermAssetBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingLongTermAssets", "balance", balanceTotal));

// CONTRA ASSETS
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List contraAssetAndExprs = FastList.newInstance(mainAndExprs);
contraAssetAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, contraAssetAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(contraAssetAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);

transactionTotalsMap = [:];
transactionTotalsMap.putAll(contraAssetOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // contra assets are accounts of class CREDIT: the balance is given by credits minus debits
    BigDecimal balance = debitAmount.subtract(creditAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
//context.contraAssetAccountBalanceList = accountBalanceList;
context.assetAccountBalanceList.addAll(accountBalanceList);
context.assetAccountBalanceList.add(UtilMisc.toMap("accountName", uiLabelMap.AccountingTotalAccumulatedDepreciation, "balance", balanceTotal));
context.contraAssetBalanceTotal = balanceTotal;
//balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingLongTermAssetsAtCost", "balance", (context.longtermAssetBalanceTotal - context.contraAssetBalanceTotal)));
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingTotalAccumulatedDepreciation", "balance", balanceTotal));
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingTotalAssets", "balance", (context.currentAssetBalanceTotal + context.longtermAssetBalanceTotal + balanceTotal)));


// LIABILITY
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List liabilityAndExprs = FastList.newInstance(mainAndExprs);
liabilityAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, liabilityAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(liabilityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(liabilityOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // liabilities are accounts of class CREDIT: the balance is given by credits minus debits
    BigDecimal balance = creditAmount.subtract(debitAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);

accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.liabilityAccountBalanceList = accountBalanceList;
context.liabilityAccountBalanceList.add(UtilMisc.toMap("accountName", uiLabelMap.AccountingTotalLiabilities, "balance", balanceTotal));
context.liabilityBalanceTotal = balanceTotal;

// CURRENT LIABILITY
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List currentLiabilityAndExprs = FastList.newInstance(mainAndExprs);
currentLiabilityAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, currentLiabilityAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(currentLiabilityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(currentLiabilityOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // liabilities are accounts of class CREDIT: the balance is given by credits minus debits
    BigDecimal balance = creditAmount.subtract(debitAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.currentLiabilityBalanceTotal = balanceTotal;
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingCurrentLiabilities", "balance", balanceTotal));

// EQUITY
// account balances
accountBalanceList = [];
transactionTotals = [];
balanceTotal = BigDecimal.ZERO;
List equityAndExprs = FastList.newInstance(mainAndExprs);
equityAndExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, equityAccountClassIds));
transactionTotals = delegator.findList("AcctgTransEntrySums", EntityCondition.makeCondition(equityAndExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode", "debitCreditFlag", "amount"), UtilMisc.toList("glAccountId"), null, false);
transactionTotalsMap = [:];
transactionTotalsMap.putAll(equityOpeningBalances);
transactionTotals.each { transactionTotal ->
    Map accountMap = (Map)transactionTotalsMap.get(transactionTotal.glAccountId);
    if (!accountMap) {
        accountMap = UtilMisc.makeMapWritable(transactionTotal);
        accountMap.remove("debitCreditFlag");
        accountMap.remove("amount");
        accountMap.put("D", BigDecimal.ZERO);
        accountMap.put("C", BigDecimal.ZERO);
        accountMap.put("balance", BigDecimal.ZERO);
    }
    UtilMisc.addToBigDecimalInMap(accountMap, transactionTotal.debitCreditFlag, transactionTotal.amount);
    BigDecimal debitAmount = (BigDecimal)accountMap.get("D");
    BigDecimal creditAmount = (BigDecimal)accountMap.get("C");
    // equities are accounts of class CREDIT: the balance is given by credits minus debits
    BigDecimal balance = creditAmount.subtract(debitAmount);
    accountMap.put("balance", balance);
    transactionTotalsMap.put(transactionTotal.glAccountId, accountMap);
}
// Add the "retained earnings" account
Map netIncomeResult = dispatcher.runSync("prepareIncomeStatement", UtilMisc.toMap("organizationPartyId", organizationPartyId, "glFiscalTypeId", glFiscalTypeId, "fromDate", fromDate, "thruDate", thruDate,"userLogin", userLogin));
BigDecimal netIncome = (BigDecimal)netIncomeResult.totalNetIncome;
GenericValue retainedEarningsAccount = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId", "RETAINED_EARNINGS", "organizationPartyId", organizationPartyId), true);
if (retainedEarningsAccount) {
    GenericValue retainedEarningsGlAccount = retainedEarningsAccount.getRelatedOne("GlAccount", false);
    transactionTotalsMap.put(retainedEarningsGlAccount.glAccountId, UtilMisc.toMap("glAccountId", retainedEarningsGlAccount.glAccountId,"accountName", retainedEarningsGlAccount.accountName, "accountCode", retainedEarningsGlAccount.accountCode, "balance", netIncome));
}
updateGlAccountClassBalance(listTree, transactionTotalsMap);
accountBalanceList = UtilMisc.sortMaps(transactionTotalsMap.values().asList(), UtilMisc.toList("accountCode"));
accountBalanceList.each { accountBalance ->
    balanceTotal = balanceTotal + accountBalance.balance;
}
context.equityAccountBalanceList = accountBalanceList;
context.equityAccountBalanceList.add(UtilMisc.toMap("accountName", uiLabelMap.AccountingTotalEquities, "balance", balanceTotal));
context.equityBalanceTotal = balanceTotal;

context.liabilityEquityBalanceTotal = context.liabilityBalanceTotal + context.equityBalanceTotal
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingEquities", "balance", context.equityBalanceTotal));
balanceTotalList.add(UtilMisc.toMap("totalName", "AccountingTotalLiabilitiesAndEquities", "balance", context.liabilityEquityBalanceTotal));

context.balanceTotalList = balanceTotalList;


// Aggreagte data for parent
listTree.each{ listGlClass->
	List tmpList = listGlClass.get("children");
	BigDecimal tmpBD = listGlClass.get(balance);
	listGlClass.put(balance,sumBalance(tmpList));
}
println("listTree"+ listTree);
// convert to json format(display on jqx)
JSONArray jsonArray = new JSONArray();
listTree.each{ listGlClass->
	JSONObject tmpJsonObject = JSONObject.fromObject(listGlClass);
	jsonArray.add(tmpJsonObject);
}
context.listTree = listTree;
context.testJson = jsonArray.toString();

// Function to get all glAccount child of class type
Map getChildrenTreeForClass(String strAccountClass, int iCount){
	GenericValue tmpGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", strAccountClass), true);
	List tmpListGlAccountClass = UtilAccounting.getDescendantGlAccountClassIds(tmpGlAccountClass);
	List tmExprs = FastList.newInstance();
	tmExprs.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, tmpListGlAccountClass));
	tmExprs.add(EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, null));
	List listTMP = delegator.findList("GlAccount", EntityCondition.makeCondition(tmExprs, EntityOperator.AND), UtilMisc.toSet("glAccountId", "accountName", "accountCode"), UtilMisc.toList("glAccountId"), null, false);
	List listGlAccountChild = FastList.newInstance();
	Map tmpMap;
	for(int i = 0; i < listTMP.size();i++){
		tmpMap = new HashMap();
		tmpMap.put("parentGlAccountId", "0");
		tmpMap.put("glAccountId", listTMP.get(i).get("glAccountId"));
		tmpMap.put("accountName", listTMP.get(i).get("accountName"));
		tmpMap.put("accountCode", listTMP.get(i).get("accountCode"));
		tmpMap.put(balance, BigDecimal.ZERO);
		List tmpList = getChildrenTree(listTMP.get(i).get("glAccountId"));
		if(tmpList!=null && tmpList.size() > 0){
			tmpMap.put("children", tmpList);
		}
		listGlAccountChild.add(tmpMap);
	}
	tmpMap = new HashMap();
	tmpMap.put("glAccountId", "GL" + iCount);
	tmpMap.put("accountName", tmpGlAccountClass.get("description"));
	tmpMap.put("accountCode", tmpGlAccountClass.get("glAccountClassId"));
	tmpMap.put(balance, BigDecimal.ZERO);
	if(listGlAccountChild != null && listGlAccountChild.size() > 0){
		tmpMap.put("children", listGlAccountChild);
	}
	
	return tmpMap;
}
// get all glAccount child of a glAccount
List getChildrenTree(String parentGlAccountId){
	List listTMP = delegator.findList("GlAccount",EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, parentGlAccountId), UtilMisc.toSet("glAccountId", "accountName", "accountCode"), UtilMisc.toList("glAccountId"), null, false);
	if(listTMP.empty){
		return null;
	}else{
		List listGlAccountChild = FastList.newInstance();
		Map tmpMap;
		for(int i = 0; i < listTMP.size();i++){
			tmpMap = new HashMap();
			tmpMap.put("parentGlAccountId", parentGlAccountId);
			tmpMap.put("glAccountId", listTMP.get(i).get("glAccountId"));
			tmpMap.put("accountName", listTMP.get(i).get("accountName"));
			tmpMap.put("accountCode", listTMP.get(i).get("accountCode"));
			tmpMap.put(balance, BigDecimal.ZERO);
			List tmpList = getChildrenTree(listTMP.get(i).get("glAccountId"));
			if(tmpList != null && tmpList.size() > 0){
				tmpMap.put("children", tmpList);
			}
			listGlAccountChild.add(tmpMap);
		}
		return listGlAccountChild;
	}
}
// Update child value with real balance number
boolean updateListTree(List listData, String glAccountId, BigDecimal bValue){
	listData.each{ tmpMap ->
		if(tmpMap.get("glAccountId").equals(glAccountId)){
			tmpMap.put(balance, bValue);
			return true;
		}
		if(tmpMap.get("children") != null){
			return updateListTree(tmpMap.get("children"), glAccountId, bValue);
		}
	}
	return false
}
// Update glAccount value 
void updateGlAccountClassBalance(List listTree, Map transactionTotalsMap){
	for ( String key : transactionTotalsMap.keySet() ) {
		listTree.each{ tmpList ->
			if(updateListTree(tmpList.get("children"), key, transactionTotalsMap.get(key).get("balance"))){
				return false;
			}
		}
	}
}
// update parent balance value
BigDecimal sumBalance(List listData){
	BigDecimal tmpBD = BigDecimal.ZERO;
	listData.each{ tmpList ->
		if(!tmpList.containsKey(balance)){
			tmpList.put(balance, BigDecimal.ZERO);
		}
		if(tmpList.get("children") != null){
			BigDecimal bd = sumBalance(tmpList.get("children"));
			tmpList.put(balance, tmpList.get(balance).add(bd));
		}
		tmpBD = tmpBD.add(tmpList.get(balance));
	}
	return tmpBD;
}