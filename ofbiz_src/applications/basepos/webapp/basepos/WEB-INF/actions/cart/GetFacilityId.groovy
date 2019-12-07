import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil;
import com.olbius.basepos.session.WebPosSession;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.ofbiz.product.store.ProductStoreWorker;

import javolution.util.FastList;
HttpSession session = request.getSession(true);
WebPosSession webposSession = (WebPosSession) session.getAttribute("webPosSession");

if(UtilValidate.isNotEmpty(webposSession)){
	String facilityId = webposSession.getFacilityId();
	if(UtilValidate.isNotEmpty(facilityId)){
		List<EntityCondition> listFacilityCond = FastList.newInstance();
		listFacilityCond.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		listFacilityCond.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
		EntityCondition facilityCond = EntityCondition.makeCondition(listFacilityCond, EntityOperator.AND);
		List<GenericValue> listFacility = delegator.findList("FacilityAndPostalAddressAndTelecomNumber",facilityCond, null, null, null, false );
		if(UtilValidate.isNotEmpty(listFacility)){
			GenericValue facility = EntityUtil.getFirst(listFacility);
			if(UtilValidate.isNotEmpty(facility)){
				context.facility = facility;
			}
		}
	}
	
	context.facilityId = facilityId;
	productStoreId = ProductStoreWorker.getProductStoreId(request);
	context.productStoreId = productStoreId;
	
	postalAddress = dispatcher.runSync("getFacilityContactMechs", [facilityId : facilityId, contactMechPurposeTypeId : "PRIMARY_LOCATION", userLogin: userLogin])
	if (postalAddress){
		listFacilityContactMechs = postalAddress.listFacilityContactMechs;
		if (listFacilityContactMechs){
			context.postalAddress = listFacilityContactMechs.get(0);
		}
	}
	
}