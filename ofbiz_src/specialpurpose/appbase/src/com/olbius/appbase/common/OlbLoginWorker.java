package com.olbius.appbase.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

public class OlbLoginWorker {
	public final static String module = OlbLoginWorker.class.getName();
	
	public static String checkWizardSystem(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		
		try {
			// check exists company
			GenericValue company = delegator.findOne("Party", UtilMisc.toMap("partyId", "company"), true);
			if (company == null) {
				return "error";
			}
			
			// check user human resource manager
			List<EntityCondition> conds = new ArrayList<EntityCondition>();
			conds.add(EntityCondition.makeCondition("roleTypeIdFrom", "HR_MANAGER"));
			conds.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds.add(EntityCondition.makeCondition("partyRelationshipTypeId", "MANAGER"));
			conds.add(EntityUtil.getFilterByDateExpr());
			GenericValue hrManagerRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
			if (hrManagerRel == null) {
				return "error";
			}
			
			// check user olbius admin
			List<EntityCondition> conds2 = new ArrayList<EntityCondition>();
			conds2.add(EntityCondition.makeCondition("roleTypeIdFrom", "SYS_ADMINISTRATOR"));
			conds2.add(EntityCondition.makeCondition("roleTypeIdTo", "INTERNAL_ORGANIZATIO"));
			conds2.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
			conds2.add(EntityUtil.getFilterByDateExpr());
			GenericValue olbiusAdminRel = EntityUtil.getFirst(delegator.findList("PartyRelationship", EntityCondition.makeCondition(conds2), UtilMisc.toSet("partyIdFrom", "partyIdTo"), null, null, true));
			if (olbiusAdminRel == null) {
				return "error";
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, "Error checking Wizard system setting", module);
            String errMsg = "Error checking Wizard system setting";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
		}
		
        return "success";
    }
}
