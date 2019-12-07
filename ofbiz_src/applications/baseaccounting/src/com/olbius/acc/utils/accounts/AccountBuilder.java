package com.olbius.acc.utils.accounts;

import com.olbius.acc.utils.ErrorUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

import java.util.List;

public class AccountBuilder {
	
	public static final String module = AccountBuilder.class.getName();
	
	public static Account buildAccount(String glAccountId, Delegator delegator) {
		Account result = null;
		try {
			Account acc = new AccountComposite();
			GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);
			AccountEntity accEntity = new AccountEntity();
			accEntity.setGlAccountId(glAccount.getString("glAccountId"));
			accEntity.setAccountCode(glAccount.getString("accountCode"));
			accEntity.setAccountName(glAccount.getString("accountName"));
			
			List<GenericValue> listChild = delegator.findByAnd("GlAccount", UtilMisc.toMap("parentGlAccountId", glAccountId), null, false);
			if(!UtilValidate.isEmpty(listChild)) {
				accEntity.setLeaf(false);
				acc.setAcc(accEntity);
				for(GenericValue item : listChild) {
					Account child = buildAccount(item.getString("glAccountId"), delegator);
					acc.addAccount(child);
					acc.addListAccount(child.getListChild());
					acc.addDirectedAccount(child);
				}
			}else {
				accEntity.setLeaf(true);
				acc.setAcc(accEntity);
				result = acc;
			}
			result = acc;
		} catch (GenericEntityException e) {
			ErrorUtils.processException(e, module);
		}
		return result;
	}


    public static Account buildAccountByCode(String glAccountCode, Delegator delegator) {
        Account result = null;
        try {
            Account acc = new AccountComposite();
            GenericValue glAccount = delegator.findList("GlAccount", EntityCondition.makeCondition("accountCode", glAccountCode), null, null, null, false).get(0);
            String glAccountId = (String) glAccount.get("glAccountId");
            AccountEntity accEntity = new AccountEntity();
            accEntity.setGlAccountId(glAccount.getString("glAccountId"));
            accEntity.setAccountCode(glAccount.getString("accountCode"));
            accEntity.setAccountName(glAccount.getString("accountName"));

            List<GenericValue> listChild = delegator.findByAnd("GlAccount", UtilMisc.toMap("parentGlAccountId", glAccountId), null, false);
            if(!UtilValidate.isEmpty(listChild)) {
                accEntity.setLeaf(false);
                acc.setAcc(accEntity);
                for(GenericValue item : listChild) {
                    Account child = buildAccount(item.getString("glAccountId"), delegator);
                    acc.addAccount(child);
                    acc.addListAccount(child.getListChild());
                    acc.addDirectedAccount(child);
                }
            }else {
                accEntity.setLeaf(true);
                acc.setAcc(accEntity);
                result = acc;
            }
            result = acc;
        } catch (GenericEntityException e) {
            ErrorUtils.processException(e, module);
        }
        return result;
    }
}
