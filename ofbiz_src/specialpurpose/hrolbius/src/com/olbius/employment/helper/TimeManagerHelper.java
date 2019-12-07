package com.olbius.employment.helper;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class TimeManagerHelper {

	public static void getPartyHierarchyWorkingShift(Delegator delegator, Organization parentOrg, List<Map<String, Object>> listReturn) 
			throws GenericEntityException {		
		List<GenericValue> orgDirectChild = parentOrg.getDirectChildList(delegator);
		if(UtilValidate.isNotEmpty(orgDirectChild)){
			for(GenericValue child: orgDirectChild){
				Map<String, Object> tempMap = FastMap.newInstance();
				String childPartyId = child.getString("partyId");
				tempMap.put("partyId", childPartyId);
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

	public static String getWorkingShiftOfParty(Delegator delegator, String partyId) throws GenericEntityException {
		String rootParty = MultiOrganizationUtil.getCurrentOrganization(delegator);
		GenericValue workingShiftPartyConfig = delegator.findOne("WorkingShiftPartyConfig", UtilMisc.toMap("partyId", partyId), false);
		if(workingShiftPartyConfig != null){
			String workingShiftId = workingShiftPartyConfig.getString("workingShiftId");
			return workingShiftId;
		}else if(!rootParty.equals(partyId)){
			//TODO need add condition about dateTime
			GenericValue parentOrg = PartyUtil.getParentOrgOfDepartmentCurr(delegator, partyId);
			if(parentOrg != null){
				return getWorkingShiftOfParty(delegator, parentOrg.getString("partyIdFrom"));
			}
		}
		return null;
	}
}
