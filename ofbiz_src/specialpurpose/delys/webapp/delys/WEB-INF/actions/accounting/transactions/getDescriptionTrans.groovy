import org.ofbiz.base.util.UtilProperties;

listDesAccTrans = [:];
listDesAcctgTransEntries = [];
if(acctgTrans){
	if(acctgTrans.acctgTransTypeId !=null ){
		accType = delegator.findOne("AcctgTransType",["acctgTransTypeId" : acctgTrans.acctgTransTypeId],false);
		listDesAccTrans.accType = accType.get("description",locale);
	}else listDesAccTrans.accType = "";
	
	if(acctgTrans.glFiscalTypeId != null){
		glFiscal = delegator.findOne("GlFiscalType",[glFiscalTypeId :acctgTrans.glFiscalTypeId],false);
		listDesAccTrans.glFiscal = glFiscal.get("description",locale);
	}else listDesAccTrans.glFiscal = "";

	if(acctgTrans.roleTypeId != null){
		roleType = delegator.findOne("RoleType",[roleTypeId : acctgTrans.roleTypeId],false);
		listDesAccTrans.roleType = roleType.get("description",locale);
	}else listDesAccTrans.roleType = "";
		
	if(acctgTrans.glJournalId != null){
	 	glJournal = delegator.findOne("GlJournal",[glJournalId : acctgTrans.glJournalId],false);
		listDesAccTrans.glJournal = glJournal.get("glJournalName");
	 }else listDesAccTrans.glJournal = "";
	 
	 if(acctgTrans.fixedAssetId != null){
	 	fixedAsset = delegator.findOne("FixedAsset",[fixedAssetId : acctgTrans.fixedAssetId],false);
	 	fxType = delegator.findOne("FixedAssetType",[fixedAssetTypeId : fixedAsset.fixedAssetTypeId],false);
		listDesAccTrans.fxType = fxType.get("description",locale);
	 }else listDesAccTrans.fxType = ""
	 
	 if(acctgTrans.workEffortId != null){
	 	workEffort = delegator.findOne("WorkEffort",[workEffortId : acctgTrans.workEffortId],false);
	 	workEffortType = delegator.findOne("WorkEffortType",[workEffortTypeId : workEffort.workEffortTypeId],false);
		listDesAccTrans.workEffortType = workEffortType.get("description",locale);
	 }else listDesAccTrans.workEffortType  = "";
}

if(acctgTransEntries){
	acctgTransEntries.each{entry -> 
		entryMap = [:];
		entryMap.glAccountClassId = entry.glAccountClassId;
		clss = delegator.findOne("GlAccountClass",["glAccountClassId" : entry.glAccountClassId],false);
		entryMap.glAccountClassDes = clss.get("description",locale);
		entryMap.reconcileStatusId = entry.reconcileStatusId;
		status = delegator.findOne("StatusItem",["statusId" : entry.reconcileStatusId],false);
		entryMap.descriptionStatus = status.get("description",locale);
		entryMap.accountName = entry.accountName + "-" + entry.accountCode;
		entryMap.fullName = entry.firstName != null ? entry.firstName : ""  + " " + entry.middleName != null ? entry.middleName : "" + " "	+  entry.lastName != null ?  entry.lastName : "";
		
		entryMap.glAccountTypeId = entry.glAccountTypeId;
		accountType = delegator.findOne("GlAccountType",["glAccountTypeId" : entry.glAccountTypeId],false);
		entryMap.accountType = accountType.get("description",locale);
		credit = "";
		debit = "";
		if(entry.debitCreditFlag.equals("C")){
			entryMap.debitCreditFlag = UtilProperties.getMessage("DelysAccGLUiLabels","CREDIT",locale);
		}else if(entry.debitCreditFlag.equals("D")){
			entryMap.debitCreditFlag = UtilProperties.getMessage("DelysAccGLUiLabels","DEBIT",locale);
		}
		entryMap.origAmount = entry.origAmount;
		entryMap.origCurrencyUomId = entry.origCurrencyUomId;
		listDesAcctgTransEntries.add(entryMap);
	}
}

context.listDesAccTrans = listDesAccTrans;
context.listDesAcctgTransEntries = listDesAcctgTransEntries;
