package com.olbius.basehr.timeMgr.helper;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;

public class TimeManagerHelper {

	public static void getPartyHierarchyWorkingShift(Delegator delegator, Organization parentOrg, List<Map<String, Object>> listReturn) 
			throws GenericEntityException {		
		List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
		if(UtilValidate.isNotEmpty(orgDirectChild)){
			for(GenericValue child: orgDirectChild){
				Map<String, Object> tempMap = FastMap.newInstance();
				String childPartyId = child.getString("partyId");
				tempMap.put("partyId", childPartyId);
				tempMap.put("partyCode", PartyUtil.getPartyCode(delegator, childPartyId));
				tempMap.put("partyIdFrom", parentOrg.getOrg().getString("partyId"));
				tempMap.put("partyName", PartyHelper.getPartyName(delegator, childPartyId, false));
				List<GenericValue> workingShiftParty = delegator.findByAnd("WorkingShiftConfigAndParty", UtilMisc.toMap("partyId", childPartyId), null, false);
				if(UtilValidate.isNotEmpty(workingShiftParty)){
					tempMap.put("workingShiftId", workingShiftParty.get(0).getString("workingShiftId"));
					tempMap.put("workingShiftName", workingShiftParty.get(0).getString("workingShiftName"));
				}
				tempMap.put("expanded", true);
				listReturn.add(tempMap);
				getPartyHierarchyWorkingShift(delegator, PartyUtil.buildOrg(delegator, childPartyId, false, false), listReturn);
			}
		}
	}

	public static String getWorkingShiftOfParty(Delegator delegator, String partyId, String userLoginId) throws GenericEntityException {
		//String rootParty = PartyUtil.getRootOrganization(delegator, userLoginId);
		String tempPartyId = partyId;
		do{
			GenericValue workingShiftPartyConfig = delegator.findOne("WorkingShiftPartyConfig", UtilMisc.toMap("partyId", tempPartyId), false);
			if(workingShiftPartyConfig != null){
				String workingShiftId = workingShiftPartyConfig.getString("workingShiftId");
				return workingShiftId;
			}
			//TODO need add condition about dateTime
			GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, tempPartyId);
			if(parentOrg == null){
				break;
			}
			if(tempPartyId.equals(parentOrg.getString("partyIdFrom"))){
				break;
			}
			tempPartyId = parentOrg.getString("partyIdFrom");
		}while(true);
		
		return null;
	}
}
