import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;

import com.olbius.util.PartyUtil;

import javolution.util.FastList;

Map<String, Object> inputFields = parameters;
partyId = parameters.partyId;
List<String> partyIds = FastList.newInstance();
List<GenericValue> partyRoles = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")), null, false);
List<String> partyRoleStr = EntityUtil.getFieldListFromEntityList(partyRoles, "roleTypeId", true);
String partyIdCeo = PartyUtil.getCEO(delegator);

//userLogin is CEO or HeadOfHR, get all employee leave list
if(userLogin.getString("partyId").equals(partyIdCeo) || userLogin.getString("partyId").equals(PartyUtil.getHrmAdmin(delegator))){
	results = dispatcher.runSync("performFind", UtilMisc.toMap("inputFields", inputFields,
														   "entityName", "EmplLeave",
															"noConditionFind", "Y"));
	context.listIt = results.listIt;														
}else if(partyRoleStr.contains("MANAGER")){
	if(partyId){
		managerIdOfParty = PartyUtil.getManagerOfEmpl(delegator, partyId);
		if(userLogin.getString("partyId").equalsIgnoreCase(managerIdOfParty)){
			partyIds.add(partyId);
			inputFields.remove("partyId");
		}else{
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("EmployeeUiLabels.xml", "ManagerNotManageEmployee", UtilMisc.toMap("partyName", PartyHelper.getPartyName(delegator, partyId, false)), locale));
		}
	}else{
		List<GenericValue> listEmployee = FastList.newInstance();
		//List<EntityCondition> conditions = FastList.newInstance();
		/*conditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, userLogin.getString("partyId")));
		conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "MANAGER"));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));
		conditions.add(EntityUtil.getFilterByDateExpr());*/
		//List<GenericValue> listDepartment = delegator.findList("PartyRelationship", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		//for(GenericValue department : listDepartment){
		listEmployee = PartyUtil.getListEmployeeOfManager(delegator, userLogin.getString("partyId"));
		//}
		partyIds = EntityUtil.getFieldListFromEntityList(listEmployee, "partyIdTo", true);
	}
	if(UtilValidate.isNotEmpty(partyIds)){
		inputFields.put("partyId_fld0_op", "in");
		inputFields.put("partyId_fld0_value", partyIds);
		results = dispatcher.runSync("performFind", UtilMisc.toMap("inputFields", inputFields,
																   "entityName", "EmplLeave",
																	"noConditionFind", "Y"));
		context.listIt = results.listIt;
	}
}

														
