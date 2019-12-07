package com.olbius.acc.asset;

import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FAIncrementServices {

	public static Map<String, Object> updatePostedFixedAssetAcctgTransIncrement(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String fixedAssetIncreaseId = (String)context.get("fixedAssetIncreaseId");
		String isPosted = (String)context.get("isPosted");
		String message = null;
		
		if("Y".equals(isPosted)){
			try {
				GenericValue fixedAssetIncrement = delegator.findOne("FixedAssetIncrease", UtilMisc.toMap("fixedAssetIncreaseId",fixedAssetIncreaseId), false);
				fixedAssetIncrement.set("isPosted", Boolean.TRUE);
				message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
				
				Map<String, Object> createAccTrans = FastMap.newInstance();
				createAccTrans.put("description", fixedAssetIncrement.getString("description"));
				createAccTrans.put("transactionDate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
				createAccTrans.put("acctgTransTypeId", "FIXED_ASSET_INCREMENT");
				createAccTrans.put("userLogin", context.get("userLogin"));
				createAccTrans.put("glFiscalTypeId", "ACTUAL");
				
				List<GenericValue> fixedAssetIncreaseItem = delegator.findList("FixedAssetIncreaseItem", EntityCondition.makeCondition("fixedAssetIncreaseId", fixedAssetIncreaseId), null, null, null, false);
				List<GenericValue> acctgTransEntries = FastList.newInstance();
				String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, ((GenericValue)context.get("userLogin")).getString("userLoginId"));
				for(GenericValue item : fixedAssetIncreaseItem){
					GenericValue acctgTransEntryC = delegator.makeValue("AcctgTransEntry");
					String fixedAssetId = item.getString("fixedAssetId");
					if(UtilValidate.isNotEmpty(fixedAssetId)){
						GenericValue fixedAsset= delegator.findOne("FixedAsset", UtilMisc.toMap("fixedAssetId",fixedAssetId ), false);
						
						BigDecimal quantity = new BigDecimal(fixedAsset.getLong("quantity"));
						BigDecimal amount = quantity.multiply(fixedAsset.getBigDecimal("purchaseCost"));
						
						acctgTransEntryC.put("amount",amount);
						acctgTransEntryC.put("organizationPartyId", organizationPartyId);
						acctgTransEntryC.put("currencyUomId", fixedAsset.getString("uomId"));
						
						GenericValue acctgTransEntryD = (GenericValue) acctgTransEntryC.clone();
						acctgTransEntryC.put("debitCreditFlag", "C");
						acctgTransEntryC.put("glAccountId", item.getString("creditGlAccountId"));
						acctgTransEntries.add(acctgTransEntryC);
						
						acctgTransEntryD.put("debitCreditFlag", "D");
						acctgTransEntryD.put("glAccountId", item.getString("debitGlAccountId"));
						acctgTransEntries.add(acctgTransEntryD);
					}
					
					createAccTrans.put("acctgTransEntries", acctgTransEntries);
					LocalDispatcher localDispatcher = dctx.getDispatcher();
					 Map<String, Object> createAccTransResult= localDispatcher.runSync("createAcctgTransAndEntries", createAccTrans);
					
					if(ServiceUtil.isSuccess(createAccTransResult)){
						 String acctgTransId = (String)createAccTransResult.get("acctgTransId");
						 fixedAssetIncrement.put("acctgTransId", acctgTransId);
						 fixedAssetIncrement.store();
						 message = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedSuccess", locale);
					}else{
						return ServiceUtil.returnError("error");
					}
					
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}catch(GenericServiceException e){
				e.printStackTrace();
				return ServiceUtil.returnError(e.getLocalizedMessage());
			}
			
		}
		
		return ServiceUtil.returnSuccess(message);
	}
}
