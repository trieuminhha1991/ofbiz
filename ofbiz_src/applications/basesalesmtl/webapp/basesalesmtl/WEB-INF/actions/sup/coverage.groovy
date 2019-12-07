import java.awt.image.renderable.ContextualRenderedImageFactory;
import java.util.List
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;

import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.party.PartyWorker;
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.salesmtl.util.SupUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;

import com.olbius.salesmtl.util.MTLUtil;
import com.olbius.salesmtl.DistributorServices;
import com.olbius.salesmtl.SupervisorServices;
import com.olbius.basesales.util.SalesPartyUtil;


String partyId = userLogin.getString("partyId");

List<String> sups = PartyUtil.getDepartmentOfEmployee(delegator, partyId, UtilDateTime.nowTimestamp());
List<String> org = FastList.newInstance();
if(UtilValidate.isNotEmpty(sups)){
	String sup = sups.get(0);
	Map<String, Object> input = FastMap.newInstance();
	input.put("userLogin", userLogin);
	input.put("partyId", sup);
	List<String> distributor = PartyWorker.getDistributorIdsBySupDept(delegator, sup);
	context.distributor = distributor;
	Map<String, Object> out = dispatcher.runSync("getOrganizationUnit", input);
	List<Map<String, Object>> listReturn = (List<Map<String, Object>>) out.get("listReturn");
	if(UtilValidate.isNotEmpty(listReturn)){
		for(Map<String, Object> o : listReturn){
			String cur = (String) o.get("partyId");
			if(!org.contains(cur)){
				org.add(cur);
			}
		}
	}
	
	if(UtilValidate.isEmpty(org)){
		addressesValue = PartyWorker.getAllPartyPostalAddressValue(delegator, sup, null);
		addresses = EntityUtil.getFieldListFromEntityList(addressesValue, "stateProvinceGeoName", true);
	}else{
		addressesValue = PartyWorker.getAllPartyPostalAddressValue(delegator, org, null);
		addresses = EntityUtil.getFieldListFromEntityList(addressesValue, "stateProvinceGeoName", true);
	}
	if(UtilValidate.isNotEmpty(addresses)){
		String city = addresses.get(0);
		context.city = city;
		context.addresses = addresses;
	}
	context.currentSup = sup;
	context.addressesValue = addressesValue;
}
context.routes = SupUtil.getAllRouteValue(delegator, partyId);
def isDistributor = "N";
if (SalesPartyUtil.isDistributor(delegator, partyId)) {
	isDistributor = "Y";
	def dummy = SupervisorServices.agentsOfDistributor(delegator, partyId);
	def agents = "[";
	def flag = false;
	for(value in dummy) {
	    if(flag) {
	    	agents += ",";
	    }
	    agents += "\'" + value + "\'";
	    flag = true;
	}
	agents += "]";
	context.agents = agents;
	dummy = DistributorServices.getSupervisor(delegator, partyId);
	if(UtilValidate.isNotEmpty(dummy)){
		org = UtilMisc.toList(dummy.get("supervisorId"));
	}
}
context.org = org;
context.isDistributor = isDistributor;
